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

import androidx.documentfile.provider.DocumentFile;

import org.apache.ftpserver.ftplet.FtpFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Uri形式的FTP文件
 * Created by Alex on 2019/10/7.
 */
class UriFtpFile extends BaseFtpFile {

    private final DocumentFile mDocument;

    UriFtpFile(FtpUser user, DocumentFile document) {
        super(user);
        mDocument = document;
    }

    @Override
    public String getAbsolutePath() {
        // TODO
        return null;
    }

    @Override
    public String getName() {
        return mDocument.getName();
    }

    @Override
    public boolean isHidden() {
        return false;// TODO
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
        return false;// TODO
    }

    @Override
    public boolean isReadable() {
        return mDocument.canRead();
    }

    @Override
    public boolean isWritable() {
        return mDocument.canWrite();
    }

    @Override
    public boolean isRemovable() {
        return isWritable();
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
        return false;// TODO
    }

    @Override
    public long getSize() {
        return mDocument.length();
    }

    @Override
    public Object getPhysicalFile() {
        return mDocument;// TODO
    }

    @Override
    public boolean mkdir() {
        return false;// TODO
    }

    @Override
    public boolean delete() {
        return mDocument.delete();
    }

    @Override
    public boolean move(FtpFile destination) {
        return false;// TODO
    }

    @Override
    public ArrayList<UriFtpFile> listFiles() {
        return null;// TODO
    }

    @Override
    public OutputStream createOutputStream(long offset) throws IOException {
        return null;// TODO
    }

    @Override
    public InputStream createInputStream(long offset) throws IOException {
        return null;// TODO
    }
}
