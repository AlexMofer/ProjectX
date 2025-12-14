package io.github.alexmofer.android.support.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import io.github.alexmofer.android.support.window.EdgeToEdge;

/**
 * 全屏对话框
 * Created by Alex on 2025/11/26.
 */
public class FullscreenDialogFragment extends AppCompatDialogFragment {

    private boolean mAutoAvoidIME = true;

    public FullscreenDialogFragment() {
        super();
    }

    public FullscreenDialogFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 获取窗口动画
     *
     * @return 窗口动画
     */
    @StyleRes
    protected int getWindowAnimations() {
        // API 28 之后 windowAnimations 可接受应用内资源
        return android.R.style.Animation_Translucent;
    }

    /**
     * 设置自动避让输入法
     *
     * @param autoAvoidIME 是否自动避让输入法
     */
    protected void setAutoAvoidIME(boolean autoAvoidIME) {
        mAutoAvoidIME = autoAvoidIME;
    }

    /**
     * 判断是否自动避让输入法
     *
     * @return 自动避让输入法时返回 true
     */
    protected boolean isAutoAvoidIME() {
        return mAutoAvoidIME;
    }

    /**
     * 获取软键盘模式
     *
     * @return 软键盘模式
     */
    protected int getSoftInputMode() {
        if (mAutoAvoidIME) {
            // 以偏移的形式处理则内容 View 无需考虑 ime 避让。
            return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        } else {
            if (Build.VERSION.SDK_INT >= 30) {
                // ime 高度在 API 30 以后才能出现在窗口裁剪中，此时需要自行处理 ime 避让及将输入框调整到显示区域内，
                // 否则可能出现输入框被软键盘遮盖问题。
                return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            } else {
                return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new FullscreenDialog(requireContext(), getTheme());
    }

    /**
     * 对话框完成创建
     *
     * @param dialog 对话框
     */
    protected void onDialogCreated(@NonNull AppCompatDialog dialog) {
        final Window window = dialog.getWindow();
        if (window != null) {
            final WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0;
            // 设置绘制系统栏背景，否则系统栏区域绘制时会被裁切呈现黑色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 调整宽高
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * 对话框内容 View 发生变化
     * 注：如果需要强制状态栏与导航栏样式，应该在此处调用
     *
     * @param dialog 对话框
     */
    protected void onDialogContentChanged(@NonNull AppCompatDialog dialog) {
        final Window window = dialog.getWindow();
        if (window != null) {
            final WindowManager.LayoutParams lp = window.getAttributes();
            if (Build.VERSION.SDK_INT >= 30) {
                lp.setFitInsetsSides(0);// 不设置布局不会延伸到状态栏与导航栏
            }
            // 默认状态栏与底部栏透明，深浅色跟随系统
            EdgeToEdge.enable(window,
                    EdgeToEdge.SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
                    EdgeToEdge.SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT));
        }
    }

    private class FullscreenDialog extends AppCompatDialog {

        public FullscreenDialog(@NonNull Context context, int theme) {
            super(context, theme);
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            final Window window = getWindow();
            if (window != null) {
                final WindowManager.LayoutParams lp = window.getAttributes();
                lp.windowAnimations = getWindowAnimations();
                lp.softInputMode = getSoftInputMode();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            onDialogCreated(this);
        }

        @Override
        public void onContentChanged() {
            super.onContentChanged();
            onDialogContentChanged(this);
        }
    }
}
