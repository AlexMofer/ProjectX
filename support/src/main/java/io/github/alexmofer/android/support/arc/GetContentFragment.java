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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import io.github.alexmofer.android.support.utils.FragmentUtils;

/**
 * 文件单选选择器
 * Created by Alex on 2026/10/21.
 */
public abstract class GetContentFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        final ActivityResultLauncher<String> launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), this::handlePicked);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            onLaunch(launcher);
        }
    }

    protected void onLaunch(@NonNull ActivityResultLauncher<String> launcher) {
        launcher.launch(getTargetMIME());
    }

    @NonNull
    protected abstract String getTargetMIME();

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
        void onPicked(@NonNull GetContentFragment fragment, @Nullable Uri uri);
    }

    public static final class Anything extends GetContentFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    Anything.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    Anything.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "*/*";
        }
    }

    public static final class TextOnly extends GetContentFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    TextOnly.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    TextOnly.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "text/*";
        }
    }

    public static final class ImageOnly extends GetContentFragment {

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
        protected String getTargetMIME() {
            return "image/*";
        }
    }

    public static final class AudioOnly extends GetContentFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    AudioOnly.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    AudioOnly.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "audio/*";
        }
    }

    public static final class VideoOnly extends GetContentFragment {

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
        protected String getTargetMIME() {
            return "video/*";
        }
    }

    public static final class PDF extends GetContentFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    PDF.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    PDF.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/pdf";
        }
    }

    public static final class DOC extends GetContentFragment {

        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    DOC.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    DOC.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/msword";
        }
    }

    public static final class DOCX extends GetContentFragment {
        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    DOCX.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    DOCX.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
    }

    public static final class XLS extends GetContentFragment {
        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    XLS.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    XLS.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/vnd.ms-excel";
        }
    }

    public static final class XLSX extends GetContentFragment {
        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    XLSX.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    XLSX.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
    }

    public static final class PPT extends GetContentFragment {
        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    PPT.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    PPT.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/vnd.ms-powerpoint";
        }
    }

    public static final class PPTX extends GetContentFragment {
        public static <T extends Fragment & Callback> void launch(@NonNull T fragment) {
            FragmentUtils.add(fragment.getChildFragmentManager(),
                    PPTX.class, null);
        }

        public static <T extends FragmentActivity & Callback> void launch(@NonNull T activity) {
            FragmentUtils.add(activity.getSupportFragmentManager(),
                    PPTX.class, null);
        }

        @NonNull
        @Override
        protected String getTargetMIME() {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        }
    }
}
