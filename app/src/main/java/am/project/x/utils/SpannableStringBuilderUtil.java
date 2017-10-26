package am.project.x.utils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

/**
 * SpannableStringBuilder 工具类
 * Created by Alex on 2017/7/11.
 */

public class SpannableStringBuilderUtil {

    /**
     * 获取SpannableStringBuilder
     *
     * @param text       字符串源
     * @param color      颜色
     * @param foreground 是否为前景
     * @param value      修改的文字
     * @return SpannableStringBuilder
     */
    public static SpannableStringBuilder getSpannableStringBuilder(String text, int color,
                                                                   boolean foreground,
                                                                   String value) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int start;
        int end = 0;
        final int count = text.length();
        while (end < count) {
            start = text.indexOf(value, end);
            if (start == -1) {
                break;
            }
            end = start + value.length();
            if (foreground) {
                style.setSpan(
                        new ForegroundColorSpan(color), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                style.setSpan(
                        new BackgroundColorSpan(color), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }
}