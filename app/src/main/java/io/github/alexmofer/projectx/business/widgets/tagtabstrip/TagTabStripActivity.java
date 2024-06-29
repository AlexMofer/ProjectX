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
package io.github.alexmofer.projectx.business.widgets.tagtabstrip;

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
import com.am.widget.pageradapter.ViewsPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

import io.github.alexmofer.projectx.R;

/**
 * 图片标记栏
 */
public class TagTabStripActivity extends AppCompatActivity {

    public TagTabStripActivity() {
        super(R.layout.activity_tagtabstrip);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, TagTabStripActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.tts_toolbar);
        ((ViewPager) findViewById(R.id.tts_vp_pagers))
                .setAdapter(new ViewsPagerAdapter(getPagers()));
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
