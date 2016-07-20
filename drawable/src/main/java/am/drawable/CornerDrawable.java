package am.drawable;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
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
    private PointF mCornerLeft = new PointF();
    private PointF mCornerCenter = new PointF();
    private PointF mCornerRight = new PointF();

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
        mFillPaint.setColor(0x8000ff00);
        Rect rect = new Rect();
        rect.set(0, 0, getBounds().width(), mCornerHeight);
        canvas.drawRect(rect, mFillPaint);
        rect.set(0, getBounds().height() - mCornerHeight, getBounds().width(), getBounds().height());
        canvas.drawRect(rect, mFillPaint);
        rect.set(0, 0, mCornerHeight, getBounds().height());
        canvas.drawRect(rect, mFillPaint);
        rect.set(getBounds().width() - mCornerHeight, 0, getBounds().width(), getBounds().height());
        canvas.drawRect(rect, mFillPaint);

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

    private void makeCorner(float leftX, float leftY, float centerX, float centerY, float rightX, float rightY) {
        mPath.lineTo(leftX, leftY);
        mPath.lineTo(centerX, centerY);
        mPath.lineTo(rightX, rightY);
    }


    /**
     * 三角形
     */
    @SuppressWarnings("all")
    private void makeTrianglePath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ?
                (width > this.mCornerWidth ? this.mCornerWidth : width) :
                (height > this.mCornerWidth ? this.mCornerWidth : height);
        float margin;
        switch (mDirection) {
            case Gravity.TOP:
                if (width > mCornerWidth) {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            mCornerLeft.set((width - mCornerWidth) * 0.5f, height);
                            mCornerCenter.set(width * 0.5f, 0);
                            mCornerRight.set((width + mCornerWidth) * 0.5f, height);
                            break;
                        case Gravity.LEFT:
                            mCornerLeft.set(margin, height);
                            mCornerCenter.set(mCornerWidth * 0.5f + margin, 0);
                            mCornerRight.set(mCornerWidth + margin, height);
                            break;
                        case Gravity.RIGHT:
                            mCornerLeft.set(width - mCornerWidth - margin, height);
                            mCornerCenter.set(width - mCornerWidth * 0.5f - margin, 0);
                            mCornerRight.set(width - margin, height);
                            break;
                    }
                } else {
                    mCornerLeft.set(0, height);
                    mCornerCenter.set(width * 0.5f, 0);
                    mCornerRight.set(width, height);
                }
                makeTriangleCornerPath();
                break;
            case Gravity.BOTTOM:
                if (width > mCornerWidth) {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            mCornerLeft.set((width + mCornerWidth) * 0.5f, 0);
                            mCornerCenter.set(width * 0.5f, height);
                            mCornerRight.set((width - mCornerWidth) * 0.5f, 0);
                            break;
                        case Gravity.LEFT:
                            mCornerLeft.set(width - margin, 0);
                            mCornerCenter.set(width - mCornerWidth * 0.5f - margin, height);
                            mCornerRight.set(width - mCornerWidth - margin, 0);
                            break;
                        case Gravity.RIGHT:
                            mCornerLeft.set(margin, 0);
                            mCornerCenter.set(mCornerWidth * 0.5f + margin, height);
                            mCornerRight.set(mCornerWidth + margin, 0);
                            break;
                    }
                } else {
                    mCornerLeft.set(width, 0);
                    mCornerCenter.set(width * 0.5f, height);
                    mCornerRight.set(0, 0);
                }
                makeTriangleCornerPath();
                break;
            case Gravity.LEFT:
                if (height > mCornerWidth) {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            mCornerLeft.set(width, (height + mCornerWidth) * 0.5f);
                            mCornerCenter.set(0, height * 0.5f);
                            mCornerRight.set(width, (height - mCornerWidth) * 0.5f);
                            break;
                        case Gravity.LEFT:
                            mCornerLeft.set(width, height - margin);
                            mCornerCenter.set(0, height - mCornerWidth * 0.5f - margin);
                            mCornerRight.set(width, height - mCornerWidth - margin);
                            break;
                        case Gravity.RIGHT:
                            mCornerLeft.set(width, mCornerWidth + margin);
                            mCornerCenter.set(0, mCornerWidth * 0.5f + margin);
                            mCornerRight.set(width, margin);
                            break;
                    }
                } else {
                    mCornerLeft.set(width, height);
                    mCornerCenter.set(0, height * 0.5f);
                    mCornerRight.set(width, 0);
                }
                makeTriangleCornerPath();
                break;
            case Gravity.RIGHT:
                if (height > mCornerWidth) {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            mCornerLeft.set(0, (height - mCornerWidth) * 0.5f);
                            mCornerCenter.set(width, height * 0.5f);
                            mCornerRight.set(0, (height + mCornerWidth) * 0.5f);
                            break;
                        case Gravity.LEFT:
                            mCornerLeft.set(0, margin);
                            mCornerCenter.set(width, mCornerWidth * 0.5f + margin);
                            mCornerRight.set(0, mCornerWidth + margin);
                            break;
                        case Gravity.RIGHT:
                            mCornerLeft.set(0, height - mCornerWidth - margin);
                            mCornerCenter.set(width, height - mCornerWidth * 0.5f - margin);
                            mCornerRight.set(0, height - margin);
                            break;
                    }
                } else {
                    mCornerLeft.set(0, 0);
                    mCornerCenter.set(width, height * 0.5f);
                    mCornerRight.set(0, height);
                }
                makeTriangleCornerPath();
                break;
        }
    }

    @SuppressWarnings("all")
    private void makeTriangleCornerPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ?
                (width > this.mCornerWidth ? this.mCornerWidth : width) :
                (height > this.mCornerWidth ? this.mCornerWidth : height);
        float mCornerHeight = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ? height : width;
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final double temp = 2d * mCornerHeight * halfStokeSize / mCornerWidth;
        final float cornerStokeVertical = (float) (Math.sqrt(halfStokeSize * halfStokeSize + temp * temp));
        final float cornerStokeHorizontal = halfCornerWidth - mCornerWidth * (mCornerHeight - cornerStokeVertical - halfStokeSize) * 0.5f / mCornerHeight;
        switch (mDirection) {
            case Gravity.TOP:
                mPath.moveTo(mCornerLeft.x + cornerStokeHorizontal, mCornerLeft.y - halfStokeSize);
                mPath.lineTo(mCornerCenter.x, mCornerCenter.y + cornerStokeVertical);
                mPath.lineTo(mCornerRight.x - cornerStokeHorizontal, mCornerRight.y - halfStokeSize);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                mPath.moveTo(mCornerLeft.x - cornerStokeHorizontal, mCornerLeft.y + halfStokeSize);
                mPath.lineTo(mCornerCenter.x, mCornerCenter.y - cornerStokeVertical);
                mPath.lineTo(mCornerRight.x + cornerStokeHorizontal, mCornerRight.y + halfStokeSize);
                mPath.close();
                break;
            case Gravity.LEFT:
                mPath.moveTo(mCornerLeft.x - halfStokeSize, mCornerLeft.y - cornerStokeHorizontal);
                mPath.lineTo(mCornerCenter.x + cornerStokeVertical, mCornerCenter.y);
                mPath.lineTo(mCornerRight.x - halfStokeSize, mCornerRight.y + cornerStokeHorizontal);
                mPath.close();
                break;
            case Gravity.RIGHT:
                mPath.moveTo(mCornerLeft.x + halfStokeSize, mCornerLeft.y + cornerStokeHorizontal);
                mPath.lineTo(mCornerCenter.x - cornerStokeVertical, mCornerCenter.y);
                mPath.lineTo(mCornerRight.x + halfStokeSize, mCornerRight.y - cornerStokeHorizontal);
                mPath.close();
                break;
        }
    }

    /**
     * 矩形（三角形宽度小于等于矩形连接边）
     */
    @SuppressWarnings("all")
    private void makeTriangleRectPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ? width : height;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        final float cornerXOffset = halfStokeSize;
        final float cornerYOffset = mCornerHeight * (halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical) / mCornerHeight - halfStokeSize) / halfCornerWidth;

        switch (mDirection) {
            case Gravity.TOP:
                mPath.moveTo(cornerXOffset, mCornerHeight + cornerYOffset);
                makeCorner(cornerXOffset, mCornerHeight + cornerYOffset,
                        width * 0.5f, cornerStokeVertical,
                        width - cornerXOffset, mCornerHeight + cornerYOffset);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                mPath.moveTo(width - cornerXOffset, height - mCornerHeight - cornerYOffset);
                makeCorner(width - cornerXOffset, height - mCornerHeight - cornerYOffset,
                        width * 0.5f, height - cornerStokeVertical,
                        cornerXOffset, height - mCornerHeight - cornerYOffset);
                mPath.lineTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.close();
                break;
            case Gravity.LEFT:
                mPath.moveTo(mCornerHeight + cornerYOffset, height - cornerXOffset);
                makeCorner(mCornerHeight + cornerYOffset, height - cornerXOffset,
                        cornerStokeVertical, height * 0.5f,
                        mCornerHeight + cornerYOffset, cornerXOffset);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.RIGHT:
                mPath.moveTo(width - mCornerHeight - cornerYOffset, cornerXOffset);
                makeCorner(width - mCornerHeight - cornerYOffset, cornerXOffset,
                        width - cornerStokeVertical, height * 0.5f,
                        width - mCornerHeight - cornerYOffset, height - cornerXOffset);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, halfStokeSize);
                mPath.close();
                break;
        }
    }

    /**
     * 矩形
     */
    @SuppressWarnings("all")
    private void makeRectPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        float margin;
        switch (mDirection) {
            case Gravity.TOP:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                mPath.moveTo(halfStokeSize, mCornerHeight + halfStokeSize);
                mCornerLeft.set((width - mCornerWidth) * 0.5f, mCornerHeight);
                mCornerCenter.set(width * 0.5f, 0);
                mCornerRight.set((width + mCornerWidth) * 0.5f, mCornerHeight);
                makeRectCornerPath(margin);
                mPath.lineTo(width - halfStokeSize, mCornerHeight + halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.RIGHT:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - mCornerHeight - halfStokeSize, halfStokeSize);
                mCornerLeft.set(width - mCornerHeight, (height - mCornerWidth) * 0.5f);
                mCornerCenter.set(width, height * 0.5f);
                mCornerRight.set(width - mCornerHeight, (height + mCornerWidth) * 0.5f);
                makeRectCornerPath(margin);
                mPath.lineTo(width - mCornerHeight - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - mCornerHeight - halfStokeSize);
                mCornerLeft.set((width + mCornerWidth) * 0.5f, height - mCornerHeight);
                mCornerCenter.set(width * 0.5f, height);
                mCornerRight.set((width - mCornerWidth) * 0.5f, height - mCornerHeight);
                makeRectCornerPath(margin);
                mPath.lineTo(halfStokeSize, height - mCornerHeight - halfStokeSize);
                mPath.close();
                break;
            case Gravity.LEFT:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                mPath.moveTo(mCornerHeight + halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(mCornerHeight + halfStokeSize, height - halfStokeSize);
                mCornerLeft.set(mCornerHeight, (height + mCornerWidth) * 0.5f);
                mCornerCenter.set(0, height * 0.5f);
                mCornerRight.set(mCornerHeight, (height - mCornerWidth) * 0.5f);
                makeRectCornerPath(margin);
                mPath.close();
                break;
        }
    }

    private void makeRectCornerPath(float margin) {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        float cornerXOffset = halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical + halfStokeSize) / mCornerHeight;
        float cornerYOffset = halfStokeSize;
        float cornerEdgeOffset;
        
    }

    /**
     * 矩形
     */
    // TODO
    private void makeRectPathOld() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        float cornerXOffset = halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical + halfStokeSize) / mCornerHeight;
        float cornerYOffset = halfStokeSize;

        float cornerOffset = 0;
        switch (mDirection) {
            case Gravity.TOP:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize, mCornerHeight + halfStokeSize);
                makeCorner((width - mCornerWidth) * 0.5f + cornerXOffset + cornerOffset, mCornerHeight + cornerYOffset,
                        width * 0.5f + cornerOffset, cornerStokeVertical,
                        (width + mCornerWidth) * 0.5f - cornerXOffset + cornerOffset, mCornerHeight + cornerYOffset);
                mPath.lineTo(width - halfStokeSize, mCornerHeight + halfStokeSize);
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
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - mCornerHeight - halfStokeSize);
                makeCorner((width + mCornerWidth) * 0.5f - cornerXOffset + cornerOffset, height - mCornerHeight - cornerYOffset,
                        width * 0.5f + cornerOffset, height - cornerStokeVertical,
                        (width - mCornerWidth) * 0.5f + cornerXOffset + cornerOffset, height - mCornerHeight - cornerYOffset);
                mPath.lineTo(halfStokeSize, height - mCornerHeight - halfStokeSize);
                mPath.close();
                break;
            case Gravity.LEFT:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(mCornerHeight + halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(mCornerHeight + halfStokeSize, height - halfStokeSize);
                makeCorner(mCornerHeight + cornerYOffset, (height + mCornerWidth) * 0.5f - cornerXOffset + cornerOffset,
                        cornerStokeVertical, height * 0.5f + cornerOffset,
                        mCornerHeight + cornerYOffset, (height - mCornerWidth) * 0.5f + cornerXOffset + cornerOffset);
                mPath.close();
                break;
            case Gravity.RIGHT:
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth;
                        } else {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth;
                        } else {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - mCornerHeight - halfStokeSize, halfStokeSize);
                makeCorner(width - mCornerHeight - cornerYOffset, (height - mCornerWidth) * 0.5f + cornerXOffset + cornerOffset,
                        width - cornerStokeVertical, height * 0.5f + cornerOffset,
                        width - mCornerHeight - cornerYOffset, (height + mCornerWidth) * 0.5f - cornerXOffset + cornerOffset);
                mPath.lineTo(width - mCornerHeight - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
        }
    }

    /**
     * 圆角矩形
     */
    // TODO
    private void makeRoundRect() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final double temp = 2d * mCornerHeight * halfStokeSize / mCornerWidth;
        final float cornerStokeVertical = (float) (Math.sqrt(halfStokeSize * halfStokeSize + temp * temp));
        float cornerOffset = 0;
        float margin;
        switch (mDirection) {

            case Gravity.TOP:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin + mContentRadius;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin - mContentRadius;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize + mContentRadius, mCornerHeight + cornerStokeVertical);
                makeCorner((width - mCornerWidth) * 0.5f + cornerOffset, mCornerHeight + cornerStokeVertical,
                        width * 0.5f + cornerOffset, cornerStokeVertical,
                        (width + mCornerWidth) * 0.5f + cornerOffset, mCornerHeight + cornerStokeVertical);
                mPath.lineTo(width - halfStokeSize - mContentRadius, mCornerHeight + cornerStokeVertical);
                mRoundRect.set(width - halfStokeSize - mContentRadius * 2, mCornerHeight + cornerStokeVertical,
                        width - halfStokeSize, mCornerHeight + cornerStokeVertical + mContentRadius * 2);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize - mContentRadius);
                mRoundRect.set(width - halfStokeSize - mContentRadius * 2,
                        height - halfStokeSize - mContentRadius * 2, width - halfStokeSize, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(halfStokeSize + mContentRadius * 2, height - halfStokeSize);
                mRoundRect.set(halfStokeSize, height - halfStokeSize - mContentRadius * 2,
                        halfStokeSize + mContentRadius * 2, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, cornerStokeVertical + mCornerHeight + mContentRadius);
                mRoundRect.set(halfStokeSize, cornerStokeVertical + mCornerHeight,
                        halfStokeSize + mContentRadius * 2, cornerStokeVertical + mCornerHeight + mContentRadius * 2);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin - mContentRadius;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else if (mCornerMargin > width - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = (width - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else {
                            cornerOffset = -(width - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin + mContentRadius;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize + mContentRadius, halfStokeSize);
                mPath.lineTo(width - halfStokeSize - mContentRadius, halfStokeSize);
                mRoundRect.set(width - halfStokeSize - mContentRadius * 2, halfStokeSize,
                        width - halfStokeSize, halfStokeSize + mContentRadius * 2);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - cornerStokeVertical - mCornerHeight - mContentRadius);
                mRoundRect.set(width - halfStokeSize - mContentRadius * 2, height - cornerStokeVertical - mCornerHeight - mContentRadius * 2,
                        width - halfStokeSize, height - cornerStokeVertical - mCornerHeight);
                mPath.arcTo(mRoundRect, 0, 90);
                makeCorner((width + mCornerWidth) * 0.5f + cornerOffset, height - cornerStokeVertical - mCornerHeight,
                        width * 0.5f + cornerOffset, height - cornerStokeVertical,
                        (width - mCornerWidth) * 0.5f + cornerOffset, height - cornerStokeVertical - mCornerHeight);
                mPath.lineTo(halfStokeSize + mContentRadius, height - cornerStokeVertical - mCornerHeight);
                mRoundRect.set(halfStokeSize, height - cornerStokeVertical - mCornerHeight - mContentRadius * 2,
                        halfStokeSize + mContentRadius * 2, height - cornerStokeVertical - mCornerHeight);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, halfStokeSize + mContentRadius);
                mRoundRect.set(halfStokeSize, halfStokeSize, halfStokeSize + mContentRadius * 2, halfStokeSize + mContentRadius * 2);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.close();
                break;
            case Gravity.LEFT:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin - mContentRadius;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin + mContentRadius;
                        }
                        break;
                }
                mPath.moveTo(mCornerHeight + cornerStokeVertical + mContentRadius, halfStokeSize);
                mPath.lineTo(width - halfStokeSize - mContentRadius, halfStokeSize);
                mRoundRect.set(width - halfStokeSize - mContentRadius * 2, halfStokeSize,
                        width - halfStokeSize, halfStokeSize + mContentRadius * 2);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize - mContentRadius);
                mRoundRect.set(width - halfStokeSize - mContentRadius * 2, height - halfStokeSize - mContentRadius * 2,
                        width - halfStokeSize, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(mCornerHeight + cornerStokeVertical + mContentRadius, height - halfStokeSize);
                mRoundRect.set(mCornerHeight + cornerStokeVertical, height - halfStokeSize - mContentRadius * 2,
                        mCornerHeight + cornerStokeVertical + mContentRadius * 2, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                makeCorner(mCornerHeight + cornerStokeVertical, (height + mCornerWidth) * 0.5f + cornerOffset,
                        cornerStokeVertical, height * 0.5f + cornerOffset,
                        mCornerHeight + cornerStokeVertical, (height - mCornerWidth) * 0.5f + cornerOffset);
                mPath.lineTo(mCornerHeight + cornerStokeVertical, halfStokeSize + mContentRadius);
                mRoundRect.set(mCornerHeight + cornerStokeVertical, halfStokeSize,
                        mCornerHeight + cornerStokeVertical + mContentRadius * 2, halfStokeSize + mContentRadius * 2);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.close();
                break;
            case Gravity.RIGHT:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mCornerMargin + mContentRadius;
                        }
                        break;
                    case Gravity.RIGHT:
                        if (mCornerMargin <= 0) {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mContentRadius;
                        } else if (mCornerMargin > height - strokeSize - mCornerWidth - mContentRadius * 2) {
                            cornerOffset = -(height - strokeSize) * 0.5f + halfCornerWidth + mContentRadius;
                        } else {
                            cornerOffset = (height - strokeSize) * 0.5f - halfCornerWidth - mCornerMargin - mContentRadius;
                        }
                        break;
                }
                mPath.moveTo(halfStokeSize + mContentRadius, halfStokeSize);
                mPath.lineTo(width - mCornerHeight - cornerStokeVertical - mContentRadius, halfStokeSize);
                mRoundRect.set(width - mCornerHeight - cornerStokeVertical - mContentRadius * 2, halfStokeSize,
                        width - mCornerHeight - cornerStokeVertical, halfStokeSize + mContentRadius * 2);
                mPath.arcTo(mRoundRect, -90, 90);
                makeCorner(width - mCornerHeight - cornerStokeVertical, (height - mCornerWidth) * 0.5f + cornerOffset,
                        width - cornerStokeVertical, height * 0.5f + cornerOffset,
                        width - mCornerHeight - cornerStokeVertical, (height + mCornerWidth) * 0.5f + cornerOffset);
                mPath.lineTo(width - mCornerHeight - cornerStokeVertical, height - halfStokeSize - mContentRadius);
                mRoundRect.set(width - mCornerHeight - cornerStokeVertical - mContentRadius * 2, height - halfStokeSize - mContentRadius * 2,
                        width - mCornerHeight - cornerStokeVertical, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(halfStokeSize + mContentRadius * 2, height - halfStokeSize);
                mRoundRect.set(halfStokeSize, height - halfStokeSize - mContentRadius * 2,
                        halfStokeSize + mContentRadius * 2, height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, halfStokeSize + mContentRadius);
                mRoundRect.set(halfStokeSize, halfStokeSize, halfStokeSize + mContentRadius * 2, halfStokeSize + mContentRadius * 2);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.close();
                break;
        }
    }

    /**
     * 圆角矩形（三角形宽度加圆角直径小于等于连接边）
     */
    private void makeTriangleRoundRectPath() {
        // TODO
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
     * 圆角矩形（圆角半径重新计算）
     */
    private void makeRoundRectResizePath() {
        // TODO
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
     * 半圆
     */
    private void makeTriangleSemicirclePath() {
        // TODO
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
