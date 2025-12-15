package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

/**
 * 按钮构建器
 * Created by Alex on 2025/6/24.
 */
public final class ButtonBuilder extends TextViewBuilder {

    private final Button mView;

    public ButtonBuilder(Button view) {
        super(view);
        this.mView = view;
    }

    public ButtonBuilder(Context context) {
        this(new AppCompatButton(context, null, 0));
    }

    @Override
    public Button build() {
        return mView;
    }
}
