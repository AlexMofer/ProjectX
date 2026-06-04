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
import java.util.function.Function;

import io.github.alexmofer.android.support.concurrent.UIThreadExecutor;
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
    private final Function<FragmentActivity, Fragment> mVisibleFragmentAdapter;

    public <T extends AppCompatActivity & Holder> SystemBarStyleController(@NonNull T activity,
                                                                           @Nullable Boolean isLightStatusBar,
                                                                           @Nullable Boolean isLightNavigationBar,
                                                                           @NonNull FunctionRBoolean statusBarDefaultAdapter,
                                                                           @NonNull FunctionRBoolean navigationBarDefaultAdapter,
                                                                           @NonNull Function<FragmentActivity, Fragment> visibleFragmentAdapter) {
        mDefaultIsLightStatusBar = isLightStatusBar;
        mDefaultIsLightNavigationBar = isLightNavigationBar;
        mLightStatusBars = new MutableLiveData<>(isLightStatusBar);
        mLightNavigationBars = new MutableLiveData<>(isLightNavigationBar);
        mVisibleFragmentAdapter = visibleFragmentAdapter;
        activity.getSupportFragmentManager().registerFragmentLifecycleCallbacks(
                new FragmentLifecycleCallbacksImpl(activity), true);
        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                final Window window = activity.getWindow();
                final View decorView = window.getDecorView();
                final WindowInsetsControllerCompat controller =
                        WindowCompat.getInsetsController(window, decorView);
                decorView.addOnAttachStateChangeListener(
                        new View.OnAttachStateChangeListener() {
                            @Override
                            public void onViewAttachedToWindow(@NonNull View v) {
                                // 直接执行是无效的，
                                // 因为 Fragment 的 View 的 onViewAttachedToWindow 在该方法之后执行，
                                // 因此需要将刷新 post 到 UI 线程中执行。
                                UIThreadExecutor.getDefault().getHandler().post(
                                        () -> refreshSystemBarStyle(activity));
                            }

                            @Override
                            public void onViewDetachedFromWindow(@NonNull View v) {
                                // do nothing
                            }
                        });
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

    public <T extends AppCompatActivity & Holder> SystemBarStyleController(@NonNull T activity,
                                                                           @Nullable Boolean isLightStatusBar,
                                                                           @Nullable Boolean isLightNavigationBar,
                                                                           @NonNull FunctionRBoolean statusBarDefaultAdapter,
                                                                           @NonNull FunctionRBoolean navigationBarDefaultAdapter) {
        this(activity, isLightStatusBar, isLightNavigationBar, statusBarDefaultAdapter, navigationBarDefaultAdapter,
                FragmentUtils::findVisibleFragment);
    }

    /**
     * 刷新系统栏样式
     *
     * @param fragment Fragment
     */
    public void refreshSystemBarStyle(@NonNull Fragment fragment) {
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

    /**
     * 刷新系统栏样式
     *
     * @param activity FragmentActivity
     */
    public void refreshSystemBarStyle(@NonNull FragmentActivity activity) {
        final Fragment fragment = mVisibleFragmentAdapter.apply(activity);
        if (fragment != null) {
            refreshSystemBarStyle(fragment);
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
            // 注意：Activity 的 Window 的 DecorView 都还没有附着到窗口时，
            // Fragment 虽然已经 Resumed，但 isVisible 判断依然为 false，
            // 因为其 View 的 getWindowToken 为 null，
            // 所以需要给 Activity 的 Window 的 DecorView 添加窗口附着监听，并在其附着之后触发刷新
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
            refreshSystemBarStyle(mActivity);
        }
    }
}
