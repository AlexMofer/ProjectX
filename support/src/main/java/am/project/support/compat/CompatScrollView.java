package am.project.support.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

/**
 * 兼容性ScrollView
 */
@SuppressWarnings("unused")
public class CompatScrollView extends ScrollView {

    private CompatOnScrollChangeListener listener;
    public CompatScrollView(Context context) {
        super(context);
    }

    public CompatScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompatScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public CompatScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null)
            listener.onScrollChange(this, l, t, oldl, oldt);
    }

    /**
     * Register a callback to be invoked when the scroll X or Y positions of
     * this view change.
     * <p>
     * <b>Note:</b> Some views handle scrolling independently from View and may
     * have their own separate listeners for scroll-type events. For example,
     * {@link android.widget.ListView ListView} allows clients to register an
     * {@link android.widget.ListView#setOnScrollListener(android.widget.AbsListView.OnScrollListener) AbsListView.OnScrollListener}
     * to listen for changes in list scroll position.
     *
     * @param l The listener to notify when the scroll X or Y position changes.
     * @see android.view.View#getScrollX()
     * @see android.view.View#getScrollY()
     */
    public void setCompatOnScrollChangeListener(final CompatOnScrollChangeListener l) {
        if (Build.VERSION.SDK_INT >= 23) {
            setOnScrollChangeListener(new OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY,
                                           int oldScrollX, int oldScrollY) {
                    if (l != null)
                        l.onScrollChange(v, scrollX, scrollY,
                                oldScrollX, oldScrollY);
                }
            });
        } else {
            listener = l;
        }
    }

}
