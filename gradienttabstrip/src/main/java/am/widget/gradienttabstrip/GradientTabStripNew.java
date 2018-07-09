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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import am.widget.tabstrip.TabStripTagAdapter;
import am.widget.tabstrip.TabStripViewGroup;

/**
 * 渐变TabStrip，子项不宜过多
 *
 * @author Alex
 */
public class GradientTabStripNew extends TabStripViewGroup {

    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_MIDDLE = 1 << 1;
    public static final int SHOW_DIVIDER_END = 1 << 2;
    private final ArrayList<View> mRecycledChild = new ArrayList<>();
    private int mPosition = 0;
    private float mOffset = 0;
    private int mCount = 0;
    private int mChildWidth;
    private int mChildHeight;
    private Drawable mDivider;// 子项间隔
    private int mShowDividers = SHOW_DIVIDER_NONE;// 显示方式
    private Drawable mCenter;// 中间间隔（偶数个子项时有效）
    public GradientTabStripNew(Context context) {
        super(context);
        initView(context, null);
    }

    public GradientTabStripNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public GradientTabStripNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        mDivider = getDefaultDrawable(0xff00ff00, 10);
        mCenter = getDefaultDrawable(0xffff00ff, 46);
    }

    private Drawable getDefaultDrawable(int color, int size) {
        final GradientDrawable mBackground = new GradientDrawable();
        mBackground.setShape(GradientDrawable.RECTANGLE);
        mBackground.setColor(color);
        mBackground.setSize(size, 0);
        return mBackground;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            // TODO
        }
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
        final int childWidth;
        final int center = mCenter == null ? 0 : mCenter.getIntrinsicWidth();
        final int divider = isShowingDividers() ? 0 : mDivider.getIntrinsicWidth();
        if (count % 2 == 0) {
            // 中间预留空间
            // TODO
            childWidth = (widthSize - paddingStart - paddingEnd - center - divider * (count - 1))
                    / count;
        } else {
            childWidth = (widthSize - paddingStart - paddingEnd - divider * (count - 1)) / count;
        }
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
        final int center = mCenter == null ? 0 : mCenter.getIntrinsicWidth();
        final int divider = isShowingDividers() ? 0 : mDivider.getIntrinsicWidth();
        final int count = getChildCount();
        int start = paddingStart;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (i == count - 1) {
                child.layout(start, paddingTop, getWidth() - ViewCompat.getPaddingEnd(this),
                        paddingTop + childHeight);
                break;
            } else {
                child.layout(start, paddingTop, start + childWidth, paddingTop + childHeight);
            }
            // TODO
            if (count % 2 == 0 && i == (count / 2) - 1) {
                start += childWidth + divider + center;
            } else {
                start += childWidth + divider;
            }
        }
    }

    @Override
    protected void onViewPagerChanged(int position, float offset) {
        if (mPosition == position && offset == mOffset)
            return;
        mPosition = position;
        mOffset = offset;
        invalidateItem();
    }

    @Override
    protected void onViewPagerAdapterChanged(@Nullable PagerAdapter oldAdapter,
                                             @Nullable PagerAdapter newAdapter) {
        super.onViewPagerAdapterChanged(oldAdapter, newAdapter);
        updateItemCount();
        invalidateItem();
    }

    @Override
    protected void onViewPagerAdapterDataChanged() {
        super.onViewPagerAdapterDataChanged();
        updateItemCount();
        invalidateItem();
    }

    @Override
    protected void onObservableChangeNotified() {
        super.onObservableChangeNotified();
        invalidateItem();
    }

    private void updateItemCount() {
        final int count = getPageCount();
        if (count != mCount) {
            mCount = count;
            if (mCount >= 0) {
                boolean layout = false;
                while (getChildCount() > mCount) {
                    final View child = getChildAt(getChildCount() - 1);
                    removeViewInLayout(child);
                    mRecycledChild.add(child);
                    layout = true;
                }
                while (getChildCount() < mCount) {
                    final View child;
                    if (mRecycledChild.isEmpty()) {
                        child = new GradientTabStripItem(getContext());
                    } else {
                        child = mRecycledChild.get(mRecycledChild.size() - 1);
                    }
                    child.setBackgroundColor(0xffff00ff);
                    // TODO
                    addViewInLayout(child, -1,
                            generateDefaultLayoutParams(), true);
                    layout = true;
                }
                if (layout) {
                    requestLayout();
                }
            }
        }
    }

    private void invalidateItem() {
        // TODO
    }

    @DividerMode
    public int getShowDividers() {
        return mShowDividers;
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

    /**
     * 设置Adapter
     *
     * @param adapter Adapter
     */
    public void setAdapter(Adapter adapter) {
        setObservable(adapter);
    }

    public void setShowDividers(@DividerMode int showDividers) {
        if (showDividers == mShowDividers) {
            return;
        }
        mShowDividers = showDividers;

        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

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

//    public Drawable getDividerDrawable() {
//        return mDivider;
//    }
//
//    public void setDividerDrawable(Drawable divider) {
//        if (divider == mDivider) {
//            return;
//        }
//        mDivider = divider;
//        if (divider != null) {
//            mDividerWidth = divider.getIntrinsicWidth();
//            mDividerHeight = divider.getIntrinsicHeight();
//        } else {
//            mDividerWidth = 0;
//            mDividerHeight = 0;
//        }
//
//        setWillNotDraw(!isShowingDividers());
//        requestLayout();
//    }
//
//    public void setDividerPadding(int padding) {
//        if (padding == mDividerPadding) {
//            return;
//        }
//        mDividerPadding = padding;
//
//        if (isShowingDividers()) {
//            requestLayout();
//            invalidate();
//        }
//    }
//
//    public int getDividerPadding() {
//        return mDividerPadding;
//    }
//
//    public int getDividerWidth() {
//        return mDividerWidth;
//    }
    /**
     * Adapter
     */
    public static abstract class Adapter extends TabStripTagAdapter {

        /**
         * 获取普通状态下的 Drawable
         *
         * @param position 位置
         * @return Drawable
         */
        @Nullable
        public abstract Drawable getDrawableNormal(int position);

        /**
         * 获取选中状态下的 Drawable
         *
         * @param position 位置
         * @return Drawable
         */
        @Nullable
        public abstract Drawable getDrawableSelected(int position);
    }
}