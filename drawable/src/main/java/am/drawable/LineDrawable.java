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

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 横线Drawable
 * 支持上下左右
 * Created by Alex on 2015/9/26.
 */
@SuppressWarnings({"unused", "WeakerAccess", "NullableProblems"})
public class LineDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mLine = new RectF();
    private ColorStateList mBackgroundColor;
    private ColorStateList mLineColor;
    private float mLineSize;
    private int mGravity;

    public LineDrawable() {
        this(Color.BLACK, 1);
    }

    public LineDrawable(int lineColor, float lineSize) {
        this(0x00000000, lineColor, lineSize);
    }

    public LineDrawable(int backgroundColor, int lineColor, float lineSize) {
        this(backgroundColor, lineColor, lineSize, Gravity.NO_GRAVITY);
    }

    public LineDrawable(int backgroundColor, int lineColor, float lineSize, int gravity) {
        this(ColorStateList.valueOf(backgroundColor), ColorStateList.valueOf(lineColor),
                lineSize, gravity);
    }

    public LineDrawable(ColorStateList backgroundColor, ColorStateList lineColor, float lineSize,
                        int gravity) {
        mBackgroundColor = backgroundColor;
        mLineColor = lineColor;
        mLineSize = lineSize;
        mGravity = gravity;
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = resources.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.LineDrawable);
        final ColorStateList backgroundColor =
                custom.getColorStateList(R.styleable.LineDrawable_ldBackgroundColor);
        final ColorStateList lineColor =
                custom.getColorStateList(R.styleable.LineDrawable_ldLineColor);
        mLineSize = custom.getDimension(R.styleable.LineDrawable_ldLineSize, 0);
        mGravity = custom.getInt(R.styleable.LineDrawable_ldGravity, Gravity.NO_GRAVITY);
        custom.recycle();
        if (backgroundColor != null)
            mBackgroundColor = backgroundColor;
        if (lineColor != null)
            mLineColor = lineColor;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mLine.setEmpty();
        if (mLineSize <= 0)
            return;
        final float half = mLineSize * 0.5f;
        switch (mGravity) {
            default:
            case Gravity.TOP:
                mLine.set(bounds.left, bounds.top, bounds.right, bounds.top + mLineSize);
                break;
            case Gravity.BOTTOM:
                mLine.set(bounds.left, bounds.bottom - mLineSize, bounds.right, bounds.bottom);
                break;
            case Gravity.LEFT:
                mLine.set(bounds.left, bounds.top, bounds.left + mLineSize, bounds.bottom);
                break;
            case Gravity.RIGHT:
                mLine.set(bounds.right - mLineSize, bounds.top, bounds.right, bounds.bottom);
                break;
            case Gravity.CENTER_HORIZONTAL:
                final float y = bounds.exactCenterY();
                mLine.set(bounds.left, y - half, bounds.right, y + half);
                break;
            case Gravity.CENTER_VERTICAL:
                final float x = bounds.exactCenterX();
                mLine.set(x - half, bounds.top, x + half, bounds.bottom);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        if (bounds.isEmpty())
            return;
        if (mBackgroundColor != null) {
            mPaint.setColor(mBackgroundColor.getColorForState(getState(),
                    mBackgroundColor.getDefaultColor()));
            canvas.drawRect(bounds, mPaint);
        }
        if (mLineColor != null) {
            mPaint.setColor(mLineColor.getColorForState(getState(), mLineColor.getDefaultColor()));
            canvas.drawRect(mLine, mPaint);
        }
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
    public int getAlpha() {
        return mPaint.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    /**
     * 获取背景色
     *
     * @return 背景色
     */
    public ColorStateList getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * 设置背景色
     *
     * @param background 背景色
     */
    public void setBackgroundColor(ColorStateList background) {
        mBackgroundColor = background;
        invalidateSelf();
    }

    /**
     * 获取线条色
     *
     * @return 线条色
     */
    public ColorStateList getLineColor() {
        return mLineColor;
    }

    /**
     * 设置线条色
     *
     * @param line 线条色
     */
    public void setLineColor(ColorStateList line) {
        mLineColor = line;
        invalidateSelf();
    }

    /**
     * 获取线宽
     *
     * @return 线宽
     */
    public float getLineSize() {
        return mLineSize;
    }

    /**
     * 设置线宽
     *
     * @param size 线宽
     */
    public void setLineSize(float size) {
        mLineSize = size;
        invalidateSelf();
    }

    /**
     * 获取位置
     *
     * @return 位置
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置位置
     *
     * @param gravity 位置
     */
    public void setGravity(int gravity) {
        mGravity = gravity;
        invalidateSelf();
    }
}