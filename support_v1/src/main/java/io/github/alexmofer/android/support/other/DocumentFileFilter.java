/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.other;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;

/**
 * DocumentFile 筛选器
 * Created by Alex on 2024/5/22.
 */
public interface DocumentFileFilter {

    /**
     * 测试是否接受该 DocumentFile
     *
     * @param df DocumentFile
     * @return 接受该 DocumentFile 时返回true
     */
    boolean accept(@NonNull DocumentFile df);
}
