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
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;

/**
 * 自动调整子项个数
 * Created by Alex on 2026/11/12.
 */
public final class AutoAdjustSpanCount {

    private AutoAdjustSpanCount() {
        //no instance
    }

    private static class Handler {
        private final int mSpanMinCount;
        private final float mItemMinWidth;
        private final ToIntFunction<RecyclerView.LayoutManager> mGetter;
        private final ObjIntConsumer<RecyclerView.LayoutManager> mSetter;
        private final boolean mNotifyItemDecorations;
        private final View.OnLayoutChangeListener mListener = this::onLayoutChange;

        public Handler(int spanMinCount,
                       @Px int itemMinWidth,
                       @NonNull ToIntFunction<RecyclerView.LayoutManager> getter,
                       @NonNull ObjIntConsumer<RecyclerView.LayoutManager> setter,
                       boolean notifyItemDecorations) {
            mSpanMinCount = spanMinCount;
            mItemMinWidth = itemMinWidth;
            mGetter = getter;
            mSetter = setter;
            mNotifyItemDecorations = notifyItemDecorations;
        }

        public void onAttachedToWindow(@NonNull RecyclerView view) {
            view.addOnLayoutChangeListener(mListener);
        }

        public void onDetachedFromWindow(@NonNull RecyclerView view) {
            view.removeOnLayoutChangeListener(mListener);
        }

        public void onLayoutChange(@NonNull View v,
                                   int left, int top, int right, int bottom,
                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
            final int count = Math.max(mSpanMinCount, Math.round((float) v.getWidth() / mItemMinWidth));
            final RecyclerView.LayoutManager manager = ((RecyclerView) v).getLayoutManager();
            final int countSet = mGetter.applyAsInt(manager);
            if (count == countSet) {
                return;
            }
            v.post(() -> {
                mSetter.accept(manager, count);
                if (mNotifyItemDecorations) {
                    ((RecyclerView) v).invalidateItemDecorations();
                }
            });
        }
    }

    public static class GridLayoutManager extends
            androidx.recyclerview.widget.GridLayoutManager {
        private final Handler mHandler;

        public GridLayoutManager(@NonNull Context context,
                                 int orientation,
                                 boolean reverseLayout,
                                 int spanMinCount,
                                 @Px int itemMinWidth,
                                 boolean notifyItemDecorations) {
            super(context, spanMinCount, orientation, reverseLayout);
            mHandler = new Handler(spanMinCount, itemMinWidth,
                    GridLayoutManager::getSpanCount,
                    GridLayoutManager::setSpanCount,
                    notifyItemDecorations);
        }

        public GridLayoutManager(@NonNull Context context,
                                 int spanMinCount,
                                 @Px int itemMinWidth,
                                 boolean notifyItemDecorations) {
            super(context, spanMinCount);
            mHandler = new Handler(spanMinCount, itemMinWidth,
                    GridLayoutManager::getSpanCount,
                    GridLayoutManager::setSpanCount,
                    notifyItemDecorations);
        }

        public GridLayoutManager(@NonNull Context context,
                                 int spanMinCount,
                                 @Px int itemMinWidth) {
            this(context, spanMinCount, itemMinWidth, false);
        }

        public GridLayoutManager(@NonNull Context context,
                                 @Px int itemMinWidth) {
            this(context, 1, itemMinWidth);
        }

        private static int getSpanCount(RecyclerView.LayoutManager layoutManager) {
            if (layoutManager instanceof GridLayoutManager) {
                return ((GridLayoutManager) layoutManager).getSpanCount();
            }
            return 1;
        }

        private static void setSpanCount(RecyclerView.LayoutManager layoutManager, int spanCount) {
            if (layoutManager instanceof GridLayoutManager) {
                ((GridLayoutManager) layoutManager).setSpanCount(spanCount);
            }
        }

        @Override
        public void onAttachedToWindow(RecyclerView view) {
            super.onAttachedToWindow(view);
            mHandler.onAttachedToWindow(view);
        }

        @Override
        public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
            super.onDetachedFromWindow(view, recycler);
            mHandler.onDetachedFromWindow(view);
        }
    }

    public static class StaggeredGridLayoutManager extends
            androidx.recyclerview.widget.StaggeredGridLayoutManager {
        private final Handler mHandler;

        public StaggeredGridLayoutManager(int spanMinCount,
                                          int orientation,
                                          @Px int itemMinWidth,
                                          boolean notifyItemDecorations) {
            super(spanMinCount, orientation);
            mHandler = new Handler(spanMinCount, itemMinWidth,
                    StaggeredGridLayoutManager::getSpanCount,
                    StaggeredGridLayoutManager::setSpanCount,
                    notifyItemDecorations);
        }

        public StaggeredGridLayoutManager(int spanMinCount,
                                          @Px int itemMinWidth,
                                          boolean notifyItemDecorations) {
            this(spanMinCount, VERTICAL, itemMinWidth, notifyItemDecorations);
        }

        public StaggeredGridLayoutManager(int spanMinCount,
                                          @Px int itemMinWidth) {
            this(spanMinCount, itemMinWidth, false);
        }

        public StaggeredGridLayoutManager(@Px int itemMinWidth) {
            this(1, itemMinWidth);
        }

        private static int getSpanCount(RecyclerView.LayoutManager layoutManager) {
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
            }
            return 1;
        }

        private static void setSpanCount(RecyclerView.LayoutManager layoutManager, int spanCount) {
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                ((StaggeredGridLayoutManager) layoutManager).setSpanCount(spanCount);
            }
        }

        @Override
        public void onAttachedToWindow(RecyclerView view) {
            super.onAttachedToWindow(view);
            mHandler.onAttachedToWindow(view);
        }

        @Override
        public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
            super.onDetachedFromWindow(view, recycler);
            mHandler.onDetachedFromWindow(view);
        }
    }
}
