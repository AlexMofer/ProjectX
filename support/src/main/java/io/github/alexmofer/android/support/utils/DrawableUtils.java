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

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Drawable 工具
 * Created by Alex on 2024/1/18.
 */
public final class DrawableUtils {

    private DrawableUtils() {
        //no instance
    }

    /**
     * 新建分割器
     *
     * @param color  颜色
     * @param width  宽度，-1表示占满
     * @param height 高度，-1表示占满
     * @return 分割器
     */
    public static GradientDrawable newDivider(@ColorInt int color, int width, int height) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setSize(width, height);
        return drawable;
    }

    /**
     * 新建垂直分割器
     *
     * @param color  颜色
     * @param height 高度
     * @return 垂直分割器
     */
    public static GradientDrawable newDividerVertical(@ColorInt int color, int height) {
        return newDivider(color, -1, height);
    }

    /**
     * 新建透明垂直分割器
     *
     * @param height 高度
     * @return 垂直分割器
     */
    public static GradientDrawable newDividerVertical(int height) {
        return newDividerVertical(Color.TRANSPARENT, height);
    }

    /**
     * 新建水平分割器
     *
     * @param color 颜色
     * @param width 宽度
     * @return 水平分割器
     */
    public static GradientDrawable newDividerHorizontal(@ColorInt int color, int width) {
        return newDivider(color, width, -1);
    }

    /**
     * 新建透明水平分割器
     *
     * @param width 宽度
     * @return 水平分割器
     */
    public static GradientDrawable newDividerHorizontal(int width) {
        return newDividerHorizontal(Color.TRANSPARENT, width);
    }

    /**
     * 新建水平渐变
     *
     * @param colorStart 起始颜色
     * @param colorEnd   结束颜色
     * @return 水平渐变
     */
    public static GradientDrawable newGradientHorizontal(@ColorInt int colorStart,
                                                         @ColorInt int colorEnd) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{colorStart, colorEnd});
        drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        return drawable;
    }

    /**
     * 新建垂直渐变
     *
     * @param colorStart 起始颜色
     * @param colorEnd   结束颜色
     * @return 垂直渐变
     */
    public static GradientDrawable newGradientVertical(@ColorInt int colorStart,
                                                       @ColorInt int colorEnd) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{colorStart, colorEnd});
        return drawable;
    }

    public static GradientDrawable newEmpty(int width, int height) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(width, height);
        return drawable;
    }

    @NonNull
    public static Drawable setTintList(@NonNull Drawable drawable, @Nullable ColorStateList tint) {
        final Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
        wrapped.setTintList(tint);
        return wrapped;
    }

    @NonNull
    public static Drawable setTint(@NonNull Drawable drawable, @ColorInt int tintColor) {
        final Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
        wrapped.setTint(tintColor);
        return wrapped;
    }

    @Nullable
    public static Drawable getTintedDrawable(Context context, @DrawableRes int id,
                                             @Nullable ColorStateList tint) {
        final Drawable drawable = AppCompatResources.getDrawable(context, id);
        if (drawable == null) {
            return null;
        }
        return setTintList(drawable, tint);
    }

    @Nullable
    public static Drawable getTintedDrawable(Context context, @DrawableRes int id,
                                             @ColorInt int color) {
        return getTintedDrawable(context, id, ColorStateList.valueOf(color));
    }

    @NonNull
    public static GradientDrawable newGradientDrawable(@ColorInt int fillColor,
                                                       float cornerRadius) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    @NonNull
    public static GradientDrawable newGradientDrawableCapsule(@ColorInt int fillColor) {
        return newGradientDrawable(fillColor, ViewUtils.MAX_SIZE);
    }

    @NonNull
    public static GradientDrawable newGradientDrawable(@ColorInt int fillColor,
                                                       int strokeWidth,
                                                       @ColorInt int strokeColor,
                                                       float cornerRadius) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setStroke(strokeWidth, strokeColor);
        drawable.setCornerRadius(cornerRadius);
        return drawable;
    }

    @NonNull
    public static GradientDrawable newGradientDrawableCapsule(@ColorInt int fillColor,
                                                              int strokeWidth,
                                                              @ColorInt int strokeColor) {
        return newGradientDrawable(fillColor, strokeWidth, strokeColor, ViewUtils.MAX_SIZE);
    }
}
