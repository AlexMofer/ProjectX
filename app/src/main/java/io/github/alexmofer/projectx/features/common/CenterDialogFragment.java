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
package io.github.alexmofer.projectx.features.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.TypedValueCompat;
import androidx.window.layout.WindowMetricsCalculator;

import io.github.alexmofer.android.support.utils.ContextUtils;
import io.github.alexmofer.android.support.window.WindowSizeHelper;
import io.github.alexmofer.projectx.R;

/**
 * 居中对话框
 * Created by Alex on 2025/6/12.
 */
public class CenterDialogFragment extends AppCompatDialogFragment {
    public static final int SIZE_MEDIUM = 0;// 半屏高度
    public static final int SIZE_LARGE = 1;// 满屏高度
    public static final int SIZE_FIT_CONTENT = 2;// 适应高度
    private final int mBackgroundColor;
    private final int mSize;

    public CenterDialogFragment(@ColorRes int backgroundColor, int size) {
        setStyle(STYLE_NO_TITLE, 0);
        this.mBackgroundColor = backgroundColor;
        if (size == SIZE_MEDIUM || size == SIZE_LARGE || size == SIZE_FIT_CONTENT) {
            mSize = size;
        } else {
            mSize = SIZE_FIT_CONTENT;
        }
    }

    public CenterDialogFragment(int size) {
        this(R.color.bc_dialog, size);
    }

    public CenterDialogFragment() {
        this(SIZE_FIT_CONTENT);
    }

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final View title = onCreateTitle(savedInstanceState);
        return onCreateDialog(savedInstanceState, title);
    }

    @NonNull
    protected AppCompatDialog onCreateDialog(@Nullable Bundle savedInstanceState,
                                             @Nullable View title) {
        return new CenterDialog(
                requireContext(), R.style.Theme_Dialog_Center, this::getBackgroundColor,
                this::getCenterWidth, this::getMaxCenterWidth, mSize, this::getCenterRadius,
                title);
    }

    /**
     * 获取背景颜色
     *
     * @param context Context
     * @return 背景色
     */
    @ColorInt
    protected int getBackgroundColor(Context context) {
        if (mBackgroundColor != ResourcesCompat.ID_NULL) {
            return context.getColor(mBackgroundColor);
        }
        return ContextUtils.isNight(context) ? Color.BLACK : Color.WHITE;
    }

    /**
     * 获取居中对话框宽度
     *
     * @param metrics DisplayMetrics
     * @return 居中对话框宽度
     */
    protected float getCenterWidth(DisplayMetrics metrics) {
        return 0.9f;
    }

    /**
     * 获取居中对话框宽度
     *
     * @param metrics DisplayMetrics
     * @return 居中对话框宽度
     */
    protected int getMaxCenterWidth(DisplayMetrics metrics) {
        if (WindowSizeHelper.isW900()) {
            // 电脑
            return Math.round(TypedValueCompat.dpToPx(540, metrics));
        }
        if (WindowSizeHelper.isW840()) {
            // 平板横屏
            return Math.round(TypedValueCompat.dpToPx(500, metrics));
        }
        if (WindowSizeHelper.isW600()) {
            // 平板竖屏
            return Math.round(TypedValueCompat.dpToPx(460, metrics));
        }
        if (WindowSizeHelper.isW480()) {
            // 横屏模式
            return Math.round(TypedValueCompat.dpToPx(420, metrics));
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 获取居中对话框圆角
     *
     * @param metrics DisplayMetrics
     * @return 居中对话框圆角
     */
    protected float getCenterRadius(DisplayMetrics metrics) {
        return TypedValueCompat.dpToPx(16, metrics);
    }

    /**
     * 创建标题 View
     *
     * @param savedInstanceState 保存的实例
     * @return 实例
     */
    @Nullable
    protected View onCreateTitle(@Nullable Bundle savedInstanceState) {
        return null;
    }

    private interface SizeAdapter {
        int get(DisplayMetrics metrics);
    }

    private interface SizeFAdapter {
        float get(DisplayMetrics metrics);
    }

    private interface BackgroundAdapter {
        int get(Context context);
    }

    private static class FitHeightFrameLayout extends FrameLayout {

        private final float mPercentage;

        public FitHeightFrameLayout(@NonNull Context context, float percentage) {
            super(context);
            mPercentage = percentage;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int heightSize = Math.max(1,
                    Math.round(MeasureSpec.getSize(heightMeasureSpec) * mPercentage));
            super.onMeasure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY));
        }
    }

    private static class FitHeightScrollView extends ScrollView {

        private final float mPercentage;

        public FitHeightScrollView(@NonNull Context context, float percentage) {
            super(context);
            mPercentage = percentage;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int heightSize = Math.max(1,
                    Math.round(MeasureSpec.getSize(heightMeasureSpec) * mPercentage));
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize,
                    MeasureSpec.getMode(heightMeasureSpec)));
        }
    }

    private static class OutlinedColorDrawable extends ColorDrawable {

        private final float mRadius;

        public OutlinedColorDrawable(int color, float radius) {
            super(color);
            mRadius = radius;
        }

        @Override
        public void getOutline(@NonNull Outline outline) {
            final Rect bounds = getBounds();
            outline.setRoundRect(0, 0, bounds.width(), bounds.height(), mRadius);
            outline.setAlpha(getAlpha() / 255.0f);
        }
    }

    private static class CenterDialog extends AppCompatDialog {

        private final int mWindowWidth;
        private final int mWindowHeight;
        private final ViewGroup mRootView;
        private final ViewGroup mContentView;
        private final ViewGroup.LayoutParams mContentLayout;
        private final Drawable mBackground;

        public CenterDialog(@NonNull Context context, int theme, BackgroundAdapter background,
                            SizeFAdapter centerWidth, SizeAdapter centerMax,
                            int heightSize, SizeFAdapter centerRadius,
                            @Nullable View title) {
            super(context, theme);
            final Window window = getWindow();
            if (window != null) {
                window.setWindowAnimations(R.style.Animation_Dialog_Center);
                window.setGravity(Gravity.CENTER);
            }
            final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            final Rect windowBounds =
                    WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(
                            getContext()).getBounds();
            final int maxWidth = centerMax.get(metrics);
            mWindowWidth = Math.max(1, Math.min(maxWidth,
                    Math.round(windowBounds.width() * centerWidth.get(metrics))));
            // 全使用 MATCH_PARENT 会导致不铺满时无法点击外部关闭，而 WRAP_CONTENT 又会强行处理避让区域。
            // 只好选 WRAP_CONTENT 避免触摸事件不统一。
            final float radius = centerRadius.get(metrics);
            if (heightSize == SIZE_MEDIUM) {
                // 半屏
                mWindowHeight = Math.max(1, Math.round(windowBounds.height() * 0.5f));
                final ViewGroup root = new FitHeightFrameLayout(context, 1f);
                if (title == null) {
                    mRootView = root;
                    mContentView = root;
                } else {
                    final LinearLayout column = new LinearLayout(context);
                    column.setOrientation(LinearLayout.VERTICAL);
                    column.addView(title, new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    final FrameLayout content = new FrameLayout(context);
                    column.addView(content, new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, 0, 1));
                    root.addView(column, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
                    mRootView = root;
                    mContentView = content;
                }
                mContentLayout = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
            } else if (heightSize == SIZE_LARGE) {
                // 满屏
                mWindowHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                final ViewGroup root = new FitHeightFrameLayout(context, 0.9f);
                if (title == null) {
                    mRootView = root;
                    mContentView = root;
                } else {
                    final LinearLayout column = new LinearLayout(context);
                    column.setOrientation(LinearLayout.VERTICAL);
                    column.addView(title, new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    final FrameLayout content = new FrameLayout(context);
                    column.addView(content, new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, 0, 1));
                    root.addView(column, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
                    mRootView = root;
                    mContentView = content;
                }
                mContentLayout = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
            } else {
                // 内容高度
                mWindowHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
                final ViewGroup root = new FitHeightScrollView(context, 0.9f);
                if (title == null) {
                    mRootView = root;
                    mContentView = root;
                } else {
                    final LinearLayout column = new LinearLayout(context);
                    column.setOrientation(LinearLayout.VERTICAL);
                    column.addView(title, new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    final FrameLayout content = new FrameLayout(context);
                    column.addView(content, new LinearLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    root.addView(column, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    mRootView = root;
                    mContentView = content;
                }
                mContentLayout = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
            }
            mBackground = new OutlinedColorDrawable(background.get(context), radius);
        }

        @Override
        public void setContentView(int layoutResID) {
            setContentView(View.inflate(getContext(), layoutResID, null));
        }

        @Override
        public void setContentView(@NonNull View view, ViewGroup.LayoutParams params) {
            setContentView(view);
        }

        @Override
        public void setContentView(@NonNull View view) {
            mContentView.removeAllViews();
            if (mContentLayout != null) {
                mContentView.addView(view, mContentLayout);
            } else {
                mContentView.addView(view);
            }
            super.setContentView(mRootView);
            final Window window = getWindow();
            if (window != null) {
                window.setLayout(mWindowWidth, mWindowHeight);
                window.setBackgroundDrawable(mBackground);
            }
        }
    }
}