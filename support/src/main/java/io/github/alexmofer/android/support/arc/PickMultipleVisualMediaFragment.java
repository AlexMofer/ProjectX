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

import java.util.List;
import java.util.function.Consumer;

import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 媒体多选选择器
 * Created by Alex on 2025/7/30.
 */
public abstract class PickMultipleVisualMediaFragment extends Fragment {
    private static Consumer<PickMultipleVisualMediaFragment> PHOTO_PICKER_UNAVAILABLE_CONSUMER = null;// 一般用于提示用户长按进行多选

    public static void setPhotoPickerUnavailableConsumer(@Nullable Consumer<PickMultipleVisualMediaFragment> consumer) {
        PHOTO_PICKER_UNAVAILABLE_CONSUMER = consumer;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(
                new ActivityResultContracts.PickMultipleVisualMedia(), this::handlePicked);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            onLaunch(launcher);
        }
    }

    @NonNull
    protected abstract PickVisualMediaRequest buildRequest();


    protected void onLaunch(@NonNull ActivityResultLauncher<PickVisualMediaRequest> launcher) {
        launcher.launch(buildRequest());
        if (PHOTO_PICKER_UNAVAILABLE_CONSUMER != null) {
            if (!ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(requireContext())) {
                PHOTO_PICKER_UNAVAILABLE_CONSUMER.accept(this);
            }
        }
    }

    private void handlePicked(@Nullable List<Uri> uris) {
        if (onPicked(uris)) {
            FragmentUtils.tryRemoveSelf(this);
        }
    }

    protected boolean onPicked(@Nullable List<Uri> uris) {
        final Callback callback = FragmentUtils.findCallback(this, Callback.class);
        if (callback != null) {
            callback.onPicked(this, uris);
        }
        return true;
    }

    public interface Callback {
        void onPicked(@NonNull PickMultipleVisualMediaFragment fragment, @Nullable List<Uri> uris);
    }

    public static final class ImageOnly extends PickMultipleVisualMediaFragment {

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

    public static final class VideoOnly extends PickMultipleVisualMediaFragment {

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

    public static final class ImageAndVideo extends PickMultipleVisualMediaFragment {

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