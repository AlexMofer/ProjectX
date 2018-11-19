package am.widget.floatingactionmode.impl;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by Xiang Zhicheng on 2018/11/15.
 */
@SuppressWarnings("WeakerAccess")
final class WindowParam {
    public int x;
    public int y;
    public int width;
    public int height;
    public final Rect focus = new Rect();
    public int above;
    public int below;
    private final Rect tRect = new Rect();
    private final int[] tLocation = new int[2];

    void getParam(View target, Rect bound, boolean layoutNoLimits, boolean layoutInScreen,
                  boolean layoutInsetDecor) {
        final Rect rect = tRect;
        final int[] location = tLocation;
        final View root = target.getRootView();
        root.getLocationOnScreen(location);
        final int appScreenLocationLeft = location[0];
        final int appScreenLocationTop = location[1];
        target.getLocationOnScreen(location);
        final int viewScreenLocationLeft = location[0];
        final int viewScreenLocationTop = location[1];
        final int offsetX = viewScreenLocationLeft - appScreenLocationLeft;
        final int offsetY = viewScreenLocationTop - appScreenLocationTop;
        if (layoutNoLimits) {
            x = 0;
            y = 0;
            width = root.getWidth();
            height = root.getHeight();
            focus.set(bound);
            focus.offset(offsetX, offsetY);
            above = focus.top;
            below = height - focus.bottom;
            return;
        }
        if (!layoutInScreen) {
            target.getWindowVisibleDisplayFrame(rect);
            x = rect.left;
            y = rect.top;
            width = rect.right - rect.left;
            height = rect.bottom - rect.top;
            focus.set(bound);
            focus.offset(offsetX, offsetY);
            focus.offset(-x, -y);
            above = focus.top;
            below = height - focus.bottom;
            return;
        }
        x = 0;
        y = 0;
        if (layoutInsetDecor) {
            width = root.getWidth();
            height = root.getHeight();
            focus.set(bound);
            focus.offset(offsetX, offsetY);
            above = focus.top;
            below = height - focus.bottom;
        } else {
            target.getWindowVisibleDisplayFrame(rect);
            width = rect.left + rect.width();
            height = rect.top + rect.height();
            focus.set(bound);
            focus.offset(offsetX, offsetY);
            above = focus.top;
            below = height - focus.bottom;
        }
    }
}
