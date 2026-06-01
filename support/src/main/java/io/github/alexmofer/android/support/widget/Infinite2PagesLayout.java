/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import java.util.ArrayList;

/**
 * 无限双页水平滚动布局
 * Created by Alex on 2026/4/14.
 */
@SuppressLint("ViewConstructor")
public final class Infinite2PagesLayout extends ViewGroup {
    public static final int STATE_IDLE = ViewDragHelper.STATE_IDLE;
    public static final int STATE_DRAGGING = ViewDragHelper.STATE_DRAGGING;
    public static final int STATE_SETTLING = ViewDragHelper.STATE_SETTLING;
    private final View mChild1;
    private final View mChild2;
    private final DragHelperCallback mDragCallback;
    private final ViewDragHelper mDragHelper;
    private final OverScroller mScroller;// 用于计算飞行距离
    private final ArrayList<OnPageChangeCallback> mCallbacks = new ArrayList<>();
    private int mCurrent;

    public Infinite2PagesLayout(@NonNull Context context,
                                @NonNull View child1, @NonNull View child2,
                                boolean secondAsCurrent) {
        super(context);
        mChild1 = child1;
        mChild2 = child2;
        mCurrent = secondAsCurrent ? 1 : 0;
        addView(mChild1);
        addView(mChild2);
        mDragCallback = new DragHelperCallback();
        mDragHelper = ViewDragHelper.create(this, mDragCallback);
        mScroller = new OverScroller(context);
    }

    public Infinite2PagesLayout(@NonNull Context context,
                                @NonNull View child1, @NonNull View child2) {
        this(context, child1, child2, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        mChild1.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        mChild2.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getWidth();
        final int height = getHeight();
        if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
            if (mCurrent == 0) {
                mChild1.layout(0, 0, width, height);
                mChild2.layout(width, 0, width + width, height);
            } else {
                mChild1.layout(-width, 0, 0, height);
                mChild2.layout(0, 0, width, height);
            }
        } else {
            mChild1.layout(mDragCallback.mChild1Left, 0,
                    mDragCallback.mChild1Left + width, height);
            mChild2.layout(mDragCallback.mChild2Left, 0,
                    mDragCallback.mChild2Left + width, height);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            postInvalidateOnAnimation();// 触发刷新，循环更新布局
        }
    }

    /**
     * 添加回调
     *
     * @param callback 回调
     * @return 添加成功时返回 true
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addOnPageChangeCallback(@NonNull OnPageChangeCallback callback) {
        return mCallbacks.add(callback);
    }

    /**
     * 移除回调
     *
     * @param callback 回调
     * @return 移除成功时返回 true
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean removeOnPageChangeCallback(@NonNull OnPageChangeCallback callback) {
        return mCallbacks.remove(callback);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        private final ArrayList<OnPageChangeCallback> mTemp = new ArrayList<>();
        private int mChild1Left;
        private int mChild2Left;

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            mTemp.addAll(mCallbacks);
            for (OnPageChangeCallback callback : mTemp) {
                callback.onPageScrollStateChanged(state);
            }
            if (state == ViewDragHelper.STATE_IDLE) {
                final int width = getWidth();
                final float centerX = width * 0.5f;
                final int child1Left;
                final int child2Left;
                final int current;
                if (mChild1.getLeft() < centerX && centerX < mChild1.getRight()) {
                    child1Left = 0;
                    child2Left = width;
                    current = 0;
                } else {
                    child1Left = -width;
                    child2Left = 0;
                    current = 1;
                }
                mChild1.offsetLeftAndRight(child1Left - mChild1.getLeft());
                mChild2.offsetLeftAndRight(child2Left - mChild2.getLeft());
                if (mCurrent != current) {
                    mCurrent = current;
                    for (OnPageChangeCallback callback : mTemp) {
                        callback.onPageSelected(current);
                    }
                }
            }
            mTemp.clear();
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top,
                                          int dx, int dy) {
            final View target = changedView == mChild1 ? mChild2 : mChild1;
            if (left <= 0) {
                // target 位于 excluded 右边
                target.offsetLeftAndRight(changedView.getRight() - target.getLeft());
            } else {
                // target 位于 excluded 左边
                target.offsetLeftAndRight(left - target.getRight());
            }
            // 记录 mChild1 与 mChild2 位置，外部触发重新布局时不会被恢复到静止位置
            mChild1Left = mChild1.getLeft();
            mChild2Left = mChild2.getLeft();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            final int releasedChildWidth = releasedChild.getWidth();
            final int parentWidth = getWidth();
            // 根据飞行距离来决定是去向哪个位置
            mScroller.forceFinished(true);
            mScroller.fling(0, 0, (int) xvel, 0,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            final float flingFinalCenterX =
                    releasedChild.getLeft() + releasedChildWidth * 0.5f + mScroller.getFinalX();
            final boolean result;
            if (flingFinalCenterX <= 0) {
                // 切换到 -1 屏
                result = mDragHelper.settleCapturedViewAt(-releasedChildWidth, 0);
            } else if (flingFinalCenterX >= parentWidth) {
                // 切换到 1 屏
                result = mDragHelper.settleCapturedViewAt(parentWidth, 0);
            } else {
                // 回正到 0 屏
                result = mDragHelper.settleCapturedViewAt(0, 0);
            }
            if (result) {
                postInvalidateOnAnimation();// 触发刷新，循环更新布局
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == mChild1 || child == mChild2;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return left;
        }
    }

    public interface OnPageChangeCallback {

        /**
         * 选中页面变化
         * 注意：
         * 1. 与 ViewPager2 不同在于该方法只在前后两次静止状态选中页面变化时触发。
         * 2. 拖动过程中的变化不会触发。
         * 3. 开始拖动时页面停留在0，经过拖动后最后还是停留在0时，该方法不会触发。
         *
         * @param position 选中页面: 0 或者 1
         */
        void onPageSelected(int position);

        /**
         * 页面滚动状态变化
         *
         * @param state 滚动状态
         */
        void onPageScrollStateChanged(int state);
    }
}
