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
package io.github.alexmofer.android.support.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 子项分割线
 * Created by Alex on 2025/8/5.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * Don't show any dividers.
     */
    public static final int SHOW_DIVIDER_NONE = LinearLayout.SHOW_DIVIDER_NONE;
    /**
     * Show a divider at the beginning of the group.
     */
    public static final int SHOW_DIVIDER_BEGINNING = LinearLayout.SHOW_DIVIDER_BEGINNING;
    /**
     * Show dividers between each item in the group.
     */
    public static final int SHOW_DIVIDER_MIDDLE = LinearLayout.SHOW_DIVIDER_MIDDLE;
    /**
     * Show a divider at the end of the group.
     */
    public static final int SHOW_DIVIDER_END = LinearLayout.SHOW_DIVIDER_END;
    private final Divider mDivider;
    private final int mShowDividers;
    private final boolean mShowDividerBeginning;
    private final boolean mShowDividerMiddle;
    private final boolean mShowDividerEnd;

    public DividerItemDecoration(@NonNull Divider divider, int showDividers) {
        mDivider = divider;
        mShowDividers = showDividers;
        mShowDividerBeginning = (showDividers & SHOW_DIVIDER_BEGINNING) == SHOW_DIVIDER_BEGINNING;
        mShowDividerMiddle = (showDividers & SHOW_DIVIDER_MIDDLE) == SHOW_DIVIDER_MIDDLE;
        mShowDividerEnd = (showDividers & SHOW_DIVIDER_END) == SHOW_DIVIDER_END;
    }

    public DividerItemDecoration(Context context, @DrawableRes int divider, int showDividers) {
        this(new DrawableDivider(context, divider), showDividers);
    }

    public DividerItemDecoration(Context context, @DrawableRes int divider) {
        this(context, divider, SHOW_DIVIDER_MIDDLE);
    }

    public DividerItemDecoration(int size, @ColorInt int color,
                                 float radius, float paddingStart, float paddingEnd,
                                 int showDividers) {
        this(new ColorDivider(size, color, radius, paddingStart, paddingEnd), showDividers);
    }

    public DividerItemDecoration(int size, @ColorInt int color) {
        this(size, color, 0, 0, 0, SHOW_DIVIDER_MIDDLE);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mShowDividers == SHOW_DIVIDER_NONE) {
            return;
        }
        final RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm instanceof GridLayoutManager && ((GridLayoutManager) lm).getSpanCount() != 1) {
            // 非单个子项的网格布局
            return;
        }
        if (!(lm instanceof LinearLayoutManager)) {
            return;
        }
        final int orientation = ((LinearLayoutManager) lm).getOrientation();
        final int size = mDivider.getSize(orientation);
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            // 水平
            if (parent.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                if (mShowDividerBeginning) {
                    final int position = parent.getChildAdapterPosition(view);
                    if (position == 0) {
                        outRect.right = size;
                    }
                }
                if (mShowDividerMiddle && mShowDividerEnd) {
                    outRect.left = size;
                } else {
                    final int position = parent.getChildAdapterPosition(view);
                    final int itemCount = state.getItemCount();
                    if (position == itemCount - 1) {
                        // 尾项
                        if (mShowDividerEnd) {
                            outRect.left = size;
                        }
                    } else {
                        if (mShowDividerMiddle) {
                            outRect.left = size;
                        }
                    }
                }
            } else {
                if (mShowDividerBeginning) {
                    final int position = parent.getChildAdapterPosition(view);
                    if (position == 0) {
                        outRect.left = size;
                    }
                }
                if (mShowDividerMiddle && mShowDividerEnd) {
                    outRect.right = size;
                } else {
                    final int position = parent.getChildAdapterPosition(view);
                    final int itemCount = state.getItemCount();
                    if (position == itemCount - 1) {
                        // 尾项
                        if (mShowDividerEnd) {
                            outRect.right = size;
                        }
                    } else {
                        if (mShowDividerMiddle) {
                            outRect.right = size;
                        }
                    }
                }
            }
        } else {
            // 垂直
            if (mShowDividerBeginning) {
                final int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.top = size;
                }
            }
            if (mShowDividerMiddle && mShowDividerEnd) {
                outRect.bottom = size;
            } else {
                final int position = parent.getChildAdapterPosition(view);
                final int itemCount = state.getItemCount();
                if (position == itemCount - 1) {
                    // 尾项
                    if (mShowDividerEnd) {
                        outRect.bottom = size;
                    }
                } else {
                    if (mShowDividerMiddle) {
                        outRect.bottom = size;
                    }
                }
            }
        }
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
        if (mShowDividers == SHOW_DIVIDER_NONE) {
            return;
        }
        final RecyclerView.LayoutManager lm = parent.getLayoutManager();
        if (lm instanceof GridLayoutManager && ((GridLayoutManager) lm).getSpanCount() != 1) {
            // 非单个子项的网格布局
            return;
        }
        if (!(lm instanceof LinearLayoutManager)) {
            return;
        }
        final int orientation = ((LinearLayoutManager) lm).getOrientation();
        final int size = mDivider.getSize(orientation);
        final int count = lm.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = lm.getChildAt(i);
            if (child != null) {
                onDrawOver(c, child, parent, state, orientation, size);
            }
        }
    }

    protected void onDrawOver(@NonNull Canvas c, @NonNull View view,
                              @NonNull RecyclerView parent, @NonNull RecyclerView.State state,
                              int orientation, int size) {
        mDivider.onPreDraw(parent, orientation);
        int start;
        int end;
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            // 水平
            if (parent.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                if (mShowDividerBeginning) {
                    final int position = parent.getChildAdapterPosition(view);
                    if (position == 0) {
                        start = view.getRight() + Math.round(view.getTranslationX());
                        end = start + size;
                        mDivider.onDraw(c, orientation, start, end);
                    }
                }
                if (mShowDividerMiddle && mShowDividerEnd) {
                    end = view.getLeft() + Math.round(view.getTranslationX());
                    start = end - size;
                    mDivider.onDraw(c, orientation, start, end);
                } else {
                    final int position = parent.getChildAdapterPosition(view);
                    final int itemCount = state.getItemCount();
                    if (position == itemCount - 1) {
                        // 尾项
                        if (mShowDividerEnd) {
                            end = view.getLeft() + Math.round(view.getTranslationX());
                            start = end - size;
                            mDivider.onDraw(c, orientation, start, end);
                        }
                    } else {
                        if (mShowDividerMiddle) {
                            end = view.getLeft() + Math.round(view.getTranslationX());
                            start = end - size;
                            mDivider.onDraw(c, orientation, start, end);
                        }
                    }
                }
            } else {
                if (mShowDividerBeginning) {
                    final int position = parent.getChildAdapterPosition(view);
                    if (position == 0) {
                        end = view.getLeft() + Math.round(view.getTranslationX());
                        start = end - size;
                        mDivider.onDraw(c, orientation, start, end);
                    }
                }
                if (mShowDividerMiddle && mShowDividerEnd) {
                    start = view.getRight() + Math.round(view.getTranslationX());
                    end = start + size;
                    mDivider.onDraw(c, orientation, start, end);
                } else {
                    final int position = parent.getChildAdapterPosition(view);
                    final int itemCount = state.getItemCount();
                    if (position == itemCount - 1) {
                        // 尾项
                        if (mShowDividerEnd) {
                            start = view.getRight() + Math.round(view.getTranslationX());
                            end = start + size;
                            mDivider.onDraw(c, orientation, start, end);
                        }
                    } else {
                        if (mShowDividerMiddle) {
                            start = view.getRight() + Math.round(view.getTranslationX());
                            end = start + size;
                            mDivider.onDraw(c, orientation, start, end);
                        }
                    }
                }
            }
        } else {
            // 垂直
            if (mShowDividerBeginning) {
                final int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    end = view.getTop() + Math.round(view.getTranslationY());
                    start = end - size;
                    mDivider.onDraw(c, orientation, start, end);
                }
            }
            if (mShowDividerMiddle && mShowDividerEnd) {
                start = view.getBottom() + Math.round(view.getTranslationY());
                end = start + size;
                mDivider.onDraw(c, orientation, start, end);
            } else {
                final int position = parent.getChildAdapterPosition(view);
                final int itemCount = state.getItemCount();
                if (position == itemCount - 1) {
                    // 尾项
                    if (mShowDividerEnd) {
                        start = view.getBottom() + Math.round(view.getTranslationY());
                        end = start + size;
                        mDivider.onDraw(c, orientation, start, end);
                    }
                } else {
                    if (mShowDividerMiddle) {
                        start = view.getBottom() + Math.round(view.getTranslationY());
                        end = start + size;
                        mDivider.onDraw(c, orientation, start, end);
                    }
                }
            }
        }
    }

    public interface Divider {

        /**
         * 获取主轴间隔
         *
         * @param orientation 布局方向
         * @return 主轴间隔
         */
        int getSize(int orientation);

        /**
         * 开始绘制
         *
         * @param parent      RecyclerView
         * @param orientation 布局方向
         */
        default void onPreDraw(@NonNull RecyclerView parent, int orientation) {
        }

        /**
         * 绘制
         *
         * @param canvas      画布
         * @param orientation 布局方向
         * @param start       主轴开始位置（已处理 RTL）
         * @param end         主轴结束位置（已处理 RTL）
         */
        void onDraw(@NonNull Canvas canvas, int orientation, int start, int end);
    }

    public static class DrawableDivider implements Divider {
        private final Drawable mImage;

        public DrawableDivider(Context context, @DrawableRes int divider) {
            mImage = AppCompatResources.getDrawable(context, divider);
        }

        @Override
        public int getSize(int orientation) {
            if (mImage == null) {
                return 0;
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                // 水平
                return Math.max(1, mImage.getIntrinsicWidth());
            } else {
                // 垂直
                return Math.max(1, mImage.getIntrinsicHeight());
            }
        }

        @Override
        public void onPreDraw(@NonNull RecyclerView parent, int orientation) {
            if (mImage == null) {
                return;
            }
            // 设置位置
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                // 水平
                final int paddingTop = parent.getPaddingTop();
                final int maxHeight = parent.getHeight() - paddingTop - parent.getPaddingBottom();
                final int imageWidth = Math.max(1, mImage.getIntrinsicWidth());
                final int intrinsicHeight = mImage.getIntrinsicHeight();
                final int imageHeight = intrinsicHeight == -1 ? maxHeight : intrinsicHeight;
                final int top = Math.round(paddingTop + maxHeight * 0.5f - imageHeight * 0.5f);
                mImage.setBounds(0, top, imageWidth, top + imageHeight);
            } else {
                // 垂直
                final int paddingLeft = parent.getPaddingLeft();
                final int maxWidth = parent.getWidth() - paddingLeft - parent.getPaddingRight();
                final int intrinsicWidth = mImage.getIntrinsicWidth();
                final int imageWidth = intrinsicWidth == -1 ? maxWidth : intrinsicWidth;
                final int imageHeight = Math.max(1, mImage.getIntrinsicHeight());
                final int left = Math.round(paddingLeft + maxWidth * 0.5f - imageWidth * 0.5f);
                mImage.setBounds(left, 0, left + imageWidth, imageHeight);
            }
        }

        @Override
        public void onDraw(@NonNull Canvas canvas, int orientation, int start, int end) {
            if (mImage == null) {
                return;
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                // 水平
                canvas.save();
                canvas.translate(start, 0);
                mImage.draw(canvas);
                canvas.restore();
            } else {
                // 垂直
                canvas.save();
                canvas.translate(0, start);
                mImage.draw(canvas);
                canvas.restore();
            }
        }
    }

    public static class ColorDivider implements Divider {

        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int mSize;
        private final float mRadius;
        private final float mPaddingStart;
        private final float mPaddingEnd;
        private float mStart;
        private float mEnd;

        public ColorDivider(int size, @ColorInt int color, float radius,
                            float paddingStart, float paddingEnd) {
            mSize = size;
            mPaint.setColor(color);
            mRadius = radius;
            mPaddingStart = paddingStart;
            mPaddingEnd = paddingEnd;
        }

        @Override
        public int getSize(int orientation) {
            return mSize;
        }

        @Override
        public void onPreDraw(@NonNull RecyclerView parent, int orientation) {
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                // 水平
                mStart = parent.getPaddingTop() + mPaddingStart;
                mEnd = parent.getHeight() - parent.getPaddingBottom() - mPaddingEnd;
            } else {
                // 垂直
                if (parent.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    mStart = parent.getPaddingLeft() + mPaddingEnd;
                    mEnd = parent.getWidth() - parent.getPaddingRight() - mPaddingStart;
                } else {
                    mStart = parent.getPaddingLeft() + mPaddingStart;
                    mEnd = parent.getWidth() - parent.getPaddingRight() - mPaddingEnd;
                }
            }
        }

        @Override
        public void onDraw(@NonNull Canvas canvas, int orientation, int start, int end) {
            if (mStart >= mEnd) {
                return;
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                // 水平
                if (mRadius > 0) {
                    canvas.drawRoundRect(start, mStart, end, mEnd, mRadius, mRadius, mPaint);
                } else {
                    canvas.drawRect(start, mStart, end, mEnd, mPaint);
                }
            } else {
                // 垂直
                if (mRadius > 0) {
                    canvas.drawRoundRect(mStart, start, mEnd, end, mRadius, mRadius, mPaint);
                } else {
                    canvas.drawRect(mStart, start, mEnd, end, mPaint);
                }
            }
        }
    }
}
