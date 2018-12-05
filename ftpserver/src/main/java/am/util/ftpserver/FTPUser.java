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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.List;

/**
 * FTP用户
 * Created by Alex on 2017/12/20.
 */
@SuppressWarnings("WeakerAccess")
public class FTPUser implements Parcelable {

    public static final Parcelable.Creator<FTPUser> CREATOR = new Parcelable.Creator<FTPUser>() {
        @Override
        public FTPUser createFromParcel(Parcel source) {
            return new FTPUser(source);
        }

        @Override
        public FTPUser[] newArray(int size) {
            return new FTPUser[size];
        }
    };
    private static final String USER_NAME_ANONYMOUS = "anonymous";
    private final String mName;
    private final String mPassword;
    private final String mHomeDirectory;
    private final boolean mAdmin;
    private final boolean mEnable;
    private final int mIdleSec;
    private final boolean mHasWritePermission;
    private final int mMaxDownloadRate;
    private final int mMaxUploadRate;
    private final int mMaxConcurrentLogin;
    private final int mMaxConcurrentLoginPerIP;
    private boolean mAnonymous = false;

    private FTPUser(String name, String password, String homeDirectory, boolean admin) {
        this(name, password, homeDirectory, admin, true, 60,
                true, 0, 0,
                10, 10);
    }

    public FTPUser(String name, String password, String homeDirectory, boolean admin,
                   boolean enable, int idleSec,
                   boolean hasWritePermission, int maxDownloadRate, int maxUploadRate,
                   int maxConcurrentLogin, int maxConcurrentLoginPerIP) {
        mName = name;
        mPassword = password;
        mHomeDirectory = homeDirectory;
        mAdmin = admin;
        mEnable = enable;
        mIdleSec = idleSec;
        mHasWritePermission = hasWritePermission;
        mMaxDownloadRate = maxDownloadRate;
        mMaxUploadRate = maxUploadRate;
        mMaxConcurrentLogin = maxConcurrentLogin;
        mMaxConcurrentLoginPerIP = maxConcurrentLoginPerIP;
    }

    protected FTPUser(Parcel in) {
        this.mName = in.readString();
        this.mPassword = in.readString();
        this.mHomeDirectory = in.readString();
        this.mAdmin = in.readByte() != 0;
        this.mEnable = in.readByte() != 0;
        this.mIdleSec = in.readInt();
        this.mHasWritePermission = in.readByte() != 0;
        this.mMaxDownloadRate = in.readInt();
        this.mMaxUploadRate = in.readInt();
        this.mMaxConcurrentLogin = in.readInt();
        this.mMaxConcurrentLoginPerIP = in.readInt();
        this.mAnonymous = in.readByte() != 0;
    }

    static FTPUser from(User user) {
        final String name = user.getName();
        final String password = user.getPassword();
        final String homeDirectory = user.getHomeDirectory();
        final boolean enable = user.getEnabled();
        final int idleSec = user.getMaxIdleTime();
        boolean hasWritePermission = false;
        List<? extends Authority> authorities = user.getAuthorities();
        if (authorities != null) {
            for (Authority authority : authorities) {
                if (authority instanceof WritePermission) {
                    hasWritePermission = true;
                    break;
                }
            }
        }
        return new FTPUser(name, password, homeDirectory, false,
                enable, idleSec, hasWritePermission,
                0, 0, 10, 10);
    }

    static FTPUser getAnonymous(String homeDirectory) {
        final FTPUser user = new FTPUser(USER_NAME_ANONYMOUS, null, homeDirectory,
                false);
        user.mAnonymous = true;
        return user;
    }

    public String getName() {
        return mName;
    }

    public String getPassword() {
        return mPassword;
    }

    public boolean isAdmin() {
        return mAdmin;
    }

    public boolean isAnonymous() {
        return mAnonymous;
    }

    public User create() {
        BaseUser user = new BaseUser();
        user.setEnabled(mEnable);
        user.setHomeDirectory(mHomeDirectory);
        user.setMaxIdleTime(mIdleSec);
        user.setName(mName);
        if (!TextUtils.isEmpty(mPassword))
            user.setPassword(mPassword);
        final ArrayList<Authority> authorities = new ArrayList<>();
        if (mHasWritePermission)
            authorities.add(new WritePermission());
        authorities.add(new TransferRatePermission(mMaxDownloadRate, mMaxUploadRate));
        authorities.add(new ConcurrentLoginPermission(mMaxConcurrentLogin,
                mMaxConcurrentLoginPerIP));
        user.setAuthorities(authorities);
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeString(this.mPassword);
        dest.writeString(this.mHomeDirectory);
        dest.writeByte(this.mAdmin ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mEnable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mIdleSec);
        dest.writeByte(this.mHasWritePermission ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mMaxDownloadRate);
        dest.writeInt(this.mMaxUploadRate);
        dest.writeInt(this.mMaxConcurrentLogin);
        dest.writeInt(this.mMaxConcurrentLoginPerIP);
        dest.writeByte(this.mAnonymous ? (byte) 1 : (byte) 0);
    }


}
