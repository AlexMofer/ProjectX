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
package io.github.alexmofer.projectx.business.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.am.appcompat.app.AppCompatActivity;

import io.github.alexmofer.projectx.BuildConfig;
import io.github.alexmofer.projectx.R;
import io.github.alexmofer.projectx.business.browser.BrowserActivity;
import io.github.alexmofer.projectx.utils.ViewUtils;

/**
 * 关于
 */
public class AboutActivity extends AppCompatActivity {

    public AboutActivity() {
        super(R.layout.activity_about);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.setLayoutFullscreen(getWindow().getDecorView(), false);
        setSupportActionBar(R.id.about_toolbar);
        ((TextView) findViewById(R.id.about_tv_version)).setText(
                getString(R.string.about_version, BuildConfig.VERSION_NAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_about, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about_privacy) {
            BrowserActivity.start(this, "file:///android_asset/html/privacy.html");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
