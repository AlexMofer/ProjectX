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

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Uri 工具
 * Created by Alex on 2022/7/22.
 */
public class UriUtils {

    private static final int FLAG_VIRTUAL_DOCUMENT = 1 << 9;

    private UriUtils() {
        //no instance
    }

    /**
     * @noinspection SameParameterValue
     */
    @RequiresApi(19)
    private static long queryForLong(ContentResolver resolver, Uri uri, String column,
                                     long defaultValue) {
        try (final Cursor cursor = resolver.query(uri, new String[]{column},
                null, null, null)) {
            if (cursor != null) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    return cursor.getLong(0);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return defaultValue;
    }

    /**
     * @noinspection SameParameterValue
     */
    @RequiresApi(19)
    private static int queryForInt(ContentResolver resolver, Uri uri, String column,
                                   int defaultValue) {
        try (final Cursor cursor = resolver.query(uri, new String[]{column},
                null, null, null)) {
            if (cursor != null) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    return cursor.getInt(0);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return defaultValue;
    }

    /**
     * @noinspection SameParameterValue
     */
    @RequiresApi(19)
    @Nullable
    private static String queryForString(ContentResolver resolver, Uri uri, String column,
                                         @Nullable String defaultValue) {
        try (final Cursor cursor = resolver.query(uri, new String[]{column},
                null, null, null)) {
            if (cursor != null) {
                if (cursor.moveToFirst() && !cursor.isNull(0)) {
                    return cursor.getString(0);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return defaultValue;
    }

    @RequiresApi(19)
    @Nullable
    private static String getRawType(ContentResolver resolver, Uri uri) {
        return queryForString(resolver, uri, DocumentsContract.Document.COLUMN_MIME_TYPE, null);
    }

    /**
     * @noinspection BooleanMethodIsAlwaysInverted
     */
    @RequiresApi(21)
    private static boolean isTreeUri(@NonNull Uri uri) {
        try {
            DocumentsContract.getTreeDocumentId(uri);
            DocumentsContract.getDocumentId(uri);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 判断是否为虚拟文档
     *
     * @return 为虚拟文档时返回true
     * @see android.provider.DocumentsContract.Document#FLAG_VIRTUAL_DOCUMENT
     */
    @RequiresApi(19)
    public static boolean isVirtual(Context context, Uri uri) {
        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false;
        }
        return (queryForLong(context.getContentResolver(), uri,
                DocumentsContract.Document.COLUMN_FLAGS, 0)
                & FLAG_VIRTUAL_DOCUMENT) != 0;
    }

    /**
     * 获取名称
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 名称
     */
    @RequiresApi(19)
    @Nullable
    public static String getName(ContentResolver resolver, Uri uri) {
        return queryForString(resolver, uri, DocumentsContract.Document.COLUMN_DISPLAY_NAME, null);
    }

    /**
     * 获取名称
     *
     * @param context Context
     * @param uri     链接
     * @return 名称
     */
    @RequiresApi(19)
    @Nullable
    public static String getName(Context context, Uri uri) {
        return getName(context.getContentResolver(), uri);
    }

    /**
     * 获取名称
     *
     * @param uri 链接
     * @return 名称
     */
    @Nullable
    public static String getNameByPath(Uri uri) {
        final List<String> segments = uri.getPathSegments();
        return (segments == null || segments.isEmpty()) ? null : segments.get(segments.size() - 1);
    }

    /**
     * 获取 MIME 类型
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return MIME 类型
     */
    @RequiresApi(19)
    @Nullable
    public static String getType(ContentResolver resolver, Uri uri) {
        final String rawType = getRawType(resolver, uri);
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(rawType)) {
            return null;
        } else {
            return rawType;
        }
    }

    /**
     * 获取 MIME 类型
     *
     * @param context Context
     * @param uri     链接
     * @return MIME 类型
     */
    @RequiresApi(19)
    @Nullable
    public static String getType(Context context, Uri uri) {
        return getType(context.getContentResolver(), uri);
    }

    /**
     * 判断是否为文件夹
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 为文件夹时返回true
     */
    @RequiresApi(19)
    public static boolean isDirectory(ContentResolver resolver, Uri uri) {
        return DocumentsContract.Document.MIME_TYPE_DIR.equals(getRawType(resolver, uri));
    }

    /**
     * 判断是否为文件夹
     *
     * @param context Context
     * @param uri     链接
     * @return 为文件夹时返回true
     */
    @RequiresApi(19)
    public static boolean isDirectory(Context context, Uri uri) {
        return isDirectory(context.getContentResolver(), uri);
    }

    /**
     * 判断是否为文件
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 为文件时返回true
     */
    @RequiresApi(19)
    public static boolean isFile(ContentResolver resolver, Uri uri) {
        final String type = getRawType(resolver, uri);
        return !DocumentsContract.Document.MIME_TYPE_DIR.equals(type) && !TextUtils.isEmpty(type);
    }

    /**
     * 判断是否为文件
     *
     * @param context Context
     * @param uri     链接
     * @return 为文件时返回true
     */
    @RequiresApi(19)
    public static boolean isFile(Context context, Uri uri) {
        return isFile(context.getContentResolver(), uri);
    }

    /**
     * 获取最后编辑时间
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 最后编辑时间
     */
    @RequiresApi(19)
    public static long lastModified(ContentResolver resolver, Uri uri) {
        return queryForLong(resolver, uri, DocumentsContract.Document.COLUMN_LAST_MODIFIED, 0);
    }

    /**
     * 获取最后编辑时间
     *
     * @param context Context
     * @param uri     链接
     * @return 最后编辑时间
     */
    @RequiresApi(19)
    public static long lastModified(Context context, Uri uri) {
        return lastModified(context.getContentResolver(), uri);
    }

    /**
     * 获取文件长度
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 文件长度
     */
    @RequiresApi(19)
    public static long length(ContentResolver resolver, Uri uri) {
        return queryForLong(resolver, uri, DocumentsContract.Document.COLUMN_SIZE, 0);
    }

    /**
     * 获取文件长度
     *
     * @param context Context
     * @param uri     链接
     * @return 文件长度
     */
    @RequiresApi(19)
    public static long length(Context context, Uri uri) {
        return length(context.getContentResolver(), uri);
    }

    /**
     * 判断是否可读
     *
     * @param context Context
     * @param uri     链接
     * @return 可读时返回true
     */
    @RequiresApi(19)
    public static boolean canRead(Context context, Uri uri) {
        // Ignore if grant doesn't allow read
        if (context.checkCallingOrSelfUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        // Ignore documents without MIME
        return !TextUtils.isEmpty(getRawType(context.getContentResolver(), uri));
    }

    /**
     * 判断是否可写
     *
     * @param context Context
     * @param uri     链接
     * @return 可写时返回true
     */
    @RequiresApi(19)
    public static boolean canWrite(Context context, Uri uri) {
        // Ignore if grant doesn't allow write
        if (context.checkCallingOrSelfUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        final ContentResolver resolver = context.getContentResolver();
        final String type = getRawType(resolver, uri);
        final int flags = queryForInt(resolver, uri, DocumentsContract.Document.COLUMN_FLAGS, 0);

        // Ignore documents without MIME
        if (TextUtils.isEmpty(type)) {
            return false;
        }

        // Deletable documents considered writable
        if ((flags & DocumentsContract.Document.FLAG_SUPPORTS_DELETE) != 0) {
            return true;
        }

        // Writable normal files considered writable
        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(type)
                && (flags & DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE) != 0) {
            // Directories that allow create considered writable
            return true;
        }
        return !TextUtils.isEmpty(type)
                && (flags & DocumentsContract.Document.FLAG_SUPPORTS_WRITE) != 0;
    }

    /**
     * 判断是否存在
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 存在时返回true
     */
    @RequiresApi(19)
    public static boolean exists(ContentResolver resolver, Uri uri) {
        try (final Cursor cursor = resolver.query(uri, new String[]{
                        DocumentsContract.Document.COLUMN_DOCUMENT_ID},
                null, null, null)) {
            return cursor != null && cursor.getCount() > 0;
        } catch (Throwable t) {
            // ignore
        }
        return false;
    }

    /**
     * 判断是否存在
     *
     * @param context Context
     * @param uri     链接
     * @return 存在时返回true
     */
    @RequiresApi(19)
    public static boolean exists(Context context, Uri uri) {
        return exists(context.getContentResolver(), uri);
    }

    /**
     * 删除
     *
     * @param resolver ContentResolver
     * @param uri      链接
     * @return 删除成功时返回true
     */
    @RequiresApi(19)
    public static boolean delete(ContentResolver resolver, Uri uri) {
        try {
            return DocumentsContract.deleteDocument(resolver, uri);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 删除
     *
     * @param context Context
     * @param uri     链接
     * @return 删除成功时返回true
     */
    @RequiresApi(19)
    public static boolean delete(Context context, Uri uri) {
        return delete(context.getContentResolver(), uri);
    }

    /**
     * 创建文件
     *
     * @param resolver    ContentResolver
     * @param directory   文件夹
     * @param mimeType    MIME
     * @param displayName 名称
     * @return 链接，失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri createFile(ContentResolver resolver, @NonNull Uri directory,
                                 @NonNull String mimeType, @NonNull String displayName) {
        if (!isTreeUri(directory)) {
            return null;
        }
        try {
            return DocumentsContract.createDocument(resolver,
                    directory, mimeType, displayName);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 创建文件
     *
     * @param context     Context
     * @param directory   文件夹
     * @param mimeType    MIME
     * @param displayName 名称
     * @return 链接，失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri createFile(Context context, @NonNull Uri directory,
                                 @NonNull String mimeType, @NonNull String displayName) {
        return createFile(context.getContentResolver(), directory, mimeType, displayName);
    }

    /**
     * 创建文件夹
     *
     * @param resolver    ContentResolver
     * @param directory   文件夹
     * @param displayName 名称
     * @return 链接，失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri createDirectory(ContentResolver resolver, @NonNull Uri directory,
                                      @NonNull String displayName) {
        return createFile(resolver, directory, DocumentsContract.Document.MIME_TYPE_DIR,
                displayName);
    }

    /**
     * 创建文件夹
     *
     * @param context     Context
     * @param directory   文件夹
     * @param displayName 名称
     * @return 链接，失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri createDirectory(Context context, @NonNull Uri directory,
                                      @NonNull String displayName) {
        return createDirectory(context.getContentResolver(), directory, displayName);
    }

    /**
     * 重命名
     *
     * @param resolver    ContentResolver
     * @param uri         链接
     * @param displayName 名称
     * @return 链接，失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri rename(ContentResolver resolver, @NonNull Uri uri,
                             @NonNull String displayName) {
        if (!isTreeUri(uri)) {
            return null;
        }
        try {
            return DocumentsContract.renameDocument(resolver, uri, displayName);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 重命名
     *
     * @param context     Context
     * @param uri         链接
     * @param displayName 名称
     * @return 链接，失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri rename(Context context, @NonNull Uri uri, @NonNull String displayName) {
        return rename(context.getContentResolver(), uri, displayName);
    }

    /**
     * 列出子项
     *
     * @param resolver  ContentResolver
     * @param directory 文件夹
     * @return 子项
     * @throws Exception 错误
     */
    @RequiresApi(21)
    @NonNull
    public static List<Uri> listChildren(ContentResolver resolver, @NonNull Uri directory)
            throws Exception {
        if (!isTreeUri(directory)) {
            throw new IllegalArgumentException("Not a tree uri");
        }
        final ArrayList<Uri> results = new ArrayList<>();
        try (final Cursor cursor = resolver.query(
                DocumentsContract.buildChildDocumentsUriUsingTree(
                        directory, DocumentsContract.getDocumentId(directory)),
                new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID},
                null, null, null)) {
            if (cursor == null) {
                throw new Exception("Cannot list children.");
            }
            while (cursor.moveToNext()) {
                final String documentId = cursor.getString(0);
                final Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(directory,
                        documentId);
                results.add(documentUri);
            }
        }
        return results;
    }

    /**
     * 列出子项
     *
     * @param context   Context
     * @param directory 文件夹
     * @return 子项
     * @throws Exception 错误
     */
    @RequiresApi(21)
    @NonNull
    public static List<Uri> listChildren(Context context, @NonNull Uri directory)
            throws Exception {
        return listChildren(context.getContentResolver(), directory);
    }

    /**
     * 查找子项
     *
     * @param resolver  ContentResolver
     * @param directory 文件夹
     * @param name      名称
     * @return 子项，找不到或者失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri findChild(ContentResolver resolver,
                                @NonNull Uri directory, @NonNull String name) {
        if (!isTreeUri(directory)) {
            return null;
        }
        try (final Cursor cursor = resolver.query(
                DocumentsContract.buildChildDocumentsUriUsingTree(
                        directory, DocumentsContract.getDocumentId(directory)),
                new String[]{DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                        DocumentsContract.Document.COLUMN_DISPLAY_NAME},
                null, null, null)) {
            if (cursor == null) {
                return null;
            }
            while (cursor.moveToNext()) {
                final String documentId = cursor.getString(0);
                final String displayName = cursor.getString(1);
                if (TextUtils.equals(displayName, name)) {
                    return DocumentsContract.buildDocumentUriUsingTree(directory, documentId);
                }
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    /**
     * 查找子项
     *
     * @param context   Context
     * @param directory 文件夹
     * @param name      名称
     * @return 子项，找不到或者失败时返回null
     */
    @RequiresApi(21)
    @Nullable
    public static Uri findChild(Context context,
                                @NonNull Uri directory, @NonNull String name) {
        return findChild(context.getContentResolver(), directory, name);
    }


    /**
     * 转为 Tree Uri
     * 仅通过 {@link Intent#ACTION_OPEN_DOCUMENT_TREE} 获取的 Uri 支持转换
     *
     * @param context Context
     * @param uri     链接
     * @return Tree Uri
     */
    @RequiresApi(21)
    @NonNull
    public static Uri toTreeUri(@NonNull Context context, @NonNull Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!DocumentsContract.isTreeUri(uri)) {
                throw new IllegalArgumentException("Not a tree uri");
            }
        } else {
            final List<String> paths = uri.getPathSegments();
            if (paths.size() < 2 || !"tree".equals(paths.get(0))) {
                throw new IllegalArgumentException("Not a tree uri");
            }
        }
        if (DocumentsContract.isDocumentUri(context, uri)) {
            return DocumentsContract.buildDocumentUriUsingTree(uri,
                    DocumentsContract.getDocumentId(uri));
        } else {
            return DocumentsContract.buildDocumentUriUsingTree(uri,
                    DocumentsContract.getTreeDocumentId(uri));
        }
    }

    /**
     * 复制
     *
     * @param context Context
     * @param source  源文件
     * @param target  目标文件
     * @return 复制成功时返回true
     */
    public static boolean copy(Context context, Uri source, Uri target) {
        try (final InputStream input = context.getContentResolver().openInputStream(source);
             final OutputStream output = context.getContentResolver().openOutputStream(target)) {
            if (input == null || output == null) {
                return false;
            }
            StreamUtils.copy(input, output);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 复制
     *
     * @param context Context
     * @param source  源文件
     * @param target  目标文件
     * @return 复制成功时返回true
     */
    public static boolean copy(Context context, Uri source, File target) {
        try (final InputStream input = context.getContentResolver().openInputStream(source);
             final FileOutputStream output = new FileOutputStream(target)) {
            if (input == null) {
                return false;
            }
            StreamUtils.copy(input, output);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 复制
     *
     * @param context Context
     * @param source  源文件
     * @param target  目标文件
     * @return 复制成功时返回true
     */
    public static boolean copy(Context context, File source, Uri target) {
        try (final FileInputStream input = new FileInputStream(source);
             final OutputStream output = context.getContentResolver().openOutputStream(target)) {
            if (output == null) {
                return false;
            }
            StreamUtils.copy(input, output);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 写入字符串内容
     *
     * @param file    文件
     * @param content 字符串
     * @param cs      字符集
     * @return 是否成功
     */
    public static boolean writeString(ContentResolver resolver, Uri file,
                                      @Nullable String content, @NonNull Charset cs) {
        if (resolver == null || file == null) {
            return false;
        }
        if (content == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return delete(resolver, file);
            } else {
                content = "";
            }
        }
        try (final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(resolver.openOutputStream(file), cs))) {
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
    public static boolean writeString(ContentResolver resolver, Uri file,
                                      @Nullable String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return writeString(resolver, file, content, StandardCharsets.UTF_8);
        } else {
            //noinspection CharsetObjectCanBeUsed
            return writeString(resolver, file, content, Charset.forName("UTF-8"));
        }
    }

    /**
     * 读取字符串内容
     *
     * @param file 文件
     * @param cs   字符集
     * @return 字符串
     */
    @Nullable
    public static String readString(ContentResolver resolver, Uri file, @NonNull Charset cs) {
        if (resolver == null || file == null) {
            return null;
        }
        try (final BufferedReader reader = new BufferedReader(
                new InputStreamReader(resolver.openInputStream(file), cs))) {
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
    @Nullable
    public static String readString(ContentResolver resolver, Uri file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return readString(resolver, file, StandardCharsets.UTF_8);
        } else {
            //noinspection CharsetObjectCanBeUsed
            return readString(resolver, file, Charset.forName("UTF-8"));
        }
    }
}
