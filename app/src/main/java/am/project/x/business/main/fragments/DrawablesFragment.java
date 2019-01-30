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
package am.project.x.business.main.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import am.project.x.R;
import am.project.x.base.BaseFragment;
import am.project.x.business.drawables.centerdrawable.CenterDrawableActivity;
import am.project.x.business.drawables.combinationdrawable.CombinationDrawableActivity;
import am.project.x.business.drawables.cornerdrawable.CornerDrawableActivity;
import am.project.x.business.drawables.lineardrawable.LinearDrawableActivity;
import am.project.x.business.drawables.linedrawable.LineDrawableActivity;
import am.project.x.business.drawables.loadingdrawable.LoadingDrawableActivity;
import am.project.x.business.drawables.textdrawable.TextDrawableActivity;
import androidx.annotation.Nullable;

public class DrawablesFragment extends BaseFragment implements View.OnClickListener {

    public static DrawablesFragment newInstance() {
        return new DrawablesFragment();
    }

    @Override
    protected int getContentViewLayout(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        return R.layout.fragment_main_drawables;
    }

    @Override
    protected void initializeFragment(Activity activity, @Nullable Bundle savedInstanceState) {
        findViewById(R.id.drawable_btn_loading).setOnClickListener(this);
        findViewById(R.id.drawable_btn_center).setOnClickListener(this);
        findViewById(R.id.drawable_btn_combination).setOnClickListener(this);
        findViewById(R.id.drawable_btn_cornerdrawable).setOnClickListener(this);
        findViewById(R.id.drawable_btn_linedrawable).setOnClickListener(this);
        findViewById(R.id.drawable_btn_lineardrawable).setOnClickListener(this);
        findViewById(R.id.drawable_btn_textdrawable).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.drawable_btn_loading:
                LoadingDrawableActivity.start(getActivity());
                break;
            case R.id.drawable_btn_center:
                CenterDrawableActivity.start(getActivity());
                break;
            case R.id.drawable_btn_combination:
                CombinationDrawableActivity.start(getActivity());
                break;
            case R.id.drawable_btn_cornerdrawable:
                CornerDrawableActivity.start(getActivity());
                break;
            case R.id.drawable_btn_linedrawable:
                LineDrawableActivity.start(getActivity());
                break;
            case R.id.drawable_btn_lineardrawable:
                LinearDrawableActivity.start(getActivity());
                break;
            case R.id.drawable_btn_textdrawable:
                TextDrawableActivity.start(getActivity());
                break;
        }
    }
}
