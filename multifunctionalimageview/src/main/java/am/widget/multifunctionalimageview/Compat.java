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

package am.widget.multifunctionalimageview;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

/**
 * 版本兼容器
 * Created by Alex on 2018/11/21.
 */
@SuppressWarnings("SameParameterValue")
final class Compat {

    private static final int LAYOUT_DIRECTION_LTR = 0;
    private static final CompatImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            IMPL = new Api21CompatImpl();
        } else {
            IMPL = new BaseCompatImpl();
        }
    }

    static int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                         Paint paint) {
        return IMPL.saveLayer(canvas, left, top, right, bottom, paint);
    }

    static void addOval(Path path, float left, float top, float right, float bottom,
                        Path.Direction dir) {
        IMPL.addOval(path, left, top, right, bottom, dir);
    }

    static void addRoundRect(Path path, float left, float top, float right, float bottom,
                                    float rx, float ry, Path.Direction dir) {
        IMPL.addRoundRect(path, left, top, right, bottom, rx, ry, dir);
    }

    static void apply(int gravity, int w, int h, Rect container,
                             Rect outRect, int layoutDirection) {
        if (SDK_INT >= 17) {
            Gravity.apply(gravity, w, h, container, outRect, layoutDirection);
        } else {
            Gravity.apply(gravity, w, h, container, outRect);
        }
    }

    static int getLayoutDirection(View view) {
        if (Build.VERSION.SDK_INT >= 17) {
            return view.getLayoutDirection();
        }
        return LAYOUT_DIRECTION_LTR;
    }

    private Compat() {
        //no instance
    }

    public interface CompatImpl {

        int saveLayer(Canvas canvas, float left, float top, float right, float bottom, Paint paint);

        void addOval(Path path, float left, float top, float right, float bottom,
                     Path.Direction dir);

        void addRoundRect(Path path, float left, float top, float right, float bottom,
                          float rx, float ry, Path.Direction dir);
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
        public void addOval(Path path, float left, float top, float right, float bottom,
                            Path.Direction dir) {
            final RectF oval = get();
            oval.set(left, top, right, bottom);
            path.addOval(oval, dir);
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
    }

    @TargetApi(21)
    private static class Api21CompatImpl implements CompatImpl {

        @Override
        public int saveLayer(Canvas canvas, float left, float top, float right, float bottom,
                             Paint paint) {
            return canvas.saveLayer(left, top, right, bottom, paint);
        }

        @Override
        public void addOval(Path path, float left, float top, float right, float bottom,
                            Path.Direction dir) {
            path.addOval(left, top, right, bottom, dir);
        }

        @Override
        public void addRoundRect(Path path, float left, float top, float right, float bottom,
                                 float rx, float ry, Path.Direction dir) {
            path.addRoundRect(left, top, right, bottom, rx, ry, dir);
        }
    }
}