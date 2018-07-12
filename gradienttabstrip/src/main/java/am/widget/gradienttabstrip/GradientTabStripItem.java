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

package am.widget.gradienttabstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

final class GradientTabStripItem extends View {

    private float mTextSize;// 文字大小
    private int mTextColorNormal;// 文字默认颜色
    private int mTextColorSelected;// 文字选中颜色
    private int mDrawablePadding;// 图文间距
    private int mDotMarginCenterX;// 小圆点距离中心X轴距离（以中心点为直角坐标系原点）
    private int mDotMarginCenterY;// 小圆点距离中心Y轴距离（以中心点为直角坐标系原点）
    private boolean mDotCanGoOutside;// 小圆点是否可绘制到视图外部
    private Drawable mDotBackground;// 小圆点背景图
    private float mDotTextSize;// 小圆点文字大小
    private int mDotTextColor;// 小圆点文字颜色
    private String mTitle;
    private String mDot;
    private Drawable mNormal;
    private Drawable mSelected;
    private float mOffset = 0;
    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Rect tRect = new Rect();
    private int mDrawableWidth;
    private int mTextHeight;
    private int mTextDesc;
    private int mDrawableHeight;
    private int mDotTextHeight;
    private int mDotTextDesc;

    GradientTabStripItem(Context context) {
        super(context);
        mPaint.density = getResources().getDisplayMetrics().density;
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mPaint.setTextSize(mTextSize);
        mDrawableWidth = getDrawableWidth();
        final int textWidth;
        if (TextUtils.isEmpty(mTitle)) {
            textWidth = 0;
        } else {
            mPaint.getTextBounds(mTitle, 0, mTitle.length(), tRect);
            textWidth = tRect.width();
        }
        final int width = Math.max(mDrawableWidth, textWidth);
        mDrawableHeight = getDrawableHeight();
        Paint.FontMetricsInt metrics = mPaint.getFontMetricsInt();
        mTextHeight = metrics.bottom - metrics.top;
        mTextDesc = metrics.bottom;
        final int height = mDrawableHeight + mDrawablePadding + mTextHeight;
        setMeasuredDimension(
                resolveSize(Math.max(width, getSuggestedMinimumWidth()), widthMeasureSpec),
                resolveSize(Math.max(height, getSuggestedMinimumHeight()), heightMeasureSpec));
        getDotTextInfo();
    }

    private int getDrawableWidth() {
        final int normal = mNormal == null ? 0 : mNormal.getIntrinsicWidth();
        final int selected = mSelected == null ? 0 : mSelected.getIntrinsicWidth();
        return Math.max(normal, selected);
    }

    private int getDrawableHeight() {
        final int normal = mNormal == null ? 0 : mNormal.getIntrinsicHeight();
        final int selected = mSelected == null ? 0 : mSelected.getIntrinsicHeight();
        return Math.max(normal, selected);
    }

    private void getDotTextInfo() {
        mPaint.setTextSize(mDotTextSize);
        Paint.FontMetricsInt metrics = mPaint.getFontMetricsInt();
        mDotTextHeight = metrics.bottom - metrics.top;
        mDotTextDesc = metrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDrawable(canvas);
        drawText(canvas);
        drawDot(canvas);
    }

    private void drawDrawable(Canvas canvas) {
        final float centerX = getWidth() * 0.5f;
        final float centerY = getHeight() * 0.5f;
        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.translate(-mDrawableWidth * 0.5f,
                -(mDrawableHeight + mDrawablePadding + mTextHeight) * 0.5f);

        if (mNormal != null && mOffset < 1) {
            mNormal.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
            mNormal.setAlpha(Math.min(255, Math.max(0, Math.round((1 - mOffset) * 255))));
            mNormal.draw(canvas);
        }
        if (mSelected != null && mOffset > 0) {
            mSelected.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
            mSelected.setAlpha(Math.min(255, Math.max(0, Math.round(mOffset * 255))));
            mSelected.draw(canvas);
        }
        canvas.restore();
    }

    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(mTitle))
            return;
        final float centerX = getWidth() * 0.5f;
        final float centerY = getHeight() * 0.5f;
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(BaseTabStripViewGroup.makeColor(mTextColorNormal,
                mTextColorSelected, mOffset));
        canvas.save();
        canvas.translate(centerX, centerY);
        canvas.translate(0, (mDrawableHeight + mDrawablePadding) * 0.5f);
        canvas.drawText(mTitle, 0, mTextDesc, mPaint);
        canvas.restore();
    }

    private void drawDot(Canvas canvas) {
        // TODO
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

    void setDrawablePadding(int padding) {
        if (padding == mDrawablePadding)
            return;
        mDrawablePadding = padding;
    }

    void setDotMarginCenter(int x, int y) {
        if (mDotMarginCenterX == x && mDotMarginCenterY == y)
            return;
        mDotMarginCenterX = x;
        mDotMarginCenterY = y;
        invalidate();
    }

    void setDotCanGoOutside(boolean can) {
        if (mDotCanGoOutside == can)
            return;
        mDotCanGoOutside = can;
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

    void set(CharSequence title, String dot, Drawable normal, Drawable selected, float offset) {
        final String ts = title == null ? null : title.toString();
        if (TextUtils.equals(mTitle, ts) && TextUtils.equals(mDot, dot) && mNormal == normal &&
                mSelected == selected && mOffset == offset)
            return;
        mTitle = ts;
        mDot = dot;
        mNormal = normal;
        mSelected = selected;
        mOffset = offset;
        invalidate();
    }
}
