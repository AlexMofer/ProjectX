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
package am.project.x.business.others.ftp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import am.project.support.utils.InputMethodUtils;
import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.utils.ContextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 文件传输
 */
public class FTPActivity extends BaseActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private EditText mVPort;

    public static Intent getStarter(Context context) {
        return new Intent(context, FTPActivity.class);
    }

    public static void start(Context context) {
        context.startActivity(getStarter(context));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_ftp;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.ftp_toolbar);
        mVPort = findViewById(R.id.ftp_edt_port);
        findViewById(R.id.ftp_btn_open).setOnClickListener(this);
        findViewById(R.id.ftp_btn_close).setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    open();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ftp_btn_open:
                open();
                break;
            case R.id.ftp_btn_close:
                FTPService.stop(this);
                break;
        }
    }

    private void open() {
        if (FTPService.isStarted()) {
            Toast.makeText(this, R.string.ftp_toast_running, Toast.LENGTH_SHORT).show();
            return;
        }
        int port = 0;
        final String input = mVPort.getText().toString().trim();
        if (!TextUtils.isEmpty(input) && TextUtils.isDigitsOnly(input)) {
            try {
                port = Integer.parseInt(input);
            } catch (Exception e) {
                // ignore
            }
            if (port <= 0 || port > 65535) {
                mVPort.setText(null);
                InputMethodUtils.openInputMethod(mVPort);
                Toast.makeText(this, R.string.ftp_toast_bad_port,
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!ContextUtils.isWifiConnected(this)) {
            Toast.makeText(this, R.string.ftp_toast_no_wifi, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                !ContextUtils.hasWriteExternalStoragePermission(this)) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }
        InputMethodUtils.closeInputMethod(mVPort);
        FTPService.start(this, port);
    }
}
