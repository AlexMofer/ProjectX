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
package io.github.alexmofer.android.support.utils;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.core.util.Consumer;

/**
 * TextWatcher 工具
 * Created by Alex on 2025/6/25.
 */
public class TextWatcherUtils {

    private TextWatcherUtils() {
        //no instance
    }

    /**
     * 新建文本变更后观察者
     *
     * @param consumer 变更后回调
     * @return 观察者
     */
    public static TextWatcher newAfterTextChanged(Consumer<Editable> consumer) {
        return new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                consumer.accept(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
    }
}
