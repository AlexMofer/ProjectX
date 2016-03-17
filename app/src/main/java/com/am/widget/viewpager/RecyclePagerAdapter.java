package com.am.widget.viewpager;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * RecyclePagerAdapter
 * Created by Alex on 2016/3/16.
 */
public abstract class RecyclePagerAdapter<VH extends RecyclePagerAdapter.PagerViewHolder> extends PagerAdapter {

    public static final int NO_POSITION = -1;
    private ArrayList<VH> holderList = new ArrayList<>();
    private SparseArray<ArrayList<VH>> holderSparse = new SparseArray<>();

    @Override
    public final int getCount() {
        return getItemCount();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean isViewFromObject(View view, Object object) {
        return view == ((VH) object).itemView;
    }

    public final Object instantiateItem(ViewGroup container, int position) {
        VH holder;
        int viewType = getItemViewType(position);
        ArrayList<VH> recycleHolders = holderSparse.get(viewType);
        if (recycleHolders != null && recycleHolders.size() > 0) {
            holder = recycleHolders.remove(0);
        } else {
            holder = createViewHolder(container, viewType);
        }
        bindViewHolder(holder, position);
        container.addView(holder.itemView, 0);
        return holder;
    }

    @SuppressWarnings("unchecked")
    public final void destroyItem(ViewGroup container, int position, Object object) {
        VH holder = (VH) object;
        container.removeView(holder.itemView);
        holder.isRecycled = true;
        holder.mPosition = NO_POSITION;
        int viewType = getItemViewType(position);
        ArrayList<VH> recycleHolders = holderSparse.get(viewType, new ArrayList<VH>());
        recycleHolders.add(holder);
        holderSparse.put(viewType, recycleHolders);
        onViewRecycled(holder);
    }

    @Override
    public final Object instantiateItem(View container, int position) {
        return instantiateItem((ViewPager) container, position);
    }

    @Override
    public final void destroyItem(View container, int position, Object object) {
        destroyItem((ViewPager) container, position, object);
    }

    @Override
    public final int getItemPosition(Object object) {
        if (object != null) {
            for (VH holder : holderList) {
                if (holder == object) {
                    return holder.mPosition;
                }
            }
        }
        return NO_POSITION;
    }

    public abstract int getItemCount();

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(VH holder, int position);

    public final VH createViewHolder(ViewGroup parent, int viewType) {
        final VH holder = onCreateViewHolder(parent, viewType);
        holderList.add(holder);
        return holder;
    }

    public final void bindViewHolder(VH holder, int position) {
        holder.mPosition = position;
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
        for (VH holder : holderList) {
            if (!holder.isRecycled) {
                onBindViewHolder(holder, holder.mPosition);
            }
        }
    }

    public final void notifyItemChanged(int position) {
        for (VH holder : holderList) {
            if (!holder.isRecycled && holder.mPosition == position) {
                onBindViewHolder(holder, holder.mPosition);
                break;
            }
        }
    }

    public static abstract class PagerViewHolder {
        public final View itemView;
        int mPosition = NO_POSITION;
        boolean isRecycled = false;

        public PagerViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
