package com.am.widget.circleimageview;

import android.content.Context;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.util.AttributeSet;

/**
 * MaterialCircleImageView
 * Created by Alex on 2015/10/27.
 */
public class MaterialCircleImageView extends CircleImageView {
    public MaterialCircleImageView(Context context) {
        super(context);
        setImageDrawable(new MaterialLoadingProgressDrawable(this));
    }

    public MaterialCircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageDrawable(new MaterialLoadingProgressDrawable(this));
    }

    public MaterialCircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(new MaterialLoadingProgressDrawable(this));
    }

    public MaterialCircleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setImageDrawable(new MaterialLoadingProgressDrawable(this));
    }
}
