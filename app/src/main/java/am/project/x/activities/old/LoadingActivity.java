package am.project.x.activities.old;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.widget.ImageView;

import am.project.x.R;
import am.project.x.widgets.circleimageview.CircleImageView;
import am.project.x.widgets.drawables.DoubleCircleDrawable;


/**
 * 载入动画
 * Created by Alex on 2015/10/21.
 */
public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_activity_loading);
        ImageView iv01 = (ImageView) findViewById(R.id.loading_iv_01);
        iv01.setImageDrawable(new MaterialLoadingProgressDrawable(iv01));
        CircleImageView iv02 = (CircleImageView) findViewById(R.id.loading_iv_02);
        iv02.setImageDrawable(new DoubleCircleDrawable(getResources().getDisplayMetrics().density));
    }
}
