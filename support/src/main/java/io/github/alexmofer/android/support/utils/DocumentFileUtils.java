/*
 * Copyright (C) 2024 AlexMofer
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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import io.github.alexmofer.android.support.other.DocumentFileFilter;

/**
 * DocumentFile 工具
 * Created by Alex on 2024/5/10.
 */
public class DocumentFileUtils {

    private DocumentFileUtils() {
        //no instance
    }

    /**
     * Create a DocumentFile representing the document tree rooted at the given Uri.
     *
     * @param context Context
     * @param uri     the {@link Intent#getData()} from a successful
     *                {@link Intent#ACTION_OPEN_DOCUMENT_TREE} or
     *                {@link Intent#ACTION_OPEN_DOCUMENT} or
     *                {@link Intent#ACTION_CREATE_DOCUMENT} request.
     * @return DocumentFile, and will return {@code null} when called on earlier platform versions.
     */
    @Nullable
    public static DocumentFile fromUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                return DocumentFile.fromTreeUri(context, uri);
            }
            return DocumentFile.fromSingleUri(context, uri);
        }
        return DocumentFile.fromSingleUri(context, uri);
    }

    /**
     * 创建文件
     *
     * @param context Context
     * @param src     源文件
     * @param dir     需要创建文件的文件夹
     * @return 复制成功时返回true
     */
    @Nullable
    public static DocumentFile createFile(Context context,
                                          @NonNull DocumentFile src,
                                          @NonNull DocumentFile dir) {
        if (!src.exists() || !src.isFile()) {
            // 数据源不存在或者不是文件
            return null;
        }
        if (!dir.exists() || !dir.isDirectory()) {
            // 文件夹不存在或者不是文件夹
            return null;
        }
        final String type = src.getType();
        if (type == null) {
            return null;
        }
        final String name = src.getName();
        if (name == null) {
            return null;
        }
        final DocumentFile created = dir.createFile(type, name);
        if (created == null) {
            return null;
        }
        if (!UriUtils.copy(context, src.getUri(), created.getUri())) {
            created.delete();
            return null;
        }
        return created;
    }

    /**
     * 创建文件
     *
     * @param context Context
     * @param src     源文件
     * @param dir     需要创建文件的文件夹
     * @return 复制成功时返回true
     */
    @Nullable
    public static DocumentFile createFile(Context context,
                                          @NonNull File src,
                                          @NonNull DocumentFile dir) {
        if (!src.exists() || !src.isFile()) {
            // 数据源不存在或者不是文件
            return null;
        }
        if (!dir.exists() || !dir.isDirectory()) {
            // 文件夹不存在或者不是文件夹
            return null;
        }
        final String type = DocumentFile.fromFile(src).getType();
        if (type == null) {
            return null;
        }
        final DocumentFile created = dir.createFile(type, src.getName());
        if (created == null) {
            return null;
        }
        if (!UriUtils.copy(context, src, created.getUri())) {
            created.delete();
            return null;
        }
        return created;
    }

    /**
     * 获取或者创建文件夹
     *
     * @param dir  父文件夹
     * @param name 文件夹名称
     * @return 找到或者创建的文件夹，失败时返回null
     */
    @Nullable
    public static DocumentFile getOrCreateDirectory(@NonNull DocumentFile dir,
                                                    @NonNull String name) {
        final DocumentFile find = dir.findFile(name);
        if (find == null) {
            return dir.createDirectory(name);
        }
        if (find.isDirectory()) {
            return find;
        }
        return null;
    }

    /**
     * 移动文件夹
     * 注：完全执行成功时请自行检查 src 是否删除
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @param filter  筛选器
     * @return 返回DocumentFile或者List<DocumentFile>，返回的List<DocumentFile>为空时表示没有错误发生
     */
    @NonNull
    public static Object moveDirectory(Context context,
                                       @NonNull DocumentFile src, @NonNull DocumentFile dest,
                                       @Nullable DocumentFileFilter filter) {
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
            // 目标文件夹未创建
            return src;
        }
        final DocumentFile[] children = src.listFiles();
        final List<DocumentFile> failure = new ArrayList<>();
        for (DocumentFile child : children) {
            if (filter != null && !filter.accept(child)) {
                continue;
            }
            final String name = child.getName();
            if (name == null) {
                failure.add(child);
                continue;
            }
            if (child.isDirectory()) {
                final DocumentFile target = getOrCreateDirectory(dest, name);
                if (target == null) {
                    failure.add(child);
                    continue;
                }
                final Object result = moveDirectory(context, child, target, filter);
                if (result instanceof DocumentFile) {
                    failure.add(child);
                    continue;
                }
                //noinspection unchecked
                final List<DocumentFile> fs = (List<DocumentFile>) result;
                failure.addAll(fs);
                final DocumentFile[] after = child.listFiles();
                if (after.length == 0) {
                    child.delete();
                }
                continue;
            }
            if (child.isFile()) {
                final DocumentFile find = dest.findFile(name);
                if (find != null) {
                    failure.add(child);
                    continue;
                }
                final DocumentFile target = createFile(context, child, dest);
                if (target == null) {
                    failure.add(child);
                    continue;
                }
                child.delete();
            }
        }
        final DocumentFile[] after = src.listFiles();
        if (after.length == 0) {
            src.delete();
        }
        return failure;
    }

    /**
     * 移动文件夹
     * 注：完全执行成功时请自行检查 src 是否删除
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @return 返回DocumentFile或者List<DocumentFile>，返回的List<DocumentFile>为空时表示没有错误发生
     */
    @NonNull
    public static Object moveDirectory(Context context,
                                       @NonNull DocumentFile src, @NonNull DocumentFile dest) {
        return moveDirectory(context, src, dest, null);
    }

    /**
     * 移动文件夹
     * 注：完全执行成功时请自行检查 src 是否删除
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @param filter  筛选器
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object moveDirectory(Context context,
                                       @NonNull File src, @NonNull DocumentFile dest,
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
            // 目标文件夹未创建
            return src;
        }
        final File[] children = src.listFiles();
        final List<File> failure = new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                if (filter != null && !filter.accept(child)) {
                    continue;
                }
                if (child.isDirectory()) {
                    final DocumentFile target = getOrCreateDirectory(dest, child.getName());
                    if (target == null) {
                        failure.add(child);
                        continue;
                    }
                    final Object result = moveDirectory(context, child, target, filter);
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
                    final DocumentFile find = dest.findFile(child.getName());
                    if (find != null) {
                        failure.add(child);
                        continue;
                    }
                    final DocumentFile target = createFile(context, child, dest);
                    if (target == null) {
                        failure.add(child);
                        continue;
                    }
                    //noinspection ResultOfMethodCallIgnored
                    child.delete();
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
     * 注：完全执行成功时请自行检查 src 是否删除
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object moveDirectory(Context context,
                                       @NonNull File src, @NonNull DocumentFile dest) {
        return moveDirectory(context, src, dest, null);
    }

    /**
     * 复制文件夹
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @param filter  筛选器
     * @return 返回DocumentFile或者List<DocumentFile>，返回的List<DocumentFile>为空时表示没有错误发生
     */
    @NonNull
    public static Object copyDirectory(Context context,
                                       @NonNull DocumentFile src, @NonNull DocumentFile dest,
                                       @Nullable DocumentFileFilter filter) {
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
            // 目标文件夹未创建
            return src;
        }
        final DocumentFile[] children = src.listFiles();
        final List<DocumentFile> failure = new ArrayList<>();
        for (DocumentFile child : children) {
            if (filter != null && !filter.accept(child)) {
                continue;
            }
            final String name = child.getName();
            if (name == null) {
                failure.add(child);
                continue;
            }
            if (child.isDirectory()) {
                final DocumentFile target = getOrCreateDirectory(dest, name);
                if (target == null) {
                    failure.add(child);
                    continue;
                }
                final Object result = copyDirectory(context, child, target, filter);
                if (result instanceof DocumentFile) {
                    failure.add(child);
                    continue;
                }
                //noinspection unchecked
                final List<DocumentFile> fs = (List<DocumentFile>) result;
                failure.addAll(fs);
                continue;
            }
            if (child.isFile()) {
                final DocumentFile find = dest.findFile(name);
                if (find != null) {
                    failure.add(child);
                    continue;
                }
                final DocumentFile target = createFile(context, child, dest);
                if (target == null) {
                    failure.add(child);
                }
            }
        }
        return failure;
    }

    /**
     * 复制文件夹
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @return 返回DocumentFile或者List<DocumentFile>，返回的List<DocumentFile>为空时表示没有错误发生
     */
    @NonNull
    public static Object copyDirectory(Context context,
                                       @NonNull DocumentFile src, @NonNull DocumentFile dest) {
        return copyDirectory(context, src, dest, null);
    }

    /**
     * 复制文件夹
     *
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @param filter  筛选器
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object copyDirectory(Context context,
                                       @NonNull File src, @NonNull DocumentFile dest,
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
            // 目标文件夹未创建
            return src;
        }
        final File[] children = src.listFiles();
        final List<File> failure = new ArrayList<>();
        if (children != null) {
            for (File child : children) {
                if (filter != null && !filter.accept(child)) {
                    continue;
                }
                if (child.isDirectory()) {
                    final DocumentFile target = getOrCreateDirectory(dest, child.getName());
                    if (target == null) {
                        failure.add(child);
                        continue;
                    }
                    final Object result = copyDirectory(context, child, target, filter);
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
                    final DocumentFile find = dest.findFile(child.getName());
                    if (find != null) {
                        failure.add(child);
                        continue;
                    }
                    final DocumentFile target = createFile(context, child, dest);
                    if (target == null) {
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
     * @param context Context
     * @param src     源文件夹
     * @param dest    目标文件夹
     * @return 返回File或者List<File>，返回的List<File>为空时表示没有错误发生
     */
    @NonNull
    public static Object copyDirectory(Context context,
                                       @NonNull File src, @NonNull DocumentFile dest) {
        return copyDirectory(context, src, dest, null);
    }

    /**
     * 清空文件夹
     *
     * @param dir    文件夹
     * @param filter 筛选器
     */
    public static void clearDirectory(@NonNull DocumentFile dir,
                                      @Nullable DocumentFileFilter filter) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        final DocumentFile[] children = dir.listFiles();
        for (DocumentFile child : children) {
            if (filter != null && !filter.accept(child)) {
                continue;
            }
            if (child.isDirectory()) {
                delete(child);
                continue;
            }
            child.delete();
        }
    }

    /**
     * 清空文件夹
     *
     * @param dir 文件夹
     */
    public static void clearDirectory(@NonNull DocumentFile dir) {
        clearDirectory(dir, null);
    }

    /**
     * 删除文件或文件夹
     *
     * @param file 文件或文件夹
     */
    public static void delete(@NonNull DocumentFile file) {
        if (!file.exists()) {
            return;
        }
        if (file.delete()) {
            return;
        }
        if (file.isDirectory()) {
            final DocumentFile[] children = file.listFiles();
            for (DocumentFile child : children) {
                if (child.isDirectory()) {
                    clearDirectory(child, null);
                }
                child.delete();
            }
        }
        file.delete();
    }
}
