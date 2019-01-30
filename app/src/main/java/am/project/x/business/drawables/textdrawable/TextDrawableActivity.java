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
package am.project.x.business.drawables.textdrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import am.drawable.TextDrawable;
import am.project.x.R;
import am.project.x.base.BaseActivity;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 文本图片
 */
public class TextDrawableActivity extends BaseActivity implements
        CompoundButton.OnCheckedChangeListener {

    private TextDrawable drawable;

    public static void start(Context context) {
        context.startActivity(new Intent(context, TextDrawableActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_textdrawable;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.td_toolbar);
        final float size = 86 * getResources().getDisplayMetrics().density;
        drawable = new TextDrawable(getString(R.string.td_content), size,
                ContextCompat.getColor(this, R.color.colorPrimary));
        drawable.setDensity(getResources().getDisplayMetrics().density);
        if (Build.VERSION.SDK_INT >= 16) {
            findViewById(R.id.td_v_content).setBackground(drawable);
        } else {
            findViewById(R.id.td_v_content).setBackgroundDrawable(drawable);
        }
        this.<Switch>findViewById(R.id.td_sh_scale).setOnCheckedChangeListener(this);
    }

    // Listener
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        drawable.setAutoScale(isChecked);
    }
}
