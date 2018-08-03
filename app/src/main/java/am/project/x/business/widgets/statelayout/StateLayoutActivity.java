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
package am.project.x.business.widgets.statelayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.widget.stateframelayout.StateFrameLayout;

/**
 * 状态布局
 */
public class StateLayoutActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener {

    private StateFrameLayout mVState;
    private Drawable mDLoading;
    private Drawable mDError;
    private Drawable mDEmpty;
    private View mVLoading;
    private View mVError;
    private View mVEmpty;

    public static void start(Context context) {
        context.startActivity(new Intent(context, StateLayoutActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_statelayout;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.sl_toolbar);
        mVState = findViewById(R.id.sl_lyt_state);
        final RadioGroup state = findViewById(R.id.sl_rg_state);
        final RadioGroup mode = findViewById(R.id.sl_rg_mode);

        final AppCompatTextView loading = new AppCompatTextView(this);
        loading.setText(R.string.sl_change_state_loading);
        loading.setTextColor(0xfff2f71c);
        loading.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        mVLoading = loading;
        final AppCompatTextView error = new AppCompatTextView(this);
        error.setText(R.string.sl_change_state_error);
        error.setTextColor(0xffff4081);
        error.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        mVError = error;
        final AppCompatTextView empty = new AppCompatTextView(this);
        empty.setText(R.string.sl_change_state_empty);
        empty.setTextColor(0xff092d6d);
        empty.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 64);
        mVEmpty = empty;


        state.setOnCheckedChangeListener(this);
        state.check(R.id.sl_rb_normal);
        mode.setOnCheckedChangeListener(this);
        mode.check(R.id.sl_rb_drawable);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.sl_rb_normal:
                mVState.normal();
                break;
            case R.id.sl_rb_loading:
                mVState.loading();
                break;
            case R.id.sl_rb_error:
                mVState.error();
                break;
            case R.id.sl_rb_empty:
                mVState.empty();
                break;
            case R.id.sl_rb_drawable:
//                mVState.setStateDrawables(mLoadingDrawable, mErrorDrawable, mEmptyDrawable);
                break;
            case R.id.sl_rb_view:
                mVState.setStateViews(mVLoading, mVError, mVEmpty);
                break;
        }
    }
}
