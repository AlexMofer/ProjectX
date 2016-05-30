package am.project.supportplus.widget;

import android.content.Context;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.support.v4.widget.ProgressImageView;

/**
 * MaterialCircleImageView
 * Created by Alex on 2015/10/27.
 */
public class MaterialCircleImageView extends ProgressImageView {
    public MaterialCircleImageView(Context context) {
        super(context);
        setImageDrawable(new MaterialLoadingProgressDrawable(this));
    }
}
