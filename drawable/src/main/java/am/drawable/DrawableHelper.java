package am.drawable;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * 帮助类
 */
class DrawableHelper {

    /**
     * 刷新回调布局
     */
    static void requestCallbackLayout(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 11) {
            if (drawable.getCallback() != null && drawable.getCallback() instanceof View) {
                View view = (View) drawable.getCallback();
                view.requestLayout();
            }
        }
    }
}
