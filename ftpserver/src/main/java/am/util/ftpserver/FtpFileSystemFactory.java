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

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

/**
 * FTP 文件系统工厂
 * Created by Alex on 2019/10/7.
 */
public class FtpFileSystemFactory implements FileSystemFactory {

    @Override
    public FileSystemView createFileSystemView(User user) throws FtpException {
        if (!(user instanceof FtpUser))
            throw new FtpException("Unsupported user type.");
        final FileSystemView view =
                ((FtpUser) user).getFileSystemViewAdapter().createFileSystemView();
        if (view == null)
            throw new FtpException("Cannot create file system view.");
        return view;
    }
}
