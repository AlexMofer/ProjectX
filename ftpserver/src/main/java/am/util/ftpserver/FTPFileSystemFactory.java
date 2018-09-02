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

import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.User;

import java.util.ArrayList;

/**
 * FTP文件系统视图工厂
 * Created by Alex on 2017/12/19.
 */
public class FTPFileSystemFactory implements FileSystemFactory {

    private static volatile FTPFileSystemFactory FACTORY;

    private final ArrayList<FTPFileSystemView> mViews = new ArrayList<>();

    public static FTPFileSystemFactory getInstance() {
        if (FACTORY == null) {
            FACTORY = new FTPFileSystemFactory();
        }
        return FACTORY;
    }

    @Override
    public FileSystemView createFileSystemView(User user) {
        final String home = user.getHomeDirectory();
        final String owner = user.getName();
        final String group = user.getName();
        final int streamSize = FTPFile.DEFAULT_SIZE;

        synchronized (this) {
            if (mViews.isEmpty()) {
                return new FTPFileSystemView(home, owner, group, streamSize);
            } else {
                final FTPFileSystemView view = mViews.remove(0);
                view.init(home, owner, group, streamSize);
                return view;
            }
        }
    }

    void saveFileSystemView(FTPFileSystemView view) {
        synchronized (this) {
            mViews.add(view);
        }
    }
}