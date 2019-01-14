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
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import am.widget.R;

/**
 * 径向渐变波纹动画Drawable
 * Created by Alex on 2019/1/8.
 */
@SuppressWarnings({"NullableProblems", "unused"})
public class RadialGradientRippleAnimationDrawable extends AnimationDrawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mAlpha = 0xFF;
    private float mRadius = 0;
    private ColorStateList mStartColor;
    private ColorStateList mEndColor;
    private float mRippleRadius;
    private float mStartClipSize;
    private int mMaxCount;
    private boolean mClipStart = true;
    private final ArrayList<Float> tStops = new ArrayList<>();
    private final ArrayList<Integer> tColors = new ArrayList<>();

    public RadialGradientRippleAnimationDrawable() {
        this(0xff800000, 0xff100000);
    }

    public RadialGradientRippleAnimationDrawable(int startColor, int endColor) {
        mStartColor = ColorStateList.valueOf(startColor);
        mEndColor = ColorStateList.valueOf(endColor);
        setRepeatMode(RESTART);
        setRepeatCount(INFINITE);
        setAutoStart(true);
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.RadialGradientRippleAnimationDrawable);
        final ColorStateList start = custom.getColorStateList(
                R.styleable.RadialGradientRippleAnimationDrawable_android_startColor);
        final ColorStateList end = custom.getColorStateList(
                R.styleable.RadialGradientRippleAnimationDrawable_android_endColor);
        mRippleRadius = custom.getDimension(
                R.styleable.RadialGradientRippleAnimationDrawable_android_radius, 0);
        mMaxCount = custom.getInteger(
                R.styleable.RadialGradientRippleAnimationDrawable_android_max, 0);
        mClipStart = custom.getBoolean(
                R.styleable.RadialGradientRippleAnimationDrawable_rrClipStart, true);
        custom.recycle();
        mStartColor = start != null ? start : ColorStateList.valueOf(0x80000000);
        mEndColor = end != null ? end : ColorStateList.valueOf(0x10000000);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isVisible())
            return;
        if (mRippleRadius <= 0)
            return;
        if (mRadius <= 0)
            return;
        final int[] state = getState();
        final int rippleShowColor = DrawableHelper.getColor(mStartColor, state, mAlpha);
        final int rippleHideColor = DrawableHelper.getColor(mEndColor, state, mAlpha);
        final Rect bounds = getBounds();
        final float x = bounds.exactCenterX();
        final float y = bounds.exactCenterY();
        final float animate = getAnimatedValue();
        final int[] colors;
        final float[] stops;
        final float radius = mRadius;
        final float p = mRippleRadius / radius;
        final float edge = 0.001f;
        final ArrayList<Float> ass = tStops;
        final ArrayList<Integer> acs = tColors;
        ass.clear();
        acs.clear();
        final float base = p * animate;
        float value = base;
        ass.add(value);
        acs.add(rippleHideColor);
        value = base - p;
        while (value > 0) {
            ass.add(0, value + p - edge);
            acs.add(0, rippleShowColor);
            ass.add(0, value);
            acs.add(0, rippleHideColor);
            value -= p;
        }
        if (value + p - edge <= 0) {
            ass.add(0, 0f);
            acs.add(0, rippleHideColor);
        } else {
            ass.add(0, value + p - edge);
            acs.add(0, rippleShowColor);
            ass.add(0, 0f);
            acs.add(0, DrawableHelper.evaluateColor(-value / p, rippleHideColor,
                    rippleShowColor));
        }
        value = base + p;
        while (value < 1) {
            ass.add(value - edge);
            acs.add(rippleShowColor);
            ass.add(value);
            acs.add(rippleHideColor);
            value += p;
        }
        if (value == 1) {
            ass.add(1f);
            acs.add(rippleShowColor);
        } else {
            ass.add(1f);
            acs.add(DrawableHelper.evaluateColor((p - (value - 1)) / p, rippleHideColor,
                    rippleShowColor));
        }
        final int size = ass.size();
        if (size <= 1)
            return;
        stops = new float[size];
        colors = new int[size];
        for (int i = 0; i < size; i++) {
            stops[i] = ass.get(i);
            colors[i] = acs.get(i);
        }
        mPaint.setShader(new RadialGradient(x, y, radius, colors, stops, Shader.TileMode.CLAMP));
        if (mClipStart) {
            if (mStartClipSize < radius) {
                mStartClipSize = (getRepeatCompletedCount() + animate) * mRippleRadius;
                if (mStartClipSize < radius)
                    canvas.drawCircle(x, y, mStartClipSize, mPaint);
                else
                    canvas.drawCircle(x, y, radius, mPaint);
            } else {
                canvas.drawCircle(x, y, radius, mPaint);
            }
        } else {
            canvas.drawCircle(x, y, radius, mPaint);
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
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        update();
    }

    private void update() {
        final Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            mRadius = 0;
            return;
        }
        final float x = bounds.exactCenterX();
        final float y = bounds.exactCenterY();
        mRadius = (float) Math.sqrt(x * x + y * y);
        if (mRippleRadius <= 0)
            mRippleRadius = mMaxCount <= 0 ? mRadius : mRadius / mMaxCount;
    }

    @Override
    public boolean isStateful() {
        return mStartColor.isStateful() || mEndColor.isStateful();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        outline.setRect(getBounds());
        outline.setAlpha(1);
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
        mStartClipSize = 0;
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
     * 设置颜色
     *
     * @param start 开始颜色
     * @param end   结束颜色
     */
    public void setColor(ColorStateList start, ColorStateList end) {
        if (start == null || end == null)
            return;
        mStartColor = start;
        mEndColor = end;
        if (isRunning()) {
            end();
            update();
            invalidateSelf();
            start();
        } else {
            update();
            invalidateSelf();
        }
    }

    /**
     * 获取开始颜色
     *
     * @return 开始颜色
     */
    public ColorStateList getStartColor() {
        return mStartColor;
    }

    /**
     * 获取结束颜色
     *
     * @return 结束颜色
     */
    public ColorStateList getEndColor() {
        return mEndColor;
    }

    /**
     * 获取水纹半径
     *
     * @return 水纹半径
     */
    public float getRippleRadius() {
        return mRippleRadius;
    }

    /**
     * 设置水纹半径
     *
     * @param radius 水纹半径
     */
    public void setRippleRadius(float radius) {
        if (mRippleRadius == radius)
            return;
        mRippleRadius = radius;
        if (isRunning()) {
            end();
            update();
            invalidateSelf();
            start();
        } else {
            update();
            invalidateSelf();
        }
    }

    /**
     * 设置在图片Drawable尺寸中水纹个数
     *
     * @param count 个数
     */
    public void setMaxRippleCountInBounds(int count) {
        if (mMaxCount == count)
            return;
        mRippleRadius = 0;
        mMaxCount = count;
        if (isRunning()) {
            end();
            update();
            invalidateSelf();
            start();
        } else {
            update();
            invalidateSelf();
        }
    }

    /**
     * 判断起始水纹是否裁剪
     *
     * @return 起始水纹是否裁剪
     */
    public boolean isClipStart() {
        return mClipStart;
    }

    /**
     * 设置起始水纹是否裁剪
     *
     * @param clip 起始水纹是否裁剪
     */
    public void setClipStart(boolean clip) {
        mClipStart = clip;
        invalidateSelf();
    }
}
