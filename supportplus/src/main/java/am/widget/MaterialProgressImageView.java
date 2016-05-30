package am.widget;

import android.content.Context;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.support.v4.widget.ProgressImageView;
import android.util.AttributeSet;

/**
 * MaterialCircleImageView
 * Created by Alex on 2015/10/27.
 */
public class MaterialProgressImageView extends ProgressImageView {
    public MaterialProgressImageView(Context context) {
        this(context, null);
    }

    public MaterialProgressImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialProgressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(new MaterialLoadingProgressDrawable(this));
    }

}
