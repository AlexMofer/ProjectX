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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;

import java.util.ArrayList;

/**
 * 版本兼容控制器
 */
@SuppressWarnings("unused")
class Compat {
    /**
     * Raw bit controlling whether the layout direction is relative or not (START/END instead of
     * absolute LEFT/RIGHT).
     */
    @SuppressWarnings("WeakerAccess")
    static final int RELATIVE_LAYOUT_DIRECTION = 0x00800000;

    /**
     * Push object to x-axis position at the start of its container, not changing its size.
     */
    @SuppressLint("RtlHardcoded")
    static final int START = RELATIVE_LAYOUT_DIRECTION | Gravity.LEFT;

    /**
     * Push object to x-axis position at the end of its container, not changing its size.
     */
    @SuppressLint("RtlHardcoded")
    static final int END = RELATIVE_LAYOUT_DIRECTION | Gravity.RIGHT;

    private static final int LAYOUT_DIRECTION_LTR = 0;
    private static final CompatImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new Api21CompatImpl();
        } else {
            IMPL = new BaseCompatImpl();
        }
    }

    @SuppressWarnings("SameParameterValue")
    static int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                         Paint paint) {
        return IMPL.saveLayer(canvas, left, top, right, bottom, paint);
    }

    static void arcTo(Path path, float left, float top, float right, float bottom,
                      float startAngle, float sweepAngle, boolean forceMoveTo) {
        IMPL.arcTo(path, left, top, right, bottom, startAngle, sweepAngle, forceMoveTo);
    }

    static void addOval(Path path, float left, float top, float right, float bottom,
                        Path.Direction dir) {
        IMPL.addOval(path, left, top, right, bottom, dir);
    }

    static void addArc(Path path, float left, float top, float right, float bottom,
                       float startAngle, float sweepAngle) {
        IMPL.addArc(path, left, top, right, bottom, startAngle, sweepAngle);
    }

    static void addRoundRect(Path path, float left, float top, float right, float bottom,
                             float rx, float ry, Path.Direction dir) {
        IMPL.addRoundRect(path, left, top, right, bottom, rx, ry, dir);
    }

    static void addRoundRect(Path path, float left, float top, float right, float bottom,
                             float[] radii, Path.Direction dir) {
        IMPL.addRoundRect(path, left, top, right, bottom, radii, dir);
    }

    static void apply(Drawable drawable, int gravity, int w, int h, Rect container, Rect outRect) {
        if (drawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            Gravity.apply(gravity, w, h, container, outRect, drawable.getLayoutDirection());
        else
            Gravity.apply(gravity, w, h, container, outRect);
    }

    static void apply(Drawable drawable, int gravity, float w, float h,
                      RectF container, RectF outRect) {
        if (drawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gravity = Gravity.getAbsoluteGravity(gravity, drawable.getLayoutDirection());
            apply(gravity, w, h, container, 0, 0, outRect);
        } else
            apply(gravity, w, h, container, 0, 0, outRect);
    }

    static void apply(Drawable drawable, int gravity, float w, float h,
                      Rect container, RectF outRect) {
        if (drawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            gravity = Gravity.getAbsoluteGravity(gravity, drawable.getLayoutDirection());
            apply(gravity, w, h, container, 0, 0, outRect);
        } else
            apply(gravity, w, h, container, 0, 0, outRect);
    }

    @SuppressWarnings("SameParameterValue")
    private static void apply(int gravity, float w, float h, Rect container,
                              float xAdj, float yAdj, RectF outRect) {
        switch (gravity & ((Gravity.AXIS_PULL_BEFORE | Gravity.AXIS_PULL_AFTER) <<
                Gravity.AXIS_X_SHIFT)) {
            case 0:
                outRect.left = container.left
                        + ((container.right - container.left - w) * 0.5f) + xAdj;
                outRect.right = outRect.left + w;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT)) {
                    if (outRect.left < container.left) {
                        outRect.left = container.left;
                    }
                    if (outRect.right > container.right) {
                        outRect.right = container.right;
                    }
                }
                break;
            case Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_X_SHIFT:
                outRect.left = container.left + xAdj;
                outRect.right = outRect.left + w;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT)) {
                    if (outRect.right > container.right) {
                        outRect.right = container.right;
                    }
                }
                break;
            case Gravity.AXIS_PULL_AFTER << Gravity.AXIS_X_SHIFT:
                outRect.right = container.right - xAdj;
                outRect.left = outRect.right - w;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT)) {
                    if (outRect.left < container.left) {
                        outRect.left = container.left;
                    }
                }
                break;
            default:
                outRect.left = container.left + xAdj;
                outRect.right = container.right + xAdj;
                break;
        }

        switch (gravity & ((Gravity.AXIS_PULL_BEFORE | Gravity.AXIS_PULL_AFTER) <<
                Gravity.AXIS_Y_SHIFT)) {
            case 0:
                outRect.top = container.top
                        + ((container.bottom - container.top - h) * 0.5f) + yAdj;
                outRect.bottom = outRect.top + h;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT)) {
                    if (outRect.top < container.top) {
                        outRect.top = container.top;
                    }
                    if (outRect.bottom > container.bottom) {
                        outRect.bottom = container.bottom;
                    }
                }
                break;
            case Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_Y_SHIFT:
                outRect.top = container.top + yAdj;
                outRect.bottom = outRect.top + h;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT)) {
                    if (outRect.bottom > container.bottom) {
                        outRect.bottom = container.bottom;
                    }
                }
                break;
            case Gravity.AXIS_PULL_AFTER << Gravity.AXIS_Y_SHIFT:
                outRect.bottom = container.bottom - yAdj;
                outRect.top = outRect.bottom - h;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT)) {
                    if (outRect.top < container.top) {
                        outRect.top = container.top;
                    }
                }
                break;
            default:
                outRect.top = container.top + yAdj;
                outRect.bottom = container.bottom + yAdj;
                break;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void apply(int gravity, float w, float h, RectF container,
                              float xAdj, float yAdj, RectF outRect) {
        switch (gravity & ((Gravity.AXIS_PULL_BEFORE | Gravity.AXIS_PULL_AFTER) <<
                Gravity.AXIS_X_SHIFT)) {
            case 0:
                outRect.left = container.left
                        + ((container.right - container.left - w) * 0.5f) + xAdj;
                outRect.right = outRect.left + w;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT)) {
                    if (outRect.left < container.left) {
                        outRect.left = container.left;
                    }
                    if (outRect.right > container.right) {
                        outRect.right = container.right;
                    }
                }
                break;
            case Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_X_SHIFT:
                outRect.left = container.left + xAdj;
                outRect.right = outRect.left + w;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT)) {
                    if (outRect.right > container.right) {
                        outRect.right = container.right;
                    }
                }
                break;
            case Gravity.AXIS_PULL_AFTER << Gravity.AXIS_X_SHIFT:
                outRect.right = container.right - xAdj;
                outRect.left = outRect.right - w;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_X_SHIFT)) {
                    if (outRect.left < container.left) {
                        outRect.left = container.left;
                    }
                }
                break;
            default:
                outRect.left = container.left + xAdj;
                outRect.right = container.right + xAdj;
                break;
        }

        switch (gravity & ((Gravity.AXIS_PULL_BEFORE | Gravity.AXIS_PULL_AFTER) <<
                Gravity.AXIS_Y_SHIFT)) {
            case 0:
                outRect.top = container.top
                        + ((container.bottom - container.top - h) * 0.5f) + yAdj;
                outRect.bottom = outRect.top + h;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT)) {
                    if (outRect.top < container.top) {
                        outRect.top = container.top;
                    }
                    if (outRect.bottom > container.bottom) {
                        outRect.bottom = container.bottom;
                    }
                }
                break;
            case Gravity.AXIS_PULL_BEFORE << Gravity.AXIS_Y_SHIFT:
                outRect.top = container.top + yAdj;
                outRect.bottom = outRect.top + h;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT)) {
                    if (outRect.bottom > container.bottom) {
                        outRect.bottom = container.bottom;
                    }
                }
                break;
            case Gravity.AXIS_PULL_AFTER << Gravity.AXIS_Y_SHIFT:
                outRect.bottom = container.bottom - yAdj;
                outRect.top = outRect.bottom - h;
                if ((gravity & (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT))
                        == (Gravity.AXIS_CLIP << Gravity.AXIS_Y_SHIFT)) {
                    if (outRect.top < container.top) {
                        outRect.top = container.top;
                    }
                }
                break;
            default:
                outRect.top = container.top + yAdj;
                outRect.bottom = container.bottom + yAdj;
                break;
        }
    }

    private Compat() {
        //no instance
    }

    public interface CompatImpl {

        int saveLayer(Canvas canvas, float left, float top, float right, float bottom, Paint paint);

        void arcTo(Path path, float left, float top, float right, float bottom,
                   float startAngle, float sweepAngle, boolean forceMoveTo);

        void addOval(Path path, float left, float top, float right, float bottom,
                     Path.Direction dir);

        void addArc(Path path, float left, float top, float right, float bottom,
                    float startAngle, float sweepAngle);

        void addRoundRect(Path path, float left, float top, float right, float bottom,
                          float rx, float ry, Path.Direction dir);

        void addRoundRect(Path path, float left, float top, float right, float bottom,
                          float[] radii, Path.Direction dir);
    }

    private static class BaseCompatImpl implements CompatImpl {

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
        public int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            return canvas.saveLayer(left, top, right, bottom, paint, Canvas.ALL_SAVE_FLAG);
        }

        @Override
        public void arcTo(Path path, float left, float top, float right, float bottom,
                          float startAngle, float sweepAngle, boolean forceMoveTo) {
            final RectF oval = get();
            oval.set(left, top, right, bottom);
            path.arcTo(oval,startAngle, sweepAngle, forceMoveTo);
            put(oval);
        }

        @Override
        public void addOval(Path path, float left, float top, float right, float bottom,
                            Path.Direction dir) {
            final RectF oval = get();
            oval.set(left, top, right, bottom);
            path.addOval(oval, dir);
            put(oval);
        }

        @Override
        public void addArc(Path path, float left, float top, float right, float bottom,
                           float startAngle, float sweepAngle) {
            final RectF oval = get();
            oval.set(left, top, right, bottom);
            path.addArc(oval, startAngle, sweepAngle);
            put(oval);
        }

        @Override
        public void addRoundRect(Path path, float left, float top, float right, float bottom,
                                 float rx, float ry, Path.Direction dir) {
            final RectF rect = get();
            rect.set(left, top, right, bottom);
            path.addRoundRect(rect, rx, ry, dir);
            put(rect);
        }

        @Override
        public void addRoundRect(Path path, float left, float top, float right, float bottom,
                                 float[] radii, Path.Direction dir) {
            final RectF rect = get();
            rect.set(left, top, right, bottom);
            path.addRoundRect(rect, radii, dir);
            put(rect);
        }
    }

    @TargetApi(21)
    private static class Api21CompatImpl implements CompatImpl {

        @Override
        public int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            return canvas.saveLayer(left, top, right, bottom, paint);
        }

        @Override
        public void arcTo(Path path, float left, float top, float right, float bottom,
                          float startAngle, float sweepAngle, boolean forceMoveTo) {
            path.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo);
        }

        @Override
        public void addOval(Path path, float left, float top, float right, float bottom,
                            Path.Direction dir) {
            path.addOval(left, top, right, bottom, dir);
        }

        @Override
        public void addArc(Path path, float left, float top, float right, float bottom,
                           float startAngle, float sweepAngle) {
            path.addArc(left, top, right, bottom, startAngle, sweepAngle);
        }

        @Override
        public void addRoundRect(Path path, float left, float top, float right, float bottom,
                                 float rx, float ry, Path.Direction dir) {
            path.addRoundRect(left, top, right, bottom, rx, ry, dir);
        }

        @Override
        public void addRoundRect(Path path, float left, float top, float right, float bottom,
                                 float[] radii, Path.Direction dir) {
            path.addRoundRect(left, top, right, bottom, radii, dir);
        }
    }
}
