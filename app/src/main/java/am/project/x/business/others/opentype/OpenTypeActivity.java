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
package am.project.x.business.others.opentype;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * OpenType
 */
public class OpenTypeActivity extends BaseActivity implements OpenTypeView {

    private static final String EXTRA_PATH = "am.project.x.business.others.opentype.OpenTypeActivity.EXTRA_PATH";
    private final OpenTypePresenter mPresenter = new OpenTypePresenter(this);
    private final OpenTypeAdapter mAdapter = new OpenTypeAdapter(mPresenter);

    public static void start(Context context, String path) {
        context.startActivity(
                new Intent(context, OpenTypeActivity.class).putExtra(EXTRA_PATH, path));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_opentype;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.ot_toolbar);
        final RecyclerView content = findViewById(R.id.ot_content);
        content.setAdapter(mAdapter);
        showLoading();
        mPresenter.parse(getIntent().getStringExtra(EXTRA_PATH));
    }

    @Override
    protected OpenTypePresenter getPresenter() {
        return mPresenter;
    }

    // View
    @Override
    public void onParseFailure() {

    }

    @Override
    public void onParseSuccess(boolean isCollection) {

    }
}
