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
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import am.widget.floatingactionmode.FloatingMenuItem;


/**
 * 主面板
 * Created by Alex on 2018/10/23.
 */
final class MainLayout extends LinearLayout implements View.OnClickListener {

    MainLayout(Context context) {
        super(context);
        setWillNotDraw(false);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    // Listener
    @Override
    public void onClick(View v) {

    }

    void setData(FloatingMenuImpl menu, int maxWidth, int overflowButtonWidth) {
        int index = 0;
        int width = 0;
        while (menu.hasMoreMenu()) {
            final FloatingMenuItem item = menu.pullItemOut();
            if (item == null)
                continue;
            final boolean more = menu.hasMoreMenu();
            final MenuItemView button = getChildAt(index, item);
            button.setFirst(index == 0);
            button.setLast(!more);
            button.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            final int itemWidth = button.getMeasuredWidth();
            if (more) {
                // 存在更多菜单
                if (width + itemWidth > maxWidth) {
                    // 宽度超过最大宽度
                    menu.pushItemBack(item);
                    removeChild(index);
                    break;
                }
                if (width + itemWidth > maxWidth - overflowButtonWidth) {
                    // 宽度放不下
                    menu.pushItemBack(item);
                    removeChild(index);
                    break;
                }
                width += itemWidth;
                index++;
            } else {
                // 最后一个菜单
                if (width + itemWidth > maxWidth) {
                    // 宽度放不下
                    menu.pushItemBack(item);
                    removeChild(index);
                    break;
                }
            }
        }
        requestLayout();
        invalidate();
    }

    private MenuItemView getChildAt(int index, FloatingMenuItem item) {
        View child = getChildAt(index);
        if (child == null) {
            child = new MenuItemView(getContext());
            child.setOnClickListener(this);
            addView(child, index);
        }
        final MenuItemView view = (MenuItemView) child;
        view.setData(item);
        view.setTag(item);
        return (MenuItemView) child;
    }

    private void removeChild(int index) {
        int count = getChildCount();
        while (count > index) {
            final View child = getChildAt(index);
            child.setTag(null);
            child.setOnClickListener(null);
            removeViewInLayout(child);
            count = getChildCount();
        }
    }
}
