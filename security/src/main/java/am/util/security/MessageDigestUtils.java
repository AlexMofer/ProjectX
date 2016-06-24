package am.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 信息摘要工具类
 * Created by Mofer on 2016/4/28.
 */
public class MessageDigestUtils {

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
     * 获取SHA-256
     *
     * @param src 数据源
     * @return SHA-256
     */
    public static byte[] getSHA256(byte[] src) {
        return getMessageDigest(src, "SHA-256");
    }
}
