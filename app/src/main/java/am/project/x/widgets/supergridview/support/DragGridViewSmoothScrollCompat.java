package am.project.x.widgets.supergridview.support;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * 自动滑动支持
 * 低于 FROYO 的系统使用创建滑动手势进行自动滑动
 * @author xiangzhicheng
 *
 */
public class DragGridViewSmoothScrollCompat {
	interface SmoothScrollVersionImpl {
		void smoothScrollBy(GridView gridView, int distance, int duration);
	}

	static class BaseSmoothScrollImpl implements SmoothScrollVersionImpl {

		@Override
		public void smoothScrollBy(GridView gridView, int distance,
				int duration) {
			float x = gridView.getLeft();
			float y = gridView.getTop();
			MotionEvent down = MotionEvent.obtain(SystemClock.uptimeMillis(),
					SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y,
					0);
			gridView.dispatchTouchEvent(down);
			int num = duration / 5;
			float offset = distance * 5 / duration;
			y -= offset;
			MotionEvent move = MotionEvent.obtain(SystemClock.uptimeMillis(),
					SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x, y,
					0);
			for (int i = 1; i < num; i++) {
				y -= offset;
				gridView.dispatchTouchEvent(move);
				move = MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, x,
						y, 0);
			}
			MotionEvent up = MotionEvent.obtain(SystemClock.uptimeMillis(),
					SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
			gridView.dispatchTouchEvent(up);
			down.recycle();
			move.recycle();
			up.recycle();
		}

	}

	@TargetApi(8)
	static class FroyoSmoothScrollImpl extends BaseSmoothScrollImpl {
		@Override
		public void smoothScrollBy(GridView gridView, int distance,
				int duration) {
			gridView.smoothScrollBy(distance, duration);
		}
	}

	static final SmoothScrollVersionImpl IMPL;
	static {
		if (Build.VERSION.SDK_INT >= 8) {
			IMPL = new FroyoSmoothScrollImpl();
		} else {
			IMPL = new BaseSmoothScrollImpl();
		}
	}

	public static void smoothScrollBy(GridView gridView, int distance,
			int duration) {
		IMPL.smoothScrollBy(gridView, distance, duration);
	}
}
