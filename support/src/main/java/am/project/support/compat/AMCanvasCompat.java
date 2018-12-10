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
package am.project.support.compat;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;

import java.util.ArrayList;

/**
 * 画布兼容器
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class AMCanvasCompat {

    private static final CanvasCompatImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new Api21CanvasCompatImpl();
        } else {
            IMPL = new BaseCanvasCompatImpl();
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
    public static int saveLayer(Canvas canvas, RectF bounds, Paint paint) {
        return IMPL.saveLayer(canvas, bounds, paint);
    }

    /**
     * Convenience for {@link #saveLayer(Canvas, RectF, Paint)} that takes the four float
     * coordinates of the bounds rectangle.
     */
    public static int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                                Paint paint) {
        return IMPL.saveLayer(canvas, left, top, right, bottom, paint);
    }

    /**
     * <p>
     * Draw the specified arc, which will be scaled to fit inside the specified oval.
     * </p>
     * <p>
     * If the start angle is negative or >= 360, the start angle is treated as start angle modulo
     * 360.
     * </p>
     * <p>
     * If the sweep angle is >= 360, then the oval is drawn completely. Note that this differs
     * slightly from SkPath::arcTo, which treats the sweep angle modulo 360. If the sweep angle is
     * negative, the sweep angle is treated as sweep angle modulo 360
     * </p>
     * <p>
     * The arc is drawn clockwise. An angle of 0 degrees correspond to the geometric angle of 0
     * degrees (3 o'clock on a watch.)
     * </p>
     *
     * @param startAngle Starting angle (in degrees) where the arc begins
     * @param sweepAngle Sweep angle (in degrees) measured clockwise
     * @param useCenter  If true, include the center of the oval in the arc, and close it if it is
     *                   being stroked. This will draw a wedge
     * @param paint      The paint used to draw the arc
     */
    public static void drawArc(Canvas canvas, float left, float top, float right, float bottom,
                               float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
        IMPL.drawArc(canvas, left, top, right, bottom, startAngle, sweepAngle, useCenter, paint);
    }

    /**
     * Draw the specified oval using the specified paint. The oval will be filled or framed based on
     * the Style in the paint.
     */
    public static void drawOval(Canvas canvas, float left, float top, float right, float bottom,
                                Paint paint) {
        IMPL.drawOval(canvas, left, top, right, bottom, paint);
    }

    /**
     * Draw the specified round-rect using the specified paint. The roundrect will be filled or
     * framed based on the Style in the paint.
     *
     * @param rx    The x-radius of the oval used to round the corners
     * @param ry    The y-radius of the oval used to round the corners
     * @param paint The paint used to draw the roundRect
     */
    public static void drawRoundRect(Canvas canvas,
                                     float left, float top, float right, float bottom,
                                     float rx, float ry, Paint paint) {
        IMPL.drawRoundRect(canvas, left, top, right, bottom, rx, ry, paint);
    }

    private AMCanvasCompat() {
        //no instance
    }

    public interface CanvasCompatImpl {

        int saveLayer(Canvas canvas, RectF bounds, Paint paint);

        int saveLayer(Canvas canvas, float left, float top, float right, float bottom, Paint paint);

        void drawArc(Canvas canvas, float left, float top, float right, float bottom,
                     float startAngle, float sweepAngle, boolean useCenter, Paint paint);

        void drawOval(Canvas canvas, float left, float top, float right, float bottom, Paint paint);

        void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom,
                           float rx, float ry, Paint paint);
    }

    private static class BaseCanvasCompatImpl implements CanvasCompatImpl {

        private static final ArrayList<RectF> RECT_FS = new ArrayList<>();

        private static RectF get() {
            synchronized (RECT_FS) {
                if (RECT_FS.isEmpty())
                    return new RectF();
                else
                    return RECT_FS.remove(RECT_FS.size() - 1);
            }
        }

        private static void put(RectF rect) {
            synchronized (RECT_FS) {
                RECT_FS.add(rect);
            }
        }

        @Override
        public int saveLayer(Canvas canvas, RectF bounds, Paint paint) {
            return canvas.saveLayer(bounds, paint, Canvas.ALL_SAVE_FLAG);
        }

        @Override
        public int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            return canvas.saveLayer(left, top, right, bottom, paint, Canvas.ALL_SAVE_FLAG);
        }

        @Override
        public void drawArc(Canvas canvas, float left, float top, float right, float bottom,
                            float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
            final RectF oval = get();
            oval.set(left, top, right, bottom);
            canvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
            put(oval);
        }

        @Override
        public void drawOval(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            final RectF oval = get();
            oval.set(left, top, right, bottom);
            canvas.drawOval(oval, paint);
            put(oval);
        }

        @Override
        public void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom,
                                  float rx, float ry, Paint paint) {
            final RectF rect = get();
            rect.set(left, top, right, bottom);
            canvas.drawRoundRect(rect, rx, ry, paint);
            put(rect);
        }
    }

    @TargetApi(21)
    private static class Api21CanvasCompatImpl implements CanvasCompatImpl {

        @Override
        public int saveLayer(Canvas canvas, RectF bounds, Paint paint) {
            return canvas.saveLayer(bounds, paint);
        }

        @Override
        public int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            return canvas.saveLayer(left, top, right, bottom, paint);
        }

        @Override
        public void drawArc(Canvas canvas, float left, float top, float right, float bottom,
                            float startAngle, float sweepAngle, boolean useCenter, Paint paint) {
            canvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint);
        }

        @Override
        public void drawOval(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            canvas.drawOval(left, top, right, bottom, paint);
        }

        @Override
        public void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom,
                                  float rx, float ry, Paint paint) {
            canvas.drawRoundRect(left, top, right, bottom, rx, ry, paint);
        }
    }
}
