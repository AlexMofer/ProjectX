package am.project.x.widgets.utils;

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
 * 
 */
public class VersionCompat {

	public static final int SHAPE_RECT = 0;
	public static final int SHAPE_ROUNDRECT = 1;
	public static final int SHAPE_OVAL = 2;

	interface VersionCompatImpl {
		public void setBackground(View view, Drawable drawable);

		public void setOutlineProvider(View view, int shape, int left, int top,
									   int right, int bottom, float radius);

		public void setClipToOutline(View view, boolean clipToOutline);

		public void setStateListAnimator(View view, Context context, int id);

		public boolean isLargeHeap(Context context);

		public int getLargeMemoryClass(ActivityManager am);

		public void setTranslucaentStatus(Window window);

		public void setTranslucaentNavigation(Window window);
	}

	static class BaseVersionCompatImpl implements VersionCompatImpl {

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
		public void setTranslucaentStatus(Window window) {
			// do nothing until api 19
		}

		@Override
		public void setTranslucaentNavigation(Window window) {
			// do nothing until api 19
		}

	}

	@TargetApi(7)
	static class EclairMr1VersionCompatImpl extends BaseVersionCompatImpl {

	}

	@TargetApi(9)
	static class GBVersionCompatImpl extends EclairMr1VersionCompatImpl {

	}

	@TargetApi(11)
	static class HCVersionCompatImpl extends GBVersionCompatImpl {
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
	static class ICSVersionCompatImpl extends HCVersionCompatImpl {

	}

	@TargetApi(16)
	static class JBVersionCompatImpl extends ICSVersionCompatImpl {
		@Override
		public void setBackground(View view, Drawable drawable) {
			view.setBackground(drawable);
		}
	}

	@TargetApi(17)
	static class JbMr1VersionCompatImpl extends JBVersionCompatImpl {

	}

	@TargetApi(19)
	static class KitKatVersionCompatImpl extends JbMr1VersionCompatImpl {
		@Override
		public void setTranslucaentStatus(Window window) {
			window.setFlags(LayoutParams.FLAG_TRANSLUCENT_STATUS,
					LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}

		@Override
		public void setTranslucaentNavigation(Window window) {
			window.setFlags(LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
					LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}

	@TargetApi(21)
	static class LollipopVersionCompatImpl extends KitKatVersionCompatImpl {

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

	static final VersionCompatImpl IMPL;
	static {
		final int version = android.os.Build.VERSION.SDK_INT;
		if (version >= 21) {
			IMPL = new LollipopVersionCompatImpl();
		} else if (version >= 19) {
			IMPL = new KitKatVersionCompatImpl();
		} else if (version >= 17) {
			IMPL = new JbMr1VersionCompatImpl();
		} else if (version >= 16) {
			IMPL = new JBVersionCompatImpl();
		} else if (version >= 14) {
			IMPL = new ICSVersionCompatImpl();
		} else if (version >= 11) {
			IMPL = new HCVersionCompatImpl();
		} else if (version >= 9) {
			IMPL = new GBVersionCompatImpl();
		} else if (version >= 7) {
			IMPL = new EclairMr1VersionCompatImpl();
		} else {
			IMPL = new BaseVersionCompatImpl();
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

	public static void setTranslucaentStatus(Window window) {
		IMPL.setTranslucaentStatus(window);
	}

	public static void setTranslucaentNavigation(Window window) {
		IMPL.setTranslucaentNavigation(window);
	}
}
