package am.project.x.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 尾部载入的ViewHolder
 * Created by Alex on 2016/12/27.
 */

public abstract class LoadingViewHolder<T> extends RecyclerView.ViewHolder {

    public static final int NORMAL = 0;
    public static final int LOADING = 1;
    protected final int viewType;
    protected T data;

    public LoadingViewHolder(View itemView, int viewType) {
        super(itemView);
        this.viewType = viewType;
    }

    public void bindNormal(T data) {
        if (viewType != NORMAL)
            return;
        this.data = data;
        onBindNormal(data);
    }

    protected abstract void onBindNormal(T data);

    public void bindLoading(boolean error) {
        if (viewType != LOADING)
            return;
        onBindLoading(error);
    }

    protected abstract void onBindLoading(boolean error);

}