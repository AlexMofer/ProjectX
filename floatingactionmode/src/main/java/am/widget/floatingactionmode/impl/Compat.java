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
package am.widget.floatingactionmode.impl;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * 版本兼容器
 * Created by Alex on 2018/11/21.
 */
final class Compat {

    private Compat() {
        //no instance
    }

    @SuppressWarnings("SameParameterValue")
    static int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                         Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return canvas.saveLayer(left, top, right, bottom, paint);
        else
            //noinspection deprecation
            return canvas.saveLayer(left, top, right, bottom, paint, Canvas.ALL_SAVE_FLAG);
    }

    static Drawable getDrawable(Resources resources, int id, Resources.Theme theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(id, theme);
        }
        //noinspection deprecation
        return resources.getDrawable(id);
    }

    static boolean hasValueOrEmpty(TypedArray array, int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return array.hasValueOrEmpty(index);
        }
        return array.hasValue(index);
    }
}
