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

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 授权路径
 * Created by Alex on 2026/10/21.
 */
public class OpenDocumentTreeFragment extends Fragment {
    private static final String ARG_URI = "uri";

    @Nullable
    private static Bundle newArguments(@Nullable Uri uri) {
        if (uri == null) {
            return null;
        }
        final Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);
        return args;
    }

    public static <T extends Fragment & Callback> void launch(@NonNull T fragment,
                                                              @Nullable Uri uri) {
        FragmentUtils.add(fragment.getChildFragmentManager(),
                OpenDocumentTreeFragment.class, newArguments(uri));
    }

    public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity,
                                                                      @Nullable Uri uri) {
        FragmentUtils.add(activity.getSupportFragmentManager(),
                OpenDocumentTreeFragment.class, newArguments(uri));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final ActivityResultLauncher<Uri> launcher = registerForActivityResult(
                new ActivityResultContracts.OpenDocumentTree(), this::handlePicked);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            onLaunch(launcher);
        }
    }

    protected void onLaunch(@NonNull ActivityResultLauncher<Uri> launcher) {
        launcher.launch(getTargetUri());
    }

    @Nullable
    protected Uri getTargetUri() {
        final Bundle args = getArguments();
        return args == null ? null : BundleCompat.getParcelable(args, ARG_URI, Uri.class);
    }

    private void handlePicked(@Nullable Uri uri) {
        if (onPicked(uri)) {
            FragmentUtils.tryRemoveSelf(this);
        }
    }

    protected boolean onPicked(@Nullable Uri uri) {
        final Callback callback = FragmentUtils.findCallback(this, Callback.class);
        if (callback != null) {
            callback.onPicked(this, uri);
        }
        return true;
    }

    public interface Callback {
        void onPicked(@NonNull OpenDocumentTreeFragment fragment, @Nullable Uri uri);
    }
}
