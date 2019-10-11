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

package am.project.x.business.others.ftp.advanced;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 高级Ftp服务器配置
 * Created by Alex on 2019/10/10.
 */
final class AdvancedFtpConfig {
    private static final String SP_NAME = "ftp_config_advanced";
    private static final String KEY_PORT = "port";
    private static final String KEY_AUTO_CHANGE = "auto_change";
    private static final String KEY_URI = "uri";
    private final SharedPreferences mPreferences;
    private int mPort;
    private boolean mAutoChange;
    private String mUri;

    AdvancedFtpConfig(Context context) {
        mPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        mPort = mPreferences.getInt(KEY_PORT, 2020);
        mAutoChange = mPreferences.getBoolean(KEY_AUTO_CHANGE, true);
        mUri = mPreferences.getString(KEY_URI, null);
        if (mPort <= 0 || mPort > 65535)
            mPort = 2020;
    }

    int getPort() {
        return mPort;
    }

    void setPort(int port) {
        if (mPort <= 0 || mPort > 65535)
            return;
        mPort = port;
        mPreferences.edit().putInt(KEY_PORT, port).apply();
    }

    boolean isAutoChangePort() {
        return mAutoChange;
    }

    void setAutoChangePort(boolean auto) {
        mAutoChange = auto;
        mPreferences.edit().putBoolean(KEY_AUTO_CHANGE, auto).apply();
    }

    String getUri() {
        return mUri;
    }

    void setUri(String uri) {
        mUri = uri;
        mPreferences.edit().putString(KEY_URI, uri).apply();
    }
}
