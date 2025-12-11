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
package io.github.alexmofer.android.support.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Parcelable 工具
 * Created by Alex on 2024/4/9.
 */
public class ParcelableUtils {

    private ParcelableUtils() {
        //no instance
    }

    /**
     * 序列化
     *
     * @param parcelable Parcelable
     * @return 字节数组
     */
    @NonNull
    public static byte[] marshall(@NonNull Parcelable parcelable) {
        final Parcel parcel = Parcel.obtain();
        parcel.setDataPosition(0);
        parcelable.writeToParcel(parcel, 0);
        final byte[] data = parcel.marshall();
        parcel.recycle();
        return data;
    }

    /**
     * 反序列化
     *
     * @param data    字节数组
     * @param creator Parcelable.Creator
     * @return 对象
     */
    @NonNull
    public static <T extends Parcelable> T unmarshall(@NonNull byte[] data,
                                                      @NonNull Parcelable.Creator<T> creator) {
        final Parcel parcel = Parcel.obtain();
        parcel.unmarshall(data, 0, data.length);
        parcel.setDataPosition(0);
        final T parcelable = creator.createFromParcel(parcel);
        parcel.recycle();
        return parcelable;
    }

    /**
     * 序列化
     *
     * @param parcelable Parcelable
     * @param file       文件
     * @return 序列化成功时返回true
     */
    public static boolean marshall(@NonNull Parcelable parcelable, @NonNull File file) {
        final byte[] data = marshall(parcelable);
        try (final FileOutputStream output = new FileOutputStream(file)) {
            output.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 反序列化
     *
     * @param file    文件
     * @param creator Parcelable.Creator
     * @return 对象
     */
    @Nullable
    public static <T extends Parcelable> T unmarshall(@NonNull File file,
                                                      @NonNull Parcelable.Creator<T> creator) {
        try (final FileInputStream input = new FileInputStream(file)) {
            return unmarshall(StreamUtils.readAllBytes(input), creator);
        } catch (IOException e) {
            return null;
        }
    }
}
