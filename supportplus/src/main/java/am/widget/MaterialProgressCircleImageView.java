package am.widget;

import android.content.Context;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.support.v4.widget.ShadowsCircleImageView;
import android.util.AttributeSet;

/**
 * MaterialCircleImageView
 * Created by Alex on 2015/10/27.
 */
public class MaterialProgressCircleImageView extends ShadowsCircleImageView {

    private MaterialLoadingProgressDrawable drawable = new MaterialLoadingProgressDrawable(this);
    public MaterialProgressCircleImageView(Context context) {
        this(context, null);
    }

    public MaterialProgressCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(drawable);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE)
            start();
        else
            stop();
    }

    public void start() {
        drawable.start();
    }

    public void stop() {
        drawable.stop();
    }

    @SuppressWarnings("unused")
    public void setLoading(boolean loading) {
        setVisibility(loading ? VISIBLE : GONE);
    }

}
