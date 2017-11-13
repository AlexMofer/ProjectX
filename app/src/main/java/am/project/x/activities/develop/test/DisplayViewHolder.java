package am.project.x.activities.develop.test;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.widgets.display.DisplayLayout;
import am.project.x.widgets.display.DisplayRecyclerView;
import am.project.x.widgets.display.DisplayRenderView;


/**
 * ViewHolder
 * Created by Xiang Zhicheng on 2017/10/18.
 */

class DisplayViewHolder extends DisplayRecyclerView.ViewHolder {

    private DisplayLayout mVDisplay;
    private DisplayRenderView mVRender;

    DisplayViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_test_page, parent, false));
        mVDisplay = (DisplayLayout) itemView;
        mVRender = mVDisplay.getRender();
    }

    void bind(int displayWidth, int displayHeight, float maxWidth, float maxHeight,
              boolean isAutoFitVertical, int position) {
        final int width = 600 + (position + 1) * 40;
        final int height = 900 + (position + 1) * 40;
        mVDisplay.setDisplaySize(displayWidth, displayHeight);
        mVDisplay.setRequestedMaxSize(maxWidth, maxHeight);
        mVDisplay.setRequestedSize(width, height);
        mVDisplay.setAutoFit(isAutoFitVertical ?
                DisplayLayout.AUTO_FIT_VERTICAL : DisplayLayout.AUTO_FIT_HORIZONTAL);
        mVRender.setText(Integer.toString(position));
    }

    @Override
    public void setScale(float scale) {
        super.setScale(scale);
        mVDisplay.setScale(scale);
    }
}