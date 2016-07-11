package am.project.x.activities.drawable.sharpcornerbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.drawable.SharpCornerBoxDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class SharpCornerBoxActivity extends BaseActivity
        implements RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_sharpcornerbox;
    }

    private SharpCornerBoxDrawable drawable;
    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.scb_toolbar);


        RadioGroup rgDirection = (RadioGroup) findViewById(R.id.scb_rg_direction);
        RadioGroup rgLocation = (RadioGroup) findViewById(R.id.scb_rg_location);
        rgDirection.setOnCheckedChangeListener(this);
        rgLocation.setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.scb_sb_width)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.scb_sb_height)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.scb_sb_cornerRadius)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.scb_sb_stoke)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.scb_sb_radius)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.scb_sb_padding)).setOnSeekBarChangeListener(this);

        rgDirection.check(R.id.scb_rb_top);
        rgLocation.check(R.id.scb_rb_center);


        final int color = ContextCompat.getColor(this, R.color.colorPrimary);
        final int width = 42;
        final int height = 18;
        final int padding = 40;
        final float round = 20;
//        CompatPlus.setBackground(findViewById(R.id.scb_top_center),
//                new SharpCornerBoxDrawable(color, width, height));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.scb_rg_direction) {
            switch (checkedId) {
                case R.id.scb_rb_top:
                    break;
                case R.id.scb_rb_bottom:
                    break;
                case R.id.scb_rb_left:
                    break;
                case R.id.scb_rb_right:
                    break;
            }
        } else {
            switch (checkedId) {
                case R.id.scb_rb_center:
                    break;
                case R.id.scb_rb_start:
                    break;
                case R.id.scb_rb_end:
                    break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.scb_sb_width:
                break;
            case R.id.scb_sb_height:
                break;
            case R.id.scb_sb_cornerRadius:
                break;
            case R.id.scb_sb_margin:
                break;
            case R.id.scb_sb_stoke:
                break;
            case R.id.scb_sb_radius:
                break;
            case R.id.scb_sb_padding:
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
        context.startActivity(new Intent(context, SharpCornerBoxActivity.class));
    }
}
