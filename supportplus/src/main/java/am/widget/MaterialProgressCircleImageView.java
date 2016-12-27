package am.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * 载入动画ImageView
 *
 * android.support.v4.widget.CircleImageView
 */
@Deprecated
public class MaterialProgressCircleImageView extends MaterialProgressImageView {

    private static final int DEFAULT_PROGRESS_BACKGROUND = 0x00000000;
    private static final int[] DEFAULT_PROGRESS_COLORS = new int[]{0xff33b5e5, 0xff99cc00,
            0xffff4444, 0xffffbb33};

    public MaterialProgressCircleImageView(Context context) {
        super(context);
        setMaterialLoadingProgressDrawable(DEFAULT_PROGRESS_BACKGROUND, true,
                DEFAULT_PROGRESS_COLORS);
    }

    public MaterialProgressCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMaterialLoadingProgressDrawable(DEFAULT_PROGRESS_BACKGROUND, true,
                DEFAULT_PROGRESS_COLORS);
    }

    public MaterialProgressCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMaterialLoadingProgressDrawable(DEFAULT_PROGRESS_BACKGROUND, true,
                DEFAULT_PROGRESS_COLORS);
    }

    /**
     * 设置载入动画
     *
     * @param backgroundColor 背景颜色
     * @param start           立即开始
     * @param schemeColors    变换颜色
     */
    public void setMaterialLoadingProgressDrawable(int backgroundColor,
                                                   boolean start,
                                                   int... schemeColors) {
        setDrawableBackgroundColor(backgroundColor);
        setColorSchemeColors(schemeColors);
        if (start && ! isRunning())
            start();
    }

}
