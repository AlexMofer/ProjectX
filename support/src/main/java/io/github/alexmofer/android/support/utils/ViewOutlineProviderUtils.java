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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

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
    public static final ViewOutlineProvider IMAGE_CONTENT = new ImageViewContent(0);

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
     * @return 圆角矩形外边框
     */
    public static ViewOutlineProvider newRoundRect(float radius) {
        return new RoundRectViewOutlineProvider(radius);
    }

    /**
     * 新建顶部圆角矩形外边框
     *
     * @param radius 圆角半径
     * @return 顶部圆角矩形外边框
     */
    public static ViewOutlineProvider newTopRoundRect(float radius) {
        return new TopRoundRectViewOutlineProvider(radius);
    }

    /**
     * 新建底部圆角矩形外边框
     *
     * @param radius 圆角半径
     * @return 底部圆角矩形外边框
     */
    public static ViewOutlineProvider newBottomRoundRect(float radius) {
        return new BottomRoundRectViewOutlineProvider(radius);
    }

    /**
     * 新建图片圆角边框
     * @param radius 圆角半径
     * @return 图片圆角边框
     */
    public static ViewOutlineProvider newImageContentWithRadius(float radius) {
        return new ImageViewContent(radius);
    }

    private static class RoundRectViewOutlineProvider extends ViewOutlineProvider {

        private final float mRadius;

        public RoundRectViewOutlineProvider(float radius) {
            mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadius);
        }
    }

    private static class TopRoundRectViewOutlineProvider extends ViewOutlineProvider {

        private final float mRadius;
        private final Path mPath;
        private final float[] mRadii;

        public TopRoundRectViewOutlineProvider(float radius) {
            mRadius = radius;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 可对路径进行裁剪
                mPath = new Path();
                mRadii = new float[8];
                mRadii[0] = mRadius;
                mRadii[1] = mRadius;
                mRadii[2] = mRadius;
                mRadii[3] = mRadius;
            } else {
                mPath = null;
                mRadii = null;
            }
        }

        @Override
        public void getOutline(View view, Outline outline) {
            if (mPath == null || mRadii == null) {
                outline.setRoundRect(0, 0, view.getWidth(),
                        view.getHeight() + Math.round(mRadius * 2), mRadius);
            } else {
                mPath.rewind();
                mPath.addRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadii,
                        Path.Direction.CW);
                ViewOutlineProviderUtils.setPath(outline, mPath);
            }
        }
    }

    private static class BottomRoundRectViewOutlineProvider extends ViewOutlineProvider {

        private final float mRadius;
        private final Path mPath;
        private final float[] mRadii;

        public BottomRoundRectViewOutlineProvider(float radius) {
            mRadius = radius;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // 可对路径进行裁剪
                mPath = new Path();
                mRadii = new float[8];
                mRadii[6] = mRadius;
                mRadii[7] = mRadius;
                mRadii[4] = mRadius;
                mRadii[5] = mRadius;
            } else {
                mPath = null;
                mRadii = null;
            }
        }

        @Override
        public void getOutline(View view, Outline outline) {
            if (mPath == null || mRadii == null) {
                outline.setRoundRect(0, -Math.round(mRadius * 2), view.getWidth(),
                        view.getHeight(), mRadius);
            } else {
                mPath.rewind();
                mPath.addRoundRect(0, 0, view.getWidth(), view.getHeight(), mRadii,
                        Path.Direction.CW);
                ViewOutlineProviderUtils.setPath(outline, mPath);
            }
        }
    }

    public static class ImageViewContent extends ViewOutlineProvider {
        private final float mRadius;

        public ImageViewContent(float radius) {
            mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            if (view instanceof ImageView) {
                final Drawable content = ((ImageView) view).getDrawable();
                if (content != null) {
                    final float imageAspectRatio = 1f * content.getIntrinsicWidth() / content.getIntrinsicHeight();
                    final int viewWidth = view.getWidth();
                    final int viewHeight = view.getHeight();
                    final float viewAspectRatio = 1f * viewWidth / viewHeight;
                    if (imageAspectRatio >= viewAspectRatio) {
                        // 按照宽度计算
                        final int offset = Math.round((viewHeight - viewWidth / imageAspectRatio) * 0.5f);
                        getOutline((ImageView) view, outline,
                                0, offset, viewWidth, viewHeight - offset);
                    } else {
                        // 按照高度计算
                        final int offset = Math.round((viewWidth - viewHeight * imageAspectRatio) * 0.5f);
                        getOutline((ImageView) view, outline,
                                offset, 0, viewWidth - offset, viewHeight);
                    }
                }
            }
        }

        protected void getOutline(@NonNull ImageView view, @NonNull Outline outline,
                                  int imageLeft, int imageTop, int imageRight, int imageBottom) {
            if (mRadius > 0) {
                outline.setRoundRect(imageLeft, imageTop, imageRight, imageBottom, mRadius);
            } else {
                outline.setRect(imageLeft, imageTop, imageRight, imageBottom);
            }
        }
    }
}
