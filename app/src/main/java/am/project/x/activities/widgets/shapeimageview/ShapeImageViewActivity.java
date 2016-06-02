package am.project.x.activities.widgets.shapeimageview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.shapeimageview.RoundRectImageShape;
import am.widget.shapeimageview.ShapeImageView;

public class ShapeImageViewActivity extends BaseActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        RadioGroup.OnCheckedChangeListener {

    private ShapeImageView sivCircle;
    private ShapeImageView sivRoundRect;
    private RadioGroup rgScaleType1, rgScaleType2;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_shapeimageview;
    }

    @Override
    @SuppressWarnings("all")
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.siv_toolbar);
        sivCircle = (ShapeImageView) findViewById(R.id.siv_image_c);
        sivRoundRect = (ShapeImageView) findViewById(R.id.siv_image_r);
        sivCircle.setOnClickListener(this);
        sivRoundRect.setOnClickListener(this);
        sivCircle.setTag(true);
        sivRoundRect.setTag(true);
        SeekBar sbHeight = (SeekBar) findViewById(R.id.siv_sb_height);
        sbHeight.setOnSeekBarChangeListener(this);
        sbHeight.setProgress(0);
        SeekBar sbBorder = (SeekBar) findViewById(R.id.siv_sb_border);
        sbBorder.setOnSeekBarChangeListener(this);
        sbBorder.setProgress(2);
        SeekBar sbRadius = (SeekBar) findViewById(R.id.siv_sb_radius);
        sbRadius.setOnSeekBarChangeListener(this);
        sbRadius.setProgress(10);
        SeekBar sbPadding = (SeekBar) findViewById(R.id.siv_sb_padding);
        sbPadding.setOnSeekBarChangeListener(this);
        sbPadding.setProgress(0);
        rgScaleType1 = (RadioGroup) findViewById(R.id.siv_rg_st1);
        rgScaleType1.setOnCheckedChangeListener(this);
        rgScaleType2 = (RadioGroup) findViewById(R.id.siv_rg_st2);
        rgScaleType2.setOnCheckedChangeListener(this);
        rgScaleType1.check(R.id.siv_rb_centercrop);
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        switch (v.getId()) {
            case R.id.siv_image_c:
                if (tag != null && tag instanceof Boolean && (Boolean) tag) {
                    sivCircle.setImageResource(R.drawable.bg_shapeimageview);
                    sivCircle.setBorderColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    sivCircle.setTag(false);
                } else {
                    sivCircle.setImageResource(R.drawable.bg_welcome);
                    sivCircle.setBorderColor(ContextCompat.getColor(this, R.color.colorAccent));
                    sivCircle.setTag(true);
                }
                break;
            case R.id.siv_image_r:
                if (tag != null && tag instanceof Boolean && (Boolean) tag) {
                    sivRoundRect.setImageResource(R.drawable.bg_shapeimageview);
                    sivRoundRect.setBorderColor(ContextCompat.getColor(this, R.color.colorPrimary));
                    sivRoundRect.setTag(false);
                } else {
                    sivRoundRect.setImageResource(R.drawable.bg_welcome);
                    sivRoundRect.setBorderColor(ContextCompat.getColor(this, R.color.colorAccent));
                    sivRoundRect.setTag(true);
                }
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.siv_sb_height:
                int height = 100 - progress;
                sivCircle.setFixedSize(100, height);
                sivRoundRect.setFixedSize(100, height);
                break;
            case R.id.siv_sb_border:
                int border = (int) (progress * getResources().getDisplayMetrics().density);
                sivCircle.setBorderWidth(border);
                sivRoundRect.setBorderWidth(border);
                break;
            case R.id.siv_sb_radius:
                float radius = progress * getResources().getDisplayMetrics().density;
                ((RoundRectImageShape) sivRoundRect.getImageShape()).setRadius(radius);
                break;
            case R.id.siv_sb_padding:
                int padding = (int) (progress * getResources().getDisplayMetrics().density);
                sivCircle.setPadding(padding, padding, padding, padding);
                sivRoundRect.setPadding(padding, padding, padding, padding);
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.siv_rb_center:
                sivCircle.setScaleType(ImageView.ScaleType.CENTER);
                sivRoundRect.setScaleType(ImageView.ScaleType.CENTER);
                rgScaleType2.setOnCheckedChangeListener(null);
                rgScaleType2.clearCheck();
                rgScaleType2.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_centercrop:
                sivCircle.setScaleType(ImageView.ScaleType.CENTER_CROP);
                sivRoundRect.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rgScaleType2.setOnCheckedChangeListener(null);
                rgScaleType2.clearCheck();
                rgScaleType2.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_centerinside:
                sivCircle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                sivRoundRect.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                rgScaleType2.setOnCheckedChangeListener(null);
                rgScaleType2.clearCheck();
                rgScaleType2.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_fitcenter:
                sivCircle.setScaleType(ImageView.ScaleType.FIT_CENTER);
                sivRoundRect.setScaleType(ImageView.ScaleType.FIT_CENTER);
                rgScaleType2.setOnCheckedChangeListener(null);
                rgScaleType2.clearCheck();
                rgScaleType2.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_fitend:
                sivCircle.setScaleType(ImageView.ScaleType.FIT_END);
                sivRoundRect.setScaleType(ImageView.ScaleType.FIT_END);
                rgScaleType1.setOnCheckedChangeListener(null);
                rgScaleType1.clearCheck();
                rgScaleType1.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_fitstart:
                sivCircle.setScaleType(ImageView.ScaleType.FIT_START);
                sivRoundRect.setScaleType(ImageView.ScaleType.FIT_START);
                rgScaleType1.setOnCheckedChangeListener(null);
                rgScaleType1.clearCheck();
                rgScaleType1.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_fitxy:
                sivCircle.setScaleType(ImageView.ScaleType.FIT_XY);
                sivRoundRect.setScaleType(ImageView.ScaleType.FIT_XY);
                rgScaleType1.setOnCheckedChangeListener(null);
                rgScaleType1.clearCheck();
                rgScaleType1.setOnCheckedChangeListener(this);
                break;
            case R.id.siv_rb_matrix:
                sivCircle.setScaleType(ImageView.ScaleType.MATRIX);
                sivRoundRect.setScaleType(ImageView.ScaleType.MATRIX);
                rgScaleType1.setOnCheckedChangeListener(null);
                rgScaleType1.clearCheck();
                rgScaleType1.setOnCheckedChangeListener(this);
                break;
        }

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ShapeImageViewActivity.class));
    }
}
