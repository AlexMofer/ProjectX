/*
 * Copyright (C) 2019 AlexMofer
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
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * FTP 用户管理器
 * Created by Alex on 2019/10/8.
 */
public class FtpUserManager implements UserManager {

    private final String mAdminName;
    private final HashMap<String, FtpUser> mUsers = new HashMap<>();
    private String[] mUserNames = null;
    private OnUserChangedListener mListener;

    @SuppressWarnings("WeakerAccess")
    public FtpUserManager(String adminName, FtpUser... users) {
        mAdminName = adminName;
        for (FtpUser user : users) {
            final String name = user.getName();
            if (mUsers.containsKey(name))
                continue;
            mUsers.put(name, user);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public FtpUserManager(String adminName, Collection<? extends FtpUser> users) {
        mAdminName = adminName;
        if (users != null)
            for (FtpUser user : users) {
                final String name = user.getName();
                if (mUsers.containsKey(name))
                    continue;
                mUsers.put(name, user);
            }
    }

    /**
     * 添加用户
     *
     * @param user 用户
     */
    @SuppressWarnings("WeakerAccess")
    public void addUser(FtpUser user) {
        if (user == null)
            return;
        if (TextUtils.isEmpty(user.getName()))
            return;
        final String name = user.getName();
        if (mUsers.containsKey(name))
            return;
        mUsers.put(name, user);
    }

    @Override
    public User getUserByName(String username) {
        return mUsers.get(username);
    }

    @Override
    public String[] getAllUserNames() {
        if (mUserNames != null)
            return mUserNames;
        final ArrayList<String> names = new ArrayList<>();
        if (!mUsers.isEmpty()) {
            final Collection<FtpUser> users = mUsers.values();
            for (User user : users) {
                names.add(user.getName());
            }
            Collections.sort(names);
        }
        mUserNames = names.toArray(new String[0]);
        return mUserNames;
    }

    @Override
    public void delete(String username) {
        if (mUsers.isEmpty())
            return;
        final User deleted = mUsers.remove(username);
        if (deleted == null)
            return;
        mUserNames = null;
        if (mListener != null)
            mListener.onUserDeleted(deleted);
    }

    @Override
    public void save(User user) {
        final String name = user.getName();
        if (name == null)
            throw new UnsupportedOperationException("User name can not be empty!");
        final FtpUser u = user instanceof FtpUser ? (FtpUser) user : new FtpUser(user);
        if (!u.isEditable())
            throw new UnsupportedOperationException("User info can not edit!");
        mUsers.put(name, u);
        mUserNames = null;
        if (mListener != null)
            mListener.onUserSaved(u);
    }

    @Override
    public boolean doesExist(String username) {
        return mUsers.containsKey(username);
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if (authentication instanceof UsernamePasswordAuthentication) {
            if (mUsers.isEmpty())
                throw new AuthenticationFailedException("Authentication failed");
            final UsernamePasswordAuthentication auth =
                    (UsernamePasswordAuthentication) authentication;
            final String username = auth.getUsername();
            final String password = auth.getPassword();
            final Collection<FtpUser> users = mUsers.values();
            for (FtpUser user : users) {
                if (username.equals(user.getName()) &&
                        TextUtils.equals(password, user.getPassword()))
                    return user;
            }
            throw new AuthenticationFailedException("Authentication failed");
        } else if (authentication instanceof AnonymousAuthentication) {
            if (isAnonymousLoginEnabled()) {
                return getUserByName(FtpUser.NAME_ANONYMOUS);
            } else {
                throw new AuthenticationFailedException("Authentication failed");
            }
        }
        throw new AuthenticationFailedException("Authentication failed");
    }

    @Override
    public String getAdminName() {
        return mAdminName;
    }

    @Override
    public boolean isAdmin(String username) {
        return TextUtils.equals(mAdminName, username);
    }

    /**
     * 判断是否允许匿名登录
     *
     * @return 如果允许匿名登录则返回true
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isAnonymousLoginEnabled() {
        return doesExist(FtpUser.NAME_ANONYMOUS);
    }

    /**
     * 设置用户变更监听
     *
     * @param listener 用户变更监听
     */
    @SuppressWarnings("WeakerAccess")
    public void setOnUserChangedListener(OnUserChangedListener listener) {
        mListener = listener;
    }

    public interface OnUserChangedListener {

        /**
         * 用户删除
         *
         * @param user 删除的用户
         */
        void onUserDeleted(User user);

        /**
         * 用户保存（创建或修改）
         *
         * @param user 保存的用户
         */
        void onUserSaved(User user);
    }
}
