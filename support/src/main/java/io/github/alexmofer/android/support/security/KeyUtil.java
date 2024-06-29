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


import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


/**
 * 密钥工具类
 * Created by Alex on 2016/4/29.
 */
@SuppressWarnings("WeakerAccess")
public class KeyUtil {

    private final static String RAW_ALGORITHM = "SHA1PRNG";
    private final static String SKF_ALGORITHM = "PBKDF2WithHmacSHA1";
    private final static int ITERATION = 2048;

    /**
     * 生成密钥
     *
     * @param algorithm 算法
     * @param size      密钥长度
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     */
    public static byte[] generateKey(String algorithm, int size) throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(size);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    /**
     * 随机数种子密钥
     *
     * @param keyAlgorithm KeyGenerator算法
     * @param rawAlgorithm SecureRandom算法
     * @param seed         随机数种子
     * @param size         密钥长度
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     * @throws NoSuchProviderException  异常
     */
    @SuppressWarnings("RedundantThrows")
    public static byte[] getRandomKey(String keyAlgorithm, String rawAlgorithm,
                                      byte[] seed, int size) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        //修复OpenSSL的PRNG问题(在4.3及以下版本需要)
        PRNGFixes.apply();
        KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgorithm);
        SecureRandom sr = SecureRandom.getInstance(rawAlgorithm);
        sr.setSeed(seed);
        keyGen.init(size, sr);
        SecretKey sKey = keyGen.generateKey();
        return sKey.getEncoded();
    }

    /**
     * PBE口令密钥
     *
     * @param algorithm      SecretKeyFactory算法
     * @param password       口令
     * @param salt           盐
     * @param iterationCount 迭代次数
     * @param size           密钥长度
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeySpecException  异常
     */
    public static byte[] getPBEKey(String algorithm, char[] password, byte[] salt,
                                   int iterationCount, int size) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, size);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey.getEncoded();
    }

    /**
     * 随机数种子密钥
     *
     * @param algorithm KeyGenerator算法
     * @param seed      随机数种子
     * @param size      密钥长度
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     * @throws NoSuchProviderException  异常
     */
    public static byte[] getRandomKey(String algorithm, byte[] seed, int size) throws
            NoSuchAlgorithmException,
            NoSuchProviderException {
        return getRandomKey(algorithm, RAW_ALGORITHM, seed, size);
    }

    /**
     * PBE口令密钥
     *
     * @param password 口令
     * @param salt     盐
     * @param size     密钥长度
     * @return 密钥字节
     * @throws NoSuchAlgorithmException 异常
     * @throws InvalidKeySpecException  异常
     */
    public static byte[] getPBEKey(char[] password, byte[] salt, int size) throws
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        return getPBEKey(SKF_ALGORITHM, password, salt, ITERATION, size);
    }
}
