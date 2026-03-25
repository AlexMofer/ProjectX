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

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat;

/**
 * DrawableWrapperCompat 链式构建器
 * Created by Alex on 2026/3/25.
 */
public class DrawableWrapperBuilder extends DrawableBuilder {
    private final DrawableWrapperCompat mDrawable;

    public DrawableWrapperBuilder(@NonNull DrawableWrapperCompat drawable) {
        super(drawable);
        mDrawable = drawable;
    }

    public DrawableWrapperBuilder(@Nullable Drawable drawable) {
        this(new DrawableWrapperCompat(drawable));
    }

    @NonNull
    @Override
    public DrawableWrapperCompat build() {
        return mDrawable;
    }

    public DrawableWrapperBuilder setDrawable(@Nullable Drawable drawable) {
        mDrawable.setDrawable(drawable);
        return this;
    }
}
