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
import io.github.alexmofer.projectx.business.others.clipboard.ClipboardActivity;
import io.github.alexmofer.projectx.business.others.crypto.CryptoActivity;
import io.github.alexmofer.projectx.business.others.floatingactionmode.FloatingActionModeActivity;
import io.github.alexmofer.projectx.business.others.font.FontActivity;
import io.github.alexmofer.projectx.business.others.ftp.FtpActivity;
import io.github.alexmofer.projectx.business.others.opentypelist.OpenTypeListActivity;
import io.github.alexmofer.projectx.business.others.printer.PrinterActivity;
import io.github.alexmofer.projectx.business.others.retrofithelper.RetrofitActivity;

public class OthersFragment extends Fragment {

    public OthersFragment() {
        super(R.layout.fragment_main_others);
    }

    public static OthersFragment newInstance() {
        return new OthersFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(R.id.other_btn_printer).setOnClickListener(this::open);
        findViewById(R.id.other_btn_crypto).setOnClickListener(this::open);
        findViewById(R.id.other_btn_ftp).setOnClickListener(this::open);
        findViewById(R.id.other_btn_font).setOnClickListener(this::open);
        findViewById(R.id.other_btn_opentype).setOnClickListener(this::open);
        findViewById(R.id.other_btn_floating).setOnClickListener(this::open);
        findViewById(R.id.other_btn_clipboard).setOnClickListener(this::open);
        findViewById(R.id.other_btn_retrofit).setOnClickListener(this::open);
    }

    private void open(View v) {
        final int id = v.getId();
        if (id == R.id.other_btn_printer) {
            PrinterActivity.start(requireContext());
        } else if (id == R.id.other_btn_crypto) {
            CryptoActivity.start(requireContext());
        } else if (id == R.id.other_btn_ftp) {
            FtpActivity.start(requireContext());
        } else if (id == R.id.other_btn_font) {
            FontActivity.start(requireContext());
        } else if (id == R.id.other_btn_opentype) {
            OpenTypeListActivity.start(requireContext());
        } else if (id == R.id.other_btn_floating) {
            FloatingActionModeActivity.start(requireContext());
        } else if (id == R.id.other_btn_clipboard) {
            ClipboardActivity.start(requireContext());
        } else if (id == R.id.other_btn_retrofit) {
            RetrofitActivity.start(requireContext());
        }
    }
}
