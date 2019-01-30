/*
 * Copyright (C) 2018 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.project.x.business.widgets.shapeimageview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import androidx.annotation.Nullable;

/**
 * 裁剪图片视图
 */
public class ShapeImageViewActivity extends BaseActivity implements
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener {

//    private final ImageShape mCircle = new CircleImageShape();
//    private final ImageShape mRect = new RoundRectImageShape();
//    private ShapeImageView mVImage;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ShapeImageViewActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_shapeimageview;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.siv_toolbar);
        // TODO
//        mVImage = findViewById(R.id.siv_image);
//        final SeekBar height = findViewById(R.id.siv_sb_height);
//        final SeekBar border = findViewById(R.id.siv_sb_border);
//        final SeekBar radius = findViewById(R.id.siv_sb_radius);
//        final SeekBar padding = findViewById(R.id.siv_sb_padding);
//
//        this.<Switch>findViewById(R.id.siv_sw_crop).setOnCheckedChangeListener(this);
//        height.setOnSeekBarChangeListener(this);
//        height.setProgress(0);
//        border.setOnSeekBarChangeListener(this);
//        border.setProgress(2);
//        radius.setOnSeekBarChangeListener(this);
//        radius.setProgress(10);
//        padding.setOnSeekBarChangeListener(this);
//        padding.setProgress(0);
//        this.<Spinner>findViewById(R.id.siv_sp_scale_type).setOnItemSelectedListener(this);
    }

    // Listener
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (isChecked) {
//            mVImage.setImageShape(mCircle);
//        } else {
//            mVImage.setImageShape(mRect);
//        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.siv_sb_height:
//                mVImage.setFixedSize(100, 100 - progress);
//                break;
//            case R.id.siv_sb_border:
//                mVImage.setBorderWidth((int) (progress *
//                        getResources().getDisplayMetrics().density));
//                break;
//            case R.id.siv_sb_radius:
//                mVImage.setRoundRectRadius(progress * getResources().getDisplayMetrics().density);
//                break;
//            case R.id.siv_sb_padding:
//                final int padding = (int) (progress * getResources().getDisplayMetrics().density);
//                mVImage.setPadding(padding, padding, padding, padding);
//                break;
//        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // do nothing
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (position) {
//            default:
//            case 0:
//                mVImage.setScaleType(ImageView.ScaleType.CENTER);
//                break;
//            case 1:
//                mVImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                break;
//            case 2:
//                mVImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                break;
//            case 3:
//                mVImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                break;
//            case 4:
//                mVImage.setScaleType(ImageView.ScaleType.FIT_END);
//                break;
//            case 5:
//                mVImage.setScaleType(ImageView.ScaleType.FIT_START);
//                break;
//            case 6:
//                mVImage.setScaleType(ImageView.ScaleType.FIT_XY);
//                break;
//            case 7:
//                mVImage.setScaleType(ImageView.ScaleType.MATRIX);
//                break;
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
