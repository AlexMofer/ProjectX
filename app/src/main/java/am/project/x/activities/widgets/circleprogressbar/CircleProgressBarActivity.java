package am.project.x.activities.widgets.circleprogressbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CircleProgressBarActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_circleprogressbar;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.circleprogressbar_toolbar);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }
}
