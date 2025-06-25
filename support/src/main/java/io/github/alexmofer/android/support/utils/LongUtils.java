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

import java.util.UUID;

/**
 * Long工具
 * Created by Alex on 2022/5/21.
 */
public class LongUtils {

    private LongUtils() {
        //no instance
    }

    /**
     * 随机获取一个唯一Long
     *
     * @param least 最少的
     * @return 唯一Long
     */
    public static long random(boolean least) {
        return least ? UUID.randomUUID().getLeastSignificantBits() :
                UUID.randomUUID().getMostSignificantBits();
    }
}
