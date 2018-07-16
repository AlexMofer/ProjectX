package am.project.x.activities.widgets.indicatortabstrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.util.viewpager.adapter.ViewsPagerAdapter;
import am.widget.basetabstrip.BaseTabStrip;
import am.widget.indicatortabstrip.IndicatorTabStrip;
import am.widget.indicatortabstrip.IndicatorTabStripNew;

public class IndicatorTabStripActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_indicatortabstrip;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, IndicatorTabStripActivity.class));
    }

    private ArrayList<View> getPagers() {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TextView text = new TextView(this);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 180);
            text.setText(String.format(Locale.getDefault(), "%d", i + 1));
            text.setGravity(Gravity.CENTER);
            text.setTextColor(0xff000000);
            views.add(text);
        }
        return views;
    }

    private class IndicatorTabStripViewsPagerAdapter extends ViewsPagerAdapter {
        public IndicatorTabStripViewsPagerAdapter(List<View> views) {
            super(views);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Tab" + (position + 1);
        }
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.its_toolbar);
        ViewPager vpContent = (ViewPager) findViewById(R.id.its_vp_content);
        IndicatorTabStrip tabs = (IndicatorTabStrip) findViewById(R.id.its_its_tabs);
        vpContent.setAdapter(new IndicatorTabStripViewsPagerAdapter(getPagers()));
        BaseTabStrip.ItemTabAdapter adapter = new BaseTabStrip.ItemTabAdapter() {

            @Override
            public boolean isTagEnable(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public String getTag(int position) {
                switch (position) {
                    default:
                    case 0:
                        return null;
                    case 1:
                        return "";
                    case 2:
                        return "3";
                }
            }
        };
        tabs.setAdapter(adapter);
        ((IndicatorTabStripNew) findViewById(R.id.its_its_tabsn)).setAdapter(new TabAdapter());
    }

    private class TabAdapter extends IndicatorTabStripNew.Adapter {
        @Override
        public String getDotText(int position, int count) {
            switch (position) {
                default:
                case 0:
                    return "1";
                case 1:
                    return "";
                case 2:
                    return "888";
                case 3:
                    return "new";
            }
        }
    }
}
