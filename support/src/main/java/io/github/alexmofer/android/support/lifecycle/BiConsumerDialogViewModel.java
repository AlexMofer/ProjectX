/*
 * Copyright (C) 2025 AlexMofer
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
package io.github.alexmofer.android.support.lifecycle;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.BiConsumer;

/**
 * BiConsumer 对话框 ViewModel
 * Created by Alex on 2025/7/16.
 */
public abstract class BiConsumerDialogViewModel<T, U> extends DialogViewModel {
    private final ArrayMap<String, BiConsumer<T, U>> mConsumers = new ArrayMap<>();

    /**
     * 添加 Consumer
     *
     * @param id       ID
     * @param consumer Consumer
     */
    public void addConsumer(@NonNull String id, @NonNull BiConsumer<T, U> consumer) {
        mConsumers.put(id, consumer);
    }

    /**
     * 移除 Consumer
     *
     * @param id ID
     */
    public void removeConsumer(@Nullable String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        mConsumers.remove(id);
    }

    /**
     * 获取 Consumer
     *
     * @param id ID
     * @return Consumer
     */
    @Nullable
    public BiConsumer<T, U> getConsumer(@Nullable String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        return mConsumers.get(id);
    }
}
