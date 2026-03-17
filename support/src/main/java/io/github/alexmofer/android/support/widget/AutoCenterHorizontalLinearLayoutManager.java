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

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自动居中水平线性布局（子项不够滚动时自动居中）
 * Created by Alex on 2026/3/17.
 */
public final class AutoCenterHorizontalLinearLayoutManager extends LinearLayoutManager {
    private final Rect mChildrenBounds = new Rect();

    public AutoCenterHorizontalLinearLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
    }

    @Nullable
    private RecyclerView getRecyclerView() {
        final View child = getChildAt(0);
        if (child == null) {
            return null;
        }
        final ViewParent parent = child.getParent();
        return parent instanceof RecyclerView ? (RecyclerView) parent : null;
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
        final RecyclerView parent = getRecyclerView();
        if (parent == null ||
                parent.canScrollHorizontally(1) ||
                parent.canScrollHorizontally(-1)) {
            return;
        }
        boolean set = false;
        final Rect bounds = mChildrenBounds;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null) {
                continue;
            }
            if (set) {
                bounds.union(child.getLeft(), child.getTop(),
                        child.getRight(), child.getBottom());
            } else {
                set = true;
                bounds.set(child.getLeft(), child.getTop(),
                        child.getRight(), child.getBottom());
            }
        }
        if (!set) {
            return;
        }
        final float childrenCenterX = bounds.exactCenterX();
        final int paddingLeft = parent.getPaddingLeft();
        final float parentCenterX = paddingLeft +
                (parent.getWidth() - paddingLeft - parent.getPaddingRight()) * 0.5f;
        final float dx = parentCenterX - childrenCenterX;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child != null) {
                child.setTranslationX(dx);
            }
        }
    }
}