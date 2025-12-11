/*
 * Copyright (C) 2025 AlexMofer
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

/**
 * Math 工具
 * Created by Alex on 2025/6/25.
 */
public class MathUtils {

    private MathUtils() {
        //no instance
    }

    public static int max(int... values) {
        int max = values[0];
        for (int value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static int min(int... values) {
        int min = values[0];
        for (int value : values) {
            min = Math.min(min, value);
        }
        return min;
    }

    public static long max(long... values) {
        long max = values[0];
        for (long value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static long min(long... values) {
        long min = values[0];
        for (long value : values) {
            min = Math.min(min, value);
        }
        return min;
    }

    public static float max(float... values) {
        float max = values[0];
        for (float value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static float min(float... values) {
        float min = values[0];
        for (float value : values) {
            min = Math.min(min, value);
        }
        return min;
    }

    public static double max(double... values) {
        double max = values[0];
        for (double value : values) {
            max = Math.max(max, value);
        }
        return max;
    }

    public static double min(double... values) {
        double min = values[0];
        for (double value : values) {
            min = Math.min(min, value);
        }
        return min;
    }
}
