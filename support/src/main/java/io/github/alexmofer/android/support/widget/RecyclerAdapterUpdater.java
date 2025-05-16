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

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecyclerAdapter 更新器
 * Created by Alex on 2025/5/16.
 */
public class RecyclerAdapterUpdater implements ListUpdater {

    private final RecyclerView.Adapter<?> mAdapter;

    public RecyclerAdapterUpdater(RecyclerView.Adapter<?> adapter) {
        mAdapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void notifyItemChanged(int position, @Nullable Object payload) {
        mAdapter.notifyItemChanged(position, payload);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        mAdapter.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        mAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void notifyItemInserted(int position) {
        mAdapter.notifyItemInserted(position);
    }

    @Override
    public void notifyItemMoved(int fromPosition, int toPosition) {
        mAdapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        mAdapter.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void notifyItemRemoved(int position) {
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemRangeRemoved(int positionStart, int itemCount) {
        mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
    }
}
