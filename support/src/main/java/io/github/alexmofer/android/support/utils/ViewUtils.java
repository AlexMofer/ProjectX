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
import android.view.ViewGroup;

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

    public static void measureChild(View child, int maxWidth, int maxHeight,
                                    boolean withMargin) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (withMargin && lp instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams margin = (ViewGroup.MarginLayoutParams) lp;
            maxWidth = maxWidth - margin.leftMargin - margin.rightMargin;
            maxHeight = maxHeight - margin.topMargin - margin.bottomMargin;
        }
        final int childWidthMeasureSpec;
        if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth,
                    View.MeasureSpec.EXACTLY);
        } else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth,
                    View.MeasureSpec.AT_MOST);
        } else {
            childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(lp.width,
                    View.MeasureSpec.EXACTLY);
        }
        final int childHeightMeasureSpec;
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight,
                    View.MeasureSpec.EXACTLY);
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxHeight,
                    View.MeasureSpec.AT_MOST);
        } else {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(lp.height,
                    View.MeasureSpec.EXACTLY);
        }
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    public static int getMarginLeft(View child) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) lp).leftMargin;
        }
        return 0;
    }

    public static int getMarginTop(View child) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) lp).topMargin;
        }
        return 0;
    }

    public static int getMarginRight(View child) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) lp).rightMargin;
        }
        return 0;
    }

    public static int getMarginBottom(View child) {
        final ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) lp).bottomMargin;
        }
        return 0;
    }
}
