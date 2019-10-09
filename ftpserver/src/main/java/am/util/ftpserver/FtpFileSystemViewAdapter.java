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

/**
 * 根目录提供者
 * Created by Alex on 2019/10/8.
 */
public interface FtpFileSystemViewAdapter {

    /**
     * 附着到用户
     *
     * @param user 用户
     */
    void onAttached(FtpUser user);

    /**
     * 获取文件系统视图
     *
     * @return 文件系统视图
     */
    FileSystemView createFileSystemView();
}
