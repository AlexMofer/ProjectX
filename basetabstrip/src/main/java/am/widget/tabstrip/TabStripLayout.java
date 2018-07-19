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
package am.widget.tabstrip;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * TabStrip布局
 *
 * @param <V> 子View
 */
@SuppressWarnings("unused")
public abstract class TabStripLayout<V extends View> extends TabStripViewGroup {

    private final ArrayList<V> mRecycledChild = new ArrayList<>();
    private final OnClickListener mListener = new OnClickListener();
    private int mCount = 0;
    private boolean mSmoothScroll = false;
    private boolean mBlockAddView = true;

    public TabStripLayout(Context context) {
        super(context);
    }

    public TabStripLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabStripLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {
        if (mBlockAddView)
            throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        if (mBlockAddView)
            throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        if (mBlockAddView)
            throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, LayoutParams params) {
        if (mBlockAddView)
            throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        if (mBlockAddView)
            throw new UnsupportedOperationException("Not support add child in this ViewGroup.");
        super.addView(child, index, params);
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

    private void updateItemCount() {
        final int count = getPageCount();
        if (count != mCount) {
            mCount = count;
            if (mCount >= 0) {
                final int ic = getChildCount();
                for (int i = 0; i < ic && i < mCount; i++) {
                    final V child = getItemAt(i);
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
                    final V child = getItemAt(getChildCount() - 1);
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

    /**
     * 通知全部子项变化
     */
    protected void notifyItemChanged() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final V child = getItemAt(i);
            onBindView(child, i);
        }
    }

    /**
     * 通知子项变化
     *
     * @param position 子项坐标
     */
    protected void notifyItemChanged(int position) {
        final V child = getItemAt(position);
        if (child == null)
            return;
        onBindView(child, position);
    }

    /**
     * 创建子项
     *
     * @return 子项
     */
    protected abstract V onCreateView();

    /**
     * 绑定子项
     *
     * @param item     子项
     * @param position 坐标
     */
    protected abstract void onBindView(V item, int position);

    /**
     * 回收子项
     *
     * @param item 子项
     */
    protected void onViewRecycled(V item) {
    }

    /**
     * 点击子项
     *
     * @param item     子项
     * @param position 坐标
     */
    protected void onViewClicked(V item, int position) {
        performClick(position, mSmoothScroll);
    }

    /**
     * 判断是否屏蔽添加子项
     *
     * @return 是否屏蔽
     */
    protected boolean isBlockAddView() {
        return mBlockAddView;
    }

    /**
     * 设置是否屏蔽添加子项
     *
     * @param block 是否屏蔽
     */
    protected void setBlockAddView(boolean block) {
        mBlockAddView = block;
    }

    /**
     * 获取子项
     *
     * @param index 位置
     * @return 子项
     */
    @SuppressWarnings("unchecked")
    protected V getItemAt(int index) {
        final View child = getChildAt(index);
        return child == null ? null : (V) child;
    }

    /**
     * 判断子项点击是否平滑滚动
     *
     * @return 是否平滑滚动
     */
    @SuppressWarnings("all")
    protected boolean isItemClickSmoothScroll() {
        return mSmoothScroll;
    }

    /**
     * 设置子项点击是否平滑滚动
     *
     * @param smoothScroll 是否平滑滚动
     */
    protected void setItemClickSmoothScroll(boolean smoothScroll) {
        mSmoothScroll = smoothScroll;
    }

    private class OnClickListener implements View.OnClickListener {

        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            onViewClicked((V) v, v.getId());
        }
    }
}
