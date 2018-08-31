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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;

/**
 * 二维码生成工具类
 * Created by Alex on 2018/9/1.
 */
public class QRCodeUtil {

    private static final Canvas CANVAS = new Canvas();

    private QRCodeUtil() {
        //no instance
    }

    /**
     * 创建二维码
     *
     * @param content    内容
     * @param bitmap     图像
     * @param color      标志位颜色
     * @param background 背景颜色
     * @param logo       商标
     * @param logoWidth  商标宽（建议不超过图像宽度的五分之一）
     * @param logoHeight 商标高（建议不超过图像高度的五分之一）
     * @return 是否成功
     */
    @SuppressWarnings("all")
    public static boolean createQRCode(String content, Bitmap bitmap, int color, int background,
                                       Drawable logo, int logoWidth, int logoHeight) {
        if (TextUtils.isEmpty(content) || bitmap == null || bitmap.isRecycled())
            return false;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        if (width <= 0 || height <= 0)
            return false;
        final HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        final BitMatrix matrix;
        try {
            matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,
                    width, height, hints);
        } catch (Exception e) {
            return false;
        } catch (OutOfMemoryError error) {
            return false;
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    bitmap.setPixel(x, y, color);
                } else {
                    bitmap.setPixel(x, y, background);
                }
            }
        }
        if (logo != null && logoWidth > 0 && logoHeight > 0) {
            logo.setBounds(0, 0, logoWidth, logoHeight);
            synchronized (CANVAS) {
                CANVAS.setBitmap(bitmap);
                CANVAS.save();
                CANVAS.translate((width - logoWidth) * 0.5f, (height - logoHeight) * 0.5f);
                logo.draw(CANVAS);
                CANVAS.restore();
                CANVAS.setBitmap(null);
            }
        }
        return true;
    }

    /**
     * 创建二维码
     *
     * @param content 内容
     * @param bitmap  图像
     * @return 是否成功
     */
    public static boolean createQRCode(String content, Bitmap bitmap) {
        return createQRCode(content, bitmap, Color.BLACK, Color.WHITE,
                null, 0, 0);
    }
}