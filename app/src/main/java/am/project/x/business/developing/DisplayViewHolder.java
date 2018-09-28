package am.project.x.business.developing;

import android.view.ViewGroup;

import am.project.x.business.developing.display.DisplayLayout;
import am.project.x.business.developing.display.DisplayRecyclerView;
import am.project.x.business.developing.display.DisplayRenderView;


/**
 * ViewHolder
 * Created by Xiang Zhicheng on 2017/10/18.
 */

class DisplayViewHolder extends DisplayRecyclerView.ViewHolder {

    private DisplayLayout mVDisplay;
    private DisplayRenderView mVRender;

    DisplayViewHolder(ViewGroup parent) {
        super(new DisplayLayout(parent.getContext()));
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