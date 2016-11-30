package am.project.x.activities.develop.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.MaterialLoadingProgressDrawable;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.ZxingForegroundView;
import com.google.zxing.client.android.ZxingScanView;

import am.project.x.R;
import am.project.x.activities.BaseActivity;

public class TestActivity extends BaseActivity implements ZxingScanView.OnScanListener {

    private static final int PERMISSIONS_REQUEST_CAMERA = 108;
    private ZxingScanView scanView;
    private ZxingForegroundView foregroundView;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_test;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.test_toolbar);
        scanView = (ZxingScanView) findViewById(R.id.test_zsv_scan);
        foregroundView = (ZxingForegroundView) findViewById(R.id.test_zfv_foreground);
        scanView.addOnScanListener(this);
        foregroundView.setOpenDrawable(new MaterialLoadingProgressDrawable(foregroundView));
    }

    @Override
    public void onError(ZxingScanView scanView) {
        switch (scanView.getErrorCode()) {
            case ZxingScanView.ERROR_CODE_0:
                // 相机无法打开
                System.out.println("很遗憾，Android 相机出现问题。你可能需要重启设备。");
                break;
            case ZxingScanView.ERROR_CODE_1:
                // 缺少打开相机的权限
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
//                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanView.open();
                }
            }
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, TestActivity.class));
    }

    public static void startCaptureActivity(Context context) {
        context.startActivity(new Intent(context, CaptureActivity.class));
    }
}
