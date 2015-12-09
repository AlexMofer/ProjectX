package com.am.widget.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

/**
 * 中心Drawable
 * Created by Alex on 2015/11/4.
 */
public class CenterDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mDrawableCenter;

    public CenterDrawable(Drawable center, @ColorInt int color) {
        mDrawableCenter = center;
        mPaint.setColor(color);
    }

    @Override
    public int getMinimumHeight() {
        return mDrawableCenter == null ? super.getMinimumHeight() : Math.max(mDrawableCenter.getIntrinsicHeight(), super.getMinimumHeight());
    }

    @Override
    public int getMinimumWidth() {
        return mDrawableCenter == null ? super.getMinimumWidth() : Math.max(mDrawableCenter.getIntrinsicWidth(), super.getMinimumWidth());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
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
        canvas.drawRect(getBounds(), paint);
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
            canvas.translate(getBounds().centerX() - center.getIntrinsicWidth() * 0.5f, getBounds().centerY() - center.getIntrinsicHeight() * 0.5f);
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
     * 设置背景色
     *
     * @param color 颜色
     */
    public void setBackgroundColor(@ColorInt int color) {
        mPaint.setColor(color);
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
     * 设置中心Drawable
     *
     * @param drawable 中心Drawable
     */
    public void setCenterDrawable(Drawable drawable) {
        mDrawableCenter = drawable;
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
}
