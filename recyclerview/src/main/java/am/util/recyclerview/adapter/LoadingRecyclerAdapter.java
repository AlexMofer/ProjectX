package am.util.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 带载入的RecyclerView.Adapter
 * Created by Alex on 2016/12/27.
 */
@SuppressWarnings("all")
public abstract class LoadingRecyclerAdapter<T, VH extends LoadingViewHolder<T>>
        extends RecyclerView.Adapter<LoadingViewHolder<T>> {

    protected final ArrayList<T> mData = new ArrayList<>();
    private boolean mHasNext = false;
    private WeakReference<VH> loadingHolder;
    private boolean isError = false;
    private boolean reBindLoading = true;

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH holder;
        if (viewType == LoadingViewHolder.LOADING) {
            holder = onCreateLoadingViewHolder(parent, viewType);
            loadingHolder = new WeakReference<>(holder);
        } else {
            holder = onCreateNormalViewHolder(parent, viewType);
        }
        return holder;
    }

    protected abstract VH onCreateNormalViewHolder(ViewGroup parent, int viewType);

    protected abstract VH onCreateLoadingViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(LoadingViewHolder<T> holder, int position) {
        if (position == mData.size()) {
            holder.bindLoading(isError);
            return;
        }
        holder.bindNormal(mData.get(position));
        if (reBindLoading && position == mData.size() - 1)
            bindLoading(isError);
    }

    @Override
    public int getItemCount() {
        final int normalItemCount = mData.size();
        return mHasNext ? normalItemCount + 1 : normalItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size())
            return LoadingViewHolder.LOADING;
        return LoadingViewHolder.NORMAL;
    }

    public void setItems(List<T> list, boolean hasNext) {
        mData.clear();
        if (list != null)
            mData.addAll(list);
        mHasNext = hasNext;
        notifyDataSetChanged();
    }

    public void addItems(List<T> list, boolean hasNext) {
        if (list == null || list.size() <= 0) {
            if (mHasNext != hasNext) {
                if (hasNext) {
                    mHasNext = true;
                    notifyItemInserted(mData.size());
                } else {
                    notifyItemRemoved(mData.size());
                    mHasNext = false;
                }
            }
        } else {
            final int normalCount = mData.size();
            if (mHasNext != hasNext) {
                if (hasNext) {
                    mData.addAll(list);
                    mHasNext = true;
                    notifyItemRangeInserted(normalCount, list.size() + 1);
                } else {
                    notifyItemRemoved(normalCount);
                    mHasNext = false;
                    mData.addAll(list);
                    notifyItemRangeInserted(normalCount, list.size());
                }
            } else {
                mData.addAll(list);
                notifyItemRangeInserted(normalCount, list.size());
                notifyItemChanged(mData.size());
            }
        }
    }

    public void setError(boolean error) {
        isError = error;
        if (mHasNext)
            notifyItemChanged(mData.size());
    }

    public void setReBindLoading(boolean reBindLoading) {
        this.reBindLoading = reBindLoading;
    }

    public void bindLoading(boolean error) {
        if (loadingHolder == null)
            return;
        VH holder = loadingHolder.get();
        if (holder == null)
            return;
        holder.bindLoading(error);
    }

    public final void notifyItemChanged(T item) {
        int position = mData.indexOf(item);
        if (position != -1)
            notifyItemChanged(position);
    }
}
