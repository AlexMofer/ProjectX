/*
 * Copyright (C) 2018 AlexMofer
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

import android.app.Activity;
import android.os.Build;

/**
 * Activity兼容器
 */
@SuppressWarnings("unused")
public final class AMActivityCompat {

    private AMActivityCompat() {
        //no instance
    }

    /**
     * Returns true if the activity is currently in multi-window mode.
     *
     * @return True if the activity is in multi-window mode.
     * @see android.R.attr#resizeableActivity
     */
    public static boolean isInMultiWindowMode(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                activity.isInMultiWindowMode();
    }

    /**
     * Returns true if the activity is currently in picture-in-picture mode.
     *
     * @return True if the activity is in picture-in-picture mode.
     * @see android.R.attr#supportsPictureInPicture
     */
    public static boolean isInPictureInPictureMode(Activity activity) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                activity.isInPictureInPictureMode();
    }
}
