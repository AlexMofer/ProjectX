package am.project.x.activities.util.security;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyPair;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.project.x.utils.ImmUtils;
import am.util.security.AESUtil;
import am.util.security.DESedeUtil;
import am.util.security.MessageDigestUtils;
import am.util.security.RSAUtil;

public class CipherActivity extends BaseActivity implements View.OnClickListener{

    private EditText edtInput;
    private TextView tvOutput;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_cipher;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.cipher_toolbar);
        edtInput = (EditText) findViewById(R.id.cipher_edt_input);
        tvOutput = (TextView) findViewById(R.id.cipher_tv_output);
        findViewById(R.id.cipher_btn_message).setOnClickListener(this);
        findViewById(R.id.cipher_btn_des).setOnClickListener(this);
        findViewById(R.id.cipher_btn_aes).setOnClickListener(this);
        findViewById(R.id.cipher_btn_rsa).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String input = edtInput.getText().toString();
        if (input.length() <= 0) {
            edtInput.requestFocus();
            edtInput.requestFocusFromTouch();
            ImmUtils.showImm(this, edtInput);
            return;
        }
        ImmUtils.closeImm(this, edtInput);
        edtInput.clearFocus();
        switch (view.getId()) {
            case R.id.cipher_btn_message:
                getMessage(input);
                break;
            case R.id.cipher_btn_des:
                getDES(input);
                break;
            case R.id.cipher_btn_aes:
                getAES(input);
                break;
            case R.id.cipher_btn_rsa:
                getRSA(input);
                break;
        }
    }

    private void getMessage(String text) {
        StringBuffer buffer = new StringBuffer();

        getMD5(buffer, text);

        getSHA256(buffer, text);

        tvOutput.setText(buffer);
    }

    private void getDES(String text) {
        StringBuffer buffer = new StringBuffer();

        doDESede(buffer, text);

        doDESedeWithRandomKey(buffer, text);

        doDESedeWithPBEKey(buffer, text);

        tvOutput.setText(buffer);
    }

    private void getAES(String text) {
        StringBuffer buffer = new StringBuffer();

        doAES(buffer, text);

        doAESWithRandomKey(buffer, text);

        doAESWithPBEKey(buffer, text);

        tvOutput.setText(buffer);
    }

    private void getRSA(String text) {
        StringBuffer buffer = new StringBuffer();

        doRSA(buffer, text);

        tvOutput.setText(buffer);
    }

    private void getMD5(StringBuffer buffer, String text) {
        buffer.append("MD5：");
        buffer.append(MessageDigestUtils.getMD5(text.getBytes()));
        buffer.append("\n");
    }

    private void getSHA256(StringBuffer buffer, String text) {
        buffer.append("SHA-256：");
        buffer.append(MessageDigestUtils.getSHA256(text.getBytes()));
        buffer.append("\n");
    }

    /**
     * DESede
     */
    private void doDESede(StringBuffer buffer, String text) {
        buffer.append("DESede：\n");
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

    private void doDESedeWithRandomKey(StringBuffer buffer, String text) {
        buffer.append("\n");
        buffer.append("DESedeWithRandomKey：\n");
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

    private void doDESedeWithPBEKey(StringBuffer buffer, String text) {
        buffer.append("\n");
        buffer.append("DESedeWithPBEKey：\n");
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

    private void doAES(StringBuffer buffer, String text) {
        buffer.append("\n");
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

    private void doRSA(StringBuffer buffer, String text) {
        buffer.append("\n");
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

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CipherActivity.class));
    }
}
