package am.project.x.activities.widgets.circleprogressbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.circleprogressbar.CircleProgressBar;

public class CircleProgressBarActivity extends BaseActivity {

    private CircleProgressBar cpbDemo;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_circleprogressbar;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.circleprogressbar_toolbar);
        cpbDemo = (CircleProgressBar) findViewById(R.id.circleprogressbar_cpb_demo);
        cpbDemo.postDelayed(new Runnable() {
            @Override
            public void run() {
                cpbDemo.setProgress(0);
                cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.PROGRESS);
                cpbDemo.animationToProgress(600);
            }
        }, 12000);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }
}
