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
package am.util.job.core;

/**
 * 数据数组
 * Created by Alex on 2021/3/12.
 */
public interface DataArray {
    /**
     * 设置数据
     *
     * @param values 数据
     */
    default void setAll(Object... values) {
        clear();
        int key = 0;
        for (Object value : values) {
            put(key, value);
            key++;
        }
    }

    /**
     * 清空值
     *
     * @param <T> 类型
     * @return 自身
     */
    <T extends DataArray> T clear();

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     * @param <T>   类型
     * @return 自身
     */
    <T extends DataArray> T put(int key, Object value);

    /**
     * 获取值
     *
     * @param key 键
     * @param <V> 类型
     * @return 值
     */
    default <V> V get(int key) {
        return get(key, null);
    }

    /**
     * 获取值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <V>          类型
     * @return 值
     */
    <V> V get(int key, V defaultValue);

    /**
     * 获取Boolean
     *
     * @param key 键
     * @return Boolean
     */
    default boolean getBoolean(int key) {
        return getBoolean(key, false);
    }

    /**
     * 获取Boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Boolean
     */
    boolean getBoolean(int key, boolean defaultValue);

    /**
     * 获取Byte
     *
     * @param key 键
     * @return Byte
     */
    default byte getByte(int key) {
        return getByte(key, (byte) 0);
    }

    /**
     * 获取Byte
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Byte
     */
    byte getByte(int key, byte defaultValue);

    /**
     * 获取Short
     *
     * @param key 键
     * @return Short
     */
    default short getShort(int key) {
        return getShort(key, (short) 0);
    }

    /**
     * 获取Short
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Short
     */
    short getShort(int key, short defaultValue);

    /**
     * 获取Char
     *
     * @param key 键
     * @return Char
     */
    default char getChar(int key) {
        return getChar(key, (char) 0);
    }

    /**
     * 获取Char
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Char
     */
    char getChar(int key, char defaultValue);

    /**
     * 获取Int
     *
     * @param key 键
     * @return Int
     */
    default int getInt(int key) {
        return getInt(key, 0);
    }

    /**
     * 获取Int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Int
     */
    int getInt(int key, int defaultValue);

    /**
     * 获取Long
     *
     * @param key 键
     * @return Long
     */
    default long getLong(int key) {
        return getLong(key, 0);
    }

    /**
     * 获取Long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Long
     */
    long getLong(int key, long defaultValue);

    /**
     * 获取Float
     *
     * @param key 键
     * @return Float
     */
    default float getFloat(int key) {
        return getFloat(key, 0f);
    }

    /**
     * 获取Float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Float
     */
    float getFloat(int key, float defaultValue);

    /**
     * 获取Double
     *
     * @param key 键
     * @return Double
     */
    default double getDouble(int key) {
        return getDouble(key, 0d);
    }

    /**
     * 获取Double
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Double
     */
    double getDouble(int key, double defaultValue);

    /**
     * 获取String
     *
     * @param key 键
     * @return String
     */
    default String getString(int key) {
        return getString(key, null);
    }

    /**
     * 获取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return String
     */
    String getString(int key, String defaultValue);
}
