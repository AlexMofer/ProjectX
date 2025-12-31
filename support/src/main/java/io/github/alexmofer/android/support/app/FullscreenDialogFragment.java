package io.github.alexmofer.android.support.app;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import io.github.alexmofer.android.support.window.EdgeToEdge;

/**
 * 全屏对话框
 * Created by Alex on 2025/11/26.
 */
public class FullscreenDialogFragment extends CenterDialogFragment {

    private boolean mFullscreen;
    private boolean mAutoAvoidIME = true;

    public FullscreenDialogFragment() {
        super();
    }

    public FullscreenDialogFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    /**
     * 分离
     *
     * @param savedInstanceState 保存的状态
     * @return 返回 true 时进入 Sheet 模式，反之进入居中对话框模式
     */
    protected boolean onBranch(@Nullable Bundle savedInstanceState) {
        return true;
    }

    /**
     * 判断是否为全屏模式
     *
     * @return 为全屏模式时返回 true
     */
    protected final boolean isFullscreen() {
        return mFullscreen;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mFullscreen = onBranch(savedInstanceState);
        return mFullscreen ? new FullscreenDialog(requireContext(), getTheme())
                : super.onCreateDialog(savedInstanceState);
    }

    @Override
    protected final void onDialogCreated(@NonNull AppCompatDialog dialog) {
        onDialogCreated(dialog, false);
    }

    /**
     * 对话框完成创建
     *
     * @param dialog     对话框
     * @param fullscreen 是否为全屏对话框模式
     */
    protected void onDialogCreated(@NonNull AppCompatDialog dialog, boolean fullscreen) {
        if (fullscreen) {
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
        } else {
            super.onDialogCreated(dialog);
        }
    }

    @Override
    protected final void onDialogContentChanged(@NonNull AppCompatDialog dialog) {
        onDialogContentChanged(dialog, false);
    }

    /**
     * 对话框内容 View 发生变化
     * 注：如果需要强制状态栏与导航栏样式，应该在此处调用
     *
     * @param dialog     对话框
     * @param fullscreen 是否为全屏对话框模式
     */
    protected void onDialogContentChanged(@NonNull AppCompatDialog dialog, boolean fullscreen) {
        super.onDialogContentChanged(dialog);
        if (fullscreen) {
            final Window window = dialog.getWindow();
            if (window != null) {
                // 默认状态栏与底部栏透明，深浅色跟随系统
                EdgeToEdge.enable(window,
                        EdgeToEdge.SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
                        EdgeToEdge.SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT));
            }
        }
    }

    @Override
    protected final int getWindowAnimations() {
        return getWindowAnimations(false);
    }

    /**
     * 获取窗口动画
     *
     * @param fullscreen 是否为全屏对话框模式
     * @return 窗口动画
     */
    @StyleRes
    protected int getWindowAnimations(boolean fullscreen) {
        if (fullscreen) {
            return android.R.style.Animation_Translucent;
        } else {
            return super.getWindowAnimations();
        }
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

    @Override
    protected final int getSoftInputMode() {
        return getSoftInputMode(false);
    }

    /**
     * 获取软键盘模式
     *
     * @param fullscreen 是否为全屏对话框模式
     * @return 软键盘模式
     */
    protected int getSoftInputMode(boolean fullscreen) {
        if (fullscreen) {
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
        return super.getSoftInputMode();
    }

    @Nullable
    @Override
    protected final Drawable getContentBackground(@NonNull Context context) {
        return getContentBackground(context, false);
    }

    /**
     * 获取内容背景
     *
     * @param context    Context
     * @param fullscreen 是否为全屏对话框模式
     * @return 内容背景
     */
    @Nullable
    protected Drawable getContentBackground(@NonNull Context context, boolean fullscreen) {
        if (fullscreen) {
            return new ColorDrawable(Color.TRANSPARENT);
        }
        return super.getContentBackground(context);
    }

    private class FullscreenDialog extends AppCompatDialog {

        public FullscreenDialog(@NonNull Context context, int theme) {
            super(context, theme);
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            final Window window = getWindow();
            if (window != null) {
                final WindowManager.LayoutParams lp = window.getAttributes();
                lp.windowAnimations = getWindowAnimations(true);
                lp.softInputMode = getSoftInputMode(true);
                window.setBackgroundDrawable(getContentBackground(context, true));
            }
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            onDialogCreated(this, true);
        }

        @Override
        public void onContentChanged() {
            super.onContentChanged();
            onDialogContentChanged(this, true);
        }
    }
}
