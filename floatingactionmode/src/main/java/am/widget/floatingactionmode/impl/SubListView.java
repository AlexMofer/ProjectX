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
import am.widget.floatingactionmode.FloatingSubMenu;
import am.widget.floatingactionmode.R;

/**
 * 次级菜单列表
 * Created by Alex on 2018/10/19.
 */
final class SubListView extends MenuListView {

    private final int mMaxHeight;

    SubListView(Context context) {
        super(context);
        final Resources resources = context.getResources();
        final TypedValue outValue = new TypedValue();
        resources.getValue(R.dimen.floatingActionModeSubItemMaxShowCount, outValue, true);
        float count = outValue.getFloat();
        int subMinimumWidth = resources.getDimensionPixelOffset(
                R.dimen.floatingActionModeSubItemMinimumWidth);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        count = custom.getFloat(
                R.styleable.FloatingActionMode_floatingActionModeSubItemMaxShowCount, count);
        subMinimumWidth = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeSubItemMinimumWidth,
                subMinimumWidth);
        custom.recycle();
        setItemMinimumWidth(subMinimumWidth);
        mMaxHeight = (int) (mCalculator.getSize() * count);
    }

    int setData(FloatingSubMenu menu) {
        mAdapter.clear();
        int itemMaxWidth = Integer.MIN_VALUE;
        final int count = menu.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = menu.getItem(i);
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

    void clear() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();
    }

    int getMaxHeight() {
        return mMaxHeight;
    }
}
