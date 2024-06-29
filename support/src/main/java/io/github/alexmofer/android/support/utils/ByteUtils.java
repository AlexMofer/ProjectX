/*
 * Copyright (C) 2019 AlexMofer
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

import java.math.BigInteger;

/**
 * 字节工具
 * Created by Alex on 2019/6/26.
 */
public class ByteUtils {

    private ByteUtils() {
        //no instance
    }

    /**
     * 转换为16进制
     *
     * @param bytes     数据
     * @param minLength 16进制字符串长度
     * @return 16进制字符串
     */
    public static String toHexString(byte[] bytes, int minLength) {
        if (bytes == null)
            return null;
        final String str = new BigInteger(1, bytes).toString(16);
        final StringBuilder builder = new StringBuilder();
        if (str.length() < minLength) {
            final int number = minLength - str.length();
            for (int i = 0; i < number; i++) {
                builder.append("0");
            }
        }
        builder.append(str);
        return builder.toString();
    }
}
