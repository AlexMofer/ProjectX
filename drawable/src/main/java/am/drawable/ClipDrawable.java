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

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 裁剪Drawable
 * Created by Alex on 2019/1/11.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ClipDrawable extends DrawableWrapper {

    /**
     * 圆形
     */
    public static final ClipOutlineProvider CIRCLE = new ClipOutlineProvider() {

        @Override
        public void getOutline(Rect bounds, Path outline) {
            outline.addCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                    Math.min(bounds.width(), bounds.height()) * 0.5f, Path.Direction.CW);
        }
    };
    /**
     * 椭圆
     */
    public static final ClipOutlineProvider OVAL = new ClipOutlineProvider() {

        @Override
        public void getOutline(Rect bounds, Path outline) {
            Compat.addOval(outline, bounds.left, bounds.top, bounds.right, bounds.bottom,
                    Path.Direction.CW);
        }
    };
    /**
     * 圆角矩形（短边为半圆）
     */
    public static final ClipOutlineProvider FULL_ROUND_RECT = new ClipOutlineProvider() {

        @Override
        public void getOutline(Rect bounds, Path outline) {
            final float radius = Math.min(bounds.width(), bounds.height()) * 0.5f;
            Compat.addRoundRect(outline, bounds.left, bounds.top, bounds.right, bounds.bottom,
                    radius, radius, Path.Direction.CW);
        }
    };
    private final Path mClipPath = new Path();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private final Path mOutlinePath = new Path();
    private ClipOutlineProvider mProvider;
    private ColorStateList mStrokeColor;
    private int mAlpha = 0xFF;

    public ClipDrawable() {
        this(null, null);
    }

    public ClipDrawable(Drawable drawable, ClipOutlineProvider provider) {
        this(drawable, provider, 0, null);
    }

    public ClipDrawable(Drawable drawable, ClipOutlineProvider provider, float strokeWidth,
                        int strokeColor) {
        this(drawable, provider, strokeWidth, ColorStateList.valueOf(strokeColor));
    }

    public ClipDrawable(Drawable drawable, ClipOutlineProvider provider, float strokeWidth,
                        ColorStateList strokeColor) {
        super(drawable);
        mClipPath.setFillType(Path.FillType.EVEN_ODD);
        mOutlinePath.setFillType(Path.FillType.EVEN_ODD);
        mProvider = provider;
        mPaint.setStrokeWidth(strokeWidth * 2);
        mStrokeColor = strokeColor;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.ClipDrawable);
        final int type = custom.getInt(R.styleable.ClipDrawable_cdClipType, 0);
        final float radius = custom.getDimension(R.styleable.ClipDrawable_cdRoundRectRadius,
                0);
        float strokeWidth = custom.getFloat(R.styleable.ClipDrawable_android_strokeWidth,
                0);
        strokeWidth = custom.getDimension(R.styleable.ClipDrawable_cdStrokeWidth,
                strokeWidth);
        mStrokeColor = custom.getColorStateList(R.styleable.ClipDrawable_android_strokeColor);
        final int strokeLineCap = custom.getInt(R.styleable.ClipDrawable_android_strokeLineCap,
                0);
        final int strokeLineJoin = custom.getInt(R.styleable.ClipDrawable_android_strokeLineJoin,
                0);
        final float strokeMiterLimit = custom.getFloat(
                R.styleable.ClipDrawable_android_strokeMiterLimit, 0);
        custom.recycle();
        setWrappedDrawableFormText(resources, parser, attrs, theme);
        switch (type) {
            default:
                break;
            case 1:
                mProvider = CIRCLE;
                break;
            case 2:
                mProvider = OVAL;
            case 3:
                mProvider = FULL_ROUND_RECT;
            case 4:
                mProvider = new RoundRectClipOutlineProvider(radius);
        }
        mPaint.setStrokeWidth(strokeWidth * 2);
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
    }

    @Override
    public void draw(Canvas canvas) {
        if (mProvider == null) {
            super.draw(canvas);
            return;
        }
        final Rect bounds = getBounds();
        final int layer = Compat.saveLayer(canvas, bounds.left, bounds.top, bounds.right,
                bounds.bottom, null);
        super.draw(canvas);
        if (mStrokeColor != null) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(DrawableHelper.getColor(mStrokeColor, getState(), mAlpha));
            canvas.drawPath(mOutlinePath, mPaint);
        }
        mPaint.setStyle(Paint.Style.FILL);
        final ColorFilter filter = mPaint.getColorFilter();
        mPaint.setColorFilter(null);
        mPaint.setXfermode(mXfermode);
        canvas.drawPath(mClipPath, mPaint);
        canvas.restoreToCount(layer);
        mPaint.setXfermode(null);
        mPaint.setColorFilter(filter);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        invalidateClipOutline(false);
    }

    private void invalidateClipOutline(boolean redraw) {
        if (mProvider == null)
            return;
        final Rect bounds = getBounds();
        mClipPath.reset();
        mClipPath.addRect(bounds.left - 1, bounds.top - 1,
                bounds.right + 1, bounds.bottom + 1,
                Path.Direction.CW);
        mOutlinePath.reset();
        mProvider.getOutline(bounds, mOutlinePath);
        mClipPath.addPath(mOutlinePath);
        if (redraw)
            invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        super.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        super.setColorFilter(cf);
        mPaint.setColorFilter(cf);
    }

    @Override
    public boolean isStateful() {
        return super.isStateful() || (mStrokeColor != null && mStrokeColor.isStateful());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(@SuppressWarnings("NullableProblems") Outline outline) {
        if (mProvider == null) {
            super.getOutline(outline);
            return;
        }
        outline.setConvexPath(mOutlinePath);
        outline.setAlpha(1);
    }

    /**
     * 获取裁剪提供器
     *
     * @return 裁剪提供器
     */
    public ClipOutlineProvider getClipOutlineProvider() {
        return mProvider;
    }

    /**
     * 设置裁剪提供器
     *
     * @param provider 裁剪提供器
     */
    public void setClipOutlineProvider(ClipOutlineProvider provider) {
        if (mProvider == provider)
            return;
        mProvider = provider;
        invalidateClipOutline(true);
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
     * 设置描边色
     *
     * @param color 描边色
     */
    public void setStrokeColor(int color) {
        setStrokeColor(ColorStateList.valueOf(color));
    }

    /**
     * 设置描边色
     *
     * @param color 描边色
     */
    public void setStrokeColor(ColorStateList color) {
        if (mStrokeColor == color)
            return;
        mStrokeColor = color;
        if (mProvider != null)
            invalidateSelf();
    }

    /**
     * Return the width for stroking.
     * <p/>
     * A value of 0 strokes in hairline mode.
     * Hairlines always draws a single pixel independent of the canva's matrix.
     *
     * @return the paint's stroke width, used whenever the paint's style is
     * Stroke or StrokeAndFill.
     */
    public float getStrokeWidth() {
        return mPaint.getStrokeWidth() * 2;
    }

    /**
     * Set the width for stroking.
     * Pass 0 to stroke in hairline mode.
     * Hairlines always draws a single pixel independent of the canva's matrix.
     *
     * @param width set the paint's stroke width, used whenever the paint's
     *              style is Stroke or StrokeAndFill.
     */
    public void setStrokeWidth(float width) {
        if (mPaint.getStrokeWidth() == width * 2)
            return;
        mPaint.setStrokeWidth(width * 2);
        if (mProvider != null)
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
        if (mProvider != null)
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
     * Set the paint's Cap.
     *
     * @param cap set the paint's line cap style, used whenever the paint's
     *            style is Stroke or StrokeAndFill.
     */
    public void setStrokeCap(Paint.Cap cap) {
        if (mPaint.getStrokeCap() == cap)
            return;
        mPaint.setStrokeCap(cap);
        if (mProvider != null)
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
        if (mProvider != null)
            invalidateSelf();
    }

    /**
     * 裁剪轮廓提供器
     */
    public static abstract class ClipOutlineProvider {

        /**
         * 获取裁剪轮廓
         *
         * @param bounds  边界
         * @param outline 路径
         */
        public abstract void getOutline(Rect bounds, Path outline);
    }

    /**
     * 圆角矩形
     */
    public static final class RoundRectClipOutlineProvider extends ClipOutlineProvider {

        private float mRadius;

        public RoundRectClipOutlineProvider(float radius) {
            mRadius = radius;
        }

        @Override
        public void getOutline(Rect bounds, Path outline) {
            Compat.addRoundRect(outline, bounds.left, bounds.top, bounds.right, bounds.bottom,
                    mRadius, mRadius, Path.Direction.CW);
        }

        /**
         * 设置圆角半径
         *
         * @param radius 圆角半径
         */
        public void setRadius(float radius) {
            mRadius = radius;
        }
    }
}
