package com.am.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
public class DESUtil {

    private final static String ALGORITHM = "DESede";
    private final static String TRANSFORMATION = "DESede/ECB/PKCS5Padding";

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
    public static byte[] encrypt(byte[] key, byte[] clear) throws
            InvalidKeyException,
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {

        DESedeKeySpec dks = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secureKey = keyFactory.generateSecret(dks);

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
    public static byte[] decrypt(byte[] key, byte[] encrypted) throws
            InvalidKeyException,
            InvalidKeySpecException,
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {

        DESedeKeySpec dks = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secureKey = keyFactory.generateSecret(dks);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secureKey);
        return cipher.doFinal(encrypted);
    }
}
