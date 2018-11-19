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
package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.R;

/**
 * 更多面板列表
 * Created by Alex on 2018/10/19.
 */
final class OverflowListView extends MenuListView {

    private final int mMaxHeight;
    private int mPaddingSpace;
    private boolean mReverse;

    OverflowListView(Context context) {
        super(context);
        final Resources resources = context.getResources();
        final TypedValue outValue = new TypedValue();
        resources.getValue(R.dimen.floatingActionModeOverflowItemMaxShowCount, outValue, true);
        float count = outValue.getFloat();
        int overflowMinimumWidth = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeOverflowItemMinimumWidth);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        count = custom.getFloat(
                R.styleable.FloatingActionMode_floatingActionModeOverflowItemMaxShowCount, count);
        overflowMinimumWidth = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeOverflowItemMinimumWidth,
                overflowMinimumWidth);
        custom.recycle();
        setItemMinimumWidth(overflowMinimumWidth);
        mMaxHeight = (int) (mCalculator.getSize() * count);
    }

    void setPaddingSpace(int space) {
        mPaddingSpace = space;
        if (mReverse)
            setPadding(0, 0, 0, mPaddingSpace);
        else
            setPadding(0, mPaddingSpace, 0, 0);
    }

    int setData(FloatingMenuImpl menu) {
        mAdapter.clear();
        int itemMaxWidth = Integer.MIN_VALUE;
        while (menu.hasMoreMenu()) {
            final FloatingMenuItem item = menu.pullItemOut();
            if (item == null)
                continue;
            mCalculator.setData(item);
            mCalculator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            itemMaxWidth = itemMaxWidth < mCalculator.getMeasuredWidth() ?
                    mCalculator.getMeasuredWidth() : itemMaxWidth;
            mAdapter.add(item);
        }
        mAdapter.notifyDataSetChanged();
        return itemMaxWidth;
    }

    int getMaxHeight() {
        return mMaxHeight + mPaddingSpace;
    }

    boolean isReverse() {
        return mReverse;
    }

    void setReverse(boolean reverse) {
        if (mReverse == reverse)
            return;
        mReverse = reverse;
        if (mReverse)
            setPadding(0, 0, 0, mPaddingSpace);
        else
            setPadding(0, mPaddingSpace, 0, 0);
    }

    void clear() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }
}
