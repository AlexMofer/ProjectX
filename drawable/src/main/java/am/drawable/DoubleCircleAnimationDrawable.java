/*
 * Copyright (C) 2019 AlexMofer
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

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 双圈动图
 * Created by Alex on 2019/1/11.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class DoubleCircleAnimationDrawable extends AnimationDrawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mAlpha = 0xFF;
    private int mWidth;
    private int mHeight;
    private ColorStateList mStartColor;
    private ColorStateList mEndColor;
    private float mSpacing;
    private boolean mConstantSize;
    private float mMaxRadius;
    private float mMinRadius;
    private float mRadiusScale;

    public DoubleCircleAnimationDrawable() {
        this(0xfff75656, 0xff569af7, 0.5f);
    }

    public DoubleCircleAnimationDrawable(int start, int end, float radiusScale) {
        this(start, end, 0, radiusScale);
    }

    public DoubleCircleAnimationDrawable(int start, int end, float spacing, float radiusScale) {
        this(ColorStateList.valueOf(start), ColorStateList.valueOf(end), spacing, radiusScale);
    }

    public DoubleCircleAnimationDrawable(ColorStateList start, ColorStateList end, float spacing,
                                         float radiusScale) {
        this(-1, -1, start, end, spacing, radiusScale);
    }

    public DoubleCircleAnimationDrawable(int width, int height, ColorStateList start,
                                         ColorStateList end, float spacing, float radiusScale) {
        this(width, height, start, end, spacing, radiusScale, false);
    }

    public DoubleCircleAnimationDrawable(int width, int height, ColorStateList start,
                                         ColorStateList end, float spacing, float radiusScale,
                                         boolean constantSize) {
        mWidth = width;
        mHeight = height;
        mStartColor = start;
        mEndColor = end;
        mSpacing = spacing;
        mConstantSize = constantSize;
        mRadiusScale = radiusScale;
        setRepeatMode(REVERSE);
        setRepeatCount(INFINITE);
        setAutoStart(true);
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.DoubleCircleAnimationDrawable);
        mWidth = custom.getDimensionPixelSize(
                R.styleable.DoubleCircleAnimationDrawable_android_width, -1);
        mHeight = custom.getDimensionPixelSize(
                R.styleable.DoubleCircleAnimationDrawable_android_height, -1);
        mStartColor = custom.getColorStateList(
                R.styleable.DoubleCircleAnimationDrawable_android_startColor);
        mEndColor = custom.getColorStateList(
                R.styleable.DoubleCircleAnimationDrawable_android_endColor);
        mSpacing = custom.getDimension(
                R.styleable.DoubleCircleAnimationDrawable_android_spacing, 0);
        mConstantSize = custom.getBoolean(
                R.styleable.DoubleCircleAnimationDrawable_android_constantSize, false);
        mRadiusScale = custom.getFloat(R.styleable.DoubleCircleAnimationDrawable_dcRadiusScale,
                1f);
        custom.recycle();
    }

    @Override
    public void draw(Canvas canvas) {
        final float halfSpacing = mSpacing * 0.5f;
        final float animate = getAnimatedValue();
        final float offset = mMaxRadius - mMinRadius;
        final float startRadius = mMinRadius + animate * offset;
        final float endRadius = mMinRadius + (1 - animate) * offset;
        final Rect bounds = getBounds();
        final float centerX = bounds.exactCenterX();
        final float centerY = bounds.exactCenterY();
        if (Compat.isLayoutDirectionLTR(this)) {
            mPaint.setColor(DrawableHelper.getColor(mStartColor, getState(), mAlpha));
            canvas.drawCircle(centerX - halfSpacing - startRadius, centerY, startRadius,
                    mPaint);
            mPaint.setColor(DrawableHelper.getColor(mEndColor, getState(), mAlpha));
            canvas.drawCircle(centerX + halfSpacing + endRadius, centerY, endRadius,
                    mPaint);
        } else {
            mPaint.setColor(DrawableHelper.getColor(mEndColor, getState(), mAlpha));
            canvas.drawCircle(centerX - halfSpacing - endRadius, centerY, endRadius,
                    mPaint);
            mPaint.setColor(DrawableHelper.getColor(mStartColor, getState(), mAlpha));
            canvas.drawCircle(centerX + halfSpacing + startRadius, centerY, startRadius,
                    mPaint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        updateLocation();
    }

    private void updateLocation() {
        if (mConstantSize) {
            mMaxRadius = (Math.min(mWidth, mHeight) - mSpacing) * 0.25f;
            mMinRadius = mMaxRadius * mRadiusScale;
        } else {
            final Rect bounds = getBounds();
            mMaxRadius = (Math.min(bounds.width(), bounds.height()) - mSpacing) * 0.25f;
            mMinRadius = mMaxRadius * mRadiusScale;
        }
    }

    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mAlpha == alpha)
            return;
        mAlpha = alpha;
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

    @Override
    public int getAlpha() {
        return mAlpha;
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
        return (mStartColor != null && mStartColor.isStateful()) ||
                (mEndColor != null && mEndColor.isStateful());
    }

    @Override
    public long getDuration() {
        return super.getDuration();
    }

    @Override
    public void setDuration(long duration) {
        super.setDuration(duration);
    }

    @Override
    public long getFrameDelay() {
        return super.getFrameDelay();
    }

    @Override
    public void setFrameDelay(long frameDelay) {
        super.setFrameDelay(frameDelay);
    }

    @Override
    public int getRepeatMode() {
        return super.getRepeatMode();
    }

    @Override
    public void setRepeatMode(int mode) {
        super.setRepeatMode(mode);
    }

    @Override
    public void setRepeatCount(int count) {
        super.setRepeatCount(count);
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        super.setInterpolator(interpolator);
    }

    @Override
    public Interpolator getInterpolator() {
        return super.getInterpolator();
    }

    @Override
    public boolean isAutoStart() {
        return super.isAutoStart();
    }

    @Override
    public void setAutoStart(boolean auto) {
        super.setAutoStart(auto);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public boolean isRunning() {
        return super.isRunning();
    }

    @Override
    public boolean isPaused() {
        return super.isPaused();
    }

    @Override
    public long getRepeatCompletedCount() {
        return super.getRepeatCompletedCount();
    }

    /**
     * 设置尺寸
     *
     * @param width  宽度
     * @param height 高度
     */
    public void setSize(int width, int height) {
        if (mWidth == width && mHeight == height)
            return;
        mWidth = width;
        mHeight = height;
        updateLocation();
        invalidateSelf();
    }

    /**
     * 设置颜色
     *
     * @param start 左边（相对）
     * @param end   右边（相对）
     */
    public void setColor(ColorStateList start, ColorStateList end) {
        if (mStartColor == start && mEndColor == end)
            return;
        mStartColor = start;
        mEndColor = end;
        invalidateSelf();
    }

    /**
     * 设置颜色
     *
     * @param start 左边（相对）
     * @param end   右边（相对）
     */
    public void setColor(int start, int end) {
        setColor(ColorStateList.valueOf(start), ColorStateList.valueOf(end));
    }

    /**
     * 获取左边颜色
     *
     * @return 左边颜色
     */
    public ColorStateList getStartColor() {
        return mStartColor;
    }

    /**
     * 获取右边颜色
     *
     * @return 右边颜色
     */
    public ColorStateList getEndColor() {
        return mEndColor;
    }

    /**
     * 设置间隔
     *
     * @param spacing 间隔
     */
    public void setSpacing(float spacing) {
        if (mSpacing == spacing)
            return;
        mSpacing = spacing;
        updateLocation();
        invalidateSelf();
    }

    /**
     * 获取间隔
     *
     * @return 间隔
     */
    public float getSpacing() {
        return mSpacing;
    }

    /**
     * 设置是否固定尺寸
     *
     * @param constantSize 是否固定尺寸
     */
    public void setConstantSize(boolean constantSize) {
        if (mConstantSize == constantSize)
            return;
        mConstantSize = constantSize;
        updateLocation();
        invalidateSelf();
    }

    /**
     * 判断是否固定尺寸
     *
     * @return 是否固定尺寸
     */
    public boolean isConstantSize() {
        return mConstantSize;
    }

    /**
     * 设置半径缩放比
     *
     * @param scale 半径缩放比
     */
    public void setRadiusScale(float scale) {
        if (mRadiusScale == scale)
            return;
        mRadiusScale = scale;
        updateLocation();
        invalidateSelf();
    }

    /**
     * 获取半径缩放比
     *
     * @return 半径缩放比
     */
    public float getRadiusScale() {
        return mRadiusScale;
    }
}
