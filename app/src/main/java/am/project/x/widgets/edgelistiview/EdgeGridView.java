package am.project.x.widgets.edgelistiview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import am.project.support.listener.EdgeScrollListener;


/**
 * 边缘监听GridView
 * 可直接继承自AbsListView
 * 
 * @author AlexMofer
 * 
 */
public class EdgeGridView extends GridView {

	
	private EdgeScrollListener.OnScrollLocationChangedListener mListener;
	private EdgeScrollListener mEdgeScrollListener = new EdgeScrollListener() {
		
		@Override
		protected void onEdgeTop() {
			
		}
		
		@Override
		protected void onEdgeBottom() {
			
		}

		@Override
		protected void onScrollLocationChanged(ScrollLocation newlocation,
				ScrollLocation oldLocation) {
			super.onScrollLocationChanged(newlocation, oldLocation);
			invokeOnScrollLocationChanged(newlocation, oldLocation);
		}
		
		
	};
	public EdgeGridView(Context context) {
		super(context);
	}

	public EdgeGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EdgeGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		mEdgeScrollListener.onScroll(this, getFirstVisiblePosition(), getChildCount(), getCount());
	}
	

	/**
	 * 通知滚动位置更改
	 * 
	 * @param newlocation
	 * @param oldLocation
	 */
	private void invokeOnScrollLocationChanged(EdgeScrollListener.ScrollLocation newlocation,
			EdgeScrollListener.ScrollLocation oldLocation) {
		if (mListener != null)
			mListener.onScrollLocationChanged(newlocation, oldLocation);
	}

	/**
	 * 获取滚动状态
	 * 
	 * @return
	 */
	public EdgeScrollListener.ScrollLocation getScrollLocation() {
		return mEdgeScrollListener.getScrollLocation();
	}

	/**
	 * 获取滚动位置改变监听器
	 * 
	 * @return
	 */
	public EdgeScrollListener.OnScrollLocationChangedListener getOnScrollLocationChangedListener() {
		return mListener;
	}

	/**
	 * 设置滚动位置改变监听器
	 * 
	 * @param listener
	 */
	public void setOnScrollLocationChangedListener(
			EdgeScrollListener.OnScrollLocationChangedListener listener) {
		mListener = listener;
	}

	/**
	 * 移除滚动位置改变监听器
	 */
	public void removeOnScrollLocationChangedListener() {
		mListener = null;
	}
}
