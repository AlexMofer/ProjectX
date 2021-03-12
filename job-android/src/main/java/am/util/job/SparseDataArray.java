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
package am.util.job;

import android.os.Build;
import android.util.SparseArray;

import am.util.job.core.DataArray;

/**
 * 使用SparseArray实现的数据数组
 * Created by Alex on 2021/3/12.
 */
class SparseDataArray implements DataArray {

    private final SparseArray<Object> mValues = new SparseArray<>();

    private boolean contains(int key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return mValues.contains(key);
        } else {
            return mValues.get(key) != null;
        }
    }

    @Override
    public <T extends DataArray> T clear() {
        mValues.clear();
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public <T extends DataArray> T put(int key, Object value) {
        if (value == null) {
            mValues.remove(key);
            //noinspection unchecked
            return (T) this;
        }
        mValues.put(key, value);
        //noinspection unchecked
        return (T) this;
    }

    @Override
    public <V> V get(int key, V defaultValue) {
        if (contains(key)) {
            //noinspection unchecked
            return (V) mValues.get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean getBoolean(int key, boolean defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public byte getByte(int key, byte defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public short getShort(int key, short defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public char getChar(int key, char defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public int getInt(int key, int defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public long getLong(int key, long defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public float getFloat(int key, float defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public double getDouble(int key, double defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public String getString(int key, String defaultValue) {
        if (contains(key)) {
            return get(key);
        } else {
            return defaultValue;
        }
    }
}
