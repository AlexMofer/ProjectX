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

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.github.alexmofer.android.support.app.ApplicationHolder;
import io.github.alexmofer.android.support.app.NonRepeatable;

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
    public static <T> T getCallback(@Nullable View view, @NonNull Class<T> clazz) {
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
    public static <T> T getCallback(@NonNull Fragment fragment, @NonNull Class<T> clazz) {
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
    public static void putData(@NonNull Bundle args, Object data) {
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
    public static <T> T getData(@NonNull Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return null;
        }
        return ApplicationHolder.getData(Objects.requireNonNull(args.getString(KEY_ID)));
    }

    /**
     * 移除数据
     *
     * @param fragment Fragment
     */
    public static void removeData(@NonNull Fragment fragment) {
        final Bundle args = fragment.getArguments();
        if (args == null || !args.containsKey(KEY_ID)) {
            return;
        }
        ApplicationHolder.removeData(Objects.requireNonNull(args.getString(KEY_ID)));
    }

    /**
     * 尝试移除自身
     *
     * @param fragment Fragment
     * @return 移除成功时返回 true
     */
    public static boolean tryRemoveSelf(@NonNull Fragment fragment) {
        try {
            fragment.getParentFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    private static void add(@NonNull FragmentManager manager,
                            @IdRes int containerId,
                            @NonNull Class<? extends Fragment> clazz,
                            @Nullable Bundle args) {
        final String tag = clazz.getName();
        if (NonRepeatable.class.isAssignableFrom(clazz)) {
            // 不可重复
            final Fragment find = manager.findFragmentByTag(tag);
            if (clazz.isInstance(find)) {
                if (args != null) {
                    ((NonRepeatable) find).onNewArguments(args);
                }
                return;
            }
        }
        manager.beginTransaction()
                .add(containerId, clazz, args, tag)
                .commit();
    }

    /**
     * 添加
     *
     * @param activity FragmentActivity
     * @param clazz    Fragment 类名
     * @param args     参数
     * @return 添加成功时返回 true
     */
    public static boolean add(@NonNull FragmentActivity activity,
                              @IdRes int containerId,
                              @NonNull Class<? extends Fragment> clazz,
                              @Nullable Bundle args) {
        if (activity.findViewById(containerId) == null) {
            // 未找到容器
            return false;
        }
        add(activity.getSupportFragmentManager(), containerId, clazz, args);
        return true;
    }

    /**
     * 添加
     *
     * @param fragment Fragment
     * @param clazz    Fragment 类名
     * @param args     参数
     * @return 添加成功时返回 true
     */
    public static boolean add(@NonNull Fragment fragment,
                              @IdRes int containerId,
                              @NonNull Class<? extends Fragment> clazz,
                              @Nullable Bundle args) {
        final View view = fragment.getView();
        if (view == null) {
            return false;
        }
        if (view.findViewById(containerId) == null) {
            // 未找到容器
            return false;
        }
        add(fragment.getChildFragmentManager(), containerId, clazz, args);
        return true;
    }

    /**
     * 添加
     * 注意：仅推荐无 View 的 Fragment 使用
     * @param manager FragmentManager
     * @param clazz   Fragment 类名
     * @param args    参数
     */
    public static void add(@NonNull FragmentManager manager,
                           @NonNull Class<? extends Fragment> clazz,
                           @Nullable Bundle args) {
        final String tag = clazz.getName();
        if (NonRepeatable.class.isAssignableFrom(clazz)) {
            // 不可重复
            final Fragment find = manager.findFragmentByTag(tag);
            if (clazz.isInstance(find)) {
                if (args != null) {
                    ((NonRepeatable) find).onNewArguments(args);
                }
                return;
            }
        }
        manager.beginTransaction()
                .add(clazz, args, tag)
                .commit();
    }

    private static void replace(@NonNull FragmentManager manager,
                                @IdRes int containerId,
                                @NonNull Class<? extends Fragment> clazz,
                                @Nullable Bundle args) {
        final String tag = clazz.getName();
        if (NonRepeatable.class.isAssignableFrom(clazz)) {
            // 不可重复
            final Fragment find = manager.findFragmentByTag(tag);
            if (clazz.isInstance(find)) {
                if (args != null) {
                    ((NonRepeatable) find).onNewArguments(args);
                }
                return;
            }
        }
        manager.beginTransaction()
                .replace(containerId, clazz, args, tag)
                .commit();
    }

    /**
     * 替换
     *
     * @param activity FragmentActivity
     * @param clazz    Fragment 类名
     * @param args     参数
     * @return 添加成功时返回 true
     */
    public static boolean replace(@NonNull FragmentActivity activity,
                                  @IdRes int containerId,
                                  @NonNull Class<? extends Fragment> clazz,
                                  @Nullable Bundle args) {
        if (activity.findViewById(containerId) == null) {
            // 未找到容器
            return false;
        }
        replace(activity.getSupportFragmentManager(), containerId, clazz, args);
        return true;
    }

    /**
     * 替换
     *
     * @param fragment Fragment
     * @param clazz    Fragment 类名
     * @param args     参数
     * @return 添加成功时返回 true
     */
    public static boolean replace(@NonNull Fragment fragment,
                                  @IdRes int containerId,
                                  @NonNull Class<? extends Fragment> clazz,
                                  @Nullable Bundle args) {
        final View view = fragment.getView();
        if (view == null) {
            return false;
        }
        if (view.findViewById(containerId) == null) {
            // 未找到容器
            return false;
        }
        replace(fragment.getChildFragmentManager(), containerId, clazz, args);
        return true;
    }

    /**
     * 显示对话框
     *
     * @param manager FragmentManager
     * @param clazz   对话框类
     * @param args    参数
     * @return 显示成功时返回true
     */
    public static boolean show(@NonNull FragmentManager manager,
                                  @NonNull Class<? extends DialogFragment> clazz,
                                  @Nullable Bundle args) {
        final String tag = clazz.getName();
        if (NonRepeatable.class.isAssignableFrom(clazz)) {
            // 不可重复
            final Fragment find = manager.findFragmentByTag(tag);
            if (find != null && clazz.isInstance(find)) {
                if (args != null) {
                    ((NonRepeatable) find).onNewArguments(args);
                }
                return true;
            }
        }
        final DialogFragment created;
        try {
            created = clazz.newInstance();
        } catch (Throwable t) {
            return false;
        }
        created.setArguments(args);
        created.show(manager, tag);
        return true;
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
     * 查找可见的 Fragment
     * 注意：Activity 的 Window 的 DecorView 都还没有附着到窗口时，
     * Fragment 虽然已经 Resumed，但 isVisible 判断依然为 false，
     * 因为其 View 的 getWindowToken 为 null，
     * 所以需要在 Activity 的 Window 的 DecorView 附着到窗口 其 onViewAttachedToWindow 调用完成以后该方法才有效。
     * 该方法放在 View 的点击事件中执行是没有问题的。
     *
     * @param fragmentManager FragmentManager
     * @return 可见的 Fragment
     */
    @Nullable
    public static Fragment findVisibleFragment(@NonNull FragmentManager fragmentManager) {
        final List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments.isEmpty()) return null;
        for (int i = fragments.size() - 1; i >= 0; i--) {
            final Fragment fragment = fragments.get(i);
            if (fragment != null && fragment.isVisible()) {
                final FragmentManager childFragmentManager = fragment.getChildFragmentManager();
                final Fragment childFragment = findVisibleFragment(childFragmentManager);
                return childFragment != null ? childFragment : fragment;
            }
        }
        return null;
    }

    /**
     * 获取可见的 Fragment
     *
     * @param activity FragmentActivity
     * @return 可见的 Fragment
     */
    @Nullable
    public static Fragment findVisibleFragment(@NonNull FragmentActivity activity) {
        if (activity.getWindow().getDecorView().isAttachedToWindow()) {
            return findVisibleFragment(activity.getSupportFragmentManager());
        }
        return null;
    }
}
