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
package io.github.alexmofer.android.support.formulas;

import androidx.annotation.Nullable;

/**
 * 相交公式
 * Created by Alex on 2022/11/28.
 */
public class IntersectFormulas {

    private IntersectFormulas() {
        //no instance
    }

    /**
     * 计算点位于直线的哪一边
     *
     * @param x  点X坐标
     * @param y  点Y坐标
     * @param x1 直线经过的点1的X轴坐标
     * @param y1 直线经过的点1的Y轴坐标
     * @param x2 直线经过的点2的X轴坐标
     * @param y2 直线经过的点2的Y轴坐标
     * @return 小于0：点在直线左侧；大于0：点在直线右侧；等于0：点在直线上。
     */
    public static double calculateSidePointToLine(double x, double y,
                                                  double x1, double y1, double x2, double y2) {
        // 直线方程：Ax + By + C = 0
        final double A = y2 - y1;
        final double B = x1 - x2;
        final double C = x2 * y1 - x1 * y2;
        return A * x + B * y + C;
    }

    /**
     * 判断点与直线相交
     *
     * @param x  点X坐标
     * @param y  点Y坐标
     * @param x1 直线经过的点1的X轴坐标
     * @param y1 直线经过的点1的Y轴坐标
     * @param x2 直线经过的点2的X轴坐标
     * @param y2 直线经过的点2的Y轴坐标
     * @return 相交时返回true
     */
    public static boolean isIntersectPointToLine(double x, double y,
                                                 double x1, double y1, double x2, double y2) {
        return calculateSidePointToLine(x, y, x1, y1, x2, y2) == 0;
    }

    /**
     * 判断点与线段相交
     *
     * @param x  点X坐标
     * @param y  点Y坐标
     * @param x1 线段起点X坐标
     * @param y1 线段起点Y坐标
     * @param x2 线段终点X坐标
     * @param y2 线段终点Y坐标
     * @return 相交时返回true
     */
    public static boolean isIntersectPointToLineSegment(double x, double y,
                                                        double x1, double y1,
                                                        double x2, double y2) {
        if (isIntersectPointToLine(x, y, x1, y1, x2, y2)) {
            double l, t, r, b;
            l = r = x1;
            t = b = y1;
            l = Math.min(l, x2);
            t = Math.min(t, y2);
            r = Math.max(r, x2);
            b = Math.max(b, y2);
            return isIntersectPointToRect(x, y, l, t, r, b);
        }
        return false;
    }


    /**
     * 判断点与矩形相交
     *
     * @param x      点X坐标
     * @param y      点Y坐标
     * @param left   矩形左边
     * @param top    矩形上边
     * @param right  矩形右边
     * @param bottom 矩形下边
     * @return 相交时返回true
     */
    public static boolean isIntersectPointToRect(double x, double y,
                                                 double left, double top,
                                                 double right, double bottom) {
        return left <= x && x <= right && top <= y && y <= bottom;
    }

    /**
     * 判断矩形与矩形是否相交
     *
     * @param l1 矩形1左边
     * @param t1 矩形1上边
     * @param r1 矩形1右边
     * @param b1 矩形1下边
     * @param l2 矩形2左边
     * @param t2 矩形2上边
     * @param r2 矩形2右边
     * @param b2 矩形2下边
     * @return 相交时返回true
     */
    public static boolean isIntersectRectToRect(double l1, double t1, double r1, double b1,
                                                double l2, double t2, double r2, double b2) {
        return !(l2 > r1) && !(t2 > b1) && !(r2 < l1) && !(b2 < t1);
    }

    /**
     * 判断线段与矩形是否相交
     *
     * @param x1     线段起点X坐标
     * @param y1     线段起点Y坐标
     * @param x2     线段终点X坐标
     * @param y2     线段终点Y坐标
     * @param left   矩形左边
     * @param top    矩形上边
     * @param right  矩形右边
     * @param bottom 矩形下边
     * @return 相交时返回true
     */
    public static boolean isIntersectLineSegmentToRect(double x1, double y1, double x2, double y2,
                                                       double left, double top,
                                                       double right, double bottom) {
        double l, t, r, b;
        l = r = x1;
        t = b = y1;
        l = Math.min(l, x2);
        t = Math.min(t, y2);
        r = Math.max(r, x2);
        b = Math.max(b, y2);
        if (!isIntersectRectToRect(l, t, r, b, left, top, right, bottom)) {
            // 线段外接矩形不相交
            return false;
        }
        // 线段外接矩形相交
        if (isIntersectPointToRect(x1, y1, left, top, right, bottom)) {
            // 点1在矩形内部，必然相交
            return true;
        }
        if (isIntersectPointToRect(x2, y2, left, top, right, bottom)) {
            // 点2在矩形内部，必然相交
            return true;
        }
        // 线段外接矩形相交，两个端点不在矩形内部
        final double cx = l + (r - l) * 0.5f;
        final double cy = t + (b - t) * 0.5f;
        if (isIntersectPointToRect(cx, cy, left, top, right, bottom)) {
            // 线段外接矩形中心点在矩形内部，必然相交
            return true;
        }
        // 线段外接矩形相交，两个端点不在矩形内部，线段外接矩形中心点在矩形外部
        final double side_lt = calculateSidePointToLine(left, top, x1, y1, x2, y2);
        final double side_rt = calculateSidePointToLine(right, top, x1, y1, x2, y2);
        final double side_lb = calculateSidePointToLine(left, bottom, x1, y1, x2, y2);
        final double side_rb = calculateSidePointToLine(right, bottom, x1, y1, x2, y2);
        // 四个点均在线段所在直线的同侧时不相交，反之相交
        return !((side_lt < 0 && side_rt < 0 && side_lb < 0 && side_rb < 0) ||
                (side_lt > 0 && side_rt > 0 && side_lb > 0 && side_rb > 0));
    }

    /**
     * 判断直线与圆是否相交
     *
     * @param x1     直线经过的点1的X轴坐标
     * @param y1     直线经过的点1的Y轴坐标
     * @param x2     直线经过的点2的X轴坐标
     * @param y2     直线经过的点2的Y轴坐标
     * @param cx     圆心X轴坐标
     * @param cy     圆心Y轴坐标
     * @param radius 圆半径
     * @return 相交时返回true
     */
    public static boolean isIntersectLineToCircle(double x1, double y1,
                                                  double x2, double y2,
                                                  double cx, double cy,
                                                  double radius) {
        return DistanceFormulas.calculatePointToLine(cx, cy, x1, y1, x2, y2) <= radius;
    }

    /**
     * 判断线段与圆是否相交
     *
     * @param x1     线段起点X轴坐标
     * @param y1     线段起点Y轴坐标
     * @param x2     线段终点X轴坐标
     * @param y2     线段终点Y轴坐标
     * @param cx     圆心X轴坐标
     * @param cy     圆心Y轴坐标
     * @param radius 圆半径
     * @return 相交时返回true
     */
    public static boolean isIntersectLineSegmentToCircle(double x1, double y1,
                                                         double x2, double y2,
                                                         double cx, double cy,
                                                         double radius) {
        if (DistanceFormulas.calculatePointToLineSegment(cx, cy, x1, y1, x2, y2) <= radius) {
            final double d1 = DistanceFormulas.calculatePointToPoint(cx, cy, x1, y1);
            final double d2 = DistanceFormulas.calculatePointToPoint(cx, cy, x2, y2);
            // 端点均在圆内部，无交点
            return !(d1 < radius && d2 < radius);
        }
        return false;
    }

    /**
     * 判断两条直线是否相交
     *
     * @param x1 直线1经过的点1的X轴坐标
     * @param y1 直线1经过的点1的Y轴坐标
     * @param x2 直线1经过的点2的X轴坐标
     * @param y2 直线1经过的点2的Y轴坐标
     * @param x3 直线2经过的点3的X轴坐标
     * @param y3 直线2经过的点3的Y轴坐标
     * @param x4 直线2经过的点4的X轴坐标
     * @param y4 直线2经过的点4的Y轴坐标
     * @return 相交时返回true
     */
    public static boolean isIntersectLineToLine(double x1, double y1, double x2, double y2,
                                                double x3, double y3, double x4, double y4) {
        final double dx1 = x1 - x2;
        final double dx2 = x3 - x4;
        if (dx1 == 0 && dx2 == 0) {
            // 均垂直于X轴，平行线不相交
            return false;
        }
        if (dx1 == 0 || dx2 == 0) {
            // 一条垂直一条不垂直，相交
            return true;
        }
        final double k1 = (y1 - y2) / dx1;
        final double k2 = (y3 - y4) / dx2;
        return k1 != k2;// 相同斜率，平行线不相交
    }

    /**
     * 判断两条线段是否相交
     *
     * @param x1 线段1起点X轴坐标
     * @param y1 线段1起点Y轴坐标
     * @param x2 线段1终点X轴坐标
     * @param y2 线段1终点Y轴坐标
     * @param x3 线段2起点X轴坐标
     * @param y3 线段2起点Y轴坐标
     * @param x4 线段2终点X轴坐标
     * @param y4 线段2终点Y轴坐标
     * @return 相交时返回true
     */
    public static boolean isIntersectLineSegmentToLineSegment(double x1, double y1,
                                                              double x2, double y2,
                                                              double x3, double y3,
                                                              double x4, double y4) {
        final double dx1 = x1 - x2;
        final double dx2 = x3 - x4;
        if (dx1 == 0 && dx2 == 0) {
            // 均垂直于X轴，平行线不相交
            return false;
        }
        if (dx1 == 0 || dx2 == 0) {
            // 一条垂直一条不垂直，相交
            double left = x3, top = y3, right = x3, bottom = y3;
            left = Math.min(left, x4);
            top = Math.min(top, y4);
            right = Math.max(right, x4);
            bottom = Math.max(bottom, y4);
            if (isIntersectLineSegmentToRect(x1, y1, x2, y2, left, top, right, bottom)) {
                // 线段与另一线段外接矩形相交
                return calculateIntersectionLineSegmentToLineSegment(x1, y1, x2, y2,
                        x3, y3, x4, y4) != null;
            }
            return false;
        }
        final double k1 = (y1 - y2) / dx1;
        final double k2 = (y3 - y4) / dx2;
        if (k1 == k2) {
            // 相同斜率，平行线不相交
            return false;
        }
        double left = x3, top = y3, right = x3, bottom = y3;
        left = Math.min(left, x4);
        top = Math.min(top, y4);
        right = Math.max(right, x4);
        bottom = Math.max(bottom, y4);
        if (isIntersectLineSegmentToRect(x1, y1, x2, y2, left, top, right, bottom)) {
            // 线段与另一线段外接矩形相交
            return calculateIntersectionLineSegmentToLineSegment(x1, y1, x2, y2,
                    x3, y3, x4, y4) != null;
        }
        return false;
    }

    /**
     * 计算直线与圆交点
     *
     * @param x1     直线经过的点1的X轴坐标
     * @param y1     直线经过的点1的Y轴坐标
     * @param x2     直线经过的点2的X轴坐标
     * @param y2     直线经过的点2的Y轴坐标
     * @param cx     圆心X轴坐标
     * @param cy     圆心Y轴坐标
     * @param radius 圆半径
     * @return 交点，null：无焦点；length为2：单个交点；length为4：两个交点。
     */
    @Nullable
    public static double[] calculateIntersectionLineToCircle(double x1, double y1,
                                                             double x2, double y2,
                                                             double cx, double cy,
                                                             double radius) {
        final double dx = x1 - x2;
        if (dx == 0) {
            // 直线X轴垂直
            final double dis = Math.abs(cx - x1);
            if (dis == 0) {
                return new double[]{cx, cy - radius, cx, cy + radius};
            } else if (dis > radius) {
                // 无交点
                return null;
            } else if (dis == radius) {
                // 一个交点
                return new double[]{x1, cy};
            } else {
                // 两个交点
                final double half = Math.sin(Math.acos(dis / radius)) * radius;
                return new double[]{x1, cy - half, x1, cy + half};
            }
        } else {
            final double k = (y1 - y2) / dx;
            final double b = (y1 - x1 * k);
            if (k == 0) {
                // 直线Y轴垂直
                final double dis = Math.abs(cy - b);
                if (dis == 0) {
                    return new double[]{cx - radius, cy, cx + radius, cy};
                } else if (dis > radius) {
                    // 无交点
                    return null;
                } else if (dis == radius) {
                    // 一个交点
                    return new double[]{cx, b};
                } else {
                    // 两个交点
                    final double half = Math.sin(Math.acos(dis / radius)) * radius;
                    return new double[]{cx - half, b, cx + half, b};
                }
            } else {
                // 倾斜直线，计算圆心到直线距离
                final double dis =
                        DistanceFormulas.calculatePointToLine(cx, cy, x1, y1, x2, y2);
                if (dis > radius) {
                    // 距离大于半径，无交点
                    return null;
                }
                final double A = Math.pow(k, 2) + 1;
                final double B = 2 * (k * b - k * cy - cx);
                final double C = Math.pow(cx, 2) + Math.pow((b - cy), 2) -
                        Math.pow(radius, 2);
                final double delta = Math.pow(B, 2) - 4 * A * C;
                if (delta < 0) {
                    // 算法精度问题，无法求解
                    return null;
                } else {
                    final double i1x = (-B - Math.sqrt(delta)) / (2 * A);
                    final double i1y = k * i1x + b;
                    final double i2x = (-B + Math.sqrt(delta)) / (2 * A);
                    final double i2y = k * i2x + b;
                    if (i1x == i2x && i1y == i2y) {
                        return new double[]{i1x, i1y};
                    }
                    return new double[]{i1x, i1y, i2x, i2y};
                }
            }
        }
    }

    /**
     * 计算线段与圆交点
     *
     * @param x1     线段起点X轴坐标
     * @param y1     线段起点Y轴坐标
     * @param x2     线段终点X轴坐标
     * @param y2     线段终点Y轴坐标
     * @param cx     圆心X轴坐标
     * @param cy     圆心Y轴坐标
     * @param radius 圆半径
     * @return 交点，null：无焦点；length为2：单个交点；length为4：两个交点，存在顺序，第一个点靠近起点。
     */
    @Nullable
    public static double[] calculateIntersectionLineSegmentToCircle(double x1, double y1,
                                                                    double x2, double y2,
                                                                    double cx, double cy,
                                                                    double radius) {
        if (DistanceFormulas.calculatePointToLineSegment(cx, cy, x1, y1, x2, y2) > radius) {
            // 不相交
            return null;
        }
        final double d1 = DistanceFormulas.calculatePointToPoint(cx, cy, x1, y1);
        final double d2 = DistanceFormulas.calculatePointToPoint(cx, cy, x2, y2);
        if (d1 < radius && d2 < radius) {
            // 端点均在圆内部，无交点
            return null;
        }
        if (d1 == radius && d2 == radius) {
            // 两个端点均在圆上
            return new double[]{x1, y1, x2, y2};
        }
        if (d1 == radius) {
            // 起点在圆上
            return new double[]{x1, y1};
        }
        if (d2 == radius) {
            // 终点在圆上
            return new double[]{x2, y2};
        }
        // 一个内一个在外，或者两个端点都在外部
        final double[] is = calculateIntersectionLineToCircle(x1, y1, x2, y2, cx, cy, radius);
        if (is == null) {
            return null;
        }
        double left = x1, top = y1, right = x1, bottom = y1;
        left = Math.min(left, x2);
        top = Math.min(top, y2);
        right = Math.max(right, x2);
        bottom = Math.max(bottom, y2);
        if (is.length == 2) {
            final double ix = is[0];
            final double iy = is[1];
            if (isIntersectPointToRect(ix, iy, left, top, right, bottom)) {
                return is;
            }
            return null;
        } else if (is.length == 4) {
            final double i1x = is[0];
            final double i1y = is[1];
            final double i2x = is[2];
            final double i2y = is[3];
            double[] i1 = null, i2 = null;
            if (isIntersectPointToRect(i1x, i1y, left, top, right, bottom)) {
                i1 = new double[]{i1x, i1y};
            }
            if (isIntersectPointToRect(i2x, i2y, left, top, right, bottom)) {
                i2 = new double[]{i2x, i2y};
            }
            if (i1 == null && i2 == null) {
                return null;
            }
            if (i1 != null && i2 == null) {
                return i1;
            }
            if (i1 == null) {
                return i2;
            }
            // 因为线段有方向
            final double dis1 = DistanceFormulas.calculatePointToPoint(x1, y1, i1[0], i1[1]);
            final double dis2 = DistanceFormulas.calculatePointToPoint(x1, y1, i2[0], i2[1]);
            if (dis1 < dis2) {
                return new double[]{i1[0], i1[1], i2[0], i2[1]};
            } else {
                return new double[]{i2[0], i2[1], i1[0], i1[1]};
            }
        }
        return null;
    }

    /**
     * 计算两条直线交点
     *
     * @param x1 直线1经过的点1的X轴坐标
     * @param y1 直线1经过的点1的Y轴坐标
     * @param x2 直线1经过的点2的X轴坐标
     * @param y2 直线1经过的点2的Y轴坐标
     * @param x3 直线2经过的点3的X轴坐标
     * @param y3 直线2经过的点3的Y轴坐标
     * @param x4 直线2经过的点4的X轴坐标
     * @param y4 直线2经过的点4的Y轴坐标
     * @return 交点，null：无焦点；length为2：单个交点
     */
    @Nullable
    public static double[] calculateIntersectionLineToLine(double x1, double y1,
                                                           double x2, double y2,
                                                           double x3, double y3,
                                                           double x4, double y4) {
        final double dx1 = x1 - x2;
        final double dx2 = x3 - x4;
        if (dx1 == 0 && dx2 == 0) {
            // 均垂直于X轴，平行线不相交
            return null;
        }
        if (dx1 == 0) {
            // 一条垂直一条不垂直，相交
            final double y;
            final double k2 = (y3 - y4) / dx2;
            if (k2 == 0) {
                y = y3;
            } else {
                y = k2 * (x1 - x4) + y4;
            }
            return new double[]{x1, y};
        }
        if (dx2 == 0) {
            // 一条垂直一条不垂直，相交
            final double y;
            final double k1 = (y1 - y2) / dx1;
            if (k1 == 0) {
                y = y1;
            } else {
                y = k1 * (x3 - x2) + y2;
            }
            return new double[]{x3, y};
        }
        final double k1 = (y1 - y2) / dx1;
        final double k2 = (y3 - y4) / dx2;
        if (k1 == k2) {
            // 相同斜率，不相交
            return null;
        }
        final double x;
        final double y;
        if (k1 == 0) {
            y = y1;
            x = (y - y4) / k2 + x4;
        } else if (k2 == 0) {
            y = y3;
            x = (y - y2) / k1 + x2;
        } else {
            x = (k1 * x2 - k2 * x4 + y4 - y2) / (k1 - k2);
            y = k1 * (x - x2) + y2;
        }
        return new double[]{x, y};
    }

    /**
     * 计算两条线段交点
     *
     * @param x1 线段1起点X轴坐标
     * @param y1 线段1起点Y轴坐标
     * @param x2 线段1终点X轴坐标
     * @param y2 线段1终点Y轴坐标
     * @param x3 线段2起点X轴坐标
     * @param y3 线段2起点Y轴坐标
     * @param x4 线段2终点X轴坐标
     * @param y4 线段2终点Y轴坐标
     * @return 交点，null：无焦点；length为2：单个交点
     */
    @Nullable
    public static double[] calculateIntersectionLineSegmentToLineSegment(double x1, double y1,
                                                                         double x2, double y2,
                                                                         double x3, double y3,
                                                                         double x4, double y4) {
        final double dx1 = x1 - x2;
        final double dx2 = x3 - x4;
        if (dx1 == 0 && dx2 == 0) {
            // 均垂直于X轴，平行线不相交
            return null;
        }
        if (dx1 == 0) {
            // 一条垂直一条不垂直，相交
            final double y;
            final double k2 = (y3 - y4) / dx2;
            if (k2 == 0) {
                y = y3;
            } else {
                y = k2 * (x1 - x4) + y4;
            }
            if (isIntersectPointToLineSegment(x1, y, x1, y1, x2, y2) &&
                    isIntersectPointToLineSegment(x1, y, x3, y3, x4, y4)) {
                return new double[]{x1, y};
            } else {
                return null;
            }
        }
        if (dx2 == 0) {
            // 一条垂直一条不垂直，相交
            final double y;
            final double k1 = (y1 - y2) / dx1;
            if (k1 == 0) {
                y = y1;
            } else {
                y = k1 * (x3 - x2) + y2;
            }
            if (isIntersectPointToLineSegment(x3, y, x1, y1, x2, y2) &&
                    isIntersectPointToLineSegment(x3, y, x3, y3, x4, y4)) {
                return new double[]{x3, y};
            } else {
                return null;
            }
        }
        final double k1 = (y1 - y2) / dx1;
        final double k2 = (y3 - y4) / dx2;
        if (k1 == k2) {
            // 相同斜率，不相交
            return null;
        }
        final double x;
        final double y;
        if (k1 == 0) {
            y = y1;
            x = (y - y4) / k2 + x4;
        } else if (k2 == 0) {
            y = y3;
            x = (y - y2) / k1 + x2;
        } else {
            x = (k1 * x2 - k2 * x4 + y4 - y2) / (k1 - k2);
            y = k1 * (x - x2) + y2;
        }
        if (isIntersectPointToLineSegment(x, y, x1, y1, x2, y2) &&
                isIntersectPointToLineSegment(x, y, x3, y3, x4, y4)) {
            return new double[]{x, y};
        } else {
            return null;
        }
    }

    /**
     * 计算直线与线段交点
     *
     * @param x1 直线1经过的点1的X轴坐标
     * @param y1 直线1经过的点1的Y轴坐标
     * @param x2 直线1经过的点2的X轴坐标
     * @param y2 直线1经过的点2的Y轴坐标
     * @param x3 线段2起点X轴坐标
     * @param y3 线段2起点Y轴坐标
     * @param x4 线段2终点X轴坐标
     * @param y4 线段2终点Y轴坐标
     * @return 交点，null：无焦点；length为2：单个交点
     */
    @Nullable
    public static double[] calculateIntersectionLineToLineSegment(double x1, double y1,
                                                                  double x2, double y2,
                                                                  double x3, double y3,
                                                                  double x4, double y4) {
        final double[] p = calculateIntersectionLineToLine(x1, y1, x2, y2, x3, y3, x4, y4);
        if (p != null && isIntersectPointToLineSegment(p[0], p[1], x3, y3, x4, y4)) {
            return p;
        }
        return null;
    }
}
