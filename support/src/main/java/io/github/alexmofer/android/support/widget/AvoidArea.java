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
package io.github.alexmofer.android.support.widget;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.util.TypedValueCompat;
import androidx.core.view.DisplayCutoutCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

/**
 * 避让区域
 * Created by Alex on 2025/5/15.
 */
public class AvoidArea {
    public static final int LEFT = 1;
    public static final int TOP = 1 << 1;
    public static final int RIGHT = 1 << 2;
    public static final int BOTTOM = 1 << 3;
    public static final int HORIZONTAL = LEFT | RIGHT;
    public static final int VERTICAL = TOP | BOTTOM;
    public static final int ALL = HORIZONTAL | VERTICAL;

    private AvoidArea() {
        //no instance
    }

    /**
     * 以 Padding 形式处理系统避让区域
     *
     * @param view           View
     * @param edge           待处理的边
     * @param cutoutCritical 挖孔尺寸临界值，单位 DP，挖孔尺寸不超过临界值时仅采样 systemBars，挖孔尺寸超过临界值时采样 systemBars 与 displayCutout
     * @param consumed       是否阻止传递，建议不阻止，阻止传递在 Android 10（API 29） 前后表现不一致，需要使用 {@link androidx.core.view.ViewGroupCompat#installCompatInsetsDispatch(View)} 处理
     */
    public static void padding(View view, int edge, int cutoutCritical, boolean consumed) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            final DisplayCutoutCompat cutout = windowInsets.getDisplayCutout();
            boolean ignoreCutout = true;
            if (cutout != null) {
                final List<Rect> boundingRects = cutout.getBoundingRects();
                final DisplayMetrics metrics = v.getResources().getDisplayMetrics();
                for (Rect rect : boundingRects) {
                    final float width = TypedValueCompat.pxToDp(rect.width(), metrics);
                    final float height = TypedValueCompat.pxToDp(rect.height(), metrics);
                    if (width > cutoutCritical || height > cutoutCritical) {
                        ignoreCutout = false;
                        break;
                    }
                }
            }
            final int type = ignoreCutout ? WindowInsetsCompat.Type.systemBars() :
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout();
            final Insets insets = windowInsets.getInsets(type);
            final int left = (edge & LEFT) == LEFT ? insets.left : 0;
            final int top = (edge & TOP) == TOP ? insets.top : 0;
            final int right = (edge & RIGHT) == RIGHT ? insets.right : 0;
            final int bottom = (edge & BOTTOM) == BOTTOM ? insets.bottom : 0;
            v.setPadding(left, top, right, bottom);
            return consumed ? WindowInsetsCompat.CONSUMED : windowInsets;
        });
    }

    /**
     * 以 Padding 形式处理系统避让区域
     *
     * @param view View
     * @param edge 待处理的边
     */
    public static void padding(View view, int edge) {
        padding(view, edge, 50, false);
    }

    /**
     * 以 Padding 形式处理系统避让区域（忽略底部）
     *
     * @param view View
     */
    public static void paddingIgnoreBottom(View view) {
        padding(view, LEFT | TOP | RIGHT);
    }

    /**
     * 以 Padding 形式处理系统避让区域（忽略顶部）
     *
     * @param view View
     */
    public static void paddingIgnoreTop(View view) {
        padding(view, LEFT | RIGHT | BOTTOM);
    }

    /**
     * 以 Padding 形式处理系统避让区域
     *
     * @param view View
     */
    public static void paddingAll(View view) {
        padding(view, LEFT | TOP | RIGHT | BOTTOM);
    }
}
