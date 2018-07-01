/*
 * Copyright (C) 2017 AlexMofer
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

package am.widget.scrollbarrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import am.widget.multifunctionalrecyclerview.R;
import am.widget.multifunctionalrecyclerview.animation.ViewAnimation;


/**
 * 默认的水平滚动条
 * Created by Alex on 2017/11/2.
 */

class DefaultScrollbarHorizontal implements DefaultScrollbar.Scrollbar {

    private final DefaultScrollbar mScrollbar;
    private final RectF mSliderBound = new RectF();
    private final RectF mScrollbarBound = new RectF();
    private final ScrollbarAnimation mAnimation = new ScrollbarAnimation();
    private Drawable mBackground;
    private int mBackgroundHeight = 0;
    private int mPaddingEdge = 0;
    private int mPaddingStart = 0;
    private int mPaddingEnd = 0;
    private Drawable mSlider;
    private int mSliderWidth;
    private int mSliderHeight;
    private boolean mShowIndicator = false;
    private int mIndicatorPadding = 0;
    private Drawable mIndicator;
    private int mIndicatorGravity;
    private boolean mIndicatorInside;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private float mTextCenterX;
    private float mTextCenterY;
    private int mTextColor;
    private int mTextSize;
    private boolean mTouchStartOnBar;
    private float mIndicatorAlpha;
    private int mAnimatorType;
    private float mAnimatorValue = 1;
    private long mAnimatorDelay = 3000;
    private IndicatorAnimation mIndicatorAnimation = new IndicatorAnimation();
    private float mScrollbarMove;
    private boolean mAlwaysTouchable;

    DefaultScrollbarHorizontal(DefaultScrollbar scrollbar) {
        mScrollbar = scrollbar;
    }

    @Override
    public void onConstruct(ScrollbarRecyclerView view, Context context, TypedArray custom) {
        final float density = context.getResources().getDisplayMetrics().density;
        if (Utils.hasValueOrEmpty(custom,
                R.styleable.ScrollbarRecyclerView_dsHorizontalBackground)) {
            mBackground = custom.getDrawable(
                    R.styleable.ScrollbarRecyclerView_dsHorizontalBackground);
        } else {
            mBackground = Utils.getDefaultBackground(context);
        }
        mPaddingEdge = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsHorizontalPaddingEdge, 0);
        mPaddingStart = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsHorizontalPaddingStart, 0);
        mPaddingEnd = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsHorizontalPaddingEnd, 0);
        if (Utils.hasValueOrEmpty(custom, R.styleable.ScrollbarRecyclerView_dsHorizontalSlider)) {
            mSlider = custom.getDrawable(R.styleable.ScrollbarRecyclerView_dsHorizontalSlider);
        } else {
            mSlider = Utils.getDefaultHorizontalSlider(context);
        }
        mIndicatorPadding = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsHorizontalIndicatorPadding, 0);
        if (Utils.hasValueOrEmpty(custom,
                R.styleable.ScrollbarRecyclerView_dsHorizontalIndicator)) {
            mIndicator = custom.getDrawable(
                    R.styleable.ScrollbarRecyclerView_dsHorizontalIndicator);
        } else {
            mIndicator = Utils.getDefaultHorizontalIndicator(context);
        }
        mIndicatorGravity = custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsHorizontalIndicatorGravity,
                DefaultScrollbar.GRAVITY_CENTER);
        mIndicatorInside = custom.getBoolean(
                R.styleable.ScrollbarRecyclerView_dsHorizontalIndicatorInside, false);
        mTextColor = custom.getColor(
                R.styleable.ScrollbarRecyclerView_dsHorizontalTextColor, Color.WHITE);
        mTextSize = custom.getDimensionPixelOffset(
                R.styleable.ScrollbarRecyclerView_dsHorizontalTextSize, (int) (density * 24));
        mTouchStartOnBar = custom.getBoolean(
                R.styleable.ScrollbarRecyclerView_dsHorizontalTouchStartOnBar, false);
        mAnimatorType = custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsHorizontalAnimator,
                DefaultScrollbar.ANIMATOR_TYPE_ALL);
        mAlwaysTouchable = custom.getBoolean(
                R.styleable.ScrollbarRecyclerView_dsHorizontalAlwaysTouchable, false);
        mBackgroundHeight = mBackground == null ? 0 : mBackground.getIntrinsicHeight();
        mSliderWidth = mSlider == null ? 0 : mSlider.getIntrinsicWidth();
        mSliderHeight = mSlider == null ? 0 : mSlider.getIntrinsicHeight();
        mIndicatorWidth = mIndicator == null ? 0 : mIndicator.getIntrinsicWidth();
        mIndicatorHeight = mIndicator == null ? 0 : mIndicator.getIntrinsicHeight();
        Interpolator interpolator = AnimationUtils.loadInterpolator(context,
                android.R.interpolator.accelerate_quad);
        mAnimatorDelay = custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsHorizontalAnimatorDelay, 3000);
        mAnimation.attach(view);
        mAnimation.setDuration(custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsHorizontalAnimatorDuration, 1500));
        mAnimation.setInterpolator(interpolator);
        if (mAnimatorType != DefaultScrollbar.ANIMATOR_TYPE_NONE)
            mAnimation.startDelayed(mAnimatorDelay);
        mIndicatorAnimation.setDuration(250);
        mIndicatorAnimation.setInterpolator(interpolator);
        mShowIndicator = view.isInEditMode();
    }

    @Override
    public void onAttachedToView(ScrollbarRecyclerView view) {
        mAnimation.attach(view);
        mIndicatorAnimation.attach(view);
    }

    @Override
    public void onDetachedFromView(ScrollbarRecyclerView view) {
        mAnimation.detach();
        mIndicatorAnimation.detach();
    }

    @Override
    public void onDraw(ScrollbarRecyclerView view, Canvas canvas, Paint paint, Rect bound) {
        final int width = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight()
                - mPaddingStart - mPaddingEnd;
        final float left = view.getPaddingLeft() + mPaddingStart;
        final float maxHeight = Math.max(mBackgroundHeight, mSliderHeight);
        final float center = view.getHeight() - view.getPaddingBottom() - mPaddingEdge
                - maxHeight * 0.5f;
        final float top = center - maxHeight * 0.5f;
        mScrollbarBound.set(left, top, left + width, top + maxHeight);
        mScrollbarMove = view.getHeight() - view.getPaddingBottom() - mScrollbarBound.top;

        if (mAnimatorValue > 0) {
            final boolean move = mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_MOVE ||
                    mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_ALL;
            if (move) {
                canvas.save();
                canvas.translate(0, mScrollbarMove - mScrollbarMove * mAnimatorValue);
            }
            drawBackground(canvas);
            drawSlider(view, canvas);
            if (move) {
                canvas.restore();
            }
        }
        drawIndicator(view, canvas);
        drawIndicatorText(view, canvas, paint, bound);
    }

    private void drawBackground(Canvas canvas) {
        if (mBackground == null)
            return;
        if (mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_HIDE ||
                mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_ALL) {
            mBackground.setAlpha(Math.round(255 * mAnimatorValue));
        }
        mBackground.setBounds(0, 0, (int) mScrollbarBound.width(), mBackgroundHeight);
        canvas.save();
        canvas.translate(mScrollbarBound.left,
                mScrollbarBound.centerY() - mBackgroundHeight * 0.5f);
        mBackground.draw(canvas);
        canvas.restore();
    }

    private void drawSlider(ScrollbarRecyclerView view, Canvas canvas) {
        if (mSlider == null)
            return;
        if (mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_HIDE ||
                mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_ALL) {
            mSlider.setAlpha(Math.round(255 * mAnimatorValue));
        }
        mSlider.setBounds(0, 0, mSliderWidth, mSliderHeight);
        final float left = mScrollbarBound.left +
                (mScrollbarBound.width() - mSliderWidth) * getSliderOffset(view);
        final float top = mScrollbarBound.centerY() - mSliderHeight * 0.5f;
        mSliderBound.set(left, top, left + mSliderWidth, top + mSliderHeight);
        canvas.save();
        canvas.translate(left, top);
        mSlider.draw(canvas);
        canvas.restore();
    }

    private float getSliderOffset(ScrollbarRecyclerView view) {
        if (view.getChildCount() <= 0)
            return 0;
        final int offset = view.computeHorizontalScrollOffset();
        final int range = view.computeHorizontalScrollRange() -
                view.computeHorizontalScrollExtent();
        if (offset <= 0) {
            return 0;
        } else if (offset >= range) {
            return 1;
        } else {
            return ((float) offset) / range;
        }
    }

    private void drawIndicator(ScrollbarRecyclerView view, Canvas canvas) {
        if (mIndicator == null)
            return;
        if (!mShowIndicator && !mIndicatorAnimation.isRunning())
            return;
        if (mShowIndicator) {
            mIndicatorAlpha = 1;
        }
        mIndicator.setAlpha(Math.round(255 * mIndicatorAlpha));
        mIndicator.setBounds(0, 0,
                mIndicatorWidth, mIndicatorHeight);
        float left = mSliderBound.centerX() - mIndicatorWidth * 0.5f;
        final float top = mScrollbarBound.top - mIndicatorPadding - mIndicatorHeight;
        switch (mIndicatorGravity) {
            case DefaultScrollbar.GRAVITY_START:
                left += mIndicatorWidth * 0.5f;
                break;
            case DefaultScrollbar.GRAVITY_END:
                left -= mIndicatorWidth * 0.5f;
                break;
        }
        if (mIndicatorInside) {
            if (left < view.getPaddingLeft()) {
                left = view.getPaddingLeft();
            } else if ((left + mIndicatorWidth) >
                    (view.getWidth() - view.getPaddingRight())) {
                left = view.getWidth() - view.getPaddingRight() - mIndicatorWidth;
            }
        }
        mTextCenterX = left + mIndicatorWidth * 0.5f;
        mTextCenterY = top + mIndicatorHeight * 0.5f;
        canvas.save();
        canvas.translate(left, top);
        mIndicator.draw(canvas);
        canvas.restore();
    }

    private void drawIndicatorText(ScrollbarRecyclerView view, Canvas canvas, Paint paint, Rect bound) {
        if (!mShowIndicator && !mIndicatorAnimation.isRunning())
            return;
        final String text = view.getScrollbarIndicator();
        if (text == null || text.length() <= 0)
            return;
        paint.setColor(mTextColor);
        paint.setAlpha(Math.round(255 * mIndicatorAlpha));
        paint.setTextSize(mTextSize);
        paint.getTextBounds(text, 0, text.length(), bound);
        canvas.save();
        canvas.translate(mTextCenterX, mTextCenterY);
        canvas.drawText(text, 0, text.length(), 0, bound.height() * 0.5f - bound.bottom,
                paint);
        canvas.restore();
    }

    @Override
    public void getTouchBound(ScrollbarRecyclerView view, RectF bound, int slop) {
        if (mAnimatorValue == 0 && !mAlwaysTouchable) {
            bound.setEmpty();
            return;
        }
        if (mTouchStartOnBar) {
            bound.set(view.getPaddingLeft() + mPaddingStart - slop,
                    mSliderBound.top - slop,
                    view.getWidth() - view.getPaddingRight() - mPaddingEnd + slop,
                    mSliderBound.bottom + slop);
        } else {
            bound.set(mSliderBound.left - slop,
                    mSliderBound.top - slop,
                    mSliderBound.right + slop,
                    mSliderBound.bottom + slop);
        }
    }

    @Override
    public boolean onTouch(ScrollbarRecyclerView view, MotionEvent event) {
        if (mAnimatorType != DefaultScrollbar.ANIMATOR_TYPE_NONE) {
            if (mAnimation.isRunning())
                mAnimation.stop();
            mAnimatorValue = 1;
        }
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mShowIndicator = true;
                down();
                mIndicatorAnimation.stop();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mShowIndicator = false;
                up();
                mIndicatorAnimation.start();
                break;
        }
        final float x = event.getX();
        final float xStart = view.getPaddingLeft() + mPaddingStart +
                mSliderHeight * 0.5f;
        final float touchWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight()
                - mPaddingStart - mPaddingEnd - mSliderHeight;
        final float offset = (x - xStart) / touchWidth;
        scroll(view, offset);
        return true;
    }

    private void down() {
        if (mSlider == null)
            return;
        if (Build.VERSION.SDK_INT >= 21) {
            mSlider.setHotspot(mSlider.getIntrinsicWidth() * 0.5f,
                    mSlider.getIntrinsicHeight() * 0.5f);
        }
        mSlider.setState(DefaultScrollbar.PRESS);
    }

    private void up() {
        if (mSlider == null)
            return;
        mSlider.setState(StateSet.NOTHING);
    }

    private void scroll(ScrollbarRecyclerView view, float percentageX) {
        final int offset = view.computeHorizontalScrollOffset();
        final int range = view.computeHorizontalScrollRange() -
                view.computeHorizontalScrollExtent();
        final int x = Math.round(percentageX * range) - offset;
        view.scrollBy(x, 0);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_NONE)
            return;
        switch (state) {
            default:
                if (mAnimation.isRunning())
                    mAnimation.stop();
                mAnimatorValue = 1;
                break;
            case ScrollbarRecyclerView.SCROLL_STATE_IDLE:
                mAnimation.startDelayed(mAnimatorDelay);
                break;
        }
    }

    @Override
    public void setPadding(int edge, int start, int end) {
        mPaddingEdge = edge;
        mPaddingStart = start;
        mPaddingEnd = end;
        mScrollbar.invalidate();
    }

    private class IndicatorAnimation extends ViewAnimation {

        @Override
        protected void onAnimate(float interpolation) {
            mIndicatorAlpha = 1 - interpolation;
            final float centerX = mTextCenterX;
            final float centerY = mTextCenterY;
            final int left = (int) Math.floor(centerX - mIndicatorWidth * 0.5f);
            final int top = (int) Math.floor(centerY - mIndicatorHeight * 0.5f);
            final int right = (int) Math.ceil(centerX + mIndicatorWidth * 0.5f);
            final int bottom = (int) Math.ceil(centerY + mIndicatorHeight * 0.5f);
            mScrollbar.invalidate(left, top, right, bottom);
        }

        @Override
        protected void onStop(float interpolation) {
            super.onStop(interpolation);
            onAnimate(interpolation);
        }
    }

    private class ScrollbarAnimation extends ViewAnimation {

        @Override
        protected void onAnimate(float interpolation) {
            mAnimatorValue = 1 - interpolation;
            final int left = (int) Math.floor(mScrollbarBound.left);
            final int top = (int) Math.floor(mScrollbarBound.top);
            final int right = (int) Math.ceil(mScrollbarBound.right);
            final int bottom = (int) Math.ceil(mScrollbarBound.top + mScrollbarMove);
            mScrollbar.invalidate(left, top, right, bottom);
        }

        @Override
        protected void onStop(float interpolation) {
            super.onStop(interpolation);
            onAnimate(interpolation);
        }
    }
}
