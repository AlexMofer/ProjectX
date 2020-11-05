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

import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.ContentView;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 基础Activity
 * Created by Alex on 2020/6/1.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Object mToolbar;

    public BaseActivity() {
    }

    @ContentView
    public BaseActivity(int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) mToolbar;
            toolbar.setTitle(title);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar toolbar =
                        (android.widget.Toolbar) mToolbar;
                toolbar.setTitle(title);
            }
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setSupportActionBar(@IdRes int toolbarId) {
        final View view = findViewById(toolbarId);
        if (view instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) view;
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
     * 获取Toolbar
     *
     * @param <T> androidx.appcompat.widget.Toolbar 或 android.widget.Toolbar
     * @return Toolbar
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends View> T getToolbar() {
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            return (T) mToolbar;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                return (T) mToolbar;
            }
        }
        return null;
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbar Toolbar
     */
    public final void setToolbar(View toolbar) {
        if (toolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar tb =
                    (androidx.appcompat.widget.Toolbar) toolbar;
            tb.setNavigationOnClickListener(this::onToolbarNavigationClick);
            tb.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
            mToolbar = toolbar;
            invalidateToolbarMenu();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (toolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar tb =
                        (android.widget.Toolbar) toolbar;
                tb.setNavigationOnClickListener(this::onToolbarNavigationClick);
                tb.setOnMenuItemClickListener(this::onToolbarMenuItemClick);
                mToolbar = toolbar;
                invalidateToolbarMenu();
            }
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    public final void setToolbar(@IdRes int toolbarId) {
        setToolbar(findViewById(toolbarId));
    }

    /**
     * 点击了Toolbar的返回按钮
     *
     * @param v 返回按钮
     */
    protected void onToolbarNavigationClick(View v) {
        onBackPressed();
    }

    /**
     * 点击Toolbar的菜单子项
     *
     * @param item 子项
     * @return 是否消耗掉这次点击事件
     */
    protected boolean onToolbarMenuItemClick(@NonNull MenuItem item) {
        return false;
    }

    /**
     * 刷新Toolbar菜单
     */
    protected void invalidateToolbarMenu() {
        if (mToolbar instanceof androidx.appcompat.widget.Toolbar) {
            final androidx.appcompat.widget.Toolbar toolbar =
                    (androidx.appcompat.widget.Toolbar) mToolbar;
            onUpdateToolbarMenu(toolbar.getMenu());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mToolbar instanceof android.widget.Toolbar) {
                final android.widget.Toolbar toolbar =
                        (android.widget.Toolbar) mToolbar;
                onUpdateToolbarMenu(toolbar.getMenu());
            }
        }
    }

    /**
     * 更新Toolbar菜单
     *
     * @param menu 菜单
     */
    protected void onUpdateToolbarMenu(@NonNull Menu menu) {
    }
}