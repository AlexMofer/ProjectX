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
import androidx.lifecycle.LifecycleOwner;

import io.github.alexmofer.android.support.utils.InputMethodManagerUtils;
import io.github.alexmofer.android.support.utils.ScrollViewUtils;
import io.github.alexmofer.android.support.window.AvoidAreaCalculator;

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
        this(build(context));
    }

    @NonNull
    public static ScrollView build(@NonNull Context context) {
        return new ScrollView(context);
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

    public ScrollViewBuilder setSmoothScrollingEnabled(boolean smoothScrollingEnabled) {
        mView.setSmoothScrollingEnabled(smoothScrollingEnabled);
        return this;
    }

    /**
     * 设置自动滚动到焦点子项（软键盘导致布局变更）
     *
     * @param calculator  避让区域计算器
     * @param owner       生命周期拥有者
     * @param offsetTop   聚焦子项顶部偏移
     * @param delayMillis 延迟时间
     */
    public ScrollViewBuilder setAutoScrollToFocusChild(@NonNull AvoidAreaCalculator calculator,
                                                       @NonNull LifecycleOwner owner,
                                                       int offsetTop, long delayMillis) {
        calculator.calculateBottom(owner, false, unused ->
                mView.postDelayed(() -> {
                    if (InputMethodManagerUtils.isKeyboardOpen(mView)) {
                        ScrollViewUtils.scrollToFocusChild(mView, offsetTop);
                    }
                }, delayMillis));
        return this;
    }
}
