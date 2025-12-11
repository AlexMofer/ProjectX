/*
 * Copyright (C) 2024 AlexMofer
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

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

/**
 * ViewBindingPageViewHolder
 * Created by Alex on 2024/6/18.
 */
public class ViewBindingPageViewHolder<V extends ViewBinding>
        extends RecyclePagerAdapter.PagerViewHolder {

    protected final V binding;

    public ViewBindingPageViewHolder(@NonNull V binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
