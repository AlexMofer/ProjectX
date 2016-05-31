package am.widget;

import android.content.Context;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * MaterialImageView
 * Created by Alex on 2015/11/13.
 */
public class MaterialProgressImageView extends ImageView {

    private MaterialLoadingProgressDrawable drawable = new MaterialLoadingProgressDrawable(this);

    public MaterialProgressImageView(Context context) {
        super(context);
        setImageDrawable(drawable);
    }

    public MaterialProgressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageDrawable(drawable);
    }

    public MaterialProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(drawable);
    }

    public void start() {
        drawable.start();
    }

    public void stop() {
        drawable.stop();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE)
            drawable.start();
        else
            drawable.stop();
    }
}
