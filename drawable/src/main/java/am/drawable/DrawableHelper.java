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
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * 帮助类
 */
class DrawableHelper {

    static int getAlpha(ColorStateList color, int[] state) {
        if (color == null)
            return 0;
        return Color.alpha(color.getColorForState(state, color.getDefaultColor()));
    }

    static int modulateAlpha(int color, int alpha) {
        return Color.alpha(color) * (alpha + (alpha >> 7)) >> 8;
    }

    static Rect PADDING = new Rect();

    /**
     * 刷新回调布局
     *
     * @param drawable Drawable
     */
    static void requestCallbackLayout(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (drawable.getCallback() != null && drawable.getCallback() instanceof View) {
                View view = (View) drawable.getCallback();
                view.requestLayout();
            }
        }
    }

    /**
     * 刷新Padding
     *
     * @param drawable Drawable
     */
    static void invalidateCallbackPadding(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (drawable.getCallback() != null && drawable.getCallback() instanceof View) {
                View view = (View) drawable.getCallback();
                drawable.getPadding(PADDING);
                view.setPadding(PADDING.left, PADDING.top, PADDING.right, PADDING.bottom);
            }
        }
    }
}
