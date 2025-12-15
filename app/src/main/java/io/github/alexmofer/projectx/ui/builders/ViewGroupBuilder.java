package io.github.alexmofer.projectx.ui.builders;

import android.animation.LayoutTransition;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewGroup 构建器
 * Created by Alex on 2025/7/15.
 */
public class ViewGroupBuilder extends ViewBuilder {

    private final ViewGroup mView;

    public ViewGroupBuilder(ViewGroup view) {
        super(view);
        this.mView = view;
    }

    @Override
    public ViewGroup build() {
        return mView;
    }

    public ViewGroupBuilder addView(View child) {
        mView.addView(child);
        return this;
    }

    public ViewGroupBuilder addView(View child, ViewGroup.LayoutParams params) {
        mView.addView(child, params);
        return this;
    }

    public ViewGroupBuilder setLayoutTransition(LayoutTransition transition) {
        mView.setLayoutTransition(transition);
        return this;
    }
}
