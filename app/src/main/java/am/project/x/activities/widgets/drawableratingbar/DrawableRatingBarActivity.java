package am.project.x.activities.widgets.drawableratingbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.drawableratingbar.DrawableRatingBar;

public class DrawableRatingBarActivity extends BaseActivity implements 
        RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener{

    private DrawableRatingBar dbrDemo;
    private RadioGroup rgGravity1, rgGravity2, rgGravity3;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_drawableratingbar;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.drb_toolbar);
        dbrDemo = (DrawableRatingBar) findViewById(R.id.drb_rb_stars);
        rgGravity1 = (RadioGroup) findViewById(R.id.drb_rg_gravity_1);
        rgGravity1.setOnCheckedChangeListener(this);
        rgGravity2 = (RadioGroup) findViewById(R.id.drb_rg_gravity_2);
        rgGravity2.setOnCheckedChangeListener(this);
        rgGravity3 = (RadioGroup) findViewById(R.id.drb_rg_gravity_3);
        rgGravity3.setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.drb_sh_manually)).setOnCheckedChangeListener(this);
        ((Switch) findViewById(R.id.drb_sh_touchable)).setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.drb_sb_max)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.drb_sb_min)).setOnSeekBarChangeListener(this);
    }

    @Override
    @SuppressWarnings("all")
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.drb_rb_left:
                dbrDemo.setGravity(Gravity.LEFT);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_center_horizontal:
                dbrDemo.setGravity(Gravity.CENTER_HORIZONTAL);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_right:
                dbrDemo.setGravity(Gravity.RIGHT);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_center_vertical:
                dbrDemo.setGravity(Gravity.CENTER_VERTICAL);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_center:
                dbrDemo.setGravity(Gravity.CENTER);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_center_vertical_right:
                dbrDemo.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity3.setOnCheckedChangeListener(null);
                rgGravity3.clearCheck();
                rgGravity3.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_bottom_left:
                dbrDemo.setGravity(Gravity.BOTTOM | Gravity.LEFT);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_bottom_center_horizontal:
                dbrDemo.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                break;
            case R.id.drb_rb_bottom_right:
                dbrDemo.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                rgGravity1.setOnCheckedChangeListener(null);
                rgGravity1.clearCheck();
                rgGravity1.setOnCheckedChangeListener(this);
                rgGravity2.setOnCheckedChangeListener(null);
                rgGravity2.clearCheck();
                rgGravity2.setOnCheckedChangeListener(this);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.drb_sh_manually:
                dbrDemo.setManually(isChecked);
                break;
            case R.id.drb_sh_touchable:
                dbrDemo.setOnlyItemTouchable(isChecked);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.drb_sb_max:
                dbrDemo.setMax(6 + progress);
                break;
            case R.id.drb_sb_min:
                dbrDemo.setMin(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DrawableRatingBarActivity.class));
    }
}
