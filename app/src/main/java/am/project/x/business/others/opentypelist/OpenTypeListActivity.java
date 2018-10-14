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
package am.project.x.business.others.opentypelist;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import am.project.x.R;
import am.project.x.base.BaseActivity;

/**
 * 字体文件列表
 */
public class OpenTypeListActivity extends BaseActivity implements OpenTypeListView,
        OpenTypeListViewHolder.OnViewHolderListener {

    private final OpenTypeListPresenter mPresenter = new OpenTypeListPresenter(this);
    private final OpenTypeListAdapter mAdapter = new OpenTypeListAdapter(mPresenter, this);

    public static void start(Context context) {
        context.startActivity(new Intent(context, OpenTypeListActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_opentypelist;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.otl_toolbar);
        final RecyclerView list = findViewById(R.id.otl_content);
        final Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider_common);
        if (divider != null) {
            final DividerItemDecoration decoration = new DividerItemDecoration(list.getContext(),
                    DividerItemDecoration.VERTICAL);
            decoration.setDrawable(divider);
            list.addItemDecoration(decoration);
        }

        list.setAdapter(mAdapter);
        mPresenter.loadOpenType();
    }

    @Override
    protected OpenTypeListPresenter getPresenter() {
        return mPresenter;
    }

    // View
    @Override
    public void onOpenTypeLoaded() {
        mAdapter.notifyDataSetChanged();
    }

    // Listener
    @Override
    public void onItemClick(int position, Object item) {

    }
}
