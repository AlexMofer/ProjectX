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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.am.appcompat.app.AppCompatActivity;

import java.util.Locale;

import am.project.x.R;
import am.util.viewpager.adapter.RecyclePagerAdapter;

/**
 * 回收页视图
 */
public class RecyclePagerActivity extends AppCompatActivity implements View.OnClickListener {

    private final Adapter mAdapter = new Adapter();
    private TextView mVPage;
    private TextView mVTitle;

    public RecyclePagerActivity() {
        super(R.layout.activity_recyclepager);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, RecyclePagerActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.rp_toolbar);
        final ViewPager pager = findViewById(R.id.rp_vp_content);
        mVPage = findViewById(R.id.rp_tv_page_value);
        mVTitle = findViewById(R.id.rp_tv_offset_value);
        pager.setAdapter(mAdapter);
        findViewById(R.id.rp_btn_reduce_page).setOnClickListener(this);
        findViewById(R.id.rp_btn_add_page).setOnClickListener(this);
        findViewById(R.id.rp_btn_reduce_offset).setOnClickListener(this);
        findViewById(R.id.rp_btn_add_offset).setOnClickListener(this);
        findViewById(R.id.rp_btn_exchange).setOnClickListener(this);
        mVPage.setText(String.format(Locale.getDefault(), "%d", mAdapter.mCount));
        mVTitle.setText(String.format(Locale.getDefault(), "%d", mAdapter.mOffset));
    }

    // Listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rp_btn_reduce_page:
                mAdapter.reducePage();
                mVPage.setText(String.format(Locale.getDefault(), "%d", mAdapter.mCount));
                break;
            case R.id.rp_btn_add_page:
                mAdapter.addPage();
                mVPage.setText(String.format(Locale.getDefault(), "%d", mAdapter.mCount));
                break;
            case R.id.rp_btn_reduce_offset:
                mAdapter.reduceOffset();
                mVTitle.setText(String.format(Locale.getDefault(), "%d", mAdapter.mOffset));
                break;
            case R.id.rp_btn_add_offset:
                mAdapter.addOffset();
                mVTitle.setText(String.format(Locale.getDefault(), "%d", mAdapter.mOffset));
                break;
            case R.id.rp_btn_exchange:
                mAdapter.exchange();
                break;
        }
    }

    private class Holder extends RecyclePagerAdapter.PagerViewHolder {

        Holder(Context context) {
            super(new AppCompatTextView(context));
            final AppCompatTextView text = (AppCompatTextView) itemView;
            text.setGravity(Gravity.CENTER);
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 128);
        }

        void setData(String data) {
            ((AppCompatTextView) itemView).setText(data);
        }
    }

    private class RedHolder extends Holder {
        RedHolder(Context context) {
            super(context);
            ((AppCompatTextView) itemView).setTextColor(
                    ContextCompat.getColor(context, R.color.colorAccent));
        }
    }

    private class BlueHolder extends Holder {

        BlueHolder(Context context) {
            super(context);
            ((AppCompatTextView) itemView).setTextColor(
                    ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

    private class Adapter extends RecyclePagerAdapter<Holder> {

        private static final int TYPE_RED = 1;
        private static final int TYPE_BLUE = 2;
        private int mCount = 5;
        private int mOffset = 1;
        private boolean mExchanged = false;

        @Override
        public int getItemCount() {
            return mCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (mExchanged)
                return position % 2 == 0 ? TYPE_RED : TYPE_BLUE;
            return position % 2 == 0 ? TYPE_BLUE : TYPE_RED;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_RED)
                return new RedHolder(parent.getContext());
            return new BlueHolder(parent.getContext());
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.setData(String.format(Locale.getDefault(), "%d",
                    position + mOffset));
        }

        void addPage() {
            mCount++;
            notifyItemInserted(mCount - 1);
        }

        void reducePage() {
            mCount--;
            mCount = mCount < 0 ? 0 : mCount;
            notifyItemRemoved(mCount);
        }

        void addOffset() {
            mOffset++;
            notifyItemRangeChanged(0, mCount);
        }

        void reduceOffset() {
            mOffset--;
            notifyItemRangeChanged(0, mCount);
        }

        void exchange() {
            mExchanged = !mExchanged;
            notifyItemRangeChanged(0, mCount);
        }
    }
}
