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
import android.graphics.Color;
import android.os.Build;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;

/**
 * EditText 构建器
 * Created by Alex on 2025/6/24.
 */
public final class EditTextBuilder extends TextViewBuilder {
    private final EditText mView;

    public EditTextBuilder(@NonNull EditText view) {
        super(view);
        this.mView = view;
    }

    public EditTextBuilder(@NonNull Context context) {
        this(build(context));
    }

    @NonNull
    public static EditText build(@NonNull Context context) {
        final AppCompatEditText editor = new AppCompatEditText(context);
        editor.setBackgroundColor(Color.TRANSPARENT);
        editor.setMinimumHeight(0);
        editor.setMinHeight(0);
        editor.setMinimumWidth(0);
        editor.setMinWidth(0);
        editor.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        return editor;
    }

    @NonNull
    @Override
    public EditText build() {
        return mView;
    }

    public EditTextBuilder setSelection(int start, int stop) {
        mView.setSelection(start, stop);
        return this;
    }

    public EditTextBuilder setSelection(int index) {
        mView.setSelection(index);
        return this;
    }

    public EditTextBuilder selectAll() {
        mView.selectAll();
        return this;
    }

    public EditTextBuilder extendSelection(int index) {
        mView.extendSelection(index);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public EditTextBuilder setStyleShortcutsEnabled(boolean enabled) {
        mView.setStyleShortcutsEnabled(enabled);
        return this;
    }
}
