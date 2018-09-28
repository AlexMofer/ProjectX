package am.project.x.business.developing.display;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;

import am.widget.multifunctionalrecyclerview.MultifunctionalLinearLayoutManager;


/**
 * 显示布局管理器
 * Created by Xiang Zhicheng on 2017/11/6.
 */

class DisplayLayoutManager extends MultifunctionalLinearLayoutManager {

    private final Rect mDecoration = new Rect();
    private Location mLocation;

    DisplayLayoutManager(Context context) {
        super(context);
        setLayoutInCenter(true);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        if (mLocation != null) {
            setDisplayLocation(mLocation.position, mLocation.x, mLocation.y,
                    mLocation.last, mLocation.maxChildWidth, mLocation.maxChildHeight,
                    mLocation.width, mLocation.height);
            mLocation = null;
        }
    }

    void setDisplayLocation(int position, int offset, float percentage) {
        setAnotherDirectionScrollOffsetPercentage(percentage);
        scrollToPositionWithOffset(position, offset);
    }

    void setDisplayLocation(int position, float x, float y, boolean last,
                            float maxChildWidth, float maxChildHeight,
                            float width, float height) {
        if (!isAttachedToWindow()) {
            mLocation = new Location(position, x, y, last, maxChildWidth, maxChildHeight,
                    width, height);
            return;
        }
        final int offset = computeOffset(position, x, y, last, maxChildWidth, maxChildHeight,
                width, height);
        final float percentage = computePercentage(x, y, maxChildWidth, maxChildHeight,
                width, height);
        setAnotherDirectionScrollOffsetPercentage(percentage);
        scrollToPositionWithOffset(position, offset);
    }

    private int computeOffset(int position, float x, float y, boolean last,
                              float maxChildWidth, float maxChildHeight,
                              float width, float height) {
        if (getOrientation() == HORIZONTAL && x == 0)
            return 0;
        if (getOrientation() != HORIZONTAL && y == 0)
            return 0;
        final RecyclerView view = getRecyclerView();
        if (!(view instanceof DisplayRecyclerView))
            return 0;
        final DisplayRecyclerView display = (DisplayRecyclerView) view;
        final int type = position == 0 ? DisplayItemDecoration.TYPE_FIRST :
                (last ? DisplayItemDecoration.TYPE_LAST : DisplayItemDecoration.TYPE_NORMAL);
        final float scale = display.getScale();
        if (getOrientation() == HORIZONTAL) {
            display.getHorizontalDecoration(mDecoration, type);
            final float maxHeight = display.getDisplayHeight();
            final float p = maxHeight / maxChildHeight;
            final float offset = width * p * scale * x;
            return -Math.round(mDecoration.left + offset);
        } else {
            display.getVerticalDecoration(mDecoration, type);
            final float maxWidth = display.getDisplayWidth();
            final float p = maxWidth / maxChildWidth;
            final float offset = height * p * scale * y;
            return -Math.round(mDecoration.top + offset);
        }
    }

    private float computePercentage(float x, float y,
                                    float maxChildWidth, float maxChildHeight,
                                    float width, float height) {
        if (getOrientation() == HORIZONTAL && y == 0)
            return 0;
        if (getOrientation() != HORIZONTAL && x == 0)
            return 0;
        final RecyclerView view = getRecyclerView();
        if (!(view instanceof DisplayRecyclerView))
            return 0;
        final DisplayRecyclerView display = (DisplayRecyclerView) view;
        final float scale = display.getScale();
        if (getOrientation() == HORIZONTAL) {
            final float maxHeight = display.getDisplayHeight();
            final float p = maxHeight / maxChildHeight;
            final float offset = (maxHeight * scale - height * p * scale) * 0.5f
                    + height * p * scale * y;
            if (offset <= 0)
                return 0;
            else if (offset >= 1)
                return 1;
            else
                return offset;
        } else {
            final float maxWidth = display.getDisplayWidth();
            final float p = maxWidth / maxChildWidth;
            final float offset = (maxWidth * scale - width * p * scale) * 0.5f
                    + width * p * scale * x;
            if (offset <= 0)
                return 0;
            else if (offset >= 1)
                return 1;
            else
                return offset;
        }
    }

    private class Location {
        private int position;
        private float x;
        private float y;
        private boolean last;
        private float maxChildWidth;
        private float maxChildHeight;
        private float width;
        private float height;

        private Location(int position, float x, float y, boolean last,
                         float maxChildWidth, float maxChildHeight, float width, float height) {
            this.position = position;
            this.x = x;
            this.y = y;
            this.last = last;
            this.maxChildWidth = maxChildWidth;
            this.maxChildHeight = maxChildHeight;
            this.width = width;
            this.height = height;
        }
    }

}
