package io.github.alexmofer.android.support.media;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 图片裁剪Contracts
 * Created by Alex on 2023/4/18.
 */
public class ImageCropContracts {

    private ImageCropContracts() {
        //no instance
    }

    public static class Request {
        private final Uri mInput;
        private final Uri mOutput;

        public Request(@NonNull Uri input, @NonNull Uri output) {
            mInput = input;
            mOutput = output;
        }
    }

    /**
     * 裁剪
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static class CropImage extends ActivityResultContract<Request, Integer> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Request request) {
            // TODO: Use a public intent, when there is one.
            final Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(request.mInput, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, request.mOutput);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setClipData(ClipData.newRawUri(MediaStore.EXTRA_OUTPUT, request.mOutput));
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("return-data", false);
            return intent;
        }

        @Override
        public Integer parseResult(int resultCode, @Nullable Intent intent) {
            return resultCode;
        }
    }
}
