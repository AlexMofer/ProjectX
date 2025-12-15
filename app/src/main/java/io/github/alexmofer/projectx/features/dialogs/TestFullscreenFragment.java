package io.github.alexmofer.projectx.features.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.github.alexmofer.projectx.ui.builders.ButtonBuilder;
import io.github.alexmofer.projectx.ui.builders.EditTextBuilder;
import io.github.alexmofer.projectx.ui.builders.LayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.LinearLayoutBuilder;
import io.github.alexmofer.projectx.ui.builders.LinearLayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.MarginLayoutParamsBuilder;
import io.github.alexmofer.projectx.ui.builders.TextViewBuilder;
import io.github.alexmofer.projectx.ui.builders.ViewBuilder;
import io.github.alexmofer.projectx.ui.builders.ViewGroupBuilder;

import io.github.alexmofer.android.support.app.FullscreenDialogFragment;
import io.github.alexmofer.android.support.utils.TypedValueUtils;
import io.github.alexmofer.android.support.widget.AvoidArea;

/**
 * 测试全屏
 * Created by Alex on 2025/11/26.
 */
public final class TestFullscreenFragment extends FullscreenDialogFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Context context = requireContext();
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int dp48 = TypedValueUtils.getDimensionPixelOffset(48, metrics);
        final int dp12 = TypedValueUtils.getDimensionPixelOffset(12, metrics);
        final GradientDrawable bottom1 = new GradientDrawable();
        bottom1.setStroke(dp12, Color.RED);
        bottom1.setColor(Color.CYAN);
        final GradientDrawable bottom2 = new GradientDrawable();
        bottom2.setStroke(dp12, Color.RED);
        bottom2.setColor(Color.CYAN);
        final View content = new LinearLayoutBuilder(context)
                .setOrientation(LinearLayout.VERTICAL)
                .addView(new TextViewBuilder(context)
                                .setText("测试全屏")
                                .setTextSize(64)
                                .setTextColor(Color.WHITE)
                                .setBackgroundColor(Color.RED)
                                .build(),
                        new LayoutParamsBuilder()
                                .matchWidth()
                                .build())
                .addView(new EditTextBuilder(context)
                                .setInputType(InputType.TYPE_CLASS_TEXT)
                                .setBackgroundColor(Color.GREEN)
                                .setId(android.R.id.text1)
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMargin(dp12)
                                .matchWidth()
                                .build())
                .addView(new ButtonBuilder(context)
                                .setText("关闭")
                                .setTextSize(16)
                                .setTextColor(Color.WHITE)
                                .setGravity(Gravity.CENTER)
                                .setMinHeight(dp48)
                                .setBackgroundColor(Color.BLUE)
                                .setOnClickListener(v -> dismiss())
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMarginTop(dp12)
                                .matchWidth()
                                .build())
                .addView(new ViewBuilder(context)
                                .setBackground(bottom1)
                                .build(),
                        new LinearLayoutParamsBuilder()
                                .matchWidth()
                                .setHeight(TypedValueUtils.getDimensionPixelOffset(700, metrics))
                                .build())
                .addView(new EditTextBuilder(context)
                                .setInputType(InputType.TYPE_CLASS_TEXT)
                                .setBackgroundColor(Color.GREEN)
                                .setId(android.R.id.text2)
                                .build(),
                        new MarginLayoutParamsBuilder()
                                .setMargin(dp12)
                                .matchWidth()
                                .build())
                .addView(new ViewBuilder(context)
                                .setBackground(bottom2)
                                .build(),
                        new LinearLayoutParamsBuilder()
                                .matchWidth()
                                .setHeight(TypedValueUtils.getDimensionPixelOffset(80, metrics))
                                .build())
                .setBackgroundColor(Color.YELLOW)
                .build();
        AvoidArea.paddingAll(content);
        return new ViewGroupBuilder(new ScrollView(context))
                .addView(content,
                        new LayoutParamsBuilder()
                                .matchWidth()
                                .build())
                .build();
    }
}
