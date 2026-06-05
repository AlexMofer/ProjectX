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

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Slide;

/**
 * 基础 Fragment
 * Created by Alex on 2026/4/25.
 */
public abstract class Fragment extends androidx.fragment.app.Fragment implements BackPressable {

    /**
     * 尝试移除自身
     *
     * @param fragment Fragment
     * @return 移除成功时返回 true
     */
    @SuppressWarnings("UnusedReturnValue")
    public static boolean tryRemoveSelf(@NonNull androidx.fragment.app.Fragment fragment) {
        if (fragment instanceof Fragment) {
            try {
                ((Fragment) fragment).removeSelf();
                return true;
            } catch (Throwable t) {
                //ignore
            }
        }
        return false;
    }


    private static void add(@NonNull FragmentManager manager,
                            @IdRes int containerId,
                            @NonNull Class<? extends Fragment> clazz,
                            @Nullable Bundle args) {
        final String tag = clazz.getName();
        if (NonRepeatable.class.isAssignableFrom(clazz)) {
            // 不可重复
            final androidx.fragment.app.Fragment find = manager.findFragmentByTag(tag);
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
    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected static boolean add(@NonNull FragmentActivity activity,
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
    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected static boolean add(@NonNull androidx.fragment.app.Fragment fragment,
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

    private static void replace(@NonNull FragmentManager manager,
                                @IdRes int containerId,
                                @NonNull Class<? extends Fragment> clazz,
                                @Nullable Bundle args) {
        final String tag = clazz.getName();
        if (NonRepeatable.class.isAssignableFrom(clazz)) {
            // 不可重复
            final androidx.fragment.app.Fragment find = manager.findFragmentByTag(tag);
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
    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected static boolean replace(@NonNull FragmentActivity activity,
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
    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    protected static boolean replace(@NonNull androidx.fragment.app.Fragment fragment,
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onConfigTransition(savedInstanceState);
    }

    /**
     * 配置转场
     *
     * @param savedInstanceState 状态
     */
    protected void onConfigTransition(@Nullable Bundle savedInstanceState) {
        if (this instanceof SlideEnd) {
            final long duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            setEnterTransition(new Slide(Gravity.END).setDuration(duration));
            setExitTransition(new Slide(Gravity.END).setDuration(duration));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onConfigBackPressed();
    }

    protected void onConfigBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new InnerOnBackPressedCallback());
    }

    @Override
    public final void dispatchBackPressed() {
        if (onBackPressed()) {
            return;
        }
        removeSelf();
    }

    /**
     * 移除自身
     */
    protected void removeSelf() {
        getParentFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    /**
     * 返回操作
     *
     * @return 返回 true 表示消耗本次返回操作
     */
    protected boolean onBackPressed() {
        return false;
    }

    private class InnerOnBackPressedCallback extends OnBackPressedCallback {

        public InnerOnBackPressedCallback() {
            super(true);
        }

        @Override
        public void handleOnBackPressed() {
            dispatchBackPressed();
        }
    }
}
