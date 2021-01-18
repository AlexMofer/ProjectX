/*
 * Copyright (C) 2021 AlexMofer
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
package am.util.clipboard;

import android.os.ParcelFileDescriptor;

/**
 * 文件型剪切板内容提供者
 */
public interface FileClipboardAdapter<T> {
    /**
     * 获取总数
     *
     * @return 总数
     */
    int getCount();

    /**
     * 获取MIME类型集
     *
     * @return MIME类型集
     */
    String[] getTypes();

    /**
     * 写入
     *
     * @param position   位置
     * @param descriptor 文件
     * @return 是否成功
     */
    boolean write(int position, ParcelFileDescriptor descriptor);

    /**
     * 读取
     *
     * @param descriptor 文件
     * @return 数据
     */
    T read(ParcelFileDescriptor descriptor);
}
