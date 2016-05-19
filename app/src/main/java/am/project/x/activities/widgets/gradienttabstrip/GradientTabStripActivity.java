package am.project.x.activities.widgets.gradienttabstrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import am.project.x.R;
import am.project.x.activities.widgets.gradienttabstrip.adapters.GradientTabStripAdapter;
import am.widget.gradienttabstrip.GradientTabStrip;

public class GradientTabStripActivity extends AppCompatActivity {

    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradient_tab_strip);
        Toolbar toolbar = (Toolbar) findViewById(R.id.gts_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        ViewPager vpFragments = (ViewPager) findViewById(R.id.gts_vp_fragments);
        GradientTabStrip tabStrip = (GradientTabStrip) findViewById(R.id.gts_gts_tabs);
        GradientTabStripAdapter adapter = new GradientTabStripAdapter(getSupportFragmentManager());
        vpFragments.setAdapter(adapter);
        tabStrip.setAdapter(adapter);
        tabStrip.bindViewPager(vpFragments);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, GradientTabStripActivity.class));
    }
}
