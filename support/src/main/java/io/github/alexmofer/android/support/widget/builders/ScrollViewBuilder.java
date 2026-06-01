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
import android.widget.ScrollView;

import androidx.annotation.NonNull;

/**
 * ScrollView 构建器
 * Created by Alex on 2026/1/23.
 */
public final class ScrollViewBuilder extends ViewGroupBuilder {
    private final ScrollView mView;

    public ScrollViewBuilder(@NonNull ScrollView view) {
        super(view);
        mView = view;
    }

    public ScrollViewBuilder(@NonNull Context context) {
        this(new ScrollView(context));
    }

    @NonNull
    @Override
    public ScrollView build() {
        return mView;
    }

    public ScrollViewBuilder setFillViewport(boolean fillViewport) {
        mView.setFillViewport(fillViewport);
        return this;
    }
}
