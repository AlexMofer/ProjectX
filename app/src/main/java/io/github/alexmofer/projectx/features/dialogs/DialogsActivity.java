package io.github.alexmofer.projectx.features.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 主页
 * Created by Alex on 2025/11/25.
 */
public final class DialogsActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, DialogsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
