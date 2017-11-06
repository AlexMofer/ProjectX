package am.project.x.widgets.display;

import android.content.Context;
import android.support.v7.widget.LinearSmoothScroller;
import android.view.View;

/**
 * 分页平滑滚动器
 * Created by Alex on 2017/11/6.
 */
class PagingSmoothScroller extends LinearSmoothScroller {

    PagingSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public int calculateDxToMakeVisible(View view, int snapPreference) {
        return 0;
    }

    @Override
    public int calculateDyToMakeVisible(View view, int snapPreference) {
        return super.calculateDyToMakeVisible(view, snapPreference);
    }
}

