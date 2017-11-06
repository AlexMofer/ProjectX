package am.project.x.widgets.display;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import am.widget.scrollbarrecyclerview.ScrollbarRecyclerView;

/**
 * 分页RecyclerView
 * Created by Alex on 2017/11/6.
 */

public class PagingRecyclerView extends ScrollbarRecyclerView {


    public PagingRecyclerView(Context context) {
        super(context);

    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
