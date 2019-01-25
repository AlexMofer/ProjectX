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

package androidx.recyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

/**
 * 公开部分包内私有的方法及保存状态的线性布局管理器
 * Created by Alex on 2017/11/3.
 */
@SuppressWarnings("unused")
public class PublicLinearLayoutManager extends LinearLayoutManager {

    private RecyclerView mRecyclerView;

    public PublicLinearLayoutManager(Context context) {
        super(context);
    }

    public PublicLinearLayoutManager(Context context, int orientation,
                                     boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PublicLinearLayoutManager(Context context, AttributeSet attrs,
                                     int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 获取目标RecyclerView
     *
     * @return RecyclerView
     */
    @Nullable
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.setRecyclerView(recyclerView);
    }

    /**
     * 获取滚动状态
     *
     * @return 滚动状态
     */
    protected int getScrollState() {
        return mRecyclerView == null ?
                RecyclerView.SCROLL_STATE_IDLE : mRecyclerView.getScrollState();
    }

    /**
     * 设置滚动状态
     *
     * @param state 滚动状态
     */
    protected void setScrollState(int state) {
        if (mRecyclerView != null)
            mRecyclerView.setScrollState(state);
    }

    /**
     * 拦截分发滚动状态变化
     *
     * @param state 状态变化
     * @return 是否拦截
     */
    protected boolean onInterceptDispatchOnScrollStateChanged(int state) {
        return false;
    }
}
