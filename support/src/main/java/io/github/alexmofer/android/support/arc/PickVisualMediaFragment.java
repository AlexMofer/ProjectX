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
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 媒体单选选择器
 * Created by Alex on 2025/7/30.
 */
public abstract class PickVisualMediaFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(), this::handlePicked);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            launcher.launch(buildRequest());
        }
    }

    @NonNull
    protected abstract PickVisualMediaRequest buildRequest();

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
        void onPicked(@NonNull PickVisualMediaFragment fragment, @Nullable Uri uri);
    }

    public static final class ImageOnly extends PickVisualMediaFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    ImageOnly.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    ImageOnly.class, null);
        }

        @NonNull
        @Override
        protected PickVisualMediaRequest buildRequest() {
            return PickVisualMediaRequestHelper.newImageOnly();
        }
    }

    public static final class VideoOnly extends PickVisualMediaFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    VideoOnly.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    VideoOnly.class, null);
        }

        @NonNull
        @Override
        protected PickVisualMediaRequest buildRequest() {
            return PickVisualMediaRequestHelper.newVideoOnly();
        }
    }

    public static final class ImageAndVideo extends PickVisualMediaFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    ImageAndVideo.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    ImageAndVideo.class, null);
        }

        @NonNull
        @Override
        protected PickVisualMediaRequest buildRequest() {
            return PickVisualMediaRequestHelper.newImageAndVideo();
        }
    }
}