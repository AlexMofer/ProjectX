package io.github.alexmofer.android.support.graphics.drawable.builders;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.annotation.NonNull;

/**
 * LayerDrawable 链式构建器
 * Created by Alex on 2026/3/25.
 */
public class LayerDrawableBuilder extends DrawableBuilder {
    private final LayerDrawable mDrawable;

    public LayerDrawableBuilder(@NonNull LayerDrawable drawable) {
        super(drawable);
        mDrawable = drawable;
    }

    public LayerDrawableBuilder(@NonNull Drawable[] layers) {
        this(new LayerDrawable(layers));
    }

    @NonNull
    @Override
    public LayerDrawable build() {
        return mDrawable;
    }

    public LayerDrawableBuilder setId(int index, int id) {
        mDrawable.setId(index, id);
        return this;
    }

    public LayerDrawableBuilder setDrawable(int index, Drawable drawable) {
        mDrawable.setDrawable(index, drawable);
        return this;
    }

    public LayerDrawableBuilder setLayerSize(int index, int w, int h) {
        mDrawable.setLayerSize(index, w, h);
        return this;
    }

    public LayerDrawableBuilder setLayerWidth(int index, int w) {
        mDrawable.setLayerWidth(index, w);
        return this;
    }

    public LayerDrawableBuilder setLayerHeight(int index, int h) {
        mDrawable.setLayerHeight(index, h);
        return this;
    }

    public LayerDrawableBuilder setLayerGravity(int index, int gravity) {
        mDrawable.setLayerGravity(index, gravity);
        return this;
    }

    public LayerDrawableBuilder setLayerInset(int index, int l, int t, int r, int b) {
        mDrawable.setLayerInset(index, l, t, r, b);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetRelative(int index, int s, int t, int e, int b) {
        mDrawable.setLayerInsetRelative(index, s, t, e, b);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetLeft(int index, int l) {
        mDrawable.setLayerInsetLeft(index, l);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetRight(int index, int r) {
        mDrawable.setLayerInsetRight(index, r);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetTop(int index, int t) {
        mDrawable.setLayerInsetTop(index, t);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetBottom(int index, int b) {
        mDrawable.setLayerInsetBottom(index, b);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetStart(int index, int s) {
        mDrawable.setLayerInsetStart(index, s);
        return this;
    }

    public LayerDrawableBuilder setLayerInsetEnd(int index, int e) {
        mDrawable.setLayerInsetEnd(index, e);
        return this;
    }

    public LayerDrawableBuilder setPaddingMode(int mode) {
        mDrawable.setPaddingMode(mode);
        return this;
    }

    public LayerDrawableBuilder setPadding(int left, int top, int right, int bottom) {
        mDrawable.setPadding(left, top, right, bottom);
        return this;
    }

    public LayerDrawableBuilder setPaddingRelative(int start, int top, int end, int bottom) {
        mDrawable.setPaddingRelative(start, top, end, bottom);
        return this;
    }

    public LayerDrawableBuilder setOpacity(int opacity) {
        mDrawable.setOpacity(opacity);
        return this;
    }
}
