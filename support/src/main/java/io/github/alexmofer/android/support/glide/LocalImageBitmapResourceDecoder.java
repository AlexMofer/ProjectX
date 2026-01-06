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
        final Bitmap bitmap = source.load(width, height, options);
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
