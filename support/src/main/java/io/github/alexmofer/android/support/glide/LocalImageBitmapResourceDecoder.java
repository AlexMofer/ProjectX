package io.github.alexmofer.android.support.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Util;

import java.io.IOException;

/**
 * 本地图片位图解码器
 * Created by Alex on 2026/1/5.
 */
final class LocalImageBitmapResourceDecoder
        implements ResourceDecoder<LocalImageBitmapAdapter, Bitmap> {

    @Override
    public boolean handles(@NonNull LocalImageBitmapAdapter source,
                           @NonNull Options options) throws IOException {
        return source.handles(options);
    }

    @Nullable
    @Override
    public Resource<Bitmap> decode(@NonNull LocalImageBitmapAdapter source,
                                   int width, int height, @NonNull Options options) throws IOException {
        final Bitmap bitmap = source.decode(width, height, options);
        return bitmap == null ? null : new BitmapResource(bitmap);
    }

    private static final class BitmapResource implements Resource<Bitmap> {

        private final Bitmap mBitmap;

        BitmapResource(@NonNull Bitmap bitmap) {
            mBitmap = bitmap;
        }

        @NonNull
        @Override
        public Class<Bitmap> getResourceClass() {
            return Bitmap.class;
        }

        @NonNull
        @Override
        public Bitmap get() {
            return mBitmap;
        }

        @Override
        public int getSize() {
            return Util.getBitmapByteSize(mBitmap);
        }

        @Override
        public void recycle() {
            mBitmap.recycle();
        }
    }
}
