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

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Fragment 工具
 * Created by Alex on 2024/3/8.
 */
public class FragmentUtils {

    private FragmentUtils() {
        //no instance
    }

    /**
     * 获取回调 (检查 Fragment 的 View 的上级 View 是否为回调)
     *
     * @return 回调
     */
    @Nullable
    public static <T> T getCallback(View view, @NonNull Class<T> clazz) {
        if (view == null) {
            return null;
        }
        ViewParent vp = view.getParent();
        while (vp != null) {
            if (clazz.isInstance(vp)) {
                //noinspection unchecked
                return (T) vp;
            }
            if (vp instanceof View) {
                vp = ((View) vp).getParent();
            } else {
                vp = null;
            }
        }
        return null;
    }

    /**
     * 获取回调
     * 1. 检查 Fragment 的 View 的父 View 是否为回调
     * 2. 检查 Fragment 的 父 Fragment 是否为回调
     * 3. 检查 Fragment 的 Activity 是否为回调
     * 4. 检查 Fragment 的 View 的上级 View 是否为回调
     *
     * @return 回调
     */
    @Nullable
    public static <T> T getCallback(Fragment fragment, @NonNull Class<T> clazz) {
        final View view = fragment.getView();
        if (view != null) {
            final ViewParent vp = view.getParent();
            if (clazz.isInstance(vp)) {
                //noinspection unchecked
                return (T) vp;
            }
        }
        final Fragment parent = fragment.getParentFragment();
        if (clazz.isInstance(parent)) {
            //noinspection unchecked
            return (T) parent;
        }
        final FragmentActivity activity = fragment.getActivity();
        if (clazz.isInstance(activity)) {
            //noinspection unchecked
            return (T) activity;
        }
        return getCallback(view, clazz);
    }
}
