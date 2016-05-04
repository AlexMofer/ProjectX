package com.am.security;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * RSA加密解密工具类
 * 一般用于加密对称加密的密钥
 * Created by Alex on 2016/4/28.
 */
public class RSAUtil {

    private final static String ALGORITHM = "RSA";
    private final static String TRANSFORMATION = "RSA/ECB/OAEPWithSHA256AndMGF1Padding";
    private final static int SIZE = 2048;
    private final static String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 公钥加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encryptByPublicKey(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        PublicKey publicKey = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(key));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(clear);
    }

    /**
     * 私钥加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] encryptByPrivateKey(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(key));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(clear);
    }

    /**
     * 公钥解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decryptByPublicKey(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        PublicKey publicKey = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(key));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encrypted);
    }

    /**
     * 私钥解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decryptByPrivateKey(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(key));
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }


    /**
     * 数字签名
     *
     * @param key  私钥字节
     * @param data 数据字节
     * @return 签名字节
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static byte[] signature(byte[] key, byte[] data) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidKeyException,
            SignatureException {
        PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(key));
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    /**
     * 校验签名
     *
     * @param key  公钥字节
     * @param data 数据字节
     * @param sign 签名字节
     * @return 是否通过校验
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean verify(byte[] key, byte[] data, byte[] sign) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidKeyException,
            SignatureException {
        PublicKey publicKey = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(key));
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    /**
     * 生成密钥
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(SIZE);
        return keyPairGenerator.generateKeyPair();
    }


}
