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
package io.github.alexmofer.android.support.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

/**
 * TextView 工具
 * Created by Alex on 2026/1/23.
 */
public final class TextViewUtils {

    private TextViewUtils() {
        //no instance
    }

    /**
     * 设置自动跑马灯
     *
     * @param view TextView
     */
    public static void setAutoMarquee(@NonNull TextView view) {
        view.setSingleLine();
        view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        view.setFreezesText(true);
        view.setMarqueeRepeatLimit(-1);
        view.setHorizontallyScrolling(true);
        view.setSelected(true);
    }

    /**
     * 添加文本变化监听
     *
     * @param editor   EditText
     * @param consumer 回调
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public static TextWatcher addOnTextChangedListener(@NonNull TextView editor,
                                                       @NonNull Consumer<Editable> consumer) {
        final TextWatcher watcher = TextWatcherUtils.newAfterTextChanged(consumer);
        editor.addTextChangedListener(watcher);
        return watcher;
    }

    /**
     * 设置 TextView 的最大字符长度限制
     *
     * @param textView  目标 TextView 或 EditText
     * @param maxLength 最大允许输入的字符数（如 10 代表最多输入 10 个字符）
     */
    public static void setMaxLength(@NonNull TextView textView, int maxLength) {
        if (maxLength <= 0) {
            textView.setFilters(new InputFilter[0]);
            return;
        }
        final InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(maxLength);
        final InputFilter[] existingFilters = textView.getFilters();
        if (existingFilters != null && existingFilters.length > 0) {
            InputFilter[] newFilters = new InputFilter[existingFilters.length + 1];
            System.arraycopy(existingFilters, 0, newFilters, 0, existingFilters.length);
            newFilters[existingFilters.length] = lengthFilter;
            textView.setFilters(newFilters);
        } else {
            textView.setFilters(new InputFilter[]{lengthFilter});
        }
    }
}
