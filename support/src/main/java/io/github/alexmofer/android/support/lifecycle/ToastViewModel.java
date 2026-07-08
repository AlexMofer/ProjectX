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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.github.alexmofer.android.support.function.FunctionPObjectObject;
import io.github.alexmofer.android.support.other.StringAdapter;

/**
 * Toast ViewModel
 * Created by Alex on 2024/4/23.
 */
public class ToastViewModel extends ViewModel {
    private final MutableLiveData<StringAdapter> mToast = new MutableLiveData<>();
    private FunctionPObjectObject<Context, StringAdapter> mToastShowFunction;

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
                viewModel.onShowToast(contextSupplier.get(), value);
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
    public LiveData<StringAdapter> getToast() {
        return mToast;
    }

    /**
     * 设置 Toast 消息
     *
     * @param message 消息
     */
    protected void setToast(@Nullable StringAdapter message) {
        mToast.setValue(message);
    }

    /**
     * 抛出 Toast 消息
     *
     * @param message 消息
     */
    protected void postToast(@Nullable StringAdapter message) {
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

    /**
     * 显示 Toast
     *
     * @param context Context
     * @param message 消息
     */
    protected void onShowToast(@NonNull Context context, @NonNull StringAdapter message) {
        if (mToastShowFunction != null) {
            mToastShowFunction.execute(context, message);
        } else {
            final String msg = message.getString(context);
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 设置 Toast 显示函数
     *
     * @param function 函数
     */
    public void setToastShowFunction(@Nullable FunctionPObjectObject<Context, StringAdapter> function) {
        mToastShowFunction = function;
    }
}
