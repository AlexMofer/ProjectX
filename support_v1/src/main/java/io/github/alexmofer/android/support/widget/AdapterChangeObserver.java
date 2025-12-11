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

import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter 更新观察者
 * Created by Alex on 2024/1/20.
 */
public class AdapterChangeObserver<T> implements Observer<T> {

    private final RecyclerView.Adapter<?> mAdapter;

    public AdapterChangeObserver(RecyclerView.Adapter<?> adapter) {
        mAdapter = adapter;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onChanged(T t) {
        mAdapter.notifyDataSetChanged();
    }
}
