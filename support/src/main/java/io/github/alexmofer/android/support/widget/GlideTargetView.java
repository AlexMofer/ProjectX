/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.EmptySuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * 基础的 Glide 目标视图
 * Created by Alex on 2026/11/4.
 */
public abstract class GlideTargetView extends View {
    private final Target mTarget = new Target(this);
    private Drawable mImage;

    public GlideTargetView(@NonNull Context context) {
        super(context);
    }

    /**
     * 将图片加载到目标视图中
     *
     * @param builder 图片加载构建器
     */
    public final void into(@NonNull RequestBuilder<Drawable> builder) {
        builder.into(mTarget);
    }

    private void setImage(@Nullable Drawable image) {
        mImage = image;
        onImageChanged(mImage);
    }

    /**
     * 图片改变
     *
     * @param image 新的图片
     */
    protected void onImageChanged(@Nullable Drawable image) {
        invalidate();
    }

    /**
     * 获取图片
     *
     * @return 图片
     */
    @Nullable
    protected Drawable getImage() {
        return mImage;
    }

    private static class Target extends CustomViewTarget<GlideTargetView, Drawable> {

        public Target(@NonNull GlideTargetView view) {
            super(view);
        }

        @Override
        protected void onResourceCleared(@Nullable Drawable placeholder) {
            getView().setImage(placeholder);
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            getView().setImage(errorDrawable);
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            getView().setImage(resource);
        }
    }

    public abstract static class CenterInside extends GlideTargetView {
        private boolean mImageBoundsCalculated = false;

        public CenterInside(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mImageBoundsCalculated = false;
        }

        @Override
        protected void onImageChanged(@Nullable Drawable image) {
            mImageBoundsCalculated = false;
            super.onImageChanged(image);
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            super.onDraw(canvas);
            final Drawable image = getImage();
            if (image == null) {
                onDrawImage(canvas, null);
                return;
            }
            calculateImageBounds(image);
            onDrawImage(canvas, image);
        }

        protected void onDrawImage(@NonNull Canvas canvas, @Nullable Drawable image) {
            if (image != null) {
                image.draw(canvas);
            }
        }

        private void calculateImageBounds(@NonNull Drawable image) {
            if (mImageBoundsCalculated) {
                return;
            }
            mImageBoundsCalculated = true;
            final int maxLeft = getPaddingLeft();
            final int maxTop = getPaddingTop();
            final int maxRight = getWidth() - getPaddingRight();
            final int maxBottom = getHeight() - getPaddingBottom();
            final int maxWidth = maxRight - maxLeft;
            final int maxHeight = maxBottom - maxTop;
            final int imageWidth = image.getIntrinsicWidth();
            final int imageHeight = image.getIntrinsicHeight();
            final int imageLeft;
            final int imageTop;
            final int imageRight;
            final int imageBottom;
            if (imageWidth <= maxWidth && imageHeight <= maxHeight) {
                imageLeft = Math.round(maxLeft + (maxWidth - imageWidth) * 0.5f);
                imageTop = Math.round(maxTop + (maxHeight - imageHeight) * 0.5f);
                imageRight = imageLeft + imageWidth;
                imageBottom = imageTop + imageHeight;
            } else {
                final float scale = Math.min((float) maxWidth / imageWidth, (float) maxHeight / imageHeight);
                final int scaledWidth = Math.round(imageWidth * scale);
                final int scaledHeight = Math.round(imageHeight * scale);
                imageLeft = Math.round(maxLeft + (maxWidth - scaledWidth) * 0.5f);
                imageTop = Math.round(maxTop + (maxHeight - scaledHeight) * 0.5f);
                imageRight = imageLeft + scaledWidth;
                imageBottom = imageTop + scaledHeight;
            }
            image.setBounds(imageLeft, imageTop, imageRight, imageBottom);
            onImageBoundsChanged(image);
        }

        @EmptySuper
        protected void onImageBoundsChanged(@NonNull Drawable image) {
        }
    }
}
