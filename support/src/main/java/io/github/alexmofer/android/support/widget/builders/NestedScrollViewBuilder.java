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
package io.github.alexmofer.android.support.widget.builders;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

/**
 * NestedScrollView 构造器
 * Created by Alex on 2026/7/21.
 */
public final class NestedScrollViewBuilder extends ViewGroupBuilder {
    private final NestedScrollView mView;

    public NestedScrollViewBuilder(@NonNull NestedScrollView view) {
        super(view);
        mView = view;
    }

    public NestedScrollViewBuilder(@NonNull Context context) {
        this(build(context));
    }

    @NonNull
    public static NestedScrollView build(@NonNull Context context) {
        return new NestedScrollView(context);
    }

    @NonNull
    @Override
    public NestedScrollView build() {
        return mView;
    }

    @NonNull
    public NestedScrollViewBuilder setFillViewport(boolean fillViewport) {
        mView.setFillViewport(fillViewport);
        return this;
    }

    @NonNull
    public NestedScrollViewBuilder setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        mView.setSmoothScrollingEnabled(smoothScrollingEnabled);
        return this;
    }
}