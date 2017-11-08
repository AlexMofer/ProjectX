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
    private boolean mPagingEnable = true;
    private int mPagingGravity = Gravity.CENTER;
    private float mPagingSplitPoint = 0.5f;
    private boolean mMultiPageScrollEnable = false;
    private int mScrollState = RecyclerView.SCROLL_STATE_IDLE;
    private boolean mForceInterceptDispatchOnScrollStateChanged = false;


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
     * 页模式下页分割点
     *
     * @return 分割点 0~1
     */
    public float getPagingSplitPoint() {
        return mPagingSplitPoint;
    }

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
    protected int getScrollState() {
        return mScrollState;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        if (mScrollState == RecyclerView.SCROLL_STATE_IDLE) {
            adjustPaging(false);
        }
    }

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

    private boolean fling(int velocityX, int velocityY) {
        if (!mPagingEnable)
            return false;
        if (mMultiPageScrollEnable)
            return false;
        if (getChildCount() <= 0)
            return false;
        setScrollState(RecyclerView.SCROLL_STATE_SETTLING);
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
            final View child = getChildAt(0);// TODO 获取的子项不对
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            final int childStart = getDecoratedLeft(child) - params.leftMargin;
            final int childEnd = getDecoratedRight(child) + params.rightMargin;
            final int childSize = childEnd - childStart;
            if (velocityX > 0) {
                // 向下
                if (getChildCount() == 1) {
                    // 单个子项
                    if (childSize >= contentSize && childEnd >= contentEnd) {
                        // 页内飞行
                        maxX = childEnd - contentEnd;
                    } else {
                        // 跨页飞行
                        maxX = contentSize;
                    }
                } else {
                    // 多个子项
                    // TODO 获取的子项不对
                    final View next = getChildAt(1);
                    final RecyclerView.LayoutParams nextParams = (RecyclerView.LayoutParams)
                            next.getLayoutParams();
                    final int nextStart = getDecoratedLeft(next) - nextParams.leftMargin;
                    maxX = nextStart - contentStart;
                }
            } else if (velocityX < 0) {
                // 向上
                if (getChildCount() == 1) {
                    // 单个子项
                    if (childSize > contentSize && childStart < contentStart) {
                        // 页内飞行
                        minX = -(contentStart - childStart);
                    } else {
                        // 跨页飞行
                        minX = -contentSize;
                    }
                } else {
                    // 多个子项
                    // TODO 获取的子项不对
                    final View next = getChildAt(1);
                    final RecyclerView.LayoutParams nextParams = (RecyclerView.LayoutParams)
                            next.getLayoutParams();
                    final int nextStart = getDecoratedLeft(next) - nextParams.leftMargin;
                    minX = -(contentEnd - nextStart);
                }
            }
            minY = -offset;
            maxY = maxOffset - offset;
        } else {
            final int contentStart = getPaddingTop();
            final int contentEnd = getHeight() - getPaddingBottom();
            final int contentSize = contentEnd - contentStart;
            final View child = getChildAt(0);// TODO 获取的子项不对
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            final int childStart = getDecoratedTop(child) - params.topMargin;
            final int childEnd = getDecoratedBottom(child) + params.bottomMargin;
            final int childSize = childEnd - childStart;
            if (velocityY > 0) {
                // 向下
                if (getChildCount() == 1) {
                    // 单个子项
                    if (childSize > contentSize && childEnd > contentEnd) {
                        // 页内飞行
                        maxY = childEnd - contentEnd;
                    } else {
                        // 跨页飞行
                        maxY = contentSize;
                    }
                } else {
                    // 多个子项
                    // TODO 获取的子项不对
                    final View next = getChildAt(1);
                    final RecyclerView.LayoutParams nextParams = (RecyclerView.LayoutParams)
                            next.getLayoutParams();
                    final int nextStart = getDecoratedTop(next) - nextParams.topMargin;
                    maxY = nextStart - contentStart;
                }
            } else if (velocityY < 0) {
                // 向上
                if (getChildCount() == 1) {
                    // 单个子项
                    if (childSize > contentSize && childStart < contentStart) {
                        // 页内飞行
                        minY = -(contentStart - childStart);
                    } else {
                        // 跨页飞行
                        minY = -contentSize;
                    }
                } else {
                    // 多个子项
                    // TODO 获取的子项不对
                    final View next = getChildAt(1);
                    final RecyclerView.LayoutParams nextParams = (RecyclerView.LayoutParams)
                            next.getLayoutParams();
                    final int nextStart = getDecoratedTop(next) - nextParams.topMargin;
                    minY = -(contentEnd - nextStart);
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

    public boolean isPagingEnable() {
        return mPagingEnable;
    }

    public void setPagingEnable(boolean enable) {
        if (mPagingEnable == enable)
            return;
        mPagingEnable = enable;
        final RecyclerView view = getRecyclerView();
        if (view != null) {
            view.setOnFlingListener(mPagingEnable ? mFlingListener : null);
        }
        requestLayout();
    }

    public int getPagingGravity() {
        return mPagingGravity;
    }

    public void setPagingGravity(int gravity) {
        if (mPagingGravity == gravity)
            return;
        mPagingGravity = gravity;
        requestLayout();
    }

    public boolean isMultiPageScrollEnable() {
        return mMultiPageScrollEnable;
    }

    public void setMultiPageScrollEnable(boolean enable) {
        mMultiPageScrollEnable = enable;
    }

    public void setForceInterceptDispatchOnScrollStateChanged(boolean force) {
        this.mForceInterceptDispatchOnScrollStateChanged = force;
    }

    private class PagingOnFlingListener extends RecyclerView.OnFlingListener {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            return fling(velocityX, velocityY);
        }
    }
}
