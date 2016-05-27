package android.support.v4.widget;

import android.view.View;

/**
 * MaterialProgressDrawable
 * 其为包内可见类，官方下拉刷新图
 * Created by Alex on 2015/10/27.
 */
@SuppressWarnings("unused")
public class MaterialLoadingProgressDrawable extends MaterialProgressDrawable {

    public MaterialLoadingProgressDrawable(View parent) {
        super(parent.getContext(), parent);
        setAlpha(255);
        setBackgroundColor(0x00000000);
        setColorSchemeColors(0xff33b5e5, 0xff99cc00, 0xffff4444, 0xffffbb33);
        start();
    }
}
