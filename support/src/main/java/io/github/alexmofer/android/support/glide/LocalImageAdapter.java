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
package io.github.alexmofer.android.support.glide;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;

/**
 * 本地图片提供者
 * Created by Alex on 2026/1/5.
 */
public interface LocalImageAdapter {

    /**
     * 用于做缓存的 Key
     *
     * @return Key
     */
    default Key getKey() {
        return new ObjectKey(this);
    }
}