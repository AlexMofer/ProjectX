package am.widget.selectionview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

/**
 * 版本兼容控制器
 */
class Compat {

    interface CompatImpl {
        int getPaddingStart(View view);

        int getPaddingEnd(View view);

        void setHotspot(Drawable drawable, float x, float y);

        Drawable getDrawable(Context context, int id);
    }

    static class BaseCompatImpl implements CompatImpl {
        private static final Object sLock = new Object();
        private static TypedValue sTempValue;

        @Override
        public int getPaddingStart(View view) {
            return view.getPaddingLeft();
        }

        @Override
        public int getPaddingEnd(View view) {
            return view.getPaddingRight();
        }

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            // do nothing
        }

        @SuppressWarnings("all")
        @Override
        public Drawable getDrawable(Context context, int id) {
            // Prior to JELLY_BEAN, Resources.getDrawable() would not correctly
            // retrieve the final configuration density when the resource ID
            // is a reference another Drawable resource. As a workaround, try
            // to resolve the drawable reference manually.
            final int resolvedId;
            synchronized (sLock) {
                if (sTempValue == null) {
                    sTempValue = new TypedValue();
                }
                context.getResources().getValue(id, sTempValue, true);
                resolvedId = sTempValue.resourceId;
            }
            return context.getResources().getDrawable(resolvedId);
        }
    }

    @TargetApi(16)
    static class JBCompatImpl extends BaseCompatImpl {

        @SuppressWarnings("all")
        @Override
        public Drawable getDrawable(Context context, int id) {
            return context.getResources().getDrawable(id);
        }
    }

    @TargetApi(17)
    static class JbMr1CompatImpl extends JBCompatImpl {
        @Override
        public int getPaddingStart(View view) {
            return view.getPaddingStart();
        }

        @Override
        public int getPaddingEnd(View view) {
            return view.getPaddingEnd();
        }
    }

    @TargetApi(21)
    static class LollipopCompatImpl extends JbMr1CompatImpl {
        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            drawable.setHotspot(x, y);
        }

        @Override
        public Drawable getDrawable(Context context, int id) {
            return context.getDrawable(id);
        }
    }

    private static final CompatImpl IMPL;


    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopCompatImpl();
        } else if (version >= 17) {
            IMPL = new JbMr1CompatImpl();
        } else if (version >= 16) {
            IMPL = new JBCompatImpl();
        } else {
            IMPL = new BaseCompatImpl();
        }
    }

    public static int getPaddingStart(View view) {
        return IMPL.getPaddingStart(view);
    }

    public static int getPaddingEnd(View view) {
        return IMPL.getPaddingEnd(view);
    }

    public static void setHotspot(Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }

    public static Drawable getDrawable(Context context, int id) {
        return IMPL.getDrawable(context, id);
    }
}
