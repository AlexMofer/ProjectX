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

import org.apache.ftpserver.ftplet.FileSystemView;

import java.io.File;
import java.util.ArrayList;

/**
 * 文件系统视图
 * Created by Alex on 2019/10/8.
 */
final class FileFtpFileSystemView implements FileSystemView {

    private final FtpUser mUser;
    private final FileFtpFile mHome;
    private final FileFtpFile mWorking;
    private final ArrayList<FileFtpFile> mItems = new ArrayList<>();
    private final ArrayList<FileFtpFile> mSaved = new ArrayList<>();

    FileFtpFileSystemView(FtpUser user, File homeDirectory) {
        mUser = user;
        mHome = new FileFtpFile(user, homeDirectory);
        mWorking = new FileFtpFile(user, homeDirectory);
    }

    @Override
    public FileFtpFile getHomeDirectory() {
        return mHome;
    }

    @Override
    public FileFtpFile getWorkingDirectory() {
        return mWorking;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) {
        // 支持库该方法调用有错误，该情况由复制非空文件夹导致，会访问一个不存在的路径，
        // 因此路径会增加一个错误的前缀，尝试做过容错处理，但是处理效果是主页跟该复制的文件夹无法区分
        File working = new File(dir);
        if (!working.isAbsolute()) {
            working = new File(mWorking.getPath() + File.separator + dir);
        }
        if (!working.exists() && !working.isDirectory())
            return false;
        mWorking.setFile(working);
        return true;
    }

    @Override
    public FileFtpFile getFile(String file) {
        final String path;
        if (file.charAt(0) == '/') {
            path = file;
        } else if ("./".equals(file) || ".".equals(file)) {
            path = mWorking.getPath();
        } else {
            path = mWorking.getPath() + "/" + file;
        }
        final FileFtpFile item;
        if (mSaved.isEmpty()) {
            item = new FileFtpFile(mUser, new File(path));
        } else {
            item = mSaved.remove(mSaved.size() - 1);
            item.setFile(new File(path));
        }
        mItems.add(item);
        return item;
    }

    @Override
    public boolean isRandomAccessible() {
        return true;
    }

    @Override
    public void dispose() {
        mSaved.addAll(mItems);
        mItems.clear();
    }
}
