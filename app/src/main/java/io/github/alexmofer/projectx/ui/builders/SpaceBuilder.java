package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.Space;

import io.github.alexmofer.android.support.utils.TypedValueUtils;

/**
 * Space 构建器
 * Created by Alex on 2025/7/15.
 */
public final class SpaceBuilder extends ViewBuilder {

    private final Space mView;

    public SpaceBuilder(Space view) {
        super(view);
        this.mView = view;
    }

    public SpaceBuilder(Context context) {
        this(new Space(context));
    }

    public static Space newInstance(Context context, int widthPX, int heightPX) {
        return new SpaceBuilder(context)
                .setLayoutParams(new ViewGroup.LayoutParams(widthPX, heightPX))
                .<SpaceBuilder>cast()
                .build();
    }

    public static Space newInstance(Context context, int sizePX) {
        return newInstance(context, sizePX, sizePX);
    }

    public static Space newInstance(Context context, float widthDP, float heightDP) {
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return newInstance(context, TypedValueUtils.getDimensionPixelSize(widthDP, metrics),
                TypedValueUtils.getDimensionPixelSize(heightDP, metrics));
    }

    public static Space newInstance(Context context, float sizeDP) {
        return newInstance(context, sizeDP, sizeDP);
    }

    @Override
    public Space build() {
        return mView;
    }
}
