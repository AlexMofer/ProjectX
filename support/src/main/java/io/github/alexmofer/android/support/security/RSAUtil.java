/*
 * Copyright (C) 2015 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.alexmofer.android.support.security;

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
@SuppressWarnings({"unused", "WeakerAccess"})
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
     * @throws NoSuchAlgorithmException  异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchPaddingException    异常
     * @throws InvalidKeyException       异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] encryptByPublicKey(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        final PublicKey publicKey = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(key));
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(clear);
    }

    /**
     * 私钥加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws NoSuchAlgorithmException  异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchPaddingException    异常
     * @throws InvalidKeyException       异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] encryptByPrivateKey(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        final PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(key));
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(clear);
    }

    /**
     * 公钥解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws NoSuchAlgorithmException  异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchPaddingException    异常
     * @throws InvalidKeyException       异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] decryptByPublicKey(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        final PublicKey publicKey = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(key));
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(encrypted);
    }

    /**
     * 私钥解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws NoSuchAlgorithmException  异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchPaddingException    异常
     * @throws InvalidKeyException       异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] decryptByPrivateKey(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        final PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(key));
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encrypted);
    }


    /**
     * 数字签名
     *
     * @param key  私钥字节
     * @param data 数据字节
     * @return 签名字节
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeySpecException  异常
     * @throws InvalidKeyException      异常
     * @throws SignatureException       异常
     */
    public static byte[] signature(byte[] key, byte[] data) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidKeyException,
            SignatureException {
        final PrivateKey privateKey = KeyFactory.getInstance(ALGORITHM)
                .generatePrivate(new PKCS8EncodedKeySpec(key));
        final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
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
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeySpecException  异常
     * @throws InvalidKeyException      异常
     * @throws SignatureException       异常
     */
    public static boolean verify(byte[] key, byte[] data, byte[] sign) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            InvalidKeyException,
            SignatureException {
        final PublicKey publicKey = KeyFactory.getInstance(ALGORITHM)
                .generatePublic(new X509EncodedKeySpec(key));
        final Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    /**
     * 生成密钥
     *
     * @param size 密钥长度
     * @return 密钥对
     * @throws NoSuchAlgorithmException 异常
     */
    public static KeyPair generateKeyPair(int size) throws NoSuchAlgorithmException {
        final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成密钥
     *
     * @return 密钥对
     * @throws NoSuchAlgorithmException 异常
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        return generateKeyPair(SIZE);
    }
}
