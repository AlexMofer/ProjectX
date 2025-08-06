/*
 * Copyright (C) 2024 AlexMofer
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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * DividerItemDecoration is a RecyclerView.ItemDecoration that can be used as a divider between
 * items of a LinearLayoutManager. It supports both HORIZONTAL and VERTICAL orientations.
 * Created by Alex on 2024/4/26.
 */
@Deprecated
public class DividerItemDecorationD extends RecyclerView.ItemDecoration {
    private final Rect mBounds = new Rect();
    private final Drawable mDivider;
    private final boolean mShowBeginning;
    private final boolean mShowMiddle;
    private final boolean mShowEnd;
    private final int mDividerPadding;
    private final int mDividerWidth;
    private final int mDividerHeight;

    public DividerItemDecorationD(Drawable divider, int showDividers, int dividerPadding) {
        mDivider = divider;
        mShowBeginning = (showDividers & LinearLayout.SHOW_DIVIDER_BEGINNING)
                == LinearLayout.SHOW_DIVIDER_BEGINNING;
        mShowMiddle = (showDividers & LinearLayout.SHOW_DIVIDER_MIDDLE)
                == LinearLayout.SHOW_DIVIDER_MIDDLE;
        mShowEnd = (showDividers & LinearLayout.SHOW_DIVIDER_END)
                == LinearLayout.SHOW_DIVIDER_END;
        mDividerPadding = dividerPadding;
        if (divider == null) {
            mDividerWidth = 0;
            mDividerHeight = 0;
        } else {
            mDividerWidth = divider.getIntrinsicWidth();
            mDividerHeight = divider.getIntrinsicHeight();
        }
    }

    public DividerItemDecorationD(Drawable divider, int showDividers) {
        this(divider, showDividers, 0);
    }

    public DividerItemDecorationD(Drawable divider) {
        this(divider, LinearLayout.SHOW_DIVIDER_MIDDLE, 0);
    }

    public DividerItemDecorationD(Context context, @DrawableRes int divider,
                                  int showDividers, int dividerPadding) {
        this(AppCompatResources.getDrawable(context, divider), showDividers, dividerPadding);
    }

    public DividerItemDecorationD(Context context, @DrawableRes int divider, int showDividers) {
        this(AppCompatResources.getDrawable(context, divider), showDividers, 0);
    }

    public DividerItemDecorationD(Context context, @DrawableRes int divider) {
        this(AppCompatResources.getDrawable(context, divider),
                LinearLayout.SHOW_DIVIDER_MIDDLE, 0);
    }

    public DividerItemDecorationD(int showDividers, int dividerHeight) {
        this(new TransparentDividerDrawable(dividerHeight), showDividers, 0);
    }

    public DividerItemDecorationD(int dividerHeight) {
        this(new TransparentDividerDrawable(dividerHeight),
                LinearLayout.SHOW_DIVIDER_MIDDLE, 0);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mDivider == null) {
            return;
        }
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (!(manager instanceof LinearLayoutManager)) {
            return;
        }
        final LinearLayoutManager lm = (LinearLayoutManager) manager;
        if (lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            getItemOffsetsHorizontal(outRect, view, lm);
        } else {
            getItemOffsetsVertical(outRect, view, lm);
        }
    }

    private void getItemOffsetsHorizontal(Rect outRect, View view, LinearLayoutManager manager) {
        final int position = manager.getPosition(view);
        final int itemCount = manager.getItemCount();
        if (position == 0) {
            // 首项
            if (mShowBeginning) {
                outRect.left = mDividerWidth;
            }
        }
        if (position == itemCount - 1) {
            // 尾项
            if (mShowEnd) {
                outRect.right = mDividerWidth;
            }
        } else {
            // 非尾项
            if (mShowMiddle) {
                outRect.right = mDividerWidth;
            }
        }
    }

    private void getItemOffsetsVertical(Rect outRect, View view, LinearLayoutManager manager) {
        final int position = manager.getPosition(view);
        final int itemCount = manager.getItemCount();
        if (position == 0) {
            // 首项
            if (mShowBeginning) {
                outRect.top = mDividerHeight;
            }
        }
        if (position == itemCount - 1) {
            // 尾项
            if (mShowEnd) {
                outRect.bottom = mDividerHeight;
            }
        } else {
            // 非尾项
            if (mShowMiddle) {
                outRect.bottom = mDividerHeight;
            }
        }
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mDivider == null) {
            return;
        }
        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (!(manager instanceof LinearLayoutManager)) {
            return;
        }
        final LinearLayoutManager lm = (LinearLayoutManager) manager;
        if (lm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            drawHorizontal(c, parent, lm);
        } else {
            drawVertical(c, parent, lm);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent, LinearLayoutManager manager) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop() + mDividerPadding;
            bottom = parent.getHeight() - parent.getPaddingBottom() - mDividerPadding;
            canvas.clipRect(parent.getPaddingLeft(), top,
                    parent.getWidth() - parent.getPaddingRight(), bottom);
        } else {
            top = mDividerPadding;
            bottom = parent.getHeight() - mDividerPadding;
        }
        final int itemCount = manager.getItemCount();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int position = manager.getPosition(child);
            if (position == 0) {
                // 首项
                if (mShowBeginning) {
                    final int left = mBounds.left + Math.round(child.getTranslationX());
                    final int right = left + mDivider.getIntrinsicWidth();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            }
            if (position == itemCount - 1) {
                // 尾项
                if (mShowEnd) {
                    final int right = mBounds.right + Math.round(child.getTranslationX());
                    final int left = right - mDivider.getIntrinsicWidth();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            } else {
                // 非尾项
                if (mShowMiddle) {
                    final int right = mBounds.right + Math.round(child.getTranslationX());
                    final int left = right - mDivider.getIntrinsicWidth();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            }
        }
        canvas.restore();
    }

    private void drawVertical(Canvas canvas, RecyclerView parent, LinearLayoutManager manager) {
        canvas.save();
        final int left;
        final int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft() + mDividerPadding;
            right = parent.getWidth() - parent.getPaddingRight() - mDividerPadding;
            canvas.clipRect(left, parent.getPaddingTop(), right,
                    parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = mDividerPadding;
            right = parent.getWidth() - mDividerPadding;
        }
        final int itemCount = manager.getItemCount();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            parent.getDecoratedBoundsWithMargins(child, mBounds);
            final int position = manager.getPosition(child);
            if (position == 0) {
                // 首项
                if (mShowBeginning) {
                    final int top = mBounds.top + Math.round(child.getTranslationY());
                    final int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            }
            if (position == itemCount - 1) {
                // 尾项
                if (mShowEnd) {
                    final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                    final int top = bottom - mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            } else {
                // 非尾项
                if (mShowMiddle) {
                    final int bottom = mBounds.bottom + Math.round(child.getTranslationY());
                    final int top = bottom - mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(canvas);
                }
            }
        }
        canvas.restore();
    }

    private static class TransparentDividerDrawable extends
            io.github.alexmofer.android.support.graphics.drawable.Drawable {

        private final int mSize;

        public TransparentDividerDrawable(int size) {
            mSize = size;
        }

        @Override
        public int getIntrinsicWidth() {
            return mSize;
        }

        @Override
        public int getIntrinsicHeight() {
            return mSize;
        }
    }
}
