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
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 双向滚动布局管理器
 * Created by Alex on 2017/11/3.
 */
@SuppressWarnings("all")
public class BothDirectionsScrollLayoutManager extends CenterLinearLayoutManager {

    public static final float INVALID_PERCENTAGE = -1;
    private static final String KEY_OFFSET = "am.widget.multifunctionalrecyclerview.BothDirectionsScrollLayoutManager.KEY_OFFSET";
    private int mChildMaxWidth;
    private int mChildMaxHeight;
    private int mLeftDecorationMaxWidthOfChildMaxWidth;
    private int mRightDecorationMaxWidthOfChildMaxWidth;
    private int mTopDecorationMaxWidthOfChildMaxHeight;
    private int mBottomDecorationMaxWidthOfChildMaxHeight;
    private int mOffset = 0;
    private float mPercentage = INVALID_PERCENTAGE;
    private float mPendingPercentage = INVALID_PERCENTAGE;
    private int mWidthSize;
    private int mHeightSize;

    public BothDirectionsScrollLayoutManager(Context context) {
        super(context);
    }

    public BothDirectionsScrollLayoutManager(Context context, int orientation,
                                             boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public BothDirectionsScrollLayoutManager(Context context, AttributeSet attrs,
                                             int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                          int widthSpec, int heightSpec) {
        mWidthSize = View.MeasureSpec.getSize(widthSpec);
        mHeightSize = View.MeasureSpec.getSize(heightSpec);
        final int maxOffset = computeAnotherDirectionMaxScrollOffset();
        if (maxOffset > 0) {
            if (mPercentage == INVALID_PERCENTAGE) {
                mPercentage = getDefaultScrollOffsetPercentage();
            }
            if (mPendingPercentage != INVALID_PERCENTAGE) {
                mPercentage = mPendingPercentage;
                mPendingPercentage = INVALID_PERCENTAGE;
            }
            mOffset = Math.round(mPercentage * maxOffset);
        } else {
            mPercentage = INVALID_PERCENTAGE;
            mOffset = 0;
        }
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    /**
     * Return the measured width of the parent RecyclerView
     *
     * @return Measured width in pixels
     */
    public int getMeasuredWidth() {
        return mWidthSize;
    }

    /**
     * Return the measured height of the parent RecyclerView
     *
     * @return Measured height in pixels
     */
    public int getMeasuredHeight() {
        return mHeightSize;
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation != getOrientation()) {
            mPercentage = INVALID_PERCENTAGE;
        }
        super.setOrientation(orientation);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putFloat(KEY_OFFSET, mPercentage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mPercentage = bundle.getFloat(KEY_OFFSET);
    }

    /**
     * 计算另一方向上的默认滚动偏移百分比
     *
     * @return 默认滚动偏移百分比
     */
    protected float getDefaultScrollOffsetPercentage() {
        if (isLayoutInCenter())
            return 0.5f;
        return 0;
    }

    protected int computeAnotherDirectionDefaultScrollOffset() {
        return Math.round(getDefaultScrollOffsetPercentage() *
                computeAnotherDirectionMaxScrollOffset());
    }

    protected int computeAnotherDirectionMaxScrollOffset() {
        final int offset;
        if (getOrientation() == HORIZONTAL) {
            final int range = computeVerticalScrollRange();
            final int extent = computeVerticalScrollExtent();
            offset = range - extent;
        } else {
            final int range = computeHorizontalScrollRange();
            final int extent = computeHorizontalScrollExtent();
            offset = range - extent;
        }
        return offset < 0 ? 0 : offset;
    }

    @Override
    public void layoutDecorated(View child, int left, int top, int right, int bottom) {
        super.layoutDecorated(child, left, top, right, bottom);
        final int offset = computeAnotherDirectionDefaultScrollOffset();
        final int move = offset - mOffset;
        if (move == 0)
            return;
        if (getOrientation() == HORIZONTAL) {
            child.offsetTopAndBottom(move);
        } else {
            child.offsetLeftAndRight(move);
        }
    }

    @Override
    public void layoutDecoratedWithMargins(View child, int left, int top, int right, int bottom) {
        super.layoutDecoratedWithMargins(child, left, top, right, bottom);
        final int offset = computeAnotherDirectionDefaultScrollOffset();
        final int move = offset - mOffset;
        if (move == 0)
            return;
        if (getOrientation() == HORIZONTAL) {
            child.offsetTopAndBottom(move);
        } else {
            child.offsetLeftAndRight(move);
        }
    }

    /**
     * 计算另一方向上的滚动偏移
     *
     * @return 滚动偏移
     */
    public int computeAnotherDirectionScrollOffset() {
        return mOffset;
    }

    @Override
    public int computeHorizontalScrollOffset(RecyclerView.State state) {
        if (getOrientation() == HORIZONTAL) {
            return super.computeHorizontalScrollOffset(state);
        }
        return computeHorizontalScrollOffset();
    }

    public int computeHorizontalScrollOffset() {
        if (getOrientation() == HORIZONTAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeHorizontalScrollOffset();
        }
        return computeAnotherDirectionScrollOffset();
    }

    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        if (getOrientation() == VERTICAL) {
            return super.computeVerticalScrollOffset(state);
        }
        return computeVerticalScrollOffset();
    }

    public int computeVerticalScrollOffset() {
        if (getOrientation() == VERTICAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeVerticalScrollOffset();
        }
        return computeAnotherDirectionScrollOffset();
    }

    @Override
    public int computeHorizontalScrollExtent(RecyclerView.State state) {
        if (getOrientation() == HORIZONTAL) {
            return super.computeHorizontalScrollExtent(state);
        }
        return computeHorizontalScrollExtent();
    }

    protected int computeHorizontalScrollExtent() {
        if (getOrientation() == HORIZONTAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeHorizontalScrollExtent();
        }
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        if (getOrientation() == VERTICAL) {
            return super.computeVerticalScrollExtent(state);
        }
        return computeVerticalScrollExtent();
    }

    protected int computeVerticalScrollExtent() {
        if (getOrientation() == VERTICAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeVerticalScrollExtent();
        }
        return getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public int computeHorizontalScrollRange(RecyclerView.State state) {
        if (getOrientation() == HORIZONTAL) {
            return super.computeHorizontalScrollRange(state);
        }
        return computeHorizontalScrollRange();
    }

    protected int computeHorizontalScrollRange() {
        if (getOrientation() == HORIZONTAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeHorizontalScrollRange();
        }
        return getChildMaxWidth(mChildMaxWidth) + mLeftDecorationMaxWidthOfChildMaxWidth +
                mRightDecorationMaxWidthOfChildMaxWidth;
    }

    protected int getChildMaxWidth(int width) {
        return width;
    }

    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        if (getOrientation() == VERTICAL) {
            return super.computeVerticalScrollRange(state);
        }
        return computeVerticalScrollRange();
    }

    protected int computeVerticalScrollRange() {
        if (getOrientation() == VERTICAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeVerticalScrollRange();
        }
        return getChildMaxHeight(mChildMaxHeight) + mTopDecorationMaxWidthOfChildMaxHeight +
                mBottomDecorationMaxWidthOfChildMaxHeight;
    }

    protected int getChildMaxHeight(int height) {
        return height;
    }

    public boolean canScrollAnotherDirection() {
        if (getOrientation() == HORIZONTAL) {
            return canScrollVertically();
        } else {
            return canScrollHorizontally();
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return super.canScrollHorizontally() ||
                computeHorizontalScrollRange() > computeHorizontalScrollExtent();
    }

    @Override
    public boolean canScrollVertically() {
        return super.canScrollVertically() ||
                computeVerticalScrollRange() > computeVerticalScrollExtent();
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
                                    RecyclerView.State state) {
        if (getOrientation() == HORIZONTAL) {
            return super.scrollHorizontallyBy(dx, recycler, state);
        }
        return scrollBy(dx);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler,
                                  RecyclerView.State state) {
        if (getOrientation() == VERTICAL) {
            return super.scrollVerticallyBy(dy, recycler, state);
        }
        return scrollBy(dy);
    }

    private int scrollBy(int distance) {
        if (distance == 0)
            return 0;
        final int max = computeAnotherDirectionMaxScrollOffset();
        int offset = mOffset + distance;
        if (offset < 0) {
            offset = 0;
        } else if (offset > max) {
            offset = max;
        }
        final int move = mOffset - offset;
        if (move == 0)
            return 0;
        if (getOrientation() == HORIZONTAL) {
            offsetChildrenVertical(move);
        } else {
            offsetChildrenHorizontal(move);
        }
        mOffset = offset;
        mPercentage = ((float) mOffset) / max;
        return -move;
    }

    public void scrollAnotherDirectionBy(int distance) {
        scrollBy(distance);
    }

    public float getAnotherDirectionScrollOffsetPercentage() {
        return mPercentage == INVALID_PERCENTAGE ? 0 : mPercentage;
    }

    public void setAnotherDirectionScrollOffsetPercentage(float percentage) {
        setAnotherDirectionScrollOffsetPercentage(percentage, true);
    }

    public void setAnotherDirectionScrollOffsetPercentage(float percentage, boolean updateLayout) {
        if (mPendingPercentage == percentage || percentage < 0 || percentage > 1)
            return;
        mPendingPercentage = percentage;
        if (updateLayout)
            requestLayout();
    }

    public void setChildMaxSize(int width, int height) {
        mChildMaxWidth = width;
        mChildMaxHeight = height;
    }

    public int getChildMaxWidth() {
        return mChildMaxWidth;
    }

    public int getChildMaxHeight() {
        return mChildMaxHeight;
    }

    public int getLeftDecorationMaxWidthOfChildMaxWidth() {
        return mLeftDecorationMaxWidthOfChildMaxWidth;
    }

    public int getRightDecorationMaxWidthOfChildMaxWidth() {
        return mRightDecorationMaxWidthOfChildMaxWidth;
    }

    public int getTopDecorationMaxWidthOfChildMaxHeight() {
        return mTopDecorationMaxWidthOfChildMaxHeight;
    }

    public int getBottomDecorationMaxWidthOfChildMaxHeight() {
        return mBottomDecorationMaxWidthOfChildMaxHeight;
    }

    public void setDecorationMaxWidthOfChildWithMaxSize(int left, int right, int top, int bottom) {
        mLeftDecorationMaxWidthOfChildMaxWidth = left;
        mRightDecorationMaxWidthOfChildMaxWidth = right;
        mTopDecorationMaxWidthOfChildMaxHeight = top;
        mBottomDecorationMaxWidthOfChildMaxHeight = bottom;
    }
}
