package io.github.alexmofer.android.support.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;

import java.util.function.Function;

/**
 * 资源工具类
 * Created by Alex on 2026/6/17.
 */
public final class ResourceUtils {
    private static final int[] ATTRS_HOME_AS_UP_INDICATOR = new int[]{androidx.appcompat.R.attr.homeAsUpIndicator};

    private ResourceUtils() {
        //no instance
    }

    private static <T> T handleTypedArray(@NonNull Context context,
                                          @NonNull @StyleableRes int[] attrs,
                                          @NonNull Function<TypedArray, T> consumer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try (final TypedArray array = context.obtainStyledAttributes(attrs)) {
                return consumer.apply(array);
            }
        } else {
            final TypedArray array = context.obtainStyledAttributes(attrs);
            try {
                return consumer.apply(array);
            } finally {
                array.recycle();
            }
        }
    }

    /**
     * 获取当前主题中的 homeAsUpIndicator
     *
     * @return 返回箭头的 Drawable，可能为 null
     */
    @Nullable
    public static Drawable getHomeAsUpIndicatorDrawable(@NonNull Context context) {
        return handleTypedArray(context, ATTRS_HOME_AS_UP_INDICATOR,
                typedArray -> typedArray.getDrawable(0));
    }
}
