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

import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Uri形式的FTP文件
 * Created by Alex on 2019/10/7.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class UriFtpFile extends CreateUriFtpFile {

    UriFtpFile(FtpUser user) {
        super(user);
    }

    @Override
    protected UriFtpFile onCreateChild() {
        return new UriFtpFile(getUser());
    }
}
