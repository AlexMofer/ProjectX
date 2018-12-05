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

package am.widget.stateframelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * 版本兼容控制器
 */
class Compat {

    private static final CompatPlusImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new LollipopCompatPlusImpl();
        } else {
            IMPL = new BaseCompatPlusImpl();
        }
    }

    static Drawable getDrawable(Context context, int id) {
        return IMPL.getDrawable(context, id);
    }

    static void setHotspot(Drawable drawable, float x, float y) {
        IMPL.setHotspot(drawable, x, y);
    }

    private interface CompatPlusImpl {
        Drawable getDrawable(Context context, int id);

        void setHotspot(Drawable drawable, float x, float y);
    }

    private static class BaseCompatPlusImpl implements CompatPlusImpl {
        @Override
        public Drawable getDrawable(Context context, int id) {
            return context.getResources().getDrawable(id);
        }

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            // do nothing
        }
    }

    @TargetApi(21)
    private static class LollipopCompatPlusImpl extends BaseCompatPlusImpl {

        @Override
        public Drawable getDrawable(Context context, int id) {
            return context.getDrawable(id);
        }

        @Override
        public void setHotspot(Drawable drawable, float x, float y) {
            drawable.setHotspot(x, y);
        }
    }
}
