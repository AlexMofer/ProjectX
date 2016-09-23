package am.project.x.activities.widgets.wraplayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.wraplayout.WrapLayout;

public class WrapLayoutActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener,
RadioGroup.OnCheckedChangeListener{

    private WrapLayout lytWrap;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_wraplayout;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.wly_toolbar);
        lytWrap = (WrapLayout) findViewById(R.id.wly_lyt_warp);
        final RadioGroup rgGravity = (RadioGroup) findViewById(R.id.wly_rg_gravity);
        rgGravity.setOnCheckedChangeListener(this);
        rgGravity.check(R.id.wly_rb_top);
        final SeekBar sbHorizontal = (SeekBar) findViewById(R.id.wly_sb_horizontal);
        sbHorizontal.setOnSeekBarChangeListener(this);
        sbHorizontal.setProgress(15);
        final SeekBar sbVertical = (SeekBar) findViewById(R.id.wly_sb_vertical);
        sbVertical.setOnSeekBarChangeListener(this);
        sbVertical.setProgress(15);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.wly_rb_top:
                lytWrap.setGravity(WrapLayout.GRAVITY_TOP);
                break;
            case R.id.wly_rb_center:
                lytWrap.setGravity(WrapLayout.GRAVITY_CENTER);
                break;
            case R.id.wly_rb_bottom:
                lytWrap.setGravity(WrapLayout.GRAVITY_BOTTOM);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.wly_sb_horizontal:
                int horizontal = (int) (progress * getResources().getDisplayMetrics().density);
                lytWrap.setHorizontalSpacing(horizontal);
                break;
            case R.id.wly_sb_vertical:
                int vertical = (int) (progress * getResources().getDisplayMetrics().density);
                lytWrap.setVerticalSpacing(vertical);
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
        context.startActivity(new Intent(context, WrapLayoutActivity.class));
    }
}
