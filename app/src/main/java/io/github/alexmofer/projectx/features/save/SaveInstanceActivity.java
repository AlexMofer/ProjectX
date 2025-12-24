package io.github.alexmofer.projectx.features.save;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import io.github.alexmofer.android.support.window.EdgeToEdge;

/**
 * 保存实例 Activity
 * Created by Alex on 2025/12/23.
 */
public final class SaveInstanceActivity extends AppCompatActivity {

    private SaveInstanceViewModel mViewModel;

    public static void start(Context context) {
        context.startActivity(new Intent(context, SaveInstanceActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mViewModel = new ViewModelProvider(this).get(SaveInstanceViewModel.class);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
