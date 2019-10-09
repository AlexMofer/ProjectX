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

/**
 * 基础的FTP文件
 * Created by Alex on 2019/10/9.
 */
abstract class BaseFtpFile implements FtpFile {

    private final FtpUser mUser;

    BaseFtpFile(FtpUser user) {
        mUser = user;
    }

    /**
     * 获取用户
     *
     * @return 用户
     */
    protected FtpUser getUser() {
        return mUser;
    }

    @Override
    public String getOwnerName() {
        return mUser.getName();
    }

    @Override
    public String getGroupName() {
        return mUser.getName();
    }
}
