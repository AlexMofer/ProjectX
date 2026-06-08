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
package io.github.alexmofer.android.support.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 圆形投影背景
 * Created by Alex on 2026/6/8.
 */
public final class CircleDropShadowBackground extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float mPercentage;
    private final float mBlurredRadius;
    private final float mDX;
    private final float mDY;
    private final int mShadowColor;

    public CircleDropShadowBackground(float percentage,
                                      float blurredRadius, float dx, float dy,
                                      @ColorInt int shadowColor) {
        mPercentage = percentage;
        mBlurredRadius = blurredRadius;
        mDX = dx;
        mDY = dy;
        mShadowColor = shadowColor;
        mPaint.setColor(Color.TRANSPARENT);
    }

    public CircleDropShadowBackground(float blurredRadius, float dx, float dy,
                                      @ColorInt int shadowColor) {
        this(1f, blurredRadius, dx, dy, shadowColor);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Rect bounds = getBounds();
        mPaint.setShadowLayer(mBlurredRadius, mDX, mDY, mShadowColor);
        canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                Math.min(bounds.width(), bounds.height()) * 0.5f * mPercentage, mPaint);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }
}
