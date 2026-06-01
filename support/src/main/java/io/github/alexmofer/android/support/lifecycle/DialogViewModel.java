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
package io.github.alexmofer.android.support.lifecycle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

/**
 * 对话框 ViewModel
 * Created by Alex on 2025/7/15.
 */
public class DialogViewModel extends ToastViewModel {
    private static final String KEY_DISMISS = "io.github.alexmofer.android.support:dialog_vm:dismiss";
    private static final String KEY_CANCELABLE = "io.github.alexmofer.android.support:dialog_vm:cancelable";
    private final MutableLiveData<Boolean> mDismiss;
    private final MutableLiveData<Boolean> mCancelable;

    public DialogViewModel(@NonNull SavedStateHandle handle) {
        mDismiss = handle.getLiveData(KEY_DISMISS, false);
        mCancelable = handle.getLiveData(KEY_CANCELABLE, true);
    }

    public DialogViewModel() {
        mDismiss = new MutableLiveData<>(false);
        mCancelable = new MutableLiveData<>(true);
    }

    /**
     * 观察 Dismiss
     *
     * @param viewModel DialogViewModel
     * @param fragment  DialogFragment
     */
    public static void observeDismiss(DialogViewModel viewModel, DialogFragment fragment) {
        viewModel.getDismiss().observe(fragment, value -> {
            if (Boolean.TRUE.equals(value)) {
                fragment.dismiss();
            }
        });
    }

    /**
     * 观察 Cancelable
     *
     * @param viewModel DialogViewModel
     * @param fragment  DialogFragment
     */
    public static void observeCancelable(DialogViewModel viewModel, DialogFragment fragment) {
        viewModel.getCancelable().observe(fragment,
                value -> fragment.setCancelable(Boolean.TRUE.equals(value)));
    }

    /**
     * 获取 Dismiss
     *
     * @return Dismiss
     */
    public LiveData<Boolean> getDismiss() {
        return mDismiss;
    }

    /**
     * 设置 Dismiss
     *
     * @param dismiss Dismiss
     */
    protected void setDismiss(@Nullable Boolean dismiss) {
        mDismiss.setValue(dismiss);
    }

    /**
     * 抛出 Dismiss
     *
     * @param dismiss Dismiss
     */
    protected void postDismiss(@Nullable Boolean dismiss) {
        mDismiss.postValue(dismiss);
    }

    /**
     * 获取 Cancelable
     *
     * @return Cancelable
     */
    public LiveData<Boolean> getCancelable() {
        return mCancelable;
    }

    /**
     * 设置 Cancelable
     *
     * @param cancelable Cancelable
     */
    protected void setCancelable(@Nullable Boolean cancelable) {
        mCancelable.setValue(cancelable);
    }

    /**
     * 抛出 Cancelable
     *
     * @param cancelable Cancelable
     */
    protected void postCancelable(@Nullable Boolean cancelable) {
        mCancelable.postValue(cancelable);
    }
}
