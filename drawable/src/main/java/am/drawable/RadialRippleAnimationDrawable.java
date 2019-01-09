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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import am.widget.R;

/**
 * 镜像波纹动画Drawable
 * Created by Alex on 2019/1/8.
 */
@SuppressWarnings("NullableProblems")
public class RadialRippleAnimationDrawable extends AnimationDrawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mAlpha = 0xFF;
    private float mRadius = 0;
    private ColorStateList mStartColor;
    private ColorStateList mEndColor;
    private float mRippleSize;
    private float mStartSize;
    private final ArrayList<Float> tStops = new ArrayList<>();
    private final ArrayList<Integer> tColors = new ArrayList<>();

    public RadialRippleAnimationDrawable() {
        setDuration(3000);
        setRepeatMode(RESTART);
        setRepeatCount(INFINITE);
    }

    public RadialRippleAnimationDrawable(int startColor, int endColor) {
        setDuration(3000);
        setRepeatMode(RESTART);
        setRepeatCount(INFINITE);
        mStartColor = ColorStateList.valueOf(startColor);
        mEndColor = ColorStateList.valueOf(endColor);
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.RadialRippleAnimationDrawable);
        final ColorStateList start = custom.getColorStateList(
                R.styleable.RadialRippleAnimationDrawable_android_startColor);
        final ColorStateList end = custom.getColorStateList(
                R.styleable.RadialRippleAnimationDrawable_android_endColor);

        // TODO
        custom.recycle();
        mStartColor = start != null ? start : ColorStateList.valueOf(0x80000000);
        mEndColor = end != null ? end : ColorStateList.valueOf(0x10000000);
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isVisible())
            return;
        if (mRippleSize <= 0)
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
        final float p = mRippleSize / radius;
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
        if (mStartSize < radius) {
            mStartSize = (getRepeatCompletedCount() + animate) * mRippleSize;
            if (mStartSize < radius)
                canvas.drawCircle(x, y, mStartSize, mPaint);
            else
                canvas.drawCircle(x, y, radius, mPaint);
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
        mRippleSize = mRadius * 0.3f;// TODO
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
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        // TODO
        start();
    }

    @Override
    protected void onAnimationUpdate() {
        super.onAnimationUpdate();
        invalidateSelf();
    }

    @Override
    protected void start() {
        mStartSize = 0;
        super.start();
    }
}
