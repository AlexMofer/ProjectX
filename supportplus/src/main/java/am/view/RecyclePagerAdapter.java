package am.view;

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
            holder.mPosition = POSITION_UNCHANGED;
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
        holder.mPosition = POSITION_NONE;
        int viewType = getItemViewType(position);
        ArrayList<VH> recycleHolders = holderSparse.get(viewType, new ArrayList<VH>());
        recycleHolders.add(holder);
        holderSparse.put(viewType, recycleHolders);
        onViewRecycled(holder);
    }

    @Override
    @SuppressWarnings("all")
    @Deprecated
    public final Object instantiateItem(View container, int position) {
        return instantiateItem((ViewPager) container, position);
    }

    @Override
    @SuppressWarnings("all")
    @Deprecated
    public final void destroyItem(View container, int position, Object object) {
        destroyItem((ViewPager) container, position, object);
    }

    @Override
    @SuppressWarnings("all")
    public final int getItemPosition(Object object) {
        int position = POSITION_UNCHANGED;
        if (object != null) {
            VH holder = (VH) object;
            if (holderList.contains(holder)) {
                position = holder.mPosition;
                position = position >= getItemCount() ? POSITION_NONE : position;
            }
        }
        return position;
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

    @SuppressWarnings("unused")
    public int getItemViewType(int position) {
        return 0;
    }

    @SuppressWarnings("unused")
    public void onViewRecycled(VH holder) {
    }

    @Override
    public final void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (VH holder : holderList) {
            if (!holder.isRecycled && holder.mPosition < getItemCount()) {
                onBindViewHolder(holder, holder.mPosition);
            }
        }
    }

    @SuppressWarnings("unused")
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
        int mPosition = POSITION_UNCHANGED;
        boolean isRecycled = false;

        public PagerViewHolder(View itemView) {
            this.itemView = itemView;
        }
    }
}
