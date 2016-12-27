package am.drawable;

import android.annotation.TargetApi;
import android.content.Context;

/**
 * 版本兼容控制器
 */
class Compat {

    private interface CompatPlusImpl {
        int getColor(Context context, int id);
    }

    @SuppressWarnings("all")
    private static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        public int getColor(Context context, int id) {
            return context.getResources().getColor(id);
        }
    }

    @TargetApi(23)
    private static class MarshmallowCompatPlusImpl extends BaseCompatPlusImpl {
        @Override
        public int getColor(Context context, int id) {
            return context.getColor(id);
        }
    }

    private static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new MarshmallowCompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    static int getColor(Context context, int id) {
        return IMPL.getColor(context, id);
    }
}
