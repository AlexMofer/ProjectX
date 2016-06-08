package am.project.x.activities.widgets.wraplayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.wraplayout.WrapLayout;

public class WrapLayoutActivity extends BaseActivity {

    private WrapLayout lytWrap;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_wraplayout;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.wly_toolbar);
        lytWrap = (WrapLayout) findViewById(R.id.wly_lyt_warp);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, WrapLayoutActivity.class));
    }
}
