/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.other;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

/**
 * 字符串资源错误
 * Created by Alex on 2024/3/8.
 */
public class StringResourceException extends Exception {

    private final StringResource mMessage;

    public StringResourceException(@NonNull StringResource message) {
        mMessage = message;
    }

    public StringResourceException(@StringRes int message) {
        this(new StringResource(message));
    }

    public StringResourceException(@NonNull StringResource message, String msg) {
        super(msg);
        mMessage = message;
    }

    public StringResourceException(String message) {
        this(new StringResource(message), message);
    }

    public StringResourceException(@NonNull StringResource message,
                                   String msg, Throwable cause) {
        super(msg, cause);
        mMessage = message;
    }

    public StringResourceException(String message, Throwable cause) {
        this(new StringResource(message), message, cause);
    }

    public StringResourceException(@NonNull StringResource message,
                                   Throwable cause) {
        super(cause);
        mMessage = message;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected StringResourceException(@NonNull StringResource message,
                                      String msg, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(msg, cause, enableSuppression, writableStackTrace);
        mMessage = message;
    }

    /**
     * 获取信息
     *
     * @return 信息
     */
    public StringResource getStringResource() {
        return mMessage;
    }
}
