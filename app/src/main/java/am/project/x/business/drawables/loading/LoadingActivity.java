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
package am.project.x.business.drawables.loading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import am.drawable.CirclingDrawable;
import am.drawable.DoubleCircleDrawable;
import am.drawable.MaterialProgressDrawable;
import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 载入图片
 */
public class LoadingActivity extends BaseActivity {

    private float density;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoadingActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_loading;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.loading_toolbar);
        density = getResources().getDisplayMetrics().density;
        setDoubleCircleDrawable();
        setCirclingDrawable();
        setMaterialProgressDrawable();
    }

    private void setDoubleCircleDrawable() {
        final ImageView loading = findViewById(R.id.loading_iv_01);
        final DoubleCircleDrawable drawable = new DoubleCircleDrawable(density);
        loading.setImageDrawable(drawable);
        drawable.start();
    }

    private void setCirclingDrawable() {
        final ImageView loading = findViewById(R.id.loading_iv_02);
        final CirclingDrawable drawable = new CirclingDrawable((int) (4 * density),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getDrawable(this, R.drawable.ic_drawables_drawable));
        loading.setImageDrawable(drawable);
        drawable.start();
    }

    private void setMaterialProgressDrawable() {
        final ImageView loading = findViewById(R.id.loading_iv_03);
        final MaterialProgressDrawable drawable = new MaterialProgressDrawable(density,
                MaterialProgressDrawable.DEFAULT, 0x00000000, 255,
                0xff33b5e5, 0xff99cc00, 0xffff4444, 0xffffbb33);
        loading.setImageDrawable(drawable);
        drawable.start();
    }
}
