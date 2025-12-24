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
package io.github.alexmofer.projectx.features.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.github.alexmofer.android.support.utils.TypedValueUtils;
import io.github.alexmofer.android.support.widget.AvoidArea;
import io.github.alexmofer.android.support.window.EdgeToEdge;
import io.github.alexmofer.projectx.features.dialogs.DialogsActivity;
import io.github.alexmofer.projectx.features.save.SaveInstanceActivity;
import io.github.alexmofer.projectx.ui.builders.ButtonBuilder;
import io.github.alexmofer.projectx.ui.builders.LayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.LinearLayoutBuilder;
import io.github.alexmofer.projectx.ui.builders.MarginLayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.TextViewBuilder;
import io.github.alexmofer.projectx.ui.builders.ViewGroupBuilder;

/**
 * 主页面
 * Created by Alex on 2025/6/17.
 */
public class MainActivity extends AppCompatActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int dp48 = TypedValueUtils.getDimensionPixelOffset(48, metrics);
        final int dp12 = TypedValueUtils.getDimensionPixelOffset(12, metrics);
        final Context context = this;
        final View content = new LinearLayoutBuilder(context)
                .setOrientation(LinearLayout.VERTICAL)
                .addView(new TextViewBuilder(context)
                                .setText("主页")
                                .setTextSize(32)
                                .setTextColor(Color.WHITE)
                                .setBackgroundColor(Color.RED)
                                .build(),
                        new LayoutParamsBuilder()
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("对话框")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> DialogsActivity.start(this))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("应用实例保存框架")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> SaveInstanceActivity.start(this))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .build();
        AvoidArea.paddingAll(content);
        setContentView(new ViewGroupBuilder(new ScrollView(context))
                .addView(content,
                        new LayoutParamsBuilder()
                                .matchWidth()
                                .build())
                .build());
    }
}
