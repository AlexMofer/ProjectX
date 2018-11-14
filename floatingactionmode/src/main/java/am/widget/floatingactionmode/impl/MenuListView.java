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
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.R;

/**
 * 菜单列表
 * Created by Alex on 2018/10/19.
 */
class MenuListView extends ListView {

    final Adapter mAdapter = new Adapter();
    final MenuItemView mCalculator;

    MenuListView(Context context) {
        super(context);
        setDivider(null);
        setDividerHeight(0);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        final Drawable background = custom.getDrawable(
                R.styleable.FloatingActionMode_floatingActionModeItemBackground);
        if (background == null) {
            final TypedArray a = context.obtainStyledAttributes(
                    new int[]{android.R.attr.selectableItemBackground});
            setSelector(a.getResourceId(0, 0));
            a.recycle();
        } else {
            setSelector(background);
        }
        custom.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            setScrollBarDefaultDelayBeforeFade(ViewConfiguration.getScrollDefaultDelay() * 3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            setScrollIndicators(View.SCROLL_INDICATOR_TOP | View.SCROLL_INDICATOR_BOTTOM);
        setAdapter(mAdapter);
        mCalculator = new MenuItemView(context);
        mCalculator.setFirst(true);
        mCalculator.setLast(true);
    }

    void awakenScrollBar() {
        awakenScrollBars(ViewConfiguration.getScrollDefaultDelay() * 3, true);
    }

    class Adapter extends BaseAdapter {

        private final ArrayList<FloatingMenuItem> mItems = new ArrayList<>();

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public FloatingMenuItem getItem(int position) {
            return position < 0 | position >= mItems.size() ? null : mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new MenuItemView(parent.getContext());
            }
            final MenuItemView button = (MenuItemView) convertView;
            button.setFirst(true);
            button.setLast(true);
            final FloatingMenuItem item = getItem(position);
            button.setData(item);
            convertView.setTag(item);
            return convertView;
        }

        void clear() {
            mItems.clear();
        }

        void add(FloatingMenuItem item) {
            mItems.add(item);
        }
    }
}
