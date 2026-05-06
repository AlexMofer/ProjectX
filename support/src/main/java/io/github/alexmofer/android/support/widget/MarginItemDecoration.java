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

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 子项间隔 ItemDecoration
 * Created by Alex on 2026/5/6.
 */
public final class MarginItemDecoration extends RecyclerView.ItemDecoration {

    private final int mLeft;
    private final int mTop;
    private final int mRight;
    private final int mBottom;

    private MarginItemDecoration(int left, int top, int right, int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    @NonNull
    public static MarginItemDecoration newInstance(int left, int top, int right, int bottom) {
        return new MarginItemDecoration(left, top, right, bottom);
    }

    @NonNull
    public static MarginItemDecoration newInstance(int horizontal, int vertical) {
        return newInstance(horizontal, vertical, horizontal, vertical);
    }

    @NonNull
    public static MarginItemDecoration newInstanceHorizontal(int horizontal) {
        return newInstance(horizontal, 0);
    }

    @NonNull
    public static MarginItemDecoration newInstanceVertical(int vertical) {
        return newInstance(0, vertical);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mLeft;
        outRect.top = mTop;
        outRect.right = mRight;
        outRect.bottom = mBottom;
    }
}