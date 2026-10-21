/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.arc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;

import io.github.alexmofer.android.support.utils.FragmentUtils;
import io.github.alexmofer.android.support.utils.PermissionUtils;

/**
 * 图片拍摄器
 * Created by Alex on 2025/7/30.
 */
public abstract class TakePictureFragment extends Fragment {
    private static final String KEY_PATH = "path";
    private File mFile;
    private long mPermissionLaunchTimestamp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final Context context = requireContext();
        if (savedInstanceState == null) {
            mFile = newFile();
        } else {
            final String path = savedInstanceState.getString(KEY_PATH);
            if (path != null) {
                mFile = new File(path);
            }
        }
        final ActivityResultLauncher<Uri> launcher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), this::handleResult);
        super.onCreate(savedInstanceState);
        if (mFile == null) {
            FragmentUtils.tryRemoveSelf(this);
            return;
        }
        final Uri uri = toUri(mFile);
        if (PermissionUtils.hasSelfDeclarePermissions(context,
                android.Manifest.permission.CAMERA)) {
            // 有声明相机权限，需要获取权限后发起
            final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    value -> {
                        if (Boolean.TRUE.equals(value)) {
                            launcher.launch(uri);
                        } else {
                            handlePermissionDenied();
                        }
                    });
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED) {
                // 已有权限，直接发起
                if (savedInstanceState == null) {
                    launcher.launch(uri);
                }
            } else {
                // 没有权限，需要申请
                handlePermissionRequest(permissionLauncher);
            }
        } else {
            // 没有声明相机权限，不涉及权限，直接发起
            if (savedInstanceState == null) {
                launcher.launch(uri);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PATH, mFile == null ? null : mFile.getPath());
    }

    private void handleResult(@Nullable Boolean success) {
        if (mFile == null) {
            FragmentUtils.tryRemoveSelf(this);
            return;
        }
        if (onResult(mFile, success)) {
            FragmentUtils.tryRemoveSelf(this);
        }
    }

    @NonNull
    protected abstract File newFile();

    @NonNull
    protected abstract Uri toUri(@NonNull File file);

    protected void handlePermissionRequest(@NonNull ActivityResultLauncher<String> launcher) {
        // 可重写该方法根据 Activity 的 shouldShowRequestPermissionRationale 来决定是否跳转说明页面，该业务逻辑很清晰，说明页面没有必要。
        mPermissionLaunchTimestamp = System.currentTimeMillis();
        launcher.launch(android.Manifest.permission.CAMERA);
    }

    protected void handlePermissionDenied() {
        // 需要处理多次拒绝场景
        final long duration = System.currentTimeMillis() - mPermissionLaunchTimestamp;
        if (duration < 1000) {
            // 秒拒绝
            final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
            startActivity(intent);
        }
        FragmentUtils.tryRemoveSelf(this);
    }

    protected abstract boolean onResult(@NonNull File file, @Nullable Boolean success);
}