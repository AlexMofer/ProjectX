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
package io.github.alexmofer.android.support.app;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * 不可重复的 Fragment
 * Created by Alex on 2026/1/13.
 */
public interface NonRepeatable {

    /**
     * 接收到新参数
     *
     * @param args 参数
     */
    default void onNewArguments(@NonNull Bundle args) {
        // do nothing
    }
}
