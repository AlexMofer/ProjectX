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
import android.app.ActivityManager;

/**
 * ActivityManager 版本兼容器
 * Created by Alex on 2016/11/22.
 */
@SuppressWarnings("unused")
public final class AMActivityManagerCompat {

    private static final AMActivityManagerCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 11) {
            IMPL = new AMActivityManagerCompatHC();
        } else {
            IMPL = new AMActivityManagerCompatBase();
        }
    }

    private AMActivityManagerCompat() {
        //no instance
    }

    /**
     * Return the approximate per-application memory class of the current
     * device when an application is running with a large heap.  This is the
     * space available for memory-intensive applications; most applications
     * should not need this amount of memory, and should instead stay with the
     * {@link ActivityManager#getMemoryClass()} limit.  The returned value is in megabytes.
     * This may be the same size as {@link ActivityManager#getMemoryClass()} on memory
     * constrained devices, or it may be significantly larger on devices with
     * a large amount of available RAM.
     * <p>
     * <p>The is the size of the application's Dalvik heap if it has
     * specified <code>android:largeHeap="true"</code> in its manifest.
     *
     * @param am ActivityManager
     * @return size of available RAM
     */
    public static int getLargeMemoryClass(ActivityManager am) {
        return IMPL.getLargeMemoryClass(am);
    }

    private interface AMActivityManagerCompatImpl {
        int getLargeMemoryClass(ActivityManager am);
    }

    private static class AMActivityManagerCompatBase implements AMActivityManagerCompatImpl {
        @Override
        public int getLargeMemoryClass(ActivityManager am) {
            return 0;
        }
    }

    @TargetApi(11)
    private static class AMActivityManagerCompatHC extends AMActivityManagerCompatBase {
        @Override
        public int getLargeMemoryClass(ActivityManager am) {
            return am.getLargeMemoryClass();
        }
    }
}
