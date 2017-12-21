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

package am.drawable;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.Gravity;

/**
 * 版本兼容控制器
 */
@SuppressWarnings("all")
class Compat {
    /**
     * Raw bit controlling whether the layout direction is relative or not (START/END instead of
     * absolute LEFT/RIGHT).
     */
    static final int RELATIVE_LAYOUT_DIRECTION = 0x00800000;

    /**
     * Push object to x-axis position at the start of its container, not changing its size.
     */
    static final int START = RELATIVE_LAYOUT_DIRECTION | Gravity.LEFT;

    /**
     * Push object to x-axis position at the end of its container, not changing its size.
     */
    static final int END = RELATIVE_LAYOUT_DIRECTION | Gravity.RIGHT;

    private static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 23) {
            IMPL = new MarshmallowCompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    static int getColor(Context context, int id) {
        return IMPL.getColor(context, id);
    }

    private interface CompatPlusImpl {
        int getColor(Context context, int id);
    }

    @SuppressWarnings("all")
    private static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        public int getColor(Context context, int id) {
            return context.getResources().getColor(id);
        }
    }

    @TargetApi(23)
    private static class MarshmallowCompatPlusImpl extends BaseCompatPlusImpl {
        @Override
        public int getColor(Context context, int id) {
            return context.getColor(id);
        }
    }
}
