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
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.widget.CircularProgressImageView;
import am.widget.stateframelayout.StateFrameLayout;

/**
 * 状态布局
 */
public class StateLayoutActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener, View.OnClickListener {

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
        mDLoading = ContextCompat.getDrawable(this, R.drawable.ic_statelayout_loading);
        mDError = ContextCompat.getDrawable(this, R.drawable.ic_statelayout_error);
        mDEmpty = ContextCompat.getDrawable(this, R.drawable.ic_statelayout_empty);
        final CircularProgressImageView loading = new CircularProgressImageView(this);
        loading.setColorSchemeColors(
                ContextCompat.getColor(this, android.R.color.holo_red_light),
                ContextCompat.getColor(this, android.R.color.holo_blue_light),
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.holo_purple));
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

        mVState.setOnClickListener(this);
    }

    // Listener
    @Override
    public void onClick(View v) {
        if (v == mVState) {
            if (mVState.isNormal())
                Toast.makeText(getApplicationContext(), R.string.sl_change_state_normal,
                        Toast.LENGTH_SHORT).show();
            else if (mVState.isLoading())
                Toast.makeText(getApplicationContext(), R.string.sl_change_state_loading,
                        Toast.LENGTH_SHORT).show();
            else if (mVState.isEmpty())
                Toast.makeText(getApplicationContext(), R.string.sl_change_state_empty,
                        Toast.LENGTH_SHORT).show();
            else if (mVState.isError())
                Toast.makeText(getApplicationContext(), R.string.sl_change_state_error,
                        Toast.LENGTH_SHORT).show();
        }
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
                mVState.removeView(mVLoading);
                mVState.removeView(mVError);
                mVState.removeView(mVEmpty);
                mVState.setDrawable(mDLoading, mDError, mDEmpty);
                break;
            case R.id.sl_rb_view:
                mVState.setDrawable(null, null, null);
                if (mVEmpty.getParent() != null)
                    return;
                if (mVEmpty.getLayoutParams() != null)
                    mVState.addView(mVEmpty);
                else
                    mVState.addView(mVEmpty, new StateFrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER, StateFrameLayout.STATE_EMPTY));
                if (mVError.getLayoutParams() != null)
                    mVState.addView(mVError);
                else
                    mVState.addView(mVError, new StateFrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER, StateFrameLayout.STATE_ERROR));
                if (mVLoading.getLayoutParams() != null)
                    mVState.addView(mVLoading);
                else
                    mVState.addView(mVLoading, new StateFrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.CENTER, StateFrameLayout.STATE_LOADING));
                break;
        }
    }
}
