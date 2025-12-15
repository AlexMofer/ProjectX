package io.github.alexmofer.projectx.ui.builders;

import android.view.ViewGroup;

/**
 * LayoutParams 构建器
 * Created by Alex on 2025/7/15.
 */
public class LayoutParamsBuilder {

    private final ViewGroup.LayoutParams mLayoutParams;

    public LayoutParamsBuilder(ViewGroup.LayoutParams layoutParams) {
        this.mLayoutParams = layoutParams;
    }

    public LayoutParamsBuilder() {
        this(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 强制转换
     */
    public <T extends LayoutParamsBuilder> T cast() {
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 构建
     *
     * @return ViewGroup.LayoutParams
     */
    public ViewGroup.LayoutParams build() {
        return mLayoutParams;
    }

    public LayoutParamsBuilder setWidth(int width) {
        mLayoutParams.width = width;
        return this;
    }

    public LayoutParamsBuilder wrapWidth() {
        return setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public LayoutParamsBuilder matchWidth() {
        return setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public LayoutParamsBuilder setHeight(int height) {
        mLayoutParams.height = height;
        return this;
    }

    public LayoutParamsBuilder wrapHeight() {
        return setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public LayoutParamsBuilder matchHeight() {
        return setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public LayoutParamsBuilder wrapAll() {
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        return this;
    }

    public LayoutParamsBuilder matchAll() {
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        return this;
    }

    public LayoutParamsBuilder setSize(int size) {
        mLayoutParams.width = size;
        mLayoutParams.height = size;
        return this;
    }
}
