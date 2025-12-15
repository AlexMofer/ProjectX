package io.github.alexmofer.projectx.ui.builders;

import android.view.ViewGroup;

/**
 * ViewGroup.MarginLayoutParams 构建器
 * Created by Alex on 2025/7/15.
 */
public class MarginLayoutParamsBuilder extends LayoutParamsBuilder {

    private final ViewGroup.MarginLayoutParams mLayoutParams;

    public MarginLayoutParamsBuilder(ViewGroup.MarginLayoutParams layoutParams) {
        super(layoutParams);
        mLayoutParams = layoutParams;
    }

    public MarginLayoutParamsBuilder() {
        this(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public ViewGroup.MarginLayoutParams build() {
        return mLayoutParams;
    }

    public MarginLayoutParamsBuilder setMarginLeft(int left) {
        mLayoutParams.leftMargin = left;
        return this;
    }

    public MarginLayoutParamsBuilder setMarginTop(int top) {
        mLayoutParams.topMargin = top;
        return this;
    }

    public MarginLayoutParamsBuilder setMarginRight(int right) {
        mLayoutParams.rightMargin = right;
        return this;
    }

    public MarginLayoutParamsBuilder setMarginBottom(int bottom) {
        mLayoutParams.bottomMargin = bottom;
        return this;
    }

    public MarginLayoutParamsBuilder setMarginStart(int start) {
        mLayoutParams.setMarginStart(start);
        return this;
    }

    public MarginLayoutParamsBuilder setMarginEnd(int end) {
        mLayoutParams.setMarginEnd(end);
        return this;
    }

    public MarginLayoutParamsBuilder setMarginVertical(int vertical) {
        mLayoutParams.topMargin = vertical;
        mLayoutParams.bottomMargin = vertical;
        return this;
    }

    public MarginLayoutParamsBuilder setMarginHorizontal(int horizontal) {
        mLayoutParams.leftMargin = horizontal;
        mLayoutParams.rightMargin = horizontal;
        return this;
    }

    public MarginLayoutParamsBuilder setMargin(int left, int top, int right, int bottom) {
        mLayoutParams.setMargins(left, top, right, bottom);
        return this;
    }

    public MarginLayoutParamsBuilder setMargin(int margin) {
        mLayoutParams.setMargins(margin, margin, margin, margin);
        return this;
    }
}
