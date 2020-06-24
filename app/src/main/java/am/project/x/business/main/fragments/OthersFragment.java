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
import am.project.x.business.others.clipboard.ClipboardActivity;
import am.project.x.business.others.crypto.CryptoActivity;
import am.project.x.business.others.floatingactionmode.FloatingActionModeActivity;
import am.project.x.business.others.font.FontActivity;
import am.project.x.business.others.ftp.FtpActivityRename;
import am.project.x.business.others.opentypelist.OpenTypeListActivity;
import am.project.x.business.others.printer.PrinterActivity;
import am.project.x.business.others.retrofithelper.RetrofitActivity;

public class OthersFragment extends BaseFragment {

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
        switch (v.getId()) {
            case R.id.other_btn_printer:
                PrinterActivity.start(requireContext());
                break;
            case R.id.other_btn_crypto:
                CryptoActivity.start(requireContext());
                break;
            case R.id.other_btn_ftp:
                FtpActivityRename.start(requireContext());
                break;
            case R.id.other_btn_font:
                FontActivity.start(requireContext());
                break;
            case R.id.other_btn_opentype:
                OpenTypeListActivity.start(requireContext());
                break;
            case R.id.other_btn_floating:
                FloatingActionModeActivity.start(requireContext());
                break;
            case R.id.other_btn_clipboard:
                ClipboardActivity.start(requireContext());
                break;
            case R.id.other_btn_retrofit:
                RetrofitActivity.start(requireContext());
                break;
        }
    }
}
