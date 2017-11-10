package am.project.x.activities.develop.test;

import android.view.ViewGroup;

import am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;

/**
 * Adapter
 * Created by Xiang Zhicheng on 2017/10/18.
 */

class DisplayAdapter extends MultifunctionalRecyclerView.Adapter<DisplayViewHolder> {

    @Override
    public DisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DisplayViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(DisplayViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
