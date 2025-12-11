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
package io.github.alexmofer.android.support.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Drawable
 * Created by Alex on 2024/3/10.
 */
public class Drawable extends android.graphics.drawable.Drawable {

    private ColorFilter mColorFilter;
    private int mAlpha = 255;
    private int mOpacity;

    public Drawable() {
        this(PixelFormat.TRANSLUCENT);
    }

    protected Drawable(int opacity) {
        mOpacity = opacity;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        draw(canvas, mAlpha, mColorFilter);
    }

    protected void draw(@NonNull Canvas canvas, int alpha, @Nullable ColorFilter colorFilter) {
    }

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha == alpha) {
            return;
        }
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (mColorFilter == colorFilter) {
            return;
        }
        mColorFilter = colorFilter;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mOpacity;
    }

    /**
     * 设置透明度（API29 开始无意义，29以前的版本有效）
     *
     * @param opacity 透明度
     */
    protected void setOpacity(int opacity) {
        mOpacity = opacity;
    }
}
