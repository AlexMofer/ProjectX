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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import am.project.x.R;
import am.project.x.common.CommonActivity;

/**
 * OpenType
 */
public class OpenTypeActivity extends CommonActivity implements OpenTypeView,
        OpenTypePickerDialog.OnPickerListener, OpenTypeViewHolder.OnViewHolderListener {

    private static final String EXTRA_PATH = "am.project.x.business.others.opentype.OpenTypeActivity.EXTRA_PATH";
    private final OpenTypePresenter mPresenter =
            new OpenTypePresenter().setViewHolder(getViewHolder());
    private final OpenTypeAdapter mAdapter = new OpenTypeAdapter(mPresenter, this);
    private OpenTypePickerDialog mPicker;
    private AlertDialog mInfo;

    public OpenTypeActivity() {
        super(R.layout.activity_opentype);
    }

    public static void start(Context context, String path) {
        context.startActivity(
                new Intent(context, OpenTypeActivity.class).putExtra(EXTRA_PATH, path));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.ot_toolbar);
        final RecyclerView content = findViewById(R.id.ot_content);
        content.setAdapter(mAdapter);
        showLoading();
        mPresenter.parse(getIntent().getStringExtra(EXTRA_PATH));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_opentype, menu);
        final MenuItem item = menu.findItem(R.id.ot_collection);
        item.setVisible(mPresenter.isCollection());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ot_collection:
                if (mPresenter.isCollection())
                    showPicker();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // View
    @Override
    public void onParseFailure() {
        dismissLoading();
        Toast.makeText(this, R.string.ot_toast_parse_failure,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onParseSuccess(boolean isCollection) {
        dismissLoading();
        mAdapter.notifyDataSetChanged();
        if (isCollection) {
            invalidateOptionsMenu();
            // 显示字体选择对话框
            showPicker();
        }
    }

    private void showPicker() {
        if (mPicker == null)
            mPicker = new OpenTypePickerDialog(this, mPresenter, this);
        mPicker.notifyDataSetChanged();
        showDialog(mPicker);
    }

    // Listener
    @Override
    public void onItemPicked(int position) {
        mPicker.dismiss();
        mPresenter.setCollectionItem(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Object item) {
        if (mInfo == null)
            mInfo = new AlertDialog.Builder(this)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        mInfo.setTitle(mPresenter.getItemLabel(item));
        mInfo.setMessage(mPresenter.getItemInfo(item));
        mInfo.show();
    }
}
