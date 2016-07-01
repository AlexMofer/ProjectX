package am.project.x.activities.drawable.loading;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.widgets.drawables.DoubleCircleDrawable;

public class LoadingActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_loading;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.loading_toolbar);

        ImageView iv01 = (ImageView) findViewById(R.id.loading_iv_01);
        DoubleCircleDrawable drawable = new DoubleCircleDrawable(getResources().getDisplayMetrics().density);
        iv01.setImageDrawable(drawable);
        drawable.start();
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoadingActivity.class));
    }
}
