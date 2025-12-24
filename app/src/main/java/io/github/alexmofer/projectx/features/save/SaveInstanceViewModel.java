package io.github.alexmofer.projectx.features.save;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.SavedStateHandle;

import java.util.UUID;

import io.github.alexmofer.android.support.lifecycle.SavedStateViewModel;

/**
 * ViewModel
 * Created by Alex on 2025/12/23.
 */
public final class SaveInstanceViewModel extends SavedStateViewModel {

    private static final String KEY_UUID = "uuid";
    private String mUUID;

    public SaveInstanceViewModel(@NonNull SavedStateHandle handle) {
        super(handle);
        mUUID = UUID.randomUUID().toString();
        System.out.println("lalalalal------------------------------------create:");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        System.out.println("lalalalal------------------------------------onCleared");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        System.out.println("lalalalal------------------------------------onSaveInstanceState:" + mUUID);
        outState.putString(KEY_UUID, mUUID);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUUID = savedInstanceState.getString(KEY_UUID);
        System.out.println("lalalalal------------------------------------onRestoreInstanceState:" + mUUID);
    }
}
