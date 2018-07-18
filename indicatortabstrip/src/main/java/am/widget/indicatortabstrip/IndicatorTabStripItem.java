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

package am.widget.indicatortabstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

final class IndicatorTabStripItem extends View {

    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Rect tRect = new Rect();
    private int mColorBackgroundNormal;// 默认颜色背景
    private int mColorBackgroundSelected;// 选中颜色背景
    private float mTextSize;// 文字大小
    private int mTextColorNormal;// 文字默认颜色
    private int mTextColorSelected;// 文字选中颜色
    private float mDotCenterToViewCenterX;// 小圆点中心距离View中心X轴距离（以中心点为直角坐标系原点）
    private float mDotCenterToViewCenterY;// 小圆点中心距离View中心Y轴距离（以中心点为直角坐标系原点）
    private boolean mDotCanGoOutside;// 小圆点是否可绘制到视图外部
    private boolean mDotAutoChangeWidth;// 小圆点是否自动修改宽度（宽度小于高度时调整宽度，使其为圆点）
    private Drawable mDotBackground;// 小圆点背景图
    private float mDotTextSize;// 小圆点文字大小
    private int mDotTextColor;// 小圆点文字颜色
    private String mTitle;
    private String mDot;
    private float mOffset = 0;
    private int mTextDesc;
    private float mTextScale;// 选中文字缩放
    private int mDotTextHeight;
    private int mDotTextDesc;

    IndicatorTabStripItem(Context context) {
        super(context);
        mPaint.density = getResources().getDisplayMetrics().density;
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPaint.setTextSize(mTextSize);
        final int textWidth;
        if (TextUtils.isEmpty(mTitle)) {
            textWidth = 0;
        } else {
            mPaint.getTextBounds(mTitle, 0, mTitle.length(), tRect);
            textWidth = tRect.width();
        }
        final int width = mTextScale > 1 ?
                (int) (Math.ceil((float) textWidth * mTextScale) + 1) : textWidth;
        Paint.FontMetricsInt metrics = mPaint.getFontMetricsInt();
        final int textHeight = metrics.bottom - metrics.top;
        mTextDesc = metrics.bottom;
        final int height = mTextScale > 1 ?
                (int) (Math.ceil((float) textHeight * mTextScale) + 1) : textHeight;
        setMeasuredDimension(
                resolveSize(Math.max(width, getSuggestedMinimumWidth()), widthMeasureSpec),
                resolveSize(Math.max(height, getSuggestedMinimumHeight()), heightMeasureSpec));
        getDotTextInfo();
    }

    private void getDotTextInfo() {
        mPaint.setTextSize(mDotTextSize);
        Paint.FontMetricsInt metrics = mPaint.getFontMetricsInt();
        mDotTextHeight = metrics.bottom - metrics.top;
        mDotTextDesc = metrics.bottom;
    }

    @Override
    public void draw(Canvas canvas) {
        drawColorBackground(canvas);
        super.draw(canvas);
    }

    private void drawColorBackground(Canvas canvas) {
        if (mColorBackgroundNormal == 0 && mColorBackgroundSelected == 0)
            return;
        final int color = IndicatorTabStrip.makeColor(
                mColorBackgroundNormal, mColorBackgroundSelected, mOffset);
        canvas.drawColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawDot(canvas);
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mTitle))
            return;
        final float centerX = getWidth() * 0.5f;
        final float centerY = getHeight() * 0.5f;
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(IndicatorTabStrip.makeColor(mTextColorNormal,
                mTextColorSelected, mOffset));
        float scale;
        if (mOffset == 1) {
            scale = mTextScale;
        } else if (mOffset == 0) {
            scale = 1;
        } else {
            scale = 1 + (mTextScale - 1) * mOffset;
        }
        canvas.save();
        canvas.translate(centerX, centerY + mTextDesc);
        if (scale != 1) {
            canvas.scale(scale, scale, 0, -mTextDesc);
        }
        canvas.drawText(mTitle, 0, 0, mPaint);
        canvas.restore();
    }

    private void drawDot(Canvas canvas) {
        if (mDot == null)
            return;
        mPaint.setTextSize(mDotTextSize);
        final int dotTextWidth;
        final int dotTextHeight;
        if (mDot.length() <= 0) {
            dotTextWidth = 0;
            dotTextHeight = 0;
        } else {
            mPaint.getTextBounds(mDot, 0, mDot.length(), tRect);
            dotTextWidth = tRect.width();
            dotTextHeight = mDotTextHeight;
        }
        final int paddingLeft;
        final int paddingTop;
        final int paddingRight;
        final int paddingBottom;
        final int backgroundWidth;
        final int backgroundHeight;
        if (mDotBackground != null) {
            mDotBackground.getPadding(tRect);
            paddingLeft = tRect.left;
            paddingTop = tRect.top;
            paddingRight = tRect.right;
            paddingBottom = tRect.bottom;
            backgroundWidth = mDotBackground.getMinimumWidth();
            backgroundHeight = mDotBackground.getMinimumHeight();
        } else {
            paddingLeft = 0;
            paddingTop = 0;
            paddingRight = 0;
            paddingBottom = 0;
            backgroundWidth = 0;
            backgroundHeight = 0;
        }
        final int dotSize = Math.max(dotTextHeight + paddingTop + paddingBottom, backgroundHeight);
        int dotWidth = Math.max(dotTextWidth + paddingLeft + paddingRight, backgroundWidth);
        if (dotWidth < dotSize && mDotAutoChangeWidth)
            dotWidth = dotSize;
        final float centerX = getWidth() * 0.5f;
        final float centerY = getHeight() * 0.5f;
        float left = centerX + mDotCenterToViewCenterX - dotWidth * 0.5f;
        if (!mDotCanGoOutside) {
            if (left < 0)
                left = 0;
            if (left > getWidth() - dotWidth)
                left = getWidth() - dotWidth;
        }
        float top = centerY + mDotCenterToViewCenterY - dotSize * 0.5f;
        if (!mDotCanGoOutside) {
            if (top < 0)
                top = 0;
            if (top > getHeight() - dotSize)
                top = getHeight() - dotSize;
        }
        canvas.save();
        canvas.translate(left, top);
        if (mDotBackground != null) {
            mDotBackground.setBounds(0, 0, dotWidth, dotSize);
            mDotBackground.draw(canvas);
        }
        if (mDot.length() > 0) {
            final float offsetX = Math.max(dotWidth * 0.5f, paddingLeft + dotTextWidth * 0.5f);
            final float offsetY = Math.max(dotSize * 0.5f, paddingTop + dotTextHeight * 0.5f);
            canvas.translate(offsetX, offsetY);
            mPaint.setColor(mDotTextColor);
            canvas.drawText(mDot, 0, mDotTextDesc, mPaint);
        }
        canvas.restore();
    }

    void setColorBackground(int normal, int selected) {
        if (mColorBackgroundNormal == normal && mColorBackgroundSelected == selected)
            return;
        mColorBackgroundNormal = normal;
        mColorBackgroundSelected = selected;
        invalidate();
    }

    void setTextSize(float size) {
        if (mTextSize == size)
            return;
        mTextSize = size;
    }

    void setTextColor(int normal, int selected) {
        if (mTextColorNormal == normal && mTextColorSelected == selected)
            return;
        mTextColorNormal = normal;
        mTextColorSelected = selected;
        invalidate();
    }

    void setTextScale(float scale) {
        if (mTextScale == scale)
            return;
        mTextScale = scale;
    }

    void setDotCenterToViewCenter(float x, float y) {
        if (mDotCenterToViewCenterX == x && mDotCenterToViewCenterY == y)
            return;
        mDotCenterToViewCenterX = x;
        mDotCenterToViewCenterY = y;
        invalidate();
    }

    void setDotCanGoOutside(boolean can) {
        if (mDotCanGoOutside == can)
            return;
        mDotCanGoOutside = can;
        invalidate();
    }

    void setDotAutoChangeWidth(boolean auto) {
        if (mDotAutoChangeWidth == auto)
            return;
        mDotAutoChangeWidth = auto;
        invalidate();
    }

    void setDotBackground(Drawable background) {
        if (mDotBackground == background)
            return;
        mDotBackground = background;
        invalidate();
    }

    void setDotTextSize(float size) {
        if (mDotTextSize == size)
            return;
        mDotTextSize = size;
        getDotTextInfo();
        invalidate();
    }

    void setDotTextColor(int color) {
        if (mDotTextColor == color)
            return;
        mDotTextColor = color;
        invalidate();
    }

    void set(CharSequence title, String dot, float offset) {
        final String ts = title == null ? null : title.toString();
        if (TextUtils.equals(mTitle, ts) && TextUtils.equals(mDot, dot) && mOffset == offset)
            return;
        mTitle = ts;
        mDot = dot;
        mOffset = offset;
        invalidate();
    }
}
