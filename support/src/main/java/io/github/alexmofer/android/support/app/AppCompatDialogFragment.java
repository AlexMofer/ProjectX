package io.github.alexmofer.android.support.app;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 功能拓展的AppCompatDialogFragment
 * Created by Alex on 2024/2/29.
 */
public class AppCompatDialogFragment extends androidx.appcompat.app.AppCompatDialogFragment {

    public AppCompatDialogFragment() {
    }

    public AppCompatDialogFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(this);
    }

    @Nullable
    @Override
    public AppCompatDialog getDialog() {
        return (Dialog) super.getDialog();
    }

    /**
     * 窗口内容View变化
     *
     * @param window 窗口
     */
    protected void onWindowContentViewChanged(@NonNull Window window) {
    }

    /**
     * 获取回调
     *
     * @return 回调
     */
    @Nullable
    protected <T> T getCallback(@NonNull Class<T> clazz) {
        return FragmentUtils.getCallback(this, clazz);
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    @Nullable
    public final <V extends View> V findViewById(int id) {
        final AppCompatDialog dialog = getDialog();
        return dialog == null ? null : dialog.findViewById(id);
    }

    /**
     * 通过ID查找View
     *
     * @param id  View 的资源ID
     * @param <V> View类型
     * @return 对应资源ID的View
     */
    @NonNull
    public final <V extends View> V requireViewById(int id) {
        final AppCompatDialog dialog = getDialog();
        if (dialog == null) {
            throw new IllegalArgumentException("DialogFragment does not has a Dialog");
        }
        final V view = dialog.findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this DialogFragment");
        }
        return view;
    }

    /**
     * 对话框
     */
    protected static class Dialog extends AppCompatDialog {

        private final AppCompatDialogFragment mFragment;

        public Dialog(@NonNull AppCompatDialogFragment fragment) {
            super(fragment.requireContext(), fragment.getTheme());
            mFragment = fragment;
        }

        @Override
        protected boolean isAutoDismissByCreateActivity() {
            return false;
        }

        @Override
        protected void onWindowContentViewChanged(@NonNull Window window) {
            super.onWindowContentViewChanged(window);
            mFragment.onWindowContentViewChanged(window);
        }
    }
}
