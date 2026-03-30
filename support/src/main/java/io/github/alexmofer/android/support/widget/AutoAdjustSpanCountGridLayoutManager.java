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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.util.TypedValueCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 自动调整子项个数
 * Created by Alex on 2026/3/30.
 */
public class AutoAdjustSpanCountGridLayoutManager extends GridLayoutManager {
    private final int mSpanMinCount;
    private final float mItemMinWidth;
    private final boolean mNotifyItemDecorations;
    private final View.OnLayoutChangeListener mListener = this::onLayoutChange;

    public AutoAdjustSpanCountGridLayoutManager(@NonNull Context context,
                                                int orientation, boolean reverseLayout,
                                                int spanMinCount, float itemMinWidth,
                                                boolean notifyItemDecorations) {
        super(context, spanMinCount, orientation, reverseLayout);
        mSpanMinCount = spanMinCount;
        mItemMinWidth = itemMinWidth;
        mNotifyItemDecorations = notifyItemDecorations;
    }

    public AutoAdjustSpanCountGridLayoutManager(@NonNull Context context,
                                                int spanMinCount, int itemMinWidth,
                                                boolean notifyItemDecorations) {
        super(context, spanMinCount);
        mSpanMinCount = spanMinCount;
        mItemMinWidth = itemMinWidth;
        mNotifyItemDecorations = notifyItemDecorations;
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        view.addOnLayoutChangeListener(mListener);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        view.removeOnLayoutChangeListener(mListener);
    }

    private void onLayoutChange(View v, int left, int top, int right, int bottom,
                                int oldLeft, int oldTop, int oldRight, int oldBottom) {
        final int width = v.getWidth();
        final float itemWidth = TypedValueCompat.dpToPx(mItemMinWidth,
                v.getResources().getDisplayMetrics());
        final int count = Math.max(mSpanMinCount, Math.round(width / itemWidth));
        final RecyclerView.LayoutManager manager = ((RecyclerView) v).getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            if (count == ((GridLayoutManager) manager).getSpanCount()) {
                return;
            }
            v.post(() -> {
                ((GridLayoutManager) manager).setSpanCount(count);
                if (mNotifyItemDecorations) {
                    ((RecyclerView) v).invalidateItemDecorations();
                }
            });
        }
    }
}