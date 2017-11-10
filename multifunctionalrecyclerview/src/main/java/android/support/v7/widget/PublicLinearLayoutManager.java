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
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 公开部分包内私有的方法及保存状态的线性布局管理器
 * Created by Alex on 2017/11/3.
 */
@SuppressWarnings("all")
public class PublicLinearLayoutManager extends LinearLayoutManager {

    private RecyclerView mView;
    private boolean mSizeUnlimited = false;

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

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        mView = view;
        super.onAttachedToWindow(view);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        mView = null;
    }

    /**
     * 获取目标RecyclerView
     *
     * @return RecyclerView
     */
    @Nullable
    protected RecyclerView getRecyclerView() {
        return mView;
    }

    /**
     * 获取滚动状态
     *
     * @return 滚动状态
     */
    protected int getScrollState() {
        return mView == null ? RecyclerView.SCROLL_STATE_IDLE : mView.getScrollState();
    }

    /**
     * 设置滚动状态
     *
     * @param state 滚动状态
     */
    protected void setScrollState(int state) {
        if (mView == null)
            return;
        mView.setScrollState(state);
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

    @Override
    public int getHeightMode() {
        return mSizeUnlimited ? View.MeasureSpec.UNSPECIFIED : super.getHeightMode();
    }

    @Override
    public int getWidthMode() {
        return mSizeUnlimited ? View.MeasureSpec.UNSPECIFIED : super.getWidthMode();
    }

    /**
     * 设置尺寸不受限制
     *
     * @param unlimited 是否不受限制
     */
    public void setSizeUnlimited(boolean unlimited) {
        if (mSizeUnlimited == unlimited)
            return;
        mSizeUnlimited = unlimited;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        return new BaseSavedState(bundle, (SavedState) super.onSaveInstanceState());
    }

    /**
     * 存储状态
     *
     * @param bundle 数据包
     */
    protected void onSaveInstanceState(Bundle bundle) {

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseSavedState savedState = (BaseSavedState) state;
        onRestoreInstanceState(savedState.getState());
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    /**
     * 恢复状态
     *
     * @param bundle 数据包
     */
    protected void onRestoreInstanceState(Bundle bundle) {

    }

    private static class BaseSavedState implements Parcelable {

        public static final Creator<BaseSavedState> CREATOR
                = new Creator<BaseSavedState>() {
            @Override
            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            @Override
            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        };
        private final SavedState mSuperState;
        private final Bundle mState;

        private BaseSavedState(Bundle state, SavedState superState) {
            mState = state;
            mSuperState = superState;
        }

        private BaseSavedState(Parcel source) {
            mSuperState = source.readParcelable(SavedState.class.getClassLoader());
            mState = source.readBundle();
        }

        private Parcelable getSuperState() {
            return mSuperState;
        }

        private Bundle getState() {
            return mState;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mSuperState, flags);
            dest.writeBundle(mState);
        }
    }
}
