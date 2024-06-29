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
 * 弧度公式
 * Created by Alex on 2022/11/29.
 */
public class DegreesFormulas {

    private DegreesFormulas() {
        //no instance
    }

    // 线段与X轴正方向顺时针形成的夹角角度（0~360）
    private static double calculateXPositiveCW(double x, double y) {
        if (y == 0) {
            if (x >= 0)
                return 0;
            return 180;
        }
        if (x == 0) {
            if (y > 0)
                return 90;
            return 270;
        }
        if (x > 0 && y > 0) {
            // 右下
            if (x == y)
                return 45;
            return Math.toDegrees(Math.atan(y / x));
        }
        if (x < 0 && y > 0) {
            // 左下
            if (-x == y)
                return 135;

            return Math.toDegrees(Math.PI - Math.atan(-y / x));
        }
        if (x < 0 && y < 0) {
            // 左上
            if (x == y)
                return 225;
            return Math.toDegrees(Math.PI + Math.atan(y / x));
        }
        if (x > 0 && y < 0) {
            // 右上
            if (x == -y)
                return 315;
            return Math.toDegrees(Math.PI * 2 - Math.atan(-y / x));
        }
        return 0;
    }

    // 线段与X轴正方向逆时针形成的夹角角度（0~360）
    private static double calculateXPositiveCCW(double x, double y) {
        if (y == 0) {
            if (x >= 0)
                return 0;
            return 180;
        }
        if (x == 0) {
            if (y > 0)
                return 270;
            return 90;
        }
        if (x > 0 && y < 0) {
            // 右上
            if (x == -y)
                return 45;
            return Math.toDegrees(Math.atan(-y / x));
        }
        if (x < 0 && y < 0) {
            // 左上
            if (x == y)
                return 135;
            return Math.toDegrees(Math.PI - Math.atan(y / x));
        }
        if (x < 0 && y > 0) {
            // 左下
            if (-x == y)
                return 225;

            return Math.toDegrees(Math.PI + Math.atan(-y / x));
        }
        if (x > 0 && y > 0) {
            // 右下
            if (x == y)
                return 315;
            return Math.toDegrees(Math.PI * 2 - Math.atan(y / x));
        }
        return 0;
    }

    // 线段与X轴反方向顺时针形成的夹角角度（0~360）
    private static double calculateXNegativeCW(double x, double y) {
        if (y == 0) {
            if (x <= 0)
                return 0;
            return 180;
        }
        if (x == 0) {
            if (y < 0)
                return 90;
            return 270;
        }
        if (x < 0 && y < 0) {
            // 左上
            if (x == y)
                return 45;
            return Math.toDegrees(Math.atan(y / x));
        }
        if (x > 0 && y < 0) {
            // 右上
            if (x == -y)
                return 135;
            return Math.toDegrees(Math.PI - Math.atan(-y / x));
        }
        if (x > 0 && y > 0) {
            // 右下
            if (x == y)
                return 225;
            return Math.toDegrees(Math.PI + Math.atan(y / x));
        }
        if (x < 0 && y > 0) {
            // 左下
            if (-x == y)
                return 315;
            return Math.toDegrees(Math.PI * 2 - Math.atan(-y / x));
        }
        return 0;
    }

    // 线段与X轴反方向逆时针形成的夹角角度（0~360）
    private static double calculateXNegativeCCW(double x, double y) {
        if (y == 0) {
            if (x <= 0)
                return 0;
            return 180;
        }
        if (x == 0) {
            if (y > 0)
                return 90;
            return 270;
        }
        if (x < 0 && y > 0) {
            // 左下
            if (-x == y)
                return 45;
            return Math.toDegrees(Math.atan(-y / x));
        }
        if (x > 0 && y > 0) {
            // 右下
            if (x == y)
                return 135;
            return Math.toDegrees(Math.PI - Math.atan(y / x));
        }
        if (x > 0 && y < 0) {
            // 右上
            if (x == -y)
                return 225;
            return Math.toDegrees(Math.PI + Math.atan(-y / x));
        }
        if (x < 0 && y < 0) {
            // 左上
            if (x == y)
                return 315;
            return Math.toDegrees(Math.PI * 2 - Math.atan(y / x));
        }
        return 0;
    }

    // 线段与Y轴正方向顺时针形成的夹角角度（0~360）
    private static double calculateYPositiveCW(double x, double y) {
        double degrees = 90 + calculateXPositiveCW(x, y);
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }
        return degrees;
    }

    // 线段与X轴反方向顺时针形成的夹角角度（0~360）
    private static double calculateYNegativeCW(double x, double y) {
        double degrees = 90 + calculateXNegativeCW(x, y);
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }
        return degrees;
    }

    // 线段与X轴正方向逆时针形成的夹角角度（0~360）
    private static double calculateYPositiveCCW(double x, double y) {
        double degrees = calculateXPositiveCCW(x, y) - 90;
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }
        return degrees;
    }

    // 线段与X轴反方向逆时针形成的夹角角度（0~360）
    private static double calculateYNegativeCCW(double x, double y) {
        double degrees = calculateXNegativeCCW(x, y) - 90;
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }
        return degrees;
    }

    /**
     * 计算在屏幕坐标系中，从(0, 0)到(x, y)连成线段，
     * 该线段与X或Y轴的正反方向的顺逆时针形成的夹角角度（0~360）
     *
     * @param xAxis             X轴为true，Y轴为false
     * @param positiveDirection 正方向为true，反方向为false
     * @param clockwise         顺时针为true，逆时针为false
     * @param x                 屏幕坐标系X轴坐标
     * @param y                 屏幕坐标系Y轴坐标
     * @return 夹角角度（0~360）
     */
    public static double calculate(boolean xAxis, boolean positiveDirection, boolean clockwise,
                                   double x, double y) {
        if (xAxis) {
            // X轴
            if (positiveDirection) {
                // X轴正方向
                if (clockwise) {
                    // 顺时针
                    return calculateXPositiveCW(x, y);
                } else {
                    // 逆时针
                    return calculateXPositiveCCW(x, y);
                }
            } else {
                // X轴反方向
                if (clockwise) {
                    // 顺时针
                    return calculateXNegativeCW(x, y);
                } else {
                    // 逆时针
                    return calculateXNegativeCCW(x, y);
                }
            }
        } else {
            // Y轴
            if (positiveDirection) {
                // Y轴正方向
                if (clockwise) {
                    // 顺时针
                    return calculateYPositiveCW(x, y);
                } else {
                    // 逆时针
                    return calculateYPositiveCCW(x, y);
                }
            } else {
                // Y轴反方向
                if (clockwise) {
                    // 顺时针
                    return calculateYNegativeCW(x, y);
                } else {
                    // 逆时针
                    return calculateYNegativeCCW(x, y);
                }
            }
        }
    }

    /**
     * 计算在屏幕坐标系中，从(ox, oy)到(x, y)连成线段，
     * 该线段与X或Y轴的正反方向的顺逆时针形成的夹角角度（0~360）
     *
     * @param xAxis             X轴为true，Y轴为false
     * @param positiveDirection 正方向为true，反方向为false
     * @param clockwise         顺时针为true，逆时针为false
     * @param ox                屏幕坐标系ox
     * @param oy                屏幕坐标系oy
     * @param x                 屏幕坐标系x
     * @param y                 屏幕坐标系y
     * @return 夹角角度（0~360）
     */
    public static double calculate(boolean xAxis, boolean positiveDirection, boolean clockwise,
                                   double ox, double oy, double x, double y) {
        return calculate(xAxis, positiveDirection, clockwise, x - ox, y - oy);
    }
}
