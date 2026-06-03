package io.github.alexmofer.android.support.theme;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 系统栏样式控制器
 * Created by Alex on 2026/5/27.
 */
public interface SystemBarStyleControllable {

    /**
     * 获取期望的样式
     *
     * @return 系统栏样式
     */
    @NonNull
    SystemBarStyleConfig getExpectedSystemBarStyle();

    /**
     * 刷新系统栏样式
     *
     * @param activity FragmentActivity
     * @return 是否成功刷新
     */
    static boolean refreshSystemBarStyle(@NonNull FragmentActivity activity) {
        if (activity instanceof SystemBarStyleController.Holder) {
            ((SystemBarStyleController.Holder) activity)
                    .getSystemBarStyleController()
                    .refreshSystemBarStyle(activity);
            return true;
        }
        return false;
    }

    /**
     * 刷新系统栏样式
     *
     * @param fragment Fragment
     * @return 是否成功刷新
     */
    static boolean refreshSystemBarStyle(@NonNull Fragment fragment) {
        final FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            return refreshSystemBarStyle(activity);
        }
        return false;
    }
}
