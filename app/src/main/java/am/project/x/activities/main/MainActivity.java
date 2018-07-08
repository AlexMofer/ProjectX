package am.project.x.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import am.project.x.R;
import am.project.x.activities.main.adapters.MainPagerAdapter;
import am.project.x.activities.widgets.gradienttabstrip.GradientTabStripActivity;
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
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ViewPager vpFragments = (ViewPager) findViewById(R.id.main_vp_fragments);
        GradientTabStrip gtsTabs = (GradientTabStrip) findViewById(R.id.main_gts_tabs);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        vpFragments.setAdapter(adapter);
        gtsTabs.setAdapter(adapter);
        gtsTabs.bindViewPager(vpFragments);
        if (savedInstanceState == null)
            GradientTabStripActivity.start(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ViewPager vpFragments = findViewById(R.id.main_vp_fragments);
        GradientTabStrip gtsTabs = findViewById(R.id.main_gts_tabs);
        gtsTabs.bindViewPager(vpFragments);
    }
}
