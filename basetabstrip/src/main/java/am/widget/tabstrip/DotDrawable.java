/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.tabstrip;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 圆点背景图
 */
@SuppressWarnings("unused")
public class DotDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mRoundRect = new RectF();
    private int mColor;
    private int mMinWidth;
    private int mMinHeight;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private ColorFilter mColorFilter;
    private PorterDuffColorFilter mTintFilter;
    private ColorStateList mTints;
    private PorterDuff.Mode mTintMode = PorterDuff.Mode.SRC_IN;

    public DotDrawable(int color, int minWidth, int minHeight,
                       int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        mColor = color;
        mMinWidth = minWidth;
        mMinHeight = minHeight;
        mPaddingLeft = paddingLeft;
        mPaddingTop = paddingTop;
        mPaddingRight = paddingRight;
        mPaddingBottom = paddingBottom;
        mPaint.setColor(mColor);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final ColorFilter filter = mColorFilter != null ? mColorFilter : mTintFilter;
        mPaint.setColorFilter(filter);
        final float radius = Math.min(mRoundRect.width(), mRoundRect.height()) * 0.5f;
        canvas.drawRoundRect(mRoundRect, radius, radius, mPaint);
        mPaint.setColorFilter(null);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getAlpha() {
        return mPaint.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public boolean isFilterBitmap() {
        return mPaint.isFilterBitmap();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public void setTintList(@Nullable ColorStateList tint) {
        mTints = tint;
        mTintFilter = getTintFilter(mTints, mTintMode, mColor);
        invalidateSelf();
    }

    @Override
    public void setTintMode(@NonNull PorterDuff.Mode tintMode) {
        mTintMode = tintMode;
        mTintFilter = getTintFilter(mTints, mTintMode, mColor);
        invalidateSelf();
    }

    @Nullable
    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mColorFilter = colorFilter;
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (mTints != null && mTints.isStateful()) {
            mTintFilter = getTintFilter(mTints, mTintMode, mColor);
            return true;
        }
        return false;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mRoundRect.set(bounds);
    }

    @Override
    public int getMinimumWidth() {
        return Math.max(mPaddingLeft + mPaddingRight, mMinWidth);
    }

    @Override
    public int getMinimumHeight() {
        return Math.max(mPaddingTop + mPaddingBottom, mMinHeight);
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        padding.set(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        return true;
    }

    /**
     * 获取颜色
     *
     * @return 颜色
     */
    public int getColor() {
        return mColor;
    }

    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        if (mColor == color)
            return;
        mColor = color;
        mPaint.setColor(mColor);
        mTintFilter = getTintFilter(mTints, mTintMode, mColor);
        invalidateSelf();
    }

    /**
     * 设置尺寸
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(int width, int height) {
        if (mMinWidth == width && mMinHeight == height)
            return;
        mMinWidth = width;
        mMinHeight = height;
        invalidateSelf();
    }

    /**
     * 设置间距
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setPadding(int left, int top, int right, int bottom) {
        if (mPaddingLeft == left && mPaddingTop == top &&
                mPaddingRight == right && mPaddingBottom == bottom)
            return;
        mPaddingLeft = left;
        mPaddingTop = top;
        mPaddingRight = right;
        mPaddingBottom = bottom;
        invalidateSelf();
    }

    @Nullable
    private PorterDuffColorFilter getTintFilter(@Nullable ColorStateList tint,
                                                @Nullable PorterDuff.Mode tintMode,
                                                int defaultColor) {
        return tint == null || tintMode == null ? null : new PorterDuffColorFilter(
                tint.getColorForState(getState(), defaultColor), tintMode);
    }
}
