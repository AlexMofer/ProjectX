package com.am.widget.wraplayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自动换行布局
 * Created by Alex on 2015/9/17.
 */
public class WrapLayout extends ViewGroup {

    private static final int[] ATTRS = new int[]{android.R.attr.horizontalSpacing,
            android.R.attr.verticalSpacing};
    private int mVerticalSpacing = 0;
    private int mHorizontalSpacing = 0;
    private int mNumRows = 0;

    public WrapLayout(Context context) {
        super(context);
    }


    @SuppressWarnings("ResourceType")
    public WrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mHorizontalSpacing = a.getDimensionPixelSize(0, 0);
        mVerticalSpacing = a.getDimensionPixelSize(1, 0);
        a.recycle();
    }

    @SuppressWarnings("ResourceType")
    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mHorizontalSpacing = a.getDimensionPixelSize(0, 0);
        mVerticalSpacing = a.getDimensionPixelSize(1, 0);
        a.recycle();
    }

    @TargetApi(21)
    @SuppressWarnings("ResourceType")
    public WrapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mHorizontalSpacing = a.getDimensionPixelSize(0, 0);
        mVerticalSpacing = a.getDimensionPixelSize(1, 0);
        a.recycle();
    }

    public void setHorizontalSpacing(int pixelSize) {
        mHorizontalSpacing = pixelSize;
    }

    public void setVerticalSpacing(int pixelSize) {
        mVerticalSpacing = pixelSize;
    }

    public int getNumRows() {
        return mNumRows;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        if (getChildCount() <= 0) {
            mNumRows = 0;
            setMeasuredDimension(resolveSize(Math.max(paddingStart + paddingEnd, getMinWidth()), widthMeasureSpec),
                    resolveSize(Math.max(paddingTop + paddingBottom, getMinHeight()), heightMeasureSpec));
            return;
        } else {
            mNumRows = 1;
        }
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int needWidth = 0;
        int needHeight = 0;
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            for (int i = 0; i < getChildCount(); ++i) {
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                needWidth += childWidth + mHorizontalSpacing;
                needHeight = Math.max(childHeight, needHeight);
            }
            needWidth = Math.max(needWidth - mHorizontalSpacing + paddingStart + paddingEnd, getMinWidth());
            needHeight = Math.max(needHeight, getMinHeight());
            setMeasuredDimension(needWidth, resolveSize(needHeight, heightMeasureSpec));
            return;
        }
        final int maxEnd = widthSize - paddingEnd;
        needWidth = paddingStart + paddingEnd;
        needHeight = paddingTop + paddingBottom;
        int rowHeight = 0;
        for (int i = 0, childLeft = paddingStart, rowItemMaxHeight = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            rowItemMaxHeight = Math.max(childHeight, rowItemMaxHeight);
            if (childLeft + childWidth <= maxEnd) {
                childLeft += childWidth + mHorizontalSpacing;
                rowHeight = rowItemMaxHeight;
            } else {
                mNumRows++;
                needWidth = Math.max(needWidth, childLeft + paddingEnd);
                needHeight += rowHeight + mVerticalSpacing;
                childLeft = paddingStart + childWidth + mHorizontalSpacing;
                rowItemMaxHeight = childHeight;
                rowHeight = rowItemMaxHeight;
            }
        }
        needHeight += rowHeight;
        setMeasuredDimension(resolveSize(Math.max(needWidth, getMinWidth()), widthMeasureSpec), resolveSize(Math.max(needHeight, getMinHeight()), heightMeasureSpec));
    }

    private int getMinWidth() {
        int minWidth = 0;
        final Drawable bg = getBackground();
        if (bg != null) {
            minWidth = bg.getIntrinsicWidth();
        }
        return minWidth;
    }

    private int getMinHeight() {
        int minHeight = 0;
        final Drawable bg = getBackground();
        if (bg != null) {
            minHeight = bg.getIntrinsicHeight();
        }
        return minHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingTop = getPaddingTop();
        final int itemEnd = r - l - paddingEnd;
        for (int i = 0, childLeft = paddingStart, childTop = paddingTop, rowItemMaxHeight = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            final int childWidth = childView.getMeasuredWidth();
            final int childHeight = childView.getMeasuredHeight();
            rowItemMaxHeight = Math.max(childHeight, rowItemMaxHeight);
            if (childLeft + childWidth <= itemEnd) {
                childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                childLeft += childWidth + mHorizontalSpacing;

            } else {
                childLeft = paddingStart;
                childTop += rowItemMaxHeight + mVerticalSpacing;
                rowItemMaxHeight = 0;
                i--;
            }
        }
    }
}
