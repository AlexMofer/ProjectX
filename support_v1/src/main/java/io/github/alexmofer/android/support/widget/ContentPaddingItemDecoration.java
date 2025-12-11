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

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 内容间隔 ItemDecoration
 * Created by Alex on 2025/7/17.
 */
public class ContentPaddingItemDecoration extends RecyclerView.ItemDecoration {

    private final int mLeft;
    private final int mTop;
    private final int mRight;
    private final int mBottom;
    private final boolean mSupportRTL;

    public ContentPaddingItemDecoration(int left, int top, int right, int bottom,
                                        boolean supportRTL) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
        mSupportRTL = supportRTL;
    }

    public ContentPaddingItemDecoration(int start, int top, int end, int bottom) {
        this(start, top, end, bottom, true);
    }

    public ContentPaddingItemDecoration(int padding) {
        this(padding, padding, padding, padding);
    }

    static void resolveLayoutDirection(@NonNull Rect outRect, @NonNull View view) {
        if (view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            final int left = outRect.left;
            outRect.left = outRect.right;
            outRect.right = left;
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) manager).getOrientation()
                    == LinearLayoutManager.HORIZONTAL) {
                outRect.top = mTop;
                outRect.bottom = mBottom;
                final int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = mLeft;
                }
                final int count = state.getItemCount();
                if (position == count - 1) {
                    outRect.right = mRight;
                }
            } else {
                outRect.left = mLeft;
                outRect.right = mRight;
                final int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.top = mTop;
                }
                final int count = state.getItemCount();
                if (position == count - 1) {
                    outRect.bottom = mBottom;
                }
            }
            if (mSupportRTL) {
                resolveLayoutDirection(outRect, parent);
            }
        }
    }
}
