/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.os.Looper;

/**
 * 线程工具
 * Created by Alex on 2024/4/17.
 */
public class ThreadUtils {

    private ThreadUtils() {
        //no instance
    }

    /**
     * Returns true if the current thread is the main thread, false otherwise.
     *
     * @return true if we are on the main thread, false otherwise.
     */
    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 检查主线程，如果是工作线程则抛出运行时异常
     */
    public static void checkMainThread() {
        if (isMainThread()) {
            return;
        }
        throw new IllegalStateException("Access In main thread.");
    }

    /**
     * 检查工作线程，如果是主线程则抛出运行时异常
     */
    public static void checkWorkerThread() {
        if (isMainThread()) {
            throw new IllegalStateException("Access In main thread.");
        }
    }
}
