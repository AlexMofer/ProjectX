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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 尖角框
 * TODO 改进路径算法
 */
@SuppressLint("RtlHardcoded")
@SuppressWarnings({"NullableProblems", "WeakerAccess", "unused"})
public class CornerDrawable extends Drawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Xfermode mXfermode;

    private int mAlpha = 0xFF;
    private int mCornerWidth;// 尖角宽
    private int mCornerHeight;// 尖角高
    private float mCornerBezier;// 贝塞尔尖角
    private int mDirection = Gravity.TOP;//朝向
    private int mLocation = Gravity.CENTER;// 位置
    private int mCornerMargin;//尖角边距
    private float mContentRadius;// 内容圆角半径
    private ColorStateList mFillColor;// 充填颜色
    private float mStrokeWidth;// 描边宽度
    private ColorStateList mStrokeColor;// 描边颜色
    private boolean mClipFill;// 裁剪充填
    private final Rect mContentPaddingRect = new Rect();// 内容间隔

    private final Rect mPaddingRect = new Rect();
    private final Path mPath = new Path();
    private final RectF mRoundRect = new RectF();
    private PointF mCornerLeft = new PointF();
    private PointF mCornerCenter = new PointF();
    private PointF mCornerRight = new PointF();
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    public CornerDrawable() {
        this(10, 10, Color.BLACK);
    }

    public CornerDrawable(int cornerWidth, int cornerHeight, int color) {
        this(cornerWidth, cornerHeight, 0, Gravity.TOP, Gravity.CENTER, 0,
                ColorStateList.valueOf(color));
    }

    public CornerDrawable(int cornerWidth, int cornerHeight, float cornerBezier,
                          int direction, int location, float contentRadius,
                          ColorStateList fillColor) {
        mCornerWidth = cornerWidth;
        mCornerHeight = cornerHeight;
        if (cornerBezier >= 0 && cornerBezier <= 1)
            mCornerBezier = cornerBezier;
        mDirection = direction;
        mLocation = location;
        mContentRadius = contentRadius;
        mFillColor = fillColor;
        calculateValue();
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.CornerDrawable);
        mCornerWidth = custom.getDimensionPixelSize(R.styleable.CornerDrawable_cdCornerWidth,
                mCornerWidth);
        mCornerHeight = custom.getDimensionPixelSize(R.styleable.CornerDrawable_cdCornerHeight,
                mCornerHeight);
        final float cornerBezier = custom.getFloat(R.styleable.CornerDrawable_cdCornerBezier,
                0);
        mDirection = custom.getInt(R.styleable.CornerDrawable_cdDirection, mDirection);
        mLocation = custom.getInt(R.styleable.CornerDrawable_cdLocation, mLocation);
        mCornerMargin = custom.getDimensionPixelSize(R.styleable.CornerDrawable_cdCornerMargin,
                mCornerMargin);
        mContentRadius = custom.getDimension(R.styleable.CornerDrawable_cdContentRadius,
                mContentRadius);
        mFillColor = custom.getColorStateList(R.styleable.CornerDrawable_android_fillColor);
        mStrokeWidth = custom.getDimension(R.styleable.CornerDrawable_cdStrokeWidth, mStrokeWidth);
        mStrokeWidth = custom.getFloat(R.styleable.CornerDrawable_android_strokeWidth,
                mStrokeWidth);
        mStrokeColor = custom.getColorStateList(R.styleable.CornerDrawable_android_strokeColor);
        final int strokeLineCap = custom.getInt(R.styleable.CornerDrawable_android_strokeLineCap,
                0);
        final int strokeLineJoin = custom.getInt(R.styleable.CornerDrawable_android_strokeLineJoin,
                0);
        final float strokeMiterLimit = custom.getFloat(
                R.styleable.CornerDrawable_android_strokeMiterLimit, 0);
        final float dashWidth = custom.getDimension(R.styleable.CornerDrawable_android_dashWidth,
                0);
        final float dashGap = custom.getDimension(R.styleable.CornerDrawable_android_dashGap,
                0);
        mClipFill = custom.getBoolean(R.styleable.CornerDrawable_cdClipFill, false);
        final int padding = custom.getDimensionPixelSize(R.styleable.CornerDrawable_android_padding,
                0);
        final int paddingStart = custom.getDimensionPixelSize(
                R.styleable.CornerDrawable_android_paddingStart, padding);
        final int paddingTop = custom.getDimensionPixelSize(
                R.styleable.CornerDrawable_android_paddingTop, padding);
        final int paddingEnd = custom.getDimensionPixelSize(
                R.styleable.CornerDrawable_android_paddingEnd, padding);
        final int paddingBottom = custom.getDimensionPixelSize(
                R.styleable.CornerDrawable_android_paddingBottom, padding);
        custom.recycle();
        if (cornerBezier >= 0 && cornerBezier <= 1)
            mCornerBezier = cornerBezier;
        mPaint.setStrokeWidth(mStrokeWidth);
        switch (strokeLineCap) {
            default:
            case 0:
                mPaint.setStrokeCap(Paint.Cap.BUTT);
                break;
            case 1:
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case 2:
                mPaint.setStrokeCap(Paint.Cap.SQUARE);
                break;
        }
        switch (strokeLineJoin) {
            default:
            case 0:
                mPaint.setStrokeJoin(Paint.Join.MITER);
                break;
            case 1:
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                break;
            case 2:
                mPaint.setStrokeJoin(Paint.Join.BEVEL);
                break;
        }
        mPaint.setStrokeMiter(strokeMiterLimit);
        if (dashWidth > 0 && dashGap > 0)
            mPaint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, 0));
        mContentPaddingRect.set(paddingStart, paddingTop, paddingEnd, paddingBottom);
        calculateValue();
    }

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
                    //noinspection SuspiciousNameCombination
                    mIntrinsicWidth = mCornerHeight;
                    //noinspection SuspiciousNameCombination
                    mIntrinsicHeight = mCornerWidth;
                    break;
                case Gravity.RIGHT:
                    mPaddingRect.set(mContentPaddingRect.left,
                            mContentPaddingRect.top,
                            mContentPaddingRect.right + mCornerHeight,
                            mContentPaddingRect.bottom);
                    //noinspection SuspiciousNameCombination
                    mIntrinsicWidth = mCornerHeight;
                    //noinspection SuspiciousNameCombination
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

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updatePath();
    }

    private void updatePath() {
        mPath.reset();
        final float halfStokeSize = mStrokeWidth * 0.5f;
        float mContentRadius = this.mContentRadius > halfStokeSize ? this.mContentRadius : 0;
        if (mCornerHeight == 0) {
            final Rect bounds = getBounds();
            if (mContentRadius == 0)
                mPath.addRect(bounds.left, bounds.top, bounds.right, bounds.bottom,
                        Path.Direction.CW);
            else
                Compat.addRoundRect(mPath, bounds.left, bounds.top, bounds.right, bounds.bottom,
                        mContentRadius, mContentRadius, Path.Direction.CCW);
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
    }

    /**
     * 三角形
     */
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

    private void makeTriangleCornerPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ?
                (width > this.mCornerWidth ? this.mCornerWidth : width) :
                (height > this.mCornerWidth ? this.mCornerWidth : height);
        float mCornerHeight = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ? height : width;
        final float halfStokeSize = mStrokeWidth * 0.5f;
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
    private void makeTriangleRectPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float halfStokeSize = mStrokeWidth * 0.5f;
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
                //noinspection SuspiciousNameCombination
                mCornerLeft.set(mCornerHeight, height);
                mCornerCenter.set(0, height * 0.5f);
                //noinspection SuspiciousNameCombination
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

    private void makeTriangleRectCornerPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float halfStokeSize = mStrokeWidth * 0.5f;
        final float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ? width : height;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        //noinspection UnnecessaryLocalVariable
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
    private void makeRectPath() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float halfStokeSize = mStrokeWidth * 0.5f;
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
                //noinspection SuspiciousNameCombination
                mCornerLeft.set(mCornerHeight, (height + mCornerWidth) * 0.5f);
                mCornerCenter.set(0, height * 0.5f);
                //noinspection SuspiciousNameCombination
                mCornerRight.set(mCornerHeight, (height - mCornerWidth) * 0.5f);
                makeRectCornerPath(offset);
                mPath.close();
                break;
        }
    }

    private void makeRectCornerPath(float offset) {
        final float halfStokeSize = mStrokeWidth * 0.5f;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        final float cornerStokeHorizontal = halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical) / mCornerHeight;
        final float cornerXOffset = cornerStokeHorizontal - halfCornerWidth * halfStokeSize / mCornerHeight;
        //noinspection UnnecessaryLocalVariable
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
    private void makeRoundRect() {
        final int width = getBounds().width();
        final int height = getBounds().height();
        final float halfStokeSize = mStrokeWidth * 0.5f;
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
                    //noinspection SuspiciousNameCombination
                    mCornerLeft.set(mCornerHeight, height - mContentRadius);
                    mCornerCenter.set(0, height * 0.5f);
                    //noinspection SuspiciousNameCombination
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
                    //noinspection SuspiciousNameCombination
                    mCornerLeft.set(mCornerHeight, (height + mCornerWidth) * 0.5f + offset);
                    mCornerCenter.set(0, height * 0.5f + offset);
                    //noinspection SuspiciousNameCombination
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

    private void makeRoundRectCornerPath() {
        final float halfStokeSize = mStrokeWidth * 0.5f;
        final float mCornerWidth = (mDirection == Gravity.TOP || mDirection == Gravity.BOTTOM) ?
                mCornerRight.x - mCornerLeft.x < 0 ? mCornerLeft.x - mCornerRight.x : mCornerRight.x - mCornerLeft.x :
                mCornerRight.y - mCornerLeft.y < 0 ? mCornerLeft.y - mCornerRight.y : mCornerRight.y - mCornerLeft.y;
        if (mCornerWidth == 0 || mCornerWidth < mStrokeWidth)
            return;
        final float halfCornerWidth = mCornerWidth * 0.5f;
        final float cornerStokeVertical = (float) (Math.sqrt(halfCornerWidth * halfCornerWidth + mCornerHeight * mCornerHeight) * halfStokeSize / halfCornerWidth);
        final float cornerStokeHorizontal = halfCornerWidth - halfCornerWidth * (mCornerHeight - cornerStokeVertical) / mCornerHeight;
        final float cornerXOffset = cornerStokeHorizontal - halfCornerWidth * halfStokeSize / mCornerHeight;
        //noinspection UnnecessaryLocalVariable
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

    private void makeCornerPath(float leftX, float leftY,
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
    public void draw(Canvas canvas) {
        if (mPath.isEmpty())
            return;
        final int[] state = getState();
        if (mStrokeColor == null) {
            mPaint.setColor(DrawableHelper.getColor(mFillColor, state, mAlpha));
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(mPath, mPaint);
            return;
        }
        final int fillColor = DrawableHelper.getColor(mFillColor, state, mAlpha);
        final int strokeColor = DrawableHelper.getColor(mStrokeColor, state, mAlpha);
        if (fillColor == strokeColor) {
            mPaint.setColor(fillColor);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawPath(mPath, mPaint);
        } else {
            if (mClipFill) {
                final Rect bounds = getBounds();
                final int layer = Compat.saveLayer(canvas,
                        bounds.left, bounds.top, bounds.right, bounds.bottom, null);
                mPaint.setColor(fillColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mPath, mPaint);
                if (mXfermode == null)
                    mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
                final Xfermode old = mPaint.getXfermode();
                mPaint.setXfermode(mXfermode);
                mPaint.setColor(Color.BLACK);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(mPath, mPaint);
                mPaint.setXfermode(old);
                canvas.restoreToCount(layer);
                mPaint.setColor(strokeColor);
                canvas.drawPath(mPath, mPaint);
            } else {
                mPaint.setColor(fillColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mPath, mPaint);
                mPaint.setColor(strokeColor);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha == alpha)
            return;
        mAlpha = alpha;
        invalidateSelf();
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mPaint.getColorFilter() == colorFilter)
            return;
        mPaint.setColorFilter(colorFilter);
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
    public boolean isStateful() {
        return (mFillColor != null && mFillColor.isStateful()) ||
                (mStrokeColor != null && mStrokeColor.isStateful());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        if (mPath.isEmpty() || !mPath.isConvex()) {
            super.getOutline(outline);
            return;
        }
        final int[] state = getState();
        final int fillColor = DrawableHelper.getColor(mFillColor, state, mAlpha);
        final int strokeColor = DrawableHelper.getColor(mStrokeColor, state, mAlpha);
        final int alpha = Math.max(Color.alpha(fillColor), Color.alpha(strokeColor));
        outline.setConvexPath(mPath);
        outline.setAlpha(alpha / 255f);
    }

    @Override
    public boolean getPadding(Rect padding) {
        if (Compat.isLayoutDirectionLTR(this))
            padding.set(mPaddingRect);
        else
            padding.set(mPaddingRect.right, mPaddingRect.top, mPaddingRect.left,
                    mPaddingRect.bottom);
        return true;
    }

    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return true;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        return isStateful();
    }

    /**
     * 获取尖角宽度
     *
     * @return 尖角宽度
     */
    public int getCornerWidth() {
        return mCornerWidth;
    }

    /**
     * 设置尖角宽度
     *
     * @param cornerWidth 尖角宽度
     */
    public void setCornerWidth(int cornerWidth) {
        if (mCornerWidth == cornerWidth)
            return;
        mCornerWidth = cornerWidth;
        calculateValue();
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取尖角高度
     *
     * @return 尖角高度
     */
    public int getCornerHeight() {
        return mCornerHeight;
    }

    /**
     * 设置尖角高度
     *
     * @param cornerHeight 尖角高度
     */
    public void setCornerHeight(int cornerHeight) {
        if (mCornerHeight == cornerHeight)
            return;
        mCornerHeight = cornerHeight;
        calculateValue();
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取贝塞尔尖角比例
     *
     * @return 贝塞尔尖角比例
     */
    public float getCornerBezier() {
        return mCornerBezier;
    }

    /**
     * 设置贝塞尔尖角
     *
     * @param cornerBezier 贝塞尔尖角高度占尖角高度百分比(0~1)，推荐值为0.25
     */
    public void setCornerBezier(float cornerBezier) {
        if (mCornerBezier == cornerBezier)
            return;
        mCornerBezier = cornerBezier;
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取朝向
     *
     * @return 朝向
     */
    public int getDirection() {
        return mDirection;
    }

    /**
     * 设置朝向
     *
     * @param direction 朝向
     */
    public void setDirection(int direction) {
        if (direction != Gravity.START &&
                direction != Gravity.TOP &&
                direction != Gravity.END &&
                direction != Gravity.BOTTOM)
            return;
        if (mDirection == direction)
            return;
        mDirection = direction;
        calculateValue();
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取尖角位置
     *
     * @return 尖角位置
     */
    public int getLocation() {
        return mLocation;
    }

    /**
     * 获取尖角边缘间隔
     *
     * @return 尖角边缘间隔
     */
    public int getCornerMargin() {
        return mCornerMargin;
    }

    /**
     * 设置尖角位置
     *
     * @param location     尖角位置
     * @param cornerMargin 尖角边缘间隔
     */
    public void setLocation(int location, int cornerMargin) {
        if (location != Gravity.START &&
                location != Gravity.CENTER &&
                location != Gravity.END)
            return;
        if (mLocation == location && mCornerMargin == cornerMargin)
            return;
        mLocation = location;
        mCornerMargin = cornerMargin;
        calculateValue();
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取内容圆角半径
     *
     * @return 内容圆角半径
     */
    public float getContentRadius() {
        return mContentRadius;
    }

    /**
     * 设置内容圆角半径
     *
     * @param contentRadius 内容圆角半径
     */
    public void setContentRadius(float contentRadius) {
        if (mContentRadius == contentRadius)
            return;
        mContentRadius = contentRadius;
        calculateValue();
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取充填色
     *
     * @return 充填色
     */
    public ColorStateList getFillColor() {
        return mFillColor;
    }

    /**
     * 设置充填色
     *
     * @param color 充填色
     */
    public void setFillColor(ColorStateList color) {
        if (mFillColor == color)
            return;
        mFillColor = color;
        invalidateSelf();
    }

    /**
     * 设置充填色
     *
     * @param color 充填色
     */
    public void setFillColor(int color) {
        setFillColor(ColorStateList.valueOf(color));
    }

    /**
     * 获取描边线
     *
     * @return 描边线
     */
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * 设置描边线宽
     *
     * @param strokeWidth 描边线宽
     */
    public void setStrokeWidth(float strokeWidth) {
        if (mStrokeWidth == strokeWidth)
            return;
        mStrokeWidth = strokeWidth;
        mPaint.setStrokeWidth(mStrokeWidth);
        calculateValue();
        updatePath();
        invalidateSelf();
    }

    /**
     * 获取描边颜色
     *
     * @return 描边颜色
     */
    public ColorStateList getStrokeColor() {
        return mStrokeColor;
    }

    /**
     * 设置描边颜色
     *
     * @param strokeColor 描边颜色
     */
    public void setStrokeColor(ColorStateList strokeColor) {
        if (mStrokeColor == strokeColor)
            return;
        mStrokeColor = strokeColor;
        invalidateSelf();
    }

    /**
     * 设置描边颜色
     *
     * @param strokeColor 描边颜色
     */
    public void setStrokeColor(int strokeColor) {
        setStrokeColor(ColorStateList.valueOf(strokeColor));
    }

    /**
     * Return the paint's Cap, controlling how the start and end of stroked
     * lines and paths are treated.
     *
     * @return the line cap style for the paint, used whenever the paint's
     * style is Stroke or StrokeAndFill.
     */
    public Paint.Cap getStrokeCap() {
        return mPaint.getStrokeCap();
    }

    /**
     * Set the paint's Cap.
     *
     * @param cap set the paint's line cap style, used whenever the paint's
     *            style is Stroke or StrokeAndFill.
     */
    public void setStrokeCap(Paint.Cap cap) {
        if (mPaint.getStrokeCap() == cap)
            return;
        mPaint.setStrokeCap(cap);
        invalidateSelf();
    }

    /**
     * Return the paint's stroke join type.
     *
     * @return the paint's Join.
     */
    public Paint.Join getStrokeJoin() {
        return mPaint.getStrokeJoin();
    }

    /**
     * Set the paint's Join.
     *
     * @param join set the paint's Join, used whenever the paint's style is
     *             Stroke or StrokeAndFill.
     */
    public void setStrokeJoin(Paint.Join join) {
        if (mPaint.getStrokeJoin() == join)
            return;
        mPaint.setStrokeJoin(join);
        invalidateSelf();
    }

    /**
     * Return the paint's stroke miter value. Used to control the behavior
     * of miter joins when the joins angle is sharp.
     *
     * @return the paint's miter limit, used whenever the paint's style is
     * Stroke or StrokeAndFill.
     */
    public float getStrokeMiter() {
        return mPaint.getStrokeMiter();
    }

    /**
     * Set the paint's stroke miter value. This is used to control the behavior
     * of miter joins when the joins angle is sharp. This value must be >= 0.
     *
     * @param miter set the miter limit on the paint, used whenever the paint's
     *              style is Stroke or StrokeAndFill.
     */
    public void setStrokeMiter(float miter) {
        if (mPaint.getStrokeMiter() == miter)
            return;
        mPaint.setStrokeMiter(miter);
        invalidateSelf();
    }

    /**
     * Set stroke dash
     *
     * @param dashWidth Length of a dash in the stroke.
     * @param dashGap   Gap between dashes in the stroke.
     * @param phase     offset form start
     */
    public void setStrokeDash(float dashWidth, float dashGap, float phase) {
        if (dashWidth <= 0 || dashGap <= 0)
            return;
        mPaint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, phase));
        invalidateSelf();
    }

    /**
     * 判断是否裁剪充填（描边与充填不覆盖）
     *
     * @return 是否裁剪充填
     */
    public boolean isClipFill() {
        return mClipFill;
    }

    /**
     * 设置是否裁剪充填
     *
     * @param clip 是否裁剪充填
     */
    public void setClipFill(boolean clip) {
        if (mClipFill == clip)
            return;
        mClipFill = clip;
        invalidateSelf();
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
        updatePath();
        invalidateSelf();
    }

    /**
     * 设置Padding
     *
     * @param padding Padding
     */
    public void setPadding(Rect padding) {
        if (padding == null)
            return;
        setPadding(padding.left, padding.top, padding.right, padding.bottom);
    }
}
