/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.graphics.Outline;
import android.graphics.Path;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;

/**
 * ViewOutlineProvider 工具
 * Created by Alex on 2024/3/18.
 */
public class ViewOutlineProviderUtils {

    public static final ViewOutlineProvider OVAL = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };
    public static final ViewOutlineProvider PADDED_OVAL = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getWidth() - view.getPaddingRight(),
                    view.getHeight() - view.getPaddingBottom());
        }
    };
    public static final ViewOutlineProvider CAPSULE = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            final int width = view.getWidth();
            final int height = view.getHeight();
            outline.setRoundRect(0, 0, width, height, height * 0.5f);
        }
    };
    public static final int FLAG_ROUND_RECT_LT = 1;
    public static final int FLAG_ROUND_RECT_RT = 2;
    public static final int FLAG_ROUND_RECT_LB = 4;
    public static final int FLAG_ROUND_RECT_RB = 8;

    private ViewOutlineProviderUtils() {
        //no instance
    }

    /**
     * 设置路径，Android R 以前必须为凸路径
     *
     * @param outline Outline
     * @param path    路径
     */
    public static void setPath(Outline outline, @NonNull Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path);
        } else {
            outline.setConvexPath(path);
        }
    }

    /**
     * 新建圆角矩形外边框
     *
     * @param radius 圆角半径
     * @param flags  四个角
     * @return 圆角矩形外边框
     */
    public static ViewOutlineProvider newRoundRect(float radius, int flags) {
        return new RoundRectViewOutlineProvider(radius, flags);
    }

    /**
     * 新建圆角矩形外边框
     *
     * @param radius 圆角半径
     * @return 圆角矩形外边框
     */
    public static ViewOutlineProvider newRoundRect(float radius) {
        return newRoundRect(radius,
                FLAG_ROUND_RECT_LT | FLAG_ROUND_RECT_RT | FLAG_ROUND_RECT_LB | FLAG_ROUND_RECT_RB);
    }

    private static class RoundRectViewOutlineProvider extends ViewOutlineProvider {

        private final float mRadius;
        private final int mFlags;
        private final float[] mRadii = new float[8];
        private final Path mPath;

        public RoundRectViewOutlineProvider(float radius, int flags) {
            mRadius = radius;
            mFlags = flags;
            Path path = null;
            if (flags != (FLAG_ROUND_RECT_LT | FLAG_ROUND_RECT_RT | FLAG_ROUND_RECT_LB | FLAG_ROUND_RECT_RB)) {
                if ((flags & FLAG_ROUND_RECT_LT) == FLAG_ROUND_RECT_LT) {
                    path = new Path();
                    mRadii[0] = mRadius;
                    mRadii[1] = mRadius;
                }
                if ((flags & FLAG_ROUND_RECT_RT) == FLAG_ROUND_RECT_RT) {
                    path = new Path();
                    mRadii[2] = mRadius;
                    mRadii[3] = mRadius;
                }
                if ((flags & FLAG_ROUND_RECT_LB) == FLAG_ROUND_RECT_LB) {
                    path = new Path();
                    mRadii[6] = mRadius;
                    mRadii[7] = mRadius;
                }
                if ((flags & FLAG_ROUND_RECT_RB) == FLAG_ROUND_RECT_RB) {
                    path = new Path();
                    mRadii[4] = mRadius;
                    mRadii[5] = mRadius;
                }
            }
            mPath = path;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            if (mFlags == 0) {
                return;
            }
            if (mFlags == (FLAG_ROUND_RECT_LT | FLAG_ROUND_RECT_RT | FLAG_ROUND_RECT_LB | FLAG_ROUND_RECT_RB)) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadius);
                return;
            }
            if (mPath == null) {
                return;
            }
            mPath.rewind();
            mPath.addRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadii,
                    Path.Direction.CW);
            setPath(outline, mPath);
        }
    }
}
