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
package io.github.alexmofer.projectx.business.main.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am.appcompat.app.Fragment;

import io.github.alexmofer.projectx.R;
import io.github.alexmofer.projectx.business.drawables.cornerdrawable.CornerDrawableActivity;
import io.github.alexmofer.projectx.business.drawables.framedrawable.FrameDrawableActivity;
import io.github.alexmofer.projectx.business.drawables.griddrawable.GridDrawableActivity;
import io.github.alexmofer.projectx.business.drawables.linedrawable.LineDrawableActivity;
import io.github.alexmofer.projectx.business.drawables.loadingdrawable.LoadingDrawableActivity;
import io.github.alexmofer.projectx.business.drawables.textdrawable.TextDrawableActivity;

public class DrawablesFragment extends Fragment {

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
        final int id = v.getId();
        if (id == R.id.drawable_btn_loading) {
            LoadingDrawableActivity.start(requireContext());
        } else if (id == R.id.drawable_btn_cornerdrawable) {
            CornerDrawableActivity.start(requireContext());
        } else if (id == R.id.drawable_btn_framedrawable) {
            FrameDrawableActivity.start(requireContext());
        } else if (id == R.id.drawable_btn_linedrawable) {
            LineDrawableActivity.start(requireContext());
        } else if (id == R.id.drawable_btn_griddrawable) {
            GridDrawableActivity.start(requireContext());
        } else if (id == R.id.drawable_btn_textdrawable) {
            TextDrawableActivity.start(requireContext());
        }
    }
}
