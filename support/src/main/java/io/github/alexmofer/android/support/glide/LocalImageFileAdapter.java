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
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.bumptech.glide.Priority;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.Options;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地图片文件提供者
 * Created by Alex on 2026/1/5.
 */
public interface LocalImageFileAdapter extends LocalImageAdapter {

    /**
     * 引擎进行数据清理时，需要将{@link #load(Priority, int, int, Options)}返回的图片文件删除时，
     * 则返回{@code true}，否则返回{@code false}。
     */
    default boolean cleanup() {
        return true;
    }

    /**
     * 根据给定数据生成图片文件，如果无法生成图片文件则返回{@code null} 。
     *
     * <p>注意 - {@code width}和{@code height}参数仅供参考，生成的图片文件无需与给定尺寸完全匹配。
     * 一个典型的应用场景是使用目标尺寸来确定图片文件的降采样比例，以避免分配过大的内存空间。
     *
     * @param priority 该请求指定的优先级
     * @param width    生成位图的理想宽度，值为{@link com.bumptech.glide.request.target.Target#SIZE_ORIGINAL}表示资源原始宽度。
     * @param height   生成位图的理想高度，值为{@link com.bumptech.glide.request.target.Target#SIZE_ORIGINAL}表示资源原始高度。
     * @param options  选项参数，注意检查需要的选项值是否存在且类型符合。
     * @throws IOException      生成位图时可抛出该异常用于表示读取失败。
     * @throws OutOfMemoryError 可抛出该错误用于表示生成位图时内存不足。
     * @throws RuntimeException 该方法也可抛出运行时异常，最好是能在消息中阐述跟详细的失败原因。
     */
    @WorkerThread
    @Nullable
    File load(@NonNull Priority priority,
              int width, int height, @NonNull Options options) throws IOException;

    /**
     * 注册
     *
     * @param registry 注册器
     */
    static void register(@NonNull Registry registry) {
        registry.prepend(LocalImageFileAdapter.class, InputStream.class,
                new LocalImageFileModelLoaderFactory());
    }
}
