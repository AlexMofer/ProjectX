package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

/**
 * 颜色构建器
 * Created by Alex on 2025/7/29.
 */
public final class ColorBuilder {

    private ColorBuilder() {
        //no instance
    }

    public static ColorStateList newSelectState(@ColorInt int unselect,
                                                @ColorInt int selected) {
        return new ColorStateList(new int[][]{new int[]{android.R.attr.state_selected}, new int[0]},
                new int[]{selected, unselect});
    }

    public static ColorStateList newSelectState(Context context,
                                                @ColorRes int unselect,
                                                @ColorRes int selected) {
        return newSelectState(context.getColor(unselect), context.getColor(selected));
    }

    public static ColorStateList newActivatedState(@ColorInt int normal, @ColorInt int activated) {
        return new ColorStateList(new int[][]{new int[]{android.R.attr.state_activated}, new int[0]},
                new int[]{activated, normal});
    }

    public static ColorStateList newActivatedState(Context context,
                                                   @ColorRes int normal,
                                                   @ColorRes int activated) {
        return newActivatedState(context.getColor(normal), context.getColor(activated));
    }

    /**
     * 透明度叠加
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
}
