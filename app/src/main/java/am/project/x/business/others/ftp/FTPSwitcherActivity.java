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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.utils.ContextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 文件传输开关
 */
public class FTPSwitcherActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;

    @Override
    protected int getContentViewLayout() {
        return 0;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        if (FTPService.isStarted()) {
            stop();
        } else {
            start();
        }
    }

    private void stop() {
        Toast.makeText(this, R.string.ftp_switcher_toast_stop, Toast.LENGTH_SHORT).show();
        FTPService.stop(this);
        finish();
    }

    private void start() {
        Toast.makeText(this, R.string.ftp_switcher_toast_start, Toast.LENGTH_SHORT).show();
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
        if (!FTPService.isStarted()) {
            FTPService.start(this, 0);
        }
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    start();
        }
    }
}
