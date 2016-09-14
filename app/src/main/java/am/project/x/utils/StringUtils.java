package am.project.x.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.regex.Pattern;


/**
 * 字符串工具
 *
 * @author Alex
 */
@SuppressWarnings("unused")
public class StringUtils {


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
     * 字符串检查
     *
     * @param str 待检查字符串
     * @return 判断结果
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
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
     * 转为HexString
     *
     * @param data 待转码字节数组
     * @return 转码字符串
     */
    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int v : data) {
            int hs = v & 0xff;
            if (hs < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(hs));
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }

    /**
     * 获取信息摘要
     *
     * @param src       数据源
     * @param algorithm 算法
     * @return 信息摘要
     */
    public static byte[] getMessageDigest(byte[] src, String algorithm) {
        if (src == null)
            return null;
        try {
            return MessageDigest.getInstance(algorithm).digest(src);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 获取MD5
     *
     * @param src 数据源
     * @return MD5
     */
    public static byte[] getMD5(byte[] src) {
        return getMessageDigest(src, "MD5");
    }

    /**
     * 获取MD5
     *
     * @param src       待计数据
     * @param lowerCase 是否小写
     * @return MD5值
     */
    public static String getMD5(byte[] src, boolean lowerCase) {
        StringBuilder builder = new StringBuilder();
        byte[] md5 = getMD5(src);
        builder.append(Base64.encodeToString(md5, Base64.DEFAULT));
        if (lowerCase)
            return builder.toString();
        else
            return builder.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * MD5加码
     *
     * @param str         待计算字符串
     * @param charsetName 编码格式
     * @return MD5值
     */
    public static String getMD5(String str, String charsetName, boolean lowerCase) {
        try {
            return getMD5(str.getBytes(charsetName), lowerCase);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 二进制转十六进制
     *
     * @param data 二进制数组
     * @return 十六进制字符
     */
    public static String parseByte2HexStr(byte[] data, boolean lowerCase) {
        if (data == null || data.length <= 0)
            return null;
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
        }
        if (lowerCase)
            return builder.toString();
        else
            return builder.toString().toUpperCase(Locale.getDefault());
    }

    /**
     * 十六进制转二进制
     *
     * @param hexStr 十六进制字符
     * @return 二进制数组
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr == null || hexStr.length() <= 0)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
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
