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
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import am.widget.multifunctionalrecyclerview.R;
import am.widget.multifunctionalrecyclerview.animation.ViewAnimation;


/**
 * 默认的垂直滚动条
 * Created by Alex on 2017/11/2.
 */

class DefaultScrollbarVertical implements DefaultScrollbar.Scrollbar {

    private final DefaultScrollbar mScrollbar;
    private final RectF mSliderBound = new RectF();
    private final RectF mScrollbarBound = new RectF();
    private final ScrollbarAnimation mAnimation = new ScrollbarAnimation();
    private Drawable mBackground;
    private int mBackgroundWidth = 0;
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

    DefaultScrollbarVertical(DefaultScrollbar scrollbar) {
        mScrollbar = scrollbar;
    }

    @Override
    public void onConstruct(ScrollbarRecyclerView view, Context context, TypedArray custom) {
        final float density = context.getResources().getDisplayMetrics().density;
        if (Utils.hasValueOrEmpty(custom, R.styleable.ScrollbarRecyclerView_dsVerticalBackground)) {
            mBackground = custom.getDrawable(
                    R.styleable.ScrollbarRecyclerView_dsVerticalBackground);
        } else {
            mBackground = Utils.getDefaultBackground(context);
        }
        mPaddingEdge = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsVerticalPaddingEdge, 0);
        mPaddingStart = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsVerticalPaddingStart, 0);
        mPaddingEnd = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsVerticalPaddingEnd, 0);
        if (Utils.hasValueOrEmpty(custom, R.styleable.ScrollbarRecyclerView_dsVerticalSlider)) {
            mSlider = custom.getDrawable(
                    R.styleable.ScrollbarRecyclerView_dsVerticalSlider);
        } else {
            mSlider = Utils.getDefaultVerticalSlider(context);
        }
        mIndicatorPadding = custom.getDimensionPixelSize(
                R.styleable.ScrollbarRecyclerView_dsVerticalIndicatorPadding, 0);
        if (Utils.hasValueOrEmpty(custom, R.styleable.ScrollbarRecyclerView_dsVerticalIndicator)) {
            mIndicator = custom.getDrawable(
                    R.styleable.ScrollbarRecyclerView_dsVerticalIndicator);
        } else {
            mIndicator = Utils.getDefaultVerticalIndicator(context);
        }
        mIndicatorGravity = custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsVerticalIndicatorGravity,
                DefaultScrollbar.GRAVITY_CENTER);
        mIndicatorInside = custom.getBoolean(
                R.styleable.ScrollbarRecyclerView_dsVerticalIndicatorInside, false);
        mTextColor = custom.getColor(
                R.styleable.ScrollbarRecyclerView_dsVerticalTextColor, Color.WHITE);
        mTextSize = custom.getDimensionPixelOffset(
                R.styleable.ScrollbarRecyclerView_dsVerticalTextSize, (int) (density * 24));
        mTouchStartOnBar = custom.getBoolean(
                R.styleable.ScrollbarRecyclerView_dsVerticalTouchStartOnBar, false);
        mAnimatorType = custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsVerticalAnimator,
                DefaultScrollbar.ANIMATOR_TYPE_ALL);
        mAlwaysTouchable = custom.getBoolean(
                R.styleable.ScrollbarRecyclerView_dsVerticalAlwaysTouchable, false);
        mBackgroundWidth = mBackground == null ? 0 :
                mBackground.getIntrinsicWidth();
        mSliderWidth = mSlider == null ? 0 : mSlider.getIntrinsicWidth();
        mSliderHeight = mSlider == null ? 0 : mSlider.getIntrinsicHeight();
        mIndicatorWidth = mIndicator == null ? 0 :
                mIndicator.getIntrinsicWidth();
        mIndicatorHeight = mIndicator == null ? 0 :
                mIndicator.getIntrinsicHeight();
        Interpolator interpolator = AnimationUtils.loadInterpolator(context,
                android.R.interpolator.accelerate_quad);
        mAnimatorDelay = custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsVerticalAnimatorDelay, 3000);
        mAnimation.attach(view);
        mAnimation.setDuration(custom.getInt(
                R.styleable.ScrollbarRecyclerView_dsVerticalAnimatorDuration, 1500));
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
    public void onDraw(ScrollbarRecyclerView view, Canvas canvas, Paint paint,
                       Rect bound) {
        final int height = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom()
                - mPaddingStart - mPaddingEnd;
        final float top = view.getPaddingTop() + mPaddingStart;
        final float maxWidth = Math.max(mBackgroundWidth, mSliderWidth);
        final float center = view.getWidth() - view.getPaddingRight() - mPaddingEdge
                - maxWidth * 0.5f;
        final float left = center - maxWidth * 0.5f;
        mScrollbarBound.set(left, top, left + maxWidth, top + height);
        mScrollbarMove = view.getWidth() - view.getPaddingRight() - mScrollbarBound.left;

        if (mAnimatorValue > 0) {
            final boolean move = mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_MOVE ||
                    mAnimatorType == DefaultScrollbar.ANIMATOR_TYPE_ALL;
            if (move) {
                canvas.save();
                canvas.translate(mScrollbarMove - mScrollbarMove * mAnimatorValue, 0);
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
        mBackground.setBounds(0, 0, mBackgroundWidth, (int) mScrollbarBound.height());
        canvas.save();
        canvas.translate(mScrollbarBound.centerX() - mBackgroundWidth * 0.5f,
                mScrollbarBound.top);
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
        final float top = mScrollbarBound.top +
                (mScrollbarBound.height() - mSliderHeight) * getSliderOffset(view);
        final float left = mScrollbarBound.centerX() - mSliderWidth * 0.5f;
        mSliderBound.set(left, top, left + mSliderWidth, top + mSliderHeight);
        canvas.save();
        canvas.translate(left, top);
        mSlider.draw(canvas);
        canvas.restore();

    }

    private float getSliderOffset(ScrollbarRecyclerView view) {
        if (view.getChildCount() <= 0)
            return 0;
        final int offset = view.computeVerticalScrollOffset();
        final int range = view.computeVerticalScrollRange() - view.computeVerticalScrollExtent();
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
        final float left = mScrollbarBound.left - mIndicatorPadding - mIndicatorWidth;
        float top = mSliderBound.centerY() - mIndicatorHeight * 0.5f;
        switch (mIndicatorGravity) {
            default:
            case DefaultScrollbar.GRAVITY_CENTER:
                break;
            case DefaultScrollbar.GRAVITY_START:
                top += mIndicatorHeight * 0.5f;
                break;
            case DefaultScrollbar.GRAVITY_END:
                top -= mIndicatorHeight * 0.5f;
                break;
        }
        if (mIndicatorInside) {
            if (top < view.getPaddingTop()) {
                top = view.getPaddingTop();
            } else if ((top + mIndicatorHeight) >
                    (view.getHeight() - view.getPaddingBottom())) {
                top = view.getHeight() - view.getPaddingBottom() - mIndicatorHeight;
            }
        }
        mTextCenterX = left + mIndicatorWidth * 0.5f;
        mTextCenterY = top + mIndicatorHeight * 0.5f;
        canvas.save();
        canvas.translate(left, top);
        mIndicator.draw(canvas);
        canvas.restore();
    }


    private void drawIndicatorText(ScrollbarRecyclerView view, Canvas canvas, Paint paint,
                                   Rect bound) {
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
        canvas.drawText(text, 0, text.length(),
                0, bound.height() * 0.5f - bound.bottom, paint);
        canvas.restore();
    }

    @Override
    public void getTouchBound(ScrollbarRecyclerView view, RectF bound, int slop) {
        if (mAnimatorValue == 0 && !mAlwaysTouchable) {
            bound.setEmpty();
            return;
        }
        if (mTouchStartOnBar) {
            bound.set(mSliderBound.left - slop,
                    view.getPaddingTop() + mPaddingStart - slop,
                    mSliderBound.right + slop,
                    view.getHeight() - view.getPaddingBottom() - mPaddingEnd
                            + slop);
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
                mIndicatorAnimation.stop();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mShowIndicator = false;
                mIndicatorAnimation.start();
                break;
        }
        final float y = event.getY();
        final float yStart = view.getPaddingTop() + mPaddingStart +
                mSliderHeight * 0.5f;
        final float touchHeight = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom()
                - mPaddingStart - mPaddingEnd - mSliderHeight;
        final float offset = (y - yStart) / touchHeight;
        scroll(view, offset);
        return true;
    }

    private void scroll(ScrollbarRecyclerView view, float percentageY) {
        final int offset = view.computeVerticalScrollOffset();
        final int range = view.computeVerticalScrollRange() -
                view.computeVerticalScrollExtent();
        final int y = Math.round(percentageY * range) - offset;
        view.scrollBy(0, y);
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
