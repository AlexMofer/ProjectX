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
package am.project.x.business.drawables.griddrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import am.appcompat.app.BaseActivity;
import am.drawable.GridDrawable;
import am.project.x.R;

/**
 * 网格图片
 */
public class GridDrawableActivity extends BaseActivity implements
        SeekBar.OnSeekBarChangeListener {

    private ImageView mVImage;
    private GridDrawable mDrawable;
    private float density;

    public GridDrawableActivity() {
        super(R.layout.activity_griddrawable);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, GridDrawableActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.grd_toolbar);
        mVImage = findViewById(R.id.lrd_iv_content);
        mDrawable = new GridDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_drawableratingbar_selected));
        mDrawable.setConstantSize(true);
        density = getResources().getDisplayMetrics().density;

        mVImage.setImageDrawable(mDrawable);
        this.<SeekBar>findViewById(R.id.grd_sb_number_row).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.grd_sb_number_column).setOnSeekBarChangeListener(this);
        this.<SeekBar>findViewById(R.id.grd_sb_gap).setOnSeekBarChangeListener(this);
    }

    // Listener
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.grd_sb_number_row:
                mDrawable.setRowCount(progress + 1);
                mVImage.requestLayout();
                break;
            case R.id.grd_sb_number_column:
                mDrawable.setColumnCount(progress + 1);
                mVImage.requestLayout();
                break;
            case R.id.grd_sb_gap:
                final float spacing = density * progress;
                mDrawable.setHorizontalSpacing(spacing);
                mDrawable.setVerticalSpacing(spacing);
                mVImage.requestLayout();
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
