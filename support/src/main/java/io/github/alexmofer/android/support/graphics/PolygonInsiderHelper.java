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
package io.github.alexmofer.android.support.graphics;

import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

import androidx.annotation.NonNull;

/**
 * 矩形内部判断辅助
 * Created by Alex on 2022/10/19.
 */
public class PolygonInsiderHelper {

    private PolygonInsiderHelper() {
        //no instance
    }

    /**
     * 矩形内部计算器
     */
    public interface PolygonInsider {

        /**
         * 计算边
         *
         * @param sx 起点X轴坐标
         * @param sy 起点Y轴坐标
         * @param ex 终点X轴坐标
         * @param ey 终点Y轴坐标
         */
        void calculateSide(float sx, float sy, float ex, float ey);

        /**
         * 判断是否在内部
         *
         * @return 在内部时返回true
         */
        boolean isInside();
    }

    /**
     * 射线法，纯数学实现，注意奇偶充填，偶数区域属于多边形外部，因此不算在内部
     */
    public static class MathPolygonInsider implements PolygonInsider {

        private static final double EPS = 1e-6;
        private final float mX;
        private final float mY;
        private boolean mInside;

        public MathPolygonInsider(float x, float y) {
            mX = x;
            mY = y;
        }

        private static int compare(double x) {
            return Math.abs(x) < EPS ? 0 : Double.compare(x, 0);
        }

        @Override
        public void calculateSide(float sx, float sy, float ex, float ey) {
            if ((compare(sy - mY) > 0 != compare(ey - mY) > 0) &&
                    compare(mX - (mY - sy) * (sx - ex) / (sy - ey) - sx) < 0) {
                mInside = !mInside;
            }
        }

        @Override
        public boolean isInside() {
            return mInside;
        }
    }

    /**
     * 图形法
     * 可决定是否使用奇偶充填法则
     */
    public static class GraphicPolygonInsider implements PolygonInsider {

        private final Path mPath = new Path();
        private final float mX;
        private final float mY;
        private boolean mStart = true;

        public GraphicPolygonInsider(float x, float y, @NonNull Path.FillType ft) {
            mX = x;
            mY = y;
            mPath.setFillType(ft);
        }

        @Override
        public void calculateSide(float sx, float sy, float ex, float ey) {
            if (mStart) {
                mStart = false;
                mPath.moveTo(ex, ey);
            } else {
                mPath.lineTo(ex, ey);
            }
        }

        @Override
        public boolean isInside() {
            final Path path = new Path(mPath);
            path.close();
            final RectF rect = new RectF();
            path.computeBounds(rect, true);
            final int left = Math.round(rect.left - 0.5f);
            final int top = Math.round(rect.top - 0.5f);
            final int right = Math.round(rect.right + 0.5f);
            final int bottom = Math.round(rect.bottom + 0.5f);
            final Region region = new Region();
            region.setPath(path, new Region(left, top, right, bottom));
            return region.contains(Math.round(mX), Math.round(mY));
        }
    }
}
