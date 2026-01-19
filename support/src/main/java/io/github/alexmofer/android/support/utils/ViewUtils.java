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

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.WindowInsetsCompat;

import io.github.alexmofer.android.support.function.FunctionRIntPIntInt;

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

    /**
     * 获取子 View 相对于父 View 的边框
     *
     * @param parent 父 View 或者父父 View
     * @param child  子 View 或者子子 View
     * @param bounds 边框
     * @return 获取成功时返回 true，无父子关系时返回 false
     */
    public static boolean getChildBounds(@NonNull View parent, @NonNull View child,
                                         @NonNull Rect bounds) {
        int x = child.getLeft();
        int y = child.getTop();
        ViewParent p = child.getParent();
        while (true) {
            if (p == parent) {
                bounds.set(x, y, x + child.getWidth(), y + child.getHeight());
                return true;
            }
            if (p instanceof View) {
                final View container = (View) p;
                x += container.getLeft();
                y += container.getTop();
                p = container.getParent();
                continue;
            }
            return false;
        }
    }

    /**
     * 计算尺寸
     *
     * @param size        需要的尺寸
     * @param measureSpec 给出的测量尺寸
     * @param calculator  AT_MOST 模式下的计算方式，传空时使用二者的小值
     * @return 布局尺寸
     */
    public static int getSize(int size, int measureSpec, @Nullable FunctionRIntPIntInt calculator) {
        final int mode = View.MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case View.MeasureSpec.AT_MOST:
                return calculator == null ?
                        Math.min(size, View.MeasureSpec.getSize(measureSpec)) :
                        calculator.execute(size, View.MeasureSpec.getSize(measureSpec));
            case View.MeasureSpec.EXACTLY:
                return View.MeasureSpec.getSize(measureSpec);
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return size;
        }
    }

    /**
     * 计算尺寸
     *
     * @param size        需要的尺寸
     * @param measureSpec 给出的测量尺寸
     * @return 布局尺寸
     */
    public static int getSize(int size, int measureSpec) {
        return getSize(size, measureSpec, null);
    }
}
