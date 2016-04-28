package com.am.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.am.security.AESUtil;
import com.am.security.DESUtil;
import com.am.utils.ImmUtils;
import com.am.security.MessageDigestUtils;
import com.am.widget.R;

public class CipherActivity extends Activity implements View.OnClickListener {

    private EditText edtText;
    private EditText edtKey;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cipher);
        edtText = (EditText) findViewById(R.id.cipher_edt_text);
        edtKey = (EditText) findViewById(R.id.cipher_edt_key);
        tvInfo = (TextView) findViewById(R.id.cipher_tv_info);
        findViewById(R.id.cipher_btn_cipher).setOnClickListener(this);
        edtKey.setText("e8ffc7e56311679f12b6fc91aa77a5eb");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cipher_btn_cipher:
                getResult();
                break;
        }
    }

    private void getResult() {
        final String text = edtText.getText().toString().trim();
        if (text.length() <= 0) {
            Toast.makeText(getApplicationContext(), "输入文本串", Toast.LENGTH_SHORT).show();
            edtText.requestFocus();
            ImmUtils.showImm(getApplicationContext(), edtText);
            return;
        }
        final String key = edtKey.getText().toString().trim();
        if (key.length() <= 0) {
            Toast.makeText(getApplicationContext(), "输入密钥串", Toast.LENGTH_SHORT).show();
            edtKey.requestFocus();
            ImmUtils.showImm(getApplicationContext(), edtKey);
            return;
        }
        ImmUtils.closeImm(this);
        String info = "";

        info += "输入串：" + text + "\n";
        info += "密钥串：" + key + "\n";
        info += "输入串MD5：" + MessageDigestUtils.getMD5(text.getBytes()) + "\n";
        info += "输入串SHA-256：" + MessageDigestUtils.getSHA256(text.getBytes()) + "\n";

        byte[] cipher;
        byte[] result;
        // 3DES
        try {
            cipher = DESUtil.encrypt(key.getBytes(), text.getBytes());
        } catch (Exception e) {
            cipher = null;
        }
        if (cipher == null) {
            info += "DES加密：" + "加密失败,Key 需要超过24位" + "\n";
            info += "DES解密：" + "加密失败" + "\n";
        } else {
            info += "DES加密：" + Base64.encodeToString(cipher, Base64.DEFAULT) + "\n";
            try {
                result = DESUtil.decrypt(key.getBytes(), cipher);
            } catch (Exception e) {
                result = null;
            }
            if (result == null) {
                info += "DES解密：" + "解密失败" + "\n";
            } else {
                info += "DES解密：" + new String(result) + "\n";
            }
        }
        // AES + 随机数种子
        cipher = AESUtil.encryptWithRandomKey(key.getBytes(), text.getBytes());
        if (cipher == null) {
            info += "AES随机数种子加密：" + "加密失败" + "\n";
            info += "AES随机数种子解密：" + "加密失败" + "\n";
        } else {
            info += "AES随机数种子加密：" + Base64.encodeToString(cipher, Base64.DEFAULT) + "\n";
            result = AESUtil.decryptWithRandomKey(key.getBytes(), cipher);
            if (result == null) {
                info += "AES随机数种子解密：" + "解密失败" + "\n";
            } else {
                info += "AES随机数种子解密：" + new String(result) + "\n";
            }
        }

        // AES + PBE口令
        byte[] salt = key.getBytes();// 自行定义盐
        int iteration = 2048;// 迭代次数

        cipher = AESUtil.encryptWithPBEKey(key.toCharArray(), salt, iteration, text.getBytes());
        if (cipher == null) {
            info += "AES PBE口令加密：" + "加密失败" + "\n";
            info += "AES PBE口令解密：" + "加密失败" + "\n";
        } else {
            info += "AES PBE口令加密：" + Base64.encodeToString(cipher, Base64.DEFAULT) + "\n";
            result = AESUtil.decryptWithPBEKey(key.toCharArray(), salt, iteration, cipher);
            if (result == null) {
                info += "AES PBE口令解密：" + "解密失败" + "\n";
            } else {
                info += "AES PBE口令解密：" + new String(result) + "\n";
            }
        }

        tvInfo.setText(info);
    }
}
