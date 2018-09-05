/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.support.font;

import android.text.TextUtils;

/**
 * 字体族别名
 * Created by Alex on 2018/8/30.
 */
class Alias {
    private final String mName;// 名称
    private final String mTo;// 映射字体族名称
    private final int mWeight;// 字体粗细筛选条件，为-1时表示不筛选

    Alias(String name, String to, int weight) {
        mName = name;
        mTo = to;
        mWeight = weight;
    }

    boolean isAvailable() {
        return !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mTo);
    }

    String getName() {
        return mName;
    }

    String getTo() {
        return mTo;
    }

    int getWeight() {
        return mWeight;
    }
}
