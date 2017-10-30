package am.widget.cameraview.old;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.view.View;

/**
 * 版本兼容器
 * Created by Alex on 2016/11/28.
 */

class Compat {

    private static final CompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new CompatAPI23();
        } else if (version >= 21) {
            IMPL = new CompatLollipop();
        } else if (version >= 17) {
            IMPL = new JbMr1CompatPlusImpl();
        } else {
            IMPL = new CompatBase();
        }
    }

    private Compat() {
    }

    static int checkSelfPermission(Context context, String permission) {
        return IMPL.checkSelfPermission(context, permission);
    }

    static int getPaddingStart(View view) {
        return IMPL.getPaddingStart(view);
    }

    static int getPaddingEnd(View view) {
        return IMPL.getPaddingEnd(view);
    }

    static void setHotspot(Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }

    private interface CompatImpl {
        int checkSelfPermission(Context context, String permission);

        int getPaddingStart(View view);

        int getPaddingEnd(View view);

        void setHotspot(Drawable drawable, float x, float y);
    }

    private static class CompatBase implements CompatImpl {

        @Override
        public int checkSelfPermission(Context context, String permission) {
            return context.checkPermission(permission, Process.myPid(), Process.myUid());
        }

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
    }

    @TargetApi(17)
    private static class JbMr1CompatPlusImpl extends CompatBase {
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
    private static class CompatLollipop extends JbMr1CompatPlusImpl {

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            drawable.setHotspot(x, y);
        }
    }

    @TargetApi(23)
    private static class CompatAPI23 extends CompatBase {
        @Override
        public int checkSelfPermission(Context context, String permission) {
            return context.checkSelfPermission(permission);
        }
    }
}
