package com.am.utils;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 *
 * @author Mofer
 */
public class AESUtil {

    public final static String ENCODING = "UTF-8";
    private final static String ALGORITHM = "AES";
    private final static String SR_ALGORITHM = "SHA1PRNG";
    private final static String PROVIDER = "Crypto";
    private final static String TRANSFORMATION = "AES";


    /**
     * 加密
     *
     * @param key            加密密钥
     * @param src            明文串
     * @param srcCharsetName 明文编码格式
     * @return 密文字节
     * @throws Exception
     */
    public static byte[] encrypt(String key, String src, String srcCharsetName)
            throws Exception {
        return encrypt(getRawKey(key.getBytes(ENCODING)), src.getBytes(srcCharsetName));
    }

    /**
     * 加密封装为16进制串
     *
     * @param key            加密密钥
     * @param src            明文串
     * @param srcCharsetName 明文编码格式
     * @return 16进制密文串
     * @throws Exception
     */
    public static String encryptToHexString(String key, String src, String srcCharsetName)
            throws Exception {
        byte[] result = encrypt(key, src, srcCharsetName);
        return BytesUtil.bytesToHexString(result);
    }

    /**
     * 加密并Base64编码
     *
     * @param key            加密密钥
     * @param src            明文串
     * @param srcCharsetName 明文编码格式
     * @return Base64编码密文串
     * @throws Exception
     */
    public static String encryptToBase64(String key, String src, String srcCharsetName)
            throws Exception {
        byte[] result = encrypt(key, src, srcCharsetName);
        return new String(Base64.encode(result, Base64.DEFAULT), ENCODING);
    }

    /**
     * 解密
     *
     * @param key               加密密钥
     * @param encrypted         密文字节
     * @param outputCharsetName 明文输出编码
     * @return 明文串
     * @throws Exception
     */
    public static String decrypt(String key, byte[] encrypted, String outputCharsetName)
            throws Exception {
        return new String(decrypt(getRawKey(key.getBytes(ENCODING)), encrypted), outputCharsetName);
    }

    /**
     * 16进制密文串解密
     *
     * @param key               加密密钥
     * @param encryptedStr      16进制密文串
     * @param outputCharsetName 明文输出编码
     * @return 明文串
     * @throws Exception
     */
    public static String decryptFromHexString(String key, String encryptedStr,
                                              String outputCharsetName) throws Exception {
        byte[] encrypted = BytesUtil.hexStringToBytes(encryptedStr);
        return decrypt(key, encrypted, outputCharsetName);
    }

    /**
     * Base64编码密文串解密
     *
     * @param key               加密密钥
     * @param encryptedStr      Base64编码密文串
     * @param outputCharsetName 明文输出编码
     * @return 明文串
     * @throws Exception
     */
    public static String decryptFromBase64(String key, String encryptedStr,
                                           String outputCharsetName) throws Exception {
        byte[] encrypted = Base64.decode(encryptedStr.getBytes(ENCODING), Base64.DEFAULT);
        return decrypt(key, encrypted, outputCharsetName);
    }

    /**
     * 获取256位的加密密钥
     *
     * @param seed 密钥明文字节
     * @return 密钥密文字节
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        //修复OpenSSL的PRNG问题(在4.3及以下版本需要)
        PRNGFixes.apply();
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        // 在4.2以上版本中，SecureRandom获取方式发生了改变
        SecureRandom sr = android.os.Build.VERSION.SDK_INT >= 17 ?
                SecureRandom.getInstance(SR_ALGORITHM, PROVIDER)
                :
                SecureRandom.getInstance(SR_ALGORITHM);
        sr.setSeed(seed);
        // 256 bits or 128 bits,192bits
        keyGen.init(128, sr);
        SecretKey sKey = keyGen.generateKey();
        return sKey.getEncoded();
    }

    /**
     * 加密
     *
     * @param key 密钥密文字节
     * @param src 明文字节
     * @return 加密字节
     * @throws Exception
     */
    private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param key       密钥密文字节
     * @param encrypted 加密字节
     * @return 明文字节
     * @throws Exception
     */
    private static byte[] decrypt(byte[] key, byte[] encrypted)
            throws Exception {
        SecretKeySpec sKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        return cipher.doFinal(encrypted);
    }

}