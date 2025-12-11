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
package io.github.alexmofer.android.support.window;

import android.app.Activity;

import androidx.annotation.BoolRes;
import androidx.window.layout.WindowMetricsCalculator;

import io.github.alexmofer.android.support.R;
import io.github.alexmofer.android.support.app.ApplicationHolder;

/**
 * 窗口尺寸辅助
 * 较小的宽度	小于 600dp	99.96% 的手机处于竖屏模式
 * 中等宽度	600dp+	93.73% 的平板电脑处于竖屏模式，展开的大型内部显示屏处于竖屏模式
 * 展开宽度	840dp+	97.22% 的平板电脑处于横屏模式，展开的大型内部显示屏处于横屏模式
 * 较小的高度	小于 480dp	99.78% 的手机处于横屏模式
 * 中等高度	480dp+	96.56% 的平板电脑处于横屏模式，97.59% 的手机处于竖屏模式
 * 展开高度	900dp+	94.25% 的平板电脑处于竖屏模式
 * Created by Alex on 2023/6/29.
 */
public class WindowSizeHelper {

    public static final int LEVEL_MIN = 0;// 低于320dp的等级
    public static final int LEVEL_320 = 1;// 不低于320dp的等级
    public static final int LEVEL_480 = 2;// 不低于480dp的等级
    public static final int LEVEL_600 = 3;// 不低于600dp的等级
    public static final int LEVEL_720 = 4;// 不低于720dp的等级
    public static final int LEVEL_840 = 5;// 不低于840dp的等级
    public static final int LEVEL_900 = 6;// 不低于900dp的等级

    private WindowSizeHelper() {
        //no instance
    }

    /**
     * 获取Activity窗口宽度
     *
     * @param activity Activity
     * @return Activity窗口宽度
     */
    public static int getWidth(Activity activity) {
        return WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity).getBounds().width();
    }

    /**
     * 获取Activity窗口高度
     *
     * @param activity Activity
     * @return Activity窗口高度
     */
    public static int getHeight(Activity activity) {
        return WindowMetricsCalculator.getOrCreate()
                .computeCurrentWindowMetrics(activity).getBounds().height();
    }

    private static boolean getBoolean(@BoolRes int id) {
        return ApplicationHolder.getApplicationContext().getResources().getBoolean(id);
    }

    /**
     * 判断是否窗口可用宽度在320dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isW320() {
        return getBoolean(R.bool.w320);
    }

    /**
     * 判断是否窗口可用宽度在480dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isW480() {
        return getBoolean(R.bool.w480);
    }

    /**
     * 判断是否窗口可用宽度在600dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isW600() {
        return getBoolean(R.bool.w600);
    }

    /**
     * 判断是否窗口可用宽度在720dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isW720() {
        return getBoolean(R.bool.w720);
    }

    /**
     * 判断是否窗口可用宽度在840dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isW840() {
        return getBoolean(R.bool.w840);
    }

    /**
     * 判断是否窗口可用宽度在900dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isW900() {
        return getBoolean(R.bool.w900);
    }

    /**
     * 获取窗口可用宽度等级
     *
     * @return 可用宽度等级
     */
    public static int getWLevel() {
        if (isW900()) {
            return LEVEL_900;
        }
        if (isW840()) {
            return LEVEL_840;
        }
        if (isW720()) {
            return LEVEL_720;
        }
        if (isW600()) {
            return LEVEL_600;
        }
        if (isW480()) {
            return LEVEL_480;
        }
        if (isW320()) {
            return LEVEL_320;
        }
        return LEVEL_MIN;
    }

    /**
     * 判断是否窗口可用高度在320dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isH320() {
        return getBoolean(R.bool.h320);
    }

    /**
     * 判断是否窗口可用高度在480dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isH480() {
        return getBoolean(R.bool.h480);
    }

    /**
     * 判断是否窗口可用高度在600dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isH600() {
        return getBoolean(R.bool.h600);
    }

    /**
     * 判断是否窗口可用高度在720dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isH720() {
        return getBoolean(R.bool.h720);
    }

    /**
     * 判断是否窗口可用高度在840dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isH840() {
        return getBoolean(R.bool.h840);
    }

    /**
     * 判断是否窗口可用高度在900dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isH900() {
        return getBoolean(R.bool.h900);
    }

    /**
     * 获取窗口可用高度等级
     *
     * @return 可用高度等级
     */
    public static int getHLevel() {
        if (isH900()) {
            return LEVEL_900;
        }
        if (isH840()) {
            return LEVEL_840;
        }
        if (isH720()) {
            return LEVEL_720;
        }
        if (isH600()) {
            return LEVEL_600;
        }
        if (isH480()) {
            return LEVEL_480;
        }
        if (isH320()) {
            return LEVEL_320;
        }
        return LEVEL_MIN;
    }

    /**
     * 判断是否窗口短边宽度在320dp及以上
     * 不同平台有所差异，有些返回的是完整窗口，有些返回的是窗口可用
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isSW320() {
        return getBoolean(R.bool.sw320);
    }

    /**
     * 判断是否窗口短边宽度在480dp及以上
     * 不同平台有所差异，有些返回的是完整窗口，有些返回的是窗口可用
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isSW480() {
        return getBoolean(R.bool.sw480);
    }

    /**
     * 判断是否窗口短边宽度在600dp及以上
     * 不同平台有所差异，有些返回的是完整窗口，有些返回的是窗口可用
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isSW600() {
        return getBoolean(R.bool.sw600);
    }

    /**
     * 判断是否窗口短边宽度在720dp及以上
     * 不同平台有所差异，有些返回的是完整窗口，有些返回的是窗口可用
     *
     * @return 不低于限定值时返回true
     */
    public static boolean isSW720() {
        return getBoolean(R.bool.sw720);
    }

    /**
     * 获取窗口短边等级
     *
     * @return 窗口短边等级
     */
    public static int getSWLevel() {
        if (isSW720()) {
            return LEVEL_720;
        }
        if (isSW600()) {
            return LEVEL_600;
        }
        if (isSW480()) {
            return LEVEL_480;
        }
        if (isSW320()) {
            return LEVEL_320;
        }
        return LEVEL_MIN;
    }
}
