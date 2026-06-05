/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.transition;

import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.transition.Fade;
import androidx.transition.Slide;

/**
 * Fragment 转场
 * Created by Alex on 2026/6/5.
 */
public final class FragmentTransitions {

    private FragmentTransitions() {
        //no instance
    }

    /**
     * 配置转场
     *
     * @param fragment Fragment
     */
    public static void setTransitions(@NonNull Fragment fragment, long duration) {
        if (fragment instanceof SlideEnd) {
            fragment.setEnterTransition(new Slide(Gravity.END).setDuration(duration));
            fragment.setExitTransition(new Slide(Gravity.END).setDuration(duration));
            return;
        }
        if (fragment instanceof FadeInOut) {
            fragment.setEnterTransition(new Fade(Fade.IN).setDuration(duration));
            fragment.setExitTransition(new Fade(Fade.OUT).setDuration(duration));
        }
    }

    /**
     * 配置转场
     *
     * @param fragment Fragment
     */
    public static void setTransitions(@NonNull Fragment fragment) {
        setTransitions(fragment,
                fragment.getResources().getInteger(android.R.integer.config_shortAnimTime));
    }

    /**
     * 右侧进出
     */
    public interface SlideEnd {
    }

    /**
     * 淡入淡出
     */
    public interface FadeInOut {
    }
}
