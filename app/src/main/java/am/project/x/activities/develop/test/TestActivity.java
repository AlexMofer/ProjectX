package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import am.project.x.R;
import am.project.x.widgets.display.DisplayRecyclerView;
import am.util.mvp.AMAppCompatActivity;
import am.util.viewpager.adapter.ViewsPagerAdapter;

public class TestActivity extends AMAppCompatActivity implements View.OnClickListener {

    private final DisplayAdapter mAdapter = new DisplayAdapter();
    private DisplayRecyclerView mVContent;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_test;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);

        mVContent = findViewById(R.id.display_rv_content);
        mVContent.setAdapter(mAdapter);

        findViewById(R.id.display_btn_temp).setOnClickListener(this);


        ViewPager vpContent = findViewById(R.id.tts_vp_fragments);
        vpContent.setPageMargin(100);
        vpContent.setOffscreenPageLimit(5);
        vpContent.setPageMarginDrawable(R.drawable.bg_test_margin);
        vpContent.setAdapter(new ViewsPagerAdapter(getPagers()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.display_btn_temp:
                mVContent.setPagingEnable(!mVContent.isPagingEnable());
                break;
        }
    }

    private ArrayList<View> getPagers() {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TextView text = new TextView(this);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 180);
            text.setText(String.format(Locale.getDefault(), "%d", i + 1));
            text.setGravity(Gravity.CENTER);
            text.setTextColor(0xff000000);
            views.add(text);
        }
        return views;
    }
}
