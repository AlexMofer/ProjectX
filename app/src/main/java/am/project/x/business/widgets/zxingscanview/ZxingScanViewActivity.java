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
package am.project.x.business.widgets.zxingscanview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import am.project.x.base.BaseActivity;
import am.widget.zxingscanview.ZxingForegroundView;
import am.widget.zxingscanview.ZxingScanView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

/**
 * 条码扫描
 */
public class ZxingScanViewActivity extends BaseActivity implements ZxingScanView.OnScanListener {

    private static final int PERMISSIONS_REQUEST_CAMERA = 108;
    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);
    private ZxingScanView mVScan;
    private ZxingForegroundView mVForeground;

    public static void start(Context context) {
        context.startActivity(new Intent(context, ZxingScanViewActivity.class));
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_zxingscanview;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.zsv_toolbar);
        mVScan = findViewById(R.id.zsv_zsv_scan);
        mVForeground = findViewById(R.id.zsv_zfv_foreground);
        mVScan.addOnScanListener(this);
    }

    // Listener
    @Override
    public void onError(ZxingScanView scanView) {
        switch (scanView.getErrorCode()) {
            case ZxingScanView.ERROR_CODE_0:
                // 相机无法打开
                Toast.makeText(this, R.string.zsv_error, Toast.LENGTH_SHORT).show();
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
        final String format = "format:" + result.getBarcodeFormat().toString();
        final String type = "type:" + parsedResult.getType().toString();
        DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        final String date = "date:" + formatter.format(new Date(result.getTimestamp()));
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
                    mVScan.open();
                } else {
                    mVForeground.setMode(ZxingForegroundView.MODE_ERROR);
                }
            }
        }
    }
}
