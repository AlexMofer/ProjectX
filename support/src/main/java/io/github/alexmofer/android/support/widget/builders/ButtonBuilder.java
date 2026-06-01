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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

/**
 * 按钮构建器
 * Created by Alex on 2025/6/24.
 */
public final class ButtonBuilder extends TextViewBuilder {
    private final Button mView;

    public ButtonBuilder(@NonNull Button view) {
        super(view);
        mView = view;
    }

    public ButtonBuilder(@NonNull Context context) {
        this(new AppCompatButton(context, null, 0));
    }

    @NonNull
    @Override
    public Button build() {
        return mView;
    }
}
