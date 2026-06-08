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

import android.animation.LayoutTransition;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * ViewGroup 构建器
 * Created by Alex on 2025/7/15.
 */
public class ViewGroupBuilder extends ViewBuilder {

    private final ViewGroup mView;

    public ViewGroupBuilder(ViewGroup view) {
        super(view);
        this.mView = view;
    }

    public static ViewGroupBuilder newFrameLayout(Context context) {
        return new ViewGroupBuilder(new FrameLayout(context));
    }

    @NonNull
    public static ViewGroup.LayoutParams newMatchWidth() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    public static ViewGroup.LayoutParams newMatchHeight() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @NonNull
    public static ViewGroup.LayoutParams newMatchAll() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @NonNull
    @Override
    public ViewGroup build() {
        return mView;
    }

    public ViewGroupBuilder addView(View child) {
        mView.addView(child);
        return this;
    }

    public ViewGroupBuilder addView(View child, ViewGroup.LayoutParams params) {
        mView.addView(child, params);
        return this;
    }

    public ViewGroupBuilder setLayoutTransition(LayoutTransition transition) {
        mView.setLayoutTransition(transition);
        return this;
    }

    public ViewGroupBuilder setClipChildren(boolean clipChildren) {
        mView.setClipChildren(clipChildren);
        return this;
    }

    public ViewGroupBuilder setClipToPadding(boolean clipToPadding) {
        mView.setClipToPadding(clipToPadding);
        return this;
    }

    public ViewGroupBuilder setTransitionGroup(boolean isTransitionGroup) {
        mView.setTransitionGroup(isTransitionGroup);
        return this;
    }
}
