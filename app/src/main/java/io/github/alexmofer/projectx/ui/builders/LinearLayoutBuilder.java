package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.widget.LinearLayout;

/**
 * 线性布局构建器
 * Created by Alex on 2025/7/17.
 */
public final class LinearLayoutBuilder extends ViewGroupBuilder {

    private final LinearLayout mView;

    private LinearLayoutBuilder(LinearLayout view) {
        super(view);
        this.mView = view;
    }

    public LinearLayoutBuilder(Context context) {
        this(new LinearLayout(context));
    }

    @Override
    public LinearLayout build() {
        return mView;
    }

    public LinearLayoutBuilder setOrientation(int orientation) {
        mView.setOrientation(orientation);
        return this;
    }

    public LinearLayoutBuilder setGravity(int gravity) {
        mView.setGravity(gravity);
        return this;
    }
}
