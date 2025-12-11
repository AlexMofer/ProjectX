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

/**
 * 距离公式
 * Created by Alex on 2022/11/29.
 */
public class DistanceFormulas {

    private DistanceFormulas() {
        //no instance
    }

    /**
     * 计算点与点之间的距离
     *
     * @param x1 点1X轴坐标
     * @param y1 点1Y轴坐标
     * @param x2 点2X轴坐标
     * @param y2 点2Y轴坐标
     * @return 距离
     */
    public static double calculatePointToPoint(double x1, double y1, double x2, double y2) {
        final double dx = x2 - x1;
        final double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 计算点与直线之间的距离
     *
     * @param x  点X轴坐标
     * @param y  点Y轴坐标
     * @param x1 经过直线的点1X轴坐标
     * @param y1 经过直线的点1Y轴坐标
     * @param x2 经过直线的点2X轴坐标
     * @param y2 经过直线的点2Y轴坐标
     * @return 距离
     */
    public static double calculatePointToLine(double x, double y,
                                              double x1, double y1, double x2, double y2) {
        return Math.abs((y2 - y1) * x + (x1 - x2) * y + ((x2 * y1) - (x1 * y2)))
                / (Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x1 - x2, 2)));
    }

    /**
     * 计算点与线段之间的距离
     *
     * @param x  点X轴坐标
     * @param y  点Y轴坐标
     * @param x1 线段起点X轴坐标
     * @param y1 线段起点Y轴坐标
     * @param x2 线段终点X轴坐标
     * @param y2 线段终点Y轴坐标
     * @return 距离
     */
    public static double calculatePointToLineSegment(double x, double y,
                                                     double x1, double y1, double x2, double y2) {
        final double cross = (x2 - x1) * (x - x1) + (y2 - y1) * (y - y1);
        if (cross <= 0) {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        }
        final double d2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        if (cross >= d2) {
            return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        }
        final double r = cross / d2;
        final double px = x1 + (x2 - x1) * r;
        final double py = y1 + (y2 - y1) * r;
        return Math.sqrt((x - px) * (x - px) + (py - y) * (py - y));
    }

    /**
     * 计算两条线段之间的距离
     *
     * @param x1 线段1起点X轴坐标
     * @param y1 线段1起点Y轴坐标
     * @param x2 线段1终点X轴坐标
     * @param y2 线段1终点Y轴坐标
     * @param x3 线段2起点X轴坐标
     * @param y3 线段2起点Y轴坐标
     * @param x4 线段2终点X轴坐标
     * @param y4 线段2终点Y轴坐标
     * @return 距离
     */
    public static double calculateLineSegmentToLineSegment(double x1, double y1,
                                                           double x2, double y2,
                                                           double x3, double y3,
                                                           double x4, double y4) {
        final double dx1 = x1 - x2;
        final double dx2 = x3 - x4;
        if (dx1 == 0 && dx2 == 0) {
            // 均垂直于X轴，平行线不相交
            return calculatePointToLineSegment(x1, y1, x3, y3, x4, y4);
        }
        if (dx1 == 0 || dx2 == 0) {
            // 一条垂直一条不垂直，相交
            final double[] i = IntersectFormulas.calculateIntersectionLineSegmentToLineSegment(
                    x1, y1, x2, y2, x3, y3, x4, y4);
            if (i != null) {
                // 有交点
                return 0;
            } else {
                // 无交点
                return Math.min(calculatePointToLineSegment(x1, y1, x3, y3, x4, y4),
                        calculatePointToLineSegment(x2, y2, x3, y3, x4, y4));
            }
        }
        final double k1 = (y1 - y2) / dx1;
        final double k2 = (y3 - y4) / dx2;
        if (k1 == k2) {
            // 相同斜率，平行线不相交
            return calculatePointToLineSegment(x1, y1, x3, y3, x4, y4);
        }
        final double[] i = IntersectFormulas.calculateIntersectionLineSegmentToLineSegment(
                x1, y1, x2, y2, x3, y3, x4, y4);
        if (i != null) {
            // 有交点
            return 0;
        } else {
            // 无交点
            return Math.min(calculatePointToLineSegment(x1, y1, x3, y3, x4, y4),
                    calculatePointToLineSegment(x2, y2, x3, y3, x4, y4));
        }
    }

    /**
     * 计算移动偏移
     *
     * @param distance 偏移计算结果
     * @param downX    按下X轴坐标
     * @param downY    按下Y轴坐标
     * @param x        当前X轴坐标
     * @param y        当前Y轴坐标
     * @param limited  是否限定的
     */
    public static void calculateMove(float[] distance,
                                     float downX, float downY, float x, float y,
                                     boolean limited) {
        if (limited) {
            if (x == downX && y == downY) {
                distance[0] = 0;
                distance[1] = 0;
            } else if (x == downX) {
                distance[0] = 0;
                distance[1] = y - downY;
            } else if (y == downY) {
                distance[0] = x - downX;
                distance[1] = 0;
            } else {
                final float dx = x - downX;
                final float dy = y - downY;
                if (x > downX) {
                    if (y > downY) {
                        // 右下
                        if (dy < dx * 0.5f) {
                            distance[0] = dx;
                            distance[1] = 0;
                        } else if (dx < dy * 0.5f) {
                            distance[0] = 0;
                            distance[1] = dy;
                        } else {
                            distance[0] = distance[1] = Math.min(dx, dy);
                        }
                    } else {
                        // 右上
                        if (-dy < dx * 0.5f) {
                            distance[0] = dx;
                            distance[1] = 0;
                        } else if (dx < -dy * 0.5f) {
                            distance[0] = 0;
                            distance[1] = dy;
                        } else {
                            if (dx < -dy) {
                                distance[0] = dx;
                                distance[1] = -dx;
                            } else {
                                distance[0] = -dy;
                                distance[1] = dy;
                            }
                        }
                    }
                } else {
                    if (y > downY) {
                        // 左下
                        if (dy < -dx * 0.5f) {
                            distance[0] = dx;
                            distance[1] = 0;
                        } else if (-dx < dy * 0.5f) {
                            distance[0] = 0;
                            distance[1] = dy;
                        } else {
                            if (-dx < dy) {
                                distance[0] = dx;
                                distance[1] = -dx;
                            } else {
                                distance[0] = -dy;
                                distance[1] = dy;
                            }
                        }
                    } else {
                        // 左上
                        if (-dy < -dx * 0.5f) {
                            distance[0] = dx;
                            distance[1] = 0;
                        } else if (-dx < -dy * 0.5f) {
                            distance[0] = 0;
                            distance[1] = dy;
                        } else {
                            distance[0] = distance[1] = Math.max(dx, dy);
                        }
                    }
                }
            }
        } else {
            distance[0] = x - downX;
            distance[1] = y - downY;
        }
    }
}
