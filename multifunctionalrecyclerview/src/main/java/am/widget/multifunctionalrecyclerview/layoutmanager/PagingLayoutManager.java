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

package am.widget.multifunctionalrecyclerview.layoutmanager;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * 分页LayoutManager
 * Created by Alex on 2017/11/6.
 */
@SuppressWarnings("all")
public class PagingLayoutManager extends BothDirectionsScrollLayoutManager {

    private final PagingOverScroller mScroller;
    private final PagingOnFlingListener mFlingListener = new PagingOnFlingListener();
    private final Rect mChildBound = new Rect();
    private boolean mPagingEnable = false;
    private int mPagingGravity = Gravity.CENTER;
    private float mPagingSplitPoint = 0.5f;
    private boolean mMultiPageScrollEnable = false;
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private boolean mForceInterceptDispatchOnScrollStateChanged = false;
    private boolean mAdjustPagingAfterLayoutComplete = false;
    private int mRecoveryPosition = -1;
    private int mRecoveryStart;


    public PagingLayoutManager(Context context) {
        super(context);
        mScroller = new PagingOverScroller(context);
    }

    public PagingLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mScroller = new PagingOverScroller(context);
    }

    public PagingLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mScroller = new PagingOverScroller(context);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        mScroller.attach(view);
        super.onAttachedToWindow(view);
        if (mPagingEnable)
            view.setOnFlingListener(mFlingListener);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        if (mPagingEnable)
            view.setOnFlingListener(null);
        mScroller.detach();
    }

    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        lp.leftMargin = 0;
        lp.rightMargin = 0;
        lp.topMargin = 0;
        lp.bottomMargin = 0;
        if (!mPagingEnable) {
            super.measureChildWithMargins(child, widthUsed, heightUsed);
            return;
        }
        super.measureChildWithMargins(child, widthUsed, heightUsed);
        if (getOrientation() == HORIZONTAL) {
            final int startDecoration = getLeftDecorationWidth(child);
            final int endDecoration = getRightDecorationWidth(child);
            final int sizeRemaining = getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                    - child.getMeasuredWidth() - startDecoration - endDecoration;
            if (sizeRemaining < 0)
                return;
            final int start = getPagingMarginStart(startDecoration, endDecoration, sizeRemaining);
            lp.leftMargin = start;
            lp.rightMargin = sizeRemaining - start;
        } else {
            final int startDecoration = getTopDecorationHeight(child);
            final int endDecoration = getBottomDecorationHeight(child);
            final int sizeRemaining = getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                    - child.getMeasuredHeight() - startDecoration - endDecoration;
            if (sizeRemaining < 0)
                return;
            final int start = getPagingMarginStart(startDecoration, endDecoration, sizeRemaining);
            lp.topMargin = start;
            lp.bottomMargin = sizeRemaining - start;
        }
    }

    private int getPagingMarginStart(int startDecoration, int endDecoration, int assignSize) {
        switch (mPagingGravity) {
            default:
                return (assignSize + endDecoration - startDecoration) / 2;
            case Gravity.LEFT:
            case Gravity.START:
                return 0;
            case Gravity.RIGHT:
            case Gravity.END:
                return assignSize;
        }
    }

    /**
     * 获取分页关键点（用于确定是聚焦哪一个子项）
     *
     * @return 关键点 0~1
     */
    public float getPagingSplitPoint() {
        return mPagingSplitPoint;
    }

    /**
     * 设置分页关键点
     *
     * @param splitPoint 关键点
     */
    public void setPagingSplitPoint(float splitPoint) {
        mPagingSplitPoint = splitPoint;
    }

    @Override
    protected boolean onInterceptDispatchOnScrollStateChanged(int state) {
        if (mForceInterceptDispatchOnScrollStateChanged)
            return true;
        if (state == RecyclerView.SCROLL_STATE_IDLE && adjustPaging(true)) {
            return true;
        }
        if (mScrollState == state)
            return true;
        mScrollState = state;
        return super.onInterceptDispatchOnScrollStateChanged(state);
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (onInterceptAdjustPagingAfterLayoutComplete())
            return;
        if (mAdjustPagingAfterLayoutComplete) {
            mAdjustPagingAfterLayoutComplete = false;
            if (mPagingEnable) {
                adjustPaging(false);
            } else {
                if (mRecoveryPosition >= 0) {
                    final int position = mRecoveryPosition;
                    mRecoveryPosition = -1;
                    final RecyclerView view = getRecyclerView();
                    if (view == null)
                        return;
                    final View target = findViewByPosition(position);
                    if (target == null)
                        return;
                    final int orientation = getOrientation();
                    if (orientation == HORIZONTAL) {
                        if (target.getLeft() == mRecoveryStart)
                            return;
                        view.scrollBy(target.getLeft() - mRecoveryStart, 0);
                    } else {
                        if (target.getTop() == mRecoveryStart)
                            return;
                        view.scrollBy(0, target.getTop() - mRecoveryStart);
                    }
                }
            }
        }
    }

    /**
     * 完成布局以后拦截分页校调
     *
     * @return 是否拦截
     */
    protected boolean onInterceptAdjustPagingAfterLayoutComplete() {
        return false;
    }

    @Override
    protected int getScrollState() {
        return mScrollState;
    }

    /**
     * 分页校调
     *
     * @param smooth 平滑滚动
     * @return 是否进行了校调
     */
    public boolean adjustPaging(boolean smooth) {
        if (!mPagingEnable)
            return false;
        if (getOrientation() == HORIZONTAL) {
            return adjustPagingHorizontal(smooth);
        } else {
            return adjustPagingVertically(smooth);
        }
    }

    private boolean adjustPagingHorizontal(boolean smooth) {
        final RecyclerView view = getRecyclerView();
        if (view == null)
            return false;
        final int childCount = getChildCount();
        if (childCount < 1)
            return false;
        final int contentStart = getPaddingLeft();
        final int contentEnd = getWidth() - getPaddingRight();
        final float splitPoint = (contentEnd - contentStart) * getPagingSplitPoint();
        float distance = Float.MAX_VALUE;
        View target = null;
        int targetStart = 0;
        int targetEnd = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            final int childStart = getDecoratedLeft(child) - params.leftMargin;
            final int childEnd = getDecoratedRight(child) + params.rightMargin;
            if (childEnd < contentStart || childStart > contentEnd) {
                // 显示区域以外
                continue;
            }
            if (childEnd == contentEnd) {
                // 触底，无需移动
                return false;
            }
            if (childStart == contentStart) {
                // 触顶，无需移动
                return false;
            }
            if (childStart <= contentStart && childEnd >= contentEnd) {
                // 占满显示区域
                return false;
            }
            // 计算跳转位置
            if (childStart <= splitPoint && childEnd >= splitPoint) {
                target = child;
                targetStart = childStart;
                targetEnd = childEnd;
                break;
            }
            if (childEnd < splitPoint) {
                final float d = splitPoint - childEnd;
                if (distance > d) {
                    distance = d;
                    target = child;
                    targetStart = childStart;
                    targetEnd = childEnd;
                }
            }
            if (childStart > splitPoint) {
                final float d = childStart - splitPoint;
                if (distance > d) {
                    distance = d;
                    target = child;
                    targetStart = childStart;
                    targetEnd = childEnd;
                }
            }
        }
        if (target == null)
            return false;
        final int moveStart = targetStart - contentStart;
        final int moveEnd = -(contentEnd - targetEnd);
        final int move = Math.abs(moveStart) > Math.abs(moveEnd) ? moveEnd : moveStart;
        if (smooth)
            view.smoothScrollBy(move, 0);
        else
            view.scrollBy(move, 0);
        return true;
    }

    private boolean adjustPagingVertically(boolean smooth) {
        final RecyclerView view = getRecyclerView();
        if (view == null)
            return false;
        final int childCount = getChildCount();
        if (childCount < 1)
            return false;
        final int contentStart = getPaddingTop();
        final int contentEnd = getHeight() - getPaddingBottom();
        final float splitPoint = (contentEnd - contentStart) * getPagingSplitPoint();
        float distance = Float.MAX_VALUE;
        View target = null;
        int targetStart = 0;
        int targetEnd = 0;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            final int childStart = getDecoratedTop(child) - params.topMargin;
            final int childEnd = getDecoratedBottom(child) + params.bottomMargin;
            if (childEnd < contentStart || childStart > contentEnd) {
                // 显示区域以外
                continue;
            }
            if (childEnd == contentEnd) {
                // 触底，无需移动
                return false;
            }
            if (childStart == contentStart) {
                // 触顶，无需移动
                return false;
            }
            if (childStart <= contentStart && childEnd >= contentEnd) {
                // 占满显示区域
                return false;
            }
            // 计算跳转位置
            if (childStart <= splitPoint && childEnd >= splitPoint) {
                target = child;
                targetStart = childStart;
                targetEnd = childEnd;
                break;
            }
            if (childEnd < splitPoint) {
                final float d = splitPoint - childEnd;
                if (distance > d) {
                    distance = d;
                    target = child;
                    targetStart = childStart;
                    targetEnd = childEnd;
                }
            }
            if (childStart > splitPoint) {
                final float d = childStart - splitPoint;
                if (distance > d) {
                    distance = d;
                    target = child;
                    targetStart = childStart;
                    targetEnd = childEnd;
                }
            }
        }
        if (target == null)
            return false;
        final int moveStart = targetStart - contentStart;
        final int moveEnd = -(contentEnd - targetEnd);
        final int move = Math.abs(moveStart) > Math.abs(moveEnd) ? moveEnd : moveStart;
        if (smooth)
            view.smoothScrollBy(0, move);
        else
            view.scrollBy(0, move);
        return true;
    }

    /**
     * 查询最靠近指定位置的子项
     *
     * @param x X轴坐标
     * @param y Y轴坐标
     * @return 子项
     */
    @Nullable
    public View findChildViewNear(float x, float y) {
        if (getChildCount() <= 0)
            return null;
        if (getChildCount() == 1)
            return getChildAt(0);
        float distance = Float.MAX_VALUE;
        View target = null;
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            getDecoratedBoundsWithMargins(child, mChildBound);
            if (getOrientation() == HORIZONTAL) {
                if ((mChildBound.left < x && mChildBound.right > x)
                        || mChildBound.left == x || mChildBound.right == y) {
                    // 点在View内部
                    return child;
                } else if (mChildBound.left > x) {
                    final float dis = mChildBound.left - x;
                    if (dis < distance) {
                        distance = dis;
                        target = child;
                    }
                } else {
                    final float dis = x - mChildBound.right;
                    if (dis < distance) {
                        distance = dis;
                        target = child;
                    }
                }
            } else {
                if ((mChildBound.top < y && mChildBound.bottom > y)
                        || mChildBound.top == y || mChildBound.bottom == y) {
                    // 点在View内部
                    return child;
                } else if (mChildBound.top > y) {
                    final float dis = mChildBound.top - y;
                    if (dis < distance) {
                        distance = dis;
                        target = child;
                    }
                } else {
                    final float dis = y - mChildBound.bottom;
                    if (dis < distance) {
                        distance = dis;
                        target = child;
                    }
                }
            }
        }
        return target;
    }

    private boolean fling(int velocityX, int velocityY) {
        if (!mPagingEnable)
            return false;
        if (mMultiPageScrollEnable)
            return false;
        if (getChildCount() <= 0)
            return false;
        final float splitX = (getWidth() - getPaddingRight() - getPaddingLeft())
                * getPagingSplitPoint();
        final float splitY = (getHeight() - getPaddingBottom() - getPaddingTop())
                * getPagingSplitPoint();
        final View target = findChildViewNear(splitX, splitY);
        if (target == null)
            return false;
        setScrollState(RecyclerView.SCROLL_STATE_SETTLING);
        getDecoratedBoundsWithMargins(target, mChildBound);
        int minX = Integer.MIN_VALUE;
        int maxX = Integer.MAX_VALUE;
        int minY = Integer.MIN_VALUE;
        int maxY = Integer.MAX_VALUE;
        final int maxOffset = computeAnotherDirectionMaxScrollOffset();
        final int offset = computeAnotherDirectionScrollOffset();
        if (getOrientation() == HORIZONTAL) {
            final int contentStart = getPaddingLeft();
            final int contentEnd = getWidth() - getPaddingRight();
            final int contentSize = contentEnd - contentStart;
            final int childStart = mChildBound.left;
            final int childEnd = mChildBound.right;
            final int childSize = mChildBound.width();
            if (velocityX > 0) {
                // 向右
                if (childSize >= contentSize && childEnd > contentEnd) {
                    // 页内飞行
                    maxX = childEnd - contentEnd;
                } else {
                    // 跨页飞行
                    maxX = childEnd - contentStart;
                }
            } else if (velocityX < 0) {
                // 向左
                if (childSize > contentSize && childStart < contentStart) {
                    // 页内飞行
                    minX = -(contentStart - childStart);
                } else {
                    // 跨页飞行
                    minX = -(contentEnd - childStart);
                }
            }
            minY = -offset;
            maxY = maxOffset - offset;
        } else {
            final int contentStart = getPaddingTop();
            final int contentEnd = getHeight() - getPaddingBottom();
            final int contentSize = contentEnd - contentStart;
            final int childStart = mChildBound.top;
            final int childEnd = mChildBound.bottom;
            final int childSize = mChildBound.height();
            if (velocityY > 0) {
                // 向下
                if (childSize >= contentSize && childEnd > contentEnd) {
                    // 页内飞行
                    maxY = childEnd - contentEnd;
                } else {
                    // 跨页飞行
                    maxY = childEnd - contentStart;
                }
            } else if (velocityY < 0) {
                // 向上
                if (childSize > contentSize && childStart < contentStart) {
                    // 页内飞行
                    minY = -(contentStart - childStart);
                } else {
                    // 跨页飞行
                    minY = -(contentEnd - childStart);
                }
            }
            minX = -offset;
            maxX = maxOffset - offset;
        }
        mScroller.fling(velocityX, velocityY, minX, maxX, minY, maxY);
        return true;
    }

    void onFlingFinish() {
        setScrollState(RecyclerView.SCROLL_STATE_IDLE);
    }

    /**
     * 判断分页是否开启
     *
     * @return 是否开启
     */
    public boolean isPagingEnable() {
        return mPagingEnable;
    }

    /**
     * 设置分页是否开启
     *
     * @param enable 是否开启
     */
    public void setPagingEnable(boolean enable) {
        if (mPagingEnable == enable)
            return;
        mPagingEnable = enable;
        final RecyclerView view = getRecyclerView();
        if (view != null) {
            view.setOnFlingListener(mPagingEnable ? mFlingListener : null);
            mAdjustPagingAfterLayoutComplete = true;
            mRecoveryPosition = -1;
            if (!mPagingEnable && getChildCount() >= 1) {
                final int orientation = getOrientation();
                View target = null;
                if (getChildCount() > 1) {
                    final int count = getChildCount();
                    final float cx = getPaddingStart() +
                            (getWidth() - getPaddingStart() - getPaddingEnd()) * 0.5f;
                    final float cy = getPaddingTop() +
                            (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
                    for (int i = 0; i < count; i++) {
                        final View child = getChildAt(i);
                        if (orientation == HORIZONTAL) {
                            if (child.getLeft() <= cx && child.getRight() >= cx) {
                                target = child;
                                break;
                            }
                        } else {
                            if (child.getTop() <= cy && child.getBottom() >= cy) {
                                target = child;
                                break;
                            }
                        }
                    }
                }
                if (target == null)
                    target = getChildAt(0);
                mRecoveryPosition = getPosition(target);
                if (orientation == HORIZONTAL) {
                    mRecoveryStart = target.getLeft();
                } else {
                    mRecoveryStart = target.getTop();
                }
            }
            requestLayout();
        }
    }

    /**
     * 获取分页子项布局方位
     *
     * @return 方位
     */
    public int getPagingGravity() {
        return mPagingGravity;
    }

    /**
     * 设置分页子项布局方位
     *
     * @param gravity 方位
     */
    public void setPagingGravity(int gravity) {
        if (mPagingGravity == gravity)
            return;
        mPagingGravity = gravity;
        requestLayout();
    }

    /**
     * 判断多页滑动是否开启
     *
     * @return 是否开启
     */
    public boolean isMultiPageScrollEnable() {
        return mMultiPageScrollEnable;
    }

    /**
     * 设置多页滑动是否开启
     *
     * @param enable 是否开启
     */
    public void setMultiPageScrollEnable(boolean enable) {
        mMultiPageScrollEnable = enable;
    }

    /**
     * 是否强制拦截分发滚动状态变化
     *
     * @param force 是否强制拦截
     */
    public void setForceInterceptDispatchOnScrollStateChanged(boolean force) {
        mForceInterceptDispatchOnScrollStateChanged = force;
    }

    private class PagingOnFlingListener extends RecyclerView.OnFlingListener {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            return fling(velocityX, velocityY);
        }
    }
}
