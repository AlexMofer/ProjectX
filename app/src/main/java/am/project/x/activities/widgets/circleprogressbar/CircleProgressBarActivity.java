package am.project.x.activities.widgets.circleprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.circleprogressbar.CircleProgressBar;

public class CircleProgressBarActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    private CircleProgressBar cpbDemo;
    private ViewGroup vgControl;
    private float density;
    private View vBase;
    private RadioGroup rgGravity1, rgGravity2, rgGravity3;
    private View vProgress;
    private View vDial;
    private View vLoading;


    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_circleprogressbar;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.circleprogressbar_toolbar);
        density = getResources().getDisplayMetrics().density;
        cpbDemo = (CircleProgressBar) findViewById(R.id.circleprogressbar_cpb_demo);
        vgControl = (ViewGroup) findViewById(R.id.circleprogressbar_lyt_control);
        initBase();
        initProgress();
        initDial();
        initLoading();
    }

    private void initBase() {
        vBase = LayoutInflater.from(this).inflate(R.layout.include_circleprogressbar_base, vgControl,
                false);
        rgGravity1 = (RadioGroup) vBase.findViewById(R.id.cpb_rg_gravity_1);
        rgGravity1.setOnCheckedChangeListener(this);
        rgGravity2 = (RadioGroup) vBase.findViewById(R.id.cpb_rg_gravity_2);
        rgGravity2.setOnCheckedChangeListener(this);
        rgGravity3 = (RadioGroup) vBase.findViewById(R.id.cpb_rg_gravity_3);
        rgGravity3.setOnCheckedChangeListener(this);
        SeekBar sbRadius = (SeekBar) vBase.findViewById(R.id.cpb_sb_radius);
        sbRadius.setOnSeekBarChangeListener(this);
        RadioGroup rgScaleType = (RadioGroup) vBase.findViewById(R.id.cpb_rg_scale_type);
        rgScaleType.setOnCheckedChangeListener(this);
    }

    private void initProgress() {
        vProgress = LayoutInflater.from(this).inflate(R.layout.include_circleprogressbar_progress,
                vgControl, false);
        SeekBar sbStartAngle = (SeekBar) vProgress.findViewById(R.id.cpb_sb_start_angle);
        sbStartAngle.setOnSeekBarChangeListener(this);
        SeekBar sbSweepAngle = (SeekBar) vProgress.findViewById(R.id.cpb_sb_sweep_angle);
        sbSweepAngle.setOnSeekBarChangeListener(this);
        SeekBar sbBackgroundSize = (SeekBar) vProgress.findViewById(R.id.cpb_sb_background_size);
        sbBackgroundSize.setOnSeekBarChangeListener(this);
        SeekBar sbProgressSize = (SeekBar) vProgress.findViewById(R.id.cpb_sb_progress_size);
        sbProgressSize.setOnSeekBarChangeListener(this);
        SeekBar sbProgress = (SeekBar) vProgress.findViewById(R.id.cpb_sb_progress);
        sbProgress.setOnSeekBarChangeListener(this);
        Switch stGradient = (Switch) vProgress.findViewById(R.id.cpb_st_gradient);
        stGradient.setOnCheckedChangeListener(this);
        Switch stProgressValue = (Switch) vProgress.findViewById(R.id.cpb_st_progress_value);
        stProgressValue.setOnCheckedChangeListener(this);
        SeekBar sbProgressValueTextSize = (SeekBar)
                vProgress.findViewById(R.id.cpb_sb_progress_value_text_size);
        sbProgressValueTextSize.setOnSeekBarChangeListener(this);
        SeekBar sbTopTextGap = (SeekBar) vProgress.findViewById(R.id.cpb_sb_top_text_gap);
        sbTopTextGap.setOnSeekBarChangeListener(this);
        SeekBar sbTopTextSize = (SeekBar) vProgress.findViewById(R.id.cpb_sb_top_text_size);
        sbTopTextSize.setOnSeekBarChangeListener(this);
        SeekBar sbBottomTextGap = (SeekBar) vProgress.findViewById(R.id.cpb_sb_bottom_text_gap);
        sbBottomTextGap.setOnSeekBarChangeListener(this);
        SeekBar sbBottomTextSize = (SeekBar) vProgress.findViewById(R.id.cpb_sb_bottom_text_size);
        sbBottomTextSize.setOnSeekBarChangeListener(this);
        SeekBar sbProgressDuration = (SeekBar) vProgress.findViewById(R.id.cpb_sb_progress_duration);
        sbProgressDuration.setOnSeekBarChangeListener(this);
        vProgress.findViewById(R.id.cpb_btn_progress_animator).setOnClickListener(this);
    }

    private void initDial() {
        vDial = LayoutInflater.from(this).inflate(R.layout.include_circleprogressbar_dial,
                vgControl, false);
        RadioGroup rgVisibility = (RadioGroup) vDial.findViewById(R.id.cpb_rg_dial_visibility);
        rgVisibility.setOnCheckedChangeListener(this);
        SeekBar sbDialGap = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_gap);
        sbDialGap.setOnSeekBarChangeListener(this);
        SeekBar sbDialAngle = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_angle);
        sbDialAngle.setOnSeekBarChangeListener(this);
        SeekBar sbDialHeight = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_height);
        sbDialHeight.setOnSeekBarChangeListener(this);
        SeekBar sbDialWidth = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_width);
        sbDialWidth.setOnSeekBarChangeListener(this);
        SeekBar sbDialSpecialUnit = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_special_unit);
        sbDialSpecialUnit.setOnSeekBarChangeListener(this);
        SeekBar sbDialSpecialHeight = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_special_height);
        sbDialSpecialHeight.setOnSeekBarChangeListener(this);
        SeekBar sbDialSpecialWidth = (SeekBar) vDial.findViewById(R.id.cpb_sb_dial_special_width);
        sbDialSpecialWidth.setOnSeekBarChangeListener(this);
        RadioGroup rgDialGravity = (RadioGroup) vDial.findViewById(R.id.cpb_rg_dial_gravity);
        rgDialGravity.setOnCheckedChangeListener(this);
        Switch stSpecialDialValue = (Switch) vDial.findViewById(R.id.cpb_st_special_dial_value);
        stSpecialDialValue.setOnCheckedChangeListener(this);
        SeekBar sbSpecialDialValueGap = (SeekBar) vDial.findViewById(R.id.cpb_sb_special_dial_value_gap);
        sbSpecialDialValueGap.setOnSeekBarChangeListener(this);
        SeekBar sbSpecialDialValueTextSize = (SeekBar)
                vDial.findViewById(R.id.cpb_sb_special_dial_value_text_size);
        sbSpecialDialValueTextSize.setOnSeekBarChangeListener(this);
    }

    private void initLoading() {
        vLoading = LayoutInflater.from(this).inflate(R.layout.include_circleprogressbar_loading,
                vgControl, false);
        RadioGroup rgProgressMode = (RadioGroup) vLoading.findViewById(R.id.cpb_rg_progress_mode);
        rgProgressMode.setOnCheckedChangeListener(this);
        SeekBar sbLoadingStartAngle = (SeekBar) vLoading.findViewById(R.id.cpb_sb_loading_start_angle);
        sbLoadingStartAngle.setOnSeekBarChangeListener(this);
        SeekBar sbLoadingSweepAngle = (SeekBar) vLoading.findViewById(R.id.cpb_sb_loading_sweep_angle);
        sbLoadingSweepAngle.setOnSeekBarChangeListener(this);
        SeekBar sbLoadingDuration = (SeekBar) vLoading.findViewById(R.id.cpb_sb_loading_duration);
        sbLoadingDuration.setOnSeekBarChangeListener(this);
        RadioGroup rgRepeatMode = (RadioGroup) vLoading.findViewById(R.id.cpb_rg_repeat_mode);
        rgRepeatMode.setOnCheckedChangeListener(this);
        Switch stDrawOther = (Switch) vLoading.findViewById(R.id.cpb_st_loading_draw_other);
        stDrawOther.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_circleprogressbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cpb_action_base:
                vgControl.removeAllViews();
                vgControl.addView(vBase);
                return true;
            case R.id.cpb_action_progress:
                vgControl.removeAllViews();
                vgControl.addView(vProgress);
                return true;
            case R.id.cpb_action_dial:
                vgControl.removeAllViews();
                vgControl.addView(vDial);
                return true;
            case R.id.cpb_action_loading:
                vgControl.removeAllViews();
                vgControl.addView(vLoading);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @SuppressWarnings("all")
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.cpb_rb_left:
                cpbDemo.setGravity(Gravity.LEFT);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_center_horizontal:
                cpbDemo.setGravity(Gravity.CENTER_HORIZONTAL);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_right:
                cpbDemo.setGravity(Gravity.RIGHT);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_center_vertical:
                cpbDemo.setGravity(Gravity.CENTER_VERTICAL);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_center:
                cpbDemo.setGravity(Gravity.CENTER);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_center_vertical_right:
                cpbDemo.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_bottom_left:
                cpbDemo.setGravity(Gravity.BOTTOM | Gravity.LEFT);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_bottom_center_horizontal:
                cpbDemo.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_bottom_right:
                cpbDemo.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                break;
            case R.id.cpb_rb_visible:
                cpbDemo.setDialVisibility(View.VISIBLE);
                break;
            case R.id.cpb_rb_invisible:
                cpbDemo.setDialVisibility(View.INVISIBLE);
                break;
            case R.id.cpb_rb_gone:
                cpbDemo.setDialVisibility(View.GONE);
                break;
            case R.id.cpb_rb_dial_top:
                cpbDemo.setDialGravity(Gravity.TOP);
                break;
            case R.id.cpb_rb_dial_center:
                cpbDemo.setDialGravity(Gravity.CENTER);
                break;
            case R.id.cpb_rb_dial_bottom:
                cpbDemo.setDialGravity(Gravity.BOTTOM);
                break;
            case R.id.cpb_rb_type_none:
                cpbDemo.setScaleType(CircleProgressBar.ST_NONE);
                break;
            case R.id.cpb_rb_type_inside:
                cpbDemo.setScaleType(CircleProgressBar.ST_INSIDE);
                break;
            case R.id.cpb_rb_type_crop:
                cpbDemo.setScaleType(CircleProgressBar.ST_CROP);
                break;
            case R.id.cpb_rb_type_inside_crop:
                cpbDemo.setScaleType(CircleProgressBar.ST_INSIDE | CircleProgressBar.ST_CROP);
                break;
            case R.id.cpb_rb_mode_progress:
                cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.PROGRESS);
                break;
            case R.id.cpb_rb_mode_loading:
                cpbDemo.setProgressMode(CircleProgressBar.ProgressMode.LOADING);
                break;
            case R.id.cpb_rb_repeat_restart:
                cpbDemo.setLoadingRepeatMode(ValueAnimator.RESTART);
                break;
            case R.id.cpb_rb_repeat_reverse:
                cpbDemo.setLoadingRepeatMode(ValueAnimator.REVERSE);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.cpb_sb_radius:
                cpbDemo.setRadius(density * (progress + 100));
                break;
            case R.id.cpb_sb_start_angle:
                cpbDemo.setStartAngle(progress);
                break;
            case R.id.cpb_sb_sweep_angle:
                cpbDemo.setSweepAngle(progress);
                break;
            case R.id.cpb_sb_background_size:
                cpbDemo.setBackgroundSize(density * progress);
                break;
            case R.id.cpb_sb_progress_size:
                cpbDemo.setProgressSize(density * progress);
                break;
            case R.id.cpb_sb_progress:
                cpbDemo.animationToProgress(progress);
                break;
            case R.id.cpb_sb_dial_gap:
                cpbDemo.setDialGap(density * progress);
                break;
            case R.id.cpb_sb_dial_angle:
                cpbDemo.setDialAngle(progress);
                break;
            case R.id.cpb_sb_dial_height:
                cpbDemo.setDialHeight(density * progress);
                break;
            case R.id.cpb_sb_dial_width:
                cpbDemo.setDialWidth(density * progress);
                break;
            case R.id.cpb_sb_dial_special_unit:
                cpbDemo.setDialSpecialUnit(progress);
                break;
            case R.id.cpb_sb_dial_special_height:
                cpbDemo.setDialSpecialHeight(density * progress);
                break;
            case R.id.cpb_sb_dial_special_width:
                cpbDemo.setDialSpecialWidth(density * progress);
                break;
            case R.id.cpb_sb_special_dial_value_gap:
                cpbDemo.setSpecialDialValueGap(density * progress);
                break;
            case R.id.cpb_sb_special_dial_value_text_size:
                cpbDemo.setSpecialDialValueTextSize(density * (progress + 5));
                break;
            case R.id.cpb_sb_progress_value_text_size:
                cpbDemo.setProgressValueTextSize(density * (progress + 50));
                break;
            case R.id.cpb_sb_top_text_gap:
                cpbDemo.setTopTextGap(density * progress);
                break;
            case R.id.cpb_sb_top_text_size:
                cpbDemo.setTopTextSize(density * (progress + 10));
                break;
            case R.id.cpb_sb_bottom_text_gap:
                cpbDemo.setBottomTextGap(density * progress);
                break;
            case R.id.cpb_sb_bottom_text_size:
                cpbDemo.setBottomTextSize(density * (progress + 10));
                break;
            case R.id.cpb_sb_progress_duration:
                cpbDemo.setProgressDuration(100 * (progress + 1));
                break;
            case R.id.cpb_sb_loading_start_angle:
                cpbDemo.setLoadingStartAngle(progress);
                break;
            case R.id.cpb_sb_loading_sweep_angle:
                cpbDemo.setLoadingSweepAngle(progress);
                break;
            case R.id.cpb_sb_loading_duration:
                cpbDemo.setLoadingDuration(100 * (progress + 1));
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cpb_st_gradient:
                if (isChecked)
                    cpbDemo.setGradientColors(0xff33b5e5, 0xff99cc00, 0xffffbb33, 0xffff4444, 0xff33b5e5);
                else
                    cpbDemo.setGradientColors(0xffff4444);
                break;
            case R.id.cpb_st_special_dial_value:
                cpbDemo.setShowSpecialDialValue(isChecked);
                break;
            case R.id.cpb_st_progress_value:
                cpbDemo.setShowProgressValue(isChecked);
                break;
            case R.id.cpb_st_loading_draw_other:
                cpbDemo.setLoadingDrawOther(isChecked);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        cpbDemo.animationToProgress(0, cpbDemo.getProgress());
    }

    /**
     * 启动页面
     *
     * @param context Context
     */
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CircleProgressBarActivity.class));
    }
}
