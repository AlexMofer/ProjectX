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
package io.github.alexmofer.android.support.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

/**
 * 输入法工具类
 * Created by Alex on 2016/11/10.
 */
public class InputMethodManagerUtils {

    private static InputMethodManager getSystemService(Context context) {
        return context.getSystemService(InputMethodManager.class);
    }

    /**
     * 显示软键盘
     *
     * @param view View
     * @return 是否有执行显示动作
     */
    public static boolean showSoftInput(View view) {
        if (view == null) {
            return false;
        }
        view.requestFocus();
        view.requestFocusFromTouch();
        final InputMethodManager manager = getSystemService(view.getContext());
        if (manager == null) {
            return false;
        }
        manager.showSoftInput(view, 0);
        return true;
    }

    /**
     * 关闭软键盘
     *
     * @param view       View
     * @param clearFocus 是否清除焦点
     * @return 是否有执行关闭动作
     */
    public static boolean hideSoftInput(View view, boolean clearFocus) {
        if (view == null) {
            return false;
        }
        final InputMethodManager manager = getSystemService(view.getContext());
        if (manager == null) {
            return false;
        }
        if (manager.isActive(view)) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if (clearFocus) {
                view.clearFocus();
            }
            return true;
        }
        return false;
    }

    /**
     * 关闭软键盘
     *
     * @param activity   Activity
     * @param clearFocus 是否清除焦点
     * @return 是否有执行关闭动作
     */
    public static boolean hideSoftInput(Activity activity, boolean clearFocus) {
        if (activity == null) return false;
        return hideSoftInput(activity.getCurrentFocus(), clearFocus);
    }

    /**
     * 关闭软键盘
     *
     * @param fragment   Fragment
     * @param clearFocus 是否清除焦点
     * @return 是否有执行关闭动作
     */
    public static boolean hideSoftInput(Fragment fragment, boolean clearFocus) {
        if (fragment == null) return false;
        final View view = fragment.getView();
        if (view == null) {
            return false;
        }
        return hideSoftInput(view.findFocus(), clearFocus);
    }

    /**
     * 设置自动聚焦
     *
     * @param view        View
     * @param delayMillis 触发延迟
     */
    public static void setAutoFocus(@NonNull View view, long delayMillis) {
        final Runnable autoFocus = () -> {
            try {
                if (view.isAttachedToWindow()) {
                    InputMethodManagerUtils.showSoftInput(view);
                }
            } catch (Throwable t) {
                // ignore
            }
        };
        if (view.isAttachedToWindow()) {
            view.postDelayed(autoFocus, delayMillis);
        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(@NonNull View v) {
                    v.removeOnAttachStateChangeListener(this);
                    view.postDelayed(autoFocus, delayMillis);
                }

                @Override
                public void onViewDetachedFromWindow(@NonNull View v) {
                    // do nothing
                }
            });
        }
    }

    /**
     * 判断键盘是否打开
     *
     * @param view 任何 View
     * @return true 打开
     */
    public static boolean isKeyboardOpen(View view) {
        if (view == null || view.getRootWindowInsets() == null) {
            return false;
        }
        return WindowInsetsCompat.toWindowInsetsCompat(view.getRootWindowInsets())
                .isVisible(WindowInsetsCompat.Type.ime());
    }
}
