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
package am.project.x.business.drawables.lineardrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import androidx.annotation.Nullable;

/**
 * 线性图片
 */
public class LinearDrawableActivity extends BaseActivity implements
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private float density;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LinearDrawableActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_lineardrawable;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.lrd_toolbar);
        // TODO
//        drawable = new LinearDrawable(
//                ContextCompat.getDrawable(this, R.drawable.ic_drawableratingbar_selected));
//        drawable.setCount(1);
//        density = getResources().getDisplayMetrics().density;
//        this.<ImageView>findViewById(R.id.lrd_iv_content).setImageDrawable(drawable);
//        this.<Switch>findViewById(R.id.lrd_sh_orientation).setOnCheckedChangeListener(this);
//        this.<SeekBar>findViewById(R.id.lrd_sb_number).setOnSeekBarChangeListener(this);
//        this.<SeekBar>findViewById(R.id.lrd_sb_gap).setOnSeekBarChangeListener(this);
    }

    // Listener
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        drawable.setOrientation(isChecked ? LinearDrawable.VERTICAL : LinearDrawable.HORIZONTAL);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        switch (seekBar.getId()) {
//            case R.id.lrd_sb_number:
//                drawable.setCount(progress + 1);
//                break;
//            case R.id.lrd_sb_gap:
//                drawable.setGap((int) (density * progress));
//                break;
//        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
