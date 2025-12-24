package io.github.alexmofer.android.support.lifecycle;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistry;

import io.github.alexmofer.android.support.concurrent.UIThreadExecutor;

/**
 * 状态保存 ViewModel
 * 注意：仅适用于少量数据保存
 * Created by Alex on 2025/12/24.
 */
public class SavedStateViewModel extends ViewModel {

    public SavedStateViewModel() {
        // 使用该构造方法时无状态保存能力
    }

    public SavedStateViewModel(@NonNull SavedStateHandle handle) {
        // 依托 Activity 及 Fragment 的状态保存能力，因此只试用于少量数据保存
        final String key = getProviderKey();
        final Bundle savedInstanceState = handle.get(key);
        handle.setSavedStateProvider(key, new InnerSavedStateProvider());
        if (savedInstanceState != null) {
            if (restoreInConstructor()) {
                onRestoreInstanceState(savedInstanceState);
            } else {
                UIThreadExecutor.getDefault().getHandler().post(() -> {
                    onRestoreInstanceState(savedInstanceState);
                });
            }
        }
    }

    /**
     * 获取整体数据提供者 Key
     *
     * @return 整体数据提供者 Key
     */
    protected String getProviderKey() {
        return "SavedStateViewModel_Data";
    }

    /**
     * 判断恢复方法是否在构造函数中执行
     *
     * @return 在构造函数中执行时返回 true，默认为 false
     */
    protected boolean restoreInConstructor() {
        return false;
    }

    /**
     * 保存状态
     *
     * @param outState 保存的状态
     */
    protected void onSaveInstanceState(@NonNull Bundle outState) {
    }

    /**
     * 恢复状态
     *
     * @param savedInstanceState 恢复的状态
     */
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
    }

    private class InnerSavedStateProvider implements SavedStateRegistry.SavedStateProvider {

        @NonNull
        @Override
        public Bundle saveState() {
            final Bundle bundle = new Bundle();
            onSaveInstanceState(bundle);
            return bundle;
        }
    }
}
