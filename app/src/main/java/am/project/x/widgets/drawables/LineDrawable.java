package am.project.x.widgets.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.view.Gravity;

/**
 * 一横线Drawable
 * 支持上下左右
 * Created by Alex on 2015/9/26.
 */
public class LineDrawable extends Drawable {
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mRect = new Rect();
    private int mBackgroundColor;
    private int mLineColor;
    private int mLineHeight;
    private int mGravity;


    public LineDrawable(@ColorInt int backgroundColor, @ColorInt int lineColor, int lineHeight) {
        this(backgroundColor, lineColor, lineHeight, Gravity.BOTTOM);
    }

    public LineDrawable(@ColorInt int backgroundColor, @ColorInt int lineColor, int lineHeight, int gravity) {
        mBackgroundColor = backgroundColor;
        mLineColor = lineColor;
        mLineHeight = lineHeight;
        mGravity = gravity;
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        if (bounds == null)
            return;
        switch (mGravity) {
            default:
            case Gravity.BOTTOM:
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.bottom - mLineHeight);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.bottom - mLineHeight, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.TOP:
                mRect.set(bounds.left, bounds.top + mLineHeight, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.top + mLineHeight);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.LEFT:
                mRect.set(bounds.left + mLineHeight, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.left + mLineHeight, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.RIGHT:
                mRect.set(bounds.left, bounds.top, bounds.right - mLineHeight, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.right - mLineHeight, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.CENTER_HORIZONTAL:
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.centerY());
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.centerY() + mLineHeight, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.centerY(), bounds.right, bounds.centerY() + mLineHeight);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.CENTER_VERTICAL:
                mRect.set(bounds.left, bounds.top, bounds.centerX(), bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.centerX() + mLineHeight, bounds.top, bounds.right, bounds.bottom);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.centerX(), bounds.top, bounds.centerX() + mLineHeight, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                break;
            case Gravity.TOP | Gravity.BOTTOM:
                mRect.set(bounds.left, bounds.top + mLineHeight, bounds.right, bounds.bottom - mLineHeight);
                mPaint.setColor(mBackgroundColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.top, bounds.right, bounds.top + mLineHeight);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
                mRect.set(bounds.left, bounds.bottom - mLineHeight, bounds.right, bounds.bottom);
                mPaint.setColor(mLineColor);
                canvas.drawRect(mRect, mPaint);
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
     * 多状态一横线Drawable
     * Created by Alex on 2015/10/27.
     */
    public static class StateListLineDrawable extends StateListDrawable {

        public StateListLineDrawable(@ColorInt int backgroundColor, @ColorInt int normalColor, @ColorInt int focusColor, int lineHeight) {
            this(backgroundColor, normalColor, focusColor, lineHeight, Gravity.BOTTOM);
        }

        public StateListLineDrawable(@ColorInt int backgroundColor, @ColorInt int lineColor, @ColorInt int focusColor, int lineHeight, int gravity) {
            LineDrawable focus = new LineDrawable(backgroundColor, focusColor, lineHeight, gravity);
            LineDrawable normal = new LineDrawable(backgroundColor, lineColor, lineHeight, gravity);
            addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focus);
            addState(new int[]{android.R.attr.state_focused}, focus);
            addState(new int[]{}, normal);
        }
    }
}
