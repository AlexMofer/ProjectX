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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import am.appcompat.app.BaseActivity;
import am.drawable.CornerDrawable;
import am.project.x.R;

/**
 * 尖角框
 */
public class CornerDrawableActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private View mVContent;
    private CornerDrawable mDrawable;
    private float mDensity;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CornerDrawableActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cornerdrawable);
        setSupportActionBar(R.id.cnd_toolbar);
        mVContent = findViewById(R.id.cnd_tv_content);
        mDensity = getResources().getDisplayMetrics().density;
        mDrawable = new CornerDrawable((int) (20 * mDensity), (int) (10 * mDensity),
                ContextCompat.getColor(this, R.color.colorRipple));
        setBackground(mDrawable);
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

    private void setBackground(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            mVContent.setBackground(drawable);
        } else {
            mVContent.setBackgroundDrawable(drawable);
        }
    }

    // Listener
    @SuppressLint("RtlHardcoded")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.cnd_sp_direction:
                switch (position) {
                    default:
                    case 0:
                        mDrawable.setDirection(Gravity.TOP);
                        break;
                    case 1:
                        mDrawable.setDirection(Gravity.START);
                        break;
                    case 2:
                        mDrawable.setDirection(Gravity.END);
                        break;
                    case 3:
                        mDrawable.setDirection(Gravity.BOTTOM);
                        break;
                }
                // 因View不自动更新Padding，此处需要重新设置Padding
                setBackground(null);
                setBackground(mDrawable);
                break;
            case R.id.cnd_sp_location:
                switch (position) {
                    default:
                    case 0:
                        mDrawable.setLocation(Gravity.CENTER, mDrawable.getCornerMargin());
                        break;
                    case 1:
                        mDrawable.setLocation(Gravity.START, mDrawable.getCornerMargin());
                        break;
                    case 2:
                        mDrawable.setLocation(Gravity.END, mDrawable.getCornerMargin());
                        break;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.cnd_sb_width:
                final int width = (int) ((20 + progress) * mDensity);
                mDrawable.setCornerWidth(width);
                break;
            case R.id.cnd_sb_height:
                final int height = (int) ((10 + progress) * mDensity);
                mDrawable.setCornerHeight(height);
                // 因View不自动更新Padding，此处需要重新设置Padding
                setBackground(null);
                setBackground(mDrawable);
                break;
            case R.id.cnd_sb_margin:
                final int margin = (int) (progress * mDensity);
                mDrawable.setLocation(mDrawable.getLocation(), margin);
                break;
            case R.id.cnd_sb_bezier:
                final float bezier = progress * 0.01f;
                mDrawable.setCornerBezier(bezier);
                break;
            case R.id.cnd_sb_stoke:
                mDrawable.setStrokeWidth(progress);
                if (progress == 0) {
                    mDrawable.setStrokeColor(null);
                } else {
                    mDrawable.setStrokeColor(
                            ContextCompat.getColor(this, R.color.colorAccent));
                }
                break;
            case R.id.cnd_sb_radius:
                final int radius = (int) (progress * mDensity);
                mDrawable.setContentRadius(radius);
                break;
            case R.id.cnd_sb_padding:
                final int padding = (int) (progress * mDensity);
                mDrawable.setPadding(padding, padding, padding, padding);
                // 因View不自动更新Padding，此处需要重新设置Padding
                setBackground(null);
                setBackground(mDrawable);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
