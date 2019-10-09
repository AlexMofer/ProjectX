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

import org.apache.ftpserver.usermanager.UserManagerFactory;

import java.util.Collection;

/**
 * FTP用户管理器工厂
 * Created by Alex on 2019/10/8.
 */
public class FtpUserManagerFactory implements UserManagerFactory {

    private static FtpUserManagerFactory mInstance;
    private String mAdminName = "admin";
    private FtpUser[] mUserArray = null;
    private Collection<? extends FtpUser> mUserList = null;
    private FtpUser mAnonymous = null;
    private FtpUserManager.OnUserChangedListener mListener = null;

    private FtpUserManagerFactory() {
        //no instance
    }

    public static FtpUserManagerFactory getInstance() {
        if (mInstance == null)
            mInstance = new FtpUserManagerFactory();
        mInstance.reset();
        return mInstance;
    }

    @Override
    public FtpUserManager createUserManager() {
        final FtpUserManager manager;
        if (mUserList != null) {
            manager = new FtpUserManager(mAdminName, mUserList);
        } else {
            if (mUserArray != null) {
                manager = new FtpUserManager(mAdminName, mUserArray);
            } else {
                manager = new FtpUserManager(mAdminName);
            }
        }
        if (mAnonymous != null)
            manager.addUser(mAnonymous);
        manager.setOnUserChangedListener(mListener);
        return manager;
    }

    /**
     * 重置
     */
    @SuppressWarnings("WeakerAccess")
    public void reset() {
        mAdminName = "admin";
        mUserArray = null;
        mUserList = null;
        mAnonymous = null;
        mListener = null;
    }

    /**
     * 设置管理员名称
     *
     * @param adminName 管理员名称
     */
    @SuppressWarnings("WeakerAccess")
    public void setAdminName(String adminName) {
        if (adminName == null)
            return;
        mAdminName = adminName;
    }

    /**
     * 设置用户
     *
     * @param users 用户
     */
    @SuppressWarnings("WeakerAccess")
    public void setUsers(FtpUser... users) {
        mUserArray = users;
        mUserList = null;
    }

    /**
     * 设置用户
     *
     * @param users 用户
     */
    @SuppressWarnings("WeakerAccess")
    public void setUsers(Collection<? extends FtpUser> users) {
        mUserArray = null;
        mUserList = users;
    }

    /**
     * 设置匿名是否启用
     *
     * @param adapter 匿名用户的文件系统视图提供者，传空表示关闭
     */
    @SuppressWarnings("WeakerAccess")
    public void setAnonymousEnable(FtpFileSystemViewAdapter adapter) {
        mAnonymous = FtpUser.getAnonymous(adapter);
    }

    /**
     * 设置用户变更监听
     *
     * @param listener 用户变更监听
     */
    @SuppressWarnings("WeakerAccess")
    public void setOnUserChangedListener(FtpUserManager.OnUserChangedListener listener) {
        mListener = listener;
    }
}
