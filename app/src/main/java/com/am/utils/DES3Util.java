package com.am.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 3DES加密工具类
 *
 */
public class DES3Util {

    private final static String ENCODING = "UTF-8";
    private final static String ALGORITHM = "DESede";
    private final static String TRANSFORMATION = "DESede/ECB/PKCS5Padding";

    /**
     * 3DES加密
     *
     * @param plainText 普通文本
     * @param secretKey 密钥
     * @return 加密串
     * @throws Exception
     */
    public static String encrypt(String plainText, String secretKey)
            throws Exception {

        DESedeKeySpec dks = new DESedeKeySpec(secretKey.getBytes(ENCODING));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secureKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secureKey);
        byte[] b = cipher.doFinal(plainText.getBytes(ENCODING));
        return new String(Base64.encode(b, Base64.DEFAULT), ENCODING);
    }

    /**
     * 3DES解密
     *
     * @param encryptText 加密文本
     * @param secretKey   密钥
     * @return 解密串
     * @throws Exception
     */
    public static String decrypt(String encryptText, String secretKey)
            throws Exception {

        byte[] byteSrc = Base64.decode(encryptText.getBytes(ENCODING), Base64.DEFAULT);

        DESedeKeySpec dks = new DESedeKeySpec(secretKey.getBytes(ENCODING));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secureKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secureKey);
        byte[] retByte = cipher.doFinal(byteSrc);

        return new String(retByte, ENCODING);

    }
}
