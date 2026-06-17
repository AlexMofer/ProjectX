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
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 内阴影背景（支持圆角）
 * Created by Alex on 2026/6/8.
 */
public final class InnerShadowBackground extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final int mBackgroundColor;
    private final float mBackgroundRoundRectRadius;
    private final float mBlurredRadius;
    private final float mDX;
    private final float mDY;
    private final int mShadowColor;

    public InnerShadowBackground(@ColorInt int backgroundColor, float backgroundRoundRectRadius,
                                 float blurredRadius, float dx, float dy,
                                 @ColorInt int shadowColor) {
        mBackgroundColor = backgroundColor;
        mBackgroundRoundRectRadius = backgroundRoundRectRadius;
        mBlurredRadius = blurredRadius;
        mDX = dx;
        mDY = dy;
        mShadowColor = shadowColor;
        mPaint.setColor(Color.TRANSPARENT);
        mPath.setFillType(Path.FillType.EVEN_ODD);
    }

    public InnerShadowBackground(@ColorInt int backgroundColor, float backgroundRoundRectRadius,
                                 float blurredRadius, @ColorInt int shadowColor) {
        this(backgroundColor, backgroundRoundRectRadius, blurredRadius, 0, 0, shadowColor);
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        mPath.reset();
        final int expand = 100;
        mPath.addRect(bounds.left - expand, bounds.top - expand,
                bounds.right + expand, bounds.bottom + expand, Path.Direction.CW);
        if (mBackgroundRoundRectRadius > 0) {
            mPath.addRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom,
                    mBackgroundRoundRectRadius, mBackgroundRoundRectRadius, Path.Direction.CW);
        } else {
            mPath.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mPaint.setColor(mBackgroundColor);
        mPaint.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        final Rect bounds = getBounds();
        if (mBackgroundRoundRectRadius > 0) {
            canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom,
                    mBackgroundRoundRectRadius, mBackgroundRoundRectRadius, mPaint);
        } else {
            canvas.drawRect(bounds.left, bounds.top, bounds.right, bounds.bottom, mPaint);
        }
        mPaint.setShadowLayer(mBlurredRadius, mDX, mDY, mShadowColor);
        canvas.drawPath(mPath, mPaint);
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

    @Override
    public void getOutline(@NonNull Outline outline) {
        if (mBackgroundRoundRectRadius > 0) {
            outline.setRoundRect(getBounds(), mBackgroundRoundRectRadius);
        } else {
            outline.setRect(getBounds());
        }
        outline.setAlpha(1);
    }
}
