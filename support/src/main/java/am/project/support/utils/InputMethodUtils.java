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

package am.project.support.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 输入法工具类
 * Created by Alex on 2016/11/10.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class InputMethodUtils {

    /**
     * 判断是否已开启输入法
     *
     * @param activity Activity
     * @return 是否已开启输入法
     */
    public static boolean isInputMethodOpen(Activity activity) {
        return activity != null && activity.getWindow().getAttributes().softInputMode ==
                WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }

    /**
     * 打开输入法
     * 会使得view得到焦点
     *
     * @param view View
     */
    public static void openInputMethod(View view) {
        if (null == view) {
            return;
        }
        view.requestFocus();
        view.requestFocusFromTouch();
        InputMethodManager imm = ((InputMethodManager) (view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 关闭输入法
     *
     * @param view 焦点View
     */
    public static void closeInputMethod(View view) {
        closeInputMethod(view, true);
    }

    /**
     * 关闭输入法
     *
     * @param view       焦点View
     * @param clearFocus 是否清除焦点
     */
    public static void closeInputMethod(View view, boolean clearFocus) {
        if (null == view) {
            return;
        }
        InputMethodManager imm = ((InputMethodManager) (view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (clearFocus)
            view.clearFocus();
    }

    /**
     * 关闭输入法
     *
     * @param activity Activity
     */
    public static void closeInputMethod(Activity activity) {
        closeInputMethod(activity, true);
    }

    /**
     * 关闭输入法
     *
     * @param activity Activity
     */
    public static void closeInputMethod(Activity activity, boolean clearFocus) {
        if (null == activity) {
            return;
        }
        closeInputMethod(activity.getCurrentFocus(), clearFocus);
    }
}
