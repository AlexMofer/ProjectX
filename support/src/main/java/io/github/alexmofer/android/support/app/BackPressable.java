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
package io.github.alexmofer.android.support.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 可返回的
 * Created by Alex on 2026/2/10.
 */
public interface BackPressable {
    /**
     * 分发返回事件
     */
    void dispatchBackPressed();

    /**
     * 分发返回事件
     *
     * @param fragment Fragment
     * @return 分发成功时返回 true
     */
    @SuppressWarnings("UnusedReturnValue")
    static boolean dispatchBackPressed(@NonNull Fragment fragment) {
        if (fragment instanceof BackPressable) {
            ((BackPressable) fragment).dispatchBackPressed();
            return true;
        }
        final Fragment parent = fragment.getParentFragment();
        if (parent instanceof BackPressable) {
            ((BackPressable) parent).dispatchBackPressed();
            return true;
        }
        final FragmentActivity activity = fragment.getActivity();
        if (activity instanceof BackPressable) {
            ((BackPressable) activity).dispatchBackPressed();
            return true;
        }
        return false;
    }
}
