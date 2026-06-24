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
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

/**
 * HorizontalScrollView 构建器
 * Created by Alex on 2026/1/23.
 */
public final class HorizontalScrollViewBuilder extends ViewGroupBuilder {
    private final HorizontalScrollView mView;

    public HorizontalScrollViewBuilder(@NonNull HorizontalScrollView view) {
        super(view);
        mView = view;
    }

    public HorizontalScrollViewBuilder(@NonNull Context context) {
        this(new HorizontalScrollView(context));
    }

    @NonNull
    @Override
    public HorizontalScrollView build() {
        return mView;
    }

    public HorizontalScrollViewBuilder setFillViewport(boolean fillViewport) {
        mView.setFillViewport(fillViewport);
        return this;
    }
}
