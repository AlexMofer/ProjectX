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
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import io.github.alexmofer.android.support.function.FunctionPInt;

/**
 * ViewPager2 构建器
 * Created by Alex on 2026/3/10.
 */
public final class ViewPager2Builder extends ViewGroupBuilder {
    private final ViewPager2 mView;

    public ViewPager2Builder(@NonNull ViewPager2 view) {
        super(view);
        this.mView = view;
    }

    public ViewPager2Builder(@NonNull Context context) {
        this(new ViewPager2(context));
    }

    @NonNull
    @Override
    public ViewPager2 build() {
        return mView;
    }

    public ViewPager2Builder setAdapter(@Nullable RecyclerView.Adapter<?> adapter) {
        mView.setAdapter(adapter);
        return this;
    }

    public ViewPager2Builder setCurrentItem(int item, boolean smoothScroll) {
        mView.setCurrentItem(item, smoothScroll);
        return this;
    }

    public ViewPager2Builder setCurrentItem(@NonNull LifecycleOwner owner,
                                            @NonNull LiveData<Integer> item,
                                            boolean smoothScroll) {
        item.observe(owner, value -> {
            if (value == null || value < 0) {
                return;
            }
            if (mView.getCurrentItem() == value) {
                return;
            }
            mView.setCurrentItem(value, smoothScroll);
        });
        return this;
    }

    public ViewPager2Builder registerOnPageChangeCallback(@NonNull ViewPager2.OnPageChangeCallback callback) {
        mView.registerOnPageChangeCallback(callback);
        return this;
    }

    public ViewPager2Builder registerOnPageChangeCallback(@NonNull FunctionPInt callback) {
        mView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                callback.execute(position);
            }
        });
        return this;
    }
}
