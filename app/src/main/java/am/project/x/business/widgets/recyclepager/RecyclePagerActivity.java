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
package am.project.x.business.widgets.recyclepager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.util.viewpager.adapter.RecyclePagerAdapter;

/**
 * 回收页视图
 */
public class RecyclePagerActivity extends BaseActivity implements View.OnClickListener {

    private final Adapter mAdapter = new Adapter();


    public static void start(Context context) {
        context.startActivity(new Intent(context, RecyclePagerActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_recyclepager;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.rp_toolbar);
        final ViewPager pager = findViewById(R.id.rp_vp_content);
        pager.setAdapter(mAdapter);
        findViewById(R.id.rp_btn_remove).setOnClickListener(this);
        findViewById(R.id.rp_btn_add).setOnClickListener(this);
    }

    // Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rp_btn_remove:
                mAdapter.remove();
                break;
            case R.id.rp_btn_add:
                mAdapter.add();
                break;
        }
    }

    class Holder extends RecyclePagerAdapter.PagerViewHolder {

        Holder(Context context) {
            super(new AppCompatTextView(context));
            final AppCompatTextView text = (AppCompatTextView) itemView;
            text.setGravity(Gravity.CENTER);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        }

        public void setData(String data) {
            ((AppCompatTextView) itemView).setText(data);
        }
    }

    class Adapter extends RecyclePagerAdapter<Holder> {

        private int itemCount = 5;

        @Override
        public int getItemCount() {
            return itemCount;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(parent.getContext());
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.setData(String.format(Locale.getDefault(), "%d", position + 1));
        }

        void add() {
            itemCount++;
            notifyDataSetChanged();
        }

        void remove() {
            itemCount--;
            itemCount = itemCount < 0 ? 0 : itemCount;
            notifyDataSetChanged();
        }
    }
}
