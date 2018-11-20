package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import am.widget.floatingactionmode.R;

/**
 * TODO 低版本Elevation效果
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class AnimationLayout extends ViewGroup implements Animation.AnimationListener {

    private final View mBackground;
    private final float mCornerRadius;
    private final Rect mBound = new Rect();
    private final Animation mAnimation;
    private final Rect mBoundStart = new Rect();
    private final Rect mBoundEnd = new Rect();
    private final int mDuration;
    private final int mDurationAdjustmentUnit;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mElevation;
    private RectF mElevationBound = new RectF();
    private boolean mStart = false;
    private OnAnimationListener mListener;

    public AnimationLayout(Context context) {
        super(context);
        setWillNotDraw(false);
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

        final GradientDrawable background = new GradientDrawable();
        background.setColor(color);
        background.setCornerRadius(radius);

        mBackground = new View(context);
        mBackground.setBackgroundDrawable(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBackground.setElevation(elevation);
        }
        addView(mBackground);

        mCornerRadius = radius;
        mElevation = elevation;

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
        mElevationBound.set(mBound);
        mElevationBound.set(mElevationBound.left - mElevation,
                mElevationBound.top - mElevation, mElevationBound.right + mElevation,
                mElevationBound.bottom + mElevation);
        canvas.drawRect(mElevationBound, mPaint);

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
        if (mListener == null)
            return;
        mListener.onAnimationChange(interpolatedTime);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mStart = false;
        mBound.set(mBoundEnd);
        requestLayout();
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
