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

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.AuthorizationRequest;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;

/**
 * FTP 用户
 * Created by Alex on 2019/10/8.
 */
public class FtpUser implements User {

    static final String NAME_ANONYMOUS = "anonymous";

    private final String mName;
    private final String mPassword;
    private FtpFileSystemViewAdapter mAdapter;
    private int mMaxIdleTime = 0; // no limit
    private boolean mEnabled = true;
    private boolean mEditable = true;

    private final ArrayList<Authority> mAuthorities = new ArrayList<>();

    private FtpUser(String name, String password, FtpFileSystemViewAdapter adapter) {
        if (TextUtils.isEmpty(name))
            throw new RuntimeException("User name can not be empty!");
        mName = name;
        mPassword = password;
        mAdapter = adapter;
        mAdapter.onAttached(this);

        // 写入权限
        mAuthorities.add(new WritePermission());
        // 上传下载数目权限
        mAuthorities.add(new TransferRatePermission(0, 0));
        // 登录权限
        mAuthorities.add(new ConcurrentLoginPermission(10,
                10));
    }

    FtpUser(User user) {
        this(user.getName(), user.getPassword(),
                new FileFtpFileSystemViewAdapter(user.getHomeDirectory()));
        mMaxIdleTime = user.getMaxIdleTime();
        mEnabled = user.getEnabled();
    }

    /**
     * 创建匿名用户
     * @param adapter 文件系统内容提供者
     * @return 匿名用户
     */
    @SuppressWarnings("WeakerAccess")
    public static FtpUser createAnonymous(FtpFileSystemViewAdapter adapter) {
        if (adapter == null)
            return null;
        final FtpUser anonymous = new FtpUser(NAME_ANONYMOUS, null, adapter);
        anonymous.setEditable(false);
        return anonymous;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public String getPassword() {
        return mPassword;
    }

    @Override
    public int getMaxIdleTime() {
        return mMaxIdleTime;
    }

    /**
     * 设置最大空闲时间
     *
     * @param maxIdleTime 最大空闲时间（以秒为单位，小于等于0表示不受限）
     */
    @SuppressWarnings("unused")
    public void setMaxIdleTime(int maxIdleTime) {
        mMaxIdleTime = maxIdleTime;
    }

    @Override
    public boolean getEnabled() {
        return mEnabled;
    }

    /**
     * 设置账户是否启用
     *
     * @param enabled 是否启用
     */
    @SuppressWarnings("unused")
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public String getHomeDirectory() {
        try {
            return mAdapter.createFileSystemView().getHomeDirectory().getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断账户是否可编辑
     *
     * @return 账户可以编辑时返回true
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isEditable() {
        return mEditable;
    }

    /**
     * 设置是否可编辑
     *
     * @param editable 是否可编辑
     */
    @SuppressWarnings("WeakerAccess")
    public void setEditable(boolean editable) {
        mEditable = editable;
    }

    /**
     * 获取文件系统视图提供者
     *
     * @return 文件系统视图提供者
     */
    @SuppressWarnings("WeakerAccess")
    public FtpFileSystemViewAdapter getFileSystemViewAdapter() {
        return mAdapter;
    }

    @Override
    public ArrayList<Authority> getAuthorities() {
        return mAuthorities;
    }

    @Override
    public ArrayList<Authority> getAuthorities(Class<? extends Authority> clazz) {
        final ArrayList<Authority> selected = new ArrayList<>();
        for (Authority authority : mAuthorities) {
            if (authority.getClass().equals(clazz)) {
                selected.add(authority);
            }
        }
        return selected;
    }

    @Override
    public AuthorizationRequest authorize(AuthorizationRequest request) {
        for (Authority authority : mAuthorities) {
            if (authority.canAuthorize(request)) {
                final AuthorizationRequest result = authority.authorize(request);
                if (result != null)
                    return request;
            }
        }
        return null;
    }

}
