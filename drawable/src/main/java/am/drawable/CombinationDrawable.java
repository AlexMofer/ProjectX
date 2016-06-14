package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

/**
 * 双层Drawable
 * Created by Alex on 2015/11/4.
 */
public class CombinationDrawable extends Drawable {

    private Drawable mBackground;
    private Drawable mForeground;
    private int mGravity;
    private int mReservedLeft;
    private int mReservedTop;
    private int mReservedRight;
    private int mReservedBottom;

    public CombinationDrawable(Drawable background, Drawable foreground,
                               int gravity, int reservedLeft, int reservedTop,
                               int reservedRight, int reservedBottom) {
        mBackground = background;
        mForeground = foreground;
        mGravity = gravity;
        mReservedLeft = reservedLeft;
        mReservedTop = reservedTop;
        mReservedRight = reservedRight;
        mReservedBottom = reservedBottom;
    }

    @Override
    public int getMinimumHeight() {
        int minimumHeight = super.getMinimumHeight();
        if (mBackground != null) {
            minimumHeight = Math.max(mBackground.getIntrinsicHeight(), minimumHeight);
        }
        if (mForeground != null) {
            minimumHeight = Math.max(mForeground.getIntrinsicHeight(), minimumHeight);
        }
        return minimumHeight;
    }

    @Override
    public int getMinimumWidth() {
        int minimumWidth = super.getMinimumWidth();
        if (mBackground != null) {
            minimumWidth = Math.max(mBackground.getIntrinsicWidth(), minimumWidth);
        }
        if (mForeground != null) {
            minimumWidth = Math.max(mForeground.getIntrinsicWidth(), minimumWidth);
        }
        return minimumWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        drawBackground(canvas);
        drawForeground(canvas);
    }

    /**
     * 绘制背景
     *
     * @param canvas 画布
     */
    protected void drawBackground(Canvas canvas) {
        if (mBackground != null) {
            mBackground.setBounds(getBounds().left + mReservedLeft,
                    getBounds().top + mReservedTop,
                    getBounds().right - mReservedRight,
                    getBounds().bottom - mReservedBottom);
            mBackground.draw(canvas);
        }
    }

    /**
     * 绘制中部Drawable
     *
     * @param canvas 画布
     */
    protected void drawForeground(Canvas canvas) {
        if (mForeground != null) {
            final int width = mForeground.getIntrinsicWidth();
            final int height = mForeground.getIntrinsicHeight();
            mForeground.setBounds(0, 0, width, height);
            canvas.save();
            switch (mGravity) {
                default:
                case Gravity.LEFT:
                    break;
                case Gravity.CENTER_HORIZONTAL:
                    canvas.translate(getBounds().centerX() - width * 0.5f, 0);
                    break;
                case Gravity.RIGHT:
                    canvas.translate(getBounds().right - width, 0);
                    break;
                case Gravity.CENTER_VERTICAL:
                    canvas.translate(0, getBounds().centerY() - height * 0.5f);
                    break;
                case Gravity.CENTER:
                    canvas.translate(getBounds().centerX() - width * 0.5f, getBounds().centerY() - height * 0.5f);
                    break;
                case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
                    canvas.translate(getBounds().right - width, getBounds().centerY() - height * 0.5f);
                    break;
                case Gravity.BOTTOM:
                    canvas.translate(0, getBounds().bottom - height);
                    break;
                case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                    canvas.translate(getBounds().centerX() - width * 0.5f, getBounds().bottom - height);
                    break;
                case Gravity.BOTTOM | Gravity.RIGHT:
                    canvas.translate(getBounds().right - width, getBounds().bottom - height);
                    break;
            }
            mForeground.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mBackground != null)
            mBackground.setAlpha(alpha);
        if (mForeground != null)
            mForeground.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mBackground != null)
            mBackground.setColorFilter(cf);
        if (mForeground != null)
            mForeground.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
