package android.support.v4.widget;

import android.support.annotation.ColorInt;
import android.view.View;

/**
 * MaterialProgressDrawable
 */
public class MaterialLoadingProgressDrawable extends MaterialProgressDrawable {

    public MaterialLoadingProgressDrawable(View parent) {
        this(parent, 0x00000000, 0xff33b5e5, 0xff99cc00, 0xffff4444, 0xffffbb33);
    }

    public MaterialLoadingProgressDrawable(View parent, @ColorInt int backgroundColor,
                                           int... schemeColors) {
        this(parent, backgroundColor, true, schemeColors);
    }

    public MaterialLoadingProgressDrawable(View parent, @ColorInt int backgroundColor,
                                           boolean autoStart, int... schemeColors) {
        super(parent.getContext(), parent);
        setAlpha(255);
        setBackgroundColor(backgroundColor);
        setColorSchemeColors(schemeColors);
        if (autoStart)
            start();
    }
}
