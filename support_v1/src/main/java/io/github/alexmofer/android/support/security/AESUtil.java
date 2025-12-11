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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 *
 * @author Alex
 */
@SuppressWarnings("WeakerAccess")
public class AESUtil {

    private final static String ALGORITHM = "AES";
    private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private final static int SIZE = 256;


    /**
     * 加密
     *
     * @param key   密钥字节
     * @param clear 明文字节
     * @return 密文字节
     * @throws NoSuchAlgorithmException           异常
     * @throws NoSuchPaddingException             异常
     * @throws InvalidKeyException                异常
     * @throws InvalidAlgorithmParameterException 异常
     * @throws IllegalBlockSizeException          异常
     * @throws BadPaddingException                异常
     */
    public static byte[] encrypt(byte[] key, byte[] clear) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
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
     * @throws NoSuchAlgorithmException           异常
     * @throws NoSuchPaddingException             异常
     * @throws InvalidKeyException                异常
     * @throws InvalidAlgorithmParameterException 异常
     * @throws IllegalBlockSizeException          异常
     * @throws BadPaddingException                异常
     */
    public static byte[] decrypt(byte[] key, byte[] encrypted) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException,
            IllegalBlockSizeException,
            BadPaddingException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        return cipher.doFinal(encrypted);
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