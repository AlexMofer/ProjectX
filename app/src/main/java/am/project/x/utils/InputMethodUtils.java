package am.project.x.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * 输入法工具类
 * Created by Alex on 2016/11/10.
 */

public class InputMethodUtils {

    /**
     * 判断是否已开启输入法
     *
     * @param activity Activity
     * @return 是否已开启输入法
     */
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    public static void openInputMethod(View view) {
        if (null == view) {
            return;
        }
        view.requestFocus();
        view.requestFocusFromTouch();
        InputMethodManager imm = ((InputMethodManager) (view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 关闭输入法
     *
     * @param view 焦点View
     */
    @SuppressWarnings("unused")
    public static void closeInputMethod(View view) {
        closeInputMethod(view, true);
    }

    /**
     * 关闭输入法
     *
     * @param view       焦点View
     * @param clearFocus 是否清除焦点
     */
    @SuppressWarnings("unused")
    public static void closeInputMethod(View view, boolean clearFocus) {
        if (null == view) {
            return;
        }
        InputMethodManager imm = ((InputMethodManager) (view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        imm.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (clearFocus)
            view.clearFocus();
    }

    /**
     * 关闭输入法
     *
     * @param activity Activity
     */
    @SuppressWarnings("unused")
    public static void closeInputMethod(Activity activity) {
        closeInputMethod(activity, true);
    }

    /**
     * 关闭输入法
     *
     * @param activity Activity
     */
    @SuppressWarnings("unused")
    public static void closeInputMethod(Activity activity, boolean clearFocus) {
        if (null == activity) {
            return;
        }
        closeInputMethod(activity.getCurrentFocus(), clearFocus);
    }
}
