/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.gradienttabstrip;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import am.widget.tabstrip.TabStripViewGroup;

@SuppressWarnings("unused")
abstract class BaseTabStripViewGroup<V extends View> extends TabStripViewGroup {

    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_MIDDLE = 1 << 1;
    public static final int SHOW_DIVIDER_END = 1 << 2;
    private final ArrayList<V> mRecycledChild = new ArrayList<>();
    private final OnClickListener mListener = new OnClickListener();
    private int mCount = 0;
    private int mChildWidth;
    private int mChildHeight;
    private Drawable mDivider;// 子项间隔
    private int mShowDividers = SHOW_DIVIDER_NONE;// 显示方式
    private int mDividerPadding;
    private Drawable mCenter;// 中间间隔（偶数个子项时有效）
    private boolean mCenterAsItem = false;
    private int mCenterPadding;
    private boolean mSmoothScroll = false;

    public BaseTabStripViewGroup(Context context) {
        super(context);
    }

    public BaseTabStripViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseTabStripViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initView(Drawable divider, int showDividers, int dividerPadding,
                            Drawable center, boolean centerAsItem, int centerPadding) {
        mDivider = divider;
        mShowDividers = showDividers;
        mDividerPadding = dividerPadding;
        mCenter = center;
        mCenterAsItem = centerAsItem;
        mCenterPadding = centerPadding;
        setWillNotDraw(mCenter == null && !isShowingDividers());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        if (getChildCount() <= 0) {
            setMeasuredDimension(widthSize,
                    resolveSize(Math.max(paddingTop + paddingBottom, suggestedMinimumHeight),
                            heightMeasureSpec));
            return;
        }
        final int count = getChildCount();
        int contentWidth;
        if (count % 2 == 0 && mCenter != null) {
            // 中间预留空间
            contentWidth = widthSize - paddingStart - paddingEnd - mCenter.getIntrinsicWidth();
            if (isShowingDividers()) {
                final int divider = mDivider.getIntrinsicWidth();
                if ((mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE) {
                    contentWidth -= divider * (count - 2);
                    if (mCenterAsItem)
                        contentWidth = contentWidth - divider - divider;
                }
            }
        } else {
            // 无需预留
            contentWidth = widthSize - paddingStart - paddingEnd;
            if (isShowingDividers()) {
                final int divider = mDivider.getIntrinsicWidth();
                if ((mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END)
                    contentWidth -= divider;
                if ((mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE)
                    contentWidth -= divider * (count - 1);
            }
        }
        final int childWidth = Math.max(0, contentWidth) / count;
        final int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        final int size = Math.max(0,
                MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom);
        int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.getMode(heightMeasureSpec));
        int childHeight = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if (childHeight == 0) {
                childHeight = child.getMeasuredHeight();
                childHeightMeasureSpec =
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            }
        }
        setMeasuredDimension(widthSize,
                resolveSize(Math.max(childHeight + paddingTop + paddingBottom,
                        suggestedMinimumHeight), heightMeasureSpec));
        mChildWidth = childWidth;
        mChildHeight = childHeight;
    }

    private boolean isShowingDividers() {
        return (mShowDividers != SHOW_DIVIDER_NONE) && (mDivider != null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int childWidth = mChildWidth;
        final int childHeight = mChildHeight;
        final boolean show = isShowingDividers();
        final int divider = show ? mDivider.getIntrinsicWidth() : 0;
        final int count = getChildCount();
        int start = paddingStart;
        if (show && (mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING) {
            start += divider;
        }
        final boolean middle = (mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (i == count - 1) {
                final int right;
                if (show && (mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END)
                    right = getWidth() - ViewCompat.getPaddingEnd(this) - divider;
                else
                    right = getWidth() - ViewCompat.getPaddingEnd(this);
                child.layout(start, paddingTop, right, paddingTop + childHeight);
                break;
            } else {
                child.layout(start, paddingTop, start + childWidth, paddingTop + childHeight);
            }
            start += childWidth;
            if (count % 2 == 0 && i == (count / 2) - 1 && mCenter != null) {
                start += mCenter.getIntrinsicWidth();
                if (show && middle && mCenterAsItem)
                    start = start + divider + divider;
            } else {
                if (show && middle)
                    start += divider;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int count = getChildCount();
        if (mCenter == null && !isShowingDividers() && count <= 0)
            return;
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int childWidth = mChildWidth;
        final boolean show = isShowingDividers();
        final Drawable dd = mDivider;
        final int divider = show ? dd.getIntrinsicWidth() : 0;
        final int padding = mDividerPadding;
        int start = paddingStart;
        if (show)
            dd.setBounds(1, paddingTop + padding, divider + 1,
                    getHeight() - paddingBottom - padding);
        if (count == 1) {
            if (show && (mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING) {
                canvas.save();
                canvas.translate(start, 0);
                dd.draw(canvas);
                canvas.restore();
                start += divider;
            }
            start += childWidth;
            if (show && (mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END) {
                canvas.save();
                canvas.translate(start, 0);
                dd.draw(canvas);
                canvas.restore();
            }
        } else {
            final boolean middle = (mShowDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE;
            for (int i = 0; i < count; i++) {
                if (i == 0) {
                    if (show && (mShowDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING) {
                        canvas.save();
                        canvas.translate(start, 0);
                        dd.draw(canvas);
                        canvas.restore();
                        start += divider;
                    }
                    start += childWidth;
                } else if (i == count - 1) {
                    if (show && middle) {
                        canvas.save();
                        canvas.translate(start, 0);
                        dd.draw(canvas);
                        canvas.restore();
                        start += divider;
                    }
                    start += childWidth;
                    if (show && (mShowDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END) {
                        start = getWidth() - ViewCompat.getPaddingEnd(this) - divider;
                        canvas.save();
                        canvas.translate(start, 0);
                        dd.draw(canvas);
                        canvas.restore();
                        break;
                    }
                } else {
                    if (count % 2 == 0 && count / 2 == i && mCenter != null) {
                        if (show && middle && mCenterAsItem) {
                            canvas.save();
                            canvas.translate(start, 0);
                            dd.draw(canvas);
                            canvas.restore();
                            start += divider;
                        }
                        final Drawable center = mCenter;
                        final int p = mCenterPadding;
                        center.setBounds(0, paddingTop + p, center.getIntrinsicWidth(),
                                getHeight() - paddingBottom - p);
                        canvas.save();
                        canvas.translate(start, 0);
                        center.draw(canvas);
                        canvas.restore();
                        start += center.getIntrinsicWidth();
                        if (show && middle && mCenterAsItem) {
                            canvas.save();
                            canvas.translate(start, 0);
                            dd.draw(canvas);
                            canvas.restore();
                            start += divider;
                        }
                    } else {
                        if (show && middle) {
                            canvas.save();
                            canvas.translate(start, 0);
                            dd.draw(canvas);
                            canvas.restore();
                            start += divider;
                        }
                    }
                    start += childWidth;
                }
            }
        }
    }

    @Override
    protected void onViewPagerAdapterChanged(@Nullable PagerAdapter oldAdapter,
                                             @Nullable PagerAdapter newAdapter) {
        super.onViewPagerAdapterChanged(oldAdapter, newAdapter);
        updateItemCount();
    }

    @Override
    protected void onViewPagerAdapterDataChanged() {
        super.onViewPagerAdapterDataChanged();
        updateItemCount();
    }

    @SuppressWarnings("unchecked")
    private void updateItemCount() {
        final int count = getPageCount();
        if (count != mCount) {
            mCount = count;
            if (mCount >= 0) {
                final int ic = getChildCount();
                for (int i = 0; i < ic && i < mCount; i++) {
                    final V child = (V) getChildAt(i);
                    onBindView(child, i);
                }
                boolean layout = false;
                while (getChildCount() < mCount) {
                    final V child;
                    if (mRecycledChild.isEmpty()) {
                        child = onCreateView();
                    } else {
                        child = mRecycledChild.get(mRecycledChild.size() - 1);
                    }
                    final int position = getChildCount();
                    onBindView(child, position);
                    child.setId(position);
                    child.setOnClickListener(mListener);
                    addViewInLayout(child, -1,
                            generateDefaultLayoutParams(), true);
                    layout = true;
                }
                while (getChildCount() > mCount) {
                    final V child = (V) getChildAt(getChildCount() - 1);
                    removeViewInLayout(child);
                    mRecycledChild.add(child);
                    child.setId(NO_ID);
                    child.setOnClickListener(null);
                    onViewRecycled(child);
                    layout = true;
                }
                if (layout) {
                    requestLayout();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void notifyItemRangeChanged(int positionStart, int itemCount) {
        final int count = getChildCount();
        final int last = positionStart + itemCount;
        for (int i = positionStart; i < count && i < last; i++) {
            final V child = (V) getChildAt(i);
            onBindView(child, i);
        }
    }

    protected void notifyItemChanged(int position) {
        notifyItemRangeChanged(position, 1);
    }

    protected abstract V onCreateView();

    protected abstract void onBindView(V item, int position);

    protected void onViewRecycled(V item) {
    }

    protected void onViewClicked(V item, int position) {
        performClick(position, mSmoothScroll);
    }

    @DividerMode
    public int getShowDividers() {
        return mShowDividers;
    }

    public void setShowDividers(@DividerMode int showDividers) {
        if (showDividers == mShowDividers) {
            return;
        }
        mShowDividers = showDividers;

        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    @Override
    public void addView(View child) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, int width, int height) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, LayoutParams params) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
    }

    public Drawable getDividerDrawable() {
        return mDivider;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider == mDivider) {
            return;
        }
        mDivider = divider;
        setWillNotDraw(mCenter == null && !isShowingDividers());
        requestLayout();
    }

    public int getDividerPadding() {
        return mDividerPadding;
    }

    public void setDividerPadding(int padding) {
        if (padding == mDividerPadding) {
            return;
        }
        mDividerPadding = padding;
        if (isShowingDividers()) {
            invalidate();
        }
    }

    public void setCenterDrawable(Drawable center) {
        if (center == mCenter) {
            return;
        }
        mCenter = center;
        setWillNotDraw(mCenter == null && !isShowingDividers());
        requestLayout();
    }

    public boolean isCenterAsItem() {
        return mCenterAsItem;
    }

    public void setCenterAsItem(boolean centerAsItem) {
        if (mCenterAsItem == centerAsItem)
            return;
        mCenterAsItem = centerAsItem;
        requestLayout();
    }

    public int getCenterPadding() {
        return mCenterPadding;
    }

    public void setCenterPadding(int padding) {
        if (padding == mCenterPadding) {
            return;
        }
        mCenterPadding = padding;
        if (mCenter != null) {
            invalidate();
        }
    }

    public boolean isSmoothScroll() {
        return mSmoothScroll;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        mSmoothScroll = smoothScroll;
    }

    @SuppressWarnings("all")
    @IntDef(flag = true,
            value = {
                    SHOW_DIVIDER_NONE,
                    SHOW_DIVIDER_BEGINNING,
                    SHOW_DIVIDER_MIDDLE,
                    SHOW_DIVIDER_END
            })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    private class OnClickListener implements View.OnClickListener {

        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            onViewClicked((V) v, v.getId());
        }
    }
}
