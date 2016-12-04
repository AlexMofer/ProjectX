package am.widget.smoothinputlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 顺滑的输入面板
 * Created by Alex on 2016/12/4.
 */

public class SmoothInputLayout extends LinearLayout {

    public SmoothInputLayout(Context context) {
        super(context);
    }

    public SmoothInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public SmoothInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SmoothInputLayout(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
