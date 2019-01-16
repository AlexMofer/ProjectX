/*
 * Copyright (C) 2015 AlexMofer
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

package am.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * 帮助类
 */
class DrawableHelper {

    static TypedArray obtainAttributes(Resources res, Resources.Theme theme,
                                       AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    private static int modulateAlpha(int color, int alpha) {
        return Color.alpha(color) * (alpha + (alpha >> 7)) >> 8;
    }

    static int getColor(ColorStateList color, int[] state, int alpha) {
        if (color == null)
            return Color.TRANSPARENT;
        final int c = color.getColorForState(state, color.getDefaultColor());
        return Color.argb(modulateAlpha(c, alpha), Color.red(c), Color.green(c), Color.blue(c));
    }

    static int evaluateColor(float fraction, int start, int end) {
        float startA = ((start >> 24) & 0xff) / 255.0f;
        float startR = ((start >> 16) & 0xff) / 255.0f;
        float startG = ((start >> 8) & 0xff) / 255.0f;
        float startB = (start & 0xff) / 255.0f;

        float endA = ((end >> 24) & 0xff) / 255.0f;
        float endR = ((end >> 16) & 0xff) / 255.0f;
        float endG = ((end >> 8) & 0xff) / 255.0f;
        float endB = (end & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }
}
