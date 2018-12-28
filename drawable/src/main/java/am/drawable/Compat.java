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
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;

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

    private static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new MarshmallowCompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    static int getColor(Context context, int id) {
        return IMPL.getColor(context, id);
    }

    private interface CompatPlusImpl {
        int getColor(Context context, int id);
    }

    private static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        public int getColor(Context context, int id) {
            return context.getResources().getColor(id);
        }
    }

    @TargetApi(23)
    private static class MarshmallowCompatPlusImpl extends BaseCompatPlusImpl {
        @Override
        public int getColor(Context context, int id) {
            return context.getColor(id);
        }
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
}
