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

package am.project.support.compat;

import android.annotation.TargetApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Window 版本兼容器
 * Created by Alex on 2016/11/22.
 */
@SuppressWarnings("unused")
public final class AMWindowCompat {
    private static final AMWindowCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new AMWindowCompatLollipop();
        } else if (version >= 19) {
            IMPL = new AMWindowCompatKitKat();
        } else {
            IMPL = new AMWindowCompatBase();
        }
    }

    private AMWindowCompat() {
        //no instance
    }

    /**
     * 设置透明的状态栏
     *
     * @param window Window
     */
    public static void setTranslucentStatus(Window window) {
        IMPL.setTranslucentStatus(window);
    }

    /**
     * 设置透明的导航栏
     *
     * @param window Window
     */
    public static void setTranslucentNavigation(Window window) {
        IMPL.setTranslucentNavigation(window);
    }

    /**
     * 全屏
     *
     * @param window Window
     */
    public static void setLayoutFullscreen(Window window) {
        IMPL.setLayoutFullscreen(window);
    }

    /**
     * Sets the color of the status bar to {@code color}.
     * 设置状态栏颜色
     * For this to take effect,
     * the window must be drawing the system bar backgrounds with
     * {@link android.view.WindowManager.LayoutParams#FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS} and
     * {@link android.view.WindowManager.LayoutParams#FLAG_TRANSLUCENT_STATUS} must not be set.
     * <p>
     * If {@code color} is not opaque, consider setting
     * {@link android.view.View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
     * {@link android.view.View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN}.
     * <p>
     * The transitionName for the view background will be "android:status:background".
     * </p>
     *
     * @param window Window
     * @param color  颜色
     */
    public static void setStatusBarColor(Window window, int color) {
        IMPL.setStatusBarColor(window, color);
    }

    private interface AMWindowCompatImpl {
        void setTranslucentStatus(Window window);

        void setTranslucentNavigation(Window window);

        void setLayoutFullscreen(Window window);

        void setStatusBarColor(Window window, int color);
    }

    private static class AMWindowCompatBase implements AMWindowCompatImpl {

        @Override
        public void setTranslucentStatus(Window window) {
            // do nothing until api 19
        }

        @Override
        public void setTranslucentNavigation(Window window) {
            // do nothing until api 19
        }

        @Override
        public void setLayoutFullscreen(Window window) {
            // do nothing until api 19
        }

        @Override
        public void setStatusBarColor(Window window, int color) {
            // do nothing until api 21
        }
    }

    @TargetApi(19)
    private static class AMWindowCompatKitKat extends AMWindowCompatBase {

        @Override
        public void setTranslucentStatus(Window window) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        @Override
        public void setTranslucentNavigation(Window window) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        @Override
        public void setLayoutFullscreen(Window window) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @TargetApi(21)
    private static class AMWindowCompatLollipop extends AMWindowCompatKitKat {

        @Override
        public void setStatusBarColor(Window window, int color) {
            window.setStatusBarColor(color);
        }
    }
}
