/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import am.widget.floatingactionmode.R;

/**
 * 动画布局
 * Created by Alex on 2018/11/21.
 */
final class AnimationLayout extends ViewGroup implements Animation.AnimationListener {

    private final View mBackground;
    private final float mCornerRadius;
    private final Rect mBound = new Rect();
    private final boolean mDrawElevation;
    private final float mElevation;
    private final RectF mElevationBound;
    private final LinearGradient mElevationShader;
    private final Path mElevationCorner;
    private final RadialGradient mElevationCornerShader;
    private final int mElevationColor;
    private final Animation mAnimation;
    private final Rect mBoundStart = new Rect();
    private final Rect mBoundEnd = new Rect();
    private final int mDuration;
    private final int mDurationAdjustmentUnit;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean mStart = false;
    private OnAnimationListener mListener;

    public AnimationLayout(Context context) {
        super(context);
        final Resources resources = context.getResources();
        int color = resources.getColor(R.color.floatingActionModeBackgroundColor);
        float radius = resources.getDimension(R.dimen.floatingActionModeBackgroundCornerRadius);
        float elevation = resources.getDimension(R.dimen.floatingActionModeElevation);
        int interpolator;
        final TypedValue value = new TypedValue();
        resources.getValue(R.interpolator.floatingActionModeAnimationInterpolator, value,
                true);
        interpolator = value.resourceId;
        int duration = resources.getInteger(R.integer.floatingActionModeAnimationDuration);
        int durationAdjustmentUnit = resources.getInteger(
                R.integer.floatingActionModeAnimationDurationAdjustmentUnit);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        color = custom.getColor(
                R.styleable.FloatingActionMode_floatingActionModeBackgroundColor, color);
        radius = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeBackgroundCornerRadius, radius);
        elevation = custom.getDimension(
                R.styleable.FloatingActionMode_floatingActionModeElevation, elevation);
        interpolator = custom.getResourceId(
                R.styleable.FloatingActionMode_floatingActionModeAnimationInterpolator,
                interpolator);
        duration = custom.getInteger(
                R.styleable.FloatingActionMode_floatingActionModeAnimationDuration, duration);
        durationAdjustmentUnit = custom.getInteger(
                R.styleable.FloatingActionMode_floatingActionModeAnimationDurationAdjustmentUnit,
                durationAdjustmentUnit);
        custom.recycle();

        mCornerRadius = radius;

        final GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(radius);

        mBackground = new View(context);
        mBackground.setBackgroundDrawable(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBackground.setElevation(elevation);
            mDrawElevation = false;
            mElevation = 0;
            mElevationBound = null;
            mElevationShader = null;
            mElevationCorner = null;
            mElevationCornerShader = null;
            mElevationColor = 0;
        } else {
            setWillNotDraw(false);
            mDrawElevation = true;
            mElevation = elevation * 0.75f;
            mElevationBound = new RectF();
            mElevationColor = 0x30000000;
            mElevationShader = new LinearGradient(0, 0, mElevation, 0,
                    new int[]{mElevationColor, Color.TRANSPARENT},
                    new float[]{0, 1f}, Shader.TileMode.CLAMP);
            final float size = mCornerRadius + mElevation;
            mElevationCorner = new Path();
            mElevationCorner.setFillType(Path.FillType.EVEN_ODD);
            mElevationCorner.reset();
            mElevationCorner.moveTo(0, 0);
            mElevationCorner.lineTo(size, 0);
            mElevationCorner.lineTo(size, size);
            mElevationCorner.lineTo(0, size);
            mElevationCorner.close();
            mElevationCorner.moveTo(0, 0);
            mElevationBound.set(-mCornerRadius, -mCornerRadius, mCornerRadius, mCornerRadius);
            mElevationCorner.arcTo(mElevationBound, 0, 90);
            mElevationCorner.close();
            mElevationCornerShader = new RadialGradient(0, 0, size,
                    new int[]{mElevationColor, mElevationColor, Color.TRANSPARENT},
                    new float[]{0, mCornerRadius / size, 1f}, Shader.TileMode.CLAMP);
        }
        addView(mBackground);

        mAnimation = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                onAnimationChange(interpolatedTime, t);
            }
        };
        mAnimation.setInterpolator(context, interpolator);
        mAnimation.setAnimationListener(this);
        mDuration = duration;
        mDurationAdjustmentUnit = durationAdjustmentUnit;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBackground.measure(MeasureSpec.makeMeasureSpec(mBound.width(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mBound.height(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBackground.layout(mBound.left, mBound.top, mBound.right, mBound.bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawElevation)
            drawElevation(canvas);
    }

    private void drawElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(0, mElevation * 0.5f);
        mPaint.setColor(Color.BLACK);
        drawLeftElevation(canvas);
        drawLeftTopElevation(canvas);
        drawTopElevation(canvas);
        drawRightTopElevation(canvas);
        drawRightElevation(canvas);
        drawRightBottomElevation(canvas);
        drawBottomElevation(canvas);
        drawLeftBottomElevation(canvas);
        mPaint.setShader(null);
        mPaint.setColor(mElevationColor);
        mElevationBound.set(mBound);
        canvas.drawRoundRect(mElevationBound, mCornerRadius, mCornerRadius, mPaint);
        canvas.restore();
    }

    private void drawLeftElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.left, mBound.exactCenterY());
        canvas.rotate(180);
        final float halfHeight = mBound.height() * 0.5f;
        mElevationBound.set(0, -halfHeight + mCornerRadius,
                mElevation, halfHeight - mCornerRadius);
        mPaint.setShader(mElevationShader);
        canvas.drawRect(mElevationBound, mPaint);
        canvas.restore();
    }

    private void drawLeftTopElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.left + mCornerRadius, mBound.top + mCornerRadius);
        canvas.rotate(180);
        mPaint.setShader(mElevationCornerShader);
        canvas.drawPath(mElevationCorner, mPaint);
        canvas.restore();
    }

    private void drawTopElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.exactCenterX(), mBound.top);
        canvas.rotate(-90);
        final float halfHeight = mBound.width() * 0.5f;
        mElevationBound.set(0, -halfHeight + mCornerRadius,
                mElevation, halfHeight - mCornerRadius);
        mPaint.setShader(mElevationShader);
        canvas.drawRect(mElevationBound, mPaint);
        canvas.restore();
    }

    private void drawRightTopElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.right - mCornerRadius, mBound.top + mCornerRadius);
        canvas.rotate(-90);
        mPaint.setShader(mElevationCornerShader);
        canvas.drawPath(mElevationCorner, mPaint);
        canvas.restore();
    }

    private void drawRightElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.right, mBound.exactCenterY());
        final float halfHeight = mBound.height() * 0.5f;
        mElevationBound.set(0, -halfHeight + mCornerRadius,
                mElevation, halfHeight - mCornerRadius);
        mPaint.setShader(mElevationShader);
        canvas.drawRect(mElevationBound, mPaint);
        canvas.restore();
    }

    private void drawRightBottomElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.right - mCornerRadius, mBound.bottom - mCornerRadius);
        mPaint.setShader(mElevationCornerShader);
        canvas.drawPath(mElevationCorner, mPaint);
        canvas.restore();
    }

    private void drawBottomElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.exactCenterX(), mBound.bottom);
        canvas.rotate(90);
        final float halfHeight = mBound.width() * 0.5f;
        mElevationBound.set(0, -halfHeight + mCornerRadius,
                mElevation, halfHeight - mCornerRadius);
        mPaint.setShader(mElevationShader);
        canvas.drawRect(mElevationBound, mPaint);
        canvas.restore();
    }

    private void drawLeftBottomElevation(Canvas canvas) {
        canvas.save();
        canvas.translate(mBound.left + mCornerRadius, mBound.bottom - mCornerRadius);
        canvas.rotate(90);
        mPaint.setShader(mElevationCornerShader);
        canvas.drawPath(mElevationCorner, mPaint);
        canvas.restore();
    }

    float getCornerRadius() {
        return mCornerRadius;
    }

    @Override
    public void setVisibility(int visibility) {
        mBackground.setVisibility(visibility);
    }

    void setOnAnimationListener(OnAnimationListener listener) {
        mListener = listener;
    }

    void setBound(int left, int top, int right, int bottom, boolean animate) {
        if (!animate) {
            mBound.set(left, top, right, bottom);
            requestLayout();
            if (mDrawElevation)
                invalidate();
            return;
        }
        mBoundStart.set(mBound);
        mBoundEnd.set(left, top, right, bottom);
        mAnimation.setDuration(getAdjustedDuration(mDuration));
        mBackground.startAnimation(mAnimation);
    }

    private long getAdjustedDuration(long originalDuration) {
        final int w = mBoundStart.width() - mBoundEnd.width();
        final int h = mBoundStart.height() - mBoundStart.height();
        final double transitionDurationScale = Math.sqrt(w * w + h * h) /
                getResources().getDisplayMetrics().density;
        if (transitionDurationScale < 150) {
            return Math.max(originalDuration - mDurationAdjustmentUnit, 0);
        } else if (transitionDurationScale > 300) {
            return originalDuration + mDurationAdjustmentUnit;
        }
        return originalDuration;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mStart = true;
        if (mListener == null)
            return;
        mListener.onAnimationStart();
    }

    @SuppressWarnings("unused")
    private void onAnimationChange(float interpolatedTime, Transformation t) {
        if (!mStart)
            return;
        mBound.set(
                Math.round(mBoundStart.left +
                        (mBoundEnd.left - mBoundStart.left) * interpolatedTime),
                Math.round(mBoundStart.top +
                        (mBoundEnd.top - mBoundStart.top) * interpolatedTime),
                Math.round(mBoundStart.right +
                        (mBoundEnd.right - mBoundStart.right) * interpolatedTime),
                Math.round(mBoundStart.bottom +
                        (mBoundEnd.bottom - mBoundStart.bottom) * interpolatedTime));
        requestLayout();
        if (mDrawElevation)
            invalidate();
        if (mListener == null)
            return;
        mListener.onAnimationChange(interpolatedTime);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mStart = false;
        mBound.set(mBoundEnd);
        requestLayout();
        if (mDrawElevation)
            invalidate();
        if (mListener == null)
            return;
        mListener.onAnimationEnd();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    void cancel() {
        if (mStart)
            mAnimation.cancel();
    }

    public interface OnAnimationListener {
        void onAnimationStart();

        void onAnimationChange(float interpolatedTime);

        void onAnimationEnd();
    }
}
