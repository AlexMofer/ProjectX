/*
 * Copyright (C) 2015 AlexMofer
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

package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * 中心Drawable
 * 一般用于ImageView的src，保证缩放后，中心的Drawable不变形。
 * 用于一般background属性的话，无需使用本控件，直接使用layer-list来定义即可。
 */
@SuppressWarnings("unused")
public class CenterDrawable extends Drawable {

    public static final int SHAPE_RECTANGLE = 0;//矩形
    public static final int SHAPE_ROUNDED_RECTANGLE = 1;// 圆角矩形
    public static final int SHAPE_OVAL = 2;// 圆
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
    private final RectF mRectF = new RectF();// 绘制区域
    private Drawable mDrawableCenter;//中心Drawable
    private int mMinWidth;// 最小宽度
    private int mMinHeight;//最小高度
    private int mShape;// 形状
    private boolean mEquilateral;// 绘制的形状是否等边
    private float mRadius = 0;// 圆角半径

    public CenterDrawable(Drawable center) {
        this(center, 0x00000000);
    }

    public CenterDrawable(Drawable center, int color) {
        this(center, color, false);
    }

    public CenterDrawable(Drawable center, int color, boolean equilateral) {
        this(center, color, SHAPE_RECTANGLE, equilateral);
    }

    public CenterDrawable(Drawable center, int color, int shape, boolean equilateral) {
        this(center, color, 0, 0, shape, equilateral);
    }

    public CenterDrawable(Drawable center, int color, int minWidth, int minHeight, int shape,
                          boolean equilateral) {
        mDrawableCenter = center;
        mPaint.setColor(color);
        mMinWidth = minWidth;
        mMinHeight = minHeight;
        if (shape == SHAPE_RECTANGLE || shape == SHAPE_ROUNDED_RECTANGLE || shape == SHAPE_OVAL)
            mShape = shape;
        else
            mShape = SHAPE_RECTANGLE;
        mEquilateral = equilateral;
    }


    @Override
    public int getMinimumWidth() {
        return mDrawableCenter == null ? mMinWidth :
                Math.max(mDrawableCenter.getIntrinsicWidth(), mMinWidth);
    }

    @Override
    public int getMinimumHeight() {
        return mDrawableCenter == null ? mMinHeight :
                Math.max(mDrawableCenter.getIntrinsicHeight(), mMinHeight);
    }

    @Override
    public int getIntrinsicWidth() {
        return mMinWidth == 0 ? super.getIntrinsicWidth() : mMinWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mMinHeight == 0 ? super.getIntrinsicHeight() : mMinHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        drawBackground(canvas, mPaint);
        drawCenterDrawable(canvas, mDrawableCenter);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     * @param paint  画笔
     */
    protected void drawBackground(Canvas canvas, Paint paint) {
        switch (mShape) {
            case SHAPE_RECTANGLE:
                mRectF.set(getBounds());
                if (mEquilateral) {
                    float offset;
                    if (mRectF.width() > mRectF.height()) {
                        offset = (mRectF.width() - mRectF.height()) * 0.5f;
                        mRectF.set(mRectF.left + offset, mRectF.top,
                                mRectF.right - offset, mRectF.bottom);
                    } else if (mRectF.width() < mRectF.height()) {
                        offset = (mRectF.height() - mRectF.width()) * 0.5f;
                        mRectF.set(mRectF.left, mRectF.top + offset,
                                mRectF.right, mRectF.bottom - offset);
                    }
                }
                canvas.drawRect(mRectF, paint);
                break;
            case SHAPE_ROUNDED_RECTANGLE:
                mRectF.set(getBounds());
                if (mEquilateral) {
                    float offset;
                    if (mRectF.width() > mRectF.height()) {
                        offset = (mRectF.width() - mRectF.height()) * 0.5f;
                        mRectF.set(mRectF.left + offset, mRectF.top,
                                mRectF.right - offset, mRectF.bottom);
                    } else if (mRectF.width() < mRectF.height()) {
                        offset = (mRectF.height() - mRectF.width()) * 0.5f;
                        mRectF.set(mRectF.left, mRectF.top + offset,
                                mRectF.right, mRectF.bottom - offset);
                    }
                }
                canvas.drawRoundRect(mRectF, mRadius, mRadius, paint);
                break;
            case SHAPE_OVAL:
                mRectF.set(getBounds());
                if (mEquilateral) {
                    float offset;
                    if (mRectF.width() > mRectF.height()) {
                        offset = (mRectF.width() - mRectF.height()) * 0.5f;
                        mRectF.set(mRectF.left + offset, mRectF.top,
                                mRectF.right - offset, mRectF.bottom);
                    } else if (mRectF.width() < mRectF.height()) {
                        offset = (mRectF.height() - mRectF.width()) * 0.5f;
                        mRectF.set(mRectF.left, mRectF.top + offset,
                                mRectF.right, mRectF.bottom - offset);
                    }
                }
                canvas.drawOval(mRectF, paint);
                break;
        }


    }

    /**
     * 绘制中部Drawable
     *
     * @param canvas 画布
     * @param center Drawable
     */
    protected void drawCenterDrawable(Canvas canvas, Drawable center) {
        if (center != null) {
            center.setBounds(0, 0, center.getIntrinsicWidth(), center.getIntrinsicHeight());
            canvas.save();
            canvas.translate(getBounds().centerX() - center.getIntrinsicWidth() * 0.5f,
                    getBounds().centerY() - center.getIntrinsicHeight() * 0.5f);
            center.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        mDrawableCenter.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 获取背景色
     *
     * @return 背景色
     */
    public int getBackgroundColor() {
        return mPaint.getColor();
    }

    /**
     * 设置背景色
     *
     * @param color 颜色
     */
    public void setBackgroundColor(int color) {
        mPaint.setColor(color);
        invalidateSelf();
    }

    /**
     * 获取中心Drawable
     *
     * @return 中心Drawable
     */
    public Drawable getCenterDrawable() {
        return mDrawableCenter;
    }

    /**
     * 设置中心Drawable
     *
     * @param drawable 中心Drawable
     */
    public void setCenterDrawable(Drawable drawable) {
        mDrawableCenter = drawable;
        invalidateSelf();
    }

    /**
     * 设置圆角半径
     * 仅圆角矩形形状下有效
     *
     * @param radius 圆角半径
     */
    public void setCornerRadius(float radius) {
        mRadius = radius;
        invalidateSelf();
    }
}
