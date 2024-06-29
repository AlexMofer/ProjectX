/*
 * Copyright (C) 2022 AlexMofer
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

import androidx.recyclerview.widget.RecyclerView;

/**
 * 长按监听
 * Created by Alex on 2022/5/2.
 */
public interface OnViewHolderLongClickListener<VH extends RecyclerView.ViewHolder> {

    /**
     * 子项长按
     *
     * @param holder 子项
     */
    boolean onLongClick(VH holder);
}
