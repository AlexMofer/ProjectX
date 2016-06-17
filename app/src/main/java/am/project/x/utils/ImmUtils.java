package am.project.x.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class ImmUtils {

    /**
     * 关闭输入法
     *
     * @param activity Activity
     */
    public static void closeImm(Activity activity) {
        if (activity == null) {
            return;
        }
        closeImm(activity, activity.getCurrentFocus());
    }

    /**
     * 关闭输入法
     *
     * @param context Context
     * @param focus   焦点View
     */
    public static void closeImm(Context context, View focus) {
        if (null == context) {
            return;
        }
        if (focus != null) {
            InputMethodManager imm = ((InputMethodManager) (context
                    .getSystemService(Context.INPUT_METHOD_SERVICE)));
            imm.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 打开输入法
     *
     * @param context Context
     * @param view    焦点View
     */
    public static void showImm(Context context, View view) {
        if (null == context) {
            return;
        }
        if (view != null) {
            InputMethodManager imm = ((InputMethodManager) (context
                    .getSystemService(Context.INPUT_METHOD_SERVICE)));
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 判断是否已开启输入法
     *
     * @param activity Activity
     * @return 是否已开启输入法
     */
    @SuppressWarnings("unused")
    public static boolean isShowImm(Activity activity) {
        return activity != null && activity.getWindow().getAttributes().softInputMode ==
                WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED;
    }
}
