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
package io.github.alexmofer.android.support.utils;

import android.graphics.Path;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 路径工具
 * Created by Alex on 2026/7/27.
 */
public final class PathUtils {
    private PathUtils() {
        //no instance
    }

    /**
     * 添加平滑曲线
     *
     * @param path   路径
     * @param move   起始点是否为 moveTo
     * @param points 点
     * @return 是否成功
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean addSmoothCurve(@NonNull Path path, boolean move,
                                         float... points) {
        if (points == null) {
            return false;
        }
        final int length = points.length;
        if (length < 4) {
            return false;
        }
        if (length == 4) {
            if (move) {
                path.moveTo(points[0], points[1]);
            } else {
                path.lineTo(points[0], points[1]);
            }
            path.lineTo(points[2], points[3]);
            return true;
        }
        if (move) {
            path.moveTo(points[0], points[1]);
        } else {
            path.lineTo(points[0], points[1]);
        }
        // 多点平滑：利用三次贝塞尔曲线 (cubicTo) 穿过所有点
        // 原理：计算每个线段的控制点，使得相邻曲线在连接处一阶导数连续（平滑）
        for (int i = 0; i < length - 2; i += 2) {
            float x0 = (i == 0) ? points[0] : points[i - 2];
            float y0 = (i == 0) ? points[1] : points[i - 1];

            float x1 = points[i];
            float y1 = points[i + 1];

            float x2 = points[i + 2];
            float y2 = points[i + 3];

            float x3 = (i + 4 < length) ? points[i + 4] : x2;
            float y3 = (i + 5 < length) ? points[i + 5] : y2;

            // 计算控制点 1
            float cp1x = x1 + (x2 - x0) / 6f;
            float cp1y = y1 + (y2 - y0) / 6f;

            // 计算控制点 2
            float cp2x = x2 - (x3 - x1) / 6f;
            float cp2y = y2 - (y3 - y1) / 6f;

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, x2, y2);
        }
        return true;
    }

    /**
     * 添加平滑曲线
     *
     * @param path   路径
     * @param move   起始点是否为 moveTo
     * @param points 点
     * @return 是否成功
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean addSmoothCurve(@NonNull Path path, boolean move,
                                         @NonNull List<Float> points) {
        if (points.isEmpty()) {
            return false;
        }
        final int length = points.size();
        if (length < 4) {
            return false;
        }
        if (length == 4) {
            if (move) {
                path.moveTo(points.get(0), points.get(1));
            } else {
                path.lineTo(points.get(0), points.get(1));
            }
            path.lineTo(points.get(2), points.get(3));
            return true;
        }
        if (move) {
            path.moveTo(points.get(0), points.get(1));
        } else {
            path.lineTo(points.get(0), points.get(1));
        }
        // 多点平滑：利用三次贝塞尔曲线 (cubicTo) 穿过所有点
        // 原理：计算每个线段的控制点，使得相邻曲线在连接处一阶导数连续（平滑）
        for (int i = 0; i < length - 2; i += 2) {
            float x0 = (i == 0) ? points.get(0) : points.get(i - 2);
            float y0 = (i == 0) ? points.get(1) : points.get(i - 1);

            float x1 = points.get(i);
            float y1 = points.get(i + 1);

            float x2 = points.get(i + 2);
            float y2 = points.get(i + 3);

            float x3 = (i + 4 < length) ? points.get(i + 4) : x2;
            float y3 = (i + 5 < length) ? points.get(i + 5) : y2;

            // 计算控制点 1
            float cp1x = x1 + (x2 - x0) / 6f;
            float cp1y = y1 + (y2 - y0) / 6f;

            // 计算控制点 2
            float cp2x = x2 - (x3 - x1) / 6f;
            float cp2y = y2 - (y3 - y1) / 6f;

            path.cubicTo(cp1x, cp1y, cp2x, cp2y, x2, y2);
        }
        return true;
    }
}
