/*
 * Copyright (C) 2019 AlexMofer
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
package am.project.x.business.others.ftp.legacy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.am.appcompat.app.Fragment;

import java.io.File;
import java.util.Locale;

import am.project.support.utils.InputMethodUtils;
import am.project.x.R;
import am.project.x.business.others.ftp.FtpFragmentCallback;
import am.project.x.utils.ContextUtils;

/**
 * 传统实现方案
 * Created by Alex on 2017/10/10.
 */

public class LegacyFragment extends Fragment implements TextWatcher,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    private static final int ACTIVITY_REQUEST_PATH = 101;
    private EditText mVPort;
    private CheckBox mVAuto;
    private Button mVPath;
    private LegacyFtpConfig mConfig;

    public static LegacyFragment newInstance() {
        return new LegacyFragment();
    }

    public LegacyFragment() {
        super(R.layout.fragment_ftp_legacy);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        mVPort = findViewById(R.id.legacy_edt_port);
        mVAuto = findViewById(R.id.legacy_cb_auto);
        mVPath = findViewById(R.id.legacy_btn_path);

        mConfig = new LegacyFtpConfig(requireContext());
        mVPort.setText(String.format(Locale.getDefault(), "%d", mConfig.getPort()));
        mVAuto.setChecked(mConfig.isAutoChangePort());
        if (Build.VERSION.SDK_INT >= 23 &&
                !ContextUtils.hasWriteExternalStoragePermission(requireContext()))
            mVPath.setText(null);
        else {
            if (mConfig.getPath() == null)
                mConfig.setPath(Environment.getExternalStorageDirectory().getAbsolutePath());
            mVPath.setText(mConfig.getPath());
        }

        mVPort.addTextChangedListener(this);
        mVAuto.setOnCheckedChangeListener(this);
        mVPath.addTextChangedListener(this);
        findViewById(R.id.legacy_btn_open).setOnClickListener(this);
        findViewById(R.id.legacy_btn_close).setOnClickListener(this);
        mVPath.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_ftp_legacy, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ftp_advanced) {
            final Activity activity = getActivity();
            if (activity instanceof FtpFragmentCallback)
                ((FtpFragmentCallback) activity).onSwitch(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE &&
                grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mVPath.setText(Environment.getExternalStorageDirectory().getAbsolutePath());
            open();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_PATH &&
                resultCode == Activity.RESULT_OK && data != null)
            mVPath.setText(LegacyPathSelectActivity.getPath(data));
    }

    // Listener
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == mVPort.getText()) {
            final int port = getPort();
            if (port <= 0 || port > 65535)
                return;
            mConfig.setPort(port);
        } else if (s == mVPath.getText()) {
            final String path = mVPath.getText().toString().trim();
            if (path.length() > 0)
                mConfig.setPath(path);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mVAuto == buttonView)
            mConfig.setAutoChangePort(isChecked);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.legacy_btn_open:
                open();
                break;
            case R.id.legacy_btn_close:
                LegacyFtpService.stop(requireContext());
                break;
            case R.id.legacy_btn_path:
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                startActivityForResult(LegacyPathSelectActivity.getStarter(requireContext(), path),
                        ACTIVITY_REQUEST_PATH);
                break;
        }
    }

    private int getPort() {
        try {
            return Integer.parseInt(mVPort.getText().toString().trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void open() {
        if (LegacyFtpService.isStarted()) {
            Toast.makeText(requireContext(), R.string.ftp_toast_running, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ContextUtils.isWifiConnected(requireContext())) {
            Toast.makeText(requireContext(), R.string.ftp_toast_no_wifi, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                !ContextUtils.hasWriteExternalStoragePermission(requireContext())) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }
        final int port = getPort();
        if (port <= 0 || port > 65535) {
            mVPort.setText(null);
            InputMethodUtils.openInputMethod(mVPort);
            Toast.makeText(requireContext(), R.string.ftp_toast_bad_port,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        final File dir = new File(mVPath.getText().toString().trim());
        if (!dir.exists() || !dir.isDirectory()) {
            Toast.makeText(requireContext(), R.string.ftp_toast_bad_path,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        LegacyFtpService.start(requireContext());
    }
}
