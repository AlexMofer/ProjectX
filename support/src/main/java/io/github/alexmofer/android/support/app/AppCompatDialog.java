package io.github.alexmofer.android.support.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * 功能拓展的AppCompatDialog
 * Created by Alex on 2024/2/29.
 */
public class AppCompatDialog extends androidx.appcompat.app.AppCompatDialog {

    private WeakReference<AppCompatActivity> mCreateActivity;

    public AppCompatDialog(@NonNull Context context) {
        super(context);
        setCreateActivity(context);
    }

    public AppCompatDialog(@NonNull Context context, int theme) {
        super(context, theme);
        setCreateActivity(context);
    }

    protected AppCompatDialog(@NonNull Context context, boolean cancelable,
                              @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCreateActivity(context);
    }

    private void setCreateActivity(@NonNull Context context) {
        if (isAutoDismissByCreateActivity() && context instanceof AppCompatActivity) {
            final AppCompatActivity activity = (AppCompatActivity) context;
            mCreateActivity = new WeakReference<>(activity);
            activity.addDialog(this);
        }
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        final Window window = getWindow();
        if (window != null) {
            onWindowContentViewChanged(window);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        final Window window = getWindow();
        if (window != null) {
            onWindowContentViewChanged(window);
        }
    }

    @Override
    public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        final Window window = getWindow();
        if (window != null) {
            onWindowContentViewChanged(window);
        }
    }

    /**
     * 窗口内容View变化
     *
     * @param window 窗口
     */
    protected void onWindowContentViewChanged(@NonNull Window window) {
    }

    /**
     * 创建该对话框的Activity发生onStop()事件
     */
    protected void onActivityStop() {
        try {
            if (!isShowing()) {
                return;
            }
            final Window window = getWindow();
            if (window != null) {
                try {
                    window.getWindowManager().removeViewImmediate(window.getDecorView());
                } finally {
                    window.closeAllPanels();
                }
            }
            dismiss();
        } catch (Throwable t) {
            // ignore
        }
    }

    /**
     * 判断是否自动通过创建的Activity进行强制关闭
     *
     * @return 由Activity进行强制关闭时返回true，默认开启
     */
    protected boolean isAutoDismissByCreateActivity() {
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        final AppCompatActivity activity = mCreateActivity == null ? null : mCreateActivity.get();
        mCreateActivity = null;
        if (activity != null) {
            activity.removeDialog(this);
        }
    }
}
