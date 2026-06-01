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

import androidx.annotation.Nullable;

/**
 * 列表更新器包装类
 * Created by Alex on 2026/6/1.
 */
public final class ListUpdaterWrapper implements ListUpdater {
    private ListUpdater mListUpdater;

    public ListUpdater getListUpdater() {
        return mListUpdater;
    }

    public void setListUpdater(@Nullable ListUpdater updater) {
        mListUpdater = updater;
    }

    @Override
    public void notifyDataSetChanged() {
        if (mListUpdater != null) mListUpdater.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        if (mListUpdater != null) mListUpdater.notifyItemChanged(position);
    }

    @Override
    public void notifyItemChanged(int position, @Nullable Object payload) {
        if (mListUpdater != null) mListUpdater.notifyItemChanged(position, payload);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        if (mListUpdater != null) mListUpdater.notifyItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
        if (mListUpdater != null)
            mListUpdater.notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public void notifyItemInserted(int position) {
        if (mListUpdater != null) mListUpdater.notifyItemInserted(position);
    }

    @Override
    public void notifyItemMoved(int fromPosition, int toPosition) {
        if (mListUpdater != null) mListUpdater.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (mListUpdater != null)
            mListUpdater.notifyItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public void notifyItemRemoved(int position) {
        if (mListUpdater != null) mListUpdater.notifyItemRemoved(position);
    }

    @Override
    public void notifyItemRangeRemoved(int positionStart, int itemCount) {
        if (mListUpdater != null) mListUpdater.notifyItemRangeRemoved(positionStart, itemCount);
    }
}