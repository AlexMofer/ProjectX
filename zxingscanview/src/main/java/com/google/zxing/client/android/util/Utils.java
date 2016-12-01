package com.google.zxing.client.android.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

/**
 * 工具类
 * Created by Alex on 2016/11/28.
 */

public class Utils {
    public static final String PERMISSION_VIBRATE = "android.permission.VIBRATE";//振动器权限
    public static final String PERMISSION_CAMERA = "android.permission.CAMERA";//相机权限

    private Utils() {
    }

    /**
     * 检查权限
     *
     * @param context    Context
     * @param permission 权限
     * @return 是否拥有权限
     */
    public static boolean lacksPermission(Context context, String permission) {
        return Compat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

    /**
     * 为结果图添加识别效果
     *
     * @param result      扫描结果
     * @param barcode     结果图片
     * @param scaleFactor 缩放比
     * @param color       颜色
     */
    @SuppressWarnings("unused")
    public static void addResultPoints(Result result, Bitmap barcode, float scaleFactor, int color) {
        ResultPoint[] points = result.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(color);
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
            } else if (points.length == 4 &&
                    (result.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                            result.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1], scaleFactor);
                drawLine(canvas, paint, points[2], points[3], scaleFactor);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    if (point != null) {
                        canvas.drawPoint(scaleFactor * point.getX(), scaleFactor * point.getY(), paint);
                    }
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b, float scaleFactor) {
        if (a != null && b != null) {
            canvas.drawLine(scaleFactor * a.getX(),
                    scaleFactor * a.getY(),
                    scaleFactor * b.getX(),
                    scaleFactor * b.getY(),
                    paint);
        }
    }
}
