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
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

/**
 * 路径裁剪 Drawable
 * Created by Alex on 2026/10/20.
 */
public class CropDrawable extends DrawableWrapper {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private final Path mClipPath = new Path();
    @Nullable
    private final BiConsumer<Rect, Path> mPathRewinder;
    private final Path.Direction mOuterDirection;

    public CropDrawable(@Nullable Drawable dr,
                        @Nullable BiConsumer<Rect, Path> pathRewinder,
                        @NonNull Path.Direction outerDirection) {
        super(dr);
        mPathRewinder = pathRewinder;
        mOuterDirection = outerDirection;
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mClipPath.setFillType(Path.FillType.EVEN_ODD);
    }

    public CropDrawable(@Nullable Drawable dr,
                        @Nullable BiConsumer<Rect, Path> pathRewinder) {
        this(dr, pathRewinder, Path.Direction.CCW);
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        onRewindPath(bounds, mPath);
        mClipPath.rewind();
        mClipPath.addPath(mPath);
        final int over = 10;
        mClipPath.addRect(bounds.left - over, bounds.top - over,
                bounds.right + over, bounds.bottom + over, mOuterDirection);
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
        final int saveCount = canvas.saveLayer(
                bounds.left, bounds.top, bounds.right, bounds.bottom, null);
        super.draw(canvas);
        canvas.drawPath(mClipPath, mPaint);
        canvas.restoreToCount(saveCount);
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
}
