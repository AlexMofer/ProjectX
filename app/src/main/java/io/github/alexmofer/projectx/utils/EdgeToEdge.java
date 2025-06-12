package io.github.alexmofer.projectx.utils;

import android.app.UiModeManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.util.function.Function;

import io.github.alexmofer.projectx.utils.EdgeToEdge.SystemBarStyle;

/**
 * 边到边
 * Created by Alex on 2025/6/12.
 */
public class EdgeToEdge {

    private EdgeToEdge() {
        //no instance
    }


    private interface EdgeToEdgeImpl {

        void setUp(SystemBarStyle statusBarStyle, SystemBarStyle navigationBarStyle, Window window,
                   View view, boolean statusBarIsDark, boolean navigationBarIsDark);

        void adjustLayoutInDisplayCutoutMode(Window window);
    }

    private static class EdgeToEdgeBase implements EdgeToEdgeImpl {

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

    @RequiresApi(21)
    private static class EdgeToEdgeApi21 extends EdgeToEdgeBase {

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
    private static class EdgeToEdgeApi23 extends EdgeToEdgeBase {

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
    private static class EdgeToEdgeApi26 extends EdgeToEdgeBase {

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
    private static class EdgeToEdgeApi28 extends EdgeToEdgeApi26 {

        @Override
        public void adjustLayoutInDisplayCutoutMode(Window window) {
            window.getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    @RequiresApi(29)
    private static class EdgeToEdgeApi29 extends EdgeToEdgeApi28 {

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
    private static class EdgeToEdgeApi30 extends EdgeToEdgeApi29 {

        @Override
        public void adjustLayoutInDisplayCutoutMode(Window window) {
            window.getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        }
    }

    public static class SystemBarStyle {
        protected final int lightScrim;
        protected final int darkScrim;
        protected final int nightMode;
        protected final Function<Resources, Boolean> detectDarkMode;

        public SystemBarStyle(int lightScrim, int darkScrim, int nightMode,
                              Function<Resources, Boolean> detectDarkMode) {
            this.lightScrim = lightScrim;
            this.darkScrim = darkScrim;
            this.nightMode = nightMode;
            this.detectDarkMode = detectDarkMode;
        }

        fun auto(
                @ColorInt lightScrim: Int,
                @ColorInt darkScrim: Int,
                detectDarkMode: (Resources) -> Boolean = { resources ->
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        }
        ): SystemBarStyle {
            return SystemBarStyle(
                    lightScrim = lightScrim,
                    darkScrim = darkScrim,
                    nightMode = UiModeManager.MODE_NIGHT_AUTO,
                    detectDarkMode = detectDarkMode
            )
        }

        fun dark(@ColorInt scrim: Int): SystemBarStyle {
            return SystemBarStyle(
                    lightScrim = scrim,
                    darkScrim = scrim,
                    nightMode = UiModeManager.MODE_NIGHT_YES,
                    detectDarkMode = { _ -> true }
            )
        }

        fun light(@ColorInt scrim: Int, @ColorInt darkScrim: Int): SystemBarStyle {
            return SystemBarStyle(
                    lightScrim = scrim,
                    darkScrim = darkScrim,
                    nightMode = UiModeManager.MODE_NIGHT_NO,
                    detectDarkMode = { _ -> false }
            )
        }

        public int getScrim(boolean isDark)  {
            return isDark ? darkScrim : lightScrim;
        }

        public int getScrimWithEnforcedContrast(boolean isDark)  {
            if (nightMode == UiModeManager.MODE_NIGHT_AUTO) {
                return Color.TRANSPARENT;
            }
            return isDark ? darkScrim : lightScrim;
        }
    }
}
