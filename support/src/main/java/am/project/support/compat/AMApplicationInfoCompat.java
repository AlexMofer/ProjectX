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
import android.content.pm.ApplicationInfo;

/**
 * Context版本兼容器
 * Created by Alex on 2016/11/22.
 */
@SuppressWarnings("unused")
public final class AMApplicationInfoCompat {

    private static final AMApplicationInfoCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 11) {
            IMPL = new AMApplicationInfoCompatHC();
        } else {
            IMPL = new AMApplicationInfoCompatBase();
        }
    }

    private AMApplicationInfoCompat() {
        //no instance
    }

    /**
     * Value for {@link ApplicationInfo#flags}: true when the application has requested a
     * large heap for its processes.  Corresponds to
     * android.R.styleable#AndroidManifestApplication_largeHeap
     * android:largeHeap.
     *
     * @param info ApplicationInfo
     * @return true when the application has requested a large heap for its processes.
     */
    public static boolean isLargeHeap(ApplicationInfo info) {
        return IMPL.isLargeHeap(info);
    }

    private interface AMApplicationInfoCompatImpl {
        boolean isLargeHeap(ApplicationInfo info);
    }

    private static class AMApplicationInfoCompatBase implements AMApplicationInfoCompatImpl {
        @Override
        public boolean isLargeHeap(ApplicationInfo info) {
            return false;
        }
    }

    @TargetApi(11)
    private static class AMApplicationInfoCompatHC extends AMApplicationInfoCompatBase {
        @Override
        public boolean isLargeHeap(ApplicationInfo info) {
            return (info.flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
        }
    }
}
