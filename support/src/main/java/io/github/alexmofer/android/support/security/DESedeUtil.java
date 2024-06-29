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

import android.annotation.SuppressLint;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES加密解密工具类
 * 不安全，最好使用AES加密
 *
 * @author Alex
 */
@SuppressWarnings("WeakerAccess")
public class DESedeUtil {

    private final static String ALGORITHM = "DESede";
    private final static String TRANSFORMATION = "DESede/ECB/PKCS5Padding";
    private final static int SIZE = 192;// 仅支持128、168、192

    /**
     * 加密
     *
     * @param key   密钥字节
     * @param size  密钥长度
     * @param clear 明文字节
     * @return 密文字节
     * @throws InvalidKeyException       异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchAlgorithmException  异常
     * @throws NoSuchPaddingException    异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] encrypt(byte[] key, int size, byte[] clear) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            IllegalBlockSizeException,
            BadPaddingException {
        final Key secureKey;
        if (size == 128) {
            secureKey = new SecretKeySpec(key, ALGORITHM);// 168 长度的Key部分支持
        } else {
            secureKey = SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(new DESedeKeySpec(key));// 128 长度的Key不支持
        }
        @SuppressLint("GetInstance") final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secureKey);
        return cipher.doFinal(clear);
    }

    /**
     * 加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws InvalidKeyException       异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchAlgorithmException  异常
     * @throws NoSuchPaddingException    异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] encrypt(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            IllegalBlockSizeException,
            BadPaddingException {
        return encrypt(key, SIZE, clear);
    }

    /**
     * 解密
     *
     * @param key       密钥字节
     * @param size      密钥长度
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws InvalidKeyException       异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchAlgorithmException  异常
     * @throws NoSuchPaddingException    异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] decrypt(byte[] key, int size, byte[] encrypted) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            IllegalBlockSizeException,
            BadPaddingException {
        final Key secureKey;
        if (size == 128) {
            secureKey = new SecretKeySpec(key, ALGORITHM);// 168 长度的Key部分支持
        } else {
            secureKey = SecretKeyFactory.getInstance(ALGORITHM)
                    .generateSecret(new DESedeKeySpec(key));// 128 长度的Key不支持
        }
        @SuppressLint("GetInstance") final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secureKey);
        return cipher.doFinal(encrypted);
    }

    /**
     * 解密
     *
     * @param key       密钥字节
     * @param encrypted 密文字节
     * @return 明文字节
     * @throws InvalidKeyException       异常
     * @throws InvalidKeySpecException   异常
     * @throws NoSuchAlgorithmException  异常
     * @throws NoSuchPaddingException    异常
     * @throws IllegalBlockSizeException 异常
     * @throws BadPaddingException       异常
     */
    public static byte[] decrypt(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidKeySpecException,
            IllegalBlockSizeException,
            BadPaddingException {
        return decrypt(key, SIZE, encrypted);
    }

    /**
     * 生成密钥
     *
     * @param size 密钥长度
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] generateKey(int size) throws NoSuchAlgorithmException {
        return KeyUtil.generateKey(ALGORITHM, size);
    }

    /**
     * 生成密钥
     *
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] generateKey() throws NoSuchAlgorithmException {
        return generateKey(SIZE);
    }

    /**
     * 随机数种子
     *
     * @param seed 随机数种子
     * @param size 密钥长度
     * @return 密钥字节
     * @throws Exception 异常
     */
    public static byte[] getRandomKey(byte[] seed, int size) throws Exception {
        return KeyUtil.getRandomKey(ALGORITHM, seed, size);
    }

    /**
     * 随机数种子
     *
     * @param seed 随机数种子
     * @return 密钥字节
     * @throws Exception 异常
     */
    public static byte[] getRandomKey(byte[] seed) throws Exception {
        return getRandomKey(seed, SIZE);
    }

    /**
     * PBE口令密钥
     *
     * @param password 口令
     * @param salt     盐
     * @param size     密钥长度
     * @return 密钥字节
     * @throws Exception 异常
     */
    public static byte[] getPBEKey(char[] password, byte[] salt, int size) throws Exception {
        return KeyUtil.getPBEKey(password, salt, size);
    }

    /**
     * PBE口令密钥
     *
     * @param password 口令
     * @param salt     盐
     * @return 密钥字节
     * @throws Exception 异常
     */
    public static byte[] getPBEKey(char[] password, byte[] salt) throws Exception {
        return getPBEKey(password, salt, SIZE);
    }
}
