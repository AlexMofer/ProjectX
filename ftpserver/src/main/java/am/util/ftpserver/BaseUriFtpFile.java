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

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.provider.DocumentsContract;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import org.apache.ftpserver.ftplet.FtpFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Uri形式的FTP文件
 * Created by Alex on 2019/10/7.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
abstract class BaseUriFtpFile extends BaseFtpFile {

    private static final int DEFAULT_SIZE = 1024 * 1024;
    private Context mContext;
    private DocumentFile mDocument;
    private String mAbsolutePath;
    private int mBufferSize = DEFAULT_SIZE;

    BaseUriFtpFile(FtpUser user) {
        super(user);
    }

    void set(Context context, DocumentFile document, String absolutePath) {
        mContext = context;
        mDocument = document;
        mAbsolutePath = absolutePath;
    }

    @Override
    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    @Override
    public String getName() {
        return mDocument.getName();
    }

    @Override
    public boolean isHidden() {
        return getName().charAt(0) == '.';
    }

    @Override
    public boolean isDirectory() {
        return mDocument.isDirectory();
    }

    @Override
    public boolean isFile() {
        return mDocument.isFile();
    }

    @Override
    public boolean doesExist() {
        return mDocument.exists();
    }

    @Override
    public boolean isReadable() {
        return mDocument.canRead();
    }

    @Override
    public boolean isWritable() {
        if (mDocument.exists())
            return mDocument.canWrite();
        DocumentFile parent = mDocument.getParentFile();
        while (parent != null) {
            if (parent.exists())
                return parent.canWrite();
            parent = parent.getParentFile();
        }
        return false;
    }

    @Override
    public boolean isRemovable() {
        return mDocument.canWrite();
    }

    @Override
    public int getLinkCount() {
        return 0;
    }

    @Override
    public long getLastModified() {
        return mDocument.lastModified();
    }

    @Override
    public boolean setLastModified(long time) {
        // 该方法未测试可行性 TODO
        final ContentValues values = new ContentValues();
        values.put(DocumentsContract.Document.COLUMN_LAST_MODIFIED, time);
        final int updated = mContext.getContentResolver().update(mDocument.getUri(), values,
                null, null);
        return updated == 1;
    }

    @Override
    public long getSize() {
        return mDocument.length();
    }

    @Override
    public Object getPhysicalFile() {
        return mDocument;
    }

    @Override
    public boolean delete() {
        return mDocument.delete();
    }

    @Override
    public boolean move(FtpFile destination) {
        if (!(destination instanceof BaseUriFtpFile))
            return false;
        final BaseUriFtpFile file = (BaseUriFtpFile) destination;
        if (TextUtils.equals(getParentAbsolutePath(), file.getParentAbsolutePath())) {
            // 重命名文件及文件夹
            if (mDocument.renameTo(file.getName())) {
                file.set(mContext, mDocument, getAbsolutePath());
                return true;
            }
            return false;
        }
        // 移动
        if (isFile()) {
            // 移动文件
            // 方法未经过测试，Windows文件管理器不支持FTP移动文件（不知道是FTP本身不支持还是Windows文件管理器的限制）
            final DocumentFile dir = file.getDocument();
            final DocumentFile dest = Utils.createNewFile(dir, file.getName());
            if (dest == null)
                return false;
            OutputStream output = null;
            InputStream input = null;
            boolean copied;
            try {
                output = mContext.getContentResolver().openOutputStream(dest.getUri());
                input = mContext.getContentResolver().openInputStream(mDocument.getUri());
                copied = Utils.copy(output, input);
            } catch (IOException e) {
                copied = false;
            } finally {
                if (output != null)
                    try {
                        output.close();
                    } catch (IOException e) {
                        // ignore
                    }
                if (input != null)
                    try {
                        input.close();
                    } catch (IOException e) {
                        // ignore
                    }
            }
            if (!copied) {
                dest.delete();
                return false;
            }
            mDocument.delete();
            final String absolutePath = file.getAbsolutePath();
            set(mContext, dest, absolutePath);
            file.set(mContext, dest, absolutePath);
            return true;
        }
        return false;
    }

    @Override
    public List<? extends FtpFile> listFiles() {
        if (!mDocument.exists() || !mDocument.isDirectory())
            return null;
        final DocumentFile[] children = mDocument.listFiles();
        if (children.length <= 0)
            return new ArrayList<>(0);
        final ArrayList<BaseUriFtpFile> files = new ArrayList<>(children.length);
        for (DocumentFile child : children) {
            final String absolutePath = mAbsolutePath + "/" + child.getName();
            final BaseUriFtpFile item = onCreateChild();
            item.set(mContext, child, absolutePath);
            files.add(item);
        }
        return files;
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        if (offset != 0)
            throw new IOException("File is not random accessible.");
        final OutputStream output =
                mContext.getContentResolver().openOutputStream(mDocument.getUri());
        if (output == null)
            throw new IOException("File can not write.");
        return output;
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        final InputStream input = mContext.getContentResolver().openInputStream(mDocument.getUri());
        if (input == null)
            throw new IOException("File can not read.");
        if (offset == 0)
            return new BufferedInputStream(input, mBufferSize);
        if (input.skip(offset) == offset)
            return new BufferedInputStream(input, mBufferSize);
        return new BufferedInputStream(input, mBufferSize);
    }

    /**
     * 创建子项
     *
     * @return 子项
     */
    protected abstract BaseUriFtpFile onCreateChild();

    Context getContext() {
        return mContext;
    }

    DocumentFile getDocument() {
        return mDocument;
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

    private String getParentAbsolutePath() {
        final String absolutePath = getAbsolutePath();
        if (TextUtils.equals(UriFtpFileSystemView.ROOT_PATH, absolutePath))
            return null;
        return absolutePath.substring(0, absolutePath.length() - getName().length());
    }
}
