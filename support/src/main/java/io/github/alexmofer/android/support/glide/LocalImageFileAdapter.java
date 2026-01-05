package io.github.alexmofer.android.support.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;

import java.io.File;

/**
 * 本地图片文件提供者
 * Created by Alex on 2026/1/5.
 */
public interface LocalImageFileAdapter extends LocalImageAdapter {

    /**
     * 加载图片文件
     *
     * @param priority 优先级
     * @return 图片文件
     * @throws Exception 失败信息
     */
    @WorkerThread
    @Nullable
    File loadImage(@NonNull Priority priority) throws Exception;

    /**
     * 判断是否为数据清理时自动删除该生成的文件
     *
     * @return 是否删除，默认删除
     */
    default boolean deleteWhenCleanup() {
        return true;
    }

    /**
     * 注册
     *
     * @param registry 注册器
     */
    static void register(@NonNull Registry registry) {
        registry.prepend(LocalImageFileAdapter.class, File.class,
                new LocalImageFileModelLoaderFactory());
    }
}
