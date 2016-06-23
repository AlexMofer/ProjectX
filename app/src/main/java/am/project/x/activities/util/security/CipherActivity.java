package am.project.x.activities.util.security;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class CipherActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_cipher;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.cipher_toolbar);

    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CipherActivity.class));
    }
}
