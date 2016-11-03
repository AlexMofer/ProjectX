package am.project.x.activities.widgets.circleprogressbar;

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
//        cpbDemo.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                cpbDemo.setProgress(0);
//                cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.PROGRESS);
//                cpbDemo.animationToProgress(800);
//            }
//        }, 12000);
        cpbDemo.setStartAngle(-90);
        cpbDemo.setSweepAngle(360);
        cpbDemo.setGradientColors(0xffff4444);
        cpbDemo.setBackgroundSize(0);
        cpbDemo.setProgress(520);
        cpbDemo.setProgressSize(64);
        cpbDemo.setDialVisibility(View.GONE);
        cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.PROGRESS);
        cpbDemo.setShowProgressValue(true);
        cpbDemo.setTopText("步数");
        cpbDemo.setBottomText(null);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }
}
