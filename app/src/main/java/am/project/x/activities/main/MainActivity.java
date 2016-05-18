package am.project.x.activities.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import am.project.x.R;
import am.project.x.activities.main.adapters.MainPagerAdapter;
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
        ViewPager vpFragments = (ViewPager) findViewById(R.id.main_vp_fragments);
        GradientTabStrip gtsTabs = (GradientTabStrip) findViewById(R.id.main_gts_tabs);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        if (vpFragments != null && gtsTabs != null) {
            vpFragments.setAdapter(adapter);
            gtsTabs.setAdapter(adapter);
            gtsTabs.bindViewPager(vpFragments);
        }
    }

}
