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
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.function.Consumer;

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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBitmap(File file) throws Exception {
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        try (final InputStream input = StreamUtils.newInputStream(file)) {
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
    public static Bitmap fromFile(@NonNull File file,
                                  boolean mutable, Bitmap.Config config, boolean premultiplied) throws Exception {
        if (!isBitmap(file)) {
            throw new Exception("Not a bitmap file.");
        }
        final int orientation;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            orientation = getExifOrientation(input);
        }
        try (final InputStream input = StreamUtils.newInputStream(file)) {
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
    public static Size getSize(@NonNull File file) throws Exception {
        if (!isBitmap(file)) {
            throw new Exception("Not a bitmap uri.");
        }
        final int orientation;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            orientation = getExifOrientation(input);
        }
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            return getSize(input, orientation);
        }
    }

    /**
     * 计算最优采样率 (inSampleSize)
     */
    private static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (reqWidth > 0 && reqHeight > 0) {
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // 保证采样后的尺寸不低于目标要求的宽高
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }
        }
        return inSampleSize;
    }

    /**
     * 根据 Exif 方向属性旋转/翻转 Bitmap
     */
    @NonNull
    private static Bitmap rotateBitmapIfNeeded(@NonNull Bitmap bitmap, int orientation)
            throws OutOfMemoryError {
        final Matrix matrix = new Matrix();

        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap; // 不需要旋转
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (rotatedBitmap != bitmap) {
            bitmap.recycle(); // 释放原图内存
        }
        return rotatedBitmap;
    }

    /**
     * 从文件创建图片
     *
     * @param file            图片文件
     * @param maxWidth        最大宽度，小于等于0表示不限制
     * @param maxHeight       最大高度，小于等于0表示不限制
     * @param optionsConsumer 选项配置器
     * @return 图片
     * @throws Exception 创建图片时发生的异常
     */
    @NonNull
    public static Bitmap fromFile(@NonNull File file,
                                  int maxWidth, int maxHeight,
                                  @Nullable Consumer<BitmapFactory.Options> optionsConsumer) throws Exception {
        final int srcWidth;
        final int srcHeight;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            srcWidth = options.outWidth;
            srcHeight = options.outHeight;
        }
        if (srcWidth <= 0 || srcHeight <= 0) {
            throw new Exception("Not a bitmap file.");
        }
        final int orientation;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            final ExifInterface exif = new ExifInterface(input);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        }
        // 如果设置了旋转（90度或270度），测量尺寸的宽高需要互换来计算缩放比
        final boolean isRotated = (orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
                orientation == ExifInterface.ORIENTATION_ROTATE_270 ||
                orientation == ExifInterface.ORIENTATION_TRANSPOSE ||
                orientation == ExifInterface.ORIENTATION_TRANSVERSE);
        final int targetWidth = isRotated ? srcHeight : srcWidth;
        final int targetHeight = isRotated ? srcWidth : srcHeight;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        if (optionsConsumer != null) {
            optionsConsumer.accept(options);
        }
        options.inSampleSize = calculateInSampleSize(targetWidth, targetHeight, maxWidth, maxHeight);
        // 3. 第二阶段：正式解码加载 Bitmap
        Bitmap bitmap;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            bitmap = BitmapFactory.decodeStream(input, null, options);
        }
        if (bitmap == null) {
            throw new Exception("Failed to decode bitmap from file.");
        }
        // 4. 根据 Exif orientation 矩阵矫正图片角度
        bitmap = rotateBitmapIfNeeded(bitmap, orientation);
        // 5. 如果要求了精确的 maxWidth/maxHeight，做二次精确缩放
        if (maxWidth > 0 && maxHeight > 0) {
            int currentWidth = bitmap.getWidth();
            int currentHeight = bitmap.getHeight();

            if (currentWidth > maxWidth || currentHeight > maxHeight) {
                float widthRatio = (float) maxWidth / currentWidth;
                float heightRatio = (float) maxHeight / currentHeight;
                float ratio = Math.min(widthRatio, heightRatio);

                int finalWidth = Math.round(currentWidth * ratio);
                int finalHeight = Math.round(currentHeight * ratio);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
                if (scaledBitmap != bitmap) {
                    bitmap.recycle(); // 释放原图内存
                    bitmap = scaledBitmap;
                }
            }
        }
        return bitmap;
    }

    /**
     * 从文件创建图片
     *
     * @param file      图片文件
     * @param maxWidth  最大宽度，小于等于0表示不限制
     * @param maxHeight 最大高度，小于等于0表示不限制
     * @return 图片
     * @throws Exception 创建图片时发生的异常
     */
    @NonNull
    public static Bitmap fromFile(@NonNull File file,
                                  int maxWidth, int maxHeight) throws Exception {
        return fromFile(file, maxWidth, maxHeight, null);
    }

    /**
     * 将可视坐标系下的裁剪框映射回文件的原始物理像素坐标系
     */
    private static void mapRectToPhysicalCoordinates(@NonNull Rect output,
                                                     int left, int top, int right, int bottom,
                                                     int srcWidth, int srcHeight, int orientation) {
        int l = left;
        int t = top;
        int r = right;
        int b = bottom;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                // 顺时针旋转90度：物理的X来自可视的Y，物理的Y来自可视的 (srcWidth - X)
                l = top;
                t = srcWidth - right;
                r = bottom;
                b = srcWidth - left;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                l = srcWidth - right;
                t = srcHeight - bottom;
                r = srcWidth - left;
                b = srcHeight - top;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                l = srcHeight - bottom;
                t = left;
                r = srcHeight - top;
                b = right;
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                l = srcWidth - right;
                r = srcWidth - left;
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                t = srcHeight - bottom;
                b = srcHeight - top;
                break;
            default:
                break;
        }
        output.set(Math.max(0, l), Math.max(0, t), Math.min(srcWidth, r), Math.min(srcHeight, b));
    }

    /**
     * 裁剪图片
     *
     * @param file       图片文件
     * @param cropLeft   裁剪框左边界，取值范围 [0, 1]
     * @param cropTop    裁剪框上边界，取值范围 [0, 1]
     * @param cropRight  裁剪框右边界，取值范围 [0, 1]
     * @param cropBottom 裁剪框下边界，取值范围 [0, 1]
     * @return 裁剪后的图片
     * @throws Exception 裁剪图片时发生的异常
     */
    @NonNull
    public static Bitmap crop(@NonNull File file,
                              float cropLeft, float cropTop,
                              float cropRight, float cropBottom) throws Exception {
        final int srcWidth;
        final int srcHeight;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            srcWidth = options.outWidth;
            srcHeight = options.outHeight;
        }
        if (srcWidth <= 0 || srcHeight <= 0) {
            throw new Exception("Not a bitmap file.");
        }
        int left = Math.round(srcWidth * cropLeft);
        int top = Math.round(srcHeight * cropTop);
        int right = Math.round(srcWidth * cropRight);
        int bottom = Math.round(srcHeight * cropBottom);
        left = Math.min(srcWidth, Math.max(0, left));
        top = Math.min(srcHeight, Math.max(0, top));
        right = Math.min(srcWidth, Math.max(0, right));
        bottom = Math.min(srcHeight, Math.max(0, bottom));
        if (left > right) {
            int temp = left;
            left = right;
            right = temp;
        }
        if (top > bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }
        final int orientation;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            final ExifInterface exif = new ExifInterface(input);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        }

        // 1. 将基于“可视防转方向”的裁剪框坐标，映射回“物理文件原始像素”坐标系
        final Rect cropRect = new Rect();
        mapRectToPhysicalCoordinates(cropRect,
                left, top, right, bottom, srcWidth, srcHeight, orientation);

        // 校验裁剪区域有效性（避免宽高为0导致异常）
        if (cropRect.width() <= 0 || cropRect.height() <= 0) {
            throw new Exception("Invalid crop dimensions.");
        }

        // 2. 使用 BitmapRegionDecoder 仅解码指定的矩形区域（极其省内存）
        final Bitmap croppedBitmap;
        try (final InputStream input = StreamUtils.newInputStream(file)) {
            final BitmapRegionDecoder decoder;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                decoder = BitmapRegionDecoder.newInstance(input);
            } else {
                // 兼容低版本
                //noinspection deprecation
                decoder = BitmapRegionDecoder.newInstance(input, false);
            }
            if (decoder == null) {
                throw new Exception("Failed to create BitmapRegionDecoder.");
            }
            croppedBitmap = decoder.decodeRegion(cropRect, new BitmapFactory.Options());
            decoder.recycle(); // 释放解码器资源
        }

        if (croppedBitmap == null) {
            throw new Exception("Failed to decode image region.");
        }

        // 3. 将裁剪出的 Bitmap 矫正至正确的方向
        return rotateBitmapIfNeeded(croppedBitmap, orientation);
    }

    /**
     * 缩小图片
     *
     * @param src       图片
     * @param maxWidth  最大宽度，小于等于0表示不限制
     * @param maxHeight 最大高度，小于等于0表示不限制
     * @return 缩放后的图片，没有进行缩放则返回原图
     */
    @NonNull
    public static Bitmap shrink(@NonNull Bitmap src, int maxWidth, int maxHeight)
            throws OutOfMemoryError {
        final int width = src.getWidth();
        final int height = src.getHeight();

        // 如果没有限制，直接返回原图
        if (maxWidth <= 0 && maxHeight <= 0) {
            return src;
        }

        // 计算目标宽高
        float targetWidth = maxWidth > 0 ? maxWidth : width;
        float targetHeight = maxHeight > 0 ? maxHeight : height;

        // 如果原图尺寸已经在限制范围内，直接返回原图
        if (width <= targetWidth && height <= targetHeight) {
            return src;
        }

        // 计算缩放比例，保持纵横比
        float scale;
        if (maxWidth > 0 && maxHeight > 0) {
            // 两个都有限制，取较小的比例以确保不超出任一限制
            float scaleX = (float) maxWidth / width;
            float scaleY = (float) maxHeight / height;
            scale = Math.min(scaleX, scaleY);
        } else if (maxWidth > 0) {
            scale = (float) maxWidth / width;
        } else {
            scale = (float) maxHeight / height;
        }

        // 这里主要是为了缩小。如果 scale >= 1，则不需要缩放
        if (scale >= 1.0f) {
            return src;
        }

        final int newWidth = Math.round(width * scale);
        final int newHeight = Math.round(height * scale);

        // 确保至少为1像素
        if (newWidth <= 0 || newHeight <= 0) {
            return src;
        }

        return Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
    }

    /**
     * 放大图片
     *
     * @param src       图片
     * @param minWidth  最小宽度，小于等于0表示不限制
     * @param minHeight 最小高度，小于等于0表示不限制
     * @return 放大后的图片，没有进行放大则返回原图
     */
    @NonNull
    public static Bitmap enlarge(@NonNull Bitmap src, int minWidth, int minHeight)
            throws OutOfMemoryError {
        if (minWidth <= 0 || minHeight <= 0) {
            return src;
        }

        final int srcWidth = src.getWidth();
        final int srcHeight = src.getHeight();

        // 如果原图宽高均已满足或大于要求，直接返回原图，不进行额外内存开辟
        if (srcWidth >= minWidth && srcHeight >= minHeight) {
            return src;
        }

        // 计算宽和高的放大比例，取较大者以确保宽高均能满足最小要求（保持等比放大）
        final float scaleX = (float) minWidth / srcWidth;
        final float scaleY = (float) minHeight / srcHeight;
        final float scale = Math.max(scaleX, scaleY);

        // 计算最终目标宽高（向上取整，防止浮点数精度丢失导致差 1 像素）
        final int targetWidth = (int) Math.ceil(srcWidth * scale);
        final int targetHeight = (int) Math.ceil(srcHeight * scale);

        // 创建新的目标 Bitmap
        final Bitmap.Config config = src.getConfig() != null ? src.getConfig() : Bitmap.Config.ARGB_8888;
        final Bitmap dst = Bitmap.createBitmap(targetWidth, targetHeight, config);

        // 使用带双线性插值的 Canvas 进行绘制，保证放大的平滑度与画质
        final Canvas canvas = new Canvas(dst);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(src, null, new android.graphics.Rect(0, 0, targetWidth, targetHeight), paint);

        return dst;
    }

    /**
     * 给图片添加描边效果
     *
     * @param image       图片
     * @param strokeSize  描边大小，单位像素。如果小于等于0，则不添加描边
     * @param strokeColor 描边颜色
     * @return 添加描边后的图片
     */
    @NonNull
    public static Bitmap stroke(@NonNull Bitmap image, int strokeSize, int strokeColor) {
        if (strokeSize <= 0) {
            return image;
        }

        final int srcWidth = image.getWidth();
        final int srcHeight = image.getHeight();

        final int newWidth = srcWidth + strokeSize * 2;
        final int newHeight = srcHeight + strokeSize * 2;
        final Bitmap result = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);

        // 1. 提取 Alpha 蒙版
        final Bitmap alphaMask = image.extractAlpha();

        // 2. 创建纯色 Paint 并替换颜色为 strokeColor
        final Paint maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        maskPaint.setColorFilter(new PorterDuffColorFilter(strokeColor, PorterDuff.Mode.SRC_IN));

        // 3. 在 8 个方向（或更多方向）上偏移绘制 Alpha 蒙版，形成外扩轮廓
        // 步长越密，边缘越平滑；8 个方向足以应对大多数尺寸
        int steps = 12;
        for (int i = 0; i < steps; i++) {
            double angle = 2 * Math.PI * i / steps;
            float dx = (float) (strokeSize + Math.cos(angle) * strokeSize);
            float dy = (float) (strokeSize + Math.sin(angle) * strokeSize);
            canvas.drawBitmap(alphaMask, dx, dy, maskPaint);
        }

        // 如果 strokeSize 较大，可以再补充内层圆圈绘制，避免中间镂空
        if (strokeSize > 4) {
            for (int i = 0; i < steps; i++) {
                double angle = 2 * Math.PI * i / steps;
                float dx = (float) (strokeSize + Math.cos(angle) * (strokeSize * 0.5f));
                float dy = (float) (strokeSize + Math.sin(angle) * (strokeSize * 0.5f));
                canvas.drawBitmap(alphaMask, dx, dy, maskPaint);
            }
        }

        // 4. 将原图绘制在最上层中心位置
        final Paint srcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(image, strokeSize, strokeSize, srcPaint);

        // 5. 回收临时资源
        alphaMask.recycle();

        return result;
    }

    /**
     * 裁剪 Bitmap 上下左右的透明/近透明区域
     *
     * @param src            源 Bitmap
     * @param alphaThreshold Alpha 阈值 (0-255)。推荐填 0 或 1。如果希望忽略微弱噪点，可传 5~10。
     * @return 裁切后的新 Bitmap（如果原图全透明，则返回 null；如果没有透明区域需要裁切，返回原图）
     */
    @Nullable
    public static Bitmap trimTransparentBounds(@NonNull Bitmap src, int alphaThreshold)
            throws OutOfMemoryError {
        final int width = src.getWidth();
        final int height = src.getHeight();

        // 1. 一次性获取全图像素数组（CPU 内存连续存储，扫描性能远高于逐个调用 src.getPixel(x, y)）
        final int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);

        int top = 0;
        int bottom = height - 1;
        int left = 0;
        int right = width - 1;

        // 2. 从顶部向下扫描找 top 边界
        topLoop:
        for (int y = 0; y < height; y++) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                if (Color.alpha(pixels[rowOffset + x]) > alphaThreshold) {
                    top = y;
                    break topLoop;
                }
            }
            if (y == height - 1) {
                // 整张图都是透明的，无需裁切
                return null;
            }
        }

        // 3. 从底部向上扫描找 bottom 边界
        bottomLoop:
        for (int y = height - 1; y >= top; y--) {
            int rowOffset = y * width;
            for (int x = 0; x < width; x++) {
                if (Color.alpha(pixels[rowOffset + x]) > alphaThreshold) {
                    bottom = y;
                    break bottomLoop;
                }
            }
        }

        // 4. 从左向右扫描找 left 边界 (仅在 top ~ bottom 垂直范围内扫描即可，大幅提升效率)
        leftLoop:
        for (int x = 0; x < width; x++) {
            for (int y = top; y <= bottom; y++) {
                if (Color.alpha(pixels[y * width + x]) > alphaThreshold) {
                    left = x;
                    break leftLoop;
                }
            }
        }

        // 5. 从右向左扫描找 right 边界
        rightLoop:
        for (int x = width - 1; x >= left; x--) {
            for (int y = top; y <= bottom; y++) {
                if (Color.alpha(pixels[y * width + x]) > alphaThreshold) {
                    right = x;
                    break rightLoop;
                }
            }
        }

        // 6. 计算有效区域的宽高
        final int trimmedWidth = right - left + 1;
        final int trimmedHeight = bottom - top + 1;

        // 如果没有透明留白，直接返回原图
        if (left == 0 && top == 0 && trimmedWidth == width && trimmedHeight == height) {
            return src;
        }

        // 7. 根据有效区域剪裁 Bitmap
        return Bitmap.createBitmap(src, left, top, trimmedWidth, trimmedHeight);
    }
}
