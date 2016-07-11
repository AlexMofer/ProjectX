package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;

/**
 * 尖角框
 */
@SuppressWarnings("unused")
public class SharpCornerBoxDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mPaddingRect = new Rect();
    private final Path mPath = new Path();
    private int mColor;// 颜色
    private int mCornerWidth;// 尖角宽
    private int mCornerHeight;// 尖角高
    private float mCornerRadius;// 尖角圆角半径
    private int mDirection = Gravity.TOP;//朝向
    private int mLocation = Gravity.CENTER;// 位置
    private int mCornerMargin;//尖角边距
    private float mRadius;// 内容圆角半径
    private int mPadding;// 内容间隔
    private int mStokeColor;// 描边颜色
    private int mStokeSize;// 描边线宽

    private float mCornerOffset;// 尖角圆角导致的空余
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;


    private final RectF mRoundRect = new RectF();
    private Point mPoint;
    private float mRoundRectCorner;
    private boolean considerCorner = true;

    public SharpCornerBoxDrawable(int color) {
        setColor(color);
    }

    public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight, float cornerRadius,
                                  int direction, int location, int padding,
                                  float roundRectCorner) {
        setColor(color);
        setCornerWidth(cornerWidth);
        setCornerHeight(cornerHeight);
        setCornerRadius(cornerRadius);
        setDirection(direction);
        setLocation(location);
        setPadding(padding);
        setRoundRectCorner(roundRectCorner);
        setConsiderCorner(true);
        updatePaddingRect();
    }

    @SuppressWarnings("all")
    private void updatePaddingRect() {
        switch (mDirection) {
            case Gravity.BOTTOM:
                mPaddingRect.set(0, 0, 0, mCornerHeight);
                break;
            case Gravity.LEFT:
                mPaddingRect.set(mCornerHeight, 0, 0, 0);
                break;
            case Gravity.RIGHT:
                mPaddingRect.set(0, 0, mCornerHeight, 0);
                break;
            default:
            case Gravity.TOP:
                mPaddingRect.set(0, mCornerHeight, 0, 0);
                break;
        }
    }

    private void pathCornerLeft(Rect bounds) {
        final float halfWidth = mCornerWidth * 0.5f;
        final float mCornerPointX;
        final float mCornerPointY;
        mCornerPointX = bounds.left;
        if (mPoint == null) {
            switch (mLocation) {
                default:
                case Gravity.CENTER:
                    mCornerPointY = bounds.centerY();
                    break;
                case Gravity.RIGHT:
                    mCornerPointY = bounds.bottom - mPadding - halfWidth;
                    break;
                case Gravity.LEFT:
                    mCornerPointY = bounds.top + mPadding + halfWidth;
                    break;
            }
        } else {
            mCornerPointY = mPoint.y;
        }

        mPath.moveTo(mCornerPointX, mCornerPointY);
        mPath.lineTo(mCornerPointX + mCornerHeight, mCornerPointY + halfWidth);
        mPath.lineTo(mCornerPointX + mCornerHeight, mCornerPointY - halfWidth);
        mPath.close();
    }

    /**
     * 获取Padding
     *
     * @return Padding
     */
    public int getPadding() {
        return mPadding;
    }

    /**
     * 设置Padding
     *
     * @param padding Padding
     */
    public void setPadding(int padding) {
        if (getPadding() != padding) {
            mPadding = padding;
            invalidateSelf();
        }
    }

    /**
     * 设置圆角半径
     *
     * @return 圆角半径
     */
    public float getRoundRectCorner() {
        return mRoundRectCorner;
    }

    /**
     * 设置圆角半径
     *
     * @param corner 设置圆角半径
     */
    public void setRoundRectCorner(float corner) {
        if (getRoundRectCorner() != corner) {
            mRoundRectCorner = corner;
            if (mRoundRectCorner > mPadding * 0.5f) {
                mPadding = (int) Math.ceil(mRoundRectCorner);
            }
            invalidateSelf();
        }
    }

    /**
     * 设置圆角固定点
     *
     * @param point 固定点
     */
    public void setCornerPoint(Point point) {
        mPoint = point;
        invalidateSelf();
    }

    /**
     * 判断高宽是否考虑圆角
     *
     * @return 高宽是否考虑圆角
     */
    public boolean isConsiderCorner() {
        return considerCorner;
    }

    /**
     * 设置高宽是否考虑圆角
     *
     * @param considerCorner 高宽是否考虑圆角
     */
    public void setConsiderCorner(boolean considerCorner) {
        this.considerCorner = considerCorner;
    }


    @Override
    public void draw(Canvas canvas) {
        // TODO
        canvas.drawColor(mColor);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
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

    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }

    @Override
    @SuppressWarnings("all")
    public boolean getPadding(Rect padding) {
        if (mPaddingRect != null) {
            padding.set(mPaddingRect);
            return true;
        } else {
            return super.getPadding(padding);
        }
    }

    /**
     * 计算必要的数据
     * @param all 全部重新计算
     */
    private void calculateValue(boolean all) {
        if (all) {
            // TODO 计算Padding，高度，宽度，尖角位置，Path
            if (mCornerWidth > 0 && mCornerHeight > 0) {
                mIntrinsicWidth = 0;
                mIntrinsicHeight = 0;
            } else {
                mIntrinsicWidth = 0;
                mIntrinsicHeight = 0;
            }
        } else {
            // TODO 尖角位置，Path
        }

    }


    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        if (mColor != color) {
            invalidateSelf();
        }
    }

    /**
     * 获取尖角相对宽
     *
     * @return 相对宽
     */
    @SuppressWarnings("unused")
    public int getCornerWidth() {
        return mCornerWidth;
    }

    /**
     * 设置尖角相对宽
     *
     * @param width 相对宽
     */
    public void setCornerWidth(int width) {
        if (mCornerWidth != width) {
            mCornerWidth = width;
            calculateValue(true);
            invalidateSelf();
            DrawableHelper.requestCallbackLayout(this);
        }
    }

    /**
     * 获取尖角相对高
     *
     * @return 相对高
     */
    @SuppressWarnings("unused")
    public int getCornerHeight() {
        return mCornerHeight;
    }

    /**
     * 设置尖角相对高
     *
     * @param height 相对高
     */
    public void setCornerHeight(int height) {
        if (mCornerHeight != height) {
            mCornerHeight = height;
            calculateValue(true);
            invalidateSelf();
            DrawableHelper.requestCallbackLayout(this);
        }
    }

    /**
     * 设置尖角圆角半径
     *
     * @param cornerRadius 尖角圆角半径
     */
    public void setCornerRadius(float cornerRadius) {
        if (mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;
            calculateValue(true);
            invalidateSelf();
            DrawableHelper.requestCallbackLayout(this);
        }
    }

    /**
     * 设置朝向
     *
     * @param direction 朝向
     */
    @SuppressWarnings("all")
    public void setDirection(int direction) {
        if (direction != Gravity.LEFT &&
                direction != Gravity.TOP &&
                direction != Gravity.RIGHT &&
                direction != Gravity.BOTTOM)
            return;
        if (mDirection != direction) {
            mDirection = direction;
            calculateValue(true);
            invalidateSelf();
            DrawableHelper.requestCallbackLayout(this);
        }
    }

    /**
     * 设置位置
     *
     * @param location 位置
     */
    @SuppressWarnings("all")
    public void setLocation(int location) {
        if (location != Gravity.LEFT &&
                location != Gravity.CENTER &&
                location != Gravity.RIGHT)
            return;

        if (mLocation != location) {
            mLocation = location;
            calculateValue(false);
            invalidateSelf();
        }
    }
    // TODO
}
