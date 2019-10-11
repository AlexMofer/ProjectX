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

import android.os.Bundle;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.base.BaseActivity;

import androidx.annotation.Nullable;

/**
 * 文件传输开关
 */
public class FtpSwitcherActivity extends BaseActivity {

    @Override
    protected int getContentViewLayout() {
        return 0;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        final FtpServiceHelper helper = FtpServiceHelper.getInstance();
        if (helper.isStarted()) {
            Toast.makeText(this, R.string.ftp_switcher_toast_stop, Toast.LENGTH_SHORT).show();
            helper.stop(this);
            finish();
        } else {
            Toast.makeText(this, R.string.ftp_switcher_toast_start, Toast.LENGTH_SHORT).show();
            helper.start(this);
            finish();
        }
    }
}
