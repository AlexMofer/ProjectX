package am.widget.floatingactionmode.impl;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by Xiang Zhicheng on 2018/11/15.
 */
@SuppressWarnings("WeakerAccess")
final class WindowParam {
    public final Rect focus = new Rect();
    private final Rect tRect = new Rect();
    private final int[] tLocation = new int[2];
    public int x;
    public int y;
    public int width;
    public int height;
    public int above;
    public int below;

    void getParam(View target, Rect bound) {
        final Rect rect = tRect;
        target.getWindowVisibleDisplayFrame(rect);
        x = rect.left;
        y = rect.top;
        width = rect.right - rect.left;
        height = rect.bottom - rect.top;
        final int[] location = tLocation;
        target.getRootView().getLocationOnScreen(location);
        final int appScreenLocationLeft = location[0];
        final int appScreenLocationTop = location[1];
        target.getLocationOnScreen(location);
        final int viewScreenLocationLeft = location[0];
        final int viewScreenLocationTop = location[1];
        final int offsetX = viewScreenLocationLeft - appScreenLocationLeft;
        final int offsetY = viewScreenLocationTop - appScreenLocationTop;
        focus.set(bound);
        focus.offset(offsetX, offsetY);
        focus.offset(-x, -y);
        above = focus.top;
        below = height - focus.bottom;
    }
}
