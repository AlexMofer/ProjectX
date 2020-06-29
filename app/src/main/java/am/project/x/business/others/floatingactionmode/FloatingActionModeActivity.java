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
package am.project.x.business.others.floatingactionmode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import am.appcompat.app.BaseActivity;
import am.project.support.compat.AMActivityCompat;
import am.project.x.R;
import am.widget.floatingactionmode.FloatingActionMode;
import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.FloatingSubMenu;

/**
 * 悬浮菜单
 */
public class FloatingActionModeActivity extends BaseActivity implements PressView.OnPressListener,
        FloatingActionMode.Callback {

    private final Rect mBound = new Rect();
    private FloatingActionMode mMode;

    public FloatingActionModeActivity() {
        super(R.layout.activity_floatingactionmode);
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, FloatingActionModeActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(R.id.floating_toolbar);
        final PressView bound = findViewById(R.id.floating_dv_bound);
        bound.setOnPressListener(this);
    }

    // Listener
    @Override
    public void onPressed(View view, int left, int top, int right, int bottom) {
        if (mMode != null)
            mMode.finish();
        mBound.set(left, top, right, bottom);
        mMode = new FloatingActionMode(view, this, R.style.FloatingActionMode);
        mMode.start();
    }

    // Callback
    @Override
    public boolean onPrepareActionMode(FloatingActionMode mode, FloatingMenu menu) {
        menu.add(R.string.floating_menu_1);
        menu.add(R.string.floating_menu_2);
        menu.add(R.string.floating_menu_3);
        menu.add(R.string.floating_menu_4);
        menu.add(R.string.floating_menu_5);
        menu.add(R.string.floating_menu_6);
        menu.add(R.string.floating_menu_7);
        menu.add(R.string.floating_menu_8);
        final View custom = View.inflate(this,
                R.layout.layout_menu_floatingactionmode, null);
        menu.add(R.string.floating_menu_9).setSubMenu(custom);
        menu.add(R.string.floating_menu_10);
        menu.add(R.string.floating_menu_11);
        menu.add(R.string.floating_menu_12);
        final FloatingSubMenu sub = menu.add(R.string.floating_menu_13).setSubMenu();
        sub.add(R.string.floating_menu_15);
        sub.add(R.string.floating_menu_16);
        sub.add(R.string.floating_menu_17);
        sub.add(R.string.floating_menu_18);
        menu.add(R.string.floating_menu_14);
        return false;
    }

    @Override
    public void onGetContentRect(FloatingActionMode mode, View view, Rect outRect) {
        outRect.set(mBound);
        mode.setInMultiWindowMode(AMActivityCompat.isInMultiWindowMode(this));
    }

    @Override
    public boolean onActionItemClicked(FloatingActionMode mode, FloatingMenuItem item) {
        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onDestroyActionMode(FloatingActionMode mode) {
        mMode = null;
    }
}
