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
package am.project.x.business.widgets.circleprogressbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.widget.circleprogressbar.CircleProgressBar;

/**
 * 环形进度条
 */
public class CircleProgressBarActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {

    private CircleProgressBar mProgress;
    private float density;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_circleprogressbar;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.cpb_toolbar);
        mProgress = findViewById(R.id.cpb_cpb_bar);
        density = getResources().getDisplayMetrics().density;

        this.<Spinner>findViewById(R.id.cpb_sp_gravity).setOnItemSelectedListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_radius).setOnSeekBarChangeListener(this);
        this.<Spinner>findViewById(R.id.cpb_sp_scale_type).setOnItemSelectedListener(this);

        this.<Switch>findViewById(R.id.cpb_sh_progress_mode).setOnCheckedChangeListener(this);

    }

    // Listener
    @SuppressWarnings("all")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.cpb_sp_gravity:
                switch (position) {
                    default:
                    case 0:
                        mProgress.setGravity(Gravity.CENTER);
                        break;
                    case 1:
                        mProgress.setGravity(Gravity.LEFT);
                        break;
                    case 2:
                        mProgress.setGravity(Gravity.CENTER_HORIZONTAL);
                        break;
                    case 3:
                        mProgress.setGravity(Gravity.RIGHT);
                        break;
                    case 4:
                        mProgress.setGravity(Gravity.CENTER_VERTICAL);
                        break;
                    case 5:
                        mProgress.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                        break;
                    case 6:
                        mProgress.setGravity(Gravity.BOTTOM);
                        break;
                    case 7:
                        mProgress.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        break;
                    case 8:
                        mProgress.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                        break;
                }
                break;
            case R.id.cpb_sp_scale_type:
                switch (position) {
                    default:
                    case 0:
                        mProgress.setScaleType(CircleProgressBar.ST_INSIDE);
                        break;
                    case 1:
                        mProgress.setScaleType(CircleProgressBar.ST_CROP);
                        break;
                    case 2:
                        mProgress.setScaleType(
                                CircleProgressBar.ST_INSIDE | CircleProgressBar.ST_CROP);
                        break;
                    case 3:
                        mProgress.setScaleType(CircleProgressBar.ST_NONE);
                        break;
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.cpb_sb_radius:
                mProgress.setRadius(density * (progress + 100));
                break;

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // do nothing
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cpb_sh_progress_mode:
                mProgress.setProgressMode(isChecked ?
                        CircleProgressBar.ProgressMode.LOADING :
                        CircleProgressBar.ProgressMode.PROGRESS);
                break;
        }
    }
}
