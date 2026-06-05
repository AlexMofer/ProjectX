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
package io.github.alexmofer.android.support.app;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.alexmofer.android.support.function.FunctionRBoolean;
import io.github.alexmofer.android.support.theme.SystemBarStyleController;

/**
 * 状态栏可控的 Activity
 * Created by Alex on 2026/5/28.
 */
public abstract class AppCompatActivity extends androidx.appcompat.app.AppCompatActivity
        implements SystemBarStyleController.Holder {
    private final SystemBarStyleController mSystemBarStyleController;

    @ContentView
    public AppCompatActivity(@LayoutRes int contentLayoutId,
                             @Nullable Boolean isLightStatusBar,
                             @Nullable Boolean isLightNavigationBar,
                             @NonNull FunctionRBoolean statusBarDefaultAdapter,
                             @NonNull FunctionRBoolean navigationBarDefaultAdapter) {
        super(contentLayoutId);
        mSystemBarStyleController = new SystemBarStyleController(this,
                isLightStatusBar, isLightNavigationBar,
                statusBarDefaultAdapter, navigationBarDefaultAdapter);
    }

    public AppCompatActivity(@Nullable Boolean isLightStatusBar,
                             @Nullable Boolean isLightNavigationBar,
                             @NonNull FunctionRBoolean statusBarDefaultAdapter,
                             @NonNull FunctionRBoolean navigationBarDefaultAdapter) {
        super();
        mSystemBarStyleController = new SystemBarStyleController(this,
                isLightStatusBar, isLightNavigationBar,
                statusBarDefaultAdapter, navigationBarDefaultAdapter);
    }

    @NonNull
    @Override
    public SystemBarStyleController getSystemBarStyleController() {
        return mSystemBarStyleController;
    }
}
