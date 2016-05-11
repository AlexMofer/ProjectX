package am.project.x.widgets.headerfootergridview.support;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.GridView;

/**
 * GridView 版本控制器
 * TODO 可完成低版本支持
 * @author AlexMofer
 * 
 */
public class GridViewCompat {

	interface GridViewVersionImpl {
		public int getNumColumns(GridView gridView);

		public int getColumnWidth(GridView gridView);

		public int getRequestedColumnWidth(GridView gridView);

		public int getVerticalSpacing(GridView gridView);

		public int getHorizontalSpacing(GridView gridView);

		public int getRequestedHorizontalSpacing(GridView gridView);
	}

	static class BaseGridViewVersionImpl implements GridViewVersionImpl {

		@Override
		public int getNumColumns(GridView gridView) {
			return GridView.AUTO_FIT;
		}

		@Override
		public int getColumnWidth(GridView gridView) {
			return 0;
		}

		@Override
		public int getRequestedColumnWidth(GridView gridView) {
			return 0;
		}

		@Override
		public int getVerticalSpacing(GridView gridView) {
			return 0;
		}

		@Override
		public int getHorizontalSpacing(GridView gridView) {
			return 0;
		}

		@Override
		public int getRequestedHorizontalSpacing(GridView gridView) {
			return 0;
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	static class HoneycombGridViewVersionImpl extends BaseGridViewVersionImpl {

		@Override
		public int getNumColumns(GridView gridView) {
			return gridView.getNumColumns();
		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	static class JellybeanGridViewVersionImpl extends
			HoneycombGridViewVersionImpl {

		@Override
		public int getColumnWidth(GridView gridView) {
			return gridView.getColumnWidth();
		}

		@Override
		public int getRequestedColumnWidth(GridView gridView) {
			return gridView.getRequestedColumnWidth();
		}

		@Override
		public int getVerticalSpacing(GridView gridView) {
			return gridView.getVerticalSpacing();
		}

		@Override
		public int getHorizontalSpacing(GridView gridView) {
			return gridView.getHorizontalSpacing();
		}

		@Override
		public int getRequestedHorizontalSpacing(GridView gridView) {
			return gridView.getRequestedHorizontalSpacing();
		}
	}

	static final GridViewVersionImpl IMPL;
	static {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 16) {
			IMPL = new JellybeanGridViewVersionImpl();
		} else if (version >= 11) {
			IMPL = new HoneycombGridViewVersionImpl();
		} else {
			IMPL = new BaseGridViewVersionImpl();
		}
	}

	/**
	 * 获取列数目
	 * <p>
	 * Build.VERSION.SDK_INT >= 11
	 * 
	 * @param gridView
	 * @return
	 */
	public static int getNumColumns(GridView gridView) {
		return IMPL.getNumColumns(gridView);
	}

	/**
	 * 获取子项宽
	 * <p>
	 * Build.VERSION.SDK_INT >= 16
	 * 
	 * @param gridView
	 * @return
	 */
	public static int getColumnWidth(GridView gridView) {
		return IMPL.getColumnWidth(gridView);
	}

	/**
	 * 获取初始设置的列宽
	 * <p>
	 * Build.VERSION.SDK_INT >= 16
	 * 
	 * @param gridView
	 * @return
	 */
	public static int getRequestedColumnWidth(GridView gridView) {
		return IMPL.getRequestedColumnWidth(gridView);
	}

	/**
	 * 获取行间距
	 * <p>
	 * Build.VERSION.SDK_INT >= 16
	 * 
	 * @param gridView
	 * @return
	 */
	public static int getVerticalSpacing(GridView gridView) {
		return IMPL.getVerticalSpacing(gridView);
	}

	/**
	 * 获取列间距
	 * <p>
	 * Build.VERSION.SDK_INT >= 16
	 * 
	 * @param gridView
	 * @return
	 */
	public static int getHorizontalSpacing(GridView gridView) {
		return IMPL.getHorizontalSpacing(gridView);
	}

	/**
	 * 获取初始设置的列间距
	 * <p>
	 * Build.VERSION.SDK_INT >= 16
	 * 
	 * @param gridView
	 * @return
	 */
	public static int getRequestedHorizontalSpacing(GridView gridView) {
		return IMPL.getRequestedHorizontalSpacing(gridView);
	}
}
