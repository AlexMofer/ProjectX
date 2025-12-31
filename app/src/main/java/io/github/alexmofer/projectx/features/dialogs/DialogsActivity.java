package io.github.alexmofer.projectx.features.dialogs;

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
import io.github.alexmofer.projectx.ui.builders.ButtonBuilder;
import io.github.alexmofer.projectx.ui.builders.LayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.LinearLayoutBuilder;
import io.github.alexmofer.projectx.ui.builders.MarginLayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.TextViewBuilder;
import io.github.alexmofer.projectx.ui.builders.ViewGroupBuilder;

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
        EdgeToEdge.enable(this);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int dp48 = TypedValueUtils.getDimensionPixelOffset(48, metrics);
        final int dp12 = TypedValueUtils.getDimensionPixelOffset(12, metrics);
        final Context context = this;
        final View content = new LinearLayoutBuilder(context)
                .setOrientation(LinearLayout.VERTICAL)
                .addView(new TextViewBuilder(context)
                                .setText("三种主要样式对话框")
                                .setTextSize(32)
                                .setTextColor(Color.WHITE)
                                .setBackgroundColor(Color.RED)
                                .build(),
                        new LayoutParamsBuilder()
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("Fullscreen")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestFullscreenFragment().show(getSupportFragmentManager(), "Fullscreen"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("FullscreenLimited")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestFullscreenLimitedFragment().show(getSupportFragmentManager(), "FullscreenLimited"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestCenterScrollable")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestCenterScrollableFragment().show(getSupportFragmentManager(), "TestCenterScrollable"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestCenterScrollableOver")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestCenterScrollableOverFragment().show(getSupportFragmentManager(), "TestCenterScrollableOver"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestCenterNoScrollOver")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestCenterNoScrollOverFragment().show(getSupportFragmentManager(), "TestCenterNoScrollOver"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestCenterNoScroll")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestCenterNoScrollFragment().show(getSupportFragmentManager(), "TestCenterNoScroll"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestCenterMatch")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestCenterMatchFragment().show(getSupportFragmentManager(), "TestCenterMatch"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetScrollable")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetScrollableFragment().show(getSupportFragmentManager(), "TestSheetScrollable"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetScrollableOver")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetScrollableOverFragment().show(getSupportFragmentManager(), "TestSheetScrollableOver"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetNoScrollOver")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetNoScrollOverFragment().show(getSupportFragmentManager(), "TestSheetNoScrollOver"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetNoScroll")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetNoScrollFragment().show(getSupportFragmentManager(), "TestSheetNoScroll"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetMatch")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetMatchFragment().show(getSupportFragmentManager(), "TestSheetMatch"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetScrollableSTS")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetScrollableSTSFragment().show(getSupportFragmentManager(), "TestSheetScrollableSTS"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetScrollableOverSTS")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetScrollableOverSTSFragment().show(getSupportFragmentManager(), "TestSheetScrollableOverSTS"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetNoScrollOverSTS")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetNoScrollOverSTSFragment().show(getSupportFragmentManager(), "TestSheetNoScrollOverSTS"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetNoScrollSTS")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetNoScrollSTSFragment().show(getSupportFragmentManager(), "TestSheetNoScrollSTS"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("TestSheetMatchSTS")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> new TestSheetMatchSTSFragment().show(getSupportFragmentManager(), "TestSheetMatchSTS"))
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .setBackgroundColor(Color.GREEN)
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
