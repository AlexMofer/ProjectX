package com.am.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密工具类
 * 
 * @author Mofer
 * 
 */
public class AESUtil {

    private final static String ENCODING = "UTF-8";
    private final static String ALGORITHM = "AES";
    private final static String SR_ALGORITHM = "SHA1PRNG";
    private final static String PROVIDER = "Crypto";
	private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";

	/**
	 * 加密
	 * 
	 * @param key 密钥
	 * @param src 字符串
	 * @return 加密字节
	 * @throws Exception
	 */
	public static byte[] encrypt(String key, String src, String charsetName) throws Exception {
		return encrypt(getRawKey(key.getBytes(ENCODING)), src.getBytes(charsetName));
	}

	/**
	 * 解密
	 * 
	 * @param key 密钥串
	 * @param encrypted 解密字节
	 * @return 解密串
	 * @throws Exception
	 */
	public static String decrypt(String key, byte[] encrypted, String charsetName) throws Exception {
		return new String(decrypt(getRawKey(key.getBytes(ENCODING)), encrypted), charsetName);
	}

	/**
	 * 获取256位的加密密钥
	 * 
	 * @param seed 密钥种子
	 * @return 密钥字节
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
        keyGen.init(256, sr);
		SecretKey sKey = keyGen.generateKey();
		return sKey.getEncoded();
	}

	/**
	 * 真正的加密过程
	 * 
	 * @param key 密钥
	 * @param src 字节
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
	 * 真正的解密过程
	 * 
	 * @param key 密钥
	 * @param encrypted 加密字节
	 * @return 解密字节
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