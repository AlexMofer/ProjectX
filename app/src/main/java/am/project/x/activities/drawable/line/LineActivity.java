package am.project.x.activities.drawable.line;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.drawable.CombinationDrawable;
import am.drawable.LineDrawable;
import am.project.support.view.CompatPlus;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class LineActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        SeekBar.OnSeekBarChangeListener {

    private LineDrawable drawable;
    private RadioGroup rgGravityNormal;
    private RadioGroup rgGravityPlus;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_line;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.line_toolbar);
        drawable = new LineDrawable(ContextCompat.getColor(this, R.color.colorPrimary), 1);
        CompatPlus.setBackground(findViewById(R.id.line_tv_text), drawable);
        rgGravityNormal = (RadioGroup) findViewById(R.id.line_rg_gravity);
        rgGravityPlus = (RadioGroup) findViewById(R.id.line_rg_gravity_plus);
        rgGravityNormal.setOnCheckedChangeListener(this);
        rgGravityPlus.setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.line_sb_size)).setOnSeekBarChangeListener(this);
        rgGravityNormal.check(R.id.line_rb_left);
    }

    @Override
    @SuppressWarnings("all")
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.line_rb_left:
                drawable.setGravity(Gravity.LEFT);
                rgGravityPlus.setOnCheckedChangeListener(null);
                rgGravityPlus.clearCheck();
                rgGravityPlus.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_top:
                drawable.setGravity(Gravity.TOP);
                rgGravityPlus.setOnCheckedChangeListener(null);
                rgGravityPlus.clearCheck();
                rgGravityPlus.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_right:
                drawable.setGravity(Gravity.RIGHT);
                rgGravityPlus.setOnCheckedChangeListener(null);
                rgGravityPlus.clearCheck();
                rgGravityPlus.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_bottom:
                drawable.setGravity(Gravity.BOTTOM);
                rgGravityPlus.setOnCheckedChangeListener(null);
                rgGravityPlus.clearCheck();
                rgGravityPlus.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_horizontal:
                drawable.setGravity(Gravity.CENTER_HORIZONTAL);
                rgGravityNormal.setOnCheckedChangeListener(null);
                rgGravityNormal.clearCheck();
                rgGravityNormal.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_vertical:
                rgGravityNormal.clearCheck();
                drawable.setGravity(Gravity.CENTER_VERTICAL);
                rgGravityNormal.setOnCheckedChangeListener(null);
                rgGravityNormal.clearCheck();
                rgGravityNormal.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_topAndBottom:
                drawable.setGravity(Gravity.TOP | Gravity.BOTTOM);
                rgGravityNormal.setOnCheckedChangeListener(null);
                rgGravityNormal.clearCheck();
                rgGravityNormal.setOnCheckedChangeListener(this);
                break;
            case R.id.line_rb_leftAndRight:
                drawable.setGravity(Gravity.LEFT | Gravity.RIGHT);
                rgGravityNormal.setOnCheckedChangeListener(null);
                rgGravityNormal.clearCheck();
                rgGravityNormal.setOnCheckedChangeListener(this);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        drawable.setLineSize(progress + 1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LineActivity.class));
    }
}
