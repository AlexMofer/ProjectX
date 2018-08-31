/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * 二维码生成工具类
 * Created by Alex on 2016/10/10.
 */

public class QRCodeUtil {

    private QRCodeUtil() {
        //no instance
    }

    /**
     * 创建二维码
     *
     * @param content 内容
     * @param bitmap  图像
     * @return 是否成功
     */
    @Nullable
    public static boolean createQRCode(String content, Bitmap bitmap) {
        return false;
    }

    /**
     * 创建二维码
     *
     * @param content 内容
     * @param width   宽度
     * @param height  高度
     * @return 图像
     */
    @Nullable
    public static Bitmap createQRCode(String content, int width, int height) {
        if (TextUtils.isEmpty(content) || width <= 0 || height <= 0)
            return null;
        final HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        final BitMatrix matrix;
        try {
            matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,
                    width, height, hints);
        } catch (Exception e) {
            return null;
        } catch (OutOfMemoryError error) {
            return null;
        }
        final int[] pixels = new int[width * height];// TODO
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        final Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError error) {
            return null;
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static boolean createQRImage(String content, int width, int height,
                                        Bitmap logo, String path) {
        if (TextUtils.isEmpty(path))
            return false;
        final Bitmap qr = createQRCode(content, width, height);
        if (qr == null)
            return false;
        if (logo != null) {
            // TODO 增加LOGO
//            bitmap = addLogo(bitmap, logoBm);
        }
        final FileOutputStream stream;
        try {
            stream = new FileOutputStream(path);
        } catch (Exception e) {
            qr.recycle();
            return false;
        }
        boolean result = qr.compress(Bitmap.CompressFormat.PNG, 100, stream);
        qr.recycle();
        try {
            stream.flush();
        } catch (Exception e) {
            result = false;
        }
        try {
            stream.close();
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 在二维码中间添加Logo图案
     */
    private static Bitmap addLogo(Bitmap src, Bitmap logo) {
        if (src == null) {
            return null;
        }
        if (logo == null) {
            return src;
        }
        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }
        //logo大小为二维码整体大小的1/5
        float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2,
                    (srcHeight - logoHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

}