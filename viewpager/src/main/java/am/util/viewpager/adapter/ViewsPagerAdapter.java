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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 视图集PagerAdapter
 * Created by Alex on 2016/3/16.
 */
@SuppressWarnings({"WeakerAccess", "deprecation", "unused"})
public class ViewsPagerAdapter extends PagerAdapter {

    private final ArrayList<ViewHolder> mItems = new ArrayList<>();
    private final ArrayList<ViewHolder> mItemsInLayout = new ArrayList<>();
    private final ArrayList<ViewHolder> mItemsShouldCheck = new ArrayList<>();
    private ViewHolder mPrimaryItem;

    public ViewsPagerAdapter() {
    }

    public ViewsPagerAdapter(@NonNull Collection<? extends View> views,
                             @Nullable Collection<? extends CharSequence> titles) {
        addAll(views, titles);
    }

    public ViewsPagerAdapter(@NonNull Collection<? extends View> views) {
        this(views, null);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        // do nothing
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        final ViewHolder holder = mItems.get(position);
        holder.mPosition = position;
        holder.mNeedRebind = false;
        holder.mPositionChanged = false;
        holder.mRemoved = false;
        final View view = holder.itemView;
        onBindView(view, position);
        container.addView(view);
        mItemsInLayout.add(holder);
        return holder;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if ((!(object instanceof ViewHolder)))
            return;
        final ViewHolder holder = (ViewHolder) object;
        final View view = holder.itemView;
        container.removeView(view);
        mItemsInLayout.remove(holder);
        mItemsShouldCheck.add(holder);
        onUnbindView(view, position);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if ((!(object instanceof ViewHolder)))
            return;
        if (mPrimaryItem == object)
            return;
        final View old = getPrimaryItem();
        mPrimaryItem = (ViewHolder) object;
        onPrimaryItemChanged(old, mPrimaryItem.itemView);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        for (ViewHolder holder : mItemsShouldCheck) {
            if (holder.mRemoved)
                holder.recycle();
        }
        mItemsShouldCheck.clear();
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
        return object instanceof ViewHolder && view == ((ViewHolder) object).itemView;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if ((!(object instanceof ViewHolder)))
            return POSITION_NONE;
        final ViewHolder holder = (ViewHolder) object;
        if (holder.mRemoved)
            return POSITION_NONE;
        if (holder.mNeedRebind)
            return POSITION_NONE;
        return holder.mPositionChanged ? holder.mPosition : POSITION_UNCHANGED;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return (position < 0 || position >= mItems.size()) ? null : mItems.get(position).mTitle;
    }

    /**
     * 绑定子View
     *
     * @param view     子View
     * @param position 位置
     */
    public void onBindView(@NonNull View view, int position) {
    }

    /**
     * 解绑子View
     *
     * @param view     子View
     * @param position 位置
     */
    public void onUnbindView(@NonNull View view, int position) {
    }

    /**
     * 主子项已发生改变
     *
     * @param oldItem 旧的子项
     * @param newItem 新的子项
     */
    protected void onPrimaryItemChanged(@Nullable View oldItem, @NonNull View newItem) {
    }

    /**
     * 获取主子项
     *
     * @return 主子项
     */
    @Nullable
    public View getPrimaryItem() {
        return mPrimaryItem == null ? null : mPrimaryItem.itemView;
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
     * 通知区间内子项改变
     *
     * @param positionStart 开始位置
     * @param itemCount     总数
     */
    public final void notifyItemRangeChanged(int positionStart, int itemCount) {
        if (itemCount <= 0)
            return;
        for (int i = 0; i < itemCount; i++) {
            final ViewHolder holder = mItems.get(positionStart + i);
            holder.mNeedRebind = true;
        }
        notifyDataSetChanged();
    }

    /**
     * 设置子视图与标题
     *
     * @param position 位置
     * @param view     子视图
     * @param title    标题
     */
    public void set(int position, @NonNull View view, @Nullable CharSequence title) {
        if (position < 0 || position >= mItems.size())
            return;
        final ViewHolder old = mItems.get(position);
        old.mRemoved = true;
        mItemsShouldCheck.add(old);
        final ViewHolder holder = ViewHolderFactory.getInstance().get();
        holder.set(view, title);
        holder.mPosition = position;
        mItems.set(position, holder);
        notifyDataSetChanged();
    }

    /**
     * 设置子视图
     *
     * @param position 位置
     * @param view     子视图
     */
    public void setView(int position, @NonNull View view) {
        set(position, view, getPageTitle(position));
    }

    /**
     * 设置标题
     * 修改标题不会触发刷新
     *
     * @param position 位置
     * @param title    标题
     */
    public void setTitle(int position, @Nullable CharSequence title) {
        if (position < 0 || position >= mItems.size())
            return;
        mItems.get(position).mTitle = title;
    }

    /**
     * 添加视图
     *
     * @param view  视图
     * @param title 标题
     */
    public void add(@NonNull View view, @Nullable CharSequence title) {
        final ViewHolder holder = ViewHolderFactory.getInstance().get();
        holder.set(view, title);
        holder.mPosition = mItems.size();
        mItems.add(holder);
        notifyDataSetChanged();
    }

    /**
     * 添加视图
     *
     * @param view 视图
     */
    public void addView(View view) {
        add(view, null);
    }

    /**
     * 在指定位置添加视图
     *
     * @param position 位置
     * @param view     视图
     * @param title    标题
     */
    public void add(int position, View view, CharSequence title) {
        final ViewHolder holder = ViewHolderFactory.getInstance().get();
        holder.set(view, title);
        holder.mPosition = position;
        mItems.add(position, holder);
        final int count = getCount();
        for (int i = position; i < count; i++) {
            final ViewHolder item = mItems.get(i);
            item.mPosition = i;
            if (mItemsInLayout.contains(item))
                item.mPositionChanged = true;
        }
        notifyDataSetChanged();
    }

    /**
     * 添加视图
     *
     * @param position 位置
     * @param view     视图
     */
    public void addView(int position, View view) {
        add(position, view, null);
    }

    /**
     * 移除子项
     *
     * @param position 位置
     */
    public void remove(int position) {
        final ViewHolder removed = mItems.remove(position);
        removed.mRemoved = true;
        mItemsShouldCheck.add(removed);
        final int count = getCount();
        for (int i = position; i < count; i++) {
            final ViewHolder item = mItems.get(i);
            item.mPosition = i;
            if (mItemsInLayout.contains(item))
                item.mPositionChanged = true;
        }
        notifyDataSetChanged();
    }

    /**
     * 移除子项
     *
     * @param view 子项
     * @return 是否成功
     */
    public boolean remove(@NonNull View view) {
        final int count = mItems.size();
        for (int i = 0; i < count; i++) {
            final ViewHolder holder = mItems.get(i);
            if (holder.itemView == view) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加所有
     *
     * @param views  子项视图
     * @param titles 子项标题
     */
    public void addAll(@NonNull Collection<? extends View> views,
                       @Nullable Collection<? extends CharSequence> titles) {
        if (views.isEmpty())
            return;
        int position = mItems.size();
        int index = -1;
        boolean changed = false;
        final CharSequence[] ts = titles == null ? null : (CharSequence[]) titles.toArray();
        for (View view : views) {
            index++;
            if (view == null)
                continue;
            changed = true;
            final CharSequence title = ts == null ? null : (index >= ts.length ? null : ts[index]);
            final ViewHolder holder = ViewHolderFactory.getInstance().get();
            holder.set(view, title);
            holder.mPosition = position;
            mItems.add(holder);
            position++;
        }
        if (changed)
            notifyDataSetChanged();
    }

    /**
     * 添加所有
     *
     * @param views 子项视图
     */
    public void addAllViews(@NonNull Collection<? extends View> views) {
        addAll(views, null);
    }

    /**
     * 添加所有
     *
     * @param views 子项视图
     */
    public void addAllViews(View... views) {
        if (views == null || views.length <= 0)
            return;
        int position = mItems.size();
        boolean changed = false;
        for (View view : views) {
            if (view == null)
                continue;
            changed = true;
            final ViewHolder holder = ViewHolderFactory.getInstance().get();
            holder.set(view, null);
            holder.mPosition = position;
            mItems.add(holder);
            position++;
        }
        if (changed)
            notifyDataSetChanged();
    }

    /**
     * 清空
     */
    public void clear() {
        if (mItems.isEmpty())
            return;
        for (ViewHolder holder : mItems) {
            holder.mRemoved = true;
        }
        mItemsShouldCheck.addAll(mItems);
        mItems.clear();
        notifyDataSetChanged();
    }

    /**
     * 移动子项
     *
     * @param fromPosition 原始位置
     * @param toPosition   新位置
     */
    public void move(int fromPosition, int toPosition) {
        if (fromPosition == toPosition)
            return;
        final ViewHolder formHolder = mItems.remove(fromPosition);
        mItems.add(toPosition, formHolder);
        final int count = mItems.size();
        for (int i = 0; i < count; i++) {
            final ViewHolder holder = mItems.get(i);
            if (holder.mPosition == i)
                continue;
            holder.mPosition = i;
            if (mItemsInLayout.contains(holder))
                holder.mPositionChanged = true;
        }
        notifyDataSetChanged();
    }

    /**
     * 配置变更
     *
     * @param newConfig 新配置
     */
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (mItems.isEmpty())
            return;
        for (ViewHolder holder : mItems) {
            if (!mItemsInLayout.contains(holder))
                holder.itemView.dispatchConfigurationChanged(newConfig);
        }
    }

    private static final class ViewHolder {
        View itemView;
        CharSequence mTitle;
        boolean mNeedRebind = false;
        boolean mPositionChanged = false;
        int mPosition = 0;
        boolean mRemoved = false;

        void recycle() {
            if (itemView == null)
                return;
            itemView = null;
            mTitle = null;
            mNeedRebind = false;
            mPositionChanged = false;
            mPosition = -1;
            mRemoved = false;
            ViewHolderFactory.getInstance().put(this);
        }

        void set(@NonNull View view, @Nullable CharSequence title) {
            itemView = view;
            mTitle = title;
            mNeedRebind = false;
            mPositionChanged = false;
            mPosition = 0;
            mRemoved = false;
        }
    }

    private static final class ViewHolderFactory {

        private static ViewHolderFactory mInstance;
        private final ArrayList<ViewHolder> mRecycledViewHolders = new ArrayList<>();

        private ViewHolderFactory() {
            //no instance
        }

        static ViewHolderFactory getInstance() {
            if (mInstance == null)
                mInstance = new ViewHolderFactory();
            return mInstance;
        }

        @NonNull
        ViewHolder get() {
            return mRecycledViewHolders.isEmpty() ? new ViewHolder() :
                    mRecycledViewHolders.remove(mRecycledViewHolders.size() - 1);
        }

        void put(@NonNull ViewHolder holder) {
            mRecycledViewHolders.add(holder);
        }
    }
}
