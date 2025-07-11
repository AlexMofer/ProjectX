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

    public StringResourceException() {
        super();
        mMessage = new StringResource("");
    }

    public StringResourceException(String message) {
        super(message);
        mMessage = new StringResource(message);
    }

    public StringResourceException(String message, Throwable cause) {
        super(message, cause);
        mMessage = new StringResource(message);
    }

    public StringResourceException(@NonNull Throwable cause) {
        super(cause);
        mMessage = StringResource.from(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected StringResourceException(String message, Throwable cause,
                                      boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        mMessage = new StringResource(message);
    }

    public StringResourceException(@NonNull StringResource message) {
        super();
        mMessage = message;
    }

    public StringResourceException(@NonNull StringResource message, Throwable cause) {
        super(cause);
        mMessage = message;
    }

    public StringResourceException(@StringRes int message) {
        this(new StringResource(message));
    }

    public StringResourceException(@StringRes int message, Object... formatArgs) {
        this(new StringResource(message, formatArgs));
    }

    public StringResourceException(Throwable cause, @StringRes int message) {
        this(new StringResource(message), cause);
    }

    public StringResourceException(Throwable cause, @StringRes int message, Object... formatArgs) {
        this(new StringResource(message, formatArgs), cause);
    }

    /**
     * 获取消息
     *
     * @param t 异常
     * @return 消息
     */
    public static StringResource getMessage(Throwable t) {
        if (t instanceof StringResourceException) {
            return ((StringResourceException) t).getStringResource();
        }
        return new StringResource(t.getMessage());
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
