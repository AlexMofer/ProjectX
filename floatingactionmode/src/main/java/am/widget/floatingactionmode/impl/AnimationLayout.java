package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
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
final class AnimationLayout extends ViewGroup {

    private final View mBackground;
    private final float mCornerRadius;
    private final Rect mBound = new Rect();
    private final SwitchAnimation mAnimation;
    private final Rect mAnimationBound = new Rect();
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

        mAnimation = new SwitchAnimation(context, interpolator);
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

    float getCornerRadius() {
        return mCornerRadius;
    }

    void setBound(Rect bound) {
        mBound.set(bound);
        requestLayout();
    }

    void setBound(int left, int top, int right, int bottom) {
        mBound.set(left, top, right, bottom);
        requestLayout();
    }

    @Override
    public void setVisibility(int visibility) {
        mBackground.setVisibility(visibility);
    }

    void setOnAnimationListener(OnAnimationListener listener) {
        mListener = listener;
    }

    void setBound(int left, int top, int right, int bottom, long duration) {
        mAnimationBound.set(left, top, right, bottom);


        mAnimation.setDuration(duration);
        startAnimation(mAnimation);
    }

    public interface OnAnimationListener {
        void onAnimationStart();

        void onAnimationChange(float interpolatedTime);

        void onAnimationEnd();
    }

    private class SwitchAnimation extends Animation implements Animation.AnimationListener {


        private boolean mStart = false;

        SwitchAnimation(Context context, int interpolator) {
            setInterpolator(context, interpolator);
            setAnimationListener(this);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (!mStart)
                return;
            if (mListener == null)
                return;
            mListener.onAnimationChange(interpolatedTime);
        }

        @Override
        public void onAnimationStart(Animation animation) {
            mStart = true;
            if (mListener == null)
                return;
            mListener.onAnimationStart();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            mStart = false;
            if (mListener == null)
                return;
            mListener.onAnimationEnd();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
