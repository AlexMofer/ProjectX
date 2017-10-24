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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;

/**
 * 文字Drawable
 */
@SuppressWarnings("unused")
public class TextDrawable extends Drawable {

    private final float density;
    private final TextPaint mTextPaint;// 文字画笔
    private final Rect mMeasureRect = new Rect();
    private float mTextSize;
    private String mText;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private boolean autoScale = false;

    public TextDrawable(Context context, int dimen, int colorId, int strId) {
        this(context, context.getResources().getDimension(dimen),
                Compat.getColor(context, colorId), context.getString(strId));
    }

    public TextDrawable(Context context, float textSize, int textColor, String text) {
        density = context.getResources().getDisplayMetrics().density;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        if (Build.VERSION.SDK_INT > 4) {
            updateTextPaintDensity();
        }
        setTextSize(textSize);
        setTextColor(textColor);
        setText(text);
    }

    @TargetApi(5)
    private void updateTextPaintDensity() {
        mTextPaint.density = density;
    }

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

    }

    @Override
    public void draw(Canvas canvas) {
        if (mText == null || mText.length() <= 0)
            return;
        final int width = getBounds().width();
        final int height = getBounds().height();
        if (width <= 0 || height <= 0)
            return;
        if (autoScale)
            mTextPaint.setTextSize(Math.min(width, height));
        else
            mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
        final int textWidth = mMeasureRect.width();
        final int textHeight = mMeasureRect.height();
        if (textWidth <= 0 || textHeight <= 0)
            return;
        canvas.save();
        if (autoScale) {
            float scale;
            float scaleWidth = width / (float) textWidth;
            float scaleHeight = height / (float) textHeight;
            scale = Math.min(scaleWidth, scaleHeight);
            canvas.translate(getBounds().centerX(), getBounds().centerY());
            canvas.scale(scale, scale, 0, 0);
            canvas.translate(0, textHeight * 0.5f);
            canvas.drawText(mText, 0, -mMeasureRect.bottom, mTextPaint);
        } else {
            canvas.translate(getBounds().centerX(), getBounds().centerY());
            canvas.translate(0, textHeight * 0.5f);
            canvas.drawText(mText, 0, -mMeasureRect.bottom, mTextPaint);
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(int alpha) {
        mTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mTextPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 设置文字大小
     *
     * @param size 文字大小
     */
    public void setTextSize(float size) {
        if (mTextSize != size) {
            mTextSize = size;
            mTextPaint.setTextSize(mTextSize);
            if (mText == null) {
                mIntrinsicWidth = 0;
                Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
                mIntrinsicHeight = metrics.bottom - metrics.top;
            } else {
                mTextPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
                mIntrinsicWidth = mMeasureRect.width();
                mIntrinsicHeight = mMeasureRect.height();
            }
            invalidateSelf();
        }
    }

    /**
     * 设置文字颜色
     *
     * @param color 颜色
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidateSelf();
    }

    /**
     * 设置文本
     *
     * @param text 文本
     */
    public void setText(String text) {
        if (mText == null || !mText.equals(text)) {
            mText = text;
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.getTextBounds(mText, 0, mText.length(), mMeasureRect);
            mIntrinsicWidth = mMeasureRect.width();
            mIntrinsicHeight = mMeasureRect.height();
            invalidateSelf();
        }
    }

    /**
     * 设置文字大小是否根据Bound自动缩放
     *
     * @param auto 自动缩放
     */
    public void setAutoScale(boolean auto) {
        if (autoScale != auto) {
            autoScale = auto;
            invalidateSelf();
        }
    }
}
