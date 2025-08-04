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
package io.github.alexmofer.android.support.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.function.IntConsumer;

/**
 * RecyclerView 工具
 * Created by Alex on 2025/8/4.
 */
public class RecyclerViewUtils {

    private RecyclerViewUtils() {
        //no instance
    }

    /**
     * 添加子项个数变更监听
     * @param view RecyclerView
     * @param listener 监听
     */
    public static void addChildrenCountChangedListener(RecyclerView view, IntConsumer listener) {
        view.addOnLayoutChangeListener((v, left, top, right, bottom,
                                        oldLeft, oldTop, oldRight, oldBottom)
                -> listener.accept(view.getChildCount()));
        view.addOnChildAttachStateChangeListener(
                new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(@NonNull View child) {
                        listener.accept(view.getChildCount());
                    }

                    @Override
                    public void onChildViewDetachedFromWindow(@NonNull View child) {
                        listener.accept(view.getChildCount());
                    }
                });
    }
}
