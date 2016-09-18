package am.project.x.utils;

import java.util.Locale;

/**
 * 字节工具类
 * Created by Alex on 2016/4/26.
 */
@SuppressWarnings("unused")
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
        String hexStr = "";
        for (byte b : src) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexStr += hex.toUpperCase(Locale.getDefault());
        }
        return hexStr;
    }

    /**
     * 把为字符串转化为字节数组
     *
     * @param hexStr 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexStringToBytes(String hexStr) {
        if (hexStr == null || hexStr.equals("") || hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 合并byte数组
     *
     * @param items byte数组
     * @return 合并结果
     */
    public static byte[] byteMerger(byte[]... items) {
        if (items == null || items.length <= 0)
            return null;
        if (items.length == 1) {
            return items[0];
        }
        int count = 0;
        for (byte[] item : items) {
            count += item.length;
        }
        byte[] result = new byte[count];
        count = 0;
        for (byte[] item : items) {
            System.arraycopy(item, 0, result, count, item.length);
            count += item.length;
        }
        return result;
    }
}
