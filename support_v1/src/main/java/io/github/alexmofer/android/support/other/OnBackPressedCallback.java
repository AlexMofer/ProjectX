/*
 * Copyright (C) 2025 AlexMofer
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
package io.github.alexmofer.android.support.other;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

/**
 * 返回回调
 * Created by Alex on 2025/7/22.
 */
public class OnBackPressedCallback extends androidx.activity.OnBackPressedCallback {

    private final Runnable mCallback;

    public OnBackPressedCallback(Runnable callback) {
        super(false);
        mCallback = callback;
    }

    public OnBackPressedCallback setEnable(LifecycleOwner owner, LiveData<Boolean> enable) {
        enable.observe(owner, value -> setEnabled(Boolean.TRUE.equals(value)));
        return this;
    }

    @Override
    public void handleOnBackPressed() {
        mCallback.run();
    }
}
