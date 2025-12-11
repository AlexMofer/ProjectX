/*
 * Copyright (C) 2025 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntRange;

/**
 * 窗口工具
 * Created by Alex on 2025/7/16.
 */
public class WindowUtils {

    private WindowUtils() {
        //no instance
    }

    /**
     * 判断是否支持窗口模糊
     *
     * @return 支持窗口模糊时返回true
     */
    //noinspection all
    public static boolean isSupportBlurBehind() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }

    /**
     * 设置窗口模糊
     *
     * @param window           窗口
     * @param blurBehindRadius 模糊半径
     */
    public static void setBlurBehind(Window window, @IntRange(from = 0) int blurBehindRadius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            window.getAttributes().setBlurBehindRadius(blurBehindRadius);
        }
    }
}
