/*
 * Copyright (C) 2017 AlexMofer
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

package am.widget.scrollbarrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

/**
 * 工具
 * Created by Alex on 2017/11/2.
 */

class Utils {

    static boolean hasValueOrEmpty(TypedArray ta, int index) {
        if (Build.VERSION.SDK_INT >= 22) {
            return ta.hasValueOrEmpty(index);
        } else {
            return ta.hasValue(index);
        }
    }

    static Drawable getDefaultBackground(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable background = new GradientDrawable();
        background.setSize((int) (8 * density), (int) (8 * density));
        background.setColor(0x40000000);
        return background;
    }

    static Drawable getDefaultHorizontalSlider(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable background = new GradientDrawable();
        background.setSize((int) (24 * density), (int) (8 * density));
        background.setColor(0x80000000);
        return background;
    }

    static Drawable getDefaultVerticalSlider(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable background = new GradientDrawable();
        background.setSize((int) (8 * density), (int) (24 * density));
        background.setColor(0x80000000);
        return background;
    }

    static Drawable getDefaultHorizontalIndicator(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable background = new GradientDrawable();
        background.setSize((int) (54 * density), (int) (54 * density));
        final float radius = 27 * density;
        background.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0,
                radius, radius});
        background.setColor(0xFF4859D2);
        return background;
    }

    static Drawable getDefaultVerticalIndicator(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        GradientDrawable background = new GradientDrawable();
        background.setSize((int) (54 * density), (int) (54 * density));
        final float radius = 27 * density;
        background.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0,
                radius, radius});
        background.setColor(0xFF4859D2);
        return background;
    }
}
