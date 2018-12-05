/*
 * Copyright (C) 2017 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.util.ftpserver;

import android.text.TextUtils;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * FTP用户管理器
 * Created by Alex on 2017/12/19.
 */
@SuppressWarnings({"WeakerAccess", "RedundantThrows"})
public class FTPUserManager implements UserManager {

    private final ArrayList<FTPUser> mUsers = new ArrayList<>();

    public FTPUserManager(boolean anonymousEnable, String anonymousHomeDirectory,
                          FTPUser... users) {
        if (anonymousEnable) {
            mUsers.add(FTPUser.getAnonymous(anonymousHomeDirectory));
        }
        Collections.addAll(mUsers, users);
    }

    public FTPUserManager(boolean anonymousEnable, String anonymousHomeDirectory,
                          Collection<? extends FTPUser> users) {
        this(anonymousEnable, anonymousHomeDirectory);
        if (users != null)
            mUsers.addAll(users);
    }

    @Override
    public User getUserByName(String username) throws FtpException {
        for (FTPUser user : mUsers) {
            if (username.equals(user.getName()))
                return user.create();
        }
        return null;
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        final String[] users = new String[mUsers.size()];
        int index = 0;
        for (FTPUser user : mUsers) {
            users[index] = user.getName();
            index++;
        }
        return users;
    }

    @Override
    public void delete(String username) throws FtpException {
        for (FTPUser user : mUsers) {
            if (username.equals(user.getName())) {
                mUsers.remove(user);
                return;
            }
        }
    }

    @Override
    public void save(User user) throws FtpException {
        mUsers.add(FTPUser.from(user));
    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        for (FTPUser user : mUsers) {
            if (username.equals(user.getName()))
                return true;
        }
        return false;
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof AnonymousAuthentication) {
            for (FTPUser user : mUsers) {
                if (user.isAnonymous())
                    return user.create();
            }
            throw new AuthenticationFailedException();
        }
        if (mUsers.isEmpty())
            throw new AuthenticationFailedException();
        if (authentication instanceof UsernamePasswordAuthentication) {
            UsernamePasswordAuthentication auth = (UsernamePasswordAuthentication) authentication;
            final String username = auth.getUsername();
            final String password = auth.getPassword();
            for (FTPUser user : mUsers) {
                if (username.equals(user.getName()) &&
                        TextUtils.equals(password, user.getPassword()))
                    return user.create();
            }
        }
        throw new AuthenticationFailedException();
    }

    @Override
    public String getAdminName() throws FtpException {
        for (FTPUser user : mUsers) {
            if (user.isAdmin())
                return user.getName();
        }
        return null;
    }

    @Override
    public boolean isAdmin(String username) throws FtpException {
        return username.equals(getAdminName());
    }
}
