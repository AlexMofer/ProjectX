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
package io.github.alexmofer.projectx.business.widgets.circleprogressbar;

import android.animation.ValueAnimator;
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
import android.widget.Switch;

import androidx.annotation.Nullable;

import com.am.appcompat.app.AppCompatActivity;
import com.am.widget.circleprogressbar.CircleProgressBar;

import io.github.alexmofer.projectx.R;

/**
 * 环形进度条
 */
public class CircleProgressBarActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private CircleProgressBar mProgress;
    private float density;

    public CircleProgressBarActivity() {
        super(R.layout.activity_circleprogressbar);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.cpb_toolbar);
        mProgress = findViewById(R.id.cpb_cpb_bar);
        density = getResources().getDisplayMetrics().density;

        this.<Spinner>findViewById(R.id.cpb_sp_gravity).setOnItemSelectedListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_radius).setOnSeekBarChangeListener(this);
        this.<Spinner>findViewById(R.id.cpb_sp_scale_type).setOnItemSelectedListener(this);

        this.<Switch>findViewById(R.id.cpb_sh_progress_mode).setOnCheckedChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_loading_start_angle)
                .setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_loading_sweep_angle)
                .setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_loading_duration)
                .setOnSeekBarChangeListener(this);
        this.<Switch>findViewById(R.id.cpb_sh_loading_repeat_mode).setOnCheckedChangeListener(this);
        this.<Switch>findViewById(R.id.cpb_sh_loading_draw_other).setOnCheckedChangeListener(this);

        this.<SeekBar>findViewById(R.id.cpb_sb_start_angle).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_sweep_angle).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_background_size).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_progress_size).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_progress).setOnSeekBarChangeListener(this);
        this.<Switch>findViewById(R.id.cpb_sh_gradient).setOnCheckedChangeListener(this);
        this.<Switch>findViewById(R.id.cpb_sh_progress_value).setOnCheckedChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_progress_value_text_size)
                .setOnSeekBarChangeListener(this);

        this.<SeekBar>findViewById(R.id.cpb_sb_top_text_gap).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_top_text_size).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_bottom_text_gap).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_bottom_text_size).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_progress_duration).setOnSeekBarChangeListener(this);

        this.<Spinner>findViewById(R.id.cpb_sp_dial_visibility).setOnItemSelectedListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_gap).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_angle).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_height).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_width).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_special_unit).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_special_height).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_dial_special_width).setOnSeekBarChangeListener(this);
        this.<Spinner>findViewById(R.id.cpb_sp_dial_gravity).setOnItemSelectedListener(this);
        this.<Switch>findViewById(R.id.cpb_sh_special_dial_value).setOnCheckedChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_special_dial_value_gap).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.cpb_sb_special_dial_value_text_size)
                .setOnSeekBarChangeListener(this);
        findViewById(R.id.cpb_btn_progress_animator).setOnClickListener(this);
    }

    // Listener
    @SuppressLint("RtlHardcoded")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final int pid = parent.getId();
        if (pid == R.id.cpb_sp_gravity) {
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
        } else if (pid == R.id.cpb_sp_scale_type) {
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
        } else if (pid == R.id.cpb_sp_dial_visibility) {
            switch (position) {
                default:
                case 0:
                    mProgress.setDialVisibility(View.VISIBLE);
                    break;
                case 1:
                    mProgress.setDialVisibility(View.INVISIBLE);
                    break;
                case 2:
                    mProgress.setDialVisibility(View.GONE);
                    break;
            }
        } else if (pid == R.id.cpb_sp_dial_gravity) {
            switch (position) {
                default:
                case 0:
                    mProgress.setDialGravity(Gravity.CENTER);
                    break;
                case 1:
                    mProgress.setDialGravity(Gravity.TOP);
                    break;
                case 2:
                    mProgress.setDialGravity(Gravity.BOTTOM);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        final int id = seekBar.getId();
        if (id == R.id.cpb_sb_radius) {
            mProgress.setRadius(density * (progress + 100));
        } else if (id == R.id.cpb_sb_loading_start_angle) {
            mProgress.setLoadingStartAngle(progress);
        } else if (id == R.id.cpb_sb_loading_sweep_angle) {
            mProgress.setLoadingSweepAngle(progress);
        } else if (id == R.id.cpb_sb_loading_duration) {
            mProgress.setLoadingDuration(100L * (progress + 1));
        } else if (id == R.id.cpb_sb_start_angle) {
            mProgress.setStartAngle(progress);
        } else if (id == R.id.cpb_sb_sweep_angle) {
            mProgress.setSweepAngle(progress);
        } else if (id == R.id.cpb_sb_background_size) {
            mProgress.setBackgroundSize(density * progress);
        } else if (id == R.id.cpb_sb_progress_size) {
            mProgress.setProgressSize(density * progress);
        } else if (id == R.id.cpb_sb_progress) {
            mProgress.animationToProgress(progress);
        } else if (id == R.id.cpb_sb_progress_value_text_size) {
            mProgress.setProgressValueTextSize(density * (progress + 50));
        } else if (id == R.id.cpb_sb_top_text_gap) {
            mProgress.setTopTextGap(density * progress);
        } else if (id == R.id.cpb_sb_top_text_size) {
            mProgress.setTopTextSize(density * (progress + 10));
        } else if (id == R.id.cpb_sb_bottom_text_gap) {
            mProgress.setBottomTextGap(density * progress);
        } else if (id == R.id.cpb_sb_bottom_text_size) {
            mProgress.setBottomTextSize(density * (progress + 10));
        } else if (id == R.id.cpb_sb_progress_duration) {
            mProgress.setProgressDuration(100L * (progress + 1));
        } else if (id == R.id.cpb_sb_dial_gap) {
            mProgress.setDialGap(density * progress);
        } else if (id == R.id.cpb_sb_dial_angle) {
            mProgress.setDialAngle(progress);
        } else if (id == R.id.cpb_sb_dial_height) {
            mProgress.setDialHeight(density * progress);
        } else if (id == R.id.cpb_sb_dial_width) {
            mProgress.setDialWidth(density * progress);
        } else if (id == R.id.cpb_sb_dial_special_unit) {
            mProgress.setDialSpecialUnit(progress);
        } else if (id == R.id.cpb_sb_dial_special_height) {
            mProgress.setDialSpecialHeight(density * progress);
        } else if (id == R.id.cpb_sb_dial_special_width) {
            mProgress.setDialSpecialWidth(density * progress);
        } else if (id == R.id.cpb_sb_special_dial_value_gap) {
            mProgress.setSpecialDialValueGap(density * progress);
        } else if (id == R.id.cpb_sb_special_dial_value_text_size) {
            mProgress.setSpecialDialValueTextSize(density * (progress + 5));
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
        final int id = buttonView.getId();
        if (id == R.id.cpb_sh_progress_mode) {
            mProgress.setProgressMode(isChecked ?
                    CircleProgressBar.ProgressMode.LOADING :
                    CircleProgressBar.ProgressMode.PROGRESS);
        } else if (id == R.id.cpb_sh_loading_repeat_mode) {
            mProgress.setLoadingRepeatMode(isChecked ? ValueAnimator.REVERSE :
                    ValueAnimator.RESTART);
        } else if (id == R.id.cpb_sh_loading_draw_other) {
            mProgress.setLoadingDrawOther(isChecked);
        } else if (id == R.id.cpb_sh_gradient) {
            if (isChecked)
                mProgress.setGradientColors(0xff33b5e5, 0xff99cc00, 0xffffbb33,
                        0xffff4444, 0xff33b5e5);
            else
                mProgress.setGradientColors(0xffff4444);
        } else if (id == R.id.cpb_sh_progress_value) {
            mProgress.setShowSpecialDialValue(isChecked);
        } else if (id == R.id.cpb_sh_special_dial_value) {
            mProgress.setShowSpecialDialValue(isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cpb_btn_progress_animator) {
            mProgress.animationToProgress(0, mProgress.getProgress());
        }
    }
}
