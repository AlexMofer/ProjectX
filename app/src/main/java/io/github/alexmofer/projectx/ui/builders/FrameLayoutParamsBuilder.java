package io.github.alexmofer.projectx.ui.builders;

import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * FrameLayout.LayoutParams
 * Created by Alex on 2025/7/18.
 */
public final class FrameLayoutParamsBuilder extends MarginLayoutParamsBuilder {

    private final FrameLayout.LayoutParams mLayoutParams;

    public FrameLayoutParamsBuilder(FrameLayout.LayoutParams layoutParams) {
        super(layoutParams);
        mLayoutParams = layoutParams;
    }

    public FrameLayoutParamsBuilder() {
        this(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public FrameLayout.LayoutParams build() {
        return mLayoutParams;
    }

    public FrameLayoutParamsBuilder setGravity(int gravity) {
        mLayoutParams.gravity = gravity;
        return this;
    }
}