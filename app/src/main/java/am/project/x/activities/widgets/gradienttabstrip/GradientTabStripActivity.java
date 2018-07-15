package am.project.x.activities.widgets.gradienttabstrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import am.widget.gradienttabstrip.GradientTabStrip;

public class GradientTabStripActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_gradienttabstrip;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, GradientTabStripActivity.class));
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.gts_toolbar);
        ((ViewPager) findViewById(R.id.gts_vp_pagers))
                .setAdapter(new PagerAdapter(getPagers()));
        ((GradientTabStrip) findViewById(R.id.gts_gts_tabs))
                .setAdapter(new TabAdapter(this));
    }

    private ArrayList<View> getPagers() {
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            TextView text = new TextView(this);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 180);
            text.setText(String.format(Locale.getDefault(), "%d", i + 1));
            text.setGravity(Gravity.CENTER);
            text.setTextColor(0xff000000);
            views.add(text);
        }
        return views;
    }

    private class PagerAdapter extends ViewsPagerAdapter {
        PagerAdapter(List<View> views) {
            super(views);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                default:
                    return "标签" + position;
                case 0:
                    return "微信";
                case 1:
                    return "通讯录";
                case 2:
                    return "发现";
                case 3:
                    return "我";
            }
        }
    }

    private class TabAdapter extends GradientTabStrip.Adapter {

        private final Drawable mNormal0;
        private final Drawable mNormal1;
        private final Drawable mNormal2;
        private final Drawable mNormal3;
        private final Drawable mSelected0;
        private final Drawable mSelected1;
        private final Drawable mSelected2;
        private final Drawable mSelected3;

        TabAdapter(Context context) {
            mNormal0 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_chat_normal);
            mNormal1 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_contacts_normal);
            mNormal2 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_discovery_normal);
            mNormal3 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_account_normal);
            mSelected0 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_chat_selected);
            mSelected1 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_contacts_selected);
            mSelected2 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_discovery_selected);
            mSelected3 = ContextCompat.getDrawable(context,
                    R.drawable.ic_gradienttabstrip_account_selected);
        }

        @Nullable
        @Override
        public Drawable getDrawableNormal(int position, int count) {
            switch (position) {
                default:
                case 0:
                    return mNormal0;
                case 1:
                    return mNormal1;
                case 2:
                    return mNormal2;
                case 3:
                    return mNormal3;
            }
        }

        @Nullable
        @Override
        public Drawable getDrawableSelected(int position, int count) {
            switch (position) {
                default:
                case 0:
                    return mSelected0;
                case 1:
                    return mSelected1;
                case 2:
                    return mSelected2;
                case 3:
                    return mSelected3;
            }
        }

        @Override
        public String getDotText(int position, int count) {
            switch (position) {
                default:
                case 0:
                    return "1";
                case 1:
                    return "";
                case 2:
                    return " 888 ";
                case 3:
                    return " new ";
            }
        }
    }
}
