/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.project.x.business.widgets.gradienttabstrip;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import am.appcompat.app.BaseActivity;
import am.project.x.R;
import am.util.viewpager.adapter.ViewsPagerAdapter;
import am.widget.gradienttabstrip.GradientTabStrip;

/**
 * 渐变底部栏
 */
public class GradientTabStripActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, GradientTabStripActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gradienttabstrip);
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
                    return null;
                case 0:
                    return "999";
                case 1:
                    return "1";
                case 2:
                    return "";
            }
        }
    }
}
