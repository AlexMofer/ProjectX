/*
 * Copyright (C) 2018 AlexMofer
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
package io.github.alexmofer.projectx.business.others.crypto;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.am.job.Job;
import com.am.tool.support.security.AESUtil;
import com.am.tool.support.security.DESedeUtil;
import com.am.tool.support.security.MessageDigestUtils;
import com.am.tool.support.security.RSAUtil;

import java.security.KeyPair;

/**
 * Job
 */
class CryptoJob extends Job<CryptoJob.Callback> {

    private static final int ID_MESSAGE = 0;
    private static final int ID_DES = 1;
    private static final int ID_AES = 2;
    private static final int ID_RSA = 3;

    private CryptoJob(Callback callback, int id, Object... params) {
        super(callback, id, params);
    }

    static void getMessage(Callback callback, String input) {
        new CryptoJob(callback, ID_MESSAGE, input).execute();
    }

    static void getDES(Callback callback, String input) {
        new CryptoJob(callback, ID_DES, input).execute();
    }

    static void getAES(Callback callback, String input) {
        new CryptoJob(callback, ID_AES, input).execute();
    }

    static void getRSA(Callback callback, String input) {
        new CryptoJob(callback, ID_RSA, input).execute();
    }

    @Override
    protected void doInBackground(Result result) {
        switch (getId()) {
            case ID_MESSAGE:
                handleActionMessage(result);
                break;
            case ID_DES:
                handleActionDES(result);
                break;
            case ID_AES:
                handleActionAES(result);
                break;
            case ID_RSA:
                handleActionRSA(result);
                break;
        }
    }

    private void handleActionMessage(@NonNull Result result) {
        final String input = getParams().get(0);
        final StringBuffer buffer = new StringBuffer();
        getMD5(buffer, input);
        getSHA1(buffer, input);
        getSHA224(buffer, input);
        getSHA256(buffer, input);
        getSHA384(buffer, input);
        getSHA512(buffer, input);
        result.set(true, buffer.toString());
    }

    private void getMD5(StringBuffer buffer, String text) {
        buffer.append("MD5：");
        buffer.append(MessageDigestUtils.getMD5String(text));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void getSHA1(StringBuffer buffer, String text) {
        buffer.append("SHA-1：");
        buffer.append(MessageDigestUtils.getSHA1String(text));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void getSHA224(StringBuffer buffer, String text) {
        buffer.append("SHA-224：");
        if (Build.VERSION.SDK_INT < 22) {
            buffer.append("SHA-224 Supported API Levels 1-8,22+");
        } else {
            buffer.append(MessageDigestUtils.getSHA224String(text));
        }
        buffer.append("\n");
        buffer.append("\n");
    }

    private void getSHA256(StringBuffer buffer, String text) {
        buffer.append("SHA-256：");
        buffer.append(MessageDigestUtils.getSHA256String(text));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void getSHA384(StringBuffer buffer, String text) {
        buffer.append("SHA-384：");
        buffer.append(MessageDigestUtils.getSHA384String(text));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void getSHA512(StringBuffer buffer, String text) {
        buffer.append("SHA-512：");
        buffer.append(MessageDigestUtils.getSHA512String(text));
        buffer.append("\n");
    }

    private void handleActionDES(@NonNull Result result) {
        final String input = getParams().get(0);
        final StringBuffer buffer = new StringBuffer();
        doDES(buffer, input);
        doDESWithRandomKey(buffer, input);
        doDESWithPBEKey(buffer, input);
        result.set(true, buffer.toString());
    }

    private void doDES(StringBuffer buffer, String text) {
        buffer.append("DES：\n");
        byte[] cipher;
        byte[] result;
        byte[] key;
        try {
            key = DESedeUtil.generateKey();
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("KEY：");
        buffer.append(Base64.encodeToString(key, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = DESedeUtil.encrypt(key, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = DESedeUtil.decrypt(key, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void doDESWithRandomKey(StringBuffer buffer, String text) {
        buffer.append("\n");
        buffer.append("DESWithRandomKey：\n");
        String seed = "this is my seed";
        buffer.append("SEED：");
        buffer.append(seed);
        buffer.append("\n");
        byte[] cipher;
        byte[] result;
        byte[] key;
        try {
            key = DESedeUtil.getRandomKey(seed.getBytes());
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("KEY：");
        buffer.append(Base64.encodeToString(key, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = DESedeUtil.encrypt(key, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = DESedeUtil.decrypt(key, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
    }

    private void doDESWithPBEKey(StringBuffer buffer, String text) {
        buffer.append("\n");
        buffer.append("DESWithPBEKey：\n");
        String password = "this is my password";
        buffer.append("PASSWORD：");
        buffer.append(password);
        buffer.append("\n");
        String salt = "this is my salt, it should be very long.";
        buffer.append("SALT：");
        buffer.append(salt);
        buffer.append("\n");
        byte[] cipher;
        byte[] result;
        byte[] key;
        try {
            key = DESedeUtil.getPBEKey(password.toCharArray(), salt.getBytes());
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("KEY：");
        buffer.append(Base64.encodeToString(key, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = DESedeUtil.encrypt(key, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            e.printStackTrace();
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = DESedeUtil.decrypt(key, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
    }

    private void handleActionAES(@NonNull Result result) {
        final String input = getParams().get(0);
        final StringBuffer buffer = new StringBuffer();
        doAES(buffer, input);
        doAESWithRandomKey(buffer, input);
        doAESWithPBEKey(buffer, input);
        result.set(true, buffer.toString());
    }

    private void doAES(StringBuffer buffer, String text) {
        buffer.append("AES：\n");
        byte[] cipher;
        byte[] result;
        byte[] key;
        try {
            key = AESUtil.generateKey();
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("KEY：");
        buffer.append(Base64.encodeToString(key, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = AESUtil.encrypt(key, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = AESUtil.decrypt(key, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void doAESWithRandomKey(StringBuffer buffer, String text) {
        buffer.append("\n");
        buffer.append("AESWithRandomKey：\n");
        String seed = "this is my seed";
        buffer.append("SEED：");
        buffer.append(seed);
        buffer.append("\n");
        byte[] cipher;
        byte[] result;
        byte[] key;
        try {
            key = AESUtil.getRandomKey(seed.getBytes());
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("KEY：");
        buffer.append(Base64.encodeToString(key, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = AESUtil.encrypt(key, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = AESUtil.decrypt(key, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void doAESWithPBEKey(StringBuffer buffer, String text) {
        buffer.append("\n");
        buffer.append("AESWithPBEKey：\n");
        String password = "this is my password";
        buffer.append("PASSWORD：");
        buffer.append(password);
        buffer.append("\n");
        String salt = "this is my salt, it should be very long.";
        buffer.append("SALT：");
        buffer.append(salt);
        buffer.append("\n");
        byte[] cipher;
        byte[] result;
        byte[] key;
        try {
            key = AESUtil.getPBEKey(password.toCharArray(), salt.getBytes());
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("KEY：");
        buffer.append(Base64.encodeToString(key, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = AESUtil.encrypt(key, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = AESUtil.decrypt(key, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
        buffer.append("\n");
    }

    private void handleActionRSA(@NonNull Result result) {
        final String input = getParams().get(0);
        final StringBuffer buffer = new StringBuffer();
        doRSA(buffer, input);
        result.set(true, buffer.toString());
    }

    private void doRSA(StringBuffer buffer, String text) {
        buffer.append("RSA：\n");
        byte[] cipher;
        byte[] result;
        byte[] privateKey;
        byte[] publicKey;
        try {
            KeyPair keyPair = RSAUtil.generateKeyPair();
            privateKey = keyPair.getPrivate().getEncoded();
            publicKey = keyPair.getPublic().getEncoded();
        } catch (Exception e) {
            buffer.append("KEY：failure.\n");
            return;
        }
        buffer.append("PRIVATE KEY：");
        buffer.append(Base64.encodeToString(privateKey, Base64.DEFAULT));
        buffer.append("\n");
        buffer.append("PUBLIC KEY：");
        buffer.append(Base64.encodeToString(publicKey, Base64.DEFAULT));
        buffer.append("\n");
        try {
            cipher = RSAUtil.encryptByPublicKey(publicKey, text.getBytes());
        } catch (Exception e) {
            buffer.append("ENCRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(Base64.encodeToString(cipher, Base64.DEFAULT));
        buffer.append("\n");
        try {
            result = RSAUtil.decryptByPrivateKey(privateKey, cipher);
        } catch (Exception e) {
            buffer.append("DECRYPT：failure.\n");
            return;
        }
        buffer.append("ENCRYPT：");
        buffer.append(new String(result));
        buffer.append("\n");
        buffer.append("\n");
    }

    @Override
    protected void onResult(@NonNull Callback callback, @NonNull Result result) {
        super.onResult(callback, result);
        callback.onResult(result.get(0));
    }

    public interface Callback {
        /**
         * 输出计算结果
         *
         * @param output 输出
         */
        void onResult(String output);
    }
}
