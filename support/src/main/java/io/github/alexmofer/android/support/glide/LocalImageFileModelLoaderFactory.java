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

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import io.github.alexmofer.android.support.utils.FileUtils;

/**
 * 本地图片文件模型加载器工厂工厂
 * Created by Alex on 2026/1/5.
 */
final class LocalImageFileModelLoaderFactory
        implements ModelLoaderFactory<LocalImageFileAdapter, InputStream> {

    @NonNull
    @Override
    public ModelLoader<LocalImageFileAdapter, InputStream> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new FileModelLoader();
    }

    @Override
    public void teardown() {
        // Do nothing.
    }

    private static final class FileModelLoader
            implements ModelLoader<LocalImageFileAdapter, InputStream> {

        @NonNull
        @Override
        public LoadData<InputStream> buildLoadData(@NonNull LocalImageFileAdapter model,
                                            int width, int height,
                                            @NonNull Options options) {
            return new LoadData<>(model.getKey(),
                    new FileDataFetcher(model, width, height, options));
        }

        @Override
        public boolean handles(@NonNull LocalImageFileAdapter model) {
            return true;
        }
    }

    private static final class FileDataFetcher implements DataFetcher<InputStream> {

        private final LocalImageFileAdapter mAdapter;
        private final int mWidth;
        private final int mHeight;
        private final Options mOptions;
        private File mFile;
        private InputStream mInput;

        public FileDataFetcher(@NonNull LocalImageFileAdapter adapter,
                               int width, int height, @NonNull Options options) {
            mAdapter = adapter;
            mWidth = width;
            mHeight = height;
            mOptions = options;
        }

        @Override
        public void loadData(@NonNull Priority priority,
                             @NonNull DataCallback<? super InputStream> callback) {
            try {
                mFile = mAdapter.load(priority, mWidth, mHeight, mOptions);
                mInput = mFile == null ? null : Files.newInputStream(mFile.toPath());
            } catch (Exception e) {
                callback.onLoadFailed(e);
                return;
            } catch (Throwable t) {
                callback.onLoadFailed(new RuntimeException(t));
                return;
            }
            callback.onDataReady(mInput);
        }

        @Override
        public void cleanup() {
            if (mInput != null) {
                try {
                    mInput.close();
                } catch (Throwable t) {
                    // ignore
                }
                mInput = null;
            }
            if (mFile != null && mAdapter.cleanup()) {
                FileUtils.delete(mFile);
                mFile = null;
            }
        }

        @Override
        public void cancel() {
            // do nothing.
        }

        @NonNull
        @Override
        public Class<InputStream> getDataClass() {
            return InputStream.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }
}
