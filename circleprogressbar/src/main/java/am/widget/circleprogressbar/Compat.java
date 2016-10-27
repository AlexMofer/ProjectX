package am.widget.circleprogressbar;

import android.annotation.TargetApi;
import android.view.Gravity;
import android.view.View;

/**
 * 版本兼容控制器
 */
class Compat {

    /** Raw bit controlling whether the layout direction is relative or not (START/END instead of
     * absolute LEFT/RIGHT).
     */
    private static final int RELATIVE_LAYOUT_DIRECTION = 0x00800000;

    /** Push object to x-axis position at the start of its container, not changing its size. */
    static final int START = RELATIVE_LAYOUT_DIRECTION | Gravity.LEFT;

    /** Push object to x-axis position at the end of its container, not changing its size. */
    static final int END = RELATIVE_LAYOUT_DIRECTION | Gravity.RIGHT;
    private interface CompatPlusImpl {
        int getPaddingStart(View view);
        int getPaddingEnd(View view);
    }

    private static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        public int getPaddingStart(View view) {
            return view.getPaddingLeft();
        }

        @Override
        public int getPaddingEnd(View view) {
            return view.getPaddingRight();
        }
    }

    @TargetApi(17)
    private static class JbMr1CompatPlusImpl extends BaseCompatPlusImpl {
        @Override
        public int getPaddingStart(View view) {
            return view.getPaddingStart();
        }

        @Override
        public int getPaddingEnd(View view) {
            return view.getPaddingEnd();
        }
    }

    private static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 17) {
            IMPL = new JbMr1CompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    static int getPaddingStart(View view) {
        return IMPL.getPaddingStart(view);
    }

    static int getPaddingEnd(View view) {
        return IMPL.getPaddingEnd(view);
    }
}
