package am.widget;

import android.annotation.TargetApi;
import android.graphics.Paint;
import android.view.View;

/**
 * 版本兼容控制器
 */
class Compat {
    static final int LAYER_TYPE_SOFTWARE = 1;
    private interface CompatImpl {
        void setLayerType(View view, int layerType, Paint paint);
        void setElevation(View view, float elevation);
    }

    private static class BaseCompatImpl implements CompatImpl {

        @Override
        public void setLayerType(View view, int layerType, Paint paint) {
            // No-op until layers became available (HC)
        }

        @Override
        public void setElevation(View view, float elevation) {
        }
    }

    @TargetApi(11)
    private static class HCViewCompatImpl extends BaseCompatImpl {
        @Override
        public void setLayerType(View view, int layerType, Paint paint) {
            view.setLayerType(layerType, paint);
        }
    }

    @TargetApi(21)
    private static class LollipopCompatImpl extends HCViewCompatImpl {

        @Override
        public void setElevation(View view, float elevation) {
            view.setElevation(elevation);
        }
    }

    private static final CompatImpl IMPL;


    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopCompatImpl();
        } else if (version >= 11) {
            IMPL = new HCViewCompatImpl();
        } else {
            IMPL = new BaseCompatImpl();
        }
    }


    static void setLayerType(View view, int layerType, Paint paint) {
        IMPL.setLayerType(view, layerType, paint);
    }

    /**
     * Sets the base elevation of this view, in pixels.
     */
    static void setElevation(View view, float elevation) {
        IMPL.setElevation(view, elevation);
    }
}
