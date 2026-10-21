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
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

/**
 * 阴影 Drawable
 * Created by Alex on 2026/10/20.
 */
public class ShadowDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final Paint mClearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    @Nullable
    private final BiConsumer<Rect, Path> mPathRewinder;
    private float mOver = 1;

    public ShadowDrawable(@Nullable BiConsumer<Rect, Path> pathRewinder) {
        mPathRewinder = pathRewinder;
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public ShadowDrawable() {
        this(null);
    }

    public ShadowDrawable(@Nullable BiConsumer<Rect, Path> pathRewinder,
                          float radius, float dx, float dy, @ColorInt int shadowColor) {
        this(pathRewinder);
        setShadow(radius, dx, dy, shadowColor);
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        onRewindPath(bounds, mPath);
    }

    protected void onRewindPath(@NonNull Rect bounds, @NonNull Path path) {
        if (mPathRewinder != null) {
            mPathRewinder.accept(bounds, path);
        } else {
            path.rewind();
            path.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom, Path.Direction.CW);
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Rect bounds = getBounds();
        final int saveCount = canvas.saveLayer(bounds.left - mOver, bounds.top - mOver,
                bounds.right + mOver, bounds.bottom + mOver, null);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mPath, mClearPaint);
        canvas.restoreToCount(saveCount);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(mPath);
        } else {
            //noinspection deprecation
            outline.setConvexPath(mPath);
        }
        outline.setAlpha(1);
    }

    public void setShadow(float radius, float dx, float dy, @ColorInt int shadowColor) {
        mOver = Math.max(radius + Math.abs(dx), radius + Math.abs(dy)) * 2;
        mPaint.setShadowLayer(radius, dx, dy, shadowColor);
    }
}
