/*
 * Copyright (C) 2023 AlexMofer
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

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

/**
 * 字符串资源
 * Created by Alex on 2023/11/28.
 */
public class StringResource {
    private final int mRes;
    private final Object[] mFormatArgs;
    private final String mStr;

    public StringResource(@StringRes int res) {
        mRes = res;
        mFormatArgs = null;
        mStr = null;
    }

    public StringResource(@StringRes int res, Object... formatArgs) {
        mRes = res;
        mFormatArgs = formatArgs;
        mStr = null;
    }

    public StringResource(String str) {
        mRes = ResourcesCompat.ID_NULL;
        mFormatArgs = null;
        mStr = str;
    }

    /**
     * 设置文本
     *
     * @param view     TextView
     * @param resource 字符串资源
     */
    public static void setText(TextView view, StringResource resource) {
        if (view == null) {
            return;
        }
        if (resource == null) {
            view.setText(null);
            return;
        }
        view.setText(resource.getString(view.getContext()));
    }

    /**
     * 显示Toast
     *
     * @param context  Context
     * @param resource 字符串资源
     */
    public static void showToast(Context context, StringResource resource) {
        if (context == null || resource == null) {
            return;
        }
        resource.showToast(context);
    }

    /**
     * 获取消息
     *
     * @param t 异常
     * @return 消息
     */
    public static StringResource from(Throwable t) {
        return StringResourceException.getMessage(t);
    }

    /**
     * 获取字符串
     *
     * @param context Context
     * @return 字符串
     */
    @Nullable
    public String getString(Context context) {
        if (mRes != ResourcesCompat.ID_NULL) {
            if (mFormatArgs == null) {
                return context.getString(mRes);
            } else {
                return context.getString(mRes, mFormatArgs);
            }
        }
        return mStr;
    }

    /**
     * 显示Toast
     *
     * @param context Context
     */
    public void showToast(Context context) {
        final String message = getString(context);
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}