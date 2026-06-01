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

import android.view.View;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

/**
 * Checkable View 构造器
 * Created by Alex on 2026/1/19.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class CheckableViewBuilder<T extends View & Checkable> extends ViewBuilder {
    private final T mView;

    public CheckableViewBuilder(@NonNull T view) {
        super(view);
        mView = view;
    }

    @NonNull
    @Override
    public T build() {
        return mView;
    }

    public ViewBuilder setChecked(boolean checked) {
        mView.setChecked(checked);
        return this;
    }

    public ViewBuilder setChecked(@NonNull LifecycleOwner owner, @NonNull LiveData<Boolean> checked) {
        checked.observe(owner, value -> mView.setChecked(Boolean.TRUE.equals(value)));
        return this;
    }

    public ViewBuilder toggle() {
        mView.toggle();
        return this;
    }
}
