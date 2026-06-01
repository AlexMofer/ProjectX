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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.List;
import java.util.UUID;

import io.github.alexmofer.android.support.app.ApplicationHolder;

/**
 * Fragment 工具
 * Created by Alex on 2024/3/8.
 */
public class FragmentUtils {

    private static final String KEY_ID = "io.github.alexmofer.android.support.key.ID";

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

    /**
     * 存入数据
     *
     * @param args Fragment 参数
     * @param data 数据
     */
    public static void putData(Bundle args, Object data) {
        final String id = UUID.randomUUID().toString();
        ApplicationHolder.putData(id, data);
        args.putString(KEY_ID, id);
    }

    /**
     * 获取数据
     *
     * @param fragment Fragment
     * @return 数据，注意类型转换问题
     */
    @Nullable
    public static <T> T getData(Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return null;
        }
        return ApplicationHolder.getData(args.getString(KEY_ID));
    }

    /**
     * 移除数据
     *
     * @param fragment Fragment
     */
    public static void removeData(Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return;
        }
        ApplicationHolder.removeData(args.getString(KEY_ID));
    }

    @Nullable
    private static Fragment findFragmentInChildren(@NonNull Fragment parent, @NonNull String tag) {
        // 检查当前 Fragment 的子 Fragment
        final Fragment child = parent.getChildFragmentManager().findFragmentByTag(tag);
        if (child != null) {
            return child;
        }

        // 递归检查子 Fragment 的子 Fragment
        final List<Fragment> children = parent.getChildFragmentManager().getFragments();
        for (Fragment f : children) {
            if (f != null && f.isAdded()) {
                Fragment found = findFragmentInChildren(f, tag);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * 通过 Tag 查找 Fragment
     *
     * @param activity FragmentActivity
     * @param tag      Tag
     * @return Fragment
     */
    @Nullable
    public static Fragment findFragment(@NonNull FragmentActivity activity,
                                        @NonNull String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }

        // 首先尝试从 Activity 的 FragmentManager 中查找
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment != null) {
            return fragment;
        }

        // 如果未找到，递归遍历所有已添加的 Fragment 及其子 Fragment
        final List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null && f.isAdded()) {
                Fragment found = findFragmentInChildren(f, tag);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * 查找当前处于活跃状态的 Fragment
     *
     * @param fragmentManager FragmentManager
     * @return 当前处于活跃状态的 Fragment
     */
    @Nullable
    public static Fragment findActiveFragment(@NonNull FragmentManager fragmentManager) {
        final List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments.isEmpty()) return null;
        // 倒序遍历，因为新添加/压入栈顶的 Fragment 通常在列表末尾
        for (int i = fragments.size() - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            // 必须同时满足 added、visible 且未被隐藏
            if (fragment != null && fragment.isAdded() && fragment.isVisible() && !fragment.isHidden()) {
                // 递归查找该 Fragment 的子组件（Child Fragment）
                final FragmentManager childFragmentManager = fragment.getChildFragmentManager();
                final Fragment childFragment = findActiveFragment(childFragmentManager);
                // 如果子 Fragment 存在，则返回子 Fragment，否则返回当前 Fragment
                return childFragment != null ? childFragment : fragment;
            }
        }
        return null;
    }
}
