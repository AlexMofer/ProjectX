package am.project.support.view;

import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

/**
 * L Elevation特效版本控制器
 *
 * @author Mofer
 */
public class CompatPlus {

    public static final int SHAPE_RECT = 0;
    public static final int SHAPE_ROUNDRECT = 1;
    public static final int SHAPE_OVAL = 2;

    interface CompatPlusImpl {
        void setBackground(View view, Drawable drawable);

        void setOutlineProvider(View view, int shape, int left, int top,
                                int right, int bottom, float radius);

        void setClipToOutline(View view, boolean clipToOutline);

        void setStateListAnimator(View view, Context context, int id);

        boolean isLargeHeap(Context context);

        int getLargeMemoryClass(ActivityManager am);

        void setTranslucentStatus(Window window);

        void setTranslucentNavigation(Window window);
    }

    static class BaseCompatPlusImpl implements CompatPlusImpl {

        @SuppressWarnings("deprecation")
        @Override
        public void setBackground(View view, Drawable drawable) {
            view.setBackgroundDrawable(drawable);
        }

        @Override
        public void setOutlineProvider(View view, int shape, int left, int top,
                                       int right, int bottom, float radius) {
            // do nothing

        }

        @Override
        public void setClipToOutline(View view, boolean clipToOutline) {
            // do nothing

        }

        @Override
        public void setStateListAnimator(View view, Context context, int id) {
            // do nothing

        }

        @Override
        public boolean isLargeHeap(Context context) {
            return false;
        }

        @Override
        public int getLargeMemoryClass(ActivityManager am) {
            return 0;
        }

        @Override
        public void setTranslucentStatus(Window window) {
            // do nothing until api 19
        }

        @Override
        public void setTranslucentNavigation(Window window) {
            // do nothing until api 19
        }

    }

    @TargetApi(7)
    static class EclairMr1CompatPlusImpl extends BaseCompatPlusImpl {

    }

    @TargetApi(9)
    static class GBCompatPlusImpl extends EclairMr1CompatPlusImpl {

    }

    @TargetApi(11)
    static class HCCompatPlusImpl extends GBCompatPlusImpl {
        @Override
        public boolean isLargeHeap(Context context) {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
        }

        @Override
        public int getLargeMemoryClass(ActivityManager am) {
            return am.getLargeMemoryClass();
        }
    }

    @TargetApi(14)
    static class ICSCompatPlusImpl extends HCCompatPlusImpl {

    }

    @TargetApi(16)
    static class JBCompatPlusImpl extends ICSCompatPlusImpl {
        @Override
        public void setBackground(View view, Drawable drawable) {
            view.setBackground(drawable);
        }
    }

    @TargetApi(17)
    static class JbMr1CompatPlusImpl extends JBCompatPlusImpl {

    }

    @TargetApi(19)
    static class KitKatCompatPlusImpl extends JbMr1CompatPlusImpl {
        @Override
        public void setTranslucentStatus(Window window) {
            window.setFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        @Override
        public void setTranslucentNavigation(Window window) {
            window.setFlags(LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @TargetApi(21)
    static class LollipopCompatPlusImpl extends KitKatCompatPlusImpl {

        @Override
        public void setOutlineProvider(View view, final int shape,
                                       final int left, final int top, final int right,
                                       final int bottom, final float radius) {
            view.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    switch (shape) {
                        default:
                        case SHAPE_RECT:
                            outline.setRect(left, top, right, bottom);
                            break;
                        case SHAPE_ROUNDRECT:
                            outline.setRoundRect(left, top, right, bottom, radius);
                            break;
                        case SHAPE_OVAL:
                            outline.setOval(left, top, right, bottom);
                            break;
                    }
                }
            });
        }

        @Override
        public void setClipToOutline(View view, boolean clipToOutline) {
            view.setClipToOutline(clipToOutline);
        }

        @Override
        public void setStateListAnimator(View view, Context context, int id) {
            view.setStateListAnimator(AnimatorInflater.loadStateListAnimator(
                    context, id));
        }
    }

    static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopCompatPlusImpl();
        } else if (version >= 19) {
            IMPL = new KitKatCompatPlusImpl();
        } else if (version >= 17) {
            IMPL = new JbMr1CompatPlusImpl();
        } else if (version >= 16) {
            IMPL = new JBCompatPlusImpl();
        } else if (version >= 14) {
            IMPL = new ICSCompatPlusImpl();
        } else if (version >= 11) {
            IMPL = new HCCompatPlusImpl();
        } else if (version >= 9) {
            IMPL = new GBCompatPlusImpl();
        } else if (version >= 7) {
            IMPL = new EclairMr1CompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    public static void setBackground(View view, Drawable drawable) {
        IMPL.setBackground(view, drawable);
    }

    public static void setOutlineProvider(View view, int shape, int left,
                                          int top, int right, int bottom, float radius) {
        IMPL.setOutlineProvider(view, shape, left, top, right, bottom, radius);
    }

    public static void setClipToOutline(View view, boolean clipToOutline) {
        IMPL.setClipToOutline(view, clipToOutline);
    }

    public static void setStateListAnimator(View view, Context context, int id) {
        IMPL.setStateListAnimator(view, context, id);
    }

    public static boolean isLargeHeap(Context context) {
        return IMPL.isLargeHeap(context);
    }

    public static int getLargeMemoryClass(ActivityManager am) {
        return IMPL.getLargeMemoryClass(am);
    }

    public static void setTranslucentStatus(Window window) {
        IMPL.setTranslucentStatus(window);
    }

    public static void setTranslucentNavigation(Window window) {
        IMPL.setTranslucentNavigation(window);
    }
}
