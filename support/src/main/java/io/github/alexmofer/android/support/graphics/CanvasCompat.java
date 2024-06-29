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
package io.github.alexmofer.android.support.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 画布兼容器
 * Created by Alex on 2022/10/18.
 */
public class CanvasCompat {

    private CanvasCompat() {
        //no instance
    }

    /**
     * Convenience for {@link Canvas#saveLayer(RectF, Paint)} that takes the four float coordinates of the
     * bounds rectangle.
     */
    public static int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                                @Nullable Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayer(left, top, right, bottom, paint);
        } else {
            //noinspection deprecation
            return canvas.saveLayer(left, top, right, bottom, paint, Canvas.ALL_SAVE_FLAG);
        }
    }

    /**
     * This behaves the same as save(), but in addition it allocates and
     * redirects drawing to an offscreen rendering target.
     * <p class="note"><strong>Note:</strong> this method is very expensive,
     * incurring more than double rendering cost for contained content. Avoid
     * using this method when possible and instead use a
     * {@link android.view.View#LAYER_TYPE_HARDWARE hardware layer} on a View
     * to apply an xfermode, color filter, or alpha, as it will perform much
     * better than this method.
     * <p>
     * All drawing calls are directed to a newly allocated offscreen rendering target.
     * Only when the balancing call to restore() is made, is that offscreen
     * buffer drawn back to the current target of the Canvas (which can potentially be a previous
     * layer if these calls are nested).
     * <p>
     * Attributes of the Paint - {@link Paint#getAlpha() alpha},
     * {@link Paint#getXfermode() Xfermode}, and
     * {@link Paint#getColorFilter() ColorFilter} are applied when the
     * offscreen rendering target is drawn back when restore() is called.
     *
     * @param bounds May be null. The maximum size the offscreen render target
     *               needs to be (in local coordinates)
     * @param paint  This is copied, and is applied to the offscreen when
     *               restore() is called.
     * @return value to pass to restoreToCount() to balance this save()
     */
    public static int saveLayer(Canvas canvas, @Nullable RectF bounds, @Nullable Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayer(bounds, paint);
        } else {
            return canvas.saveLayer(bounds, paint, Canvas.ALL_SAVE_FLAG);
        }
    }

    /**
     * Convenience for {@link Canvas#saveLayer(RectF, Paint)} but instead of taking a entire Paint
     * object it takes only the {@code alpha} parameter.
     */
    public static int saveLayerAlpha(@NonNull Canvas canvas, @Nullable RectF bounds, int alpha) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayerAlpha(bounds, alpha);
        } else {
            return canvas.saveLayerAlpha(bounds, alpha, Canvas.ALL_SAVE_FLAG);
        }
    }

    /**
     * Convenience for {@link #saveLayerAlpha(Canvas, RectF, int)} that takes the four float
     * coordinates of the bounds rectangle.
     */
    public static int saveLayerAlpha(
            @NonNull Canvas canvas, float left, float top, float right, float bottom, int alpha) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            return canvas.saveLayerAlpha(left, top, right, bottom, alpha);
        } else {
            return canvas.saveLayerAlpha(left, top, right, bottom, alpha, Canvas.ALL_SAVE_FLAG);
        }
    }
}
