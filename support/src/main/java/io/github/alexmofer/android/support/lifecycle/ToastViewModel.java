/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.lifecycle;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import io.github.alexmofer.android.support.other.StringResource;

/**
 * Toast ViewModel
 * Created by Alex on 2024/4/23.
 */
public class ToastViewModel extends SavedStateViewModel {

    private final MutableLiveData<StringResource> mToast = new MutableLiveData<>();

    public ToastViewModel() {
    }

    public ToastViewModel(@NonNull SavedStateHandle handle) {
        super(handle);
    }

    /**
     * 观察 Toast
     *
     * @param viewModel       ToastViewModel
     * @param owner           LifecycleOwner
     * @param contextSupplier Context 提供者
     */
    public static void observeToast(ToastViewModel viewModel, LifecycleOwner owner,
                                    Supplier<Context> contextSupplier) {
        viewModel.getToast().observe(owner, value -> {
            if (value != null) {
                value.showToast(contextSupplier.get());
                viewModel.clearToast();
            }
        });
    }

    /**
     * 观察 Toast
     *
     * @param viewModel ToastViewModel
     * @param fragment  Fragment
     */
    public static void observeToast(ToastViewModel viewModel, Fragment fragment) {
        observeToast(viewModel, fragment, fragment::requireContext);
    }

    /**
     * 观察 Toast
     *
     * @param viewModel ToastViewModel
     * @param activity  FragmentActivity
     */
    public static void observeToast(ToastViewModel viewModel, FragmentActivity activity) {
        observeToast(viewModel, activity, activity::getApplicationContext);
    }

    /**
     * 获取 Toast
     *
     * @return Toast
     */
    public LiveData<StringResource> getToast() {
        return mToast;
    }

    /**
     * 设置 Toast 消息
     *
     * @param message 消息
     */
    protected void setToast(@Nullable StringResource message) {
        mToast.setValue(message);
    }

    /**
     * 抛出 Toast 消息
     *
     * @param message 消息
     */
    protected void postToast(@Nullable StringResource message) {
        mToast.postValue(message);
    }

    /**
     * 清理 Toast
     */
    public void clearToast() {
        if (mToast.getValue() != null) {
            mToast.setValue(null);
        }
    }
}
