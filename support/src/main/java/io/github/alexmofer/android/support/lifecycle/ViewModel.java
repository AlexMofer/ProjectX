package io.github.alexmofer.android.support.lifecycle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;

import io.github.alexmofer.android.support.utils.MediatorLiveDataUtils;

/**
 * ViewModel 拓展实现
 * 增加创建永久激活状态的 MediatorLiveData，并在销毁时自动释放。
 * 解决：在 ViewModel 的构造函数中使用 MediatorLiveData 的 addSource 由于 MediatorLiveData 处于未激活状态导致源变化被忽略问题
 * Created by Alex on 2025/12/24.
 */
public class ViewModel extends androidx.lifecycle.ViewModel {
    private final ArrayList<MediatorLiveData<?>> mMediators = new ArrayList<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        for (MediatorLiveData<?> mediator : mMediators) {
            MediatorLiveDataUtils.releaseForeverActiveMediatorLiveData(mediator);
        }
        mMediators.clear();
    }

    @NonNull
    protected final <T> MediatorLiveData<T> newMediatorLiveData(@Nullable T initValue) {
        final MediatorLiveData<T> data =
                MediatorLiveDataUtils.createForeverActiveMediatorLiveData(initValue);
        mMediators.add(data);
        return data;
    }

    @NonNull
    protected final <T> MediatorLiveData<T> newMediatorLiveData() {
        return newMediatorLiveData(null);
    }
}
