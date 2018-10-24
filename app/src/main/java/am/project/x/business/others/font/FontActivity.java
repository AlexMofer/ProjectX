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
package am.project.x.business.others.font;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 字体
 */
public class FontActivity extends BaseActivity implements FontView {

    private final FontPresenter mPresenter = new FontPresenter(this);

    public static void start(Context context) {
        context.startActivity(new Intent(context, FontActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_font;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.font_toolbar);

        showLoading();
        mPresenter.loadConfig();
    }

    @Override
    protected FontPresenter getPresenter() {
        return mPresenter;
    }

    // View
    @Override
    public void onLoadConfigFailure() {
        dismissLoading();
        Toast.makeText(this, "无法载入字体配置文件", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onLoadConfigSuccess() {
        dismissLoading();
        Toast.makeText(this, "展示字体列表", Toast.LENGTH_SHORT).show();
    }
}
