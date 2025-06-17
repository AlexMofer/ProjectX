package io.github.alexmofer.projectx.business.widgets.multifunctionalrecyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;


/**
 * Adapter
 * Created by Alex on 2017/10/18.
 */

class Adapter extends MultifunctionalRecyclerView.Adapter<ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    int getChildMaxWidth() {
        return ViewHolder.getViewWidth(getItemCount());
    }

    int getChildMaxHeight() {
        return ViewHolder.getViewHeight(getItemCount());
    }
}
