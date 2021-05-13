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
package am.project.x.business.widgets.indicatortabstrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.am.appcompat.app.AppCompatActivity;
import com.am.widget.indicatortabstrip.IndicatorTabStrip;
import com.am.widget.pageradapter.ViewsPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import am.project.x.R;

/**
 * 游标顶部栏
 */
public class IndicatorTabStripActivity extends AppCompatActivity {

    public IndicatorTabStripActivity() {
        super(R.layout.activity_indicatortabstrip);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, IndicatorTabStripActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.its_toolbar);
        ((ViewPager) findViewById(R.id.its_vp_content)).setAdapter(new PagerAdapter(getPagers()));
        ((IndicatorTabStrip) findViewById(R.id.its_its_tabs)).setAdapter(new TabAdapter());
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

    private static class PagerAdapter extends ViewsPagerAdapter {

        PagerAdapter(List<View> views) {
            super(views);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Tab" + (position + 1);
        }
    }

    private static class TabAdapter extends IndicatorTabStrip.Adapter {
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
