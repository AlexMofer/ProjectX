/*
 * Copyright (C) 2015 AlexMofer
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

package am.project.support.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.util.List;

import am.project.support.compat.AMStorageManagerCompat;

/**
 * Uri工具类
 * Created by Alex on 2017/10/23.
 */
@SuppressWarnings("unused")
public class UriUtils {
    private static final String EXTERNAL_STORAGE_PROVIDER_AUTHORITY =
            "com.android.externalstorage.documents";
    private static final String EXTERNAL_STORAGE_PROVIDER_PREFIX = "primary";
    private static final String DOWNLOADS_PROVIDER_AUTHORITY =
            "com.android.providers.downloads.documents";
    private static final String DOWNLOADS_PROVIDER_PREFIX = "raw";
    private static final String MEDIA_PROVIDER_AUTHORITY =
            "com.android.providers.media.documents";
    private static final String MEDIA_PROVIDER_PREFIX_IMAGE = "image";
    private static final String MEDIA_PROVIDER_PREFIX_VIDEO = "video";
    private static final String MEDIA_PROVIDER_PREFIX_AUDIO = "audio";
    private static final String REGEX = "^[-\\+]?[\\d]*$";

    /**
     * 通过Uri获取文件路径
     *
     * @param context Context
     * @param uri     Uri
     * @return 文件路径
     */
    public static String getPath(Context context, Uri uri) {
        if (uri == null)
            return null;
        final String scheme = uri.getScheme();
        // File
        if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(scheme)) {
            return uri.getPath();
        }
        // ContentProvider
        if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(scheme)) {
            if (Build.VERSION.SDK_INT >= 19) {
                final String authority = uri.getAuthority();
                // DocumentProvider
                if (DocumentsContract.isDocumentUri(context, uri)) {
                    final String documentId = DocumentsContract.getDocumentId(uri);
                    final String[] split = documentId.split(":");
                    if (EXTERNAL_STORAGE_PROVIDER_AUTHORITY.equals(authority)) {
                        // ExternalStorageProvider
                        if (split.length == 2 &&
                                EXTERNAL_STORAGE_PROVIDER_PREFIX.equalsIgnoreCase(split[0])) {
                            List<AMStorageManagerCompat.StorageVolumeImpl> storageVolumes =
                                    AMStorageManagerCompat.getEmulatedStorageVolumes(
                                            (StorageManager) context.getSystemService(
                                                    Context.STORAGE_SERVICE));
                            final String path = split[1];
                            if (storageVolumes.size() > 1) {
                                for (AMStorageManagerCompat.StorageVolumeImpl storage :
                                        storageVolumes) {
                                    if (path.startsWith(storage.getPath()))
                                        return path;
                                }
                                String filePath;
                                for (AMStorageManagerCompat.StorageVolumeImpl storage :
                                        storageVolumes) {
                                    filePath = storage.getPath() + "/" + path;
                                    if (new File(filePath).exists()) {
                                        return filePath;
                                    }
                                }
                            }
                            return Environment.getExternalStorageDirectory().getPath() + "/" +
                                    split[1];
                        }
                    }
                    if (DOWNLOADS_PROVIDER_AUTHORITY.equals(authority)) {
                        // DownloadsProvider
                        if (documentId.matches(REGEX)) {
                            // 已写入数据库的媒体文件
                            return queryContentProvider(context, ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"),
                                    Long.valueOf(documentId)), null, null);
                        } else {
                            // 不写入系统数据库的非媒体文件
                            if (split.length == 2 &&
                                    DOWNLOADS_PROVIDER_PREFIX.equalsIgnoreCase(split[0])) {
                                return split[1];
                            }
                        }
                    }
                    if (MEDIA_PROVIDER_AUTHORITY.equals(authority)) {
                        // MediaProvider
                        if (split.length == 2 && split[1].matches(REGEX)) {
                            final String selection = "_id=?";
                            final String[] selectionArgs = new String[]{split[1]};
                            switch (split[0]) {
                                case MEDIA_PROVIDER_PREFIX_IMAGE:
                                    return queryContentProvider(context,
                                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            selection, selectionArgs);
                                case MEDIA_PROVIDER_PREFIX_VIDEO:
                                    return queryContentProvider(context,
                                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                            selection, selectionArgs);
                                case MEDIA_PROVIDER_PREFIX_AUDIO:
                                    return queryContentProvider(context,
                                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                            selection, selectionArgs);
                            }
                        }
                    }
                }
            }
            // 常规的系统媒体文件库查询
            final String result = queryContentProvider(context, uri, null, null);
            if (result != null)
                return result;
            // 非常规查询
            final String dir = Environment.getExternalStorageDirectory().getPath();
            if (uri.getPath().contains(dir)) {
                final String[] parts = uri.getPath().split(dir);
                return dir + parts[parts.length - 1];
            }
            List<AMStorageManagerCompat.StorageVolumeImpl> storages =
                    AMStorageManagerCompat.getEmulatedStorageVolumes(
                            (StorageManager) context.getSystemService(Context.STORAGE_SERVICE));
            if (storages != null && !storages.isEmpty()) {
                for (AMStorageManagerCompat.StorageVolumeImpl storage : storages) {
                    final String storagePath = storage.getPath();
                    if (uri.getPath().contains(storagePath)) {
                        final String[] parts = uri.getPath().split(storagePath);
                        return storagePath + parts[parts.length - 1];
                    }
                }
            }
            final String path = uri.getPath();
            String filePath = path;
            if (new File(filePath).exists()) {
                return filePath;
            }
            filePath = dir + "/" + path;
            if (new File(filePath).exists()) {
                return filePath;
            }
            if (storages != null && !storages.isEmpty()) {
                for (AMStorageManagerCompat.StorageVolumeImpl storage : storages) {
                    filePath = storage.getPath() + "/" + path;
                    if (new File(filePath).exists()) {
                        return filePath;
                    }
                }
            }
            return null;
        }
        return null;
    }

    /**
     * 从系统ContentProvider查询
     *
     * @param context       Context
     * @param uri           查询Uri
     * @param selection     筛选条件
     * @param selectionArgs 筛选条件参数
     * @return 数据库“_data”列的数据，一般来说就是文件路径
     */
    private static String queryContentProvider(Context context, Uri uri, String selection,
                                               String[] selectionArgs) {
        final String column = "_data";
        final String[] projection = {column};
        final Cursor cursor = context.getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
        String result = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                try {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(column));
                } catch (IllegalArgumentException e) {
                    result = null;
                }
            }
            cursor.close();
        }
        return result;
    }
}