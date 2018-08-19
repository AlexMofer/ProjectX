/*
 * Copyright (C) 2015 AlexMofer
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

package am.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

/**
 * 尖角框
 * 注意：使用该Drawable时，会改变View的Padding值。
 */
@SuppressWarnings("unused")
public class CornerDrawable extends Drawable {

    private final Paint mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect mPaddingRect = new Rect();
    private final Path mPath = new Path();
    private final RectF mRoundRect = new RectF();
    private final Rect mContentPaddingRect = new Rect();// 内容间隔
    private Paint mStrokePaint;// optional, set by the caller
    private int mAlpha = 0xFF;  // modified by the caller
    private int mColor;// 颜色
    private int mCornerWidth;// 尖角宽
    private int mCornerHeight;// 尖角高
    private float mCornerBezier;// 是否贝塞尔尖角
    private int mDirection = Gravity.TOP;//朝向
    private int mLocation = Gravity.CENTER;// 位置
    private int mCornerMargin;//尖角边距
    private float mContentRadius;// 内容圆角半径

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    private RectF mTempRect = new RectF();
    private PointF mCornerLeft = new PointF();
    private PointF mCornerCenter = new PointF();
    private PointF mCornerRight = new PointF();

    public CornerDrawable(int color, int cornerWidth, int cornerHeight) {
        this(color, cornerWidth, cornerHeight, 0, Gravity.TOP, Gravity.CENTER, 0);
    }

    public CornerDrawable(int color, int cornerWidth, int cornerHeight, float cornerBezier,
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
    @SuppressWarnings("all")
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
        final float halfStokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() * 0.5f : 0;
        float mContentRadius = this.mContentRadius > halfStokeSize ? this.mContentRadius : 0;
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
                        if (height <= mCornerHeight) {
                            makeTrianglePath();
                        } else {
                            makeRoundRect();
                        }
                        break;
                    case Gravity.LEFT:
                    case Gravity.RIGHT:
                        if (width <= mCornerHeight) {
                            makeTrianglePath();
                        } else {
                            makeRoundRect();
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
                mPath.close();
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
                mPath.close();
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
                mPath.close();
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
                mPath.close();
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
                makeCornerPath(mCornerLeft.x + cornerStokeHorizontal, mCornerLeft.y - halfStokeSize,
                        mCornerCenter.x, mCornerCenter.y + cornerStokeVertical,
                        mCornerRight.x - cornerStokeHorizontal, mCornerRight.y - halfStokeSize);
                break;
            case Gravity.BOTTOM:
                mPath.moveTo(mCornerLeft.x - cornerStokeHorizontal, mCornerLeft.y + halfStokeSize);
                makeCornerPath(mCornerLeft.x - cornerStokeHorizontal, mCornerLeft.y + halfStokeSize,
                        mCornerCenter.x, mCornerCenter.y - cornerStokeVertical,
                        mCornerRight.x + cornerStokeHorizontal, mCornerRight.y + halfStokeSize);
                break;
            case Gravity.LEFT:
                mPath.moveTo(mCornerLeft.x - halfStokeSize, mCornerLeft.y - cornerStokeHorizontal);
                makeCornerPath(mCornerLeft.x - halfStokeSize, mCornerLeft.y - cornerStokeHorizontal,
                        mCornerCenter.x + cornerStokeVertical, mCornerCenter.y,
                        mCornerRight.x - halfStokeSize, mCornerRight.y + cornerStokeHorizontal);
                break;
            case Gravity.RIGHT:
                mPath.moveTo(mCornerLeft.x + halfStokeSize, mCornerLeft.y + cornerStokeHorizontal);
                makeCornerPath(mCornerLeft.x + halfStokeSize, mCornerLeft.y + cornerStokeHorizontal,
                        mCornerCenter.x - cornerStokeVertical, mCornerCenter.y,
                        mCornerRight.x + halfStokeSize, mCornerRight.y - cornerStokeHorizontal);
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
        final float halfStokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() * 0.5f : 0;
        switch (mDirection) {
            case Gravity.TOP:
                mPath.moveTo(halfStokeSize, mCornerHeight + halfStokeSize);
                mCornerLeft.set(0, mCornerHeight);
                mCornerCenter.set(width * 0.5f, 0);
                mCornerRight.set(width, mCornerHeight);
                makeTriangleRectCornerPath();
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                mPath.moveTo(width - halfStokeSize, height - mCornerHeight - halfStokeSize);
                mCornerLeft.set(width, height - mCornerHeight);
                mCornerCenter.set(width * 0.5f, height);
                mCornerRight.set(0, height - mCornerHeight);
                makeTriangleRectCornerPath();
                mPath.lineTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.close();
                break;
            case Gravity.LEFT:
                mPath.moveTo(mCornerHeight + halfStokeSize, height - halfStokeSize);
                mCornerLeft.set(mCornerHeight, height);
                mCornerCenter.set(0, height * 0.5f);
                mCornerRight.set(mCornerHeight, 0);
                makeTriangleRectCornerPath();
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.RIGHT:
                mPath.moveTo(width - mCornerHeight - halfStokeSize, halfStokeSize);
                mCornerLeft.set(width - mCornerHeight, 0);
                mCornerCenter.set(width, height * 0.5f);
                mCornerRight.set(width - mCornerHeight, height);
                makeTriangleRectCornerPath();
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, halfStokeSize);
                mPath.close();
                break;
        }
    }

    @SuppressWarnings("all")
    private void makeTriangleRectCornerPath() {
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
                makeCornerPath(mCornerLeft.x + cornerXOffset, mCornerLeft.y + cornerYOffset,
                        mCornerCenter.x, mCornerCenter.y + cornerStokeVertical,
                        mCornerRight.x - cornerXOffset, mCornerRight.y + cornerYOffset);
                break;
            case Gravity.BOTTOM:
                makeCornerPath(mCornerLeft.x - cornerXOffset, mCornerLeft.y - cornerYOffset,
                        mCornerCenter.x, mCornerCenter.y - cornerStokeVertical,
                        mCornerRight.x + cornerXOffset, mCornerRight.y - cornerYOffset);
                break;
            case Gravity.LEFT:
                makeCornerPath(mCornerLeft.x + cornerYOffset, mCornerLeft.y - cornerXOffset,
                        mCornerCenter.x + cornerStokeVertical, mCornerCenter.y,
                        mCornerRight.x + cornerYOffset, mCornerRight.y + cornerXOffset);
                break;
            case Gravity.RIGHT:
                makeCornerPath(mCornerLeft.x - cornerYOffset, mCornerLeft.y + cornerXOffset,
                        mCornerCenter.x - cornerStokeVertical, mCornerCenter.y,
                        mCornerRight.x - cornerYOffset, mCornerRight.y - cornerXOffset);
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
        float offset = 0;
        switch (mDirection) {
            case Gravity.TOP:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        offset = -(width - mCornerWidth) * 0.5f + margin;
                        break;
                    case Gravity.RIGHT:
                        offset = (width - mCornerWidth) * 0.5f - margin;
                        break;
                }
                mPath.moveTo(halfStokeSize, mCornerHeight + halfStokeSize);
                mCornerLeft.set((width - mCornerWidth) * 0.5f, mCornerHeight);
                mCornerCenter.set(width * 0.5f, 0);
                mCornerRight.set((width + mCornerWidth) * 0.5f, mCornerHeight);
                makeRectCornerPath(offset);
                mPath.lineTo(width - halfStokeSize, mCornerHeight + halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.RIGHT:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        offset = -(height - mCornerWidth) * 0.5f + margin;
                        break;
                    case Gravity.RIGHT:
                        offset = (height - mCornerWidth) * 0.5f - margin;
                        break;
                }
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - mCornerHeight - halfStokeSize, halfStokeSize);
                mCornerLeft.set(width - mCornerHeight, (height - mCornerWidth) * 0.5f);
                mCornerCenter.set(width, height * 0.5f);
                mCornerRight.set(width - mCornerHeight, (height + mCornerWidth) * 0.5f);
                makeRectCornerPath(offset);
                mPath.lineTo(width - mCornerHeight - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(halfStokeSize, height - halfStokeSize);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > width ? width - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        offset = -(width - mCornerWidth) * 0.5f + margin;
                        break;
                    case Gravity.RIGHT:
                        offset = (width - mCornerWidth) * 0.5f - margin;
                        break;
                }
                mPath.moveTo(halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - mCornerHeight - halfStokeSize);
                mCornerLeft.set((width + mCornerWidth) * 0.5f, height - mCornerHeight);
                mCornerCenter.set(width * 0.5f, height);
                mCornerRight.set((width - mCornerWidth) * 0.5f, height - mCornerHeight);
                makeRectCornerPath(offset);
                mPath.lineTo(halfStokeSize, height - mCornerHeight - halfStokeSize);
                mPath.close();
                break;
            case Gravity.LEFT:
                margin = mCornerMargin <= 0 ? 0 : (mCornerMargin + mCornerWidth > height ? height - mCornerWidth : mCornerMargin);
                switch (mLocation) {
                    case Gravity.CENTER:
                        break;
                    case Gravity.LEFT:
                        offset = -(height - mCornerWidth) * 0.5f + margin;
                        break;
                    case Gravity.RIGHT:
                        offset = (height - mCornerWidth) * 0.5f - margin;
                        break;
                }
                mPath.moveTo(mCornerHeight + halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, halfStokeSize);
                mPath.lineTo(width - halfStokeSize, height - halfStokeSize);
                mPath.lineTo(mCornerHeight + halfStokeSize, height - halfStokeSize);
                mCornerLeft.set(mCornerHeight, (height + mCornerWidth) * 0.5f);
                mCornerCenter.set(0, height * 0.5f);
                mCornerRight.set(mCornerHeight, (height - mCornerWidth) * 0.5f);
                makeRectCornerPath(offset);
                mPath.close();
                break;
        }
    }

    @SuppressWarnings("all")
    private void makeRectCornerPath(float offset) {
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        final float cornerStokeHorizontal = halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical) / mCornerHeight;
        final float cornerXOffset = cornerStokeHorizontal - halfCornerWidth * halfStokeSize / mCornerHeight;
        final float cornerYOffset = halfStokeSize;
        switch (mDirection) {
            case Gravity.TOP:
                makeCornerPath(mCornerLeft.x + cornerXOffset + offset, mCornerLeft.y + cornerYOffset,
                        mCornerCenter.x + offset, mCornerCenter.y + cornerStokeVertical,
                        mCornerRight.x - cornerXOffset + offset, mCornerRight.y + cornerYOffset);
                break;
            case Gravity.RIGHT:
                makeCornerPath(mCornerLeft.x - cornerYOffset, mCornerLeft.y + cornerXOffset + offset,
                        mCornerCenter.x - cornerStokeVertical, mCornerCenter.y + offset,
                        mCornerRight.x - cornerYOffset, mCornerRight.y - cornerXOffset + offset);
                break;
            case Gravity.BOTTOM:
                makeCornerPath(mCornerLeft.x - cornerXOffset - offset, mCornerLeft.y - cornerYOffset,
                        mCornerCenter.x - offset, mCornerCenter.y - cornerStokeVertical,
                        mCornerRight.x + cornerXOffset - offset, mCornerRight.y - cornerYOffset);
                break;
            case Gravity.LEFT:
                makeCornerPath(mCornerLeft.x + cornerYOffset, mCornerLeft.y - cornerXOffset - offset,
                        mCornerCenter.x + cornerStokeVertical, mCornerCenter.y - offset,
                        mCornerRight.x + cornerYOffset, mCornerRight.y + cornerXOffset - offset);
                break;
        }
    }

    /**
     * 圆角矩形
     */
    @SuppressWarnings("all")
    private void makeRoundRect() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float halfStokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() * 0.5f : 0;
        float maxContentRadius;
        float mContentRadius;
        float mCornerMove;
        float margin;
        float offset = 0;
        switch (mDirection) {
            case Gravity.TOP:
                maxContentRadius = (width < height - mCornerHeight ? width : height - mCornerHeight) * 0.5f;
                mContentRadius = this.mContentRadius > maxContentRadius ? maxContentRadius : this.mContentRadius;
                mCornerMove = width - mContentRadius - mContentRadius - mCornerWidth;
                mPath.moveTo(mContentRadius, mCornerHeight + halfStokeSize);
                if (mCornerMove <= 0) {
                    mCornerLeft.set(mContentRadius, mCornerHeight);
                    mCornerCenter.set(width * 0.5f, 0);
                    mCornerRight.set(width - mContentRadius, mCornerHeight);
                } else {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin > mCornerMove ? mCornerMove : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            break;
                        case Gravity.LEFT:
                            offset = -mCornerMove * 0.5f + margin;
                            break;
                        case Gravity.RIGHT:
                            offset = mCornerMove * 0.5f - margin;
                            break;
                    }
                    mCornerLeft.set((width - mCornerWidth) * 0.5f + offset, mCornerHeight);
                    mCornerCenter.set(width * 0.5f + offset, 0);
                    mCornerRight.set((width + mCornerWidth) * 0.5f + offset, mCornerHeight);
                }
                makeRoundRectCornerPath();
                mPath.lineTo(width - mContentRadius, mCornerHeight + halfStokeSize);
                mRoundRect.set(width - mContentRadius - mContentRadius + halfStokeSize,
                        mCornerHeight + halfStokeSize,
                        width - halfStokeSize,
                        mCornerHeight + mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - mContentRadius);
                mRoundRect.set(width - mContentRadius - mContentRadius + halfStokeSize,
                        height - mContentRadius - mContentRadius + halfStokeSize,
                        width - halfStokeSize,
                        height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(mContentRadius, height - halfStokeSize);
                mRoundRect.set(halfStokeSize,
                        height - mContentRadius - mContentRadius + halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize,
                        height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, mCornerHeight + mContentRadius);
                mRoundRect.set(halfStokeSize,
                        mCornerHeight + halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize,
                        mCornerHeight + mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.close();
                break;
            case Gravity.RIGHT:
                maxContentRadius = (width - mCornerHeight < height ? width - mCornerHeight : height) * 0.5f;
                mContentRadius = this.mContentRadius > maxContentRadius ? maxContentRadius : this.mContentRadius;
                mCornerMove = height - mContentRadius - mContentRadius - mCornerWidth;
                mPath.moveTo(width - mCornerHeight - halfStokeSize, mContentRadius);
                if (mCornerMove <= 0) {
                    mCornerLeft.set(width - mCornerHeight, mContentRadius);
                    mCornerCenter.set(width, height * 0.5f);
                    mCornerRight.set(width - mCornerHeight, height - mContentRadius);
                } else {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin > mCornerMove ? mCornerMove : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            break;
                        case Gravity.LEFT:
                            offset = -mCornerMove * 0.5f + margin;
                            break;
                        case Gravity.RIGHT:
                            offset = mCornerMove * 0.5f - margin;
                            break;
                    }
                    mCornerLeft.set(width - mCornerHeight, (height - mCornerWidth) * 0.5f + offset);
                    mCornerCenter.set(width, height * 0.5f + offset);
                    mCornerRight.set(width - mCornerHeight, (height + mCornerWidth) * 0.5f + offset);
                }
                makeRoundRectCornerPath();
                mPath.lineTo(width - mCornerHeight - halfStokeSize, height - mContentRadius);
                mRoundRect.set(width - mCornerHeight - mContentRadius - mContentRadius + halfStokeSize,
                        height - mContentRadius - mContentRadius + halfStokeSize,
                        width - mCornerHeight - halfStokeSize,
                        height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(mContentRadius, height - halfStokeSize);
                mRoundRect.set(halfStokeSize,
                        height - mContentRadius - mContentRadius + halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize,
                        height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, mContentRadius);
                mRoundRect.set(halfStokeSize,
                        halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.lineTo(width - mCornerHeight - mContentRadius, halfStokeSize);
                mRoundRect.set(width - mCornerHeight - mContentRadius - mContentRadius + halfStokeSize,
                        halfStokeSize,
                        width - mCornerHeight - halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.close();
                break;
            case Gravity.BOTTOM:
                maxContentRadius = (width < height - mCornerHeight ? width : height - mCornerHeight) * 0.5f;
                mContentRadius = this.mContentRadius > maxContentRadius ? maxContentRadius : this.mContentRadius;
                mCornerMove = width - mContentRadius - mContentRadius - mCornerWidth;
                mPath.moveTo(width - mContentRadius, height - mCornerHeight - halfStokeSize);
                if (mCornerMove <= 0) {
                    mCornerLeft.set(width - mContentRadius, height - mCornerHeight);
                    mCornerCenter.set(width * 0.5f, height);
                    mCornerRight.set(mContentRadius, height - mCornerHeight);
                } else {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin > mCornerMove ? mCornerMove : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            break;
                        case Gravity.LEFT:
                            offset = mCornerMove * 0.5f - margin;
                            break;
                        case Gravity.RIGHT:
                            offset = -mCornerMove * 0.5f + margin;
                            break;
                    }
                    mCornerLeft.set((width + mCornerWidth) * 0.5f + offset, height - mCornerHeight);
                    mCornerCenter.set(width * 0.5f + offset, height);
                    mCornerRight.set((width - mCornerWidth) * 0.5f + offset, height - mCornerHeight);
                }
                makeRoundRectCornerPath();
                mPath.lineTo(mContentRadius, height - mCornerHeight - halfStokeSize);
                mRoundRect.set(halfStokeSize,
                        height - mCornerHeight - mContentRadius - mContentRadius + halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize,
                        height - mCornerHeight - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.lineTo(halfStokeSize, mContentRadius);
                mRoundRect.set(halfStokeSize,
                        halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.lineTo(width - mContentRadius, halfStokeSize);
                mRoundRect.set(width - mContentRadius - mContentRadius + halfStokeSize,
                        halfStokeSize,
                        width - halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - mCornerHeight - mContentRadius);
                mRoundRect.set(width - mContentRadius - mContentRadius + halfStokeSize,
                        height - mCornerHeight - mContentRadius - mContentRadius + halfStokeSize,
                        width - halfStokeSize,
                        height - mCornerHeight - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                break;
            case Gravity.LEFT:
                maxContentRadius = (width - mCornerHeight < height ? width - mCornerHeight : height) * 0.5f;
                mContentRadius = this.mContentRadius > maxContentRadius ? maxContentRadius : this.mContentRadius;
                mCornerMove = height - mContentRadius - mContentRadius - mCornerWidth;
                mPath.moveTo(mCornerHeight + halfStokeSize, height - mContentRadius);
                if (mCornerMove <= 0) {
                    mCornerLeft.set(mCornerHeight, height - mContentRadius);
                    mCornerCenter.set(0, height * 0.5f);
                    mCornerRight.set(mCornerHeight, mContentRadius);
                } else {
                    margin = mCornerMargin <= 0 ? 0 : (mCornerMargin > mCornerMove ? mCornerMove : mCornerMargin);
                    switch (mLocation) {
                        case Gravity.CENTER:
                            break;
                        case Gravity.LEFT:
                            offset = mCornerMove * 0.5f - margin;
                            break;
                        case Gravity.RIGHT:
                            offset = -mCornerMove * 0.5f + margin;
                            break;
                    }
                    mCornerLeft.set(mCornerHeight, (height + mCornerWidth) * 0.5f + offset);
                    mCornerCenter.set(0, height * 0.5f + offset);
                    mCornerRight.set(mCornerHeight, (height - mCornerWidth) * 0.5f + offset);
                }
                makeRoundRectCornerPath();
                mPath.lineTo(mCornerHeight + halfStokeSize, mContentRadius);
                mRoundRect.set(mCornerHeight + halfStokeSize,
                        halfStokeSize,
                        mCornerHeight + mContentRadius + mContentRadius - halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, 180, 90);
                mPath.lineTo(width - mContentRadius, halfStokeSize);
                mRoundRect.set(width - mContentRadius - mContentRadius + halfStokeSize,
                        halfStokeSize,
                        width - halfStokeSize,
                        mContentRadius + mContentRadius - halfStokeSize);
                mPath.arcTo(mRoundRect, -90, 90);
                mPath.lineTo(width - halfStokeSize, height - mContentRadius);
                mRoundRect.set(width - mContentRadius - mContentRadius + halfStokeSize,
                        height - mContentRadius - mContentRadius + halfStokeSize,
                        width - halfStokeSize,
                        height - halfStokeSize);
                mPath.arcTo(mRoundRect, 0, 90);
                mPath.lineTo(mCornerHeight + mContentRadius, height - halfStokeSize);
                mRoundRect.set(mCornerHeight + halfStokeSize,
                        height - mContentRadius - mContentRadius + halfStokeSize,
                        mCornerHeight + mContentRadius + mContentRadius - halfStokeSize,
                        height - halfStokeSize);
                mPath.arcTo(mRoundRect, 90, 90);
                mPath.close();
                break;
        }
    }

    @SuppressWarnings("all")
    private void makeRoundRectCornerPath() {
        final float strokeSize = mStrokePaint != null ? mStrokePaint.getStrokeWidth() : 0;
        final float halfStokeSize = strokeSize * 0.5f;
        final float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ?
                mCornerRight.x - mCornerLeft.x < 0 ? mCornerLeft.x - mCornerRight.x : mCornerRight.x - mCornerLeft.x :
                mCornerRight.y - mCornerLeft.y < 0 ? mCornerLeft.y - mCornerRight.y : mCornerRight.y - mCornerLeft.y;
        if (mCornerWidth == 0 || mCornerWidth < strokeSize)
            return;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        final float cornerStokeHorizontal = halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical) / mCornerHeight;
        final float cornerXOffset = cornerStokeHorizontal - halfCornerWidth * halfStokeSize / mCornerHeight;
        final float cornerYOffset = halfStokeSize;
        switch (mDirection) {
            case Gravity.TOP:
                makeCornerPath(mCornerLeft.x + cornerXOffset, mCornerLeft.y + cornerYOffset,
                        mCornerCenter.x, mCornerCenter.y + cornerStokeVertical,
                        mCornerRight.x - cornerXOffset, mCornerRight.y + cornerYOffset);
                break;
            case Gravity.RIGHT:
                makeCornerPath(mCornerLeft.x - cornerYOffset, mCornerLeft.y + cornerXOffset,
                        mCornerCenter.x - cornerStokeVertical, mCornerCenter.y,
                        mCornerRight.x - cornerYOffset, mCornerRight.y - cornerXOffset);
                break;
            case Gravity.BOTTOM:
                makeCornerPath(mCornerLeft.x - cornerXOffset, mCornerLeft.y - cornerYOffset,
                        mCornerCenter.x, mCornerCenter.y - cornerStokeVertical,
                        mCornerRight.x + cornerXOffset, mCornerRight.y - cornerYOffset);
                break;
            case Gravity.LEFT:
                makeCornerPath(mCornerLeft.x + cornerYOffset, mCornerLeft.y - cornerXOffset,
                        mCornerCenter.x + cornerStokeVertical, mCornerCenter.y,
                        mCornerRight.x + cornerYOffset, mCornerRight.y + cornerXOffset);
                break;
        }
    }

    @SuppressWarnings("all")
    protected void makeCornerPath(float leftX, float leftY,
                                  float centerX, float centerY,
                                  float rightX, float rightY) {
        mPath.lineTo(leftX, leftY);
        if (mCornerBezier <= 0) {
            mPath.lineTo(centerX, centerY);
            mPath.lineTo(rightX, rightY);
            return;
        } else if (mCornerBezier >= 1) {
            mPath.quadTo(centerX, centerY, rightX, rightY);
            return;
        }
        if (leftX == rightX && leftY == rightY) {
            mPath.lineTo(centerX, centerY);
            mPath.lineTo(rightX, rightY);
            return;
        }
        final float width = leftX == rightX ? (leftY > rightY ? leftY - rightY : rightY - leftY) : (leftX > rightX ? leftX - rightX : rightX - leftX);
        final float height = leftX == rightX ? (centerX > leftX ? centerX - leftX : leftX - centerX) : (centerY > leftY ? centerY - leftY : leftY - centerY);
        final float heightBezier = height * mCornerBezier;
        final float widthBezier = width * heightBezier / height;
        switch (mDirection) {
            case Gravity.TOP:
                mPath.lineTo(centerX - widthBezier * 0.5f, centerY + heightBezier);
                mPath.quadTo(centerX, centerY, centerX + widthBezier * 0.5f, centerY + heightBezier);
                mPath.lineTo(centerX + widthBezier * 0.5f, centerY + heightBezier);
                break;
            case Gravity.RIGHT:
                mPath.lineTo(centerX - heightBezier, centerY - widthBezier * 0.5f);
                mPath.quadTo(centerX, centerY, centerX - heightBezier, centerY + widthBezier * 0.5f);
                mPath.lineTo(centerX - heightBezier, centerY + widthBezier * 0.5f);
                break;
            case Gravity.BOTTOM:
                mPath.lineTo(centerX + widthBezier * 0.5f, centerY - heightBezier);
                mPath.quadTo(centerX, centerY, centerX - widthBezier * 0.5f, centerY - heightBezier);
                mPath.lineTo(centerX - widthBezier * 0.5f, centerY - heightBezier);
                break;
            case Gravity.LEFT:
                mPath.lineTo(centerX + heightBezier, centerY + widthBezier * 0.5f);
                mPath.quadTo(centerX, centerY, centerX + heightBezier, centerY - widthBezier * 0.5f);
                mPath.lineTo(centerX + heightBezier, centerY - widthBezier * 0.5f);
                break;
        }
        mPath.lineTo(rightX, rightY);
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha != mAlpha) {
            mAlpha = alpha;
            invalidateSelf();
        }
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
     * 设置贝塞尔尖角
     *
     * @param cornerBezier 贝塞尔尖角高度占尖角高度百分比(0~1)，推荐值为0.25
     */
    public void setCornerBezier(float cornerBezier) {
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
