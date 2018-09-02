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

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;

import java.io.File;
import java.util.ArrayList;

/**
 * FTP文件系统视图
 * Created by Alex on 2017/12/20.
 */
@SuppressWarnings("all")
public class FTPFileSystemView implements FileSystemView {

    private final ArrayList<FTPFile> mChildren = new ArrayList<>();
    private final ArrayList<FTPFile> mSaved = new ArrayList<>();
    private String mOwner;
    private String mGroup;
    private FTPFile mHome;
    private FTPFile mWorking;
    private String mPath;
    private int mStreamSize;

    public FTPFileSystemView(String home, String owner, String group, int streamSize) {
        init(home, owner, group, streamSize);
    }

    public void init(String home, String owner, String group, int streamSize) {
        mOwner = owner;
        mGroup = group;
        mStreamSize = streamSize;
        if (mHome == null || !TextUtils.equals(mHome.getAbsolutePath(), home)) {
            mHome = new FTPFile(home, mOwner, mGroup, mStreamSize);
        }
        if (mWorking == null || !TextUtils.equals(mPath, home)) {
            mPath = home;
            mWorking = new FTPFile(mPath, mOwner, mGroup, mStreamSize);
        }
    }

    @Override
    public FtpFile getHomeDirectory() throws FtpException {
        return mHome;
    }

    @Override
    public FtpFile getWorkingDirectory() throws FtpException {
        return mWorking;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) throws FtpException {
        final String working = mPath;
        String path = dir;
        File fileDir = new File(path);
        if (!fileDir.isAbsolute()) {
            path = working + File.separator + dir;
            fileDir = new File(path);
        }
        if (!fileDir.isDirectory())
            return false;
        final String absPath = fileDir.getAbsolutePath();
        if (working.length() * 2 == absPath.length() && (working + working).equals(absPath))
            return true;
        mPath = path;
        mWorking.setFile(mPath);
        return true;
    }

    @Override
    public FtpFile getFile(String file) throws FtpException {
        final String working = mPath;
        final String path;
        if (file.charAt(0) == '/') {
            path = file;
        } else if ("./".equals(file) || ".".equals(file)) {
            path = working;
        } else {
            path = working + "/" + file;
        }
        final FTPFile child;
        if (mSaved.isEmpty()) {
            child = new FTPFile(path, mOwner, mGroup, mStreamSize);
        } else {
            child = mSaved.remove(0);
            child.setFile(path);
        }
        mChildren.add(child);
        return child;
    }

    @Override
    public boolean isRandomAccessible() throws FtpException {
        return true;
    }

    @Override
    public void dispose() {
        mSaved.addAll(mChildren);
        mChildren.clear();
        FTPFileSystemFactory.getInstance().saveFileSystemView(this);
    }
}
