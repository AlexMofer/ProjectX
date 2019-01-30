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
package am.project.x.business.drawables.cornerdrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 尖角框
 */
public class CornerDrawableActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private int stokeColor;
    private float density;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CornerDrawableActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_cornerdrawable;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.cnd_toolbar);
        density = getResources().getDisplayMetrics().density;
        stokeColor = ContextCompat.getColor(this, R.color.colorAccent);
        // TODO
//        final int color = ContextCompat.getColor(this, R.color.colorRipple);
//        final int width = (int) (20 * density);
//        final int height = (int) (10 * density);
//        drawable = new CornerDrawable(color, width, height);
//        if (Build.VERSION.SDK_INT >= 16) {
//            findViewById(R.id.cnd_tv_content).setBackground(drawable);
//        } else {
//            findViewById(R.id.cnd_tv_content).setBackgroundDrawable(drawable);
//        }
        this.<Spinner>findViewById(R.id.cnd_sp_direction).setOnItemSelectedListener(this);
        this.<Spinner>findViewById(R.id.cnd_sp_location).setOnItemSelectedListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_width).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_height).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_margin).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_bezier).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_stoke).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_radius).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cnd_sb_padding).setOnSeekBarChangeListener(this);
    }

    // Listener
    @SuppressLint("RtlHardcoded")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()) {
//            case R.id.cnd_sp_direction:
//                switch (position) {
//                    default:
//                    case 0:
//                        drawable.setDirection(Gravity.TOP);
//                        break;
//                    case 1:
//                        drawable.setDirection(Gravity.LEFT);
//                        break;
//                    case 2:
//                        drawable.setDirection(Gravity.RIGHT);
//                        break;
//                    case 3:
//                        drawable.setDirection(Gravity.BOTTOM);
//                        break;
//                }
//                break;
//            case R.id.cnd_sp_location:
//                switch (position) {
//                    default:
//                    case 0:
//                        drawable.setLocation(Gravity.CENTER, drawable.getCornerMargin());
//                        break;
//                    case 1:
//                        drawable.setLocation(Gravity.LEFT, drawable.getCornerMargin());
//                        break;
//                    case 2:
//                        drawable.setLocation(Gravity.RIGHT, drawable.getCornerMargin());
//                        break;
//                }
//                break;
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.cnd_sb_width:
//                final int width = (int) ((20 + progress) * density);
//                drawable.setCornerWidth(width);
//                break;
//            case R.id.cnd_sb_height:
//                final int height = (int) ((10 + progress) * density);
//                drawable.setCornerHeight(height);
//                break;
//            case R.id.cnd_sb_margin:
//                final int margin = (int) (progress * density);
//                drawable.setCornerMargin(margin);
//                break;
//            case R.id.cnd_sb_bezier:
//                final float bezier = progress * 0.01f;
//                drawable.setCornerBezier(bezier);
//                break;
//            case R.id.cnd_sb_stoke:
//                drawable.setStroke(progress, stokeColor, 0, 0);
//                break;
//            case R.id.cnd_sb_radius:
//                final int radius = (int) (progress * density);
//                drawable.setContentRadius(radius);
//                break;
//            case R.id.cnd_sb_padding:
//                final int padding = (int) (progress * density);
//                drawable.setPadding(padding, padding, padding, padding);
//                break;
//        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
