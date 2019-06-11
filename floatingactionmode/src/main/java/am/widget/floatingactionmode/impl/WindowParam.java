/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.widget.floatingactionmode.impl;

import android.graphics.Rect;
import android.view.View;

/**
 * 窗口参数
 * Created by Alex on 2018/11/21.
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
                  boolean layoutInsetDecor, boolean inMultiWindowMode) {
        final Rect rect = tRect;
        final int[] location = tLocation;
        final View root = target.getRootView();
        root.getLocationOnScreen(location);
        final int mwx;
        final int mwy;
        if (inMultiWindowMode) {
            mwx = location[0];
            mwy = location[1];
            location[0] = 0;
            location[1] = 0;
        } else {
            mwx = mwy = 0;
        }
        final int appScreenLocationLeft = location[0];
        final int appScreenLocationTop = location[1];
        target.getLocationOnScreen(location);
        if (inMultiWindowMode) {
            location[0] -= mwx;
            location[1] -= mwy;
        }
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
            if (inMultiWindowMode) {
                rect.left -= mwx;
                rect.right -= mwx;
                rect.top -= mwy;
                rect.bottom -= mwy;
            }
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
            if (inMultiWindowMode) {
                rect.left -= mwx;
                rect.right -= mwx;
                rect.top -= mwy;
                rect.bottom -= mwy;
            }
            width = rect.left + rect.width();
            height = rect.top + rect.height();
            focus.set(bound);
            focus.offset(offsetX, offsetY);
            above = focus.top;
            below = height - focus.bottom;
        }
    }
}
