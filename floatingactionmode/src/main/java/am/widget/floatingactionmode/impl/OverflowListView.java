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

import android.content.Context;

import am.widget.floatingactionmode.FloatingMenuItem;

/**
 * 更多面板列表
 * Created by Alex on 2018/10/19.
 */
final class OverflowListView extends MenuListView {

    OverflowListView(Context context) {
        super(context);
    }

    void setData(FloatingMenuImpl menu, Size size) {
        mAdapter.clear();
        int width = Integer.MIN_VALUE;
        int height = 0;
        while (menu.hasMoreMenu()) {
            final FloatingMenuItem item = menu.pullItemOut();
            if (item == null)
                continue;
            mCalculator.setData(item);
            mCalculator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            width = width < mCalculator.getMeasuredWidth() ? mCalculator.getMeasuredWidth() : width;
            height += mCalculator.getMeasuredHeight();
            mAdapter.add(item);
        }
        mAdapter.notifyDataSetChanged();
        size.width = width;
        size.height = height;
    }
}
