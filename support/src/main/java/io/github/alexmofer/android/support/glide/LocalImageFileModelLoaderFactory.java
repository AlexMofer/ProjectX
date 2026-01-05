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

import io.github.alexmofer.android.support.utils.FileUtils;

/**
 * 本地图片文件模型加载器工厂工厂
 * Created by Alex on 2026/1/5.
 */
final class LocalImageFileModelLoaderFactory
        implements ModelLoaderFactory<LocalImageFileAdapter, File> {

    @NonNull
    @Override
    public ModelLoader<LocalImageFileAdapter, File> build(@NonNull MultiModelLoaderFactory multiFactory) {
        return new FileModelLoader();
    }

    @Override
    public void teardown() {
        // Do nothing.
    }

    private static final class FileModelLoader implements ModelLoader<LocalImageFileAdapter, File> {

        @NonNull
        @Override
        public LoadData<File> buildLoadData(@NonNull LocalImageFileAdapter model,
                                            int width, int height,
                                            @NonNull Options options) {
            return new LoadData<>(model.getKey(), new FileDataFetcher(model));
        }

        @Override
        public boolean handles(@NonNull LocalImageFileAdapter model) {
            return true;
        }
    }

    private static final class FileDataFetcher implements DataFetcher<File> {

        private final LocalImageFileAdapter mAdapter;
        private File mFile;

        public FileDataFetcher(@NonNull LocalImageFileAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void loadData(@NonNull Priority priority,
                             @NonNull DataCallback<? super File> callback) {
            try {
                mFile = mAdapter.loadImage(priority);
            } catch (Exception e) {
                callback.onLoadFailed(e);
                return;
            }
            callback.onDataReady(mFile);
        }

        @Override
        public void cleanup() {
            if (mFile != null && mAdapter.deleteWhenCleanup()) {
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
        public Class<File> getDataClass() {
            return File.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }
}
