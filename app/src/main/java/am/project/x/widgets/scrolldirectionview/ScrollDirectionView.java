package am.project.x.widgets.scrolldirectionview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 滑动方向监听器
 * @author Mofer
 *
 */
public class ScrollDirectionView extends SwipeRefreshLayout {
	
	private static final int TOUCH_SLOP = 20;
	private float stateY;
	private ScrollState state;
	private OnScrollDirectionChangedListener listener;
	/**
	 * 滚动状态
	 * 
	 */
	public enum ScrollState {
		TOP, BOTTOM;
	}
	
	public ScrollDirectionView(Context context) {
		super(context);
	}

	public ScrollDirectionView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		checkUpOrDown(ev);
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 判断滑动方向
	 * 
	 * @param event
	 */
	private void checkUpOrDown(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stateY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (stateY + TOUCH_SLOP / 2 < event.getY()) {
				if (state != ScrollState.TOP) {
					notifyListener(state, ScrollState.TOP);
					state = ScrollState.TOP;
				}
				stateY = event.getY();

			} else if (stateY - TOUCH_SLOP / 2 > event.getY()) {
				if (state != ScrollState.BOTTOM) {
					notifyListener(state, ScrollState.BOTTOM);
					state = ScrollState.BOTTOM;
				}
				stateY = event.getY();
			}
			break;
		}
	}
	
	/**
	 * 修改滑动位置
	 * @param state
	 * @param listen
	 */
	public void setScrollDirectionChangedTo(ScrollState state, boolean listen) {
		if (state != this.state) {
			if (listen) {
				notifyListener(this.state, state);
			}
			this.state = state;
		}
	}
	
	private void notifyListener(ScrollState last, ScrollState currect) {
		if (listener != null) {
			listener.onDirectionChanged(this, last, currect);
		}
	}

	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnScrollDirectionChangedListener(OnScrollDirectionChangedListener listener) {
		this.listener = listener;
	}

	public interface OnScrollDirectionChangedListener {
		public void onDirectionChanged(View view,
									   ScrollState last, ScrollState currect);
	}
}
