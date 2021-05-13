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
package am.project.x.business.widgets.wraplayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.am.appcompat.app.AppCompatActivity;
import com.am.widget.wraplayout.WrapLayout;

import am.project.x.R;

/**
 * 自动换行布局
 */
public class WrapLayoutActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private WrapLayout mVContent;

    public WrapLayoutActivity() {
        super(R.layout.activity_wraplayout);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, WrapLayoutActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.wl_toolbar);
        mVContent = findViewById(R.id.wl_wl_content);
        final RadioGroup gravity = findViewById(R.id.wl_rg_gravity);
        final SeekBar horizontal = findViewById(R.id.wl_sb_horizontal);
        final SeekBar vertical = findViewById(R.id.wl_sb_vertical);

        gravity.setOnCheckedChangeListener(this);
        gravity.check(R.id.wl_rb_top);
        horizontal.setOnSeekBarChangeListener(this);
        horizontal.setProgress(15);
        vertical.setOnSeekBarChangeListener(this);
        vertical.setProgress(15);
    }

    // Listener
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.wl_rb_top) {
            mVContent.setGravity(Gravity.TOP);
        } else if (checkedId == R.id.wl_rb_center) {
            mVContent.setGravity(Gravity.CENTER);
        } else if (checkedId == R.id.wl_rb_bottom) {
            mVContent.setGravity(Gravity.BOTTOM);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.wl_sb_horizontal) {
            mVContent.setHorizontalSpacing(
                    (int) (progress * getResources().getDisplayMetrics().density));
        } else if (id == R.id.wl_sb_vertical) {
            mVContent.setVerticalSpacing(
                    (int) (progress * getResources().getDisplayMetrics().density));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
