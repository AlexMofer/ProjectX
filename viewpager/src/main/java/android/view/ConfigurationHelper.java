package android.view;

import android.content.res.Configuration;

/**
 * Configuration辅助器
 * Created by Alex on 2017/11/14.
 */

public class ConfigurationHelper {

    private ConfigurationHelper() {
    }

    public static void onConfigurationChanged(View view, Configuration newConfig) {
        view.onConfigurationChanged(newConfig);
    }
}
