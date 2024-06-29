package io.github.alexmofer.android.support.utils;

import java.util.Objects;

/**
 * 字符串功能
 * Created by Alex on 2022/5/26.
 */
public class StringUtils {

    private StringUtils() {
        //no instance
    }

    /**
     * 判断字符串是否已某个字段开始
     *
     * @param start 开始字段
     * @param str   字符串
     * @return 是以该字段开始时返回true
     */
    public static boolean startsWith(String start, String str) {
        if (str == null || start == null) {
            return false;
        }
        final int l1 = start.length();
        final int l2 = str.length();
        if (l1 <= 0 || l2 <= 0 || l2 < l1) {
            return false;
        }
        return Objects.equals(start, str.substring(0, l1));
    }

    /**
     * 判断是否为电话号码
     *
     * @param str 字符串
     * @return 为电话号码时返回true
     */
    public static boolean isPhoneNumber(String str) {
        if (str == null) {
            return false;
        }
        str = str.trim();
        if (str.length() <= 4) {
            return false;
        }
        if (str.charAt(0) == '+') {
            str = str.substring(1);
        }
        if (str.charAt(1) == '+' && (str.charAt(0) == '(' || str.charAt(0) == '（')) {
            str = str.substring(2);
        }
        str = str.replace("(", "").replace("（", "");
        str = str.replace(")", "").replace("）", "");
        str = str.replace("-", "");
        str = str.replace(" ", "");
        if (str.length() <= 4 || str.length() > 15) {
            return false;
        }
        return str.matches("\\d+");
    }

    /**
     * 判断是否为电子邮件地址
     *
     * @param str 字符串
     * @return 为电子邮件地址时返回true
     */
    public static boolean isEmailAddress(String str) {
        return str.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    }

    /**
     * 判断是否为URL链接
     *
     * @param str 字符串
     * @return 为URL链接时返回true
     */
    public static boolean isURL(String str) {
        return str.matches("(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
    }
}
