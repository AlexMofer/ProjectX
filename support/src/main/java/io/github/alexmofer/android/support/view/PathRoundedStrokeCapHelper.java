/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.view;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

import androidx.annotation.NonNull;

/**
 * 路径圆角描边辅助类
 * Created by Alex on 2026/7/2.
 */
public final class PathRoundedStrokeCapHelper {
    private final PathMeasure mPathMeasure = new PathMeasure();
    private final Matrix mMatrix = new Matrix();
    private final float[] mPos = new float[2];
    private final float[] mTan = new float[2];
    private final float mThickness;
    private final float mCornerRadius;

    public PathRoundedStrokeCapHelper(float thickness, float cornerRadius) {
        mThickness = thickness;
        mCornerRadius = cornerRadius;
    }


    /**
     * 添加圆弧 （纯数学计算，因为不是使用描边扩展的方式，因此 sweepAngle 较小时会异常）
     *
     * @param path         待写入路径
     * @param cx           圆心X
     * @param cy           圆心Y
     * @param R            圆半径
     * @param thickness    圆环宽度
     * @param cornerRadius 圆角半径
     * @param startAngle   起始角度
     * @param sweepAngle   扫过的角度
     */
    public static void addRoundedArc(@NonNull Path path,
                                     float cx, float cy, float R,
                                     float thickness, float cornerRadius,
                                     float startAngle, float sweepAngle) {
        float r = R - thickness;

        // 确保圆角半径不会导致形状崩坏
        float c = Math.min(cornerRadius, thickness / 2.01f);

        // 计算圆角圆心偏转角
        float alpha_os = (float) Math.toDegrees(Math.asin(c / (R - c)));
        float alpha_is = (float) Math.toDegrees(Math.asin(c / (r + c)));

        float endAngle = startAngle + sweepAngle;

        // 1. 外圈起点小圆角
        float cos_x = cx + (R - c) * (float) Math.cos(Math.toRadians(startAngle + alpha_os));
        float cos_y = cy + (R - c) * (float) Math.sin(Math.toRadians(startAngle + alpha_os));

        float arcToLeft, arcToTop, arcToRight, arcToBottom;
        arcToLeft = cos_x - c;
        arcToTop = cos_y - c;
        arcToRight = cos_x + c;
        arcToBottom = cos_y + c;
        path.arcTo(arcToLeft, arcToTop, arcToRight, arcToBottom, startAngle - 90f, 90f + alpha_os, false);

        // 2. 外圈大主弧
        arcToLeft = cx - R;
        arcToTop = cy - R;
        arcToRight = cx + R;
        arcToBottom = cy + R;
        path.arcTo(arcToLeft, arcToTop, arcToRight, arcToBottom, startAngle + alpha_os, sweepAngle - alpha_os * 2, false);

        // 3. 外圈终点小圆角
        float coe_x = cx + (R - c) * (float) Math.cos(Math.toRadians(endAngle - alpha_os));
        float coe_y = cy + (R - c) * (float) Math.sin(Math.toRadians(endAngle - alpha_os));
        arcToLeft = coe_x - c;
        arcToTop = coe_y - c;
        arcToRight = coe_x + c;
        arcToBottom = coe_y + c;
        path.arcTo(arcToLeft, arcToTop, arcToRight, arcToBottom, endAngle - alpha_os, 90f + alpha_os, false);

        // 4. 内圈终点小圆角
        float cie_x = cx + (r + c) * (float) Math.cos(Math.toRadians(endAngle - alpha_is));
        float cie_y = cy + (r + c) * (float) Math.sin(Math.toRadians(endAngle - alpha_is));
        arcToLeft = cie_x - c;
        arcToTop = cie_y - c;
        arcToRight = cie_x + c;
        arcToBottom = cie_y + c;
        path.arcTo(arcToLeft, arcToTop, arcToRight, arcToBottom, endAngle + 90f, 90f - alpha_is, false);

        // 5. 内圈大主弧 (逆时针)
        arcToLeft = cx - r;
        arcToTop = cy - r;
        arcToRight = cx + r;
        arcToBottom = cy + r;
        path.arcTo(arcToLeft, arcToTop, arcToRight, arcToBottom, endAngle - alpha_is, -(sweepAngle - alpha_is * 2), false);

        // 6. 内圈起点小圆角
        float cis_x = cx + (r + c) * (float) Math.cos(Math.toRadians(startAngle + alpha_is));
        float cis_y = cy + (r + c) * (float) Math.sin(Math.toRadians(startAngle + alpha_is));
        arcToLeft = cis_x - c;
        arcToTop = cis_y - c;
        arcToRight = cis_x + c;
        arcToBottom = cis_y + c;
        path.arcTo(arcToLeft, arcToTop, arcToRight, arcToBottom, startAngle + alpha_is + 180f, 90f - alpha_is, false);

        path.close();
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     * @param path   路径
     * @param paint  画笔（不支持透明度，绘制带透明度的需要使用 Xfermode 处理）
     */
    public void draw(@NonNull Canvas canvas, @NonNull Path path, @NonNull Paint paint) {
        if (path.isEmpty()) {
            return;
        }
        final float thickness = mThickness;
        final float cornerRadius = mCornerRadius;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(thickness);
        paint.setStrokeCap(Paint.Cap.BUTT);
        canvas.drawPath(path, paint);


        paint.setStyle(Paint.Style.FILL);

        mPathMeasure.setPath(path, false);
        final float length = mPathMeasure.getLength();
        final float[] pos = mPos;
        final float[] tan = mTan;
        mPathMeasure.getPosTan(0, pos, tan);

        mMatrix.reset();
        // 将纹理中心移动到当前点
        mMatrix.postTranslate(-cornerRadius, -thickness * 0.5f);
        // 旋转至切线方向
        mMatrix.postRotate((float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI));
        // 移动到目标坐标
        mMatrix.postTranslate(pos[0], pos[1]);

        final int save1 = canvas.save();
        canvas.concat(mMatrix);
        canvas.drawRect(0, cornerRadius, cornerRadius * 2, thickness - cornerRadius, paint);
        canvas.drawCircle(cornerRadius, cornerRadius, cornerRadius, paint);
        canvas.drawCircle(cornerRadius, thickness - cornerRadius, cornerRadius, paint);
        canvas.restoreToCount(save1);

        mPathMeasure.getPosTan(length, pos, tan);

        mMatrix.reset();
        // 将纹理中心移动到当前点
        mMatrix.postTranslate(-cornerRadius, -thickness * 0.5f);
        // 旋转至切线方向
        mMatrix.postRotate((float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI));
        // 移动到目标坐标
        mMatrix.postTranslate(pos[0], pos[1]);

        final int save2 = canvas.save();
        canvas.concat(mMatrix);
        canvas.drawRect(0, cornerRadius, cornerRadius * 2, thickness - cornerRadius, paint);
        canvas.drawCircle(cornerRadius, cornerRadius, cornerRadius, paint);
        canvas.drawCircle(cornerRadius, thickness - cornerRadius, cornerRadius, paint);
        canvas.restoreToCount(save2);
    }

    /**
     * 获取路径的宽度
     *
     * @return 路径的宽度
     */
    public float getThickness() {
        return mThickness;
    }

    /**
     * 获取圆角半径
     *
     * @return 圆角半径
     */
    public float getCornerRadius() {
        return mCornerRadius;
    }
}
