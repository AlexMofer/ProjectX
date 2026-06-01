package io.github.alexmofer.android.support.theme;

/**
 * 系统栏样式控制器
 * Created by Alex on 2026/5/27.
 */
public final class SystemBarStyleConfig {
    public final boolean isLightStatusBar;
    public final boolean isLightNavigationBar;

    public SystemBarStyleConfig(boolean isLightStatusBar, boolean isLightNavigationBar) {
        this.isLightStatusBar = isLightStatusBar;
        this.isLightNavigationBar = isLightNavigationBar;
    }

    // 状态栏/导航栏显示白字
    public static final SystemBarStyleConfig DARK = new SystemBarStyleConfig(false, false);

    // 状态栏/导航栏显示黑字
    public static final SystemBarStyleConfig LIGHT = new SystemBarStyleConfig(true, true);
}
