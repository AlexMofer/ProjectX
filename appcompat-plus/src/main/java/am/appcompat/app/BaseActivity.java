/*
 * Copyright (C) 2020 AlexMofer
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
package am.appcompat.app;

import android.view.View;

import androidx.annotation.ContentView;
import androidx.annotation.IdRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * 基础Activity
 * Created by Alex on 2020/6/1.
 */
@SuppressWarnings("unused")
public abstract class BaseActivity extends AppCompatActivity {

    public BaseActivity() {
    }

    @ContentView
    public BaseActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setSupportActionBar(@IdRes int toolbarId) {
        final View view = findViewById(toolbarId);
        if (view instanceof Toolbar) {
            final Toolbar toolbar = (Toolbar) view;
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(this::onToolbarNavigationClick);
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setSupportActionBar(@IdRes int toolbarId, boolean showTitle) {
        setSupportActionBar(toolbarId);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(showTitle);
        }
    }

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     */
    protected void onToolbarNavigationClick(View v) {
        onBackPressed();
    }
}