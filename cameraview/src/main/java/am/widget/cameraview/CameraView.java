package am.widget.cameraview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * 摄像头视图
 * Created by Alex on 2017/2/7.
 */

public class CameraView extends ViewGroup {


    public CameraView(Context context) {
        super(context);

        initView(null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {

        TypedArray custom = getContext().obtainStyledAttributes(attrs, R.styleable.CameraView);

        custom.recycle();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
