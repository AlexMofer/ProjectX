package am.project.x.widgets.display;

import android.content.Context;
import android.util.AttributeSet;


/**
 * 显示布局管理器
 * Created by Xiang Zhicheng on 2017/11/6.
 */

public class DisplayLayoutManager extends PagingLayoutManager {

    public DisplayLayoutManager(Context context) {
        super(context);
    }

    public DisplayLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public DisplayLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onCreate() {
        setLayoutInCenter(true);
        super.onCreate();
    }
}
