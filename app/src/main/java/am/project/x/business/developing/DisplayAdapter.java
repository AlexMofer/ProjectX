package am.project.x.business.developing;

import android.view.ViewGroup;

import am.project.x.business.developing.display.DisplayRecyclerView;


/**
 * Adapter
 * Created by Xiang Zhicheng on 2017/10/18.
 */

class DisplayAdapter extends DisplayRecyclerView.Adapter<DisplayViewHolder> {


    DisplayAdapter() {
    }

    @Override
    public DisplayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DisplayViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(DisplayViewHolder holder, int position) {
        holder.bind(getDisplayWidth(), getDisplayHeight(), getChildMaxWidth(), getChildMaxHeight(),
                isHorizontal(), position);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    protected float getChildMaxWidth() {
        return 600 + 20 * 40;
    }

    @Override
    protected float getChildMaxHeight() {
        return 900 + 20 * 40;
    }
}
