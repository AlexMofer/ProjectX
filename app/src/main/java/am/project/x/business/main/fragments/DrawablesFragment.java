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

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import am.appcompat.app.BaseFragment;
import am.project.x.R;
import am.project.x.business.drawables.cornerdrawable.CornerDrawableActivity;
import am.project.x.business.drawables.framedrawable.FrameDrawableActivity;
import am.project.x.business.drawables.griddrawable.GridDrawableActivity;
import am.project.x.business.drawables.linedrawable.LineDrawableActivity;
import am.project.x.business.drawables.loadingdrawable.LoadingDrawableActivity;
import am.project.x.business.drawables.textdrawable.TextDrawableActivity;

public class DrawablesFragment extends BaseFragment {

    public static DrawablesFragment newInstance() {
        return new DrawablesFragment();
    }

    public DrawablesFragment() {
        super(R.layout.fragment_main_drawables);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.drawable_btn_loading).setOnClickListener(this::open);
        findViewById(R.id.drawable_btn_cornerdrawable).setOnClickListener(this::open);
        findViewById(R.id.drawable_btn_framedrawable).setOnClickListener(this::open);
        findViewById(R.id.drawable_btn_linedrawable).setOnClickListener(this::open);
        findViewById(R.id.drawable_btn_griddrawable).setOnClickListener(this::open);
        findViewById(R.id.drawable_btn_textdrawable).setOnClickListener(this::open);
    }

    private void open(View v) {
        switch (v.getId()) {
            case R.id.drawable_btn_loading:
                LoadingDrawableActivity.start(requireContext());
                break;
            case R.id.drawable_btn_cornerdrawable:
                CornerDrawableActivity.start(requireContext());
                break;
            case R.id.drawable_btn_framedrawable:
                FrameDrawableActivity.start(requireContext());
                break;
            case R.id.drawable_btn_linedrawable:
                LineDrawableActivity.start(requireContext());
                break;
            case R.id.drawable_btn_griddrawable:
                GridDrawableActivity.start(requireContext());
                break;
            case R.id.drawable_btn_textdrawable:
                TextDrawableActivity.start(requireContext());
                break;
        }
    }
}
