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
package io.github.alexmofer.android.support.utils;

import android.os.Build;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.WindowInsetsCompat;

/**
 * View 工具
 * Created by Alex on 2022/12/13.
 */
public class ViewUtils {

    private ViewUtils() {
        //no instance
    }

    /**
     * 判断状态栏是否显示
     *
     * @param view View
     * @return 状态栏显示时返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isStatusBarsVisible(View view) {
        return (view.getWindowSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN)
                != View.SYSTEM_UI_FLAG_FULLSCREEN;
    }

    /**
     * 判断状态栏是否显示
     *
     * @param view   View
     * @param insets 窗口裁剪
     * @return 状态栏显示时返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isStatusBarsVisible(View view,
                                              @Nullable WindowInsetsCompat insets) {
        if (insets == null) {
            return isStatusBarsVisible(view);
        } else {
            boolean visible = insets.isVisible(WindowInsetsCompat.Type.statusBars());
            if (visible && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // 部分机型再进一步检查
                if ((view.getWindowSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN)
                        == View.SYSTEM_UI_FLAG_FULLSCREEN) {
                    visible = false;
                }
            }
            return visible;
        }
    }
}
