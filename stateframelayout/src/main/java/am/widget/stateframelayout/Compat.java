package am.widget.stateframelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 版本兼容控制器
 */
class Compat {

    interface CompatPlusImpl {
        Drawable getDrawable(Context context, int id);

        void setHotspot(Drawable drawable, float x, float y);
    }

    static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        @SuppressWarnings("all")
        public Drawable getDrawable(Context context, int id) {
            return context.getResources().getDrawable(id);
        }

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            // do nothing
        }
    }

    @TargetApi(21)
    static class LollipopCompatPlusImpl extends BaseCompatPlusImpl {

        @Override
        public Drawable getDrawable(Context context, int id) {
            return context.getDrawable(id);
        }

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            drawable.setHotspot(x, y);
        }
    }

    static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopCompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    public static Drawable getDrawable(Context context, int id) {
        return IMPL.getDrawable(context, id);
    }

    public static void setHotspot(Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }
}
