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

/**
 * 本地图片位图模型加载器工厂工厂
 * Created by Alex on 2026/1/5.
 */
final class LocalImageBitmapModelLoaderFactory
        implements ModelLoaderFactory<LocalImageBitmapAdapter, LocalImageBitmapAdapter> {

    @NonNull
    @Override
    public ModelLoader<LocalImageBitmapAdapter, LocalImageBitmapAdapter> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new BitmapModelLoader();
    }

    @Override
    public void teardown() {
        // Do nothing.
    }

    private static final class BitmapModelLoader
            implements ModelLoader<LocalImageBitmapAdapter, LocalImageBitmapAdapter> {

        @NonNull
        @Override
        public LoadData<LocalImageBitmapAdapter> buildLoadData(@NonNull LocalImageBitmapAdapter model,
                                                               int width, int height,
                                                               @NonNull Options options) {
            return new LoadData<>(model.getKey(), new BitmapDataFetcher(model));
        }

        @Override
        public boolean handles(@NonNull LocalImageBitmapAdapter model) {
            return true;
        }
    }

    /**
     * 数据提取器，因为数据已经存在于本地，其并没有什么需要实现的，工作主要集中于从本地图片解码到 bitmap
     */
    private static final class BitmapDataFetcher implements DataFetcher<LocalImageBitmapAdapter> {

        private final LocalImageBitmapAdapter mAdapter;

        public BitmapDataFetcher(@NonNull LocalImageBitmapAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void loadData(@NonNull Priority priority,
                             @NonNull DataCallback<? super LocalImageBitmapAdapter> callback) {
            callback.onDataReady(mAdapter);
        }

        @Override
        public void cleanup() {
            // do nothing.
        }

        @Override
        public void cancel() {
            // do nothing.
        }

        @NonNull
        @Override
        public Class<LocalImageBitmapAdapter> getDataClass() {
            return LocalImageBitmapAdapter.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }
}
