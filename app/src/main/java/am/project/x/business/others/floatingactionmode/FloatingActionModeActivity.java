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
package am.project.x.business.others.floatingactionmode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 悬浮菜单
 */
public class FloatingActionModeActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, FloatingActionModeActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_floatingactionmode;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.floating_toolbar);
    }
}
