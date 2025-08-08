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

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

/**
 * 输入框工具
 * Created by Alex on 2025/8/8.
 */
public class EditTextUtils {

    private EditTextUtils() {
        //no instance
    }

    /**
     * 设置自动聚焦
     *
     * @param editor      EditText
     * @param delayMillis 触发延迟
     */
    public static void setAutoFocus(EditText editor, long delayMillis) {
        editor.addOnAttachStateChangeListener(new AutoFocus(delayMillis));
    }

    /**
     * 设置文件名输入
     *
     * @param editor    EditText
     * @param extension 拓展名，null或者长度0表示为文件夹
     */
    public static void setFilenameInput(EditText editor, @Nullable String extension) {
        final InputFilter.LengthFilter lengthFilter =
                new InputFilter.LengthFilter(FileUtils.MAX_LENGTH_NAME);
        final FilenameFilter filenameFilter =
                new FilenameFilter();
        editor.setFilters(new InputFilter[]{lengthFilter, filenameFilter});
        if (TextUtils.isEmpty(extension)) {
            // 文件夹
            editor.setSelectAllOnFocus(true);
        } else {
            // 文件
            editor.setOnFocusChangeListener(new FileNameSelector(editor, extension));
        }
    }

    /**
     * 添加文本变化监听
     *
     * @param editor   EditText
     * @param consumer 回调
     */
    public static TextWatcher addOnTextChangedListener(EditText editor,
                                                       Consumer<Editable> consumer) {
        final TextWatcher watcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                consumer.accept(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // do nothing
            }
        };
        editor.addTextChangedListener(watcher);
        return watcher;
    }

    private static class AutoFocus implements View.OnAttachStateChangeListener {

        private final long mDelayMillis;

        public AutoFocus(long delayMillis) {
            mDelayMillis = delayMillis;
        }

        @Override
        public void onViewAttachedToWindow(@NonNull View v) {
            v.postDelayed(() -> {
                try {
                    InputMethodManagerUtils.showSoftInput(v);
                } catch (Throwable t) {
                    // ignore
                }
            }, mDelayMillis);
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull View v) {
            v.removeOnAttachStateChangeListener(this);
        }
    }

    private static class FilenameFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            if (TextUtils.isEmpty(source)) {
                return source;
            }
            final StringBuilder builder = new StringBuilder();
            final int count = source.length();
            for (int i = 0; i < count; i++) {
                final char c = source.charAt(i);
                if (c == FileUtils.ILLEGAL_CHARACTER_0 || c == FileUtils.ILLEGAL_CHARACTER_1
                        || c == FileUtils.ILLEGAL_CHARACTER_2 || c == FileUtils.ILLEGAL_CHARACTER_3
                        || c == FileUtils.ILLEGAL_CHARACTER_4 || c == FileUtils.ILLEGAL_CHARACTER_5
                        || c == FileUtils.ILLEGAL_CHARACTER_6 || c == FileUtils.ILLEGAL_CHARACTER_7
                        || c == FileUtils.ILLEGAL_CHARACTER_8) {
                    continue;
                }
                builder.append(c);
            }
            return builder.length() >= count ? null : builder.toString();
        }
    }

    private static class FileNameSelector implements View.OnFocusChangeListener {

        private final View.OnFocusChangeListener mOriginal;
        private final String mExtension;

        public FileNameSelector(EditText editor, @NonNull String extension) {
            mOriginal = editor.getOnFocusChangeListener();
            mExtension = extension;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (mOriginal != null) {
                mOriginal.onFocusChange(v, hasFocus);
            }
            if (hasFocus) {
                final EditText editor = (EditText) v;
                final String text = editor.getText().toString();
                if (text.length() > mExtension.length() && text.endsWith("." + mExtension)) {
                    editor.setSelection(0, text.length() - mExtension.length() - 1);
                    return;
                }
                editor.selectAll();
            }
        }
    }
}
