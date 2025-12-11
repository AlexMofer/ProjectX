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

import androidx.annotation.Nullable;

/**
 * Windows资源管理器排序方式工具
 * Created by Alex on 2022/5/2.
 */
public class WindowsExplorerStringCompareUtils {
    private WindowsExplorerStringCompareUtils() {
        //no instance
    }


    /**
     * 比较文件夹
     *
     * @param name1      文件夹名
     * @param name2      文件夹名
     * @param comparator 非数字比较器
     * @return 比较结果
     */
    public static int compareDirectory(String name1, String name2,
                                       @Nullable CharacterComparator comparator) {
        if (name1 == null && name2 == null)
            return 0;
        if (name1 == null)
            return 1;
        if (name2 == null)
            return -1;
        final String[] str = new String[2];
        final int[] pos = new int[2];
        final int[] len = new int[2];
        str[0] = name1;
        str[1] = name2;
        len[0] = name1.length();
        len[1] = name2.length();

        int result = 0;
        while (result == 0 && pos[0] < len[0] && pos[1] < len[1]) {
            char ch1 = str[0].charAt(pos[0]);
            char ch2 = str[1].charAt(pos[1]);

            if (Character.isDigit(ch1)) {
                if (Character.isDigit(ch2)) {
                    // 比较两个数字
                    result = compareNumbers(str, pos, len);
                } else {
                    // 第二个非数字
                    result = -1;
                }
            } else {
                if (Character.isDigit(ch2)) {
                    // 第二个为数字
                    result = 1;
                } else {
                    result = compareOther(str, pos, comparator);
                }
            }
            pos[0]++;
            pos[1]++;
        }
        return result == 0 ? len[0] - len[1] : result;
    }

    private static int compareNumbers(String[] str, int[] pos, int[] len) {
        final String str1 = str[0];
        final String str2 = str[1];
        int pos1 = pos[0];
        int pos2 = pos[1];
        final int len1 = len[0];
        final int len2 = len[1];
        int end1 = pos1 + 1;
        while (end1 < len1 && Character.isDigit(str1.charAt(end1))) {
            end1++;
        }
        int fullLen1 = end1 - pos1;
        while (pos1 < end1 && str1.charAt(pos1) == '0') {
            pos1++;
        }
        int end2 = pos2 + 1;
        while (end2 < len2 && Character.isDigit(str2.charAt(end2))) {
            end2++;
        }
        int fullLen2 = end2 - pos2;
        while (pos2 < end2 && str2.charAt(pos2) == '0') {
            pos2++;
        }

        int delta = (end1 - pos1) - (end2 - pos2);
        if (delta != 0) {
            pos[0] = pos1;
            pos[1] = pos2;
            return delta;
        }
        while (pos1 < end1 && pos2 < end2) {
            delta = str1.charAt(pos1++) - str2.charAt(pos2++);
            if (delta != 0) {
                pos[0] = pos1;
                pos[1] = pos2;
                return delta;
            }
        }
        pos1--;
        pos2--;
        pos[0] = pos1;
        pos[1] = pos2;
        return fullLen2 - fullLen1;
    }

    private static int compareOther(String[] str, int[] pos,
                                    @Nullable CharacterComparator comparator) {
        final String str1 = str[0];
        final String str2 = str[1];
        final int pos1 = pos[0];
        final int pos2 = pos[1];
        if (comparator != null) {
            return comparator.compare(str1, pos1, str2, pos2);
        } else {
            char ch1 = str1.charAt(pos1);
            char ch2 = str2.charAt(pos2);
            if (ch1 == ch2) {
                return 0;
            }
            ch1 = Character.toUpperCase(ch1);
            ch2 = Character.toUpperCase(ch2);
            if (ch1 != ch2) {
                ch1 = Character.toLowerCase(ch1);
                ch2 = Character.toLowerCase(ch2);
            }
            return ch1 - ch2;
        }
    }

    /**
     * 比较文件
     *
     * @param name1      文件名
     * @param name2      文件名
     * @param comparator 非数字比较器
     * @return 比较结果
     */
    public static int compareFile(String name1, String name2,
                                  @Nullable CharacterComparator comparator) {
        if (name1 == null && name2 == null)
            return 0;
        if (name1 == null)
            return 1;
        if (name2 == null)
            return -1;
        String name = name1;
        int index = name.lastIndexOf('.');
        final String prefixName;
        final String suffixName;
        if (index < 0) {
            prefixName = name;
            suffixName = "";
        } else {
            prefixName = name.substring(0, index);
            if (index + 1 == name.length())
                suffixName = "";
            else
                suffixName = name.substring(index + 1);
        }
        name = name2;
        index = name.lastIndexOf('.');
        final String prefixNameO;
        final String suffixNameO;
        if (index < 0) {
            prefixNameO = name;
            suffixNameO = "";
        } else {
            prefixNameO = name.substring(0, index);
            if (index + 1 == name.length())
                suffixNameO = "";
            else
                suffixNameO = name.substring(index + 1);
        }
        final int result = compareDirectory(prefixName, prefixNameO, comparator);
        if (result != 0)
            return result;
        return compareDirectory(suffixName, suffixNameO, comparator);
    }

    /**
     * 字符比较器
     */
    public interface CharacterComparator {

        /**
         * 比较
         *
         * @param text1  字符串1
         * @param index1 坐标1
         * @param text2  字符串2
         * @param index2 坐标2
         * @return 比较结果
         */
        int compare(String text1, int index1, String text2, int index2);
    }
}