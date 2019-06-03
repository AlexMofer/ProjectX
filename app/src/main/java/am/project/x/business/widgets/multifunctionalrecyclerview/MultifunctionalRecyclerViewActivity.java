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
package am.project.x.business.widgets.multifunctionalrecyclerview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.widget.multifunctionalrecyclerview.MultifunctionalLinearLayoutManager;
import am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;

/**
 * 多功能RecyclerView
 */
public class MultifunctionalRecyclerViewActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, MultifunctionalRecyclerViewActivity.class));
    }

    private final Adapter mAdapter = new Adapter();
    private MultifunctionalRecyclerView mVContent;
    private MultifunctionalLinearLayoutManager mManager;

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_multifunctionalrecyclerview;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.mrv_toolbar);
        mVContent = findViewById(R.id.mrv_rv_content);
        final int gap = 20;
        mManager = new MultifunctionalLinearLayoutManager(this);
        mManager.setLayoutInCenter(true);
        mManager.setChildMaxSize(mAdapter.getChildMaxWidth(), mAdapter.getChildMaxHeight());
        mManager.setDecorationMaxWidthOfChildWithMaxSize(gap, gap, gap, gap);
        mVContent.setLayoutManager(mManager);
        mVContent.addItemDecoration(new ItemDecoration(gap));
        mVContent.setScaleEnable(true);
        mVContent.setScaleRange(1, 6);

        mVContent.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_multifunctionalrecyckerview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mrv_vertical_continuity:
                mManager.setOrientation(RecyclerView.VERTICAL);
                mManager.setPagingEnable(false);
                mVContent.invalidateItemDecorations();
                return true;
            case R.id.mrv_vertical_singular:
                mManager.setOrientation(RecyclerView.VERTICAL);
                mManager.setPagingEnable(true);
                mVContent.invalidateItemDecorations();
                return true;
            case R.id.mrv_horizontal_continuity:
                mManager.setOrientation(RecyclerView.HORIZONTAL);
                mManager.setPagingEnable(false);
                mVContent.invalidateItemDecorations();
                return true;
            case R.id.mrv_horizontal_singular:
                mManager.setOrientation(RecyclerView.HORIZONTAL);
                mManager.setPagingEnable(true);
                mVContent.invalidateItemDecorations();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
