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

import kotlinx.coroutines.CoroutineScope;

/**
 * Dismiss Toast ViewModel
 * Created by Alex on 2025/7/15.
 */
public class DismissToastViewModel extends ToastViewModel {

    private final MutableLiveData<Boolean> mDismiss = new MutableLiveData<>(false);

    public DismissToastViewModel() {
    }

    public DismissToastViewModel(@NonNull CoroutineScope viewModelScope) {
        super(viewModelScope);
    }

    public DismissToastViewModel(@NonNull AutoCloseable... closeables) {
        super(closeables);
    }

    public DismissToastViewModel(@NonNull CoroutineScope viewModelScope,
                                 @NonNull AutoCloseable... closeables) {
        super(viewModelScope, closeables);
    }

    /**
     * 观察 Dismiss
     *
     * @param fragment DialogFragment
     */
    public static void observeDismiss(DismissToastViewModel viewModel, DialogFragment fragment) {
        viewModel.getDismiss().observe(fragment.getViewLifecycleOwner(), value -> {
            if (Boolean.TRUE.equals(value)) {
                fragment.dismiss();
            }
        });
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
}
