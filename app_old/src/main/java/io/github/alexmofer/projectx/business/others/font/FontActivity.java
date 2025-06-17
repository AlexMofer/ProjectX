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
package io.github.alexmofer.projectx.business.others.font;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import io.github.alexmofer.projectx.R;
import io.github.alexmofer.projectx.business.others.opentype.OpenTypeActivity;
import io.github.alexmofer.projectx.common.CommonActivity;

/**
 * 字体
 */
public class FontActivity extends CommonActivity implements FontView,
        FontFamilyPickerDialog.OnPickerListener, FontViewHolder.OnViewHolderListener {

    private final FontPresenter mPresenter = new FontPresenter().setViewHolder(getViewHolder());
    private final FontAdapter mAdapter = new FontAdapter(mPresenter, this);

    private FontFamilyPickerDialog mPicker;

    public FontActivity() {
        super(R.layout.activity_font);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, FontActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.font_toolbar);
        final RecyclerView content = findViewById(R.id.font_content);
        content.setAdapter(mAdapter);
        showLoading();
        mPresenter.loadConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_font, menu);
        final MenuItem item = menu.findItem(R.id.font_family);
        item.setVisible(mPresenter.getFamilyNameOrAliasCount() > 0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.font_family) {
            if (mPresenter.getFamilyNameOrAliasCount() > 0)
                showPicker(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // View
    @Override
    public void onLoadConfigFailure() {
        dismissLoading();
        Toast.makeText(this, R.string.font_error_config, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onLoadConfigSuccess() {
        dismissLoading();
        invalidateOptionsMenu();
        showPicker(false);
    }

    @Override
    public void onLoadTypefaceCollectionFailure() {
        dismissLoading();
        Toast.makeText(this, R.string.font_error_typeface, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadTypefaceCollectionSuccess() {
        dismissLoading();
        mAdapter.notifyDataSetChanged();
    }

    // Listener
    @Override
    public void onItemPicked(String item) {
        dismissDialog(mPicker);
        showLoading();
        mPresenter.loadTypefaceCollection(item);
    }

    @Override
    public void onItemClick(Object item) {
        OpenTypeActivity.start(this, mPresenter.getTypefaceItemPath(item));
    }

    private void showPicker(boolean cancelable) {
        if (mPicker == null)
            mPicker = new FontFamilyPickerDialog(this, mPresenter, this);
        mPicker.notifyDataSetChanged();
        mPicker.setCancelable(cancelable);
        mPicker.setCanceledOnTouchOutside(cancelable);
        showDialog(mPicker);
    }
}
