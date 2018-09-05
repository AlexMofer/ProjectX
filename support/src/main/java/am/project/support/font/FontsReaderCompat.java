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

import android.os.Build;


/**
 * 兼容器
 * Created by Alex on 2018/8/30.
 */
class FontsReaderCompat {

    private static final FontsReader IMPL;

    static {
        final int api = Build.VERSION.SDK_INT;
        if (api >= 28) {
            IMPL = new FontsReaderApi28();
        } else if (api >= 26) {
            IMPL = new FontsReaderApi26();
        } else if (api >= 24) {
            IMPL = new FontsReaderApi24();
        } else if (api >= 21) {
            IMPL = new FontsReaderApi21();
        } else if (api >= 17) {
            IMPL = new FontsReaderApi17();
        } else if (api >= 14) {
            IMPL = new FontsReaderApi14();
        } else {
            IMPL = new FontsReaderBase();
        }
    }

    /**
     * 获取字体读取器
     *
     * @return 字体读取器
     */
    static FontsReader getFontsReader() {
        return IMPL;
    }

    private FontsReaderCompat() {
        //no instance
    }
}
