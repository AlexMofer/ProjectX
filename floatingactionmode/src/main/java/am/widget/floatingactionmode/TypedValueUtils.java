/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.floatingactionmode;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 尺寸工具
 * Created by Alex on 2018/10/18.
 */
final class TypedValueUtils {
    private TypedValueUtils() {
        //no instance
    }

    /**
     * Converts a complex data value holding a dimension to its final floating
     * point value.
     *
     * @param unit    The unit to convert from.
     * @param value   The value to apply the unit to.
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    @SuppressWarnings("WeakerAccess")
    static float complexToDimension(int unit, float value, DisplayMetrics metrics) {
        return TypedValue.applyDimension(unit, value, metrics);
    }

    static float complexToDimension(float value, DisplayMetrics metrics) {
        return complexToDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }

    /**
     * Converts a complex data value holding a dimension to its final value
     * as an integer pixel offset.  This is the same as
     * {@link #complexToDimension}, except the raw floating point value is
     * truncated to an integer (pixel) value.
     *
     * @param unit    The unit to convert from.
     * @param value   The value to apply the unit to.
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     * @return The number of pixels specified by the data and its desired
     * multiplier and units.
     */
    @SuppressWarnings("WeakerAccess")
    static int complexToDimensionPixelOffset(@SuppressWarnings("SameParameterValue") int unit,
                                             float value, DisplayMetrics metrics) {
        return (int) TypedValue.applyDimension(unit, value, metrics);
    }

    static int complexToDimensionPixelOffset(float value, DisplayMetrics metrics) {
        return complexToDimensionPixelOffset(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }

    /**
     * Converts a complex data value holding a dimension to its final value
     * as an integer pixel size.  This is the same as
     * {@link #complexToDimension}, except the raw floating point value is
     * converted to an integer (pixel) value for use as a size.  A size
     * conversion involves rounding the base value, and ensuring that a
     * non-zero base value is at least one pixel in size.
     *
     * @param unit    The unit to convert from.
     * @param value   The value to apply the unit to.
     * @param metrics Current display metrics to use in the conversion --
     *                supplies display density and scaling information.
     * @return The number of pixels specified by the data and its desired
     * multiplier and units.
     */
    @SuppressWarnings("WeakerAccess")
    static int complexToDimensionPixelSize(@SuppressWarnings("SameParameterValue") int unit,
                                           float value, DisplayMetrics metrics) {
        final float f = TypedValue.applyDimension(unit, value, metrics);
        final int res = (int) ((f >= 0) ? (f + 0.5f) : (f - 0.5f));
        if (res != 0) return res;
        if (value == 0) return 0;
        if (value > 0) return 1;
        return -1;
    }

    @SuppressWarnings("unused")
    static int complexToDimensionPixelSize(float value, DisplayMetrics metrics) {
        return complexToDimensionPixelSize(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
    }
}
