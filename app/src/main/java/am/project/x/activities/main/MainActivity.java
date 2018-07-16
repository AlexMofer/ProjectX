package am.project.x.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import am.project.x.BuildConfig;
import am.project.x.R;
import am.project.x.activities.main.adapters.MainPagerAdapter;
import am.project.x.activities.main.adapters.TabStripAdapter;
import am.project.x.activities.widgets.indicatortabstrip.IndicatorTabStripActivity;
import am.widget.gradienttabstrip.GradientTabStrip;

/**
 * 主页
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 启动主页
     *
     * @param context Context
     */
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        ((GradientTabStrip) findViewById(R.id.main_gts_tabs)).setAdapter(
                new TabStripAdapter(this));
        ((ViewPager) findViewById(R.id.main_vp_fragments)).setAdapter(
                new MainPagerAdapter(getSupportFragmentManager(), this));
        if (savedInstanceState == null && BuildConfig.DEBUG)
            startDevelop();
    }

    private void startDevelop() {
        IndicatorTabStripActivity.start(this);
    }
}
