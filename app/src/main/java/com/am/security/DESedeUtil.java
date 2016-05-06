package com.am.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * DES加密解密工具类
 * 最好使用AES加密
 *
 * @author Mofer
 */
public class DESedeUtil {

    private final static String ALGORITHM = "DESede";
    private final static String TRANSFORMATION = "DESede/ECB/PKCS5Padding";
    private final static int SIZE = 192;// 仅支持128、168、192

    /**
     * 加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @SuppressWarnings("all")
    public static byte[] encrypt(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            IllegalBlockSizeException,
            BadPaddingException {
        SecretKey secureKey = SecretKeyFactory.getInstance(ALGORITHM)
                .generateSecret(new DESedeKeySpec(key));// 128 长度的Key不支持
//        SecretKeySpec secureKey = new SecretKeySpec(key, KEY_ALGORITHM);// 168 长度的Key部分支持
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secureKey);
        return cipher.doFinal(clear);
    }

    /**
     * 解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @SuppressWarnings("all")
    public static byte[] decrypt(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            IllegalBlockSizeException,
            BadPaddingException {

        SecretKey secureKey = SecretKeyFactory.getInstance(ALGORITHM)
                .generateSecret(new DESedeKeySpec(key));// 128 长度的Key不支持
//        SecretKeySpec secureKey = new SecretKeySpec(key, KEY_ALGORITHM);// 168 长度的Key部分支持
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secureKey);
        return cipher.doFinal(encrypted);
    }

    /**
     * 生成密钥
     *
     * @return 密钥字节
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        return KeyUtil.generateKey(ALGORITHM, SIZE);
    }

    /**
     * 随机数种子
     *
     * @param seed 随机数种子
     * @return 密钥字节
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static byte[] getRandomKey(byte[] seed) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        return KeyUtil.getRandomKey(ALGORITHM, seed, SIZE);
    }

    /**
     * PBE口令密钥
     *
     * @param password 口令
     * @param salt     盐
     * @return 密钥字节
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static byte[] getPBEKey(char[] password, byte[] salt) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        return KeyUtil.getPBEKey(password, salt, SIZE);
    }
}
