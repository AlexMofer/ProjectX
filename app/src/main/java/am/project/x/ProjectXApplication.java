package am.project.x;

import android.app.Application;

/**
 * 应用Application
 * 做一些全局变量存储
 * Created by Alex on 2016/5/11.
 */
public class ProjectXApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ProjectXExceptionHandler());
    }
}
