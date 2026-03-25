package io.github.alexmofer.android.support.graphics.drawable.builders;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * RippleDrawable 链式构建器
 * Created by Alex on 2026/3/25.
 */
public class RippleDrawableBuilder extends LayerDrawableBuilder {
    private final RippleDrawable mDrawable;

    public RippleDrawableBuilder(@NonNull RippleDrawable drawable) {
        super(drawable);
        mDrawable = drawable;
    }

    public RippleDrawableBuilder(@NonNull ColorStateList color, @Nullable Drawable content,
                                 @Nullable Drawable mask) {
        this(new RippleDrawable(color, content, mask));
    }

    @NonNull
    @Override
    public RippleDrawable build() {
        return mDrawable;
    }

    public RippleDrawableBuilder setColor(@NonNull ColorStateList color) {
        mDrawable.setColor(color);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    public RippleDrawableBuilder setEffectColor(@NonNull ColorStateList color) {
        mDrawable.setEffectColor(color);
        return this;
    }

    public RippleDrawableBuilder setRadius(int radius) {
        mDrawable.setRadius(radius);
        return this;
    }
}
