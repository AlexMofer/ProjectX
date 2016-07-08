package am.project.x.activities.drawable.combination;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.drawable.CombinationDrawable;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CombinationActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private CombinationDrawable drawable;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_combination;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.combination_toolbar);
        drawable = new CombinationDrawable(
                ContextCompat.getDrawable(this, R.drawable.bg_shapeimageview),
                ContextCompat.getDrawable(this, R.drawable.ic_drawable_default));
        ImageView ivImage = (ImageView) findViewById(R.id.combination_iv_image);
        ivImage.setImageDrawable(drawable);
        RadioGroup rgGravity = (RadioGroup) findViewById(R.id.combination_rg_gravity);
        rgGravity.setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.combination_sb_width)).setOnSeekBarChangeListener(this);
        rgGravity.check(R.id.combination_rb_center);
    }

    @Override
    @SuppressWarnings("all")
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.combination_rb_center:
                drawable.setGravity(Gravity.CENTER);
                break;
            case R.id.combination_rb_left:
                drawable.setGravity(Gravity.CENTER_VERTICAL);
                break;
            case R.id.combination_rb_top:
                drawable.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case R.id.combination_rb_right:
                drawable.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
            case R.id.combination_rb_bottom:
                drawable.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int reservedSide = (int) (getResources().getDisplayMetrics().density * progress);
        drawable.setReservedSide(reservedSide, reservedSide, reservedSide, reservedSide);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CombinationActivity.class));
    }
}
