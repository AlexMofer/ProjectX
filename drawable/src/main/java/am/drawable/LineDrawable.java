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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;

/**
 * 横线Drawable
 * 支持上下左右
 * Created by Alex on 2015/9/26.
 */
@SuppressWarnings("all")
public class LineDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRect = new Rect();
    private int mBackgroundColor;
    private int mLineColor;
    private int mLineSize;
    private int mGravity;

    public LineDrawable(int lineColor, int lineSize) {
        this(0x00000000, lineColor, lineSize);
    }

    public LineDrawable(int backgroundColor, int lineColor, int lineSize) {
        this(backgroundColor, lineColor, lineSize, Gravity.BOTTOM);
    }

    public LineDrawable(int backgroundColor, int lineColor, int lineSize, int gravity) {
        setBackground(backgroundColor);
        setLineColor(lineColor);
        setLineSize(lineSize);
        setGravity(gravity);
    }

    /**
     * 获取StateListDrawable
     *
     * @param backgroundColor 背景色
     * @param normalColor     普通情况线条颜色
     * @param focusColor      选中情况线条颜色
     * @param lineSize        线条粗细
     * @return StateListDrawable
     */
    public static StateListDrawable getLineStateListDrawable(int backgroundColor, int normalColor, int focusColor, int lineSize) {
        return getLineStateListDrawable(backgroundColor, normalColor, focusColor, lineSize, Gravity.BOTTOM);
    }

    /**
     * 获取StateListDrawable
     *
     * @param backgroundColor 背景色
     * @param normalColor     普通情况线条颜色
     * @param focusColor      选中情况线条颜色
     * @param lineSize        线条粗细
     * @param gravity         布局
     * @return StateListDrawable
     */
    public static StateListDrawable getLineStateListDrawable(int backgroundColor, int normalColor, int focusColor, int lineSize, int gravity) {
        StateListDrawable drawable = new StateListDrawable();
        LineDrawable focus = new LineDrawable(backgroundColor, focusColor, lineSize, gravity);
        LineDrawable normal = new LineDrawable(backgroundColor, normalColor, lineSize, gravity);
        drawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
        drawable.addState(new int[]{android.R.attr.state_focused}, focus);
        drawable.addState(new int[]{}, normal);
        return drawable;
    }

    @Override
    @SuppressWarnings("all")
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        if (bounds == null)
            return;
        switch (mGravity) {
            default:
            case Gravity.BOTTOM:
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.bottom - mLineSize);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.bottom - mLineSize, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.TOP:
                mRect.set(bounds.left, bounds.top + mLineSize, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.top + mLineSize);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.LEFT:
                mRect.set(bounds.left + mLineSize, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.left + mLineSize, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.RIGHT:
                mRect.set(bounds.left, bounds.top, bounds.right - mLineSize, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.right - mLineSize, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.CENTER_HORIZONTAL:
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.centerY());
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.centerY() + mLineSize, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.centerY(), bounds.right, bounds.centerY() + mLineSize);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.CENTER_VERTICAL:
                mRect.set(bounds.left, bounds.top, bounds.centerX(), bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.centerX() + mLineSize, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.centerX(), bounds.top, bounds.centerX() + mLineSize, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.TOP | Gravity.BOTTOM:
                mRect.set(bounds.left, bounds.top + mLineSize, bounds.right, bounds.bottom - mLineSize);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.top + mLineSize);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.bottom - mLineSize, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.LEFT | Gravity.RIGHT:
                mRect.set(bounds.left + mLineSize, bounds.top, bounds.right - mLineSize, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.left + mLineSize, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.right - mLineSize, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置背景色
     *
     * @param color 背景色
     */
    public void setBackground(int color) {
        mBackgroundColor = color;
        invalidateSelf();
    }

    /**
     * 设置线条颜色
     *
     * @param color 线条颜色
     */
    public void setLineColor(int color) {
        mLineColor = color;
        invalidateSelf();
    }

    /**
     * 设置线条粗细
     *
     * @param size 线条粗细
     */
    public void setLineSize(int size) {
        mLineSize = size;
        invalidateSelf();
    }

    /**
     * 设置布局
     *
     * @param gravity 布局
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        invalidateSelf();
    }
}
