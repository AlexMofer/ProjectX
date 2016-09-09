package am.drawable;

import android.graphics.drawable.Drawable;

/**
 * Drawable 动画回调
 * Created by Alex on 2016/9/9.
 */
public interface AnimationCallback {

    /**
     * 开始动画
     * @param drawable Drawable
     */
    void onAnimationStart(Drawable drawable);

    /**
     * 结束动画
     * @param drawable Drawable
     */
    void onAnimationEnd(Drawable drawable);
}
