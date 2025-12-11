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

import androidx.annotation.Nullable;

/**
 * 列表更新器
 * Created by Alex on 2025/5/16.
 */
public interface ListUpdater {

    default void notifyDataSetChanged() {
    }

    default void notifyItemChanged(int position) {
    }

    default void notifyItemChanged(int position, @Nullable Object payload) {
    }

    default void notifyItemRangeChanged(int positionStart, int itemCount) {
    }

    default void notifyItemRangeChanged(int positionStart, int itemCount,
                                        @Nullable Object payload) {
    }

    default void notifyItemInserted(int position) {
    }

    default void notifyItemMoved(int fromPosition, int toPosition) {
    }

    default void notifyItemRangeInserted(int positionStart, int itemCount) {
    }

    default void notifyItemRemoved(int position) {
    }

    default void notifyItemRangeRemoved(int positionStart, int itemCount) {
    }
}
