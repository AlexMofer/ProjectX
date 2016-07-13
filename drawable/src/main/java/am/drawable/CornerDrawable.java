package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

/**
 * 尖角框
 */
public class CornerDrawable extends Drawable {

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
    private final Rect mContentPaddingRect = new Rect();// 内容间隔
    private float mContentRadius;// 内容圆角半径
    private int mStokeColor;// 描边颜色
    private int mStokeSize;// 描边线宽

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    private RectF mTempRect = new RectF();

    public CornerDrawable(int color, int cornerWidth, int cornerHeight) {
        this(color, cornerWidth, cornerHeight, 0, Gravity.TOP, Gravity.CENTER, 0);
    }

    public CornerDrawable(int color, int cornerWidth, int cornerHeight, float cornerRadius,
                          int direction, int location, float contentRadius) {
        setColor(color);
        setCornerWidth(cornerWidth);
        setCornerHeight(cornerHeight);
        setCornerRadius(cornerRadius);
        setDirection(direction);
        setLocation(location);
        setContentRadius(contentRadius);
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);

    }

    @Override
    public void draw(Canvas canvas) {
        // TODO 尖角位置，Path
        mPath.rewind();
        if (mCornerHeight == 0) {
            if (mContentRadius == 0) {
                mTempRect.set(getBounds());
                mPath.addRect(mTempRect, Path.Direction.CW);
            } else {
                mTempRect.set(getBounds());
                mPath.addRoundRect(mTempRect, mContentRadius, mContentRadius, Path.Direction.CCW);
            }
        } else {
            if (mCornerRadius == 0) {

            } else {

            }


            mPath.moveTo(0, 0);
            mPath.lineTo(100, 0);
            mPath.lineTo(100, 100);
            mPath.lineTo(0, 100);
            mPath.addCircle(100, 100, 50, Path.Direction.CCW);

        }
        mPaint.setColor(mColor);
        canvas.drawPath(mPath, mPaint);
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
        padding.set(mPaddingRect);
        return true;
    }

    /**
     * 计算必要的数据
     *
     */
    private void calculateValue() {
        if (mCornerHeight <= 0) {
            mPaddingRect.set(mContentPaddingRect);
            mIntrinsicWidth = 0;
            mIntrinsicHeight = 0;
        } else {
            switch (mDirection) {
                case Gravity.LEFT:
                    mPaddingRect.set(mContentPaddingRect.left + mCornerHeight,
                            mContentPaddingRect.top, mContentPaddingRect.right,
                            mContentPaddingRect.bottom);
                    mIntrinsicWidth = mCornerHeight + (int) Math.ceil(mContentRadius * 2);
                    mIntrinsicHeight = Math.max(mCornerWidth, (int) Math.ceil(mContentRadius * 2));
                    break;
                case Gravity.RIGHT:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top, mContentPaddingRect.right + mCornerHeight,
                            mContentPaddingRect.bottom);
                    mIntrinsicWidth = mCornerHeight + (int) Math.ceil(mContentRadius * 2);
                    mIntrinsicHeight = Math.max(mCornerWidth, (int) Math.ceil(mContentRadius * 2));
                    break;
                case Gravity.BOTTOM:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top, mContentPaddingRect.right,
                            mContentPaddingRect.bottom + mCornerHeight);
                    mIntrinsicWidth = Math.max(mCornerWidth, (int) Math.ceil(mContentRadius * 2));
                    mIntrinsicHeight = mCornerHeight + (int) Math.ceil(mContentRadius * 2);
                    break;
                default:
                case Gravity.TOP:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top + mCornerHeight, mContentPaddingRect.right,
                            mContentPaddingRect.bottom);
                    mIntrinsicWidth = Math.max(mCornerWidth, (int) Math.ceil(mContentRadius * 2));
                    mIntrinsicHeight = mCornerHeight + (int) Math.ceil(mContentRadius * 2);
                    break;
            }

        }
        mCornerMargin = Math.max(mCornerMargin, (int) Math.ceil(mContentRadius * 2));
    }


    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            invalidateSelf();
        }
    }

    /**
     * 设置尖角相对宽
     *
     * @param width 相对宽
     */
    public void setCornerWidth(int width) {
        if (width >= 0 && mCornerWidth != width) {
            mCornerWidth = width;
            calculateValue();
            invalidateSelf();
            DrawableHelper.invalidateCallbackPadding(this);
            DrawableHelper.requestCallbackLayout(this);
        }
    }

    /**
     * 设置尖角相对高
     *
     * @param height 相对高
     */
    public void setCornerHeight(int height) {
        if (height >= 0 && mCornerHeight != height) {
            mCornerHeight = height;
            calculateValue();
            invalidateSelf();
            DrawableHelper.invalidateCallbackPadding(this);
            DrawableHelper.requestCallbackLayout(this);
        }
    }

    /**
     * 设置尖角圆角半径
     *
     * @param cornerRadius 尖角圆角半径
     */
    public void setCornerRadius(float cornerRadius) {
        if (cornerRadius >= 0 && mCornerRadius != cornerRadius) {
            mCornerRadius = cornerRadius;
            invalidateSelf();
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
            calculateValue();
            invalidateSelf();
            DrawableHelper.invalidateCallbackPadding(this);
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
            invalidateSelf();
        }
    }

    /**
     * 设置尖角左右间隔
     *
     * @param margin 左右间隔
     */
    public void setCornerMargin(int margin) {
        if (margin >= 0 && mCornerMargin != margin) {
            mCornerMargin = margin;
            invalidateSelf();
        }
    }

    /**
     * 设置Padding
     *
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    public void setPadding(int left, int top, int right, int bottom) {
        mContentPaddingRect.set(left, top, right, bottom);
        calculateValue();
        invalidateSelf();
        DrawableHelper.invalidateCallbackPadding(this);
        DrawableHelper.requestCallbackLayout(this);
    }

    /**
     * 设置Padding
     *
     * @param padding Padding
     */
    public void setPadding(Rect padding) {
        if (padding != null) {
            setPadding(padding.left, padding.top, padding.right, padding.bottom);
        }
    }

    /**
     * 设置内容圆角半径
     *
     * @param contentRadius 内容圆角半径
     */
    public void setContentRadius(float contentRadius) {
        if (contentRadius >= 0 && mContentRadius != contentRadius) {
            mContentRadius = contentRadius;
            invalidateSelf();
        }
    }

    /**
     * 设置描边颜色
     *
     * @param color 描边颜色
     */
    public void setStokeColor(int color) {
        if (color >= 0 && mStokeColor != color) {
            mStokeColor = color;
            invalidateSelf();
        }
    }

    /**
     * 设置描边线宽
     *
     * @param size 描边线宽
     */
    public void setStokeSize(int size) {
        if (size >= 0 && mStokeSize != size) {
            mStokeSize = size;
            invalidateSelf();
        }
    }
}
