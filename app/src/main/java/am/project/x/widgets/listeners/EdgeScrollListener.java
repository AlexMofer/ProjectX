package am.project.x.widgets.listeners;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 滚动边缘监听
 * @author Mofer
 *
 */
public abstract class EdgeScrollListener implements OnScrollListener {

	private ScrollLocation mScrollLocation = ScrollLocation.NULL;

	/**
	 * 滚动位置
	 * 
	 * @author AlexMofer
	 * 
	 */
	public enum ScrollLocation {
		/** 起始状态，TB或者TOP状态 */
		NULL,
		/** 无法滚动状态，首尾同时到达 */
		TB,
		/** 滚动到顶部 */
		TOP,
		/** 滚动到中部 */
		CENTER,
		/** 滚动到尾部 */
		BOTTOM;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		onScrollChanged(view, firstVisibleItem, visibleItemCount,
				totalItemCount);

	}

	/**
	 * 滚动更改
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @param totalItemCount
	 */
	private void onScrollChanged(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (visibleItemCount == 0) {
			// 无子项
			return;
		} else {
			ScrollLocation mNewScrollLocation;
			final int gridTop = view.getPaddingTop();
			final int gridBottom = view.getHeight() - view.getPaddingBottom();
			if (totalItemCount == visibleItemCount) {
				// 头尾均显示，需判断是否可滚动
				final int childFristTop = view.getChildAt(0).getTop();
				final int childLastBottom = view.getChildAt(
						visibleItemCount - 1).getBottom();
				if (childFristTop >= gridTop && childLastBottom <= gridBottom) {
					mNewScrollLocation = ScrollLocation.TB;
				} else if (childFristTop >= gridTop) {
					mNewScrollLocation = ScrollLocation.TOP;
				} else if (childLastBottom <= gridBottom) {
					mNewScrollLocation = ScrollLocation.BOTTOM;
				} else {
					mNewScrollLocation = ScrollLocation.CENTER;
				}

			} else {
				// 头尾不完全显示
				if (firstVisibleItem == 0) {
					// 头显示
					if (view.getChildAt(0).getTop() >= gridTop) {
						mNewScrollLocation = ScrollLocation.TOP;
					} else {
						mNewScrollLocation = ScrollLocation.CENTER;
					}

				} else if (firstVisibleItem + visibleItemCount == totalItemCount) {
					// 尾显示
					if (view.getChildAt(visibleItemCount - 1).getBottom() <= gridBottom) {
						mNewScrollLocation = ScrollLocation.BOTTOM;
					} else {
						mNewScrollLocation = ScrollLocation.CENTER;
					}
				} else {
					// 头尾均不显示
					mNewScrollLocation = ScrollLocation.CENTER;
				}
			}
			if (mScrollLocation != mNewScrollLocation) {
				if (mScrollLocation == ScrollLocation.NULL) {
					mScrollLocation = mNewScrollLocation;
					return;
				}
				onScrollLocationChanged(mNewScrollLocation, mScrollLocation);
				mScrollLocation = mNewScrollLocation;
			}
		}
	}

	/**
	 * 滚动位置更改
	 * 
	 * @param newlocation
	 * @param oldLocation
	 */
	protected void onScrollLocationChanged(ScrollLocation newlocation,
			ScrollLocation oldLocation) {
		if (newlocation == ScrollLocation.TOP) {
			onEdgeTop();
		} else if (newlocation == ScrollLocation.BOTTOM) {
			onEdgeBottom();
		} else if (newlocation == ScrollLocation.CENTER) {
			onCenter();
		} else {
			onEdgeAll();
		}
		
	}
	
	/**
	 * 到达顶部
	 */
	protected abstract void onEdgeTop();

	/**
	 * 到达底部
	 */
	protected abstract void onEdgeBottom();
	
	/**
	 * 全部出现
	 */
	protected void onEdgeAll() {
		
	}
	
	/**
	 * 到达中部
	 */
	protected void onCenter() {
		
	}
	
	/**
	 * 获取滚动状态
	 * 
	 * @return
	 */
	public ScrollLocation getScrollLocation() {
		return mScrollLocation;
	}
	
	/**
	 * 滚动位置改变监听器
	 * 
	 * @author AlexMofer
	 * 
	 */
	public interface OnScrollLocationChangedListener {
		public void onScrollLocationChanged(ScrollLocation newlocation,
											ScrollLocation oldLocation);
	}

}
