/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.graphics.Rect;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

/**
 * ScrollView 工具类
 * Created by Alex on 2026/6/24.
 */
public final class ScrollViewUtils {

    private ScrollViewUtils() {
        //no instance
    }

    /**
     * 滚动到当前聚焦的子 View (或子子 View)
     *
     * @param view ScrollView
     */
    public static void scrollToFocusChild(@NonNull ScrollView view, int offsetTop) {
        final View focusedView = view.findFocus();
        if (focusedView == null || focusedView == view) {
            return;
        }
        final Rect rect = new Rect();
        focusedView.getDrawingRect(rect);
        view.offsetDescendantRectToMyCoords(focusedView, rect);

        int scrollToY = rect.top - offsetTop;
        if (scrollToY < 0) {
            scrollToY = 0;
        }
        view.smoothScrollTo(0, scrollToY);
    }
}
