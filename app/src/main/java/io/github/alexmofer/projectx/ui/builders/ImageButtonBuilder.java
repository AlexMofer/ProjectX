package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatImageButton;

/**
 * ImageButton 构建器
 * Created by Alex on 2025/6/24.
 */
public final class ImageButtonBuilder extends ImageViewBuilder {

    private final ImageButton mView;

    public ImageButtonBuilder(ImageButton view) {
        super(view);
        this.mView = view;
    }

    public ImageButtonBuilder(Context context) {
        this(new AppCompatImageButton(context, null, 0));
    }

    @Override
    public ImageButton build() {
        return mView;
    }
}
