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

import androidx.annotation.ColorInt;

/**
 * 颜色工具
 * Created by Alex on 2024/1/12.
 */
public class ColorUtils {

    private ColorUtils() {
        //no instance
    }

    /**
     * 获取颜色
     *
     * @param color 颜色
     * @param alpha 透明度
     * @return 颜色
     */
    @ColorInt
    public static int getColor(@ColorInt int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * 获取颜色
     *
     * @param color   颜色
     * @param opacity 不透明度
     * @return 颜色
     */
    @ColorInt
    public static int getColor(@ColorInt int color, float opacity) {
        return getColor(color, Math.max(0, Math.min(255, Math.round(255 * opacity))));
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

    /**
     * 颜色叠加透明度
     *
     * @param color 颜色
     * @param alpha 透明度
     * @return 叠加后的颜色
     */
    public static int applyAlpha(int color, int alpha) {
        if (alpha == 255) {
            return color;
        }
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);
        if (alpha == 0) {
            return Color.argb(0, r, g, b);
        }
        return Color.argb(Math.min(255, Math.max(0, Math.round(
                1f * Color.alpha(color) / 255 * (1f * alpha / 255)))), r, g, b);
    }

    /**
     * 颜色叠加
     *
     * @param background 背景色，会忽略透明度
     * @param foreground 前景色
     * @return 叠加后的颜色
     */
    public static int add(int background, int foreground) {
        final float a = Color.alpha(foreground) * 1f / 255;
        final int r = Math.max(0, Math.min(255, Math.round(
                (Color.red(background) * (1 - a)) + (Color.red(foreground) * a))));
        final int g = Math.max(0, Math.min(255, Math.round(
                (Color.green(background) * (1 - a)) + (Color.green(foreground) * a))));
        final int b = Math.max(0, Math.min(255, Math.round(
                (Color.blue(background) * (1 - a)) + (Color.blue(foreground) * a))));
        return Color.argb(255, r, g, b);
    }

    /**
     * 判断颜色是否相等
     *
     * @param color1 颜色1
     * @param color2 颜色2
     * @param strict 是否为严格模式
     * @return 两个颜色相等时返回true
     */
    public static boolean equals(int color1, int color2, boolean strict) {
        if (strict) {
            return color1 == color2;
        } else {
            // r、g、b差值低于4
            return Math.abs(Color.red(color1) - Color.red(color2)) <= 3
                    && Math.abs(Color.green(color1) - Color.green(color2)) <= 3
                    && Math.abs(Color.blue(color1) - Color.blue(color2)) <= 3;
        }
    }

    /**
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startColor A 32-bit int value representing colors in the
     *                   separate bytes of the parameter
     * @param endColor   A 32-bit int value representing colors in the
     *                   separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    public static int animate(float fraction, int startColor, int endColor) {
        float startA = ((startColor >> 24) & 0xff) / 255.0f;
        float startR = ((startColor >> 16) & 0xff) / 255.0f;
        float startG = ((startColor >> 8) & 0xff) / 255.0f;
        float startB = (startColor & 0xff) / 255.0f;

        float endA = ((endColor >> 24) & 0xff) / 255.0f;
        float endR = ((endColor >> 16) & 0xff) / 255.0f;
        float endG = ((endColor >> 8) & 0xff) / 255.0f;
        float endB = (endColor & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }
}
