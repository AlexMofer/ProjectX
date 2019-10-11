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

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import org.apache.ftpserver.ftplet.FileSystemView;

import java.util.ArrayList;

/**
 * 文件系统视图
 * Created by Alex on 2019/10/8.
 */
final class UriFtpFileSystemView implements FileSystemView {

    static final String ROOT_PATH = "/content:root";
    private final FtpUser mUser;
    private final DocumentFile mRoot;
    private final UriFtpFile mHome;
    private final UriFtpFile mWorking;
    private final ArrayList<UriFtpFile> mItems = new ArrayList<>();
    private final ArrayList<UriFtpFile> mSaved = new ArrayList<>();

    UriFtpFileSystemView(FtpUser user, Context context, Uri homeDirectory) {
        mUser = user;
        mRoot = DocumentFile.fromTreeUri(context, homeDirectory);
        mHome = new UriFtpFile(user);
        mHome.set(context, mRoot, ROOT_PATH);
        mWorking = new UriFtpFile(user);
        mWorking.set(context, mRoot, ROOT_PATH);
    }

    @Override
    public UriFtpFile getHomeDirectory() {
        return mHome;
    }

    @Override
    public UriFtpFile getWorkingDirectory() {
        return mWorking;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) {
        // 支持库该方法调用有错误，该情况由复制非空文件夹导致，会访问一个不存在的路径，因此路径会增加一个错误的前缀，尝试做过容错处理，但是处理效果是主页跟该复制的文件夹无法区分
        String directory = dir;
        final String working = mWorking.getAbsolutePath();
        if (TextUtils.equals(ROOT_PATH, directory) ||
                TextUtils.equals(ROOT_PATH + "/", directory)) {
            mWorking.set(mHome.getContext(), mRoot, ROOT_PATH);
            return true;
        }
        DocumentFile target;
        final StringBuilder absolutePath;
        if (directory.startsWith(ROOT_PATH)) {
            target = mRoot;
            absolutePath = new StringBuilder(ROOT_PATH);
            directory = directory.replace(ROOT_PATH, "");
        } else if (directory.startsWith(working)) {
            target = mWorking.getDocument();
            absolutePath = new StringBuilder(working);
            directory = directory.replace(working, "");
        } else {
            target = mWorking.getDocument();
            absolutePath = new StringBuilder(working);
        }
        if (directory.charAt(0) == '/')
            directory = directory.substring(1);
        if (directory.charAt(directory.length() - 1) == '/')
            directory = directory.substring(0, directory.length() - 1);
        if (directory.contains("/")) {
            final String[] dirs = directory.split("/");
            for (String name : dirs) {
                target = target.findFile(name);
                if (target == null)
                    return false;
                absolutePath.append("/");
                absolutePath.append(target.getName());
            }
        } else {
            target = target.findFile(directory);
            if (target == null)
                return false;
            absolutePath.append("/");
            absolutePath.append(target.getName());
        }
        mWorking.set(mHome.getContext(), target, absolutePath.toString());
        return true;
    }

    @Override
    public UriFtpFile getFile(String file) {
        final UriFtpFile item;
        if (mSaved.isEmpty()) {
            item = new UriFtpFile(mUser);
        } else {
            item = mSaved.remove(mSaved.size() - 1);
        }
        if ("./".equals(file) || ".".equals(file)) {
            item.set(mWorking.getContext(), mWorking.getDocument(), mWorking.getAbsolutePath());
            return item;
        }
        if (!file.contains("/")) {
            // 当前工作目录下的文件或文件夹
            final DocumentFile working = mWorking.getDocument();
            final DocumentFile target = working.findFile(file);
            if (target != null) {
                final String absolutePath = mWorking.getAbsolutePath() + "/" + file;
                item.set(mWorking.getContext(), target, absolutePath);
                return item;
            }
            // 创建文件或文件夹
            item.setCreate(mWorking.getContext(), working, mWorking.getAbsolutePath(), file);
            return item;
        }
        if (TextUtils.equals(ROOT_PATH, file) ||
                TextUtils.equals(ROOT_PATH + "/", file)) {
            // 为主页
            item.set(mHome.getContext(), mRoot, ROOT_PATH);
            return item;
        }
        DocumentFile target;
        final StringBuilder absolutePath;
        if (file.startsWith(ROOT_PATH)) {
            // 从根目录获取
            target = mRoot;
            absolutePath = new StringBuilder(ROOT_PATH);
            file = file.replace(ROOT_PATH, "");
        } else {
            target = mWorking.getDocument();
            absolutePath = new StringBuilder(mWorking.getAbsolutePath());
        }
        if (file.charAt(0) == '/')
            file = file.substring(1);
        if (file.charAt(file.length() - 1) == '/')
            file = file.substring(0, file.length() - 1);
        final String[] names = file.split("/");
        final int count = names.length;
        for (int i = 0; i < count; i++) {
            final String name = names[i];
            if (TextUtils.isEmpty(name))
                continue;
            if (i == count - 1) {
                final DocumentFile result = target.findFile(name);
                if (result != null) {
                    absolutePath.append("/");
                    absolutePath.append(name);
                    item.set(mWorking.getContext(), result, absolutePath.toString());
                    return item;
                }
                // 创建文件或文件夹
                item.setCreate(mHome.getContext(), target, absolutePath.toString(), name);
                return item;
            } else {
                target = target.findFile(name);
                if (target == null)
                    return null;
                absolutePath.append("/");
                absolutePath.append(name);
            }
        }
        return null;
    }

    @Override
    public boolean isRandomAccessible() {
        return false;
    }

    @Override
    public void dispose() {
        int count = mItems.size();
        while (count > 0) {
            final UriFtpFile item = mItems.remove(count - 1);
            item.set(mHome.getContext(), null, null);
            mSaved.add(item);
            count = mItems.size();
        }
    }
}
