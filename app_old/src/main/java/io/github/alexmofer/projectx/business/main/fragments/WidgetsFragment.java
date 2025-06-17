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
import io.github.alexmofer.projectx.business.widgets.circleprogressbar.CircleProgressBarActivity;
import io.github.alexmofer.projectx.business.widgets.drawableratingbar.DrawableRatingBarActivity;
import io.github.alexmofer.projectx.business.widgets.gradienttabstrip.GradientTabStripActivity;
import io.github.alexmofer.projectx.business.widgets.headerfootergridview.HeaderFooterGridViewActivity;
import io.github.alexmofer.projectx.business.widgets.indicatortabstrip.IndicatorTabStripActivity;
import io.github.alexmofer.projectx.business.widgets.multiactiontextview.MultiActionTextViewActivity;
import io.github.alexmofer.projectx.business.widgets.multifunctionalimageview.MultifunctionalImageViewActivity;
import io.github.alexmofer.projectx.business.widgets.multifunctionalrecyclerview.MultifunctionalRecyclerViewActivity;
import io.github.alexmofer.projectx.business.widgets.recyclepager.RecyclePagerActivity;
import io.github.alexmofer.projectx.business.widgets.smoothinputlayout.SmoothInputLayoutActivity;
import io.github.alexmofer.projectx.business.widgets.statelayout.StateLayoutActivity;
import io.github.alexmofer.projectx.business.widgets.tagtabstrip.TagTabStripActivity;
import io.github.alexmofer.projectx.business.widgets.wraplayout.WrapLayoutActivity;
import io.github.alexmofer.projectx.business.widgets.zxingscanview.ZxingScanViewActivity;

public class WidgetsFragment extends Fragment {

    public WidgetsFragment() {
        super(R.layout.fragment_main_widgets);
    }

    public static WidgetsFragment newInstance() {
        return new WidgetsFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.widget_btn_gradienttabstrip).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_tagtabstrip).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_indicatortabstrip).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_shapeimageview).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_statelayout).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_wraplayout).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_drawableratingbar).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_headerfootergridview).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_multiactiontextview).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_recyclepager).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_circleprogressbar).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_zxingscanview).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_smoothinputlayout).setOnClickListener(this::open);
        findViewById(R.id.widget_btn_multifunctionalrecyclerview).setOnClickListener(this::open);
    }

    private void open(View v) {
        final int id = v.getId();
        if (id == R.id.widget_btn_gradienttabstrip) {
            GradientTabStripActivity.start(requireContext());
        } else if (id == R.id.widget_btn_tagtabstrip) {
            TagTabStripActivity.start(requireContext());
        } else if (id == R.id.widget_btn_indicatortabstrip) {
            IndicatorTabStripActivity.start(requireContext());
        } else if (id == R.id.widget_btn_shapeimageview) {
            MultifunctionalImageViewActivity.start(requireContext());
        } else if (id == R.id.widget_btn_statelayout) {
            StateLayoutActivity.start(requireContext());
        } else if (id == R.id.widget_btn_wraplayout) {
            WrapLayoutActivity.start(requireContext());
        } else if (id == R.id.widget_btn_drawableratingbar) {
            DrawableRatingBarActivity.start(requireContext());
        } else if (id == R.id.widget_btn_headerfootergridview) {
            HeaderFooterGridViewActivity.start(requireContext());
        } else if (id == R.id.widget_btn_multiactiontextview) {
            MultiActionTextViewActivity.start(requireContext());
        } else if (id == R.id.widget_btn_recyclepager) {
            RecyclePagerActivity.start(requireContext());
        } else if (id == R.id.widget_btn_circleprogressbar) {
            CircleProgressBarActivity.start(requireContext());
        } else if (id == R.id.widget_btn_zxingscanview) {
            ZxingScanViewActivity.start(requireContext());
        } else if (id == R.id.widget_btn_smoothinputlayout) {
            SmoothInputLayoutActivity.start(requireContext());
        } else if (id == R.id.widget_btn_multifunctionalrecyclerview) {
            MultifunctionalRecyclerViewActivity.start(requireContext());
        }
    }
}
