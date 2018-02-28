package am.project.x.activities.other.security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.KeyPair;

import am.project.support.security.AESUtil;
import am.project.support.security.DESedeUtil;
import am.project.support.security.MessageDigestUtils;
import am.project.support.security.RSAUtil;
import am.project.support.utils.InputMethodUtils;
import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CipherActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtInput;
    private TextView tvOutput;
    private AlertDialog dlgCipher;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CipherActivity.class));
    }

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_cipher;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.cipher_toolbar);
        edtInput = findViewById(R.id.cipher_edt_input);
        tvOutput = findViewById(R.id.cipher_tv_output);
        findViewById(R.id.cipher_btn_message).setOnClickListener(this);
        findViewById(R.id.cipher_btn_des).setOnClickListener(this);
        findViewById(R.id.cipher_btn_aes).setOnClickListener(this);
        findViewById(R.id.cipher_btn_rsa).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String input = edtInput.getText().toString();
        if (input.length() <= 0) {
            InputMethodUtils.openInputMethod(edtInput);
            return;
        }
        InputMethodUtils.closeInputMethod(edtInput);
        switch (view.getId()) {
            case R.id.cipher_btn_message:
                new CipherTask(this, CipherTask.MODE_MESSAGE).execute(input);
                break;
            case R.id.cipher_btn_des:
                new CipherTask(this, CipherTask.MODE_DES).execute(input);
                break;
            case R.id.cipher_btn_aes:
                new CipherTask(this, CipherTask.MODE_AES).execute(input);
                break;
            case R.id.cipher_btn_rsa:
                new CipherTask(this, CipherTask.MODE_RSA).execute(input);
                break;
        }
    }

    // TODO 修改为Job
    private static class CipherTask extends AsyncTask<String, Void, StringBuffer>
            implements DialogInterface.OnCancelListener {

        static final int MODE_MESSAGE = 0;
        static final int MODE_DES = 1;
        static final int MODE_AES = 2;
        static final int MODE_RSA = 3;
        @SuppressWarnings("all")
        private final CipherActivity mCallback;
        private final int mode;

        CipherTask(CipherActivity callback, int mode) {
            this.mCallback = callback;
            this.mode = mode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallback.dlgCipher == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCallback);
                builder.setMessage(R.string.cipher_dlg_message);
                mCallback.dlgCipher = builder.create();
            }
            mCallback.dlgCipher.setOnCancelListener(this);
            mCallback.dlgCipher.show();
        }

        @Override
        protected StringBuffer doInBackground(String... strings) {
            if (strings == null || strings.length <= 0)
                return null;
            String input = strings[0];
            switch (mode) {
                case MODE_MESSAGE:
                    return getMessage(input);
                case MODE_DES:
                    return getDES(input);
                case MODE_AES:
                    return getAES(input);
                case MODE_RSA:
                    return getRSA(input);
            }
            return null;
        }

        private StringBuffer getMessage(String text) {
            StringBuffer buffer = new StringBuffer();

            getMD5(buffer, text);

            getSHA1(buffer, text);

            getSHA224(buffer, text);

            getSHA256(buffer, text);

            getSHA384(buffer, text);

            getSHA512(buffer, text);

            return buffer;
        }

        private void getMD5(StringBuffer buffer, String text) {
            buffer.append("MD5：");
            buffer.append(MessageDigestUtils.getMD5String(text));
            buffer.append("\n");
            buffer.append("\n");
        }

        private void getSHA1(StringBuffer buffer, String text) {
            buffer.append("SHA-1：");
            buffer.append(MessageDigestUtils.getSHA1String(text.getBytes()));
            buffer.append("\n");
            buffer.append("\n");
        }

        @SuppressWarnings("all")
        private void getSHA224(StringBuffer buffer, String text) {
            buffer.append("SHA-224：");
            if (Build.VERSION.SDK_INT > 8 && Build.VERSION.SDK_INT < 22) {
                buffer.append("SHA-224 Supported API Levels 1-8,22+");
            } else {
                buffer.append(MessageDigestUtils.getSHA224String(text.getBytes()));
            }
            buffer.append("\n");
            buffer.append("\n");
        }

        private void getSHA256(StringBuffer buffer, String text) {
            buffer.append("SHA-256：");
            buffer.append(MessageDigestUtils.getSHA256String(text.getBytes()));
            buffer.append("\n");
            buffer.append("\n");
        }

        private void getSHA384(StringBuffer buffer, String text) {
            buffer.append("SHA-384：");
            buffer.append(MessageDigestUtils.getSHA384String(text.getBytes()));
            buffer.append("\n");
            buffer.append("\n");
        }

        private void getSHA512(StringBuffer buffer, String text) {
            buffer.append("SHA-512：");
            buffer.append(MessageDigestUtils.getSHA512String(text.getBytes()));
            buffer.append("\n");
        }

        private StringBuffer getDES(String text) {
            StringBuffer buffer = new StringBuffer();

            doDESede(buffer, text);

            doDESedeWithRandomKey(buffer, text);

            doDESedeWithPBEKey(buffer, text);

            return buffer;
        }

        private StringBuffer getAES(String text) {
            StringBuffer buffer = new StringBuffer();

            doAES(buffer, text);

            doAESWithRandomKey(buffer, text);

            doAESWithPBEKey(buffer, text);

            return buffer;
        }

        private StringBuffer getRSA(String text) {
            StringBuffer buffer = new StringBuffer();

            doRSA(buffer, text);

            return buffer;
        }

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

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mCallback.dlgCipher != null && mCallback.dlgCipher.isShowing())
                mCallback.dlgCipher.dismiss();
        }

        @Override
        protected void onPostExecute(StringBuffer stringBuffer) {
            super.onPostExecute(stringBuffer);
            if (mCallback.dlgCipher != null && mCallback.dlgCipher.isShowing())
                mCallback.dlgCipher.dismiss();
            mCallback.tvOutput.setText(stringBuffer);
        }

        @Override
        public void onCancel(DialogInterface dialogInterface) {
            cancel(true);
        }
    }
}
