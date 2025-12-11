/*
 * Copyright (C) 2025 AlexMofer
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
package io.github.alexmofer.android.support.window;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/**
 * 边到边
 * Created by Alex on 2025/6/12.
 */
public class EdgeToEdge {
    // The light scrim color used in the platform API 29+
    // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/com/android/internal/policy/DecorView.java;drc=6ef0f022c333385dba2c294e35b8de544455bf19;l=142
    public static final int DefaultLightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF);

    // The dark scrim color used in the platform.
    // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/res/color/system_bar_background_semi_transparent.xml
    // https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/remote_color_resources_res/values/colors.xml;l=67
    public static final int DefaultDarkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b);
    private static Compat Impl = null;

    private EdgeToEdge() {
        //no instance
    }

    public static void enable(SystemBarStyle statusBarStyle,
                              SystemBarStyle navigationBarStyle,
                              Window window,
                              View view,
                              boolean statusBarIsDark,
                              boolean navigationBarIsDark) {
        if (Impl == null) {
            if (Build.VERSION.SDK_INT >= 30) {
                Impl = new Api30Impl();
            } else if (Build.VERSION.SDK_INT >= 29) {
                Impl = new Api29Impl();
            } else if (Build.VERSION.SDK_INT >= 28) {
                Impl = new Api28Impl();
            } else if (Build.VERSION.SDK_INT >= 26) {
                Impl = new Api26Impl();
            } else if (Build.VERSION.SDK_INT >= 23) {
                Impl = new Api23Impl();
            } else {
                Impl = new Api21Impl();
            }
        }
        Impl.setUp(statusBarStyle, navigationBarStyle, window, view, statusBarIsDark, navigationBarIsDark);
        Impl.adjustLayoutInDisplayCutoutMode(window);
    }

    public static void enable(Activity activity, SystemBarStyle statusBarStyle,
                              SystemBarStyle navigationBarStyle) {
        final Window window = activity.getWindow();
        final View view = window.getDecorView();
        final boolean statusBarIsDark = Boolean.TRUE.equals(
                statusBarStyle.detectDarkMode.apply(view.getResources()));
        final boolean navigationBarIsDark = Boolean.TRUE.equals(
                navigationBarStyle.detectDarkMode.apply(view.getResources()));
        enable(statusBarStyle, navigationBarStyle, window, view, statusBarIsDark,
                navigationBarIsDark);
    }

    public static void enable(Activity activity) {
        enable(activity, SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.auto(DefaultLightScrim, DefaultDarkScrim));
    }

    public static void enable(Window window, SystemBarStyle statusBarStyle,
                              SystemBarStyle navigationBarStyle) {
        final View view = window.getDecorView();
        final boolean statusBarIsDark = Boolean.TRUE.equals(
                statusBarStyle.detectDarkMode.apply(view.getResources()));
        final boolean navigationBarIsDark = Boolean.TRUE.equals(
                navigationBarStyle.detectDarkMode.apply(view.getResources()));
        enable(statusBarStyle, navigationBarStyle, window, view, statusBarIsDark,
                navigationBarIsDark);
    }

    public static void enable(Window window) {
        enable(window, SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
                SystemBarStyle.auto(DefaultLightScrim, DefaultDarkScrim));
    }

    private interface Compat {

        void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle, Window window,
                   View view, boolean statusBarIsDark, boolean navigationBarIsDark);

        void adjustLayoutInDisplayCutoutMode(Window window);
    }

    private static class BaseImpl implements Compat {

        @Override
        public void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle,
                          Window window, View view, boolean statusBarIsDark,
                          boolean navigationBarIsDark) {
            // No edge-to-edge before SDK 21.
        }

        @Override
        public void adjustLayoutInDisplayCutoutMode(Window window) {
            // No display cutout before SDK 28.
        }
    }

    private static class Api21Impl extends BaseImpl {

        @Override
        public void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle,
                          Window window, View view, boolean statusBarIsDark,
                          boolean navigationBarIsDark) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @RequiresApi(23)
    private static class Api23Impl extends Api21Impl {

        @Override
        public void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle,
                          Window window, View view, boolean statusBarIsDark,
                          boolean navigationBarIsDark) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(statusBarStyle.getScrim(statusBarIsDark));
            window.setNavigationBarColor(navigationBarStyle.darkScrim);
            new WindowInsetsControllerCompat(window, view).setAppearanceLightStatusBars(!statusBarIsDark);
        }
    }

    @RequiresApi(26)
    private static class Api26Impl extends Api23Impl {

        @Override
        public void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle,
                          Window window, View view, boolean statusBarIsDark,
                          boolean navigationBarIsDark) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(statusBarStyle.getScrim(statusBarIsDark));
            window.setNavigationBarColor(navigationBarStyle.getScrim(navigationBarIsDark));
            final WindowInsetsControllerCompat controller =
                    new WindowInsetsControllerCompat(window, view);
            controller.setAppearanceLightStatusBars(!statusBarIsDark);
            controller.setAppearanceLightNavigationBars(!navigationBarIsDark);
        }
    }

    @RequiresApi(28)
    private static class Api28Impl extends Api26Impl {

        @Override
        public void adjustLayoutInDisplayCutoutMode(Window window) {
            window.getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @RequiresApi(29)
    private static class Api29Impl extends Api28Impl {

        @Override
        public void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle,
                          Window window, View view, boolean statusBarIsDark,
                          boolean navigationBarIsDark) {
            WindowCompat.setDecorFitsSystemWindows(window, false);
            window.setStatusBarColor(statusBarStyle.getScrimWithEnforcedContrast(statusBarIsDark));
            window.setNavigationBarColor(navigationBarStyle.getScrimWithEnforcedContrast(navigationBarIsDark));
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(navigationBarStyle.nightMode == UiModeManager.MODE_NIGHT_AUTO);
            final WindowInsetsControllerCompat controller =
                    new WindowInsetsControllerCompat(window, view);
            controller.setAppearanceLightStatusBars(!statusBarIsDark);
            controller.setAppearanceLightNavigationBars(!navigationBarIsDark);
        }
    }

    @RequiresApi(30)
    private static class Api30Impl extends Api29Impl {

        @Override
        public void adjustLayoutInDisplayCutoutMode(Window window) {
            window.getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }
    }

    // API24 以后可使用 Function<Resources, Boolean> 替代
    public interface DarkModeDetector {
        boolean apply(Resources t);
    }

    public static class SystemBarStyle {
        protected final int lightScrim;
        protected final int darkScrim;
        protected final int nightMode;
        protected final DarkModeDetector detectDarkMode;

        public SystemBarStyle(int lightScrim, int darkScrim, int nightMode,
                              DarkModeDetector detectDarkMode) {
            this.lightScrim = lightScrim;
            this.darkScrim = darkScrim;
            this.nightMode = nightMode;
            this.detectDarkMode = detectDarkMode;
        }

        public static SystemBarStyle auto(@ColorInt int lightScrim, @ColorInt int darkScrim) {
            return new SystemBarStyle(lightScrim, darkScrim, UiModeManager.MODE_NIGHT_AUTO,
                    resources -> (resources.getConfiguration().uiMode
                            & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        }

        public static SystemBarStyle dark(@ColorInt int scrim) {
            return new SystemBarStyle(scrim, scrim, UiModeManager.MODE_NIGHT_YES,
                    resources -> true);
        }

        public static SystemBarStyle light(@ColorInt int scrim, @ColorInt int darkScrim) {
            return new SystemBarStyle(scrim, darkScrim, UiModeManager.MODE_NIGHT_NO,
                    resources -> false);
        }

        public int getScrim(boolean isDark) {
            return isDark ? darkScrim : lightScrim;
        }

        public int getScrimWithEnforcedContrast(boolean isDark) {
            if (nightMode == UiModeManager.MODE_NIGHT_AUTO) {
                return Color.TRANSPARENT;
            }
            return isDark ? darkScrim : lightScrim;
        }
    }
}
