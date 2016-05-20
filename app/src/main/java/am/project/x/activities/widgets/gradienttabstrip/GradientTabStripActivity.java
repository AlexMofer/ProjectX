package am.project.x.activities.widgets.gradienttabstrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import am.project.x.R;
import am.project.x.activities.widgets.gradienttabstrip.adapters.GradientTabStripAdapter;
import am.widget.gradienttabstrip.GradientTabStrip;

public class GradientTabStripActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener {

    private ViewPager vpFragments;
    private TextView tvTitle;
    private GradientTabStripAdapter adapter;
    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_tab_strip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.gts_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tvTitle = (TextView) toolbar.findViewById(R.id.gts_tv_title);
        vpFragments = (ViewPager) findViewById(R.id.gts_vp_fragments);
        GradientTabStrip tabStrip = (GradientTabStrip) findViewById(R.id.gts_gts_tabs);
        adapter = new GradientTabStripAdapter(getSupportFragmentManager());
        vpFragments.setAdapter(adapter);
        tabStrip.setAdapter(adapter);
        vpFragments.addOnPageChangeListener(this);
        tabStrip.bindViewPager(vpFragments);
        setTitle(adapter.getPageTitle(vpFragments.getCurrentItem()));
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        tvTitle.setText(title);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle(adapter.getPageTitle(vpFragments.getCurrentItem()));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, GradientTabStripActivity.class));
    }
}
