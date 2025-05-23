/*
 * Copyright (C) 2025 AlexMofer
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

import androidx.core.util.TypedValueCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * GridLayoutManager 工具
 * Created by Alex on 2025/5/16.
 */
public class GridLayoutManagerUtils {

    private GridLayoutManagerUtils() {
        //no instance
    }

    /**
     * 设置子项列数计算器
     *
     * @param view                  RecyclerView
     * @param itemMinWidth          子项需要的最小宽度，DP为单位（以RecyclerView宽度进行均分）
     * @param roundDown             true 时向下取整，false 四舍五入
     * @param notifyItemDecorations 是否通知子项装饰刷新
     */
    public static void setSpanCountCalculator(RecyclerView view, int itemMinWidth,
                                              boolean roundDown,
                                              boolean notifyItemDecorations) {
        view.addOnLayoutChangeListener(
                (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    final int width = v.getWidth();
                    final float itemWidth =
                            TypedValueCompat.dpToPx(itemMinWidth, v.getResources().getDisplayMetrics());
                    final int count = Math.max(2, Math.round(width / itemWidth + (roundDown ? -0.5f : 0)));
                    final RecyclerView.LayoutManager manager = ((RecyclerView) v).getLayoutManager();
                    if (manager instanceof GridLayoutManager) {
                        if (count == ((GridLayoutManager) manager).getSpanCount()) {
                            return;
                        }
                        v.post(() -> {
                            ((GridLayoutManager) manager).setSpanCount(count);
                            if (notifyItemDecorations) {
                                ((RecyclerView) v).invalidateItemDecorations();
                            }
                        });
                    }
                });
    }
}
