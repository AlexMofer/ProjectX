/*
 * Copyright (C) 2022 AlexMofer
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

import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Char工具
 * Created by Alex on 2022/12/10.
 */
public class CharacterUtils {

    private CharacterUtils() {
        //no instance
    }

    /**
     * 通过UnicodeScript判断是否为中文
     *
     * @param c 字符
     * @return 为中文字符时返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isChineseByScript(char c) {
        return Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN;
    }

    /**
     * 通过UnicodeBlock判断是否为中文字符
     *
     * @param c 字符
     * @return 为中文字符时返回true
     */
    public static boolean isChineseByBlock(char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if (block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || block == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return block == Character.UnicodeBlock.CJK_STROKES
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D;
        }
        return false;
    }

    /**
     * 判断是否为中文字符
     *
     * @param c 字符
     * @return 为中文字符时返回true
     */
    public static boolean isChinese(char c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return isChineseByScript(c);
        } else {
            return isChineseByBlock(c);
        }
    }


    /**
     * 判断是否为CJK字符
     *
     * @param c 字符
     * @return 为CJK字符时返回true
     */
    public static boolean isCJK(char c) {
        final Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        if (block == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || block == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || block == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return block == Character.UnicodeBlock.CJK_STROKES
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                    || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D;
        }
        return false;
    }

    /**
     * 判断是否为一般标点符号
     *
     * @param c 字符
     * @return 为一般标点符号时返回true
     */
    public static boolean isGeneralPunctuation(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 判断是否为半角或全角符号
     *
     * @param c 字符
     * @return 为半角或全角符号时返回true
     */
    public static boolean isHalfWidthAndFullWidthForms(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
}
