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

import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;

/**
 * 文件系统视图
 * Created by Alex on 2019/10/8.
 */
final class UriFtpFileSystemView implements FileSystemView {

    private final FtpUser mUser;
    private final Uri mUri;

    UriFtpFileSystemView(FtpUser user, Context context, Uri homeDirectory) {
        mUser = user;
        mUri = homeDirectory;
    }

    @Override
    public UriFtpFile getHomeDirectory() {
        return null;
    }

    @Override
    public UriFtpFile getWorkingDirectory() throws FtpException {
        return null;
    }

    @Override
    public boolean changeWorkingDirectory(String dir) throws FtpException {
        return false;
    }

    @Override
    public UriFtpFile getFile(String file) throws FtpException {
        return null;
    }

    @Override
    public boolean isRandomAccessible() throws FtpException {
        return false;
    }

    @Override
    public void dispose() {

    }
}
