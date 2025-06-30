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

import android.util.DisplayMetrics;

import androidx.core.util.TypedValueCompat;

import org.jspecify.annotations.NonNull;

/**
 * TypedValue 工具
 * Created by Alex on 2025/6/30.
 */
public class TypedValueUtils {

    private TypedValueUtils() {
        //no instance
    }

    /**
     * Retrieve a dimensional for a particular resource ID for use
     * as an offset in raw pixels.  This is the same as
     * {@link android.util.TypedValue#getDimension}, except the returned value is converted to
     * integer pixels for you.  An offset conversion involves simply
     * truncating the base value to an integer.
     *
     * @see android.util.TypedValue#getDimension
     * @see #getDimensionPixelSize
     */
    public static int getDimensionPixelOffset(float dpValue, @NonNull DisplayMetrics metrics) {
        return (int) TypedValueCompat.dpToPx(dpValue, metrics);
    }

    /**
     * Retrieve a dimensional for a particular resource ID for use
     * as a size in raw pixels.  This is the same as
     * {@link android.util.TypedValue#getDimension}, except the returned value is converted to
     * integer pixels for use as a size.  A size conversion involves
     * rounding the base value, and ensuring that a non-zero base value
     * is at least one pixel in size.
     *
     * @see android.util.TypedValue#getDimension
     * @see #getDimensionPixelOffset
     */
    public static int getDimensionPixelSize(float dpValue, @NonNull DisplayMetrics metrics) {
        final float f = TypedValueCompat.dpToPx(dpValue, metrics);
        final int res = (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
        if (res != 0) return res;
        if (dpValue == 0) return 0;
        if (dpValue > 0) return 1;
        return -1;
    }
}
