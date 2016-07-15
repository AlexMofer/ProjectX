package am.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

/**
 * 尖角框
 * 注意：使用该Drawable时，会改变View的Padding值。
 */
public class CornerDrawable extends Drawable {

    private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mStrokePaint;// optional, set by the caller
    private final Rect mPaddingRect = new Rect();
    private final Path mPath = new Path();
    private final RectF mRoundRect = new RectF();
    private int mAlpha = 0xFF;  // modified by the caller
    private int mColor;// 颜色
    private int mCornerWidth;// 尖角宽
    private int mCornerHeight;// 尖角高
    private boolean mCornerBezier;// 是否贝塞尔尖角
    private int mDirection = Gravity.TOP;//朝向
    private int mLocation = Gravity.CENTER;// 位置
    private int mCornerMargin;//尖角边距
    private final Rect mContentPaddingRect = new Rect();// 内容间隔
    private float mContentRadius;// 内容圆角半径

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    private RectF mTempRect = new RectF();

    public CornerDrawable(int color, int cornerWidth, int cornerHeight) {
        this(color, cornerWidth, cornerHeight, false, Gravity.TOP, Gravity.CENTER, 0);
    }

    public CornerDrawable(int color, int cornerWidth, int cornerHeight, boolean cornerBezier,
                          int direction, int location, float contentRadius) {
        setColor(color);
        setCornerWidth(cornerWidth);
        setCornerHeight(cornerHeight);
        setCornerBezier(cornerBezier);
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
        // remember the alpha values, in case we temporarily overwrite them
        // when we modulate them with mAlpha
        final int prevFillAlpha = mFillPaint.getAlpha();
        final int prevStrokeAlpha = mStrokePaint != null ? mStrokePaint.getAlpha() : 0;
        // compute the modulate alpha values
        final int currFillAlpha = modulateAlpha(prevFillAlpha);
        final int currStrokeAlpha = modulateAlpha(prevStrokeAlpha);

        final boolean haveStroke = currStrokeAlpha > 0 && mStrokePaint != null &&
                mStrokePaint.getStrokeWidth() > 0;

        final boolean haveFill = currFillAlpha > 0;
        if (!haveFill && !haveStroke) {
            return;
        }
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
            final int width = getBounds().width();
            final int height = getBounds().height();
            if (mContentRadius == 0) {
                switch (mDirection) {
                    case Gravity.TOP:
                    case Gravity.BOTTOM:
                        if (height <= mCornerHeight) {
                            makeTrianglePath();
                        } else if (width <= mCornerWidth) {
                            makeTriangleRectPath();
                        } else {
                            makeRectPath();
                        }
                        break;
                    case Gravity.LEFT:
                    case Gravity.RIGHT:
                        if (width <= mCornerHeight) {
                            makeTrianglePath();
                        } else if (height <= mCornerWidth) {
                            makeTriangleRectPath();
                        } else {
                            makeRectPath();
                        }
                        break;
                }
            } else {
                switch (mDirection) {
                    case Gravity.TOP:
                    case Gravity.BOTTOM:
                        if (width > mCornerWidth + mContentRadius * 2 && height > mCornerHeight + mContentRadius * 2) {
                            makeRoundRect();
                        } else if (width >= mCornerWidth + mContentRadius * 2 && height > mCornerHeight) {
                            makeTriangleRoundRectPath();
                        } else if (width >= mCornerWidth + mContentRadius * 2) {
                            makeTrianglePath();
                        } else if (width < mCornerWidth + mContentRadius * 2 && width > mCornerWidth) {
                            makeRoundRectResizePath();
                        } else if (width == mCornerWidth) {
                            makeTriangleSemicirclePath();
                        } else {
                            makeTrianglePath();
                        }
                        break;
                    case Gravity.LEFT:
                    case Gravity.RIGHT:
                        if (height > mCornerWidth + mContentRadius * 2 && width > mCornerHeight + mContentRadius * 2) {
                            makeRoundRect();
                        } else if (height >= mCornerWidth + mContentRadius * 2 && width > mCornerHeight) {
                            makeTriangleRoundRectPath();
                        } else if (height >= mCornerWidth + mContentRadius * 2) {
                            makeTrianglePath();
                        } else if (height < mCornerWidth + mContentRadius * 2 && height > mCornerWidth) {
                            makeRoundRectResizePath();
                        } else if (height == mCornerWidth) {
                            makeTriangleSemicirclePath();
                        } else {
                            makeTrianglePath();
                        }
                        break;
                }
            }
        }
        if (haveFill) {
            mFillPaint.setAlpha(currFillAlpha);
            mFillPaint.setColor(mColor);
            canvas.drawPath(mPath, mFillPaint);
        }
        if (haveStroke) {
            mStrokePaint.setAlpha(currStrokeAlpha);
            canvas.drawPath(mPath, mStrokePaint);
        }
    }

    private int modulateAlpha(int alpha) {
        int scale = mAlpha + (mAlpha >> 7);
        return alpha * scale >> 8;
    }

    /**
     * 矩形
     */
    @SuppressWarnings("all")
    private void makeRectPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeWidth = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeWidth * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final double halfCornerWidthD = mCornerWidth * 0.5d;
        final float cornerStokeSize = (float) (Math.sqrt(halfCornerWidthD * halfCornerWidthD
                + mCornerHeight * mCornerHeight) / halfCornerWidthD * (strokeWidth * 0.5d));
        float cornerOffset = 0;
        switch (mDirection) {
            case Gravity.TOP:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(width - strokeWidth) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > width - strokeWidth - mCornerWidth) {
                            cornerOffset = (width - strokeWidth) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(width - strokeWidth) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (width - strokeWidth) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > width - strokeWidth - mCornerWidth) {
                            cornerOffset = -(width - strokeWidth) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (width - strokeWidth) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize, mCornerHeight + cornerStokeSize);
                mPath.lineTo((width - mCornerWidth) * 0.5f + cornerOffset, mCornerHeight + cornerStokeSize);
                mPath.lineTo(width * 0.5f + cornerOffset, cornerStokeSize);
                mPath.lineTo((width + mCornerWidth) * 0.5f + cornerOffset, mCornerHeight + cornerStokeSize);
                mPath.lineTo(width - halfStokeSize, mCornerHeight + cornerStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (width - strokeWidth) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > width - strokeWidth - mCornerWidth) {
                            cornerOffset = -(width - strokeWidth) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (width - strokeWidth) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(width - strokeWidth) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > width - strokeWidth - mCornerWidth) {
                            cornerOffset = (width - strokeWidth) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(width - strokeWidth) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - cornerStokeSize - mCornerHeight);
                mPath.lineTo((width + mCornerWidth) * 0.5f + cornerOffset, height - cornerStokeSize - mCornerHeight);
                mPath.lineTo(width * 0.5f + cornerOffset, height - cornerStokeSize);
                mPath.lineTo((width - mCornerWidth) * 0.5f + cornerOffset, height - cornerStokeSize - mCornerHeight);
                mPath.lineTo(halfStokeSize, height - cornerStokeSize - mCornerHeight);
                mPath.close();
                break;
            case Gravity.LEFT:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (height - strokeWidth) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > height - strokeWidth - mCornerWidth) {
                            cornerOffset = -(height - strokeWidth) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (height - strokeWidth) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(height - strokeWidth) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > height - strokeWidth - mCornerWidth) {
                            cornerOffset = (height - strokeWidth) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(height - strokeWidth) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(mCornerHeight + cornerStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(mCornerHeight + cornerStokeSize, height - halfStokeSize);
                mPath.lineTo(mCornerHeight + cornerStokeSize, (height + mCornerWidth) * 0.5f + cornerOffset);
                mPath.lineTo(cornerStokeSize, height * 0.5f + cornerOffset);
                mPath.lineTo(mCornerHeight + cornerStokeSize, (height - mCornerWidth) * 0.5f + cornerOffset);
                mPath.close();
                break;
            case Gravity.RIGHT:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(height - strokeWidth) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > height - strokeWidth - mCornerWidth) {
                            cornerOffset = (height - strokeWidth) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(height - strokeWidth) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (height - strokeWidth) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > height - strokeWidth - mCornerWidth) {
                            cornerOffset = -(height - strokeWidth) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (height - strokeWidth) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - mCornerHeight - cornerStokeSize, halfStokeSize);
                mPath.lineTo(width - mCornerHeight - cornerStokeSize, (height - mCornerWidth) * 0.5f + cornerOffset);
                mPath.lineTo(width - cornerStokeSize, height * 0.5f + cornerOffset);
                mPath.lineTo(width - mCornerHeight - cornerStokeSize, (height + mCornerWidth) * 0.5f + cornerOffset);
                mPath.lineTo(width - mCornerHeight - cornerStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
        }
    }

    /**
     * 圆角矩形
     */
    private void makeRoundRect() {
        // TODO
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeWidth = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeWidth * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final double halfCornerWidthD = mCornerWidth * 0.5d;
        final float cornerStokeSize = (float) (Math.sqrt(halfCornerWidthD * halfCornerWidthD
                + mCornerHeight * mCornerHeight) / halfCornerWidthD * (strokeWidth * 0.5d));
        float cornerOffset = 0;
        switch (mDirection) {
            case Gravity.TOP:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        break;
                    case Gravity.RIGHT:
                        break;
                }
                mPath.moveTo(halfStokeSize + mContentRadius, mCornerHeight + cornerStokeSize);
                mPath.lineTo((width - mCornerWidth) * 0.5f + cornerOffset, mCornerHeight + cornerStokeSize);
                mPath.lineTo(width * 0.5f + cornerOffset, cornerStokeSize);
                mPath.lineTo((width + mCornerWidth) * 0.5f + cornerOffset, mCornerHeight + cornerStokeSize);
                mPath.lineTo(width - halfStokeSize - mContentRadius, mCornerHeight + cornerStokeSize);
                mRoundRect.set(width - halfStokeSize - mContentRadius, mCornerHeight + cornerStokeSize, width - halfStokeSize, mCornerHeight + cornerStokeSize + mContentRadius);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize - mContentRadius);
                mRoundRect.set(width - halfStokeSize - mContentRadius, height - halfStokeSize - mContentRadius, width - halfStokeSize, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(halfStokeSize + mContentRadius, height - halfStokeSize);
                mRoundRect.set(halfStokeSize, height - halfStokeSize - mContentRadius, halfStokeSize + mContentRadius, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, cornerStokeSize + mCornerHeight + mContentRadius);
                mRoundRect.set(halfStokeSize, cornerStokeSize + mCornerHeight, halfStokeSize + mContentRadius, cornerStokeSize + mCornerHeight + mContentRadius);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        break;
                    case Gravity.RIGHT:
                        break;
                }
                break;
            case Gravity.LEFT:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        break;
                    case Gravity.RIGHT:
                        break;
                }
                break;
            case Gravity.RIGHT:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        break;
                    case Gravity.RIGHT:
                        break;
                }
                break;
        }
    }

    /**
     * 三角形
     */
    private void makeTrianglePath() {
        // TODO

    }

    /**
     * 矩形（三角形宽度等于矩形连接边）
     */
    private void makeTriangleRectPath() {
        // TODO

    }

    /**
     * 圆角矩形（三角形宽度加圆角直径小于等于连接边）
     */
    private void makeTriangleRoundRectPath() {
        // TODO

    }

    /**
     * 圆角矩形（圆角半径重新计算）
     */
    private void makeRoundRectResizePath() {
        // TODO
    }

    /**
     * 半圆
     */
    private void makeTriangleSemicirclePath() {
        // TODO

    }


    @Override
    public void setAlpha(int alpha) {
        if (alpha != mAlpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mFillPaint.setColorFilter(cf);
        if (mStrokePaint != null)
            mStrokePaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mAlpha == 255 ? PixelFormat.OPAQUE : PixelFormat.TRANSLUCENT;
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
     */
    @SuppressWarnings("all")
    private void calculateValue() {
        if (mCornerHeight <= 0) {
            mPaddingRect.set(mContentPaddingRect.left,
                    mContentPaddingRect.top,
                    mContentPaddingRect.right,
                    mContentPaddingRect.bottom);
            mIntrinsicWidth = 0;
            mIntrinsicHeight = 0;
        } else {
            switch (mDirection) {
                case Gravity.LEFT:
                    mPaddingRect.set(mContentPaddingRect.left + mCornerHeight,
                            mContentPaddingRect.top,
                            mContentPaddingRect.right,
                            mContentPaddingRect.bottom);
                    mIntrinsicWidth = mCornerHeight;
                    mIntrinsicHeight = mCornerWidth;
                    break;
                case Gravity.RIGHT:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top,
                            mContentPaddingRect.right + mCornerHeight,
                            mContentPaddingRect.bottom);
                    mIntrinsicWidth = mCornerHeight;
                    mIntrinsicHeight = mCornerWidth;
                    break;
                case Gravity.BOTTOM:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top,
                            mContentPaddingRect.right,
                            mContentPaddingRect.bottom + mCornerHeight);
                    mIntrinsicWidth = mCornerWidth;
                    mIntrinsicHeight = mCornerHeight;
                    break;
                default:
                case Gravity.TOP:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top + mCornerHeight,
                            mContentPaddingRect.right,
                            mContentPaddingRect.bottom);
                    mIntrinsicWidth = mCornerWidth;
                    mIntrinsicHeight = mCornerHeight;
                    break;
            }

        }
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
     * 设置是否为贝塞尔尖角
     *
     * @param cornerBezier 是否为贝塞尔尖角
     */
    public void setCornerBezier(boolean cornerBezier) {
        if (mCornerBezier != cornerBezier) {
            mCornerBezier = cornerBezier;
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
     * 设置描边
     *
     * @param width     描边线宽
     * @param color     描边颜色
     * @param dashWidth 虚线线宽
     * @param dashGap   虚线间隔
     */
    public void setStroke(int width, int color, float dashWidth, float dashGap) {
        if (mStrokePaint == null) {
            mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mStrokePaint.setStyle(Paint.Style.STROKE);
        }
        mStrokePaint.setStrokeWidth(width);
        mStrokePaint.setColor(color);

        DashPathEffect e = null;
        if (dashWidth > 0) {
            e = new DashPathEffect(new float[]{dashWidth, dashGap}, 0);
        }
        mStrokePaint.setPathEffect(e);
        invalidateSelf();
    }
}
