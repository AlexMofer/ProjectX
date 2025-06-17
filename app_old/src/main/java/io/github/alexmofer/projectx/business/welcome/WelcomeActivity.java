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
package io.github.alexmofer.projectx.business.welcome;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.am.appcompat.app.AppCompatActivity;

import io.github.alexmofer.projectx.R;
import io.github.alexmofer.projectx.business.main.MainActivity;

/**
 * 欢迎页
 */
public class WelcomeActivity extends AppCompatActivity implements Runnable {

    private static final long DELAY_MILLIS = 2500;
    private boolean mDoNotStart = false;

    public WelcomeActivity() {
        super(R.layout.activity_welcome);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().postDelayed(this, DELAY_MILLIS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDoNotStart = true;
    }

    @Override
    public void run() {
        if (isFinishing())
            return;
        finish();
        if (mDoNotStart)
            return;
        MainActivity.start(this);
    }
}