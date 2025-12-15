package io.github.alexmofer.projectx.ui.builders;

import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * FrameLayout.LayoutParams
 * Created by Alex on 2025/7/18.
 */
public final class LinearLayoutParamsBuilder extends MarginLayoutParamsBuilder {

    private final LinearLayout.LayoutParams mLayoutParams;

    public LinearLayoutParamsBuilder(LinearLayout.LayoutParams layoutParams) {
        super(layoutParams);
        mLayoutParams = layoutParams;
    }

    public LinearLayoutParamsBuilder() {
        this(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public LinearLayout.LayoutParams build() {
        return mLayoutParams;
    }

    public LinearLayoutParamsBuilder setWeight(float weight) {
        mLayoutParams.weight = weight;
        return this;
    }

    public LinearLayoutParamsBuilder setGravity(int gravity) {
        mLayoutParams.gravity = gravity;
        return this;
    }
}