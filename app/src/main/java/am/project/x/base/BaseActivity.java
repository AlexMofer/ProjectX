package am.project.x.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Toolbar;

import java.util.ArrayList;

/**
 * Activity
 * Created by Alex on 2017/3/13.
 */
@SuppressWarnings("unused")
public abstract class BaseActivity extends Activity {

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
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onCreate(savedInstanceState);
        }
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
    protected BasePresenter getPresenter() {
        return null;
    }

    /**
     * 获取载入对话框
     *
     * @return 载入对话框
     */
    protected Dialog getLoadingDialog() {
        return null;// TODO 增加默认载入对话框，存储载入对话框是否已显示
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
     * 判断权限是否被授予
     *
     * @param permission 权限
     * @return 是否被授予
     */
    public final boolean isPermissionGranted(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    @RequiresApi(21)
    @SuppressWarnings("unused")
    public final void setActionBar(@IdRes int toolbarId) {
        setActionBar(toolbarId, false);
    }

    /**
     * 设置 Toolbar
     *
     * @param toolbarId Toolbar资源ID
     */
    @RequiresApi(21)
    public final void setActionBar(@IdRes int toolbarId, boolean showTitle) {
        setActionBar((Toolbar) findViewById(toolbarId));
        if (null != getActionBar()) {
            getActionBar().setDisplayShowTitleEnabled(showTitle);
        }
    }

    @Override
    @RequiresApi(21)
    public void setActionBar(@Nullable Toolbar toolbar) {
        super.setActionBar(toolbar);
        if (null == toolbar)
            return;
        if (null == mToolbarListener) {
            mToolbarListener = new ToolbarNavigationOnClickListener();
        }
        toolbar.setNavigationOnClickListener(mToolbarListener);
    }

    /**
     * 显示默认载入
     */
    protected void showLoading() {
        if (mAttachedToWindow) {
            if (!mLoading.isShowing())
                mLoading.show();
        } else {
            mShowLoading = true;
        }
    }

    /**
     * 隐藏默认载入
     */
    protected void dismissLoading() {
        if (mAttachedToWindow) {
            if (mLoading.isShowing())
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
    @SuppressWarnings("unused")
    protected void onToolbarNavigationClick(View v) {
        onBackPressed();
    }

    /**
     * 显示对话框
     *
     * @param dialog Dialog
     */
    protected void showDialog(Dialog dialog) {
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
    protected void dismissDialog(Dialog dialog) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        mShowDialogs.remove(dialog);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        if (mShowLoading)
            mLoading.show();
        for (Dialog dialog : mShowDialogs) {
            dialog.show();
        }
        mShowDialogs.clear();
    }

    @Override
    public void onDetachedFromWindow() {
        mAttachedToWindow = false;
        if (mLoading.isShowing())
            mLoading.dismiss();
        super.onDetachedFromWindow();
    }

    /**
     * 是否附着到窗口层
     *
     * @return 是否
     */
    protected boolean isAttachedToWindow() {
        return mAttachedToWindow;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onRestart();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onStop();
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
        final BasePresenter presenter = getPresenter();
        if (null != presenter) {
            presenter.onDestroy();
        }
    }

    @Override
    public void finish() {
        if (!isLocalBroadcastUnregistered()) {
            mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
            mLocalBroadcastManager = null;
            onUnregisteredLocalBroadcastReceiver();
        }
        super.finish();
    }

    private class ToolbarNavigationOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onToolbarNavigationClick(v);
        }
    }
}