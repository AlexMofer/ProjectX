package am.project.x.activities.widgets.circleprogressbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.circleprogressbar.CircleProgressBar;

public class CircleProgressBarActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener {

    private CircleProgressBar cpbDemo;
    private RadioGroup rgGravity1, rgGravity2, rgGravity3;
    private float density;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_circleprogressbar;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.circleprogressbar_toolbar);
        density = getResources().getDisplayMetrics().density;
        cpbDemo = (CircleProgressBar) findViewById(R.id.circleprogressbar_cpb_demo);
        rgGravity1 = (RadioGroup) findViewById(R.id.cpb_rg_gravity_1);
        rgGravity1.setOnCheckedChangeListener(this);
        rgGravity2 = (RadioGroup) findViewById(R.id.cpb_rg_gravity_2);
        rgGravity2.setOnCheckedChangeListener(this);
        rgGravity3 = (RadioGroup) findViewById(R.id.cpb_rg_gravity_3);
        rgGravity3.setOnCheckedChangeListener(this);
        SeekBar sbRadius = (SeekBar) findViewById(R.id.cpb_sb_radius);
        sbRadius.setOnSeekBarChangeListener(this);
        SeekBar sbStartAngle = (SeekBar) findViewById(R.id.cpb_sb_start_angle);
        sbStartAngle.setOnSeekBarChangeListener(this);
        SeekBar sbSweepAngle = (SeekBar) findViewById(R.id.cpb_sb_sweep_angle);
        sbSweepAngle.setOnSeekBarChangeListener(this);
        SeekBar sbBackgroundSize = (SeekBar) findViewById(R.id.cpb_sb_background_size);
        sbBackgroundSize.setOnSeekBarChangeListener(this);
        SeekBar sbProgressSize = (SeekBar) findViewById(R.id.cpb_sb_progress_size);
        sbProgressSize.setOnSeekBarChangeListener(this);
        SeekBar sbProgress = (SeekBar) findViewById(R.id.cpb_sb_progress);
        sbProgress.setOnSeekBarChangeListener(this);
        Switch stGradient = (Switch) findViewById(R.id.cpb_st_gradient);
        stGradient.setOnCheckedChangeListener(this);
        RadioGroup rgVisibility = (RadioGroup) findViewById(R.id.cpb_rg_dial_visibility);
        rgVisibility.setOnCheckedChangeListener(this);
        SeekBar sbDialGap = (SeekBar) findViewById(R.id.cpb_sb_dial_gap);
        sbDialGap.setOnSeekBarChangeListener(this);
        SeekBar sbDialAngle = (SeekBar) findViewById(R.id.cpb_sb_dial_angle);
        sbDialAngle.setOnSeekBarChangeListener(this);
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
        if (isChecked)
            cpbDemo.setGradientColors(0xff33b5e5, 0xff99cc00, 0xffffbb33, 0xffff4444, 0xff33b5e5);
        else
            cpbDemo.setGradientColors(0xffff4444);
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
