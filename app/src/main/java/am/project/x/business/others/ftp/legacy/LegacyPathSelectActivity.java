/*
 * Copyright (C) 2019 AlexMofer
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
package am.project.x.business.others.ftp.legacy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.utils.FolderFilter;

/**
 * 传统路径选择页面
 * Created by Alex on 2019/10/10.
 */
public class LegacyPathSelectActivity extends BaseActivity implements
        DialogInterface.OnClickListener {

    private static final String EXTRA_PATH = "LegacyPathSelectActivity.EXTRA_PATH";
    private static final FolderFilter FILTER = new FolderFilter();
    private Toolbar mVToolbar;
    private final FolderAdapter mAdapter = new FolderAdapter();
    private AlertDialog mCreator;
    private EditText mVName;

    public static Intent getStarter(Context context, String path) {
        final Intent starter = new Intent(context, LegacyPathSelectActivity.class);
        starter.putExtra(EXTRA_PATH, path);
        return starter;
    }

    public static String getPath(Intent data) {
        return data == null ? null : data.getStringExtra(EXTRA_PATH);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_legacy_path_select;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        mAdapter.setDir(getIntent().getStringExtra(EXTRA_PATH));
        setSupportActionBar(R.id.lps_toolbar);
        mVToolbar = findViewById(R.id.lps_toolbar);
        final RecyclerView content = findViewById(R.id.lps_content);
        content.setLayoutManager(new LinearLayoutManager(this));
        content.setAdapter(mAdapter);
        mVToolbar.setSubtitle(mAdapter.getSubtitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_legacy_path_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
            case R.id.lps_create:
                create();
                return true;
            case R.id.lps_select:
                select();
                return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.canBack()) {
            mAdapter.back();
            mVToolbar.setSubtitle(mAdapter.getSubtitle());
            return;
        }
        super.onBackPressed();
    }

    // Listener
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (mVName == null)
                return;
            final String name = mVName.getText().toString();
            if (name.trim().length() <= 0) {
                Toast.makeText(this, R.string.lps_create_name_invalid,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            final File dir = new File(mAdapter.getPath(), name);
            if (dir.mkdir()) {
                mVName.setText(null);
                mAdapter.into(dir.getPath());
                mVToolbar.setSubtitle(mAdapter.getSubtitle());
            } else
                Toast.makeText(this, R.string.lps_create_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void create() {
        final String path = mAdapter.getPath();
        if (path == null) {
            Toast.makeText(this, R.string.lps_path_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCreator == null) {
            final View view = View.inflate(this, R.layout.dlg_legacy_path_select_create,
                    null);
            mVName = view.findViewById(R.id.lps_edt_name);
            mCreator = new AlertDialog.Builder(this)
                    .setTitle(R.string.lps_create_title)
                    .setView(view)
                    .setPositiveButton(R.string.lps_create_positive, this)
                    .setNegativeButton(R.string.lps_create_negative, this)
                    .create();
        }
        mCreator.show();
    }

    private void select() {
        final String path = mAdapter.getPath();
        if (path == null) {
            Toast.makeText(this, R.string.lps_path_invalid, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setResult(RESULT_OK, new Intent().putExtra(EXTRA_PATH, path));
        finish();
    }

    private void into(int position) {
        mAdapter.into(position);
        mVToolbar.setSubtitle(mAdapter.getSubtitle());
    }

    private class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FolderViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_legacy_path_select_item, parent, false));
            itemView.setOnClickListener(this);
        }

        void bind(File file) {
            ((TextView) itemView).setText(file.getName());
        }

        // Listener
        @Override
        public void onClick(View v) {
            into(getAdapterPosition());
        }
    }

    private class FolderAdapter extends RecyclerView.Adapter<FolderViewHolder> {

        private File[] mItems;
        private String mHome;
        private File mDir;

        @NonNull
        @Override
        public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FolderViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
            holder.bind(mItems[position]);
        }

        @Override
        public int getItemCount() {
            return mItems == null ? 0 : mItems.length;
        }

        void setDir(String path) {
            mHome = path;
            into(path);
        }

        String getSubtitle() {
            if (mDir == null)
                return getString(R.string.lps_path_invalid);
            //noinspection deprecation
            final String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            if (TextUtils.equals(root, mDir.getPath()))
                return getString(R.string.lps_path_root);
            return mDir.getName();
        }

        void into(int position) {
            mDir = mItems[position];
            mItems = null;
            if (mDir != null && mDir.exists() && mDir.isDirectory())
                mItems = mDir.listFiles(FILTER);
            notifyDataSetChanged();
        }

        void into(String path) {
            mDir = path == null ? null : new File(path);
            mItems = null;
            if (mDir != null && mDir.exists() && mDir.isDirectory())
                mItems = mDir.listFiles(FILTER);
            notifyDataSetChanged();
        }

        boolean canBack() {
            if (mHome == null)
                return false;
            return !TextUtils.equals(mHome, mDir.getPath());
        }

        void back() {
            mDir = mDir.getParentFile();
            mItems = null;
            if (mDir != null && mDir.exists() && mDir.isDirectory())
                mItems = mDir.listFiles(FILTER);
            notifyDataSetChanged();
        }

        String getPath() {
            return mDir == null ? null : mDir.getPath();
        }
    }
}
