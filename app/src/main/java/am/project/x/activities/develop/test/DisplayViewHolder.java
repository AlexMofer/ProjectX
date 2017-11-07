package am.project.x.activities.develop.test;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.widgets.display.DisplayRenderView;


/**
 * ViewHolder
 * Created by Xiang Zhicheng on 2017/10/18.
 */

class DisplayViewHolder extends RecyclerView.ViewHolder {

    private DisplayRenderView mRender;

    DisplayViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_page, parent, false));
        mRender = itemView.findViewById(R.id.idp_drv_render);
    }

    void bind(int position) {
        final int width = 600;
        final int height = 900;

        mRender.setSize(width + (position + 1) * 40, height + (position + 1) * 40);
        mRender.setText(Integer.toString(position));
    }
}
