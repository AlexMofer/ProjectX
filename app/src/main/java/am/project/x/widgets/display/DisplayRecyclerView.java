package am.project.x.widgets.display;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import am.widget.scrollbarrecyclerview.ScrollbarRecyclerView;

/**
 * 显示RecyclerView
 * Created by Xiang Zhicheng on 2017/11/3.
 */

public class DisplayRecyclerView extends ScrollbarRecyclerView {

    public DisplayRecyclerView(Context context) {
        super(context);
    }

    public DisplayRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DisplayRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected String getScrollbarIndicator() {
        // TODO
        return "0";
    }
}
