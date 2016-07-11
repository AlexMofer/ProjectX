package am.project.x.activities.drawable.linear;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.drawable.LinearDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class LinearActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private LinearDrawable drawable;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_linear;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.linear_toolbar);
        drawable = new LinearDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_drawableratingbar_selected));
        ImageView ivImage = (ImageView) findViewById(R.id.linear_iv_image);
        RadioGroup rgOrientation = (RadioGroup) findViewById(R.id.linear_rg_orientation);
        rgOrientation.setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.linear_sb_number)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.linear_sb_gap)).setOnSeekBarChangeListener(this);
        rgOrientation.check(R.id.linear_rb_horizontal);
        drawable.setCount(1);
        ivImage.setImageDrawable(drawable);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.linear_rb_horizontal:
                drawable.setOrientation(LinearDrawable.HORIZONTAL);
                break;
            case R.id.linear_rb_vertical:
                drawable.setOrientation(LinearDrawable.VERTICAL);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.linear_sb_number:
                drawable.setCount(progress + 1);
                break;
            case R.id.linear_sb_gap:
                int gap = (int) (getResources().getDisplayMetrics().density * progress);
                drawable.setGap(gap);
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
        context.startActivity(new Intent(context, LinearActivity.class));
    }
}
