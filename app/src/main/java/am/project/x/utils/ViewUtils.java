/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.utils;

import android.os.Build;
import android.view.View;

/**
 * View工具类
 */
public class ViewUtils {

    private ViewUtils() {
        //no instance
    }

    /**
     * 设置布局可绘制在状态栏下
     *
     * @param navigation 是否包含导航栏
     */
    public static void setLayoutFullscreen(View view, boolean navigation) {
        if (view == null)
            return;
        if (Build.VERSION.SDK_INT >= 16) {
            int visibility = view.getSystemUiVisibility();
            visibility = visibility | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (navigation) {
                visibility = visibility | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            }
            view.setSystemUiVisibility(visibility);
        }
    }
}
