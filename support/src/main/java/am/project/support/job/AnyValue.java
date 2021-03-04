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
package am.project.support.job;

import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.Nullable;

/**
 * 值辅助
 * Created by Xiang Zhicheng on 2021/3/1.
 */
abstract class AnyValue {

    private final SparseArray<Object> mValues = new SparseArray<>();

    private boolean contains(int key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return mValues.contains(key);
        } else {
            return mValues.get(key) != null;
        }
    }

    void set(Object... values) {
        clear();
        int key = 0;
        for (Object value : values) {
            put(key, value);
            key++;
        }
    }

    /**
     * 清空值
     */
    public <T extends AnyValue> T clear() {
        mValues.clear();
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 设置值
     *
     * @param key   键
     * @param value 值
     */
    public <T extends AnyValue> T put(int key, @Nullable Object value) {
        if (value == null) {
            mValues.remove(key);
            //noinspection unchecked
            return (T) this;
        }
        mValues.put(key, value);
        //noinspection unchecked
        return (T) this;
    }

    /**
     * 获取值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <V>          类型
     * @return 值
     */
    public <V> V get(int key, @Nullable V defaultValue) {
        if (contains(key)) {
            //noinspection unchecked
            return (V) mValues.get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取值
     *
     * @param key 键
     * @param <V> 类型
     * @return 值
     */
    public <V> V get(int key) {
        if (contains(key)) {
            //noinspection unchecked
            return (V) mValues.get(key);
        } else {
            return null;
        }
    }

    /**
     * 获取Boolean
     *
     * @param key 键
     * @return Boolean
     */
    public boolean getBoolean(int key) {
        return getBoolean(key, false);
    }

    /**
     * 获取Boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Boolean
     */
    public boolean getBoolean(int key, boolean defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Byte
     *
     * @param key 键
     * @return Byte
     */
    public byte getByte(int key) {
        return getByte(key, (byte) 0);
    }

    /**
     * 获取Byte
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Byte
     */
    public byte getByte(int key, byte defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Short
     *
     * @param key 键
     * @return Short
     */
    public short getShort(int key) {
        return getShort(key, (short) 0);
    }

    /**
     * 获取Short
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Short
     */
    public short getShort(int key, short defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Char
     *
     * @param key 键
     * @return Char
     */
    public char getChar(int key) {
        return getChar(key, (char) 0);
    }

    /**
     * 获取Char
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Char
     */
    public char getChar(int key, char defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Int
     *
     * @param key 键
     * @return Int
     */
    public int getInt(int key) {
        return getInt(key, 0);
    }

    /**
     * 获取Int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Int
     */
    public int getInt(int key, int defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Long
     *
     * @param key 键
     * @return Long
     */
    public long getLong(int key) {
        return getLong(key, 0);
    }

    /**
     * 获取Long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Long
     */
    public long getLong(int key, long defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Float
     *
     * @param key 键
     * @return Float
     */
    public float getFloat(int key) {
        return getFloat(key, 0f);
    }

    /**
     * 获取Float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Float
     */
    public float getFloat(int key, float defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取Double
     *
     * @param key 键
     * @return Double
     */
    public double getDouble(int key) {
        return getDouble(key, 0d);
    }

    /**
     * 获取Double
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return Double
     */
    public double getDouble(int key, double defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取String
     *
     * @param key 键
     * @return String
     */
    public String getString(int key) {
        return getString(key, null);
    }

    /**
     * 获取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return String
     */
    public String getString(int key, @Nullable String defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }
}
