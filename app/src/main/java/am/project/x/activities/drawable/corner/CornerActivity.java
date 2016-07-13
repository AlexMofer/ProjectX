package am.project.x.activities.drawable.corner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.drawable.CornerDrawable;
import am.project.support.view.CompatPlus;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CornerActivity extends BaseActivity
        implements RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_corner;
    }

    private CornerDrawable drawable;

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.corner_toolbar);


        RadioGroup rgDirection = (RadioGroup) findViewById(R.id.corner_rg_direction);
        RadioGroup rgLocation = (RadioGroup) findViewById(R.id.corner_rg_location);
        rgDirection.setOnCheckedChangeListener(this);
        rgLocation.setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.corner_sb_width)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.corner_sb_height)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.corner_sb_cornerRadius)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.corner_sb_stoke)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.corner_sb_radius)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.corner_sb_padding)).setOnSeekBarChangeListener(this);

        final float density = getResources().getDisplayMetrics().density;
        final int color = ContextCompat.getColor(this, R.color.colorRipple);
        final int width = (int) (10 * density);
        final int height = (int) (5 * density);
        drawable = new CornerDrawable(color, width, height);
        drawable.setStokeColor(ContextCompat.getColor(this, R.color.colorAccent));
        CompatPlus.setBackground(findViewById(R.id.corner_tv_content), drawable);
        rgDirection.check(R.id.corner_rb_top);
        rgLocation.check(R.id.corner_rb_center);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.corner_rg_direction) {
            switch (checkedId) {
                case R.id.corner_rb_top:
                    drawable.setDirection(Gravity.TOP);
                    break;
                case R.id.corner_rb_bottom:
                    drawable.setDirection(Gravity.BOTTOM);
                    break;
                case R.id.corner_rb_left:
                    drawable.setDirection(Gravity.LEFT);
                    break;
                case R.id.corner_rb_right:
                    drawable.setDirection(Gravity.RIGHT);
                    break;
            }
        } else {
            switch (checkedId) {
                case R.id.corner_rb_center:
                    drawable.setLocation(Gravity.CENTER);
                    break;
                case R.id.corner_rb_start:
                    drawable.setLocation(Gravity.LEFT);
                    break;
                case R.id.corner_rb_end:
                    drawable.setLocation(Gravity.RIGHT);
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        final float density = getResources().getDisplayMetrics().density;
        switch (seekBar.getId()) {
            case R.id.corner_sb_width:
                final int width = (int) ((10 + progress) * density);
                drawable.setCornerWidth(width);
                break;
            case R.id.corner_sb_height:
                final int height = (int) ((5 + progress) * density);
                drawable.setCornerHeight(height);
                break;
            case R.id.corner_sb_cornerRadius:
                drawable.setCornerRadius(progress);
                break;
            case R.id.corner_sb_margin:
                final int margin = (int) (progress * density);
                drawable.setCornerMargin(margin);
                break;
            case R.id.corner_sb_stoke:
                drawable.setStokeSize(progress);
                break;
            case R.id.corner_sb_radius:
                final int radius = (int) (progress * density);
                drawable.setContentRadius(radius);
                break;
            case R.id.corner_sb_padding:
                final int padding = (int) (progress * density);
                drawable.setPadding(padding, padding, padding, padding);
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
        context.startActivity(new Intent(context, CornerActivity.class));
    }
}
