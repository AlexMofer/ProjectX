/*
 * Copyright (C) 2015 AlexMofer
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

package am.util.viewpager.adapter;

import android.content.res.Configuration;
import android.util.SparseArray;
import android.view.ConfigurationHelper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * RecyclePagerAdapter
 * Created by Alex on 2016/3/16.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class RecyclePagerAdapter<VH extends RecyclePagerAdapter.PagerViewHolder>
        extends PagerAdapter {

    private ArrayList<VH> mCreatedItems = new ArrayList<>();
    private final SparseArray<ArrayList<VH>> mRecycledItems = new SparseArray<>();
    private boolean mRebindAll = false;
    private int mRebindPositionStart = -1;
    private int mRebindItemCount = 0;

    @Override
    public final int getCount() {
        return getItemCount();
    }

    /**
     * 获取子项个数
     *
     * @return 子项个数
     */
    public abstract int getItemCount();

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        VH holder;
        final int viewType = getItemViewType(position);
        final ArrayList<VH> holders = mRecycledItems.get(viewType);
        if (holders == null || holders.isEmpty())
            holder = createViewHolder(container, viewType);
        else
            holder = holders.remove(holders.size() - 1);
        bindViewHolder(holder, position, viewType);
        container.addView(holder.itemView, 0);
        return holder;
    }

    /**
     * 获取视图类型
     *
     * @param position 位置
     * @return 视图类型
     */
    public int getItemViewType(int position) {
        return 0;
    }

    /**
     * 创建ViewHolder
     *
     * @param parent   父视图
     * @param viewType 子视图类型
     * @return ViewHolder
     */
    public final VH createViewHolder(ViewGroup parent, int viewType) {
        final VH holder = onCreateViewHolder(parent, viewType);
        mCreatedItems.add(holder);
        return holder;
    }

    /**
     * 创建ViewHolder
     *
     * @param parent   父视图
     * @param viewType 子视图类型
     * @return ViewHolder
     */
    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定ViewHolder
     *
     * @param holder   ViewHolder
     * @param position 位置
     * @param viewType 视图类型
     */
    public final void bindViewHolder(VH holder, int position, int viewType) {
        holder.mPosition = position;
        holder.mViewType = viewType;
        holder.isRecycled = false;
        onBindViewHolder(holder, position);
    }

    /**
     * 绑定ViewHolder
     *
     * @param holder   ViewHolder
     * @param position 位置
     */
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public final void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        //noinspection unchecked
        final VH holder = (VH) object;
        container.removeView(holder.itemView);
        holder.isRecycled = true;
        holder.mPosition = POSITION_NONE;
        final int viewType = holder.mViewType;
        final ArrayList<VH> holders = mRecycledItems.get(viewType, new ArrayList<VH>());
        holders.add(holder);
        mRecycledItems.put(viewType, holders);
        onViewRecycled(holder);
    }

    /**
     * 销毁ViewHolder
     *
     * @param holder ViewHolder
     */
    public void onViewRecycled(VH holder) {
    }

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        //noinspection unchecked
        return view == ((VH) object).itemView;
    }


    @Override
    public final int getItemPosition(@NonNull Object object) {
        if (mRebindAll)
            return POSITION_NONE;
        //noinspection unchecked
        final int position = (((VH) object)).mPosition;
        return (position >= mRebindPositionStart &&
                position < mRebindPositionStart + mRebindItemCount) ?
                POSITION_NONE : POSITION_UNCHANGED;
    }


    @Override
    public final void notifyDataSetChanged() {
        mRebindAll = true;
        mRebindPositionStart = -1;
        mRebindItemCount = 0;
        super.notifyDataSetChanged();
    }

    /**
     * 通知子项改变
     *
     * @param position 位置
     */
    public final void notifyItemChanged(int position) {
        notifyItemRangeChanged(position, 1);
    }

    /**
     * 通知子项改变
     *
     * @param positionStart 起始子项
     * @param itemCount     子项数目
     * @see #notifyItemChanged(int)
     */
    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        mRebindAll = false;
        mRebindPositionStart = positionStart;
        mRebindItemCount = itemCount;
        super.notifyDataSetChanged();
    }

    /**
     * 配置变更
     *
     * @param newConfig 新配置
     */
    public void onConfigurationChanged(Configuration newConfig) {
        for (VH holder : mCreatedItems) {
            if (holder.isRecycled)
                ConfigurationHelper.onConfigurationChanged(holder.itemView, newConfig);
        }
    }

    public static abstract class PagerViewHolder {
        public final View itemView;
        int mPosition = POSITION_UNCHANGED;
        int mViewType = 0;
        boolean isRecycled = false;

        public PagerViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
