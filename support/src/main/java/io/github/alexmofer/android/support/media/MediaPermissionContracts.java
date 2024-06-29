package io.github.alexmofer.android.support.media;

import android.Manifest;
import android.os.Build;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * 媒体权限Contracts
 * Created by Alex on 2023/4/12.
 */
public class MediaPermissionContracts {

    private MediaPermissionContracts() {
        //no instance
    }

    /**
     * 注册图片权限
     *
     * @param fragment Fragment
     * @param callback 回调
     * @return 启动器
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static MediaPermissionLauncher registerImagePermission(Fragment fragment,
                                                                  MediaPermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new ImagePermissionLauncher33(
                    fragment.registerForActivityResult(
                            new ActivityResultContracts.RequestPermission(),
                            new PermissionCallbackSingle(callback)));
        } else {
            return new ReadPermissionLauncher(
                    fragment.registerForActivityResult(
                            new ActivityResultContracts.RequestPermission(),
                            new PermissionCallbackSingle(callback)));
        }

    }

    /**
     * 注册图片权限
     *
     * @param activity FragmentActivity
     * @param callback 回调
     * @return 启动器
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static MediaPermissionLauncher registerImagePermission(FragmentActivity activity,
                                                                  MediaPermissionCallback callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new ImagePermissionLauncher33(
                    activity.registerForActivityResult(
                            new ActivityResultContracts.RequestPermission(),
                            new PermissionCallbackSingle(callback)));
        } else {
            return new ReadPermissionLauncher(
                    activity.registerForActivityResult(
                            new ActivityResultContracts.RequestPermission(),
                            new PermissionCallbackSingle(callback)));
        }
    }

    /**
     * 媒体权限启动器
     */
    public interface MediaPermissionLauncher {

        /**
         * 请求权限
         */
        void launch();

        /**
         * 注销启动器
         */
        @MainThread
        void unregister();
    }

    /**
     * 回调
     */
    public interface MediaPermissionCallback {

        /**
         * 媒体权限结果
         *
         * @param isGranted 是否已授权
         */
        void onMediaPermissionResult(boolean isGranted);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static class ImagePermissionLauncher33 implements MediaPermissionLauncher {

        private final ActivityResultLauncher<String> mLauncher;

        public ImagePermissionLauncher33(ActivityResultLauncher<String> launcher) {
            mLauncher = launcher;
        }

        @Override
        public void launch() {
            mLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }

        @Override
        public void unregister() {
            mLauncher.unregister();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private static class ReadPermissionLauncher implements MediaPermissionLauncher {

        private final ActivityResultLauncher<String> mLauncher;

        public ReadPermissionLauncher(ActivityResultLauncher<String> launcher) {
            mLauncher = launcher;
        }

        @Override
        public void launch() {
            mLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public void unregister() {
            mLauncher.unregister();
        }
    }

    private static class PermissionCallbackSingle implements ActivityResultCallback<Boolean> {

        private final MediaPermissionCallback mCallback;

        public PermissionCallbackSingle(MediaPermissionCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onActivityResult(Boolean result) {
            mCallback.onMediaPermissionResult(result != null && result);
        }
    }
}
