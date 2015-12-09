package com.am.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.widget.ImageView;

import com.am.widget.R;
import com.am.widget.circleimageview.CircleImageView;
import com.am.widget.drawables.DoubleCircleDrawable;

/**
 * 载入动画
 * Created by Alex on 2015/10/21.
 */
public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ImageView iv01 = (ImageView) findViewById(R.id.loading_iv_01);
        iv01.setImageDrawable(new MaterialLoadingProgressDrawable(iv01));
        CircleImageView iv02 = (CircleImageView) findViewById(R.id.loading_iv_02);
        iv02.setImageDrawable(new DoubleCircleDrawable(getResources().getDisplayMetrics().density));
    }
}
