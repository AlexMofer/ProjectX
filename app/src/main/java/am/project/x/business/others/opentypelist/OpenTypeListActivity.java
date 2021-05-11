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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.am.appcompat.app.AppCompatActivity;

import am.project.x.R;
import am.project.x.business.others.opentype.OpenTypeActivity;

/**
 * 字体文件列表
 */
public class OpenTypeListActivity extends AppCompatActivity implements OpenTypeListView,
        OpenTypeListViewHolder.OnViewHolderListener {

    private final OpenTypeListPresenter mPresenter =
            new OpenTypeListPresenter().setViewHolder(getViewHolder());
    private final OpenTypeListAdapter mAdapter = new OpenTypeListAdapter(mPresenter, this);

    public OpenTypeListActivity() {
        super(R.layout.activity_opentypelist);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, OpenTypeListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    // View
    @Override
    public void onOpenTypeLoaded() {
        mAdapter.notifyDataSetChanged();
    }

    // Listener
    @Override
    public void onItemClick(Object item) {
        OpenTypeActivity.start(this, mPresenter.getItemPath(item));
    }
}
