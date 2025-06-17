package io.github.alexmofer.projectx.utils;

import java.util.regex.Pattern;

import androidx.annotation.NonNull;


/**
 * 字符串工具
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class StringUtils {

    /**
     * 字符串匹配
     *
     * @param src 源
     * @param des 匹配样式
     * @return 是否匹配
     */
    public static boolean isMatching(@NonNull String src, @NonNull String des) {
        String regex = des.replace("*", "\\w*").replace("?", "\\w{1}");
        return Pattern.compile(regex).matcher(src).matches();
    }

    /**
     * 判断是否是一个IP
     *
     * @param IP 字符串
     * @return 是否是一个IP
     */
    public static boolean isIp(@NonNull String IP) {
        boolean b = false;
        IP = IP.trim();
        while (IP.startsWith(" ")) {
            IP = IP.substring(1).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String s[] = IP.split("\\.");
            if (Integer.parseInt(s[0]) < 255)
                if (Integer.parseInt(s[1]) < 255)
                    if (Integer.parseInt(s[2]) < 255)
                        if (Integer.parseInt(s[3]) < 255)
                            b = true;
        }
        return b;
    }
}
