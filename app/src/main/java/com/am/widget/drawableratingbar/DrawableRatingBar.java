package com.am.widget.drawableratingbar;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 图片评级
 * TODO 添加控制操作
 * Created by Alex on 2015/8/31.
 */
public class DrawableRatingBar extends View {
    private static final int[] ATTRS = new int[]{android.R.attr.progressDrawable, android.R.attr.drawablePadding};
    private Drawable mProgressDrawable;
    private Drawable mSecondaryProgress;
    private int mDrawablePadding;
    private int mNumStars = 5;
    private int mRating = 0;

    public DrawableRatingBar(Context context) {
        this(context, null);
    }

    public DrawableRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    public DrawableRatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        Drawable progressDrawable = a.getDrawable(0);
        if (progressDrawable != null && progressDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) progressDrawable;
            mProgressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            mSecondaryProgress = layerDrawable.findDrawableByLayerId(android.R.id.secondaryProgress);
        }
        mDrawablePadding = a.getDimensionPixelSize(1, 0);
        a.recycle();
        if (isInEditMode())
            mRating = 4;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int progressDrawableWidth = mProgressDrawable == null ? 0 : mProgressDrawable.getIntrinsicWidth();
        final int progressDrawableHeight = mProgressDrawable == null ? 0 : mProgressDrawable.getIntrinsicHeight();
        final int secondaryProgressWidth = mSecondaryProgress == null ? 0 : mSecondaryProgress.getIntrinsicWidth();
        final int secondaryProgressHeight = mSecondaryProgress == null ? 0 : mSecondaryProgress.getIntrinsicHeight();
        int measureWidth = progressDrawableWidth * mRating + secondaryProgressWidth * (mNumStars - mRating)
                + mDrawablePadding * (mNumStars - 1) + ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this);
        int measureHeight = Math.max(progressDrawableHeight, secondaryProgressHeight) + getPaddingBottom() + getPaddingTop();
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                measureWidth = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                measureWidth = Math.min(measureWidth, widthSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                // do nothing
                break;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                measureHeight = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                measureHeight = Math.min(measureHeight, heightSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                // do nothing
                break;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawSecondaryProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        if (mProgressDrawable == null)
            return;
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int progressDrawableWidth = mProgressDrawable.getIntrinsicWidth();
        final int progressDrawableHeight = mProgressDrawable.getIntrinsicHeight();
        mProgressDrawable.setBounds(0, 0, progressDrawableWidth, progressDrawableHeight);

        canvas.save();
        canvas.translate(paddingStart, paddingTop);
        for (int i = 0; i < mRating; i++) {
            mProgressDrawable.draw(canvas);
            canvas.translate(progressDrawableWidth + mDrawablePadding, 0);
        }
        canvas.restore();
    }

    private void drawSecondaryProgress(Canvas canvas) {
        if (mSecondaryProgress == null)
            return;
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int progressDrawableWidth = mProgressDrawable == null ? 0 : mProgressDrawable.getIntrinsicWidth();
        final int secondaryProgressWidth = mSecondaryProgress.getIntrinsicWidth();
        final int secondaryProgressHeight = mSecondaryProgress.getIntrinsicHeight();
        mSecondaryProgress.setBounds(0, 0, secondaryProgressWidth, secondaryProgressHeight);
        canvas.save();
        canvas.translate(paddingStart, paddingTop);
        canvas.translate((progressDrawableWidth + mDrawablePadding) * mRating, 0);
        for (int i = mRating; i < mNumStars; i++) {
            mSecondaryProgress.draw(canvas);
            canvas.translate(secondaryProgressWidth + mDrawablePadding, 0);
        }
        canvas.restore();

    }

    public int getNumStars() {
        return mNumStars;
    }

    public void setNumStars(int numStars) {
        if (numStars > 0 && numStars != mNumStars) {
            this.mNumStars = numStars;
            mRating = mRating > mNumStars ? mNumStars : mRating;
            requestLayout();
            invalidate();
        }
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int rating) {
        if (rating >= 0 && rating <= mNumStars && rating != mRating) {
            this.mRating = rating;
            requestLayout();
            invalidate();
        }
    }

    public void setRatingDrawable(Drawable progress, Drawable secondaryProgress) {
        boolean invalidate = false;
        if (progress != mProgressDrawable) {
            mProgressDrawable = progress;
            invalidate = true;
        }
        if (secondaryProgress != mSecondaryProgress) {
            mSecondaryProgress = secondaryProgress;
            invalidate = true;
        }
        if (invalidate) {
            requestLayout();
            invalidate();
        }
    }


}
