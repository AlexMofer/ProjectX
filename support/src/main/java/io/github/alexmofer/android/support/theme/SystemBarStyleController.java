package io.github.alexmofer.android.support.theme;

import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import io.github.alexmofer.android.support.function.FunctionRBoolean;
import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 系统栏样式控制器
 * Created by Alex on 2026/5/27.
 */
public final class SystemBarStyleController {
    private final Boolean mDefaultIsLightStatusBar;
    private final Boolean mDefaultIsLightNavigationBar;
    private final MutableLiveData<Boolean> mLightStatusBars;
    private final MutableLiveData<Boolean> mLightNavigationBars;

    public <T extends AppCompatActivity & Holder> SystemBarStyleController(@NonNull T activity,
                                                                           @Nullable Boolean isLightStatusBar,
                                                                           @Nullable Boolean isLightNavigationBar,
                                                                           @NonNull FunctionRBoolean statusBarDefaultAdapter,
                                                                           @NonNull FunctionRBoolean navigationBarDefaultAdapter) {
        mDefaultIsLightStatusBar = isLightStatusBar;
        mDefaultIsLightNavigationBar = isLightNavigationBar;
        mLightStatusBars = new MutableLiveData<>(isLightStatusBar);
        mLightNavigationBars = new MutableLiveData<>(isLightNavigationBar);
        final FragmentManager.FragmentLifecycleCallbacks callbacks =
                new FragmentLifecycleCallbacksImpl(activity);
        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                activity.getSupportFragmentManager()
                        .registerFragmentLifecycleCallbacks(callbacks, true);
                final Window window = activity.getWindow();
                final View decorView = window.getDecorView();
                final WindowInsetsControllerCompat controller =
                        WindowCompat.getInsetsController(window, decorView);
                mLightStatusBars.observe(owner, isLight -> {
                    if (isLight == null) {
                        controller.setAppearanceLightStatusBars(statusBarDefaultAdapter.execute());
                        return;
                    }
                    controller.setAppearanceLightStatusBars(isLight);
                });
                mLightNavigationBars.observe(owner, isLight -> {
                    if (isLight == null) {
                        controller.setAppearanceLightNavigationBars(navigationBarDefaultAdapter.execute());
                        return;
                    }
                    controller.setAppearanceLightNavigationBars(isLight);
                });
            }
        });
    }

    public void refreshSystemBarStyle(@NonNull FragmentManager manager) {
        // 如果存在导航栏的多fragment的存活机制，该方法就不一定准确
        final Fragment fragment = FragmentUtils.findActiveFragment(manager);
        if (fragment != null) {
            if (fragment instanceof SystemBarStyleControllable) {
                final SystemBarStyleConfig config =
                        ((SystemBarStyleControllable) fragment).getExpectedSystemBarStyle();
                if (!Objects.equals(mLightStatusBars.getValue(), config.isLightStatusBar)) {
                    mLightStatusBars.setValue(config.isLightStatusBar);
                }
                if (!Objects.equals(mLightNavigationBars.getValue(), config.isLightNavigationBar)) {
                    mLightNavigationBars.setValue(config.isLightNavigationBar);
                }
            } else {
                if (mLightStatusBars.getValue() != null) {
                    mLightStatusBars.setValue(null);
                }
                if (mLightNavigationBars.getValue() != null) {
                    mLightNavigationBars.setValue(null);
                }
            }
            return;
        }
        // 无活跃 Fragment，使用 Activity 初始值
        if (!Objects.equals(mLightStatusBars.getValue(), mDefaultIsLightStatusBar)) {
            mLightStatusBars.setValue(mDefaultIsLightStatusBar);
        }
        if (!Objects.equals(mLightNavigationBars.getValue(), mDefaultIsLightNavigationBar)) {
            mLightNavigationBars.setValue(mDefaultIsLightNavigationBar);
        }
    }

    private void handleFragmentResumed(@NonNull Fragment fragment) {
        if (!fragment.isVisible()) {
            return;
        }
        if (fragment instanceof SystemBarStyleControllable) {
            final SystemBarStyleConfig config =
                    ((SystemBarStyleControllable) fragment).getExpectedSystemBarStyle();
            if (!Objects.equals(mLightStatusBars.getValue(), config.isLightStatusBar)) {
                mLightStatusBars.setValue(config.isLightStatusBar);
            }
            if (!Objects.equals(mLightNavigationBars.getValue(), config.isLightNavigationBar)) {
                mLightNavigationBars.setValue(config.isLightNavigationBar);
            }
        } else {
            if (mLightStatusBars.getValue() != null) {
                mLightStatusBars.setValue(null);
            }
            if (mLightNavigationBars.getValue() != null) {
                mLightNavigationBars.setValue(null);
            }
        }
    }

    public interface Holder {

        /**
         * 获取系统栏样式控制器
         *
         * @return 系统栏样式控制器
         */
        @NonNull
        SystemBarStyleController getSystemBarStyleController();
    }

    private class FragmentLifecycleCallbacksImpl extends FragmentManager.FragmentLifecycleCallbacks {
        private final FragmentActivity mActivity;

        public FragmentLifecycleCallbacksImpl(FragmentActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
            super.onFragmentResumed(fm, f);
            handleFragmentResumed(f);
        }

        @Override
        public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
            super.onFragmentPaused(fm, f);
            refreshSystemBarStyle(mActivity.getSupportFragmentManager());
        }
    }
}
