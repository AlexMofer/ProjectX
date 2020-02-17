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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * RecyclePagerAdapter
 * Created by Alex on 2016/3/16.
 */
@SuppressWarnings({"WeakerAccess", "deprecation", "unused"})
public abstract class RecyclePagerAdapter<VH extends RecyclePagerAdapter.PagerViewHolder>
        extends PagerAdapter {

    public static final int NO_POSITION = -1;
    public static final int INVALID_TYPE = -1;

    private final ArrayList<PagerViewHolder> mItemsInLayout = new ArrayList<>();
    private final SparseArray<ArrayList<VH>> mRecycledItems = new SparseArray<>();
    private VH mPrimaryItem;

    @Override
    public final int getCount() {
        return getItemCount();
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        // do nothing
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        VH holder;
        final int viewType = getItemViewType(position);
        final ArrayList<VH> holders = mRecycledItems.get(viewType);
        if (holders == null || holders.isEmpty())
            holder = createViewHolder(container, viewType);
        else
            holder = holders.remove(holders.size() - 1);
        bindViewHolder(holder, position);
        container.addView(holder.itemView, 0);
        mItemsInLayout.add(holder);
        return holder;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if ((!(object instanceof PagerViewHolder)))
            return;
        final PagerViewHolder holder = (PagerViewHolder) object;
        container.removeView(holder.itemView);
        mItemsInLayout.remove(holder);
        //noinspection unchecked
        recycleViewHolder((VH) holder);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if ((!(object instanceof PagerViewHolder)))
            return;
        if (mPrimaryItem == object)
            return;
        final VH old = mPrimaryItem;
        //noinspection unchecked
        mPrimaryItem = (VH) object;
        onPrimaryItemChanged(old, mPrimaryItem);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        // do nothing
    }

    @Override
    public final void startUpdate(@NonNull View container) {
        startUpdate((ViewGroup) container);
    }

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull View container, int position) {
        return instantiateItem((ViewGroup) container, position);
    }

    @Override
    public final void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        destroyItem((ViewGroup) container, position, object);
    }

    @Override
    public final void setPrimaryItem(@NonNull View container, int position, @NonNull Object object) {
        setPrimaryItem((ViewGroup) container, position, object);
    }

    @Override
    public final void finishUpdate(@NonNull View container) {
        finishUpdate((ViewGroup) container);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object instanceof PagerViewHolder && view == ((PagerViewHolder) object).itemView;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if ((!(object instanceof PagerViewHolder)))
            return POSITION_NONE;
        final PagerViewHolder holder = (PagerViewHolder) object;
        if (holder.mNeedRebind)
            return POSITION_NONE;
        return holder.mPositionChanged ? holder.mPosition : POSITION_UNCHANGED;
    }

    /**
     * 获取子项个数
     *
     * @return 子项个数
     */
    public abstract int getItemCount();

    /**
     * 获取视图类型
     *
     * @param position 位置
     * @return 视图类型
     */
    public int getItemViewType(int position) {
        return INVALID_TYPE;
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
        holder.mItemViewType = viewType;
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
     */
    public final void bindViewHolder(VH holder, int position) {
        holder.mPosition = position;
        holder.mNeedRebind = false;
        holder.mPositionChanged = false;
        onBindViewHolder(holder, position);
    }


    /**
     * 绑定ViewHolder
     *
     * @param holder   ViewHolder
     * @param position 位置
     */
    public abstract void onBindViewHolder(VH holder, int position);

    /**
     * 回收ViewHolder
     *
     * @param holder ViewHolder
     */
    private void recycleViewHolder(VH holder) {
        final int viewType = holder.mItemViewType;
        holder.mPosition = NO_POSITION;
        holder.mNeedRebind = false;
        holder.mPositionChanged = false;
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

    /**
     * 主子项已发生改变
     *
     * @param oldItem 旧的子项
     * @param newItem 新的子项
     */
    protected void onPrimaryItemChanged(VH oldItem, VH newItem) {
    }

    /**
     * 获取主子项
     *
     * @return 主子项
     */
    @Nullable
    public VH getPrimaryItem() {
        return mPrimaryItem;
    }

    @Override
    public final void notifyDataSetChanged() {
        for (PagerViewHolder holder : mItemsInLayout) {
            holder.mNeedRebind = true;
        }
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
     * 通知区域子项改变
     *
     * @param positionStart 起始子项
     * @param itemCount     子项数目
     * @see #notifyItemChanged(int)
     */
    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        if (itemCount <= 0)
            return;
        if (mItemsInLayout.isEmpty())
            return;
        boolean notify = false;
        final int positionEnd = positionStart + itemCount;
        for (PagerViewHolder holder : mItemsInLayout) {
            final int position = holder.mPosition;
            if (position >= positionStart && position < positionEnd) {
                holder.mNeedRebind = true;
                notify = true;
            }
        }
        if (notify)
            super.notifyDataSetChanged();
    }

    /**
     * 通知子项插入
     *
     * @param position 位置
     */
    public final void notifyItemInserted(int position) {
        notifyItemRangeInserted(position, 1);
    }

    /**
     * 通知区间子项插入
     *
     * @param positionStart 开始位置
     * @param itemCount     子项总数
     */
    public final void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (itemCount <= 0)
            return;
        if (mItemsInLayout.isEmpty())
            return;
        for (PagerViewHolder holder : mItemsInLayout) {
            final int position = holder.mPosition;
            if (position >= positionStart) {
                holder.mPosition = position + itemCount;
                holder.mPositionChanged = true;
            }
        }
        super.notifyDataSetChanged();
    }

    /**
     * 通知子项移除
     *
     * @param position 位置
     */
    public final void notifyItemRemoved(int position) {
        notifyItemRangeRemoved(position, 1);
    }

    /**
     * 通知区间子项移除
     *
     * @param positionStart 开始位置
     * @param itemCount     子项总数
     */
    public final void notifyItemRangeRemoved(int positionStart, int itemCount) {
        if (itemCount <= 0)
            return;
        if (mItemsInLayout.isEmpty())
            return;
        final int positionEnd = positionStart + itemCount;
        for (PagerViewHolder holder : mItemsInLayout) {
            final int position = holder.mPosition;
            if (position >= positionStart && position < positionEnd) {
                holder.mNeedRebind = true;
            } else if (position >= positionEnd) {
                holder.mPosition = position - itemCount;
                holder.mPositionChanged = true;
            }
        }
        super.notifyDataSetChanged();
    }

    /**
     * 移动子项
     *
     * @param fromPosition 开始位置
     * @param toPosition   移动到的位置
     */
    public final void notifyItemMoved(int fromPosition, int toPosition) {
        if (fromPosition == toPosition)
            return;
        final int min;
        final int max;
        boolean add;
        if (fromPosition < toPosition) {
            min = fromPosition;
            max = toPosition;
            add = false;
        } else {
            min = toPosition;
            max = fromPosition;
            add = true;
        }
        boolean notify = false;
        for (PagerViewHolder holder : mItemsInLayout) {
            final int position = holder.mPosition;
            if (position < min || position > max)
                continue;
            if (position == fromPosition) {
                holder.mPosition = toPosition;
                holder.mPositionChanged = true;
                notify = true;
                continue;
            }
            if (add)
                holder.mPosition++;
            else
                holder.mPosition--;
            holder.mPositionChanged = true;
            notify = true;
        }
        if (notify)
            super.notifyDataSetChanged();
    }

    /**
     * 配置变更
     *
     * @param newConfig 新配置
     */
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        final int size = mRecycledItems.size();
        if (size <= 0)
            return;
        for (int i = 0; i < size; i++) {
            final ArrayList<VH> holders = mRecycledItems.valueAt(i);
            if (holders == null || holders.isEmpty())
                continue;
            for (VH holder : holders) {
                holder.itemView.dispatchConfigurationChanged(newConfig);
            }
        }
    }

    /**
     * ViewHolder
     */
    public static abstract class PagerViewHolder {
        public final View itemView;
        int mItemViewType = INVALID_TYPE;

        int mPosition = NO_POSITION;
        boolean mNeedRebind = false;
        boolean mPositionChanged = false;

        public PagerViewHolder(View itemView) {
            this.itemView = itemView;
        }

        /**
         * 获取子项视图类型
         *
         * @return 子项视图类型
         */
        public final int getItemViewType() {
            return mItemViewType;
        }

        /**
         * 获取子项位置
         *
         * @return 子项位置
         */
        public final int getAdapterPosition() {
            return mPosition;
        }
    }
}
