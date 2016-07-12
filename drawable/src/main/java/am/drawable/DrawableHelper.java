package am.drawable;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * 帮助类
 */
class DrawableHelper {

    static Rect PADDING = new Rect();

    /**
     * 刷新回调布局
     *
     * @param drawable Drawable
     */
    static void requestCallbackLayout(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (drawable.getCallback() != null && drawable.getCallback() instanceof View) {
                View view = (View) drawable.getCallback();
                view.requestLayout();
            }
        }
    }

    /**
     * 刷新Padding
     *
     * @param drawable Drawable
     */
    static void invalidateCallbackPadding(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (drawable.getCallback() != null && drawable.getCallback() instanceof View) {
                View view = (View) drawable.getCallback();
                drawable.getPadding(PADDING);
                view.setPadding(PADDING.left, PADDING.top, PADDING.right, PADDING.bottom);
            }
        }
    }
}
