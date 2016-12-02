package am.project.x.activities.widgets.zxingscanview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.MaterialLoadingProgressDrawable;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;

import am.project.x.R;
import am.project.x.activities.BaseActivity;
import am.widget.zxingscanview.ZxingForegroundView;
import am.widget.zxingscanview.ZxingScanView;

/**
 * 条码扫描
 * Created by Alex on 2016/12/2.
 */

public class ZxingScanViewActivity extends BaseActivity implements ZxingScanView.OnScanListener {

    private static final int PERMISSIONS_REQUEST_CAMERA = 108;
    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);
    private ZxingScanView scanView;
    private ZxingForegroundView foregroundView;

    @Override
    protected int getContentViewLayoutResources() {
        return R.layout.activity_zxingscanview;
    }

    @Override
    protected void initResource(Bundle savedInstanceState) {
        setSupportActionBar(R.id.zxingscanview_toolbar);
        scanView = (ZxingScanView) findViewById(R.id.zxingscanview_zsv_scan);
        foregroundView = (ZxingForegroundView) findViewById(R.id.zxingscanview_zfv_foreground);
        scanView.addOnScanListener(this);
        foregroundView.setOpenDrawable(new MaterialLoadingProgressDrawable(foregroundView));


        scanView.addOnScanListener(new ZxingScanView.OnScanListener() {
            @Override
            public void onError(ZxingScanView scanView) {

            }

            @Override
            public void onResult(ZxingScanView scanView, Result result, Bitmap barcode, float scaleFactor) {

            }
        });
    }

    @Override
    public void onError(ZxingScanView scanView) {
        switch (scanView.getErrorCode()) {
            case ZxingScanView.ERROR_CODE_0:
                // 相机无法打开
                Toast.makeText(this, R.string.zxingscanview_error, Toast.LENGTH_SHORT).show();
                break;
            case ZxingScanView.ERROR_CODE_1:
                // 缺少打开相机的权限
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CAMERA);
                }
                break;
        }
    }

    @Override
    public void onResult(ZxingScanView scanView, Result result, Bitmap barcode,
                         float scaleFactor) {
        ParsedResult parsedResult = ResultParser.parseResult(result);
        final String format = "格式：" + result.getBarcodeFormat().toString();
        final String type = "类型：" + parsedResult.getType().toString();
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        final String date = "时间：" + formatter.format(new Date(result.getTimestamp()));
        String meta = "";
        Map<ResultMetadataType, Object> metadata = result.getResultMetadata();
        if (metadata != null) {
            StringBuilder metadataText = new StringBuilder(20);
            for (Map.Entry<ResultMetadataType, Object> entry : metadata.entrySet()) {
                if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
                    metadataText.append(entry.getValue()).append('\n');
                }
            }
            if (metadataText.length() > 0) {
                metadataText.setLength(metadataText.length() - 1);
                meta = metadataText.toString();
            }
        }
        CharSequence displayContents = parsedResult.getDisplayResult();
        Toast.makeText(this, format + "\n" + type + "\n" + date + "\n" + meta + "\n" + displayContents,
                Toast.LENGTH_SHORT).show();
        // 重新扫描
        scanView.restartScanDelay(3000);
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
                } else {
                    foregroundView.setMode(ZxingForegroundView.MODE_ERROR);
                }
            }
        }
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ZxingScanViewActivity.class));
    }
}
