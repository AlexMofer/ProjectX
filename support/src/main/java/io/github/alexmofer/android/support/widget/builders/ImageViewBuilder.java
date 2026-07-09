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
package io.github.alexmofer.android.support.widget.builders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

/**
 * ImageView 构建器
 * Created by Alex on 2025/6/24.
 */
public class ImageViewBuilder extends ViewBuilder {
    private final ImageView mView;

    public ImageViewBuilder(@NonNull ImageView view) {
        super(view);
        this.mView = view;
    }

    public ImageViewBuilder(@NonNull Context context) {
        this(build(context));
    }

    @NonNull
    public static ImageView build(@NonNull Context context) {
        return new AppCompatImageView(context, null, 0);
    }

    @NonNull
    @Override
    public ImageView build() {
        return mView;
    }

    public ImageViewBuilder setImageResource(@DrawableRes int resId) {
        if (resId == ResourcesCompat.ID_NULL) {
            mView.setImageDrawable(null);
            return this;
        }
        mView.setImageResource(resId);
        return this;
    }

    public ImageViewBuilder setImageResource(LifecycleOwner owner, LiveData<Integer> image) {
        image.observe(owner, value -> {
            if (value == null || value == ResourcesCompat.ID_NULL) {
                mView.setImageDrawable(null);
            } else {
                mView.setImageResource(value);
            }
        });
        return this;
    }

    public ImageViewBuilder setImageDrawable(@Nullable Drawable drawable) {
        mView.setImageDrawable(drawable);
        return this;
    }

    public ImageViewBuilder setImageDrawable(LifecycleOwner owner, @NonNull LiveData<Drawable> drawable) {
        drawable.observe(owner, mView::setImageDrawable);
        return this;
    }

    public ImageViewBuilder setImage(LifecycleOwner owner, @NonNull LiveData<?> image) {
        image.observe(owner, value -> {
            if (value instanceof Integer) {
                mView.setImageResource((Integer) value);
                return;
            }
            if (value instanceof Drawable) {
                mView.setImageDrawable((Drawable) value);
                return;
            }
            if (value instanceof Bitmap) {
                mView.setImageBitmap((Bitmap) value);
                return;
            }
            if (value instanceof Icon) {
                mView.setImageIcon((Icon) value);
                return;
            }
            if (value instanceof Uri) {
                mView.setImageURI((Uri) value);
                return;
            }
            mView.setImageDrawable(null);
        });
        return this;
    }

    public ImageViewBuilder setContentDescription(CharSequence contentDescription) {
        mView.setContentDescription(contentDescription);
        return this;
    }

    public ImageViewBuilder setScaleType(ImageView.ScaleType scaleType) {
        mView.setScaleType(scaleType);
        return this;
    }

    @Override
    public ViewBuilder setTooltipText(@Nullable CharSequence tooltipText) {
        setContentDescription(tooltipText);
        return super.setTooltipText(tooltipText);
    }

    @Override
    public ViewBuilder setTooltipText(int tooltipText) {
        setContentDescription(mView.getContext().getString(tooltipText));
        return super.setTooltipText(tooltipText);
    }
}
