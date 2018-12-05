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

import org.apache.ftpserver.ftplet.FtpFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP文件
 * Created by Alex on 2017/12/20.
 */
@SuppressWarnings("WeakerAccess")
public class FTPFile implements FtpFile {

    public static final int DEFAULT_SIZE = 1024 * 1024;
    private final String mOwner;
    private final String mGroup;
    private File mFile;
    private int mStreamSize;

    public FTPFile(String path, String owner, String group, int streamSize) {
        mFile = new File(path);
        mOwner = owner;
        mGroup = group;
        if (streamSize <= 0) {
            mStreamSize = DEFAULT_SIZE;
        } else {
            mStreamSize = streamSize;
        }
    }

    @Override
    public String getAbsolutePath() {
        return mFile.getAbsolutePath();
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public boolean isHidden() {
        return mFile.isHidden();
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    @Override
    public boolean isFile() {
        return mFile.isFile();
    }

    public void setFile(String path) {
        mFile = new File(path);
    }

    @Override
    public boolean doesExist() {
        if (mFile.exists())
            return true;
        final File parent = mFile.getParentFile();
        final File[] children = parent.listFiles();
        if (children != null) {
            for (File child : children) {
                if (mFile.equals(child))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean isReadable() {
        return mFile.canRead();
    }

    @Override
    public boolean isWritable() {
        if (mFile.exists())
            return mFile.canWrite();
        // 此处可能死循环
        File parent = mFile.getParentFile();
        while (parent != null) {
            if (parent.exists())
                return parent.canWrite();
            parent = parent.getParentFile();
        }
        return false;
    }

    @Override
    public boolean isRemovable() {
        return mFile.canWrite();
    }

    @Override
    public String getOwnerName() {
        return mOwner;
    }

    @Override
    public String getGroupName() {
        return mGroup;
    }

    @Override
    public int getLinkCount() {
        return 0;
    }

    @Override
    public long getLastModified() {
        return mFile.lastModified();
    }

    @Override
    public boolean setLastModified(long time) {
        return mFile.setLastModified(time);
    }

    @Override
    public long getSize() {
        return mFile.length();
    }

    @Override
    public Object getPhysicalFile() {
        return mFile;
    }

    @Override
    public boolean mkdir() {
        return mFile.mkdir();
    }

    @Override
    public boolean delete() {
        return mFile.delete();
    }

    @Override
    public boolean move(FtpFile destination) {
        return mFile.renameTo(new File(destination.getAbsolutePath()));
    }

    @Override
    public List<? extends FtpFile> listFiles() {
        final File[] children = mFile.listFiles();
        if (children != null) {
            final ArrayList<FTPFile> files = new ArrayList<>(children.length);
            for (File child : children) {
                files.add(new FTPFile(child.getPath(), mOwner, mGroup, mStreamSize));
            }
            return files;
        }
        return new ArrayList<>(0);
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        final OutputStream output;
        if (offset == 0) {
            output = new FileOutputStream(mFile);
        } else if (offset == mFile.length()) {
            output = new FileOutputStream(mFile, true);
        } else {
            output = new RandomAccessFileOutputStream(mFile, "rw", offset);
        }
        return new BufferedOutputStream(output, mStreamSize);
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        final FileInputStream input = new FileInputStream(mFile);
        //noinspection ResultOfMethodCallIgnored
        input.skip(offset);
        return new BufferedInputStream(input, mStreamSize);
    }
}
