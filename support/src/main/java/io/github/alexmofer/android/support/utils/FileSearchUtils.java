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

import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import io.github.alexmofer.android.support.function.FunctionPObjectDouble;

/**
 * 文件搜索工具
 * Created by Alex on 2026/5/19.
 */
public final class FileSearchUtils {
    public static final double SCORE_MAX = 1000.0;
    private static final double SCORE_START = 500.0;
    private static final double SCORE_CONTAINS = 200.0;
    private static final double SCORE_SIMILARITY = 100.0;
    private static final double SCORE_LENGTH_PENALTY = 10.0;

    private FileSearchUtils() {
        //no instance
    }

    /**
     * Levenshtein Distance (编辑距离) 算法，计算两个字符串的相似度
     * 优化：使用一维数组减少内存占用，提高性能
     */
    private static double getSimilarity(@NonNull String str1, @NonNull String str2) {
        int len1 = str1.length();
        int len2 = str2.length();

        if (len1 == 0 || len2 == 0) return 0.0;
        if (str1.equals(str2)) return 1.0;

        // 确保 str2 是较短的字符串，以优化空间复杂度
        if (len1 < len2) {
            String temp = str1;
            str1 = str2;
            str2 = temp;
            int tempLen = len1;
            len1 = len2;
            len2 = tempLen;
        }

        int[] prevRow = new int[len2 + 1];
        int[] currRow = new int[len2 + 1];

        for (int j = 0; j <= len2; j++) {
            prevRow[j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            currRow[0] = i;
            char c1 = str1.charAt(i - 1);
            for (int j = 1; j <= len2; j++) {
                char c2 = str2.charAt(j - 1);
                int cost = (c1 == c2) ? 0 : 1;

                int deletion = prevRow[j] + 1;
                int insertion = currRow[j - 1] + 1;
                int substitution = prevRow[j - 1] + cost;

                currRow[j] = Math.min(Math.min(deletion, insertion), substitution);
            }

            // 交换行引用
            int[] temp = prevRow;
            prevRow = currRow;
            currRow = temp;
        }

        int distance = prevRow[len2];
        // 相似度 = 1 - (编辑距离 / 最长字符串长度)
        return 1.0 - (double) distance / len1;
    }

    /**
     * 计算匹配度分数（分数越高越匹配）
     */
    private static double calculateMatchScore(@NonNull String fileName, @NonNull String query) {
        if (fileName.equals(query)) {
            return SCORE_MAX; // 完美完全匹配，赋予最高权重
        }

        double score = 0.0;

        // 1. 位置与包含关系权重
        if (fileName.startsWith(query)) {
            score += SCORE_START; // 前缀匹配
        } else if (fileName.contains(query)) {
            score += SCORE_CONTAINS; // 包含匹配
        }

        // 2. 引入编辑距离相似度 (0.0 ~ 1.0)
        double similarity = getSimilarity(fileName, query);
        score += similarity * SCORE_SIMILARITY;

        // 3. 长度惩罚（避免 "test.txt" 和 "a_very_long_name_with_test_inside.txt" 分数一样）
        // 文件名越短，query 的占比越高
        // 防止除以零，虽然 fileName 为空时前面可能已经处理，但为了健壮性
        //noinspection SizeReplaceableByIsEmpty
        if (fileName.length() > 0) {
            score += (1.0 / fileName.length()) * SCORE_LENGTH_PENALTY;
        }

        return score;
    }

    @NonNull
    private static String convertGlobToRegex(@NonNull String glob) {
        StringBuilder regex = new StringBuilder("^");
        for (char c : glob.toCharArray()) {
            switch (c) {
                case '*':
                    regex.append(".*");
                    break;
                case '?':
                    regex.append(".");
                    break;
                case '.':
                case '\\':
                case '$':
                case '^':
                case '+':
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case '|':
                    regex.append("\\").append(c);
                    break;
                default:
                    regex.append(c);
                    break;
            }
        }
        regex.append("$");
        return regex.toString();
    }

    private static void executeSearch(@NonNull File currentDir,
                                      @NonNull FileFilter fileFilter,
                                      @NonNull FileFilter dirFilter,
                                      @NonNull Pattern pattern,
                                      @NonNull String cleanQuery,
                                      @NonNull FunctionPObjectDouble<File> callback,
                                      @NonNull CancellationSignal signal)
            throws OperationCanceledException {
        final File[] files = currentDir.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            signal.throwIfCanceled();
            // fileFilter 筛选器决定是否接受文件或者文件夹作为搜索结果
            if (fileFilter.accept(file)) {
                final String name = file.getName();
                // 对文件名进行模式匹配
                if (pattern.matcher(name).matches()) {
                    // 计算分数（大小写不敏感）
                    final double score = calculateMatchScore(name.toLowerCase(), cleanQuery);
                    callback.execute(file, score);
                }
            }
            if (file.isDirectory()) {
                // dirFilter 筛选器决定是否递归搜索子目录
                if (dirFilter.accept(file)) {
                    executeSearch(file, fileFilter, dirFilter, pattern, cleanQuery, callback, signal);
                }
            }
        }
    }

    /**
     * 搜索目录
     *
     * @param startDir   起始目录
     * @param fileFilter 文件过滤器，用于控制文件或者文件夹是否可作为搜索结果
     * @param dirFilter  目录过滤器，用于控制子目录是否可搜索
     * @param criterion  搜索条件
     * @param isRegex    是否为正则表达式
     * @param callback   回调
     */
    public static void search(@NonNull File startDir,
                              @NonNull FileFilter fileFilter,
                              @NonNull FileFilter dirFilter,
                              @NonNull String criterion, boolean isRegex,
                              @NonNull FunctionPObjectDouble<File> callback,
                              @NonNull CancellationSignal signal) throws OperationCanceledException {
        if (startDir.isFile()) {
            return;
        }
        if (criterion.isEmpty()) {
            return;
        }

        final String regex;
        try {
            regex = isRegex ? criterion : convertGlobToRegex(criterion);
        } catch (Exception e) {
            // 如果正则转换出错，直接返回
            return;
        }

        final Pattern pattern;
        try {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            // 如果正则编译出错，直接返回
            return;
        }

        // 清理查询字符串用于相似度计算，去除通配符
        final String cleanQuery = criterion.replace("*", "").replace("?", "").toLowerCase();
        signal.throwIfCanceled();
        executeSearch(startDir, fileFilter, dirFilter, pattern, cleanQuery, callback, signal);
    }
}
