package am.project.x.utils;

import android.support.annotation.NonNull;
import android.util.Base64;

import java.util.regex.Pattern;


/**
 * 字符串工具
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class StringUtils {

    /**
     * 字符串检查
     *
     * @param str 待检查字符串
     * @return 判断结果
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

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
     * 克隆
     *
     * @param target 源
     * @return 克隆字符串
     */
    public static String clone(String target) {
        if (target == null)
            return null;
        return target + "";
    }

    /**
     * 获取哈希值
     *
     * @param target 源
     * @return 哈希值
     */
    public static int hashCode(String target) {
        if (target == null)
            return 0;
        return target.hashCode();
    }

    /**
     * 字符串排序
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 排序值
     */
    public static int compareTo(String str1, String str2) {
        if (str1 == null || str2 == null)
            return 0;
        final int lexicographically = str1.compareTo(str2);
        if (lexicographically < 0) {
            return -1;
        } else if (lexicographically > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str 字符串
     * @return 是否为数字
     */
    public static boolean isNumeric(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    /**
     * 将空字符串转为""
     *
     * @param str 待修改字符串
     * @return 修改结果
     */
    public static String setNullToEmpty(String str) {
        return str == null ? "" : str;
    }

    /**
     * 将空字符串转为"null"
     *
     * @param str 待修改字符串
     * @return 修改结果
     */
    public static String setNullToString(String str) {
        return str == null ? "null" : str;
    }


    /**
     * BASE64 编码
     *
     * @param str         待编码字符串
     * @param encodingIn  解码编码格式
     * @param encodingOut 输出编码格式
     * @return 编码字符串
     */
    public static String encodingBASE64(String str, String encodingIn, String encodingOut) {
        try {
            return new String(Base64.encode(str.getBytes(encodingIn), Base64.DEFAULT), encodingOut);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * BASE64 解码
     *
     * @param str         待编码字符串
     * @param encodingIn  解码编码格式
     * @param encodingOut 输出编码格式
     * @return 编码字符串
     */
    public static String decodingBASE64(String str, String encodingIn, String encodingOut) {
        try {
            return new String(Base64.decode(str.getBytes(encodingIn), Base64.DEFAULT), encodingOut);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取长宽
     *
     * @param size 长宽
     */
    public static void getSizeByUrl(String url, int[] size) {
        if (url == null || url.length() <= 0 || size == null || size.length < 2)
            return;
        int width = 0;
        int height = 0;
        String[] wxh = url.split("_|\\.jpg|\\.jpeg|\\.png|\\.bmp");
        if (wxh.length == 2) {
            String[] wh = wxh[1].split("x");
            if (wh.length == 2) {
                try {
                    width = Integer.parseInt(wh[0]);
                    height = Integer.parseInt(wh[1]);
                } catch (NumberFormatException e) {
                    width = 0;
                    height = 0;
                }
            }
        }
        size[0] = width;
        size[1] = height;
    }

    public static boolean isMobilePhoneNum(String phoneNum) {
        return phoneNum != null && phoneNum.length() != 0 && phoneNum.matches("[1][3458]\\d{9}");
    }

    /**
     * 手机号码，身份证证等隐私内容加*
     *
     * @param str       要加*的字符串
     * @param frontLen  *前面预留长度
     * @param behindLen *后面预留长度
     * @return 加*后的字符串
     */
    public static String getPrivateStr(String str, int frontLen, int behindLen) {
        int strLen = str.length();
        if (frontLen >= strLen || behindLen >= strLen || frontLen + behindLen >= strLen) {
            return str;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(str.substring(0, frontLen));
        int starCount = strLen - frontLen - behindLen;
        for (int i = 0; i < starCount; i++) {
            builder.append("\u002A");
        }
        return builder.append(str.substring(strLen - behindLen, strLen)).toString();
    }

    /**
     * 去掉字符串前后所有的空格
     *
     * @param str 字符串
     * @return 字符串
     */
    public static String trimStartAndEnd(String str) {
        while (str.startsWith(" ")) {
            str = str.substring(1, str.length()).trim();
        }
        while (str.endsWith(" ")) {
            str = str.substring(0, str.length() - 1).trim();
        }
        return str;
    }

    /**
     * 判断是否是一个IP
     *
     * @param IP 字符串
     * @return 是否是一个IP
     */
    public static boolean isIp(String IP) {
        boolean b = false;
        IP = trimStartAndEnd(IP);
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

    /**
     * 判断是否中文
     * GENERAL_PUNCTUATION 判断中文的“号
     * CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
     * HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
     *
     * @param c 字符
     * @return 是否中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    /**
     * 判断字符串是否包含中文
     *
     * @param str 字符串
     * @return 字符串是否包含中文
     */
    public static boolean containsChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (StringUtils.isChinese(c))
                return true;
        }
        return false;
    }

    /**
     * 获取文字字节数(2字节以上的字符一律按2字节统计)
     *
     * @param s 字符串
     * @return 字节数
     */
    public static int getWordCount(String s) {
        if (s == null)
            return 0;
        return s.replaceAll("[^\\x00-\\xff]", "\u002A\u002A").length();
    }

    /**
     * 检测是否有emoji字符
     *
     * @param source 字符串
     * @return 一旦含有就抛出
     */

    public static boolean containsEmoji(String source) {
        if (isNullOrEmpty(source)) {
            return false;
        }
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isNotEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }


    private static boolean isNotEmojiCharacter(char codePoint) {
        return codePoint == 0x0 ||
                codePoint == 0x9 ||
                codePoint == 0xA ||
                codePoint == 0xD ||
                (codePoint >= 0x20 && codePoint <= 0xD7FF) ||
                (codePoint >= 0xE000 && codePoint <= 0xFFFD) ||
                codePoint >= 0x10000;
    }

    /**
     * 获取编码类型（非精准）
     *
     * @param first3bytes 前三个字节
     * @return 编码类型
     */
    public static String getCharset(byte[] first3bytes) {
        String charset = "GBK";
        if (first3bytes == null || first3bytes.length < 3) {
            return charset;
        }
        if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
                && first3bytes[2] == (byte) 0xBF) {
            // utf-8
            charset = "utf-8";
        } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
            charset = "unicode";
        } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
            charset = "utf-16be";
        } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
            charset = "utf-16le";
        } else {
            // 添加更多判断方法
            charset = "GBK";
        }
        return charset;
    }
}
