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
package am.project.x.business.drawables.centerdrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import am.drawable.CenterDrawable;
import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 中心图片
 */
public class CenterDrawableActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, CenterDrawableActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_centerdrawable;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.cd_toolbar);
        ImageView image = findViewById(R.id.center_iv_rectangle);
        image.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_centerdrawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary)));
        image = findViewById(R.id.center_iv_rectangle_e);
        image.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_centerdrawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary), true));
        image = findViewById(R.id.center_iv_rounded_rectangle);
        CenterDrawable cRR = new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_centerdrawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_ROUNDED_RECTANGLE, false);
        cRR.setCornerRadius(getResources().getDisplayMetrics().density * 10);
        image.setImageDrawable(cRR);
        image = findViewById(R.id.center_iv_rounded_rectangle_e);
        CenterDrawable cRRE = new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_centerdrawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_ROUNDED_RECTANGLE, true);
        cRRE.setCornerRadius(getResources().getDisplayMetrics().density * 10);
        image.setImageDrawable(cRRE);
        image = findViewById(R.id.center_iv_oval);
        image.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_centerdrawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_OVAL, false));
        image = findViewById(R.id.center_iv_oval_e);
        image.setImageDrawable(new CenterDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_centerdrawable_center),
                ContextCompat.getColor(this, R.color.colorPrimary),
                CenterDrawable.SHAPE_OVAL, true));
    }
}
