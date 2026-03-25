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
import android.graphics.BlendMode;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import io.github.alexmofer.android.support.function.FunctionPObject;

/**
 * Drawable 链式构建器
 * Created by Alex on 2026/3/25.
 */
public class DrawableBuilder {
    private final Drawable mDrawable;

    public DrawableBuilder(@NonNull Drawable drawable) {
        mDrawable = drawable;
    }

    @NonNull
    public Drawable build() {
        return mDrawable;
    }

    @NonNull
    public final <T extends Drawable> T buildCast() {
        //noinspection unchecked
        return (T) build();
    }

    /**
     * 强制转换
     */
    @NonNull
    public final <T extends DrawableBuilder> T cast() {
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 挂钩子
     *
     * @param function 方法
     * @return 自身
     */
    public final <T extends DrawableBuilder> T hook(@Nullable FunctionPObject<T> function) {
        if (function != null) {
            //noinspection unchecked
            function.execute((T) this);
        }
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 修改未定义属性
     *
     * @param function 方法
     * @return 自身
     */
    public final <T extends Drawable> DrawableBuilder changeAttribute(@Nullable FunctionPObject<T> function) {
        if (function != null) {
            //noinspection unchecked
            function.execute((T) mDrawable);
        }
        return this;
    }

    public DrawableBuilder setFilterBitmap(boolean filter) {
        mDrawable.setFilterBitmap(filter);
        return this;
    }

    public DrawableBuilder setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mDrawable.setAlpha(alpha);
        return this;
    }

    public DrawableBuilder setColorFilter(@Nullable ColorFilter colorFilter) {
        mDrawable.setColorFilter(colorFilter);
        return this;
    }

    public DrawableBuilder setTint(@ColorInt int tintColor) {
        mDrawable.setTint(tintColor);
        return this;
    }

    public DrawableBuilder setTintList(@Nullable ColorStateList tint) {
        mDrawable.setTintList(tint);
        return this;
    }

    public DrawableBuilder setTintMode(@Nullable PorterDuff.Mode tintMode) {
        mDrawable.setTintMode(tintMode);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public DrawableBuilder setTintBlendMode(@Nullable BlendMode blendMode) {
        mDrawable.setTintBlendMode(blendMode);
        return this;
    }

    public DrawableBuilder setAutoMirrored(boolean mirrored) {
        mDrawable.setAutoMirrored(mirrored);
        return this;
    }
}