/*
 * Copyright (C) 2020 AlexMofer
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
package am.appcompat.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Application 持有者
 * Created by Alex on 2020/3/7.
 */
public final class ApplicationHolder implements Application.ActivityLifecycleCallbacks {

    private static ApplicationHolder mInstance;
    private final Application mApplication;
    private WeakReference<Activity> mResumedActivity;

    private ApplicationHolder(Application application) {
        mApplication = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static void create(Application application) {
        mInstance = new ApplicationHolder(application);
    }

    public static void destroy(Application application) {
        mInstance.destroy();
        mInstance = null;
    }

    /**
     * Return the application.
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public static <T extends Application> T getApplication() {
        //noinspection unchecked
        return (T) mInstance.mApplication;
    }

    public static Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    @Nullable
    public static Activity getResumedActivity() {
        return mInstance.mResumedActivity == null ? null : mInstance.mResumedActivity.get();
    }

    private void destroy() {
        mApplication.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity,
                                  @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mResumedActivity = new WeakReference<>(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (mResumedActivity != null) {
            if (activity == mResumedActivity.get())
                mResumedActivity = null;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity,
                                            @NonNull Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}