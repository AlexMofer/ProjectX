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
import io.github.alexmofer.projectx.business.developing.DevelopingActivity;

public class DevelopFragment extends Fragment {

    public static DevelopFragment newInstance() {
        return new DevelopFragment();
    }

    public DevelopFragment() {
        super(R.layout.fragment_main_develop);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.develop_btn_developing).setOnClickListener(this::open);
//        findViewById(R.id.develop_btn_supergridview).setOnClickListener(this::open);
    }

    private void open(View view) {
        DevelopingActivity.start(requireContext());
    }
}
