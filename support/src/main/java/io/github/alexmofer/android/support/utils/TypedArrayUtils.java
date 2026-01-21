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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * TypedArray 工具
 * Created by Alex on 2026/1/21.
 */
public final class TypedArrayUtils {

    private TypedArrayUtils() {
        //no instance
    }

    /**
     * Obtains styled attributes from the theme, if available, or unstyled
     * resources if the theme is null.
     */
    @NonNull
    public static TypedArray obtainAttributes(@NonNull Resources res,
                                              @Nullable Resources.Theme theme,
                                              @NonNull AttributeSet set,
                                              @NonNull int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    /**
     * 处理 TypedArray
     *
     * @param supplier 提供者
     * @param consumer 处理者
     */
    public static void handleTypedArray(@NonNull Supplier<TypedArray> supplier,
                                        @NonNull Consumer<TypedArray> consumer) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try (final TypedArray ta = supplier.get()) {
                consumer.accept(ta);
            }
        } else {
            final TypedArray ta = supplier.get();
            try {
                consumer.accept(ta);
            } catch (Throwable t) {
                // ignore
            } finally {
                ta.recycle();
            }
        }
    }
}
