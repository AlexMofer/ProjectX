package io.github.alexmofer.android.support.utils;

import android.content.res.Configuration;

/**
 * Configuration 工具
 * Created by Alex on 2026/1/12.
 */
public final class ConfigurationUtils {

    private ConfigurationUtils() {
        //no instance
    }

    /**
     * 判断是否为深色模式
     *
     * @param configuration Configuration
     * @return 为深色模式时返回true
     */
    public static boolean isNight(Configuration configuration) {
        return (configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }
}
