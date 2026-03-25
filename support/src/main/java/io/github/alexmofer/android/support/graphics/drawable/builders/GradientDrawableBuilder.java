/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.graphics.drawable.builders;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;

/**
 * GradientDrawable 链式构建器
 * Created by Alex on 2026/3/25.
 */
public class GradientDrawableBuilder extends DrawableBuilder {
    private final GradientDrawable mDrawable;

    public GradientDrawableBuilder(@NonNull GradientDrawable drawable) {
        super(drawable);
        mDrawable = drawable;
    }

    public GradientDrawableBuilder() {
        this(new GradientDrawable());
    }

    public GradientDrawableBuilder(GradientDrawable.Orientation orientation, @ColorInt int[] colors) {
        this(new GradientDrawable(orientation, colors));
    }

    @NonNull
    @Override
    public GradientDrawable build() {
        return mDrawable;
    }

    public GradientDrawableBuilder setCornerRadii(@Nullable float[] radii) {
        mDrawable.setCornerRadii(radii);
        return this;
    }

    public GradientDrawableBuilder setCornerRadius(float radius) {
        mDrawable.setCornerRadius(radius);
        return this;
    }

    public GradientDrawableBuilder setStroke(int width, @ColorInt int color) {
        mDrawable.setStroke(width, color);
        return this;
    }

    public GradientDrawableBuilder setStroke(int width, ColorStateList colorStateList) {
        mDrawable.setStroke(width, colorStateList);
        return this;
    }

    public GradientDrawableBuilder setStroke(int width, @ColorInt int color, float dashWidth, float dashGap) {
        mDrawable.setStroke(width, color, dashWidth, dashGap);
        return this;
    }

    public GradientDrawableBuilder setStroke(
            int width, ColorStateList colorStateList, float dashWidth, float dashGap) {
        mDrawable.setStroke(width, colorStateList, dashWidth, dashGap);
        return this;
    }

    public GradientDrawableBuilder setSize(int width, int height) {
        mDrawable.setSize(width, height);
        return this;
    }

    public GradientDrawableBuilder setShape(int shape) {
        mDrawable.setShape(shape);
        return this;
    }

    public GradientDrawableBuilder setGradientType(int gradient) {
        mDrawable.setGradientType(gradient);
        return this;
    }

    public GradientDrawableBuilder setGradientCenter(float x, float y) {
        mDrawable.setGradientCenter(x, y);
        return this;
    }

    public GradientDrawableBuilder setGradientRadius(float gradientRadius) {
        mDrawable.setGradientRadius(gradientRadius);
        return this;
    }

    public GradientDrawableBuilder setUseLevel(boolean useLevel) {
        mDrawable.setUseLevel(useLevel);
        return this;
    }

    public GradientDrawableBuilder setOrientation(GradientDrawable.Orientation orientation) {
        mDrawable.setOrientation(orientation);
        return this;
    }

    public GradientDrawableBuilder setColors(@Nullable @ColorInt int[] colors) {
        mDrawable.setColors(colors);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public GradientDrawableBuilder setColors(@Nullable @ColorInt int[] colors, @Nullable float[] offsets) {
        mDrawable.setColors(colors, offsets);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public GradientDrawableBuilder setInnerRadiusRatio(
            @FloatRange(from = 0.0f, fromInclusive = false) float innerRadiusRatio) {
        mDrawable.setInnerRadiusRatio(innerRadiusRatio);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public GradientDrawableBuilder setInnerRadius(@Px int innerRadius) {
        mDrawable.setInnerRadius(innerRadius);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public GradientDrawableBuilder setThicknessRatio(
            @FloatRange(from = 0.0f, fromInclusive = false) float thicknessRatio) {
        mDrawable.setThicknessRatio(thicknessRatio);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public GradientDrawableBuilder setThickness(@Px int thickness) {
        mDrawable.setThickness(thickness);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public GradientDrawableBuilder setPadding(@Px int left, @Px int top, @Px int right, @Px int bottom) {
        mDrawable.setPadding(left, top, right, bottom);
        return this;
    }

    public GradientDrawableBuilder setColor(@ColorInt int argb) {
        mDrawable.setColor(argb);
        return this;
    }

    public GradientDrawableBuilder setColor(@Nullable ColorStateList colorStateList) {
        mDrawable.setColor(colorStateList);
        return this;
    }
}
