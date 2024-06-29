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
package io.github.alexmofer.android.support.utils;

import android.graphics.Color;

/**
 * 颜色工具
 * Created by Alex on 2024/1/12.
 */
public class ColorUtils {

    private ColorUtils() {
        //no instance
    }

    /**
     * 调整颜色饱和度
     *
     * @param color         颜色
     * @param maxSaturation 最大饱和度
     * @return 调整的颜色
     */
    public static int changeSaturation(int color, float maxSaturation) {
        if (maxSaturation < 0 || maxSaturation >= 1) {
            return color;
        }
        final float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        boolean changed = false;
        if (hsv[1] > maxSaturation) {
            changed = true;
            hsv[1] = maxSaturation;
        }
        return changed ? Color.HSVToColor(hsv) : color;
    }

    /**
     * 判断该颜色是否建议使用夜间模式
     *
     * @param color 颜色
     * @return 推荐使用夜间模式时返回true
     */
    public static boolean isSuggestNightMode(int color) {
        // 转为YUV模式，Y（灰阶）小于192
        final float gray = Color.red(color) * 0.299f +
                Color.green(color) * 0.587f + Color.blue(color) * 0.114f;
        return gray < 192;
    }
}
