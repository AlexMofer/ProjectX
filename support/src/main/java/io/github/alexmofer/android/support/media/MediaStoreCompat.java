package io.github.alexmofer.android.support.media;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import io.github.alexmofer.android.support.utils.FileUtils;
import io.github.alexmofer.android.support.utils.StreamUtils;

/**
 * MediaStore兼容器
 * Created by Alex on 2023/4/19.
 */
public class MediaStoreCompat {

    private MediaStoreCompat() {
        //no instance
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private static void insertMediaImageQ(ContentResolver resolver, File file,
                                          String name, String mimeType)
            throws Exception {
        final Uri collection = MediaStore.Images.Media
                .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        final ContentValues values = getContentValues(name, mimeType);
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        final Uri uri = resolver.insert(collection, values);
        if (uri == null) {
            return;
        }
        try (final FileInputStream input = new FileInputStream(file);
             final OutputStream output = resolver.openOutputStream(uri)) {
            StreamUtils.copy(input, output);
        }
        values.clear();
        values.put(MediaStore.Images.Media.SIZE, file.length());
        values.put(MediaStore.Audio.Media.IS_PENDING, 0);
        resolver.update(uri, values, null, null);
    }

    private static ContentValues getContentValues(String name, String mimeType) {
        final ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);
        if (mimeType != null) {
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
        }
        final long date = System.currentTimeMillis() / 1000;
        values.put(MediaStore.Images.Media.DATE_ADDED, date);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, date);
        return values;
    }

    private static String getAvailableName(ContentResolver resolver, String name,
                                           String baseName, String extension, int time) {
        boolean got = true;
        try (final Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media.DISPLAY_NAME + " = ?",
                new String[]{name}, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    got = false;
                }
            }
        }
        return got ? name : getAvailableName(resolver,
                baseName + " - " + time + "." + extension, baseName, extension,
                time + 1);
    }

    private static void insertMediaImageBase(ContentResolver resolver,
                                             File file, String name, String mimeType)
            throws Exception {
        final ContentValues values = getContentValues(
                getAvailableName(resolver, name, FileUtils.getNameWithoutExtension(name),
                        FileUtils.getExtension(name, true), 1), mimeType);
        final Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            return;
        }
        try (final FileInputStream input = new FileInputStream(file);
             final OutputStream output = resolver.openOutputStream(uri)) {
            StreamUtils.copy(input, output);
        }
        values.put(MediaStore.Images.Media.SIZE, file.length());
        resolver.update(uri, values, null, null);
        values.clear();
    }

    /**
     * 插入媒体图片
     *
     * @param resolver ContentResolver
     * @param file     图片文件
     * @param name     图片文件名（必须有拓展名）
     * @param mimeType 图片MIME类型
     * @throws Exception 插入失败错误
     */
    public static void insertMediaImage(ContentResolver resolver,
                                        File file, String name, String mimeType)
            throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            insertMediaImageQ(resolver, file, name, mimeType);
        } else {
            insertMediaImageBase(resolver, file, name, mimeType);
        }
    }
}
