package com.am.utils;

/**
 * 字节工具类
 * Created by Alex on 2016/4/26.
 */
public class BytesUtil {

    /**
     * 把字节数组转化为字符串
     *
     * @param src 字节数组
     * @return 16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        if (src == null || src.length <= 0) {
            return null;
        }
        String result = "";
        for (byte data : src) {
            int v = data & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                result += "0";
            }
            result += hv + " ";
        }
        return result;
    }

    /**
     * 把为字符串转化为字节数组
     *
     * @param hexString 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * char 转 byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
