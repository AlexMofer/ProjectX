/*
 * Copyright (C) 2025 AlexMofer
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * 位图工具
 * Created by Alex on 2024/4/16.
 */
public final class BitmapUtils {

    private BitmapUtils() {
        //no instance
    }

    /**
     * 写入缓存文件
     *
     * @param context Context
     * @param name    文件名
     * @param image   位图
     * @return 缓存文件
     */
    @Nullable
    public static File toCacheFile(Context context, @NonNull String name, @NonNull Bitmap image) {
        final File cache = ContextUtils.getExternalCacheDir(context, true);
        if (cache == null) {
            return null;
        }
        final File imageCache = new File(cache, name);
        boolean failure = false;
        try (final FileOutputStream output = new FileOutputStream(imageCache)) {
            if (!image.compress(Bitmap.CompressFormat.PNG, 100, output)) {
                failure = true;
            }
        } catch (Throwable t) {
            failure = true;
        }
        if (failure) {
            FileUtils.delete(imageCache);
            return null;
        }
        return imageCache;
    }

    /**
     * 判断是否为位图
     *
     * @param input 输入流
     * @return 为位图时返回 true
     */
    public static boolean isBitmap(@NonNull InputStream input) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        return options.outWidth > 0 && options.outHeight > 0;
    }

    /**
     * 判断是否为位图
     *
     * @param context Context
     * @param uri     Uri
     * @return 为位图时返回 true
     * @throws Exception Uri打开异常
     * @noinspection BooleanMethodIsAlwaysInverted
     */
    public static boolean isBitmap(Context context, @NonNull Uri uri) throws Exception {
        try (final InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new Exception("Cannot open uri.");
            }
            return isBitmap(input);
        }
    }

    /**
     * 判断沙盒文件是否为位图
     *
     * @param file 沙盒文件
     * @return 为位图时返回 true
     * @throws Exception 其他异常
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isBitmap(File file) throws Exception {
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        try (final InputStream input = Files.newInputStream(file.toPath())) {
            return isBitmap(input);
        }
    }

    private static int getExifOrientation(@NonNull InputStream input) throws Exception {
        final ExifInterface exif = new ExifInterface(input);
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
    }

    /**
     * 从流获取位图
     *
     * @param input         流
     * @param mutable       是否可修改
     * @param config        格式
     * @param premultiplied 是否预乘，未预乘的位图不可用于显示
     * @return 位图
     */
    @NonNull
    public static Bitmap fromStream(@NonNull InputStream input, int orientation,
                                    boolean mutable, Bitmap.Config config, boolean premultiplied) throws Exception {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = mutable;
        options.inPreferredConfig = config;
        options.inPremultiplied = premultiplied;
        final Bitmap original = BitmapFactory.decodeStream(input, null, options);
        if (original == null) {
            throw new Exception("Cannot get bitmap from stream.");
        }
        // 处理 EXIF ORIENTATION
        if (orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) {
            // 水平翻转
            final Matrix matrix = new Matrix();
            matrix.setScale(-1, 1);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            // 180度旋转
            final Matrix matrix = new Matrix();
            matrix.setRotate(180);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        if (orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL) {
            // 垂直翻转
            final Matrix matrix = new Matrix();
            matrix.setScale(1, -1);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        if (orientation == ExifInterface.ORIENTATION_TRANSPOSE) {
            // 垂直翻转再旋转90度
            final Matrix matrix = new Matrix();
            matrix.setScale(1, -1);
            matrix.postRotate(90);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            // 旋转90度
            final Matrix matrix = new Matrix();
            matrix.setRotate(90);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        if (orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
            // 旋转90度再垂直翻转
            final Matrix matrix = new Matrix();
            matrix.setRotate(90);
            matrix.postScale(1, -1);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            // 旋转270度
            final Matrix matrix = new Matrix();
            matrix.setRotate(270);
            final Bitmap handled = Bitmap.createBitmap(original, 0, 0,
                    original.getWidth(), original.getHeight(), matrix, true);
            original.recycle();
            return handled;
        }
        // 无需处理
        return original;
    }

    /**
     * 从 Uri 获取位图
     *
     * @param context       Context
     * @param uri           Uri
     * @param mutable       是否可修改
     * @param config        格式
     * @param premultiplied 是否预乘，未预乘的位图不可用于显示
     * @return 位图
     */
    @NonNull
    public static Bitmap fromUri(Context context, @NonNull Uri uri,
                                 boolean mutable, Bitmap.Config config, boolean premultiplied) throws Exception {
        if (!isBitmap(context, uri)) {
            throw new Exception("Not a bitmap uri.");
        }
        final int orientation;
        try (final InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from uri.");
            }
            orientation = getExifOrientation(input);
        }
        try (final InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from uri.");
            }
            return fromStream(input, orientation, mutable, config, premultiplied);
        }
    }


    /**
     * 从文件获取位图
     *
     * @param file          文件
     * @param mutable       是否可修改
     * @param config        格式
     * @param premultiplied 是否预乘，未预乘的位图不可用于显示
     * @return 位图
     */
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap fromFile(@NonNull File file,
                                  boolean mutable, Bitmap.Config config, boolean premultiplied) throws Exception {
        if (!isBitmap(file)) {
            throw new Exception("Not a bitmap file.");
        }
        final int orientation;
        try (final InputStream input = Files.newInputStream(file.toPath())) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from file.");
            }
            orientation = getExifOrientation(input);
        }
        try (final InputStream input = Files.newInputStream(file.toPath())) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from file.");
            }
            return fromStream(input, orientation, mutable, config, premultiplied);
        }
    }

    /**
     * 获取位图尺寸
     *
     * @param input       输入流
     * @param orientation 方向
     * @noinspection SuspiciousNameCombination
     */
    @NonNull
    public static Size getSize(@NonNull InputStream input, int orientation) throws Exception {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
        // 处理 EXIF ORIENTATION
        if (orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL) {
            // 水平翻转
            return new Size(options.outWidth, options.outHeight);
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            // 180度旋转
            return new Size(options.outWidth, options.outHeight);
        }
        if (orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL) {
            // 垂直翻转
            return new Size(options.outWidth, options.outHeight);
        }
        if (orientation == ExifInterface.ORIENTATION_TRANSPOSE) {
            // 垂直翻转再旋转90度
            return new Size(options.outHeight, options.outWidth);
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            // 旋转90度
            return new Size(options.outHeight, options.outWidth);
        }
        if (orientation == ExifInterface.ORIENTATION_TRANSVERSE) {
            // 旋转90度再垂直翻转
            return new Size(options.outHeight, options.outWidth);
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            // 旋转270度
            return new Size(options.outHeight, options.outWidth);
        }
        // 无需处理
        return new Size(options.outWidth, options.outHeight);
    }

    /**
     * 获取位图尺寸
     *
     * @param context Context
     * @param uri     Uri
     */
    @NonNull
    public static Size getSize(Context context, @NonNull Uri uri) throws Exception {
        if (!isBitmap(context, uri)) {
            throw new Exception("Not a bitmap uri.");
        }
        final int orientation;
        try (final InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from uri.");
            }
            orientation = getExifOrientation(input);
        }
        try (final InputStream input = context.getContentResolver().openInputStream(uri)) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from uri.");
            }
            return getSize(input, orientation);
        }
    }

    /**
     * 获取位图尺寸
     *
     * @param file 文件
     */
    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Size getSize(@NonNull File file) throws Exception {
        if (!isBitmap(file)) {
            throw new Exception("Not a bitmap uri.");
        }
        final int orientation;
        try (final InputStream input = Files.newInputStream(file.toPath())) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from uri.");
            }
            orientation = getExifOrientation(input);
        }
        try (final InputStream input = Files.newInputStream(file.toPath())) {
            if (input == null) {
                throw new Exception("Cannot get bitmap from uri.");
            }
            return getSize(input, orientation);
        }
    }
}
