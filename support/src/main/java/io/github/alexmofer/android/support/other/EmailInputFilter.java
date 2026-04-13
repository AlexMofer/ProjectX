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
package io.github.alexmofer.android.support.other;

import android.text.InputFilter;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.regex.Pattern;

/**
 * 邮箱输入筛选器
 * Created by Alex on 2026/4/13.
 */
public final class EmailInputFilter implements InputFilter {
    // 定义允许的字符正则表达式：字母、数字、. _ + - 和 @
    private static final String ALLOWED_CHARS_PATTERN = "[a-zA-Z0-9._+\\-@]";
    // 更严格的邮箱地址正则表达式
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9]+([._+-][a-zA-Z0-9]+)*@([a-zA-Z0-9]+[-.])+[a-zA-Z]{2,}$";
    private final boolean mEnhanced;
    private final Pattern mPattern;

    public EmailInputFilter(boolean enhanced) {
        mEnhanced = enhanced;
        mPattern = enhanced ? Pattern.compile(EMAIL_PATTERN) : Pattern.compile(ALLOWED_CHARS_PATTERN);
    }

    public EmailInputFilter() {
        this(false);
    }

    @Nullable
    @Override
    public CharSequence filter(@NonNull CharSequence source, int start, int end,
                               @NonNull Spanned dest, int dstart, int dend) {
        if (mEnhanced) {
            // 构建新的文本
            final String newText = dest.subSequence(0, dstart).toString() +
                    source.subSequence(start, end) +
                    dest.subSequence(dend, dest.length());
            // 使用正则表达式验证整个邮箱格式
            if (!mPattern.matcher(newText).matches() && !newText.isEmpty()) {
                return ""; // 拒绝不符合邮箱格式的输入
            }
        } else {
            // 逐个检查新输入的字符
            for (int i = start; i < end; i++) {
                final String currentChar = String.valueOf(source.charAt(i));

                // 如果字符不在允许的范围内，拒绝输入
                if (!mPattern.matcher(currentChar).matches()) {
                    return "";
                }
                // 额外的业务逻辑检查
                if (!validateBusinessRules(source, dest, dstart, currentChar)) {
                    return "";
                }
            }
        }
        return null; // 接受所有输入字符
    }

    /**
     * 验证业务规则
     */
    private static boolean validateBusinessRules(@NonNull CharSequence source,
                                                 @NonNull Spanned dest,
                                                 int dstart,
                                                 @NonNull String currentChar) {
        final String currentText = dest.toString();

        // 规则1: "@"符号只能出现一次
        if ("@".equals(currentChar)) {
            if (currentText.contains("@") ||
                    source.toString().indexOf('@') != source.toString().lastIndexOf('@')) {
                return false;
            }
        }

        // 规则2: 点号不能连续出现，也不能在开头
        if (".".equals(currentChar)) {
            if (dstart == 0) {
                // 点号不能在开头
                return false;
            }
            // 点号不能连续
            return dstart <= 0 || currentText.isEmpty() || currentText.charAt(dstart - 1) != '.';
        }

        return true;
    }
}