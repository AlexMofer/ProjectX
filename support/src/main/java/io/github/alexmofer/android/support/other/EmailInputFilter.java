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
    // 用于输入过程中的宽松校验，允许中间状态
    private static final String EMAIL_PARTIAL_PATTERN = "^[a-zA-Z0-9._+\\-]*@?[a-zA-Z0-9._\\-]*$";

    private final boolean mEnhanced;
    private final Pattern mPartialPattern;

    public EmailInputFilter(boolean enhanced) {
        mEnhanced = enhanced;
        mPartialPattern = Pattern.compile(EMAIL_PARTIAL_PATTERN);
    }

    public EmailInputFilter() {
        this(false);
    }

    @Nullable
    @Override
    public CharSequence filter(@NonNull CharSequence source, int start, int end, @NonNull Spanned dest, int dstart, int dend) {
        // 1. 处理删除操作或清空操作 (source 为空)
        if (end == 0) {
            return null; // 允许删除
        }

        // 2. 构建输入后的新文本
        final String prefix = dest.subSequence(0, dstart).toString();
        final String insertion = source.subSequence(start, end).toString();
        final String suffix = dest.subSequence(dend, dest.length()).toString();
        final String newText = prefix + insertion + suffix;

        // 3. 如果新文本为空，允许（例如替换整个内容为空）
        if (newText.isEmpty()) {
            return null;
        }

        if (mEnhanced) {
            // 增强模式：
            // 首先检查是否包含非法字符
            for (int i = 0; i < insertion.length(); i++) {
                char c = insertion.charAt(i);
                if (!Character.isLetterOrDigit(c) && !". _+-@".contains(String.valueOf(c))) {
                    return "";
                }
            }

            // 然后检查基本结构合法性（允许中间状态）
            // 如果完全不符合邮箱的任何部分特征，则拒绝
            if (!mPartialPattern.matcher(newText).matches()) {
                return "";
            }

            // 可选：如果希望只在失去焦点时严格校验，这里可以只返回 null。
            // 但如果要在输入时提供强反馈，可以检查是否违反了硬性规则（如多个@）
            if (countMatches(newText, '@') > 1) {
                return "";
            }

        } else {
            // 基础模式：逐个检查字符
            for (int i = 0; i < insertion.length(); i++) {
                String currentChar = String.valueOf(insertion.charAt(i));

                // 检查允许字符集
                if (!Pattern.matches(ALLOWED_CHARS_PATTERN, currentChar)) {
                    return "";
                }

                // 业务规则检查
                // 计算当前字符在 dest 中的实际插入位置
                int currentInsertPos = dstart + i;
                // 构造到当前字符为止的临时文本，用于验证规则
                String tempText = prefix + insertion.substring(0, i + 1) + suffix;

                if (!validateBusinessRules(tempText, currentInsertPos, currentChar)) {
                    return "";
                }
            }
        }

        return null; // 接受输入
    }

    /**
     * 验证业务规则
     *
     * @param fullText    插入后的完整文本
     * @param insertPos   当前插入字符的位置
     * @param currentChar 当前插入的字符
     */
    private static boolean validateBusinessRules(@NonNull String fullText, int insertPos, @NonNull String currentChar) {
        // 规则1: "@"符号只能出现一次
        if ("@".equals(currentChar)) {
            if (countMatches(fullText, '@') > 1) {
                return false;
            }
        }

        // 规则2: 点号不能连续出现，也不能在开头
        if (".".equals(currentChar)) {
            // 检查是否在开头 (考虑前缀为空且当前是第一个字符的情况)
            if (insertPos == 0) {
                return false;
            }
            // 检查前一个字符是否是点号
            if (fullText.charAt(insertPos - 1) == '.') {
                return false;
            }
        }

        // 规则3: "@"后面不能直接跟点号，或者点号不能在@之前紧挨着(视具体策略而定，通常不做此限制，但防止 .@ 或 @.)
        if ("@".equals(currentChar)) {
            // 如果前一个字符是点号，通常不允许 user.@domain
            if (insertPos > 0 && fullText.charAt(insertPos - 1) == '.') {
                return false;
            }
        }
        if (".".equals(currentChar)) {
            // 如果前一个字符是@，通常不允许 user@.domain
            if (insertPos > 0 && fullText.charAt(insertPos - 1) == '@') {
                return false;
            }
        }

        return true;
    }

    private static int countMatches(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }
}