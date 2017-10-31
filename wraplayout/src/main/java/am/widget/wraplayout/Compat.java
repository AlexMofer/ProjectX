/*
 * Copyright (C) 2015 AlexMofer
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

package am.widget.wraplayout;

import android.annotation.TargetApi;
import android.view.View;

/**
 * 版本兼容控制器
 */
class Compat {

    private static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 17) {
            IMPL = new JbMr1CompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    static int getPaddingStart(View view) {
        return IMPL.getPaddingStart(view);
    }

    static int getPaddingEnd(View view) {
        return IMPL.getPaddingEnd(view);
    }

    private interface CompatPlusImpl {
        int getPaddingStart(View view);

        int getPaddingEnd(View view);
    }

    private static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        public int getPaddingStart(View view) {
            return view.getPaddingLeft();
        }

        @Override
        public int getPaddingEnd(View view) {
            return view.getPaddingRight();
        }
    }

    @TargetApi(17)
    private static class JbMr1CompatPlusImpl extends BaseCompatPlusImpl {
        @Override
        public int getPaddingStart(View view) {
            return view.getPaddingStart();
        }

        @Override
        public int getPaddingEnd(View view) {
            return view.getPaddingEnd();
        }
    }
}
