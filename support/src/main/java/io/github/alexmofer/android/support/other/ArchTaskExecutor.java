/*
 * Copyright (C) 2017 The Android Open Source Project
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

package io.github.alexmofer.android.support.other;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A static class that serves as a central point to execute common tasks.
 */
public class ArchTaskExecutor {
    private static volatile ArchTaskExecutor sInstance;
    private final Object mLock = new Object();
    @Nullable
    private volatile Handler mMainHandler;

    /**
     * Returns an instance of the task executor.
     *
     * @return The singleton ArchTaskExecutor.
     */
    @NonNull
    public static ArchTaskExecutor getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        synchronized (ArchTaskExecutor.class) {
            if (sInstance == null) {
                sInstance = new ArchTaskExecutor();
            }
        }
        return sInstance;
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @NonNull
    private static Handler createAsync(@NonNull Looper looper) {
        if (Build.VERSION.SDK_INT >= 28) {
            return Handler.createAsync(looper);
        } else {
            try {
                // This constructor was added as private in JB MR1:
                // https://android.googlesource.com/platform/frameworks/base/+/refs/heads/jb-mr1-release/core/java/android/os/Handler.java
                return Handler.class.getDeclaredConstructor(Looper.class, Handler.Callback.class,
                                boolean.class)
                        .newInstance(looper, null, true);
            } catch (Throwable t) {
                return new Handler(looper);
            }
        }
    }

    /**
     * Posts the given task to the main thread.
     *
     * @param runnable The runnable to run on the main thread.
     */
    public void postToMainThread(@NonNull Runnable runnable) {
        if (mMainHandler == null) {
            synchronized (mLock) {
                if (mMainHandler == null) {
                    mMainHandler = createAsync(Looper.getMainLooper());
                }
            }
        }
        //noinspection ConstantConditions
        mMainHandler.post(runnable);
    }
}
