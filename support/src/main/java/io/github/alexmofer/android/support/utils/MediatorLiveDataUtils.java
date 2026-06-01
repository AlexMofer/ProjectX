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
package io.github.alexmofer.android.support.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

/**
 * MediatorLiveData 工具
 * Created by Alex on 2026/6/1.
 */
public final class MediatorLiveDataUtils {
    private static final Observer<?> EMPTY_OBSERVER = value -> {
    };

    private MediatorLiveDataUtils() {
        //no instance
    }

    @NonNull
    public static <T> MediatorLiveData<T> createForeverActiveMediatorLiveData(@Nullable T initValue) {
        final MediatorLiveData<T> data = initValue == null ? new MediatorLiveData<>() : new MediatorLiveData<>(initValue);
        //noinspection unchecked
        data.observeForever((Observer<T>) EMPTY_OBSERVER);
        return data;
    }

    @NonNull
    public static <T> MediatorLiveData<T> createForeverActiveMediatorLiveData() {
        return createForeverActiveMediatorLiveData(null);
    }

    public static <T> void releaseForeverActiveMediatorLiveData(@NonNull MediatorLiveData<T> data) {
        //noinspection unchecked
        data.removeObserver((Observer<T>) EMPTY_OBSERVER);
    }
}
