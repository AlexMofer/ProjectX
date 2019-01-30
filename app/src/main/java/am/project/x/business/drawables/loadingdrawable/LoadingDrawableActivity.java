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
package am.project.x.business.drawables.loadingdrawable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import androidx.annotation.Nullable;

/**
 * 载入图片
 */
public class LoadingDrawableActivity extends BaseActivity {

    private float density;

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoadingDrawableActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_loadingdrawable;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.ld_toolbar);
        density = getResources().getDisplayMetrics().density;
        setDoubleCircleDrawable();
        setCirclingDrawable();
        setMaterialProgressDrawable();
    }

    private void setDoubleCircleDrawable() {
        final ImageView loading = findViewById(R.id.ld_iv_01);
        // TODO
    }

    private void setCirclingDrawable() {
        final ImageView loading = findViewById(R.id.ld_iv_02);
        // TODO
    }

    private void setMaterialProgressDrawable() {
        final ImageView loading = findViewById(R.id.ld_iv_03);
        // TODO
    }
}
