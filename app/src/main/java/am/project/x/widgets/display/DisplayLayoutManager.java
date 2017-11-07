package am.project.x.widgets.display;

import android.content.Context;
import android.util.AttributeSet;

import am.widget.multifunctionalrecyclerview.layoutmanager.PagingLayoutManager;


/**
 * 显示布局管理器
 * Created by Xiang Zhicheng on 2017/11/6.
 */

public class DisplayLayoutManager extends PagingLayoutManager {

    public DisplayLayoutManager(Context context) {
        super(context);
        setLayoutInCenter(true);
        resetOffsetPercentage();
    }

    public DisplayLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setLayoutInCenter(true);
        resetOffsetPercentage();
    }

    public DisplayLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutInCenter(true);
        resetOffsetPercentage();
    }
}
