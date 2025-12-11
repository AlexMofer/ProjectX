/*
 * Copyright (C) 2025 AlexMofer
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

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * 链式布局
 * Created by Alex on 2025/7/30.
 */
public class LinkedLayout extends ViewGroup {

    private int mHorizontalGap;
    private int mVerticalGap;
    private int mItemColumnMin = 1;
    private int mItemColumnMax = 0;
    private int mItemMinWidth = 200;
    private int mColumn;

    public LinkedLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public LinkedLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public LinkedLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public LinkedLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        // TODO
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int contentWidth = width - paddingLeft - paddingRight;
        int remain = contentWidth - mItemMinWidth - mHorizontalGap;
        mColumn = 1;
        while (remain > mItemMinWidth) {
            mColumn++;
            remain -= mItemMinWidth;
            remain -= mHorizontalGap;
        }
        if (mColumn < mItemColumnMin) {
            // 最小值限定
            mColumn = mItemColumnMin;
        }
        if (mItemColumnMax >= mItemColumnMin) {
            // 最大值有效
            if (mColumn > mItemColumnMax) {
                // 最大值限定
                mColumn = mItemColumnMax;
            }
        }
        final int itemWidth = Math.round(
                (float) (contentWidth - (mHorizontalGap * (mColumn - 1))) / mColumn - 0.5f);
        final int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);
        final int childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(1, MeasureSpec.UNSPECIFIED);
        int contentHeight = 0;
        int itemMaxHeight = 0;
        final int count = getChildCount();
        int index = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            itemMaxHeight = Math.max(itemMaxHeight, child.getMeasuredHeight());
            if (index % mColumn == 0) {
                contentHeight += itemMaxHeight;
                contentHeight += mVerticalGap;
                itemMaxHeight = 0;
            }
            index++;
        }
        if (contentHeight > 0) {
            contentHeight -= mVerticalGap;
        }
        setMeasuredDimension(width, getPaddingTop() + contentHeight + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        if (count <= 0) {
            return;
        }
        final int width = getWidth();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int itemWidth = Math.round((float) (width - paddingLeft - paddingRight
                - (mHorizontalGap * (mColumn - 1))) / mColumn - 0.5f);
        if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
            final int start = width - paddingRight;
            int x = start;
            int y = paddingTop - mVerticalGap;
            int itemMaxHeight = 0;
            int index = 0;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                if (index % mColumn == 0) {
                    x = start;
                    y += itemMaxHeight;
                    y += mVerticalGap;
                }
                final int childHeight = child.getMeasuredHeight();
                itemMaxHeight = Math.max(itemMaxHeight, childHeight);
                child.layout(x - child.getMeasuredWidth(), y, x, y + childHeight);
                x -= mHorizontalGap;
                x -= itemWidth;
                index++;
            }
        } else {
            int x = paddingLeft;
            int y = paddingTop - mVerticalGap;
            int itemMaxHeight = 0;
            int index = 0;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE) {
                    continue;
                }
                if (index % mColumn == 0) {
                    x = paddingLeft;
                    y += itemMaxHeight;
                    y += mVerticalGap;
                }
                final int childHeight = child.getMeasuredHeight();
                itemMaxHeight = Math.max(itemMaxHeight, childHeight);
                child.layout(x, y, x + child.getMeasuredWidth(), y + childHeight);
                x += mHorizontalGap;
                x += itemWidth;
                index++;
            }
        }
    }

    /**
     * 设置间隔
     *
     * @param horizontal 水平间隔
     * @param vertical   垂直间隔
     */
    public void setGap(int horizontal, int vertical) {
        if (mHorizontalGap == horizontal && mVerticalGap == vertical) {
            return;
        }
        mHorizontalGap = horizontal;
        mVerticalGap = vertical;
        requestLayout();
    }

    /**
     * 设置间隔
     *
     * @param gap 间隔
     */
    public void setGap(int gap) {
        setGap(gap, gap);
    }

    /**
     * 设置列数
     *
     * @param min，最小列数，大于 0
     * @param max，最大列数，小于 min 时表示不限制最大列数
     */
    public void setItemColumn(int min, int max) {
        if (min < 1) {
            min = 1;
        }
        if (max < min) {
            max = min - 1;
        }
        if (mItemColumnMin == min && mItemColumnMax == max) {
            return;
        }
        mItemColumnMin = min;
        mItemColumnMax = max;
        requestLayout();
    }

    /**
     * 设置子项最小宽度
     *
     * @param width 最小宽度
     */
    public void setItemMinWidth(int width) {
        if (width <= 0) {
            return;
        }
        if (mItemMinWidth == width) {
            return;
        }
        mItemMinWidth = width;
        requestLayout();
    }
}
