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
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

/**
 * RecyclePagerAdapter
 * Created by Alex on 2016/3/16.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class RecyclePagerAdapter<VH extends RecyclePagerAdapter.PagerViewHolder>
        extends PagerAdapter {

    private ArrayList<VH> mRunningItems = new ArrayList<>();
    private SparseArray<ArrayList<VH>> mRecycledItems = new SparseArray<>();

    @Override
    public final int getCount() {
        return getItemCount();
    }

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        //noinspection unchecked
        return view == ((VH) object).itemView;
    }

    @NonNull
    @Override
    public final Object instantiateItem(@NonNull ViewGroup container, int position) {
        VH holder;
        int viewType = getItemViewType(position);
        ArrayList<VH> recycleHolders = mRecycledItems.get(viewType);
        if (recycleHolders != null && recycleHolders.size() > 0) {
            holder = recycleHolders.remove(0);
            holder.mPosition = POSITION_UNCHANGED;
            holder.mViewType = viewType;
        } else {
            holder = createViewHolder(container, viewType);
        }
        bindViewHolder(holder, position, viewType);
        container.addView(holder.itemView, 0);
        return holder;
    }

    @Override
    public final void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        //noinspection unchecked
        VH holder = (VH) object;
        container.removeView(holder.itemView);
        holder.isRecycled = true;
        holder.mPosition = POSITION_NONE;
        ArrayList<VH> recycleHolders = mRecycledItems.get(holder.mViewType, new ArrayList<VH>());
        recycleHolders.add(holder);
        mRecycledItems.put(holder.mViewType, recycleHolders);
        onViewRecycled(holder);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public final Object instantiateItem(@NonNull View container, int position) {
        return instantiateItem((ViewPager) container, position);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Deprecated
    public final void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        destroyItem((ViewPager) container, position, object);
    }

    @Override
    public final int getItemPosition(@NonNull Object object) {
        int position = POSITION_UNCHANGED;
        //noinspection unchecked
        VH holder = (VH) object;
        if (mRunningItems.contains(holder)) {
            position = holder.mPosition;
            position = position >= getItemCount() ? POSITION_NONE : position;
        }

        return position;
    }

    public abstract int getItemCount();

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(VH holder, int position);

    public final VH createViewHolder(ViewGroup parent, int viewType) {
        final VH holder = onCreateViewHolder(parent, viewType);
        mRunningItems.add(holder);
        return holder;
    }

    @Deprecated
    public final void bindViewHolder(VH holder, int position) {
        bindViewHolder(holder, position, getItemViewType(position));
    }

    public final void bindViewHolder(VH holder, int position, int viewType) {
        holder.mPosition = position;
        holder.mViewType = viewType;
        holder.isRecycled = false;
        onBindViewHolder(holder, position);
    }

    public int getItemViewType(int position) {
        return 0;
    }

    public void onViewRecycled(VH holder) {
    }

    @Override
    public final void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (VH holder : mRunningItems) {
            if (!holder.isRecycled && holder.mPosition < getItemCount()) {
                onBindViewHolder(holder, holder.mPosition);
            }
        }
    }

    public final void notifyItemChanged(int position) {
        for (VH holder : mRunningItems) {
            if (!holder.isRecycled && holder.mPosition == position) {
                onBindViewHolder(holder, holder.mPosition);
                break;
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        final int count = mRecycledItems.size();
        for (int i = 0; i < count; i++) {
            final ArrayList<VH> holders = mRecycledItems.valueAt(i);
            if (holders == null || holders.isEmpty())
                continue;
            for (VH holder : holders) {
                if (holder == null || holder.itemView == null)
                    continue;
                ConfigurationHelper.onConfigurationChanged(holder.itemView, newConfig);
            }
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
