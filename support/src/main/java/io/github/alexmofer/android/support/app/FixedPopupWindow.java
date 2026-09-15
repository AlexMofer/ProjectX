/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.app;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

import io.github.alexmofer.android.support.utils.TypedValueUtils;

/**
 * 轻量级 PopupWindow，修正官方 PopupWindow 对齐与测量问题
 * 当弹出内容不定宽且包含文字时，不附着窗口的文字测量并不准确。
 * 如果显示位置需要根据内容宽度，则建议使用该类，否则请使用官方 PopupWindow。
 * Created by Alex on 2026/9/10.
 */
public final class FixedPopupWindow {
    private final View mContent;
    private final WindowManager mWindowManager;
    private final Container mContainer;
    private final View.OnAttachStateChangeListener mOnAttachStateChangeListener;
    private boolean mShowing = false;
    private Consumer<FixedPopupWindow> mOnDismissListener;

    public FixedPopupWindow(@NonNull View content) {
        mContent = content;
        final Context context = content.getContext();
        mWindowManager = context.getSystemService(WindowManager.class);
        mContainer = new Container(context, this::dismiss);
        mOnAttachStateChangeListener = new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                // Do nothing
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                v.removeOnAttachStateChangeListener(this);
                dismiss();
            }
        };
    }

    public void showAsDropDown(@NonNull View anchor, int xoff, int yoff, int gravity) {
        if (mWindowManager == null || mContent == null || mShowing) {
            return;
        }
        // 1. 获取 Anchor 在屏幕上的绝对坐标
        final int[] anchorLocation = new int[2];
        anchor.getLocationInWindow(anchorLocation);
        final int anchorX = anchorLocation[0];
        final int anchorY = anchorLocation[1];
        final int anchorWidth = anchor.getWidth();
        final int anchorHeight = anchor.getHeight();

        // 2. 将数据设置进 Container 并更新内容
        mContainer.setContentView(mContent, anchorX, anchorY, anchorWidth, anchorHeight, xoff, yoff, gravity);

        // 3. 构造 WindowManager.LayoutParams
        // 使用 MATCH_PARENT 撑满整个屏幕，让 Container 内部接管精准的边缘裁剪和对齐逻辑
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        // 适配刘海屏 / 挖孔屏 / 避让区域（API 28+ / Android 9.0 及以上）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // 允许窗口延伸到短边切口（刘海/挖孔）区域
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            // 或者在高版本中直接使用 ALWAYS（根据具体业务场景选择）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            }
        }
        params.token = anchor.getApplicationWindowToken();
        params.gravity = Gravity.TOP | Gravity.START;

        try {
            mWindowManager.addView(mContainer, params);
            mShowing = true;
            anchor.addOnAttachStateChangeListener(mOnAttachStateChangeListener);
        } catch (Throwable t) {
            // 防止 WindowManager 添加失败导致异常崩溃
            mShowing = false;
        }
    }

    public void dismiss() {
        if (mShowing) {
            mShowing = false;
            try {
                mWindowManager.removeView(mContainer);
            } catch (Throwable t) {
                // ignore
            }
            if (mOnDismissListener != null) {
                mOnDismissListener.accept(this);
            }
        }
    }

    public boolean isShowing() {
        return mShowing;
    }

    public void setOnDismissListener(@Nullable Consumer<FixedPopupWindow> listener) {
        mOnDismissListener = listener;
    }

    private static class Container extends ViewGroup {
        private final int mEdgeMargin;
        private final Runnable mAutoDismiss;
        private View mContent;
        private int mAnchorX, mAnchorY, mAnchorWidth, mAnchorHeight;
        private int mXOff, mYOff;
        private int mGravity;
        private boolean mTop;
        private boolean mEdgeBanding;// 是否贴边

        public Container(@NonNull Context context,
                         @NonNull Runnable autoDismiss) {
            super(context);
            mEdgeMargin = TypedValueUtils.getDimensionPixelOffset(16,
                    context.getResources().getDisplayMetrics());
            mAutoDismiss = autoDismiss;
            setClipChildren(false);
            setLayoutTransition(new LayoutTransition());
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (mContent == null) {
                return;
            }
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = MeasureSpec.getSize(heightMeasureSpec);
            final int maxWidth = width - mEdgeMargin * 2;
            final int maxHeight = height - mEdgeMargin * 2;
            mContent.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST));
            final int contentHeight = mContent.getMeasuredHeight();
            final int bottomArea = height - mAnchorY - mAnchorHeight - mYOff;
            if (bottomArea > contentHeight) {
                // 下方足够显示
                mTop = false;
                mEdgeBanding = false;
                return;
            }
            final int topArea = mAnchorY - mYOff;
            if (topArea > contentHeight) {
                // 上方足够显示
                mTop = true;
                mEdgeBanding = false;
                return;
            }
            // 空间不足，锚点中心决定
            mTop = mAnchorY + mAnchorHeight * 0.5f > height * 0.5f;
            mEdgeBanding = true;
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            if (mContent == null) {
                return;
            }
            final int width = getWidth();
            final int anchorLeft = Math.max(0, mAnchorX);
            final int anchorRight = Math.min(mAnchorX + mAnchorWidth, width);
            final int contentWidth = mContent.getMeasuredWidth();
            if (mGravity == Gravity.CENTER) {
                // 居中
                final float anchorCenterX = anchorLeft + mAnchorWidth * 0.5f;
                int contentLeft = Math.round(anchorCenterX - contentWidth * 0.5f);
                contentLeft += mXOff;
                contentLeft = Math.max(0, contentLeft);
                if (contentLeft + contentWidth > width) {
                    contentLeft = width - contentWidth;
                }
                layoutContent(contentLeft, contentWidth);
                return;
            }

            if ((getLayoutDirection() == View.LAYOUT_DIRECTION_LTR && mGravity == Gravity.END)
            || (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL && mGravity == Gravity.START)) {
                // 右对齐
                int contentLeft = anchorRight - contentWidth;
                contentLeft += mXOff;
                contentLeft = Math.max(0, contentLeft);
                if (contentLeft + contentWidth > width) {
                    contentLeft = width - contentWidth;
                }
                layoutContent(contentLeft, contentWidth);
                return;
            }
            // 左对齐
            int contentLeft = anchorLeft;
            contentLeft += mXOff;
            contentLeft = Math.max(0, contentLeft);
            if (contentLeft + contentWidth > width) {
                contentLeft = width - contentWidth;
            }
            layoutContent(contentLeft, contentWidth);
        }

        private void layoutContent(int contentLeft, int contentWidth) {
            final int height = getHeight();
            final int anchorTop = Math.max(0, mAnchorY);
            final int anchorBottom = Math.min(mAnchorY + mAnchorHeight, height);
            final int contentHeight = mContent.getMeasuredHeight();
            int contentTop;
            if (mTop) {
                if (mEdgeBanding) {
                    contentTop = mEdgeMargin;
                } else {
                    contentTop = anchorTop - mYOff - contentHeight;
                    contentTop = Math.max(mEdgeMargin, contentTop);
                }
            } else {
                if (mEdgeBanding) {
                    contentTop = height - mEdgeMargin - contentHeight;
                } else {
                    contentTop = anchorBottom + mYOff;
                    contentTop = Math.min(height - mEdgeMargin - contentHeight, contentTop);
                }
            }
            mContent.layout(contentLeft, contentTop,
                    contentLeft + contentWidth, contentTop + contentHeight);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mContent != null) {
                    float x = event.getX();
                    float y = event.getY();

                    int left = mContent.getLeft();
                    int top = mContent.getTop();
                    int right = mContent.getRight();
                    int bottom = mContent.getBottom();

                    if (x < left || x > right || y < top || y > bottom) {
                        mAutoDismiss.run();
                        return true;
                    }
                }
            }
            return super.onTouchEvent(event);
        }

        public void setContentView(@NonNull View content,
                                   int anchorX, int anchorY, int anchorWidth, int anchorHeight,
                                   int xOff, int yOff, int gravity) {
            removeAllViews();
            mContent = content;
            mAnchorX = anchorX;
            mAnchorY = anchorY;
            mAnchorWidth = anchorWidth;
            mAnchorHeight = anchorHeight;
            mXOff = xOff;
            mYOff = yOff;
            mGravity = gravity;
            addView(content);
        }
    }
}
