/*
 * Copyright (C) 2023 AlexMofer
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
package io.github.alexmofer.android.support.collection;

import java.util.Arrays;

/**
 * 简化的int数组Set
 * Created by Alex on 2023/2/8.
 */
public class SimplifiedIntArraySet {

    private int[] mArray;

    /**
     * 清空
     */
    public void clear() {
        mArray = null;
    }

    /**
     * 判断是否包含该值
     *
     * @param value 值
     * @return 包含该值时返回true
     */
    public boolean contains(int value) {
        return indexOf(value) != -1;
    }

    /**
     * 获取该值所在的下标
     *
     * @param value 值
     * @return 下标，不包含该值时返回-1
     */
    public int indexOf(int value) {
        if (mArray == null) {
            return -1;
        }
        final int length = mArray.length;
        for (int i = 0; i < length; i++) {
            if (mArray[i] == value) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取下标位置的值
     *
     * @param index 下标
     * @return 值
     */
    public int valueAt(int index) {
        return mArray[index];
    }

    /**
     * 判断是否为空
     *
     * @return 为空时返回true
     */
    public boolean isEmpty() {
        return mArray == null;
    }

    /**
     * 添加值
     *
     * @param value 值
     * @return 添加成功时返回true
     */
    public boolean add(int value) {
        if (contains(value)) {
            return false;
        }
        if (mArray == null) {
            mArray = new int[]{value};
        } else {
            final int[] array = new int[mArray.length + 1];
            System.arraycopy(mArray, 0, array, 0, mArray.length);
            array[mArray.length] = value;
            mArray = array;
            System.gc();
        }
        return true;
    }

    /**
     * 移除值
     *
     * @param value 值
     * @return 移除成功时返回true
     */
    public boolean remove(int value) {
        final int index = indexOf(value);
        if (index == -1) {
            return false;
        }
        removeAt(index);
        return true;
    }

    /**
     * 移除下标下的值
     *
     * @param index 下标
     * @return 值
     */
    public int removeAt(int index) {
        final int value = mArray[index];
        if (mArray.length == 1) {
            mArray = null;
        } else {
            final int[] array = new int[mArray.length - 1];
            if (index == 0) {
                System.arraycopy(mArray, 1, array, 0, mArray.length - 1);
            } else if (index == mArray.length - 1) {
                System.arraycopy(mArray, 0, array, 0, mArray.length - 1);
            } else {
                System.arraycopy(mArray, 0, array, 0, index);
                System.arraycopy(mArray, index + 1, array, index, mArray.length - 1 - index);
            }
            mArray = array;
        }
        System.gc();
        return value;
    }

    /**
     * 获取子项总数
     *
     * @return 子项总数
     */
    public int size() {
        return mArray == null ? 0 : mArray.length;
    }

    /**
     * 转为数组
     *
     * @return 数组
     */
    public int[] toArray() {
        if (mArray == null) {
            return null;
        } else {
            final int[] result = new int[mArray.length];
            System.arraycopy(mArray, 0, result, 0, mArray.length);
            return result;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SimplifiedIntArraySet that = (SimplifiedIntArraySet) o;
        return Arrays.equals(mArray, that.mArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(mArray);
    }

    @Override
    public String toString() {
        return Arrays.toString(mArray);
    }
}
