package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class TestActivity extends BaseActivity {


    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_test;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);
    }

    public void show(View view) {
        Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show();
        findViewById(R.id.temp).setVisibility(View.VISIBLE);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }
}
