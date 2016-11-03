package am.project.x.activities.widgets.circleprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

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

        cpbDemo.setStartAngle(-90);
        cpbDemo.setSweepAngle(360);
        cpbDemo.setGradientColors(0xffff4444);
        cpbDemo.setBackgroundSize(0);
        cpbDemo.setProgress(0);
        cpbDemo.setProgressSize(64);
        cpbDemo.setDialVisibility(View.GONE);
        cpbDemo.setLoadingStartAngle(-90);
        cpbDemo.setLoadingSweepAngle(360);
        cpbDemo.setLoadingRepeatMode(ValueAnimator.RESTART);
        cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.LOADING);
        cpbDemo.setShowProgressValue(true);
        cpbDemo.setTopText("步数");
        cpbDemo.setBottomText(null);

        cpbDemo.postDelayed(new Runnable() {
            @Override
            public void run() {
                cpbDemo.setProgress(0);
                cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.PROGRESS);
                cpbDemo.animationToProgress(678);
            }
        }, 12000);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }
}
