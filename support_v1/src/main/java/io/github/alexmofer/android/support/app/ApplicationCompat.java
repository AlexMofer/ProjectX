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
package io.github.alexmofer.android.support.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * Application兼容器
 * Created by Alex on 2022/3/24.
 */
public class ApplicationCompat {

    private static Compat Impl = null;

    private ApplicationCompat() {
        //no instance
    }

    /**
     * Returns the name of the current process. A package's default process name
     * is the same as its package name. Non-default processes will look like
     * "$PACKAGE_NAME:$NAME", where $NAME corresponds to an android:process
     * attribute within AndroidManifest.xml.
     */
    public static String getProcessName() {
        if (Impl == null) {
            if (Build.VERSION.SDK_INT >= 28) {
                Impl = new Api28Impl();
            } else {
                Impl = new Api18Impl();
            }
        }
        return Impl.getProcessName();

    }

    interface Compat {
        String getProcessName();
    }

    static class BaseImpl implements Compat {

        @SuppressLint("PrivateApi")
        @Override
        public String getProcessName() {
            try {
                return (String) Class.forName("android.app.ActivityThread", false,
                                Application.class.getClassLoader())
                        .getMethod("currentPackageName")
                        .invoke(null);
            } catch (Throwable e) {
                return null;
            }
        }
    }

    static class Api18Impl extends BaseImpl {

        @SuppressLint("PrivateApi")
        @Override
        public String getProcessName() {
            try {
                return (String) Class.forName("android.app.ActivityThread", false,
                                Application.class.getClassLoader())
                        .getMethod("currentProcessName")
                        .invoke(null);
            } catch (Throwable e) {
                return null;
            }
        }
    }

    @RequiresApi(28)
    static class Api28Impl extends Api18Impl {

        @Override
        public String getProcessName() {
            return Application.getProcessName();
        }
    }

}
