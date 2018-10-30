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
package am.widget.floatingactionmode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;

/**
 * 次级菜单列表
 * Created by Alex on 2018/10/19.
 */
@SuppressLint("ViewConstructor")
final class SubListView extends MenuListView {

    SubListView(Context context, int height, boolean light, boolean useTheme) {
        super(context, height, light, useTheme);
    }

    void setData(FloatingSubMenu menu, Point size, boolean calculateOnly) {
        if (!calculateOnly)
            mAdapter.clear();
        int width = 0;
        int height = 0;
        final int count = menu.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = menu.getItem(i);
            mCalculator.setData(item);
            mCalculator.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            width = width < mCalculator.getMeasuredWidth() ? mCalculator.getMeasuredWidth() : width;
            height += mCalculator.getMeasuredHeight();
            if (!calculateOnly)
                mAdapter.add(item);
        }
        if (!calculateOnly)
            mAdapter.notifyDataSetChanged();
        size.x = width;
        size.y = height;
    }
}
