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
package am.project.x.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * 警报对话框工具
 */
public class AlertDialogUtils {

    private AlertDialogUtils() {
        //no instance
    }

    /**
     * 获取警告对话框主题
     *
     * @param context Context
     * @return 主题
     */
    public static int getAlertDialogTheme(Context context) {
        final TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.alertDialogTheme,
                outValue, true);
        return outValue.resourceId;
    }
}
