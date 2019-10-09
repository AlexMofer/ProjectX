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

import org.apache.ftpserver.ftplet.FtpFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * 文件形式的FTP文件
 * Created by Alex on 2019/10/9.
 */
final class FileFtpFile extends BaseFtpFile {

    private static final int DEFAULT_SIZE = 1024 * 1024;
    private File mFile;
    private int mBufferSize = DEFAULT_SIZE;

    FileFtpFile(FtpUser user, File file) {
        super(user);
        mFile = file;
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

    @Override
    public boolean doesExist() {
        return mFile.exists();
    }

    @Override
    public boolean isReadable() {
        return mFile.canRead();
    }

    @Override
    public boolean isWritable() {
        if (mFile.exists())
            return mFile.canWrite();
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
    public ArrayList<FileFtpFile> listFiles() {
        if (!mFile.exists() || !mFile.isDirectory())
            return null;
        final File[] children = mFile.listFiles();
        if (children == null || children.length <= 0)
            return new ArrayList<>(0);
        final ArrayList<FileFtpFile> files = new ArrayList<>(children.length);
        for (File child : children) {
            files.add(new FileFtpFile(getUser(), child));
        }
        return files;
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        final OutputStream output;
        if (offset == 0)
            output = new FileOutputStream(mFile);
        else if (offset == mFile.length())
            output = new FileOutputStream(mFile, true);
        else
            output = new RandomAccessFileOutputStream(mFile, "rw", offset);
        return new BufferedOutputStream(output, mBufferSize);
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        final FileInputStream input = new FileInputStream(mFile);
        //noinspection ResultOfMethodCallIgnored
        input.skip(offset);
        return new BufferedInputStream(input, mBufferSize);
    }

    /**
     * 获取路径
     *
     * @return 路径
     */
    String getPath() {
        return mFile.getPath();
    }

    /**
     * 设置文件
     *
     * @param file 文件
     */
    void setFile(File file) {
        mFile = file;
    }

    /**
     * 设置缓冲大小
     *
     * @param size 缓冲大小
     */
    @SuppressWarnings("unused")
    void setBufferSize(int size) {
        if (size <= 0)
            return;
        mBufferSize = size;
    }

    private class RandomAccessFileOutputStream extends OutputStream {

        private final RandomAccessFile mFile;

        RandomAccessFileOutputStream(File file, String mode, long offset) throws IOException {
            mFile = new RandomAccessFile(file, mode);
            mFile.seek(offset);
        }

        @Override
        public void write(int b) throws IOException {
            mFile.write(b);
        }

        @Override
        public void write(@SuppressWarnings("NullableProblems") byte[] b) throws IOException {
            mFile.write(b);
        }

        @Override
        public void write(@SuppressWarnings("NullableProblems") byte[] b, int off, int len)
                throws IOException {
            mFile.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            super.close();
            mFile.close();
        }
    }
}
