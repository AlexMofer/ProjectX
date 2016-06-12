package am.project.x.activities.widgets.wraplayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.wraplayout.WrapLayout;

public class WrapLayoutActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

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
        final SeekBar sbHorizontal = (SeekBar) findViewById(R.id.wly_sb_horizontal);
        sbHorizontal.setOnSeekBarChangeListener(this);
        sbHorizontal.setProgress(15);
        final SeekBar sbVertical = (SeekBar) findViewById(R.id.wly_sb_vertical);
        sbVertical.setOnSeekBarChangeListener(this);
        sbVertical.setProgress(15);
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
