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
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * Created by Alex on 2022/3/25.
 */
public class FileUtils {

    public static final int MAX_LENGTH_NAME = 255;// Linux文件名最大字符数
    public static final int MAX_LENGTH_PATH = 4096;// Linux路径最大字符数
    public static final FileFilter FILTER_DIRECTORY_ONLY = File::isDirectory;
    public static final FileFilter FILTER_FILE_ONLY = File::isFile;

    private FileUtils() {
        //no instance
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException 输入输出异常
     */
    public static void copyOrThrow(File source, File target) throws IOException {
        try (final FileInputStream input = new FileInputStream(source);
             final FileOutputStream output = new FileOutputStream(target)) {
            StreamUtils.copy(input, output);
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException 输入输出异常
     */
    public static void copyOrThrow(InputStream source, File target) throws IOException {
        try (final FileOutputStream output = new FileOutputStream(target)) {
            StreamUtils.copy(source, output);
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException 输入输出异常
     */
    public static void copyOrThrow(File source, OutputStream target) throws IOException {
        try (final FileInputStream input = new FileInputStream(source)) {
            StreamUtils.copy(input, target);
        }
    }


    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @return 复制成功时返回true
     */
    public static boolean copyFile(File source, File target) {
        try (final FileInputStream input = new FileInputStream(source);
             final FileOutputStream output = new FileOutputStream(target)) {
            StreamUtils.copy(input, output);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @return 复制成功时返回true
     */
    public static boolean copyFile(InputStream source, File target) {
        try (final FileOutputStream output = new FileOutputStream(target)) {
            StreamUtils.copy(source, output);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param source 源文件
     * @param target 目标文件
     * @return 复制成功时返回true
     */
    public static boolean copyFile(File source, OutputStream target) {
        try (final FileInputStream input = new FileInputStream(source)) {
            StreamUtils.copy(input, target);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 获取后缀名
     * 如：pdf
     *
     * @param name  文件名
     * @param lower 转化为小写字母
     * @return 后缀名
     */
    public static String getExtension(String name, boolean lower) {
        if (name == null) {
            return null;
        }
        final int index = name.lastIndexOf('.');
        if (index >= 0 && index < name.length() - 1) {
            if (lower) {
                return name.substring(index + 1).toLowerCase();
            } else {
                return name.substring(index + 1);
            }
        }
        return null;
    }

    /**
     * 获取后缀名
     * 如：pdf
     *
     * @param file  文件
     * @param lower 转化为小写字母
     * @return 后缀名
     */
    public static String getExtension(File file, boolean lower) {
        if (file != null && file.isFile()) {
            return getExtension(file.getName(), lower);
        }
        return null;
    }

    /**
     * 获取无后缀名的文件名
     *
     * @param name 文件名
     * @return 无后缀名的文件名
     */
    public static String getNameWithoutExtension(String name) {
        if (name == null) {
            return null;
        }
        final int index = name.lastIndexOf('.');
        if (index < 0) {
            return name;
        }
        if (index == 0) {
            return "";
        }
        return name.substring(0, index);
    }

    /**
     * 校准文件名
     *
     * @param newName      新名称
     * @param originalName 原始名称
     * @param extension    后缀名
     * @return 校准后的文件名
     */
    public static String adjustFileName(@Nullable String newName, String originalName,
                                        @Nullable String extension) {
        if (TextUtils.isEmpty(newName)) {
            // 文件名为空
            return originalName;
        }
        if (!TextUtils.isEmpty(extension)) {
            // 校验后缀名
            if (newName.indexOf('.') == -1) {
                // 无后缀名自动追加
                newName += "." + extension;
            }
            if (TextUtils.equals(newName.toLowerCase(), "." + extension)) {
                // 后缀名错误
                return originalName;
            }
        }
        if (newName.length() > 255) {
            // 文件名过长
            return originalName;
        }
        if (newName.indexOf('\u0000') >= 0 ||
                newName.indexOf('\\') >= 0 ||
                newName.indexOf('/') >= 0 ||
                newName.indexOf(':') >= 0 ||
                newName.indexOf('*') >= 0 ||
                newName.indexOf('?') >= 0 ||
                newName.indexOf('"') >= 0 ||
                newName.indexOf('<') >= 0 ||
                newName.indexOf('>') >= 0 ||
                newName.indexOf('|') >= 0) {
            // 文件名包含非法字符
            return originalName;
        }
        return newName;
    }

    /**
     * 获取无后缀名的文件名
     *
     * @param file 文件
     * @return 无后缀名的文件名
     */
    public static String getNameWithoutExtension(File file) {
        if (file != null && file.isFile()) {
            return getNameWithoutExtension(file.getName());
        }
        return null;
    }

    /**
     * 写入字符串内容
     *
     * @param file    文件
     * @param content 字符串
     * @param cs      字符集
     * @return 是否成功
     */
    public static boolean writeString(File file, String content, Charset cs) {
        if (content == null)
            return file.delete();
        //noinspection IOStreamConstructor
        try (final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), cs))) {
            writer.write(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 写入字符串内容
     *
     * @param file    文件
     * @param content 字符串
     * @return 是否成功
     */
    public static boolean writeString(File file, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return writeString(file, content, StandardCharsets.UTF_8);
        } else {
            //noinspection CharsetObjectCanBeUsed
            return writeString(file, content, Charset.forName("UTF-8"));
        }
    }

    /**
     * 读取字符串内容
     *
     * @param file 文件
     * @param cs   字符集
     * @return 字符串
     */
    public static String readString(File file, Charset cs) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        //noinspection IOStreamConstructor
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), cs))) {
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取字符串内容
     *
     * @param file 文件
     * @return 字符串
     */
    public static String readString(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return readString(file, StandardCharsets.UTF_8);
        } else {
            //noinspection CharsetObjectCanBeUsed
            return readString(file, Charset.forName("UTF-8"));
        }
    }

    /**
     * 删除文件及文件夹
     *
     * @param file 文件及文件夹
     * @return 删除成功时返回true
     */
    public static boolean delete(File file) {
        if (file == null) {
            return true;
        }
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children == null || children.length <= 0) {
                return file.delete();
            }
            boolean deleted = true;
            for (File child : children) {
                if (!delete(child)) {
                    deleted = false;
                }
            }
            return deleted && file.delete();
        }
        return false;
    }

    /**
     * 清空文件夹
     *
     * @param file   文件夹
     * @param filter 筛选器
     * @return 清空文件夹
     */
    public static boolean clearDirectory(File file, @Nullable FileFilter filter) {
        if (file == null) {
            return false;
        }
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children == null || children.length <= 0) {
                return true;
            }
            boolean clear = true;
            for (File child : children) {
                if (filter == null || filter.accept(child)) {
                    if (!delete(child)) {
                        clear = false;
                    }
                }
            }
            return clear;
        }
        return false;
    }


    /**
     * 清空文件夹
     *
     * @param file 文件夹
     * @return 清空文件夹
     */
    public static boolean clearDirectory(File file) {
        return clearDirectory(file, null);
    }

    /**
     * 复制文件夹
     *
     * @param src    源文件夹
     * @param dest   目标文件夹
     * @param filter 筛选器
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object copyDirectory(@NonNull File src, @NonNull File dest,
                                       @Nullable FileFilter filter) {
        if (!src.exists() || !src.isDirectory()) {
            // 数据源不存在或者不是文件夹
            return src;
        }
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                // 目标不是文件夹
                return src;
            }
        } else {
            if (!dest.mkdir()) {
                // 目标文件夹无法创建
                return src;
            }
        }
        final File[] children = src.listFiles();
        final List<File> failure = new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                if (filter != null && !filter.accept(child)) {
                    continue;
                }
                final File target = new File(dest, child.getName());
                if (child.isDirectory()) {
                    final Object result = copyDirectory(child, target, filter);
                    if (result instanceof File) {
                        failure.add(child);
                        continue;
                    }
                    //noinspection unchecked
                    final List<File> fs = (List<File>) result;
                    failure.addAll(fs);
                    continue;
                }
                if (child.isFile()) {
                    if (target.exists()) {
                        failure.add(child);
                        continue;
                    }
                    if (!copyFile(child, target)) {
                        failure.add(child);
                    }
                }
            }
        }
        return failure;
    }

    /**
     * 复制文件夹
     *
     * @param src  源文件夹
     * @param dest 目标文件夹
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object copyDirectory(@NonNull File src, @NonNull File dest) {
        return copyDirectory(src, dest, null);
    }

    /**
     * 移动文件夹
     * 注：非系统应用对非主存储无写入权限，因此不用担心 renameTo 遇到跨挂载点问题，因此该方法仅适用第三方应用
     *
     * @param src    源文件夹
     * @param dest   目标文件夹
     * @param filter 筛选器
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object moveDirectory(@NonNull File src, @NonNull File dest,
                                       @Nullable FileFilter filter) {

        if (!src.exists() || !src.isDirectory()) {
            // 数据源不存在或者不是文件夹
            return src;
        }
        if (dest.exists()) {
            if (!dest.isDirectory()) {
                // 目标不是文件夹
                return src;
            }
        } else {
            if (src.renameTo(dest)) {
                // 直接成功
                return new ArrayList<>();
            }
            return src;
        }
        final File[] children = src.listFiles();
        final List<File> failure = new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                if (filter != null && !filter.accept(child)) {
                    continue;
                }
                final File target = new File(dest, child.getName());
                if (child.isDirectory()) {
                    final Object result = moveDirectory(child, target, filter);
                    if (result instanceof File) {
                        failure.add(child);
                        continue;
                    }
                    //noinspection unchecked
                    final List<File> fs = (List<File>) result;
                    failure.addAll(fs);
                    final File[] after = child.listFiles();
                    if (after == null || after.length == 0) {
                        //noinspection ResultOfMethodCallIgnored
                        child.delete();
                    }
                    continue;
                }
                if (child.isFile()) {
                    if (target.exists()) {
                        failure.add(child);
                        continue;
                    }
                    if (!child.renameTo(target)) {
                        failure.add(child);
                    }
                }
            }
        }
        final File[] after = src.listFiles();
        if (after == null || after.length == 0) {
            //noinspection ResultOfMethodCallIgnored
            src.delete();
        }
        return failure;
    }


    /**
     * 移动文件夹
     * 注：非系统应用对非主存储无写入权限，因此不用担心 renameTo 遇到跨挂载点问题，因此该方法仅适用第三方应用
     *
     * @param src  源文件夹
     * @param dest 目标文件夹
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object moveDirectory(@NonNull File src, @NonNull File dest) {

        return moveDirectory(src, dest, null);
    }

    /**
     * 判断文件夹是否包含该文件
     *
     * @param directory 文件夹
     * @param file      文件
     * @return 包含该文件时返回true
     */
    public static boolean contains(File directory, File file) {
        if (directory == null || file == null) {
            return false;
        }
        if (!directory.exists() || !directory.isDirectory()) {
            return false;
        }
        final String path = file.getPath();
        return path.indexOf(directory.getPath()) == 0;
    }
}
