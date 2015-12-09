package com.am.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;

/**
 * AES加密解密工具类
 * 
 * @author Mofer
 * 
 */
public class AESUtil {

	/**
	 * 加密
	 * 
	 * @param key
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(String key, String src, String charsetName) throws Exception {
		byte[] rawKey = getRawKey(key.getBytes("UTF-8"));
		return encrypt(rawKey, src.getBytes(charsetName));
	}

	/**
	 * 解密
	 * 
	 * @param key
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String key, byte[] encrypted, String charsetName) throws Exception {
		byte[] rawKey = getRawKey(key.getBytes("UTF-8"));
		byte[] result = decrypt(rawKey, encrypted);
		return new String(result, charsetName);
	}

	/**
	 * 获取256位的加密密钥
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	@SuppressLint("TrulyRandom")
	private static byte[] getRawKey(byte[] seed) throws Exception {
		// 在4.3以上版本中，修复OpenSSL的PRNG问题
		PRNGFixes.apply();
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// 在4.2以上版本中，SecureRandom获取方式发生了改变
		SecureRandom sr = android.os.Build.VERSION.SDK_INT >= 17 ? SecureRandom
				.getInstance("SHA1PRNG", "Crypto") : SecureRandom
				.getInstance("SHA1PRNG");

		sr.setSeed(seed);
		// 256 bits or 128 bits,192bits
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	/**
	 * 真正的加密过程
	 * 
	 * @param key
	 * @param src
	 * @return
	 * @throws Exception
	 */
	@SuppressLint("TrulyRandom")
	private static byte[] encrypt(byte[] key, byte[] src) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(src);
		return encrypted;
	}

	/**
	 * 真正的解密过程
	 * 
	 * @param key
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] key, byte[] encrypted)
			throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

}