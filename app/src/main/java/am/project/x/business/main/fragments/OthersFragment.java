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

import androidx.annotation.Nullable;

import am.project.x.R;
import am.project.x.base.BaseFragment;
import am.project.x.business.others.clipboard.ClipboardActivity;
import am.project.x.business.others.crypto.CryptoActivity;
import am.project.x.business.others.floatingactionmode.FloatingActionModeActivity;
import am.project.x.business.others.font.FontActivity;
import am.project.x.business.others.ftp.FtpActivity;
import am.project.x.business.others.opentypelist.OpenTypeListActivity;
import am.project.x.business.others.printer.PrinterActivity;
import am.project.x.business.others.retrofithelper.RetrofitActivity;

public class OthersFragment extends BaseFragment implements View.OnClickListener {

    public static OthersFragment newInstance() {
        return new OthersFragment();
    }

    @Override
    protected int getContentViewLayout(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        return R.layout.fragment_main_others;
    }

    @Override
    protected void initializeFragment(Activity activity, @Nullable Bundle savedInstanceState) {
        findViewById(R.id.other_btn_printer).setOnClickListener(this);
        findViewById(R.id.other_btn_crypto).setOnClickListener(this);
        findViewById(R.id.other_btn_ftp).setOnClickListener(this);
        findViewById(R.id.other_btn_font).setOnClickListener(this);
        findViewById(R.id.other_btn_opentype).setOnClickListener(this);
        findViewById(R.id.other_btn_floating).setOnClickListener(this);
        findViewById(R.id.other_btn_clipboard).setOnClickListener(this);
        findViewById(R.id.other_btn_retrofit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_btn_printer:
                PrinterActivity.start(getActivity());
                break;
            case R.id.other_btn_crypto:
                CryptoActivity.start(getActivity());
                break;
            case R.id.other_btn_ftp:
                FtpActivity.start(getActivity());
                break;
            case R.id.other_btn_font:
                FontActivity.start(getActivity());
                break;
            case R.id.other_btn_opentype:
                OpenTypeListActivity.start(getActivity());
                break;
            case R.id.other_btn_floating:
                FloatingActionModeActivity.start(getActivity());
                break;
            case R.id.other_btn_clipboard:
                ClipboardActivity.start(getActivity());
                break;
            case R.id.other_btn_retrofit:
                RetrofitActivity.start(getActivity());
                break;
        }
    }
}
