package com.am.security;

/**
 * RSA加密解密工具类
 * Created by Alex on 2016/4/28.
 */
public class RSAUtil {

    private void s() {
        //生成密钥
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        RSAPublicKey rsaPublicKey = (RSAPublicKey)keyPair.getPublic();
//        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)keyPair.getPrivate();
//
//        //签名
//        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
//        Signature signature = Signature.getInstance("SHA256withRSA");
//        signature.initSign(privateKey);
//        signature.update(src.getBytes());
//        byte[] result = signature.sign();
    }

    private void a() {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//        keyPairGenerator.initialize(2048);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
//        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
//
////公钥加密
//        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
//        KeyFactory keyFactory= KeyFactory.getInstance("RSA");
//        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA256AndMGF1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        byte[] result = cipher.doFinal(src.getBytes());
//
////私钥解密
//        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
//        KeyFactory keyFactory2 = KeyFactory.getInstance("RSA");
//        PrivateKey privateKey = keyFactory2.generatePrivate(pkcs8EncodedKeySpec);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA256AndMGF1Padding");
//        cipher5.init(Cipher.DECRYPT_MODE, privateKey5);
//        byte[] result2 = cipher.doFinal(result);
    }
}
