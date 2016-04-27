package com.am.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.am.utils.AESUtil;
import com.am.utils.BytesUtil;
import com.am.utils.DES3Util;
import com.am.utils.ImmUtils;
import com.am.widget.R;

public class CipherActivity extends Activity implements View.OnClickListener{

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
        String cipher, result;
        // 3DES
        try {
            cipher = DES3Util.encrypt(text, key);
        } catch (Exception e) {
            cipher = null;
        }
        if (cipher == null) {
            info += "3DES加密密文串：" + "加密失败" + "\n";
            info += "3DES解密明文串：" + "加密失败" + "\n";
        } else {
            info += "3DES加密密文串：" + cipher + "\n";
            try {
                result = DES3Util.decrypt(cipher, key);
            } catch (Exception e) {
                result = null;
            }
            if (result == null) {
                info += "3DES解密明文串：" + "解密失败" + "\n";
            } else {
                info += "3DES解密明文串：" + result + "\n";
            }
        }
        // AES + 16进制编码
        try {
            cipher = AESUtil.encryptToHexString(text, key, AESUtil.ENCODING);
        } catch (Exception e) {
            cipher = null;
        }
        if (cipher == null) {
            info += "AES加密16进制编码密文串：" + "加密失败" + "\n";
            info += "AES解密明文串：" + "加密失败" + "\n";
        } else {
            info += "AES加密16进制编码密文串：" + cipher + "\n";
            try {
                result = AESUtil.decryptFromHexString(cipher, key, AESUtil.ENCODING);
            } catch (Exception e) {
                result = null;
            }
            if (result == null) {
                info += "AES解密明文串：" + "解密失败" + "\n";
            } else {
                info += "AES解密明文串：" + result + "\n";
            }
        }
        // AES + Base64进制编码
        try {
            cipher = AESUtil.encryptToBase64(text, key, AESUtil.ENCODING);
        } catch (Exception e) {
            cipher = null;
        }
        if (cipher == null) {
            info += "AES加密Base64编码密文串：" + "加密失败" + "\n";
            info += "AES解密明文串：" + "加密失败" + "\n";
        } else {
            info += "AES加密Base64编码密文串：" + cipher + "\n";
            System.out.println(cipher);
            System.out.println(key);
            try {
                result = AESUtil.decryptFromBase64(cipher, key, AESUtil.ENCODING);
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
            if (result == null) {
                info += "AES解密明文串：" + "解密失败" + "\n";
            } else {
                info += "AES解密明文串：" + result + "\n";
            }
        }

        byte[] data = text.getBytes();
        String hex = BytesUtil.bytesToHexString(data);
        System.out.println(hex);
        System.out.println(new String(BytesUtil.hexStringToBytes(hex)));

        tvInfo.setText(info);
    }
}
