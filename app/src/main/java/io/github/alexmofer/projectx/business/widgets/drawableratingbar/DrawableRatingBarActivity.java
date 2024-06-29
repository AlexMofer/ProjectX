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
package io.github.alexmofer.projectx.business.widgets.drawableratingbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.am.appcompat.app.AppCompatActivity;
import com.am.widget.drawableratingbar.DrawableRatingBar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import io.github.alexmofer.projectx.R;

/**
 * 图片评级
 */
public class DrawableRatingBarActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private DrawableRatingBar mVRating;

    public DrawableRatingBarActivity() {
        super(R.layout.activity_drawableratingbar);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, DrawableRatingBarActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.drb_toolbar);
        mVRating = findViewById(R.id.drb_rb_stars);
        final SeekBar max = findViewById(R.id.drb_sb_max);
        final SeekBar min = findViewById(R.id.drb_sb_min);

        this.<Spinner>findViewById(R.id.drb_sp_gravity).setOnItemSelectedListener(this);
        this.<SwitchMaterial>findViewById(R.id.drb_sh_manually).setOnCheckedChangeListener(this);
        this.<SwitchMaterial>findViewById(R.id.drb_sh_touchable).setOnCheckedChangeListener(this);
        max.setOnSeekBarChangeListener(this);
        max.setProgress(0);
        min.setOnSeekBarChangeListener(this);
        min.setProgress(1);
    }

    // Listener
    @SuppressLint("RtlHardcoded")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            default:
            case 0:
                mVRating.setGravity(Gravity.CENTER);
                break;
            case 1:
                mVRating.setGravity(Gravity.LEFT);
                break;
            case 2:
                mVRating.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case 3:
                mVRating.setGravity(Gravity.RIGHT);
                break;
            case 4:
                mVRating.setGravity(Gravity.CENTER_VERTICAL);
                break;
            case 5:
                mVRating.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                break;
            case 6:
                mVRating.setGravity(Gravity.BOTTOM);
                break;
            case 7:
                mVRating.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                break;
            case 8:
                mVRating.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.drb_sh_manually) {
            mVRating.setManually(isChecked);
        } else if (id == R.id.drb_sh_touchable) {
            mVRating.setOnlyItemTouchable(isChecked);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int id = seekBar.getId();
        if (id == R.id.drb_sb_max) {
            mVRating.setMax(6 + progress);
        } else if (id == R.id.drb_sb_min) {
            mVRating.setMin(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
