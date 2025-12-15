package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

/**
 * ImageView 构建器
 * Created by Alex on 2025/6/24.
 */
public class ImageViewBuilder extends ViewBuilder {

    private final ImageView mView;

    public ImageViewBuilder(ImageView view) {
        super(view);
        this.mView = view;
    }

    public ImageViewBuilder(Context context) {
        this(new AppCompatImageView(context, null, 0));
    }


    public ImageViewBuilder setImageResource(@DrawableRes int resId) {
        mView.setImageResource(resId);
        return this;
    }

    public ImageViewBuilder setImageResource(LifecycleOwner owner, LiveData<Integer> image) {
        image.observe(owner, value -> {
            if (value == null) {
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

    @Override
    public ImageView build() {
        return mView;
    }
}
