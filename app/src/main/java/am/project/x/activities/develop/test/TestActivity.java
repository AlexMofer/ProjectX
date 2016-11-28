package am.project.x.activities.develop.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.widget.ZxingScanView;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class TestActivity extends BaseActivity {

    private ZxingScanView scanView;
    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_test;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);
        scanView = (ZxingScanView) findViewById(R.id.test_zsv_scan);
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    public static void startCaptureActivity(Context context) {
        context.startActivity(new Intent(context, CaptureActivity.class));
    }
}
