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
import android.support.annotation.Nullable;
import android.view.View;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.widget.floatingactionmode.FloatingActionMode;
import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;

/**
 * 悬浮菜单
 */
public class FloatingActionModeActivity extends BaseActivity implements PressView.OnPressListener,
        FloatingActionMode.Callback {

    private final Rect mBound = new Rect();
    private FloatingActionMode mMode;

    public static void start(Context context) {
        context.startActivity(new Intent(context, FloatingActionModeActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_floatingactionmode;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
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
        mMode = FloatingActionMode.startFloatingActionMode(view, this);
    }

    // Callback
    @Override
    public boolean onCreateActionMode(FloatingActionMode mode, FloatingMenu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(FloatingActionMode mode, FloatingMenu menu) {
        menu.add("复制");
        menu.add("剪切");
        menu.add("粘贴");
        menu.add("选择");
        menu.add("全选");
        menu.add("分享");
        menu.add("翻译");
        menu.add("搜索");
        menu.add("颜色");
        menu.add("字体");
        menu.add("字号");
        menu.add("样式");
        menu.add("左对齐");
        menu.add("右对齐");
        menu.add("居中");
        menu.add("网页搜索");
        return false;
    }

    @Override
    public void onGetContentRect(FloatingActionMode mode, View view, Rect outRect) {
        outRect.set(mBound);
    }

    @Override
    public boolean onActionItemClicked(FloatingActionMode mode, FloatingMenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(FloatingActionMode mode) {
        mMode = null;
    }
}
