package am.project.x.utils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

/**
 * 主题工具
 * Created by Alex on 2017/5/31.
 */
@SuppressWarnings("unused")
public class ThemeUtil {

    public static int getDimensionPixelSize(@NonNull Resources.Theme theme, int attr,
                                            int defValue) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{attr});
        int size = ta.getDimensionPixelSize(0, defValue);
        ta.recycle();
        return size;
    }

    @ColorInt
    public static int getColor(@NonNull Resources.Theme theme, int attr, int defValue) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{attr});
        int color = ta.getColor(0, defValue);
        ta.recycle();
        return color;
    }
}
