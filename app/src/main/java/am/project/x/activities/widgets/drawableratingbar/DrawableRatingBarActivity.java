package am.project.x.activities.widgets.drawableratingbar;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class DrawableRatingBarActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_drawableratingbar;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.drb_toolbar);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DrawableRatingBarActivity.class));
    }
}
