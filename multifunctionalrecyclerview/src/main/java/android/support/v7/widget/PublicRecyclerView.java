/*
 * Copyright (C) 2017 AlexMofer
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

package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

/**
 * 公开部分包内私有的方法及变量的RecyclerView
 * Created by Alex on 2017/11/8.
 */
public class PublicRecyclerView extends RecyclerView {

    public PublicRecyclerView(Context context) {
        super(context);
    }

    public PublicRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PublicRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 获取默认滚动补帧器
     *
     * @return 默认滚动补帧器
     */
    public static Interpolator getScrollerInterpolator() {
        return sQuinticInterpolator;
    }

    @Override
    protected void setScrollState(int state) {
        super.setScrollState(state);
    }

    @Override
    protected void dispatchOnScrollStateChanged(int state) {
        if (onInterceptDispatchOnScrollStateChanged(state))
            return;
        super.dispatchOnScrollStateChanged(state);
    }

    /**
     * 拦截分发滚动状态变化
     *
     * @param state 状态变化
     * @return 是否拦截
     */
    protected boolean onInterceptDispatchOnScrollStateChanged(int state) {
        final LayoutManager layoutManager = getLayoutManager();
        return layoutManager instanceof PublicLinearLayoutManager &&
                ((PublicLinearLayoutManager) layoutManager)
                        .onInterceptDispatchOnScrollStateChanged(state);
    }
}
