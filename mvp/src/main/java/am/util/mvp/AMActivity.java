/*
 * Copyright (C) 2015 AlexMofer
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

package am.util.mvp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toolbar;

import java.util.ArrayList;

/**
 * Activity
 * Created by Alex on 2017/10/28.
 */
@SuppressWarnings("unused")
public abstract class AMActivity extends Activity {

    private final ArrayList<Dialog> mShowDialogs = new ArrayList<>();
    private ToolbarNavigationOnClickListener mToolbarListener;
    private LocalBroadcastManager mLocalBroadcastManager;// 应用内部广播
    /**
     * 广播接受器
     */
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveLocalBroadcast(context, intent);
        }
    };
    private Dialog mLoading;
    private boolean mShowLoading = false;
    private boolean mAttachedToWindow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getContentViewLayout();
        if (layout != 0) {
            setContentView(layout);
        }
        initializeActivity(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        final IntentFilter filter = new IntentFilter();
        onAddLocalAction(filter);
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, filter);
        onRegisteredLocalBroadcastReceiver();
        mLoading = getLoadingDialog();
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onCreated(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onStarted();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onResumed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onPaused();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onStopped();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        if (!isLocalBroadcastUnregistered()) {
            mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
            mLocalBroadcastManager = null;
            onUnregisteredLocalBroadcastReceiver();
        }
        super.onDestroy();
        final AMPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onDestroyed();
            presenter.detach();
        }
    }

    /**
     * 获取页面布局
     *
     * @return 布局ID
     */
    @LayoutRes
    protected abstract int getContentViewLayout();

    /**
     * 初始化Activity
     *
     * @param savedInstanceState 保存的实例数据
     */
    protected abstract void initializeActivity(@Nullable Bundle savedInstanceState);

    /**
     * 获取基础Presenter
     *
     * @return 基础Presenter
     */
    protected AMPresenter getPresenter() {
        return null;
    }

    /**
     * 获取载入对话框
     *
     * @return 载入对话框
     */
    protected Dialog getLoadingDialog() {
        return null;
    }

    /**
     * 添加本地广播监听意图
     *
     * @param filter 意图过滤器
     */
    protected void onAddLocalAction(IntentFilter filter) {
    }

    /**
     * 接收到本地广播
     *
     * @param context 发起广播的context
     * @param intent  发起广播的意图
     */
    protected void onReceiveLocalBroadcast(Context context, Intent intent) {
    }

    /**
     * 已注册本地广播接收器
     */
    protected void onRegisteredLocalBroadcastReceiver() {
    }

    /**
     * 已取消注册本地广播接收器
     */
    protected void onUnregisteredLocalBroadcastReceiver() {
    }

    /**
     * 判断本地广播接收器是否已取消注册
     *
     * @return 是否已取消注册
     */
    public final boolean isLocalBroadcastUnregistered() {
        return mLocalBroadcastManager == null;
    }

    /**
     * 发送本地广播
     *
     * @param action 广播动作
     */
    public final void sendLocalBroadcast(String action) {
        sendLocalBroadcast(new Intent(action));
    }

    /**
     * 发送本地广播
     *
     * @param intent 广播意图
     */
    public final void sendLocalBroadcast(Intent intent) {
        if (isLocalBroadcastUnregistered()) {
            return;
        }
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    @RequiresApi(21)
    public final void setActionBar(@IdRes int toolbarId) {
        final View view = findViewById(toolbarId);
        if (view instanceof Toolbar) {
            if (mToolbarListener == null) {
                mToolbarListener = new ToolbarNavigationOnClickListener();
            }
            final Toolbar toolbar = (Toolbar) view;
            setActionBar(toolbar);
            toolbar.setNavigationOnClickListener(mToolbarListener);
        }
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    @RequiresApi(21)
    public final void setActionBar(@IdRes int toolbarId, boolean showTitle) {
        setActionBar(toolbarId);
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(showTitle);
        }
    }

    /**
     * 显示默认载入
     */
    public void showLoading() {
        if (mAttachedToWindow) {
            if (mLoading != null && !mLoading.isShowing())
                mLoading.show();
        } else {
            mShowLoading = true;
        }
    }

    /**
     * 隐藏默认载入
     */
    public void dismissLoading() {
        if (mAttachedToWindow) {
            if (mLoading != null && mLoading.isShowing())
                mLoading.dismiss();
        } else {
            mShowLoading = false;
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

    /**
     * 显示对话框
     *
     * @param dialog Dialog
     */
    public void showDialog(Dialog dialog) {
        if (mAttachedToWindow) {
            dialog.show();
        } else {
            if (!mShowDialogs.contains(dialog))
                mShowDialogs.add(dialog);
        }
    }

    /**
     * 关闭对话框
     *
     * @param dialog Dialog
     */
    public void dismissDialog(Dialog dialog) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        mShowDialogs.remove(dialog);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        if (mLoading != null && mShowLoading)
            mLoading.show();
        for (Dialog dialog : mShowDialogs) {
            dialog.show();
        }
        mShowDialogs.clear();
    }

    @Override
    public void onDetachedFromWindow() {
        mAttachedToWindow = false;
        if (mLoading != null && mLoading.isShowing())
            mLoading.dismiss();
        super.onDetachedFromWindow();
    }

    /**
     * 是否附着到窗口层
     *
     * @return 是否
     */
    public boolean isAttachedToWindow() {
        return mAttachedToWindow;
    }

    private class ToolbarNavigationOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onToolbarNavigationClick(v);
        }
    }
}