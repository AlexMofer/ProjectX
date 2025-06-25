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
import android.graphics.Outline;
import android.graphics.Path;
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

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.util.TypedValueCompat;
import androidx.window.layout.WindowMetricsCalculator;

import io.github.alexmofer.android.support.utils.ViewOutlineProviderUtils;
import io.github.alexmofer.android.support.window.EdgeToEdge;
import io.github.alexmofer.android.support.window.WindowSizeHelper;
import io.github.alexmofer.projectx.R;

/**
 * 半模态转场对话框
 * 在 Sheet 模式下需要处理避让区域
 * Created by Alex on 2025/6/12.
 */
public class SheetDialogFragment extends CenterDialogFragment {
    private final int mSize;
    private boolean mSheet = false;

    public SheetDialogFragment(@ColorRes int backgroundColor, int size, int sheetSize) {
        super(backgroundColor, size);
        if (sheetSize == SIZE_MEDIUM || sheetSize == SIZE_LARGE || sheetSize == SIZE_FIT_CONTENT) {
            mSize = sheetSize;
        } else {
            mSize = SIZE_FIT_CONTENT;
        }
    }

    public SheetDialogFragment(int size) {
        this(R.color.bc_dialog, SIZE_FIT_CONTENT, size);
    }

    public SheetDialogFragment() {
        this(SIZE_FIT_CONTENT);
    }

    @NonNull
    @Override
    protected AppCompatDialog onCreateDialog(@Nullable Bundle savedInstanceState,
                                             @Nullable View title) {
        mSheet = !(WindowSizeHelper.isW480() && WindowSizeHelper.isH480());
        if (mSheet) {
            return new SheetDialog(
                    requireContext(), R.style.Theme_Dialog_Sheet, this::getBackgroundColor,
                    this::getSheetWidth, mSize, this::getSheetRadius, title);
        }
        return super.onCreateDialog(savedInstanceState, title);
    }

    /**
     * 判断是否为半模态模式
     *
     * @return 为半模态模式时返回true
     */
    public boolean isSheet() {
        return mSheet;
    }

    /**
     * 获取半模态宽度
     *
     * @param metrics DisplayMetrics
     * @return 半模态宽度
     */
    protected int getSheetWidth(DisplayMetrics metrics) {
        if (WindowSizeHelper.isW480()) {
            // 横屏模式
            return Math.round(TypedValueCompat.dpToPx(420, metrics));
        } else {
            // 竖屏模式
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    /**
     * 获取半模态圆角
     *
     * @param metrics DisplayMetrics
     * @return 半模态圆角
     */
    protected float getSheetRadius(DisplayMetrics metrics) {
        return TypedValueCompat.dpToPx(24, metrics);
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
        private final Path mPath;

        public OutlinedColorDrawable(int color, float radius, boolean all) {
            super(color);
            mRadius = radius;
            if (all) {
                mPath = null;
            } else {
                mPath = new Path();
            }
        }

        @Override
        protected void onBoundsChange(@NonNull Rect bounds) {
            super.onBoundsChange(bounds);
            if (mPath != null) {
                mPath.rewind();
                mPath.addRoundRect(0, 0, bounds.width(), bounds.height(),
                        new float[]{mRadius, mRadius, mRadius, mRadius, 0, 0, 0, 0},
                        Path.Direction.CW);
            }
        }

        @Override
        public void getOutline(@NonNull Outline outline) {
            if (mPath != null) {
                ViewOutlineProviderUtils.setPath(outline, mPath);
            } else {
                final Rect bounds = getBounds();
                outline.setRoundRect(0, 0, bounds.width(), bounds.height(), mRadius);
            }
            outline.setAlpha(getAlpha() / 255.0f);
        }
    }

    private static class SheetDialog extends AppCompatDialog {

        private final int mWindowWidth;
        private final int mWindowHeight;
        private final ViewGroup mRootView;
        private final ViewGroup mContentView;
        private final ViewGroup.LayoutParams mContentLayout;
        private final Drawable mBackground;

        public SheetDialog(@NonNull Context context, int theme, BackgroundAdapter background,
                           SizeAdapter sheetWidth, int heightSize, SizeFAdapter sheetRadius,
                           @Nullable View title) {
            super(context, theme);

            final Window window = getWindow();
            if (window != null) {
                // 由于不是 floating 窗口，必须配置为边到边
                EdgeToEdge.enable(window);
                window.setWindowAnimations(R.style.Animation_Dialog_Sheet);
                window.setGravity(Gravity.BOTTOM);
            }
            final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            final Rect windowBounds =
                    WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(
                            getContext()).getBounds();
            final int backgroundColor = background.get(context);
            // 半模态模式
            mWindowWidth = sheetWidth.get(metrics);
            // 全使用 MATCH_PARENT 会导致不铺满时无法点击外部关闭，而 WRAP_CONTENT 又会强行处理避让区域。
            // 只好选 WRAP_CONTENT 避免触摸事件不统一。
            final float radius = sheetRadius.get(metrics);
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
                final ViewGroup root = new FitHeightFrameLayout(context, 0.98f);
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
                final ViewGroup root = new FitHeightScrollView(context, 0.98f);
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
            mBackground = new OutlinedColorDrawable(backgroundColor, radius, false);
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
