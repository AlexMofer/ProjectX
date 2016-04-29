package com.am.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 *
 * @author Mofer
 */
public class AESUtil {

    private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final static String KEY_ALGORITHM = "AES";
    private final static String RAW_ALGORITHM = "SHA1PRNG";
    private final static String SKF_ALGORITHM = "PBKDF2WithHmacSHA1";
    private final static String PROVIDER = "Crypto";
    private final static int SIZE = 256;


    /**
     * 加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encrypt(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        return cipher.doFinal(clear);
    }

    /**
     * 解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
    }

    /**
     * 生成密钥
     *
     * @return 密钥字节
     * @throws NoSuchAlgorithmException
     */
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        return KeyUtil.generateKey(KEY_ALGORITHM, SIZE);
    }

    /**
     * 随机数种子
     *
     * @param seed 随机数种子
     * @return 密钥字节
     * @throws Exception
     */
    private static byte[] getRandomKey(byte[] seed) throws Exception {
        return KeyUtil.getRandomKey(KEY_ALGORITHM, seed, SIZE);
    }

    /**
     * PBE口令密钥
     *
     * @param password       口令
     * @param salt           盐
     * @param iterationCount 迭代次数
     * @return 密钥字节
     * @throws Exception
     */
    private static byte[] getPBEKey(char[] password, byte[] salt,
                                    int iterationCount) throws Exception {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SKF_ALGORITHM);
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, SIZE);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey.getEncoded();
    }

    /**
     * 随机数种子模式加密
     *
     * @param seed  随机数种子
     * @param clear 明文字节
     * @return 密文字节
     */
    public static byte[] encryptWithRandomKey(byte[] seed, byte[] clear) {
        try {
            return encrypt(getRandomKey(seed), clear);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 随机数种子模式解密
     *
     * @param seed      随机数种子
     * @param encrypted 密文字节
     * @return 明文字节
     */
    public static byte[] decryptWithRandomKey(byte[] seed, byte[] encrypted) {
        try {
            return decrypt(getRandomKey(seed), encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * PBE口令密钥加密
     *
     * @param password       口令
     * @param salt           盐
     * @param iterationCount 迭代次数
     * @param clear          明文字节
     * @return 密文字节
     */
    public static byte[] encryptWithPBEKey(char[] password, byte[] salt, int iterationCount,
                                           byte[] clear) {
        try {
            return encrypt(getPBEKey(password, salt, iterationCount), clear);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * PBE口令密钥解密
     *
     * @param password       口令
     * @param salt           盐
     * @param iterationCount 迭代次数
     * @param encrypted      密文字节
     * @return 明文字节
     */
    public static byte[] decryptWithPBEKey(char[] password, byte[] salt, int iterationCount,
                                           byte[] encrypted) {
        try {
            return decrypt(getPBEKey(password, salt, iterationCount), encrypted);
        } catch (Exception e) {
            return null;
        }
    }
}