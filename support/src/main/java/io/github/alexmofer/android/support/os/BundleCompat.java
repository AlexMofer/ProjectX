/*
 * Copyright (C) 2022 AlexMofer
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
package io.github.alexmofer.android.support.os;

import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Bundle兼容器
 * Created by Alex on 2022/9/7.
 */
public class BundleCompat {

    private BundleCompat() {
        //no instance
    }

    /**
     * Returns the value associated with the given key or {@code null} if:
     * <ul>
     *     <li>No mapping of the desired type exists for the given key.
     *     <li>A {@code null} value is explicitly associated with the key.
     *     <li>The object is not of type {@code clazz}.
     * </ul>
     *
     * <p><b>Note: </b> if the expected value is not a class provided by the Android platform,
     * you must call {@link Bundle#setClassLoader(ClassLoader)} with the proper {@link ClassLoader} first.
     * Otherwise, this method might throw an exception or return {@code null}.
     *
     * @param key   a String, or {@code null}
     * @param clazz The type of the object expected
     * @return a Parcelable value, or {@code null}
     */
    @Nullable
    public static <T> T getParcelable(@NonNull Bundle bundle,
                                      @Nullable String key,
                                      @NonNull Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelable(key, clazz);
        } else {
            //noinspection unchecked
            return bundle.getParcelable(key);
        }
    }

    /**
     * Returns the value associated with the given key, or {@code null} if:
     * <ul>
     *     <li>No mapping of the desired type exists for the given key.
     *     <li>A {@code null} value is explicitly associated with the key.
     *     <li>The object is not of type {@code clazz}.
     * </ul>
     *
     * <p><b>Note: </b> if the expected value is not a class provided by the Android platform,
     * you must call {@link Bundle#setClassLoader(ClassLoader)} with the proper {@link ClassLoader} first.
     * Otherwise, this method might throw an exception or return {@code null}.
     *
     * @param key   a String, or {@code null}
     * @param clazz The type of the items inside the array. This is only verified when unparceling.
     * @return a Parcelable[] value, or {@code null}
     */
    @Nullable
    public static <T> T[] getParcelableArray(@NonNull Bundle bundle,
                                             @Nullable String key,
                                             @NonNull Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelableArray(key, clazz);
        } else {
            //noinspection unchecked
            return (T[]) bundle.getParcelableArray(key);
        }
    }

    /**
     * Returns the value associated with the given key, or {@code null} if:
     * <ul>
     *     <li>No mapping of the desired type exists for the given key.
     *     <li>A {@code null} value is explicitly associated with the key.
     *     <li>The object is not of type {@code clazz}.
     * </ul>
     *
     * <p><b>Note: </b> if the expected value is not a class provided by the Android platform,
     * you must call {@link Bundle#setClassLoader(ClassLoader)} with the proper {@link ClassLoader} first.
     * Otherwise, this method might throw an exception or return {@code null}.
     *
     * @param key   a String, or {@code null}
     * @param clazz The type of the items inside the array list. This is only verified when
     *              unparceling.
     * @return an ArrayList<T> value, or {@code null}
     */
    @Nullable
    public static <T> ArrayList<T> getParcelableArrayList(@NonNull Bundle bundle,
                                                          @Nullable String key,
                                                          @NonNull Class<? extends T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getParcelableArrayList(key, clazz);
        } else {
            //noinspection unchecked
            return (ArrayList<T>) bundle.getParcelableArrayList(key);
        }
    }

    /**
     * Returns the value associated with the given key, or {@code null} if:
     * <ul>
     *     <li>No mapping of the desired type exists for the given key.
     *     <li>A {@code null} value is explicitly associated with the key.
     *     <li>The object is not of type {@code clazz}.
     * </ul>
     *
     * @param key   a String, or null
     * @param clazz The type of the items inside the sparse array. This is only verified when
     *              unparceling.
     * @return a SparseArray of T values, or null
     */
    @Nullable
    public static <T> SparseArray<T> getSparseParcelableArray(@NonNull Bundle bundle,
                                                              @Nullable String key,
                                                              @NonNull Class<? extends T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getSparseParcelableArray(key, clazz);
        } else {
            //noinspection unchecked
            return (SparseArray<T>) bundle.getSparseParcelableArray(key);
        }
    }

    /**
     * Returns the value associated with the given key, or {@code null} if:
     * <ul>
     *     <li>No mapping of the desired type exists for the given key.
     *     <li>A {@code null} value is explicitly associated with the key.
     *     <li>The object is not of type {@code clazz}.
     * </ul>
     *
     * @param key   a String, or null
     * @param clazz The expected class of the returned type
     * @return a Serializable value, or null
     */
    @Nullable
    public static <T extends Serializable> T getSerializable(@NonNull Bundle bundle,
                                                             @Nullable String key,
                                                             @NonNull Class<T> clazz) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return bundle.getSerializable(key, clazz);
        } else {
            //noinspection unchecked
            return (T) bundle.getSerializable(key);
        }
    }
}
