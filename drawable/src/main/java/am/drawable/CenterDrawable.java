package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * 中心Drawable
 * Created by Alex on 2015/11/4.
 * TODO 增加圆，矩形，圆角矩形背景
 */
public class CenterDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Drawable mDrawableCenter;
    private int mMinWidth;
    private int mMinHeight;

    public CenterDrawable(Drawable center, int color) {
        this(center, color, 0, 0);
    }

    public CenterDrawable(Drawable center, int color, int minWidth, int minHeight) {
        mDrawableCenter = center;
        mPaint.setColor(color);
        mMinWidth = minWidth;
        mMinHeight = minHeight;
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
    public void setBackgroundColor(int color) {
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
