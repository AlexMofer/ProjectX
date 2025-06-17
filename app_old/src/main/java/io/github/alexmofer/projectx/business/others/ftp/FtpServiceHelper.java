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

package io.github.alexmofer.projectx.business.others.ftp;

import android.content.Context;

import io.github.alexmofer.projectx.business.others.ftp.legacy.LegacyFtpService;

/**
 * FTP服务辅助器
 * Created by Alex on 2019/10/10.
 */
final class FtpServiceHelper {

    private static FtpServiceHelper mInstance;

    private FtpServiceHelper() {
        //no instance
    }

    public static FtpServiceHelper getInstance() {
        if (mInstance == null)
            mInstance = new FtpServiceHelper();
        return mInstance;
    }

    /**
     * 判断服务是否开启
     *
     * @return 服务已开启时返回true
     */
    boolean isStarted() {
        return LegacyFtpService.isStarted();
    }

    /**
     * 开启服务
     */
    void start(Context context) {
        LegacyFtpService.start(context);
    }

    /**
     * 停止服务
     */
    void stop(Context context) {
        LegacyFtpService.stop(context);
    }
}
