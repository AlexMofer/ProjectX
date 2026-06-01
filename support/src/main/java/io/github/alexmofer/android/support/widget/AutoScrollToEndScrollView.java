/*
 * Copyright (C) 2026 AlexMofer
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

/**
 * 初始状态或者滚动到底部时子项高度变更后自动滚动到子项最底部
 * Created by Alex on 2026/5/13.
 */
public class AutoScrollToEndScrollView extends ScrollView {
    private boolean mAutoScrollToEnd = true;
    private boolean mIsUserTouching = false;

    public AutoScrollToEndScrollView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 如果正在触摸，不自动滚动
        if (mIsUserTouching) {
            return;
        }

        // 获取子视图高度和容器高度
        final View child = getChildAt(0);
        if (child != null) {
            final int childHeight = child.getHeight();
            final int containerHeight = getHeight();

            if (childHeight > containerHeight) {
                // 子项高度大于容器高度
                if (mAutoScrollToEnd) {
                    // 滚动到底部
                    scrollTo(0, childHeight - containerHeight);
                }
            } else {
                // 子项高度小于等于容器高度，强制开启
                mAutoScrollToEnd = true;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mIsUserTouching = true;
                mAutoScrollToEnd = false;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsUserTouching = false;
                // 检查是否贴底
                checkIfStuckToBottomAndResetFlag();
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 检查当前是否贴底，如果是，则将 mAutoScrollToEnd 设回 true
     */
    private void checkIfStuckToBottomAndResetFlag() {
        final View child = getChildAt(0);
        if (child != null) {
            final int scrollY = getScrollY();
            final int childHeight = child.getHeight();
            final int containerHeight = getHeight();
            
            // 允许一定的误差范围
            if (childHeight <= containerHeight) {
                // 内容不足一屏，自动恢复
                mAutoScrollToEnd = true;
            } else {
                // 内容超过一屏，检查是否滚动到了底部
                if (scrollY >= childHeight - containerHeight - 1) { // -1 for potential rounding errors
                    mAutoScrollToEnd = true;
                }
            }
        }
    }
}
