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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Drawable 工具
 * Created by Alex on 2024/1/18.
 */
public class DrawableUtils {

    private DrawableUtils() {
        //no instance
    }

    /**
     * Obtains styled attributes from the theme, if available, or unstyled
     * resources if the theme is null.
     */
    @NonNull
    public static TypedArray obtainAttributes(@NonNull Resources res,
                                              @Nullable Resources.Theme theme,
                                              @NonNull AttributeSet set,
                                              @NonNull int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    /**
     * 应用透明
     *
     * @param color 颜色
     * @param alpha 透明度
     * @return 应用透明后的颜色
     */
    public static int applyAlpha(@ColorInt int color, @IntRange(from = 0, to = 255) int alpha) {
        if (alpha == 255) {
            return color;
        }
        final int r = Color.red(color);
        final int b = Color.blue(color);
        final int g = Color.green(color);
        if (alpha == 0) {
            return Color.argb(0, r, b, g);
        }
        final int a = Color.alpha(color);
        if (a == 255) {
            return Color.argb(alpha, r, b, g);
        }
        if (a == 0) {
            return Color.argb(0, r, b, g);
        }
        return Color.argb(Math.max(0, Math.min(255,
                Math.round((a / 255f) * (alpha / 255f) * 255))), r, b, g);
    }
}
