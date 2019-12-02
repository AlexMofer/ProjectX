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

import android.content.ContentResolver;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 创建FTP 文件
 * Created by Alex on 2019/10/11.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
abstract class CreateUriFtpFile extends BaseUriFtpFile {

    private boolean mCreate = false;
    private ContentResolver mContentResolver;
    private DocumentFile mParent;
    private String mParentAbsolutePath;
    private String mName;

    CreateUriFtpFile(FtpUser user) {
        super(user);
    }

    @Override
    public String getAbsolutePath() {
        if (mCreate)
            return mParentAbsolutePath + "/" + mName;
        return super.getAbsolutePath();
    }

    @Override
    public String getName() {
        if (mCreate)
            return mName;
        return super.getName();
    }

    @Override
    public boolean isDirectory() {
        // 创建模式下调用该方法不准确
        if (mCreate)
            return !mName.contains(".");
        return super.isDirectory();
    }

    @Override
    public boolean isFile() {
        // 创建模式下调用该方法不准确
        if (mCreate)
            return mName.contains(".");
        return super.isFile();
    }

    @Override
    public boolean doesExist() {
        if (mCreate)
            return false;
        return super.doesExist();
    }

    @Override
    public boolean isReadable() {
        if (mCreate)
            return false;
        return super.isReadable();
    }

    @Override
    public boolean isWritable() {
        if (mCreate)
            return mParent.exists() && mParent.canWrite();
        return super.isWritable();
    }

    @Override
    public boolean isRemovable() {
        if (mCreate)
            return false;
        return super.isRemovable();
    }

    @Override
    public long getLastModified() {
        if (mCreate)
            return 0;
        return super.getLastModified();
    }

    @Override
    public boolean setLastModified(long time) {
        if (mCreate)
            return false;
        return super.setLastModified(time);
    }

    @Override
    public long getSize() {
        if (mCreate)
            return 0;
        return super.getSize();
    }

    @Override
    public Object getPhysicalFile() {
        if (mCreate)
            return null;
        return super.getPhysicalFile();
    }

    @Override
    public boolean mkdir() {
        // 创建文件夹
        final DocumentFile dir = mParent.createDirectory(mName);
        if (dir == null)
            return false;
        final String absolutePath = mParentAbsolutePath + "/" + mName;
        set(mContentResolver, dir, absolutePath);
        return true;
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        // 创建文件
        final DocumentFile file = Utils.createNewFile(mParent, mName);
        if (file == null)
            throw new IOException("Cannot create new file!");
        final String absolutePath = mParentAbsolutePath + "/" + mName;
        set(mContentResolver, file, absolutePath);
        return super.createOutputStream(offset);
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        if (mCreate)
            throw new IOException("File has not created!");
        return super.createInputStream(offset);
    }

    void setCreate(ContentResolver contentResolver, DocumentFile dir, String parentAbsolutePath,
                   String name) {
        mCreate = true;
        mContentResolver = contentResolver;
        mParent = dir;
        mParentAbsolutePath = parentAbsolutePath;
        mName = name;
    }

    @Override
    void set(ContentResolver contentResolver, DocumentFile document, String absolutePath) {
        mCreate = false;
        mContentResolver = null;
        mParent = null;
        mParentAbsolutePath = null;
        mName = null;
        super.set(contentResolver, document, absolutePath);
    }

    @Override
    ContentResolver getContentResolver() {
        if (mCreate)
            return mContentResolver;
        return super.getContentResolver();
    }

    @Override
    DocumentFile getDocument() {
        if (mCreate)
            return mParent;
        return super.getDocument();
    }
}
