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
package am.project.x.business.drawables.combinationdrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import am.drawable.CombinationDrawable;
import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 双层图片
 */
public class CombinationDrawableActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    private CombinationDrawable drawable;

    public static void start(Context context) {
        context.startActivity(new Intent(context, CombinationDrawableActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_combinationdrawable;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.cod_toolbar);
        drawable = new CombinationDrawable(
                ContextCompat.getDrawable(this, R.drawable.bg_combinationdrawable_item),
                ContextCompat.getDrawable(this, R.drawable.ic_drawables_drawable));
        this.<ImageView>findViewById(R.id.cod_iv_image).setImageDrawable(drawable);
        this.<Spinner>findViewById(R.id.cod_sp_gravity).setOnItemSelectedListener(this);
        this.<SeekBar>findViewById(R.id.cod_sb_width).setOnSeekBarChangeListener(this);
    }

    // Listener
    @SuppressLint("RtlHardcoded")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            default:
            case 0:
                drawable.setGravity(Gravity.CENTER);
                break;
            case 1:
                drawable.setGravity(Gravity.LEFT);
                break;
            case 2:
                drawable.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            case 3:
                drawable.setGravity(Gravity.RIGHT);
                break;
            case 4:
                drawable.setGravity(Gravity.CENTER_VERTICAL);
                break;
            case 5:
                drawable.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                break;
            case 6:
                drawable.setGravity(Gravity.BOTTOM);
                break;
            case 7:
                drawable.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                break;
            case 8:
                drawable.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        final int padding = (int) (getResources().getDisplayMetrics().density * progress);
        drawable.setReservedSide(padding, padding, padding, padding);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
