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
package io.github.alexmofer.android.support.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Gson 工具
 * Created by Alex on 2022/5/17.
 */
public class GsonUtils {

    private GsonUtils() {
        //no instance
    }

    /**
     * 实体对象序列化
     *
     * @param src 需要序列化的实体对象
     * @return 格式化的字符串
     */
    @Nullable
    public static String toJson(@NonNull Gson gson, @Nullable Object src) {
        try {
            return gson.toJson(src);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 范型对象序列化
     *
     * @param src  需要序列化的范型对象
     * @param type 类型
     * @return 格式化的字符串
     */
    public static String toJson(@NonNull Gson gson, @Nullable Object src, @NonNull Type type) {
        try {
            return gson.toJson(src, type);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * List对象序列化
     *
     * @param list 需要序列化的List对象
     * @param <T>  目标类型
     * @return 格式化的字符串
     */
    @Nullable
    public static <T> String listToJsonOrThrow(@NonNull Gson gson, @Nullable List<T> list)
            throws JsonIOException {
        if (list == null) {
            return gson.toJson(null);
        }
        if (list.isEmpty()) {
            return gson.toJson(list,
                    TypeToken.getParameterized(List.class, String.class).getType());
        }
        return gson.toJson(list,
                TypeToken.getParameterized(List.class, list.get(0).getClass()).getType());
    }

    /**
     * List对象序列化
     *
     * @param list 需要序列化的List对象
     * @param <T>  目标类型
     * @return 格式化的字符串
     */
    @Nullable
    public static <T> String listToJson(@NonNull Gson gson, @Nullable List<T> list) {
        try {
            return listToJsonOrThrow(gson, list);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 反序列实体对象
     *
     * @param json  字符串
     * @param clazz 实体类型
     * @param <T>   目标类型
     * @return 实体对象
     */
    @Nullable
    public static <T> T fromJson(@NonNull Gson gson,
                                 @Nullable String json, @NonNull Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 反序列实体对象（预处理转义）
     *
     * @param json  字符串
     * @param clazz 实体类型
     * @param <T>   目标类型
     * @return 实体对象
     */
    public static <T> T fromEscapedJson(@NonNull Gson gson,
                                        @Nullable String json, @NonNull Class<T> clazz) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        final JsonElement element;
        try {
            element = JsonParser.parseString(json);
        } catch (Throwable t) {
            return null;
        }
        try {
            return gson.fromJson(element, clazz);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 反序列范型对象
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  目标类型
     * @return 泛型对象
     * @throws JsonSyntaxException 错误
     */
    @Nullable
    public static <T> T fromJson(@NonNull Gson gson,
                                 @Nullable String json, @NonNull Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 反序列范型对象（预处理转义）
     *
     * @param json 字符串
     * @param type 类型
     * @param <T>  目标类型
     * @return 泛型对象
     */
    @Nullable
    public static <T> T fromEscapedJson(@NonNull Gson gson,
                                        @Nullable String json, @NonNull Type type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        final JsonElement element;
        try {
            element = JsonParser.parseString(json);
        } catch (Throwable t) {
            return null;
        }
        try {
            return gson.fromJson(element, type);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * 反序列List对象
     *
     * @param json  字符串
     * @param clazz 类型参数类
     * @param <T>   目标类型
     * @return List对象
     */
    @Nullable
    public static <T> List<T> listFromJsonOrThrow(@NonNull Gson gson,
                                                  @Nullable String json,
                                                  @NonNull Class<T> clazz)
            throws JsonSyntaxException {
        return gson.fromJson(json, TypeToken.getParameterized(List.class, clazz).getType());
    }

    /**
     * 反序列List对象（预处理转义）
     *
     * @param json  字符串
     * @param clazz 类型参数类
     * @param <T>   目标类型
     * @return List对象
     */
    @Nullable
    public static <T> List<T> listFromEscapedJsonOrThrow(@NonNull Gson gson,
                                                         @Nullable String json,
                                                         @NonNull Class<T> clazz)
            throws JsonSyntaxException {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        final JsonElement element;
        try {
            element = JsonParser.parseString(json);
        } catch (Throwable t) {
            return null;
        }
        return gson.fromJson(element, TypeToken.getParameterized(List.class, clazz).getType());
    }

    /**
     * 反序列List对象
     *
     * @param json  字符串
     * @param clazz 类型参数类
     * @param <T>   目标类型
     * @return List对象
     */
    @Nullable
    public static <T> List<T> listFromJson(@NonNull Gson gson, @Nullable String json,
                                           @NonNull Class<T> clazz) {
        try {
            return listFromJsonOrThrow(gson, json, clazz);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * 反序列List对象（预处理转义）
     *
     * @param json  字符串
     * @param clazz 类型参数类
     * @param <T>   目标类型
     * @return List对象
     */
    @Nullable
    public static <T> List<T> listFromEscapedJson(@NonNull Gson gson,
                                                  @Nullable String json,
                                                  @NonNull Class<T> clazz) {
        try {
            return listFromEscapedJsonOrThrow(gson, json, clazz);
        } catch (Throwable e) {
            return null;
        }
    }
}
