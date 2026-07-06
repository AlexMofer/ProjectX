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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;

import androidx.annotation.NonNull;

/**
 * 圆角圆弧绘制器
 * Created by Alex on 2026/7/3.
 */
public final class RoundedArcDrawer {
    private final Path mPath = new Path();
    private final Path mMask1 = new Path();
    private final Path mRoundStart = new Path();
    private final Path mRoundEnd = new Path();
    private final Path mOuter = new Path();
    private final Helper mHelper;
    private final float mThickness;
    private final float mCornerRadius;
    private final float mCutWidth;
    private int mColor;
    private float mRotateStart;
    private float mDXStart;
    private float mDYStart;
    private float mRotateEnd;
    private float mDXEnd;
    private float mDYEnd;

    /**
     * 构造方法
     *
     * @param helper       辅助器，可多个绘制器共用
     * @param thickness    线条宽度
     * @param cornerRadius 圆角半径
     * @param cutWidth     裁剪宽度
     */
    public RoundedArcDrawer(@NonNull Helper helper,
                            float thickness, float cornerRadius, float cutWidth) {
        mHelper = helper;
        mThickness = thickness;
        mCornerRadius = cornerRadius;
        mCutWidth = cutWidth;
        final float halfThickness = thickness * 0.5f;
        mMask1.moveTo(-cornerRadius - cutWidth + 1, -halfThickness - 1);
        mMask1.lineTo(cornerRadius + cutWidth - 1, -halfThickness - 1);
        mMask1.lineTo(cornerRadius + cutWidth - 1, halfThickness + 1);
        mMask1.lineTo(-cornerRadius - cutWidth + 1, halfThickness + 1);
        mMask1.close();
        mRoundStart.addRoundRect(cutWidth, -halfThickness, cutWidth + cornerRadius, halfThickness,
                new float[]{cornerRadius, cornerRadius, 0, 0, 0, 0, cornerRadius, cornerRadius}, Path.Direction.CW);
        mRoundEnd.addRoundRect(-cutWidth - cornerRadius, -halfThickness, -cutWidth, halfThickness,
                new float[]{0, 0, cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0, 0}, Path.Direction.CW);
        mOuter.setFillType(Path.FillType.EVEN_ODD);
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

    /**
     * 获取裁剪宽度
     *
     * @return 裁剪宽度
     */
    public float getCutWidth() {
        return mCutWidth;
    }

    /**
     * 设置颜色
     *
     * @param color 颜色
     */
    public void setColor(int color) {
        mColor = color;
    }

    /**
     * 获取颜色
     *
     * @return 颜色
     */
    public int getColor() {
        return mColor;
    }

    /**
     * 设置参数
     *
     * @param cx         圆心X坐标
     * @param cy         圆心Y坐标
     * @param radius     半径
     * @param startAngle 起始角度
     * @param sweepAngle 扫过的角度
     */
    public void set(float cx, float cy, float radius, float startAngle, float sweepAngle) {
        mPath.reset();
        mPath.arcTo(cx - radius, cy - radius, cx + radius, cy + radius, startAngle, sweepAngle, true);

        final PathMeasure pathMeasure = mHelper.mPathMeasure;
        final float[] pos = mHelper.mPos;
        final float[] tan = mHelper.mTan;
        pathMeasure.setPath(mPath, false);
        pathMeasure.getPosTan(0, pos, tan);
        mRotateStart = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        mDXStart = pos[0];
        mDYStart = pos[1];
        pathMeasure.getPosTan(pathMeasure.getLength(), pos, tan);
        mRotateEnd = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        mDXEnd = pos[0];
        mDYEnd = pos[1];

        mOuter.reset();
        mOuter.addCircle(cx, cy, radius + mThickness, Path.Direction.CW);
        mOuter.addCircle(cx, cy, radius + mThickness * 0.5f - 1, Path.Direction.CW);
    }

    /**
     * 重置
     */
    public void reset() {
        mPath.reset();
        mOuter.reset();
    }

    /**
     * 绘制
     *
     * @param canvas 画布
     */
    public void draw(@NonNull Canvas canvas) {
        if (mPath.isEmpty()) {
            return;
        }
        final Paint paint = mHelper.mPaint;
        final Matrix matrix = mHelper.mMatrix;
        final Xfermode clear = mHelper.mClear;

        final int saveLayer = canvas.saveLayer(null, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mThickness);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setColor(mColor);
        canvas.drawPath(mPath, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        matrix.reset();
        matrix.postRotate(mRotateStart);
        matrix.postTranslate(mDXStart, mDYStart);
        canvas.save();
        canvas.concat(matrix);
        paint.setXfermode(clear);
        canvas.drawPath(mMask1, paint);
        paint.setXfermode(null);
        paint.setColor(mColor);
        canvas.drawPath(mRoundStart, paint);
        canvas.restore();

        matrix.reset();
        matrix.postRotate(mRotateEnd);
        matrix.postTranslate(mDXEnd, mDYEnd);
        canvas.save();
        canvas.concat(matrix);
        paint.setXfermode(clear);
        canvas.drawPath(mMask1, paint);
        paint.setXfermode(null);
        paint.setColor(mColor);
        canvas.drawPath(mRoundEnd, paint);
        canvas.restore();

        paint.setXfermode(clear);
        canvas.drawPath(mOuter, paint);
        paint.setXfermode(null);

        canvas.restoreToCount(saveLayer);
    }

    public static class Helper {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final PathMeasure mPathMeasure = new PathMeasure();
        private final Matrix mMatrix = new Matrix();
        private final float[] mPos = new float[2];
        private final float[] mTan = new float[2];
        private final Xfermode mClear = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }
}
