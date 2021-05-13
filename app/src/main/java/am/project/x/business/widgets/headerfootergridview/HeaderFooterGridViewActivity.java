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
package am.project.x.business.widgets.headerfootergridview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.am.appcompat.app.AppCompatActivity;
import com.am.widget.headerfootergridview.HeaderFooterGridView;

import java.util.Locale;

import am.project.x.R;

/**
 * 头尾网格视图
 */
public class HeaderFooterGridViewActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, AdapterView.OnItemClickListener {

    private final Adapter mAdapter = new Adapter();
    private HeaderFooterGridView mVContent;
    private TextView tvHeaderView1, tvHeaderView2, tvHeaderView3;
    private TextView tvHeaderItem1, tvHeaderItem2, tvHeaderItem3;
    private TextView tvFooterView1, tvFooterView2, tvFooterView3;
    private TextView tvFooterItem1, tvFooterItem2, tvFooterItem3;

    public HeaderFooterGridViewActivity() {
        super(R.layout.activity_headerfootergridview);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, HeaderFooterGridViewActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.hfg_toolbar);
        mVContent = findViewById(R.id.hfg_hfg_content);
        tvHeaderView1 = createTextView(this);
        tvHeaderView2 = createTextView(this);
        tvHeaderView3 = createTextView(this);
        tvHeaderItem1 = createTextView(this);
        tvHeaderItem2 = createTextView(this);
        tvHeaderItem3 = createTextView(this);
        tvFooterView1 = createTextView(this);
        tvFooterView2 = createTextView(this);
        tvFooterView3 = createTextView(this);
        tvFooterItem1 = createTextView(this);
        tvFooterItem2 = createTextView(this);
        tvFooterItem3 = createTextView(this);

        tvHeaderView1.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_hv), 1));
        tvHeaderView2.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_hv), 2));
        tvHeaderView3.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_hv), 3));
        tvHeaderItem1.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_header), 1));
        tvHeaderItem2.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_header), 2));
        tvHeaderItem3.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_header), 3));
        tvFooterView1.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_fv), 1));
        tvFooterView2.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_fv), 2));
        tvFooterView3.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_fv), 3));
        tvFooterItem1.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_footer), 1));
        tvFooterItem2.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_footer), 2));
        tvFooterItem3.setText(String.format(Locale.getDefault(),
                getString(R.string.hfg_footer), 3));


        tvHeaderView1.setOnClickListener(this);
        tvHeaderView2.setOnClickListener(this);
        tvHeaderView3.setOnClickListener(this);
        tvFooterView1.setOnClickListener(this);
        tvFooterView2.setOnClickListener(this);
        tvFooterView3.setOnClickListener(this);

        ((CheckBox) findViewById(R.id.hfg_cb_hv)).setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.hfg_cb_hi)).setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.hfg_cb_fv)).setOnCheckedChangeListener(this);
        ((CheckBox) findViewById(R.id.hfg_cb_fi)).setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.hfg_sb_number)).setOnSeekBarChangeListener(this);

        mVContent.setAdapter(mAdapter);
        mVContent.setOnItemClickListener(this);

    }

    private AppCompatTextView createTextView(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        AppCompatTextView text = new AppCompatTextView(context);
        text.setTextColor(0xff999999);
        text.setGravity(Gravity.CENTER);
        text.setMinimumWidth((int) (64 * density));
        text.setMinimumHeight((int) (48 * density));
        text.setBackgroundResource(R.drawable.bg_headerfootergridview_item);
        return text;
    }

    // Listener
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        final int id = compoundButton.getId();
        if (id == R.id.hfg_cb_hv) {
            changeHeaderView(checked);
        } else if (id == R.id.hfg_cb_hi) {
            changeHeaderItem(checked);
        } else if (id == R.id.hfg_cb_fv) {
            changeFooterView(checked);
        } else if (id == R.id.hfg_cb_fi) {
            changeFooterItem(checked);
        }
    }

    private void changeHeaderView(boolean add) {
        if (add) {
            mVContent.addHeaderView(tvHeaderView1);
            mVContent.addHeaderView(tvHeaderView2);
            mVContent.addHeaderView(tvHeaderView3);
        } else {
            mVContent.removeHeaderView(tvHeaderView1);
            mVContent.removeHeaderView(tvHeaderView2);
            mVContent.removeHeaderView(tvHeaderView3);
        }
    }

    private void changeHeaderItem(boolean add) {
        if (add) {
            mVContent.addHeaderItem(tvHeaderItem1, null, true);
            mVContent.addHeaderItem(tvHeaderItem2, null, true);
            mVContent.addHeaderItem(tvHeaderItem3, null, true);
        } else {
            mVContent.removeHeaderItem(tvHeaderItem1);
            mVContent.removeHeaderItem(tvHeaderItem2);
            mVContent.removeHeaderItem(tvHeaderItem3);
        }
    }

    private void changeFooterView(boolean add) {
        if (add) {
            mVContent.addFooterView(tvFooterView1);
            mVContent.addFooterView(tvFooterView2);
            mVContent.addFooterView(tvFooterView3);
        } else {
            mVContent.removeFooterView(tvFooterView1);
            mVContent.removeFooterView(tvFooterView2);
            mVContent.removeFooterView(tvFooterView3);
        }
    }

    private void changeFooterItem(boolean add) {
        if (add) {
            mVContent.addFooterItem(tvFooterItem1, null, true);
            mVContent.addFooterItem(tvFooterItem2, null, true);
            mVContent.addFooterItem(tvFooterItem3, null, true);
        } else {
            mVContent.removeFooterItem(tvFooterItem1);
            mVContent.removeFooterItem(tvFooterItem2);
            mVContent.removeFooterItem(tvFooterItem3);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mAdapter.setCount(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            Toast.makeText(getApplicationContext(), tv.getText(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onClick(view);
    }

    private class Adapter extends BaseAdapter {

        private int count;

        @Override
        public int getCount() {
            return count;
        }

        void setCount(int count) {
            this.count = count;
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Context context = parent.getContext();
            if (view == null)
                view = createTextView(context);
            final TextView tvItem = (TextView) view;
            tvItem.setText(String.format(Locale.getDefault(),
                    context.getString(R.string.hfg_item), position + 1));
            return view;
        }
    }
}
