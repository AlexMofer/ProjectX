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
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 路径图片
 * Created by Alex on 2018/12/28.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PathDrawable extends Drawable {

    public final static int SCALE_TYPE_AVERAGE = 0;// 以缩放的均值
    public final static int SCALE_TYPE_MIN = 1;// 以缩放的小值
    public final static int SCALE_TYPE_MAX = 2;// 以缩放的大值
    public final static int SCALE_TYPE_WIDTH = 3;// 以宽度的缩放值
    public final static int SCALE_TYPE_HEIGHT = 4;// 以高度的缩放值
    public final static int SCALE_TYPE_NONE = 5;// 不缩放
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Xfermode mXfermode;
    private final Path mPath = new Path();
    private final Matrix mMatrix = new Matrix();
    private final Path mDrawPath = new Path();
    private int mWidth;
    private int mHeight;
    private float mViewportWidth;
    private float mViewportHeight;
    private int mAlpha = 0xFF;
    private ColorStateList mBackgroundColor;
    private Paint.Style mStyle = Paint.Style.FILL;
    private ColorStateList mFillColor;
    private ColorStateList mStrokeColor;
    private float mStrokeWidth;
    private int mStrokeWidthScaleType;
    private boolean mClipStroke;

    @SuppressWarnings("NullableProblems")
    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = resources.obtainAttributes(Xml.asAttributeSet(parser),
                R.styleable.PathDrawable);
        mBackgroundColor = custom.getColorStateList(R.styleable.PathDrawable_android_background);
        mWidth = custom.getDimensionPixelSize(R.styleable.PathDrawable_android_width, 0);
        mHeight = custom.getDimensionPixelSize(R.styleable.PathDrawable_android_height, 0);
        mViewportWidth = custom.getFloat(R.styleable.PathDrawable_android_viewportWidth,
                0);
        mViewportHeight = custom.getFloat(R.styleable.PathDrawable_android_viewportHeight,
                0);
        final String data = custom.getString(R.styleable.PathDrawable_android_pathData);
        final int fillType = custom.getInt(R.styleable.PathDrawable_pdFillType, 0);
        mFillColor = custom.getColorStateList(R.styleable.PathDrawable_android_fillColor);
        mStrokeColor = custom.getColorStateList(R.styleable.PathDrawable_android_strokeColor);
        mStrokeWidth = custom.getFloat(R.styleable.PathDrawable_android_strokeWidth, 0);
        mStrokeWidthScaleType = custom.getInt(R.styleable.PathDrawable_pdStrokeWidthScaleType,
                SCALE_TYPE_AVERAGE);
        final int strokeLineCap = custom.getInt(R.styleable.PathDrawable_android_strokeLineCap,
                0);
        final int strokeLineJoin = custom.getInt(R.styleable.PathDrawable_android_strokeLineJoin,
                0);
        final float strokeMiterLimit = custom.getFloat(
                R.styleable.PathDrawable_android_strokeMiterLimit, 0);
        mClipStroke = custom.getBoolean(R.styleable.PathDrawable_pdClipStroke, false);
        custom.recycle();
        updateStyle();
        switch (fillType) {
            default:
            case 0:
                mPath.setFillType(Path.FillType.WINDING);
                break;
            case 1:
                mPath.setFillType(Path.FillType.EVEN_ODD);
                break;
            case 2:
                mPath.setFillType(Path.FillType.INVERSE_WINDING);
                break;
            case 3:
                mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                break;
        }
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
        if (!TextUtils.isEmpty(data))
            setPathData(data);
    }

    private void updateStyle() {
        if (mFillColor != null && mStrokeColor != null)
            mStyle = Paint.Style.FILL_AND_STROKE;
        else if (mStrokeColor != null)
            mStyle = Paint.Style.STROKE;
        else
            mStyle = Paint.Style.FILL;
    }

    private void setPathData(String data) {
        final String[] items = data.split(";");
        for (String item : items) {
            item = item.trim();
            if (TextUtils.isEmpty(item))
                continue;
            if (item.startsWith("AR") || item.startsWith("ar"))
                // AR0,0,100,100CW
                addRect(item.substring(2));
            else if (item.startsWith("AO") || item.startsWith("ao"))
                // AO0,0,100,100CW
                addOval(item.substring(2));
            else if (item.startsWith("AC") || item.startsWith("ac"))
                // AC25,25,25,CW
                addCircle(item.substring(2));
            else if (item.startsWith("AA") || item.startsWith("aa"))
                // AA0,0,100,100,0,45
                addArc(item.substring(2));
            else if (item.startsWith("AZ") || item.startsWith("az"))
                // AZ0,0,100,100,10,10,CW or AZ0,0,100,100,10,10,10,10,10,10,10,10,CW
                addRoundRect(item.substring(2));
            else if (item.startsWith("M") || item.startsWith("m"))
                // M0,0
                moveTo(item.substring(1));
            else if (item.startsWith("RM") || item.startsWith("rm"))
                // RM1,1
                rMoveTo(item.substring(2));
            else if (item.startsWith("L") || item.startsWith("l"))
                // L0,0
                lineTo(item.substring(1));
            else if (item.startsWith("RL") || item.startsWith("rl"))
                // RL1,1
                rLineTo(item.substring(2));
            else if (item.startsWith("Q") || item.startsWith("q"))
                // Q1,1,2,2
                quadTo(item.substring(1));
            else if (item.startsWith("RQ") || item.startsWith("rq"))
                // RQ1,1,2,2
                rQuadTo(item.substring(2));
            else if (item.startsWith("C") || item.startsWith("c"))
                // C1,1,2,2,3,3
                cubicTo(item.substring(1));
            else if (item.startsWith("RC") || item.startsWith("rc"))
                // RC1,1,2,2,3,3
                rCubicTo(item.substring(2));
            else if (item.startsWith("AT") || item.startsWith("at"))
                // AT0,0,100,100,0,45 or AT0,0,100,100,0,45M
                arcTo(item.substring(2));
            else if (TextUtils.equals("z", item.toLowerCase()))
                // Z
                close();
        }
    }

    private void addRect(String data) {
        final String[] values = data.split(",");
        if (values.length != 5)
            return;
        try {
            final float left = Float.valueOf(values[0]);
            final float top = Float.valueOf(values[1]);
            final float right = Float.valueOf(values[2]);
            final float bottom = Float.valueOf(values[3]);
            if (values[4].startsWith("CW") || values[4].startsWith("cw"))
                addRect(left, top, right, bottom, Path.Direction.CW);
            else
                addRect(left, top, right, bottom, Path.Direction.CCW);

        } catch (Exception e) {
            // ignore
        }
    }

    private void addOval(String data) {
        final String[] values = data.split(",");
        if (values.length != 5)
            return;
        try {
            final float left = Float.valueOf(values[0]);
            final float top = Float.valueOf(values[1]);
            final float right = Float.valueOf(values[2]);
            final float bottom = Float.valueOf(values[3]);
            if (values[4].startsWith("CW") || values[4].startsWith("cw"))
                addOval(left, top, right, bottom, Path.Direction.CW);
            else
                addOval(left, top, right, bottom, Path.Direction.CCW);

        } catch (Exception e) {
            // ignore
        }
    }

    private void addCircle(String data) {
        final String[] values = data.split(",");
        if (values.length != 4)
            return;
        try {
            final float x = Float.valueOf(values[0]);
            final float y = Float.valueOf(values[1]);
            final float radius = Float.valueOf(values[2]);
            if (values[3].startsWith("CW") || values[3].startsWith("cw"))
                addCircle(x, y, radius, Path.Direction.CW);
            else
                addCircle(x, y, radius, Path.Direction.CCW);

        } catch (Exception e) {
            // ignore
        }
    }

    private void addArc(String data) {
        final String[] values = data.split(",");
        if (values.length != 6)
            return;
        try {
            final float left = Float.valueOf(values[0]);
            final float top = Float.valueOf(values[1]);
            final float right = Float.valueOf(values[2]);
            final float bottom = Float.valueOf(values[3]);
            final float startAngle = Float.valueOf(values[4]);
            final float sweepAngle = Float.valueOf(values[5]);
            addArc(left, top, right, bottom, startAngle, sweepAngle);
        } catch (Exception e) {
            // ignore
        }
    }

    private void addRoundRect(String data) {
        final String[] values = data.split(",");
        if (values.length == 7) {
            try {
                final float left = Float.valueOf(values[0]);
                final float top = Float.valueOf(values[1]);
                final float right = Float.valueOf(values[2]);
                final float bottom = Float.valueOf(values[3]);
                final float rx = Float.valueOf(values[4]);
                final float ry = Float.valueOf(values[5]);
                if (values[6].startsWith("CW") || values[6].startsWith("cw"))
                    addRoundRect(left, top, right, bottom, rx, ry, Path.Direction.CW);
                else
                    addRoundRect(left, top, right, bottom, rx, ry, Path.Direction.CCW);

            } catch (Exception e) {
                // ignore
            }
            return;
        }
        if (values.length == 13) {
            try {
                final float left = Float.valueOf(values[0]);
                final float top = Float.valueOf(values[1]);
                final float right = Float.valueOf(values[2]);
                final float bottom = Float.valueOf(values[3]);
                final float radii[] = new float[8];
                radii[0] = Float.valueOf(values[4]);
                radii[1] = Float.valueOf(values[5]);
                radii[2] = Float.valueOf(values[6]);
                radii[3] = Float.valueOf(values[7]);
                radii[4] = Float.valueOf(values[8]);
                radii[5] = Float.valueOf(values[9]);
                radii[6] = Float.valueOf(values[10]);
                radii[7] = Float.valueOf(values[11]);
                if (values[12].startsWith("CW") || values[12].startsWith("cw"))
                    addRoundRect(left, top, right, bottom, radii, Path.Direction.CW);
                else
                    addRoundRect(left, top, right, bottom, radii, Path.Direction.CCW);

            } catch (Exception e) {
                // ignore
            }
        }
    }

    private void moveTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 2)
            return;
        try {
            final float x = Float.valueOf(values[0]);
            final float y = Float.valueOf(values[1]);
            moveTo(x, y);
        } catch (Exception e) {
            // ignore
        }
    }

    private void rMoveTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 2)
            return;
        try {
            final float dx = Float.valueOf(values[0]);
            final float dy = Float.valueOf(values[1]);
            rMoveTo(dx, dy);
        } catch (Exception e) {
            // ignore
        }
    }

    private void lineTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 2)
            return;
        try {
            final float x = Float.valueOf(values[0]);
            final float y = Float.valueOf(values[1]);
            lineTo(x, y);
        } catch (Exception e) {
            // ignore
        }
    }

    private void rLineTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 2)
            return;
        try {
            final float dx = Float.valueOf(values[0]);
            final float dy = Float.valueOf(values[1]);
            rLineTo(dx, dy);
        } catch (Exception e) {
            // ignore
        }
    }

    private void quadTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 4)
            return;
        try {
            final float x1 = Float.valueOf(values[0]);
            final float y1 = Float.valueOf(values[1]);
            final float x2 = Float.valueOf(values[2]);
            final float y2 = Float.valueOf(values[3]);
            quadTo(x1, y1, x2, y2);
        } catch (Exception e) {
            // ignore
        }
    }

    private void rQuadTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 4)
            return;
        try {
            final float dx1 = Float.valueOf(values[0]);
            final float dy1 = Float.valueOf(values[1]);
            final float dx2 = Float.valueOf(values[2]);
            final float dy2 = Float.valueOf(values[3]);
            rQuadTo(dx1, dy1, dx2, dy2);
        } catch (Exception e) {
            // ignore
        }
    }

    private void cubicTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 6)
            return;
        try {
            final float x1 = Float.valueOf(values[0]);
            final float y1 = Float.valueOf(values[1]);
            final float x2 = Float.valueOf(values[2]);
            final float y2 = Float.valueOf(values[3]);
            final float x3 = Float.valueOf(values[4]);
            final float y3 = Float.valueOf(values[5]);
            cubicTo(x1, y1, x2, y2, x3, y3);
        } catch (Exception e) {
            // ignore
        }
    }

    private void rCubicTo(String data) {
        final String[] values = data.split(",");
        if (values.length != 6)
            return;
        try {
            final float x1 = Float.valueOf(values[0]);
            final float y1 = Float.valueOf(values[1]);
            final float x2 = Float.valueOf(values[2]);
            final float y2 = Float.valueOf(values[3]);
            final float x3 = Float.valueOf(values[4]);
            final float y3 = Float.valueOf(values[5]);
            rCubicTo(x1, y1, x2, y2, x3, y3);
        } catch (Exception e) {
            // ignore
        }
    }

    private void arcTo(String data) {
        final String[] values = data.split(",");
        if (values.length == 6) {
            try {
                final float left = Float.valueOf(values[0]);
                final float top = Float.valueOf(values[1]);
                final float right = Float.valueOf(values[2]);
                final float bottom = Float.valueOf(values[3]);
                final float startAngle = Float.valueOf(values[4]);
                final float sweepAngle = Float.valueOf(values[5]);
                arcTo(left, top, right, bottom, startAngle, sweepAngle, false);
            } catch (Exception e) {
                // ignore
            }
        } else if (values.length == 7) {
            try {
                final float left = Float.valueOf(values[0]);
                final float top = Float.valueOf(values[1]);
                final float right = Float.valueOf(values[2]);
                final float bottom = Float.valueOf(values[3]);
                final float startAngle = Float.valueOf(values[4]);
                final float sweepAngle = Float.valueOf(values[5]);
                arcTo(left, top, right, bottom, startAngle, sweepAngle,
                        TextUtils.equals("m", values[6].toLowerCase()));
            } catch (Exception e) {
                // ignore
            }
        }

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateDrawPath(bounds);
    }

    private void updateDrawPath(Rect bounds) {
        mDrawPath.reset();
        mDrawPath.setFillType(mPath.getFillType());
        if (bounds == null || bounds.isEmpty() || mViewportWidth == 0 || mViewportHeight == 0)
            return;
        mMatrix.reset();
        mMatrix.postTranslate(bounds.left, bounds.top);
        mMatrix.postScale(bounds.width() / mViewportWidth,
                bounds.height() / mViewportHeight);
        if (mPath.isEmpty())
            return;
        mPath.transform(mMatrix, mDrawPath);
    }

    @Override
    public void draw(@SuppressWarnings("NullableProblems") Canvas canvas) {
        if (!isVisible())
            return;
        final int[] state = getState();
        if (mBackgroundColor != null) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mBackgroundColor.getColorForState(state,
                    mBackgroundColor.getDefaultColor()));
            canvas.drawRect(getBounds(), mPaint);
        }
        if (mDrawPath.isEmpty())
            return;
        switch (mStyle) {
            default:
            case FILL: {
                final int fillColor = mFillColor == null ? Color.TRANSPARENT :
                        mFillColor.getColorForState(state, mFillColor.getDefaultColor());
                mPaint.setColor(fillColor);
                mPaint.setAlpha(DrawableHelper.modulateAlpha(fillColor, mAlpha));
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawPath(mDrawPath, mPaint);
            }
            break;
            case STROKE: {
                final int strokeColor = mStrokeColor == null ? Color.TRANSPARENT :
                        mStrokeColor.getColorForState(state, mStrokeColor.getDefaultColor());
                mPaint.setColor(strokeColor);
                mPaint.setAlpha(DrawableHelper.modulateAlpha(strokeColor, mAlpha));
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(computeStrokeWidth());
                canvas.drawPath(mDrawPath, mPaint);
            }
            break;
            case FILL_AND_STROKE: {
                final int fillColor = mFillColor == null ? Color.TRANSPARENT :
                        mFillColor.getColorForState(state, mFillColor.getDefaultColor());
                final int strokeColor = mStrokeColor == null ? Color.TRANSPARENT :
                        mStrokeColor.getColorForState(state, mStrokeColor.getDefaultColor());
                if (fillColor == strokeColor) {
                    mPaint.setColor(fillColor);
                    mPaint.setAlpha(DrawableHelper.modulateAlpha(fillColor, mAlpha));
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mPaint.setStrokeWidth(computeStrokeWidth());
                    canvas.drawPath(mDrawPath, mPaint);
                } else {
                    final float strokeWidth = computeStrokeWidth();
                    if (mClipStroke) {
                        @SuppressLint("CanvasSize") final float right = canvas.getWidth();
                        @SuppressLint("CanvasSize") final float bottom = canvas.getHeight();
                        final int layer = Compat.saveLayer(canvas, 0, 0, right, bottom,
                                null);
                        mPaint.setColor(fillColor);
                        mPaint.setAlpha(DrawableHelper.modulateAlpha(fillColor, mAlpha));
                        mPaint.setStyle(Paint.Style.FILL);
                        canvas.drawPath(mDrawPath, mPaint);
                        if (mXfermode == null)
                            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
                        final Xfermode old = mPaint.getXfermode();
                        mPaint.setXfermode(mXfermode);
                        mPaint.setColor(Color.BLACK);
                        mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setStrokeWidth(strokeWidth);
                        canvas.drawPath(mDrawPath, mPaint);
                        mPaint.setXfermode(old);
                        canvas.restoreToCount(layer);
                        mPaint.setColor(strokeColor);
                        mPaint.setAlpha(DrawableHelper.modulateAlpha(strokeColor, mAlpha));
                        mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setStrokeWidth(strokeWidth);
                        canvas.drawPath(mDrawPath, mPaint);
                    } else {
                        mPaint.setColor(fillColor);
                        mPaint.setAlpha(DrawableHelper.modulateAlpha(fillColor, mAlpha));
                        mPaint.setStyle(Paint.Style.FILL);
                        canvas.drawPath(mDrawPath, mPaint);
                        mPaint.setColor(strokeColor);
                        mPaint.setAlpha(DrawableHelper.modulateAlpha(strokeColor, mAlpha));
                        mPaint.setStyle(Paint.Style.STROKE);
                        mPaint.setStrokeWidth(strokeWidth);
                        canvas.drawPath(mDrawPath, mPaint);
                    }
                }
            }
            break;
        }
    }

    private float computeStrokeWidth() {
        final Rect bound = getBounds();
        final int width = bound.width();
        final int height = bound.height();
        switch (mStrokeWidthScaleType) {
            default:
            case SCALE_TYPE_AVERAGE:
                if (mViewportWidth == 0 || mViewportHeight == 0)
                    return 0;
                return mStrokeWidth * (width / mViewportWidth + height / mViewportHeight) * 0.5f;
            case SCALE_TYPE_MIN:
                if (mViewportWidth == 0 || mViewportHeight == 0)
                    return 0;
                return mStrokeWidth * Math.min(width / mViewportWidth, height / mViewportHeight);
            case SCALE_TYPE_MAX:
                if (mViewportWidth == 0 || mViewportHeight == 0)
                    return 0;
                return mStrokeWidth * Math.max(width / mViewportWidth, height / mViewportHeight);
            case SCALE_TYPE_WIDTH:
                if (mViewportWidth == 0)
                    return 0;
                return mStrokeWidth * width / mViewportWidth;
            case SCALE_TYPE_HEIGHT:
                if (mViewportHeight == 0)
                    return 0;
                return mStrokeWidth * height / mViewportHeight;
            case SCALE_TYPE_NONE:
                return mStrokeWidth;
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
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
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public boolean isStateful() {
        return (mBackgroundColor != null && mBackgroundColor.isStateful()) ||
                (mFillColor != null && mFillColor.isStateful()) ||
                (mStrokeColor != null && mStrokeColor.isStateful());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(@SuppressWarnings("NullableProblems") Outline outline) {
        if (mDrawPath.isEmpty()) {
            super.getOutline(outline);
            return;
        }
        outline.setConvexPath(mDrawPath);
        final int[] state = getState();
        if (mBackgroundColor != null) {
            outline.setAlpha(DrawableHelper.getAlpha(mBackgroundColor, state));
            return;
        }
        if (mFillColor != null) {
            outline.setAlpha(DrawableHelper.getAlpha(mFillColor, state));
            return;
        }
        outline.setAlpha(0);
    }

    /**
     * 刷新路径
     */
    public void invalidatePath() {
        updateDrawPath(getBounds());
        invalidateSelf();
    }

    /**
     * Set the beginning of the next contour to the point (x,y).
     *
     * @param x The x-coordinate of the start of a new contour
     * @param y The y-coordinate of the start of a new contour
     */
    public void moveTo(float x, float y) {
        mPath.moveTo(x, y);
    }

    /**
     * Set the beginning of the next contour relative to the last point on the
     * previous contour. If there is no previous contour, this is treated the
     * same as moveTo().
     *
     * @param dx The amount to add to the x-coordinate of the end of the
     *           previous contour, to specify the start of a new contour
     * @param dy The amount to add to the y-coordinate of the end of the
     *           previous contour, to specify the start of a new contour
     */
    public void rMoveTo(float dx, float dy) {
        mPath.rMoveTo(dx, dy);
    }

    /**
     * Add a line from the last point to the specified point (x,y).
     * If no moveTo() call has been made for this contour, the first point is
     * automatically set to (0,0).
     *
     * @param x The x-coordinate of the end of a line
     * @param y The y-coordinate of the end of a line
     */
    public void lineTo(float x, float y) {
        mPath.lineTo(x, y);
    }

    /**
     * Same as lineTo, but the coordinates are considered relative to the last
     * point on this contour. If there is no previous point, then a moveTo(0,0)
     * is inserted automatically.
     *
     * @param dx The amount to add to the x-coordinate of the previous point on
     *           this contour, to specify a line
     * @param dy The amount to add to the y-coordinate of the previous point on
     *           this contour, to specify a line
     */
    public void rLineTo(float dx, float dy) {
        mPath.rLineTo(dx, dy);
    }

    /**
     * Add a quadratic bezier from the last point, approaching control point
     * (x1,y1), and ending at (x2,y2). If no moveTo() call has been made for
     * this contour, the first point is automatically set to (0,0).
     *
     * @param x1 The x-coordinate of the control point on a quadratic curve
     * @param y1 The y-coordinate of the control point on a quadratic curve
     * @param x2 The x-coordinate of the end point on a quadratic curve
     * @param y2 The y-coordinate of the end point on a quadratic curve
     */
    public void quadTo(float x1, float y1, float x2, float y2) {
        mPath.quadTo(x1, y1, x2, y2);
    }

    /**
     * Same as quadTo, but the coordinates are considered relative to the last
     * point on this contour. If there is no previous point, then a moveTo(0,0)
     * is inserted automatically.
     *
     * @param dx1 The amount to add to the x-coordinate of the last point on
     *            this contour, for the control point of a quadratic curve
     * @param dy1 The amount to add to the y-coordinate of the last point on
     *            this contour, for the control point of a quadratic curve
     * @param dx2 The amount to add to the x-coordinate of the last point on
     *            this contour, for the end point of a quadratic curve
     * @param dy2 The amount to add to the y-coordinate of the last point on
     *            this contour, for the end point of a quadratic curve
     */
    public void rQuadTo(float dx1, float dy1, float dx2, float dy2) {
        mPath.rQuadTo(dx1, dy1, dx2, dy2);
    }

    /**
     * Add a cubic bezier from the last point, approaching control points
     * (x1,y1) and (x2,y2), and ending at (x3,y3). If no moveTo() call has been
     * made for this contour, the first point is automatically set to (0,0).
     *
     * @param x1 The x-coordinate of the 1st control point on a cubic curve
     * @param y1 The y-coordinate of the 1st control point on a cubic curve
     * @param x2 The x-coordinate of the 2nd control point on a cubic curve
     * @param y2 The y-coordinate of the 2nd control point on a cubic curve
     * @param x3 The x-coordinate of the end point on a cubic curve
     * @param y3 The y-coordinate of the end point on a cubic curve
     */
    public void cubicTo(float x1, float y1, float x2, float y2,
                        float x3, float y3) {
        mPath.cubicTo(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Same as cubicTo, but the coordinates are considered relative to the
     * current point on this contour. If there is no previous point, then a
     * moveTo(0,0) is inserted automatically.
     */
    public void rCubicTo(float x1, float y1, float x2, float y2,
                         float x3, float y3) {
        mPath.rCubicTo(x1, y1, x2, y2, x3, y3);
    }

    /**
     * Append the specified arc to the path as a new contour. If the start of
     * the path is different from the path's current last point, then an
     * automatic lineTo() is added to connect the current contour to the
     * start of the arc. However, if the path is empty, then we call moveTo()
     * with the first point of the arc.
     *
     * @param startAngle  Starting angle (in degrees) where the arc begins
     * @param sweepAngle  Sweep angle (in degrees) measured clockwise, treated
     *                    mod 360.
     * @param forceMoveTo If true, always begin a new contour with the arc
     */
    public void arcTo(float left, float top, float right, float bottom, float startAngle,
                      float sweepAngle, boolean forceMoveTo) {
        Compat.arcTo(mPath, left, top, right, bottom, startAngle, sweepAngle, forceMoveTo);
    }

    /**
     * Close the current contour. If the current point is not equal to the
     * first point of the contour, a line segment is automatically added.
     */
    public void close() {
        mPath.close();
    }

    /**
     * Add a closed rectangle contour to the path
     *
     * @param left   The left side of a rectangle to add to the path
     * @param top    The top of a rectangle to add to the path
     * @param right  The right side of a rectangle to add to the path
     * @param bottom The bottom of a rectangle to add to the path
     * @param dir    The direction to wind the rectangle's contour
     */
    public void addRect(float left, float top, float right, float bottom, Path.Direction dir) {
        mPath.addRect(left, top, right, bottom, dir);
    }

    /**
     * Add a closed oval contour to the path
     *
     * @param dir The direction to wind the oval's contour
     */
    public void addOval(float left, float top, float right, float bottom, Path.Direction dir) {
        Compat.addOval(mPath, left, top, right, bottom, dir);
    }

    /**
     * Add a closed circle contour to the path
     *
     * @param x      The x-coordinate of the center of a circle to add to the path
     * @param y      The y-coordinate of the center of a circle to add to the path
     * @param radius The radius of a circle to add to the path
     * @param dir    The direction to wind the circle's contour
     */
    public void addCircle(float x, float y, float radius, Path.Direction dir) {
        mPath.addCircle(x, y, radius, dir);
    }

    /**
     * Add the specified arc to the path as a new contour.
     *
     * @param startAngle Starting angle (in degrees) where the arc begins
     * @param sweepAngle Sweep angle (in degrees) measured clockwise
     */
    public void addArc(float left, float top, float right, float bottom, float startAngle,
                       float sweepAngle) {
        Compat.addArc(mPath, left, top, right, bottom, startAngle, sweepAngle);
    }

    /**
     * Add a closed round-rectangle contour to the path
     *
     * @param rx  The x-radius of the rounded corners on the round-rectangle
     * @param ry  The y-radius of the rounded corners on the round-rectangle
     * @param dir The direction to wind the round-rectangle's contour
     */
    public void addRoundRect(float left, float top, float right, float bottom, float rx, float ry,
                             Path.Direction dir) {
        Compat.addRoundRect(mPath, left, top, right, bottom, rx, ry, dir);
    }

    /**
     * Add a closed round-rectangle contour to the path. Each corner receives
     * two radius values [X, Y]. The corners are ordered top-left, top-right,
     * bottom-right, bottom-left
     *
     * @param radii Array of 8 values, 4 pairs of [X,Y] radii
     * @param dir   The direction to wind the round-rectangle's contour
     */
    public void addRoundRect(float left, float top, float right, float bottom, float[] radii,
                             Path.Direction dir) {
        Compat.addRoundRect(mPath, left, top, right, bottom, radii, dir);
    }

    /**
     * Set the path's fill type. This defines how "inside" is computed.
     *
     * @param ft The new fill type for this path
     */
    public void setFillType(Path.FillType ft) {
        if (mPath.getFillType() == ft)
            return;
        mPath.setFillType(ft);
        mDrawPath.setFillType(ft);
        invalidateSelf();
    }

    /**
     * Return the path's fill type. This defines how "inside" is
     * computed. The default value is WINDING.
     *
     * @return the path's fill type
     */
    public Path.FillType getFillType() {
        return mPath.getFillType();
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
     * Return the paint's stroke join type.
     *
     * @return the paint's Join.
     */
    public Paint.Join getStrokeJoin() {
        return mPaint.getStrokeJoin();
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
     * 设置背景颜色
     *
     * @param color 颜色
     */
    public void setBackgroundColor(ColorStateList color) {
        if (mBackgroundColor == color)
            return;
        mBackgroundColor = color;
        invalidateSelf();
    }

    /**
     * 设置背景颜色
     *
     * @param color 颜色
     */
    public void setBackgroundColor(int color) {
        setBackgroundColor(ColorStateList.valueOf(color));
    }

    /**
     * 获取背景色
     *
     * @return 背景色
     */
    public ColorStateList getBackgroundColor() {
        return mBackgroundColor;
    }

    /**
     * 设置Drawable尺寸
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(int width, int height) {
        if (mWidth == width && mHeight == height)
            return;
        mWidth = width;
        mHeight = height;
        invalidateSelf();
    }

    /**
     * 设置路径窗口尺寸
     *
     * @param width  宽
     * @param height 高
     */
    public void setViewport(float width, float height) {
        if (mViewportWidth == width && mViewportHeight == height)
            return;
        mViewportWidth = width;
        mViewportHeight = height;
        invalidatePath();
    }

    /**
     * 获取路径窗口宽度
     *
     * @return 宽度
     */
    public float getViewportWidth() {
        return mViewportWidth;
    }

    /**
     * 获取路径窗口高度
     *
     * @return 高度
     */
    public float getViewportHeight() {
        return mViewportHeight;
    }

    /**
     * 设置路径数据
     *
     * @param data 数据
     */
    public void setData(String data) {
        setPathData(data);
        invalidatePath();
    }

    /**
     * Replace the contents of this with the contents of path.
     */
    public void setPath(Path path) {
        mPath.set(path);
        invalidatePath();
    }

    /**
     * 设置充填色
     *
     * @param color 颜色
     */
    public void setFillColor(ColorStateList color) {
        if (mFillColor == color)
            return;
        mFillColor = color;
        updateStyle();
        invalidateSelf();
    }

    /**
     * 设置充填色
     *
     * @param color 颜色
     */
    public void setFillColor(int color) {
        setFillColor(ColorStateList.valueOf(color));
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
     * 设置描边色
     *
     * @param color 颜色
     */
    public void setStrokeColor(ColorStateList color) {
        if (mStrokeColor == color)
            return;
        mStrokeColor = color;
        updateStyle();
        invalidateSelf();
    }

    /**
     * 设置描边色
     *
     * @param color 颜色
     */
    public void setStrokeColor(int color) {
        setStrokeColor(ColorStateList.valueOf(color));
    }

    /**
     * 获取描边色
     *
     * @return 描边色
     */
    public ColorStateList getStrokeColor() {
        return mStrokeColor;
    }

    /**
     * 设置描边线宽
     *
     * @param width 线宽
     */
    public void setStrokeWidth(float width) {
        if (mStrokeWidth == width)
            return;
        mStrokeWidth = width;
        invalidateSelf();
    }

    /**
     * 获取描边线宽
     *
     * @return 描边线宽
     */
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * 设置描边线缩放类型
     *
     * @param type 缩放类型
     */
    public void setStrokeWidthScaleType(int type) {
        if (mStrokeWidthScaleType == type)
            return;
        mStrokeWidthScaleType = type;
        invalidateSelf();
    }

    /**
     * 获取描边线缩放类型
     *
     * @return 缩放类型
     */
    public int getStrokeWidthScaleType() {
        return mStrokeWidthScaleType;
    }

    /**
     * Returns true if the path is empty (contains no lines or curves)
     *
     * @return true if the path is empty (contains no lines or curves)
     */
    public boolean isEmpty() {
        return mPath.isEmpty();
    }

    /**
     * Clear any lines and curves from the path, making it empty.
     * This does NOT change the fill-type setting.
     */
    public void reset() {
        mPath.reset();
        invalidatePath();
    }

    /**
     * Rewinds the path: clears any lines and curves from the path but
     * keeps the internal data structure for faster reuse.
     */
    public void rewind() {
        mPath.rewind();
        invalidatePath();
    }

    /**
     * Compute the bounds of the control points of the path, and write the
     * answer into bounds. If the path contains 0 or 1 points, the bounds is
     * set to (0,0,0,0)
     *
     * @param bounds Returns the computed bounds of the path's control points.
     * @param exact  This parameter is no longer used.
     */
    public void computeBounds(RectF bounds, boolean exact) {
        mPath.computeBounds(bounds, exact);
    }

    /**
     * Hint to the path to prepare for adding more points. This can allow the
     * path to more efficiently allocate its storage.
     *
     * @param extraPtCount The number of extra points that may be added to this
     *                     path
     */
    public void incReserve(int extraPtCount) {
        mPath.incReserve(extraPtCount);
        invalidatePath();
    }

    /**
     * Returns true if the filltype is one of the INVERSE variants
     *
     * @return true if the filltype is one of the INVERSE variants
     */
    public boolean isInverseFillType() {
        return mPath.isInverseFillType();
    }

    /**
     * Returns true if the path specifies a rectangle. If so, and if rect is
     * not null, set rect to the bounds of the path. If the path does not
     * specify a rectangle, return false and ignore rect.
     *
     * @param rect If not null, returns the bounds of the path if it specifies
     *             a rectangle
     * @return true if the path specifies a rectangle
     */
    public boolean isRect(RectF rect) {
        return mPath.isRect(rect);
    }

    /**
     * Offset the path by (dx,dy)
     *
     * @param dx The amount in the X direction to offset the entire path
     * @param dy The amount in the Y direction to offset the entire path
     */
    public void offset(float dx, float dy) {
        mPath.offset(dx, dy);
        invalidatePath();
    }

    /**
     * Offset the path by (dx,dy)
     *
     * @param dx  The amount in the X direction to offset the entire path
     * @param dy  The amount in the Y direction to offset the entire path
     * @param dst The translated path is written here. If this is null, then
     *            the original path is modified.
     */
    public void offset(float dx, float dy, Path dst) {
        mPath.offset(dx, dy, dst);
        if (dst == null) {
            invalidatePath();
        }
    }

    /**
     * Set this path to the result of applying the Op to this path and the specified path.
     * The resulting path will be constructed from non-overlapping contours.
     * The curve order is reduced where possible so that cubics may be turned
     * into quadratics, and quadratics maybe turned into lines.
     *
     * @param path The second operand (for difference, the subtrahend)
     * @return True if operation succeeded, false otherwise and this path remains unmodified.
     * @see Path.Op
     * @see #op(Path, Path, Path.Op)
     */
    public boolean op(Path path, Path.Op op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mPath.op(path, op)) {
            invalidatePath();
            return true;
        }
        return false;
    }

    /**
     * Set this path to the result of applying the Op to the two specified paths.
     * The resulting path will be constructed from non-overlapping contours.
     * The curve order is reduced where possible so that cubics may be turned
     * into quadratics, and quadratics maybe turned into lines.
     *
     * @param path1 The first operand (for difference, the minuend)
     * @param path2 The second operand (for difference, the subtrahend)
     * @return True if operation succeeded, false otherwise and this path remains unmodified.
     * @see Path.Op
     * @see #op(Path, Path.Op)
     */
    public boolean op(Path path1, Path path2, Path.Op op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && mPath.op(path1, path2, op)) {
            invalidatePath();
            return true;
        }
        return false;
    }

    /**
     * Sets the last point of the path.
     *
     * @param dx The new X coordinate for the last point
     * @param dy The new Y coordinate for the last point
     */
    public void setLastPoint(float dx, float dy) {
        mPath.setLastPoint(dx, dy);
        invalidatePath();
    }

    /**
     * Toggles the INVERSE state of the filltype
     */
    public void toggleInverseFillType() {
        mPath.toggleInverseFillType();
        mDrawPath.setFillType(mPath.getFillType());
        invalidateSelf();
    }

    /**
     * Set or clear the patheffect object.
     * <p/>
     * Pass null to clear any previous patheffect.
     * As a convenience, the parameter passed is also returned.
     *
     * @param effect May be null. The patheffect to be installed in the paint
     * @return effect
     */
    public PathEffect setPathEffect(PathEffect effect) {
        final PathEffect old = mPaint.setPathEffect(effect);
        invalidateSelf();
        return old;
    }

    /**
     * Get the paint's patheffect object.
     *
     * @return the paint's patheffect (or null)
     */
    public PathEffect getPathEffect() {
        return mPaint.getPathEffect();
    }
}
