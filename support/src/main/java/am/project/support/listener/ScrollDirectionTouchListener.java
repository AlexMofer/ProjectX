package am.project.support.listener;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;

/**
 * 滑动方向触摸监听
 */
@SuppressWarnings("all")
public abstract class ScrollDirectionTouchListener implements OnTouchListener {

    public static final int STATE_TOP = 0;
    public static final int STATE_BOTTOM = 1;
    private int mTouchSlop;
    private float stateY;
    private int state = -1;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mTouchSlop == 0) {
            mTouchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        }
        checkUpOrDown(event);
        return false;
    }

    /**
     * 修改滑动位置
     *
     * @param direction 方向
     * @param listen    是否监听
     */
    public void setScrollDirection(int direction, boolean listen) {
        if (direction != STATE_TOP && direction != STATE_BOTTOM) {
            return;
        }
        if (listen) {
            switch (state) {
                default:
                    if (direction == STATE_TOP) {
                        onScrollUp();
                    } else {
                        onScrollDown();
                    }
                    break;
                case STATE_TOP:
                    if (direction == STATE_BOTTOM) {
                        onScrollDown();
                    }
                    break;
                case STATE_BOTTOM:
                    if (direction == STATE_TOP) {
                        onScrollUp();
                    }
                    break;
            }
        }
    }

    /**
     * 判断滑动方向
     *
     * @param event MotionEvent
     */
    private void checkUpOrDown(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stateY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (stateY + mTouchSlop / 2 < event.getY()) {
                    if (state != STATE_TOP) {
                        onScrollUp();
                        state = STATE_TOP;
                    }
                    stateY = event.getY();

                } else if (stateY - mTouchSlop / 2 > event.getY()) {
                    if (state != STATE_BOTTOM) {
                        onScrollDown();
                        state = STATE_BOTTOM;
                    }
                    stateY = event.getY();
                }
                break;
        }
    }

    /**
     * 向上滚动
     */
    public abstract void onScrollUp();

    /**
     * 向下滚动
     */
    public abstract void onScrollDown();
}
