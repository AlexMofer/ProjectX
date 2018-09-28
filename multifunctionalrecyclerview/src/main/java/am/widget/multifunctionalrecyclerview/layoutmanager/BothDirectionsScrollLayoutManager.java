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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.AbsSavedState;
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
    public int getHeightMode() {
        return View.MeasureSpec.UNSPECIFIED;
    }

    @Override
    public int getWidthMode() {
        return View.MeasureSpec.UNSPECIFIED;
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
     * 获取RecyclerView的测量宽度
     *
     * @return 测量宽度
     */
    public int getMeasuredWidth() {
        return mWidthSize;
    }

    /**
     * 获取RecyclerView的测量高度
     *
     * @return 测量高度
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
    public SavedState onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), mPercentage);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState))
            super.onRestoreInstanceState(state);
        else {
            final SavedState saved = (SavedState) state;
            super.onRestoreInstanceState(saved.getSuperState());
            mPendingPercentage = saved.getPercentage();
        }


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

    /**
     * 计算另一方向上的默认滚动偏移
     *
     * @return 默认滚动偏移
     */
    protected int computeAnotherDirectionDefaultScrollOffset() {
        return Math.round(getDefaultScrollOffsetPercentage() *
                computeAnotherDirectionMaxScrollOffset());
    }

    /**
     * 计算另一方向上的最大滚动偏移
     *
     * @return 最大滚动偏移
     */
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

    /**
     * 计算水平滚动偏移
     *
     * @return 滚动偏移
     */
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

    /**
     * 计算垂直滚动偏移
     *
     * @return 滚动偏移
     */
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

    /**
     * 计算水平滚动边界
     *
     * @return 滚动边界
     */
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

    /**
     * 计算垂直滚动边界
     *
     * @return 滚动边界
     */
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

    /**
     * 计算水平滚动范围
     *
     * @return 滚动范围
     */
    protected int computeHorizontalScrollRange() {
        if (getOrientation() == HORIZONTAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeHorizontalScrollRange();
        }
        return getChildMaxWidth(mChildMaxWidth) + mLeftDecorationMaxWidthOfChildMaxWidth +
                mRightDecorationMaxWidthOfChildMaxWidth;
    }

    /**
     * 转换子项最宽宽度（为缩放布局做准备）
     *
     * @param width 宽度
     * @return 宽度
     */
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

    /**
     * 计算垂直滚动范围
     *
     * @return 滚动范围
     */
    protected int computeVerticalScrollRange() {
        if (getOrientation() == VERTICAL) {
            final RecyclerView view = getRecyclerView();
            return view == null ? 0 : view.computeVerticalScrollRange();
        }
        return getChildMaxHeight(mChildMaxHeight) + mTopDecorationMaxWidthOfChildMaxHeight +
                mBottomDecorationMaxWidthOfChildMaxHeight;
    }

    /**
     * 转换子项最高高度（为缩放布局做准备）
     *
     * @param height 高度
     * @return 高度
     */
    protected int getChildMaxHeight(int height) {
        return height;
    }

    /**
     * 判断另一方向是否能够滚动
     *
     * @return 是否能够
     */
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

    /**
     * 往另一方向滚动
     *
     * @param distance 距离
     */
    public void scrollAnotherDirectionBy(int distance) {
        scrollBy(distance);
    }

    /**
     * 获取另一方向上滚动偏移百分比
     *
     * @return 百分比
     */
    public float getAnotherDirectionScrollOffsetPercentage() {
        return mPercentage == INVALID_PERCENTAGE ? 0 : mPercentage;
    }

    /**
     * 设置另一方向上滚动偏移百分比
     *
     * @param percentage 百分比
     */
    public void setAnotherDirectionScrollOffsetPercentage(float percentage) {
        setAnotherDirectionScrollOffsetPercentage(percentage, true);
    }

    /**
     * 设置另一方向上滚动偏移百分比
     *
     * @param percentage   百分比
     * @param updateLayout 更新布局
     */
    public void setAnotherDirectionScrollOffsetPercentage(float percentage, boolean updateLayout) {
        if (mPendingPercentage == percentage || percentage < 0 || percentage > 1)
            return;
        mPendingPercentage = percentage;
        if (updateLayout)
            requestLayout();
    }

    /**
     * 滚动到固定位置
     *
     * @param position   位置
     * @param offset     常规滚动方向上的偏移
     * @param percentage 另一滚动方向上的偏移百分比
     */
    public void scrollToPositionWithOffsetAndPercentage(int position, int offset,
                                                        float percentage) {
        if (mPendingPercentage == percentage || percentage < 0 || percentage > 1) {
            scrollToPositionWithOffset(position, offset);
            return;
        }
        mPendingPercentage = percentage;
        scrollToPositionWithOffset(position, offset);
    }

    /**
     * 设置子项最大尺寸
     *
     * @param width  宽
     * @param height 高
     */
    public void setChildMaxSize(int width, int height) {
        mChildMaxWidth = width;
        mChildMaxHeight = height;
    }

    /**
     * 获取子项最大宽度
     *
     * @return 宽度
     */
    public int getChildMaxWidth() {
        return mChildMaxWidth;
    }

    /**
     * 获取子项最大高度
     *
     * @return 高度
     */
    public int getChildMaxHeight() {
        return mChildMaxHeight;
    }

    /**
     * 获取宽度最大子项拥有的最大左边装饰宽度
     *
     * @return 宽度
     */
    public int getLeftDecorationMaxWidthOfChildMaxWidth() {
        return mLeftDecorationMaxWidthOfChildMaxWidth;
    }

    /**
     * 获取宽度最大子项拥有的最大右边装饰宽度
     *
     * @return 宽度
     */
    public int getRightDecorationMaxWidthOfChildMaxWidth() {
        return mRightDecorationMaxWidthOfChildMaxWidth;
    }

    /**
     * 获取高度最大子项拥有的最大顶边装饰宽度
     *
     * @return 宽度
     */
    public int getTopDecorationMaxWidthOfChildMaxHeight() {
        return mTopDecorationMaxWidthOfChildMaxHeight;
    }

    /**
     * 获取高度最大子项拥有的最大底边装饰宽度
     *
     * @return 宽度
     */
    public int getBottomDecorationMaxWidthOfChildMaxHeight() {
        return mBottomDecorationMaxWidthOfChildMaxHeight;
    }

    /**
     * 设置具备最大宽度及最大高度的子项的各边装饰最大宽度
     *
     * @param left   左边
     * @param right  右边
     * @param top    顶边
     * @param bottom 底边
     */
    public void setDecorationMaxWidthOfChildWithMaxSize(int left, int right, int top, int bottom) {
        mLeftDecorationMaxWidthOfChildMaxWidth = left;
        mRightDecorationMaxWidthOfChildMaxWidth = right;
        mTopDecorationMaxWidthOfChildMaxHeight = top;
        mBottomDecorationMaxWidthOfChildMaxHeight = bottom;
    }

    protected static class SavedState extends AbsSavedState {

        public static final Creator<SavedState> CREATOR =
                new ClassLoaderCreator<SavedState>() {

                    @Override
                    public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                        return new SavedState(source, loader);
                    }

                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in, null);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        private final float mPercentage;

        private SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            mPercentage = in.readFloat();
        }

        private SavedState(Parcelable superState, float percentage) {
            super(superState);
            mPercentage = percentage;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(mPercentage);
        }

        float getPercentage() {
            return mPercentage;
        }
    }
}
