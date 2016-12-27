package am.project.x.activities.widgets.replacelayout;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.util.viewpager.adapter.ViewsPagerAdapter;
import am.widget.replacelayout.ReplaceLayout;

public class ReplaceLayoutActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener, ReplaceLayout.ReplaceAdapter {

    private ReplaceLayout replaceLayout;
    private int mPageState;
    private TextView tvTitle1, tvTitle2, tvTitle3, tvTitle4, tvTitle5;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_replacelayout;
    }


    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.rlt_toolbar);
        ViewPager vpContent = (ViewPager) findViewById(R.id.rlt_vp_fragments);
        replaceLayout = (ReplaceLayout) findViewById(R.id.rlt_lyt_replace);
        vpContent.setAdapter(new ViewsPagerAdapter(getPagers()));
        getTitles();
        replaceLayout.setAdapter(this);
        vpContent.addOnPageChangeListener(this);
    }

    private ArrayList<View> getPagers() {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TextView text = new TextView(this);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            text.setText(String.format(Locale.getDefault(), "%d", i + 1));
            text.setGravity(Gravity.CENTER);
            text.setTextColor(0xff000000);
            views.add(text);
        }
        return views;
    }

    private void getTitles() {
        tvTitle1 = new TextView(this);
        tvTitle1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvTitle1.setText(String.format(Locale.getDefault(), getString(R.string.replacelayout_titles), 1));
        tvTitle1.setGravity(Gravity.CENTER);
        tvTitle1.setTextColor(0xffff4444);

        tvTitle2 = new TextView(this);
        tvTitle2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvTitle2.setText(String.format(Locale.getDefault(), getString(R.string.replacelayout_titles), 2));
        tvTitle2.setGravity(Gravity.CENTER);
        tvTitle2.setTextColor(0xff99cc00);

        tvTitle3 = new TextView(this);
        tvTitle3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvTitle3.setText(String.format(Locale.getDefault(), getString(R.string.replacelayout_titles), 3));
        tvTitle3.setGravity(Gravity.CENTER);
        tvTitle3.setTextColor(0xffffbb33);

        tvTitle4 = new TextView(this);
        tvTitle4.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvTitle4.setText(String.format(Locale.getDefault(), getString(R.string.replacelayout_titles), 4));
        tvTitle4.setGravity(Gravity.CENTER);
        tvTitle4.setTextColor(0xffaa66cc);

        tvTitle5 = new TextView(this);
        tvTitle5.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        tvTitle5.setText(String.format(Locale.getDefault(), getString(R.string.replacelayout_titles), 5));
        tvTitle5.setGravity(Gravity.CENTER);
        tvTitle5.setTextColor(0xffffffff);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        replaceLayout.move(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        if (mPageState == ViewPager.SCROLL_STATE_IDLE) {
            replaceLayout.moveTo(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mPageState = state;
    }

    @Override
    public View getReplaceView(ReplaceLayout replaceLayout, int position) {
        switch (position) {
            case 0:
                return tvTitle1;
            case 1:
                return tvTitle2;
            case 2:
                return tvTitle3;
            case 3:
                return tvTitle4;
            case 4:
                return tvTitle5;
        }
        return null;
    }

    @Override
    public void onAnimation(ViewGroup replace, int correct, int next, float offset) {
        View correctV = getReplaceView(replaceLayout, correct);
        View nextV = getReplaceView(replaceLayout, next);
        if (correctV != null) {
            correctV.setAlpha(offset);
        }
        if (nextV != null) {
            nextV.setAlpha(1F - offset);
        }
    }

    @Override
    public void onSelected(ViewGroup replace, int position) {
        View child = getReplaceView(replaceLayout, position);
        if (child != null) {
            child.setAlpha(1);
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ReplaceLayoutActivity.class));
    }
}
