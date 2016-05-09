package com.am.widget.replacelayout;

import android.content.Context;
import android.support.v4.view.BaseTabStripOld.OnTabChangeListener;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 交替布局
 * Created by Alex on 2015/8/28.
 */
public class ReplaceLayout extends ViewGroup implements OnTabChangeListener {

    private ReplaceAdapter mAdapter;
    private int mCorrect = -1;
    private boolean shouldInterceptTouchEvent = false;

    public ReplaceLayout(Context context) {
        super(context);
    }

    public ReplaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReplaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int maxChildWidth = 0;
        int maxChildHeight = 0;
        for (int i = 0; i < getChildCount();i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxChildWidth = maxChildWidth < child.getMeasuredWidth() ? child.getMeasuredWidth() : maxChildWidth;
                maxChildHeight = maxChildHeight < child.getMeasuredHeight() ? child.getMeasuredHeight() : maxChildHeight;
            }
        }
        final int needWidth = Math.max(maxChildWidth + ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this), getSuggestedMinimumWidth());
        final int needHeight = Math.max(maxChildHeight + getPaddingTop() + getPaddingBottom(), getSuggestedMinimumHeight());
        int measuredWidth = 0;
        int measuredHeight = 0;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                measuredHeight = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                measuredHeight = heightSize > needHeight ? needHeight : heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                measuredHeight = needHeight;
                break;
        }
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                measuredWidth = widthSize;
                break;
            case MeasureSpec.AT_MOST:
                measuredWidth = widthSize > needWidth ? needWidth : widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                measuredWidth = needWidth;
                break;
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            child.layout(paddingLeft, paddingTop,
                    paddingLeft + child.getMeasuredWidth(), paddingTop
                            + child.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return shouldInterceptTouchEvent || super.onInterceptTouchEvent(ev);
    }

    @Override
    public void jumpTo(int correct) {
        shouldInterceptTouchEvent = false;
        if (mCorrect != correct) {
            mCorrect = correct;
            removeAllViews();
            if (mAdapter != null) {
                View correctView = mAdapter.getReplaceView(mCorrect);
                LayoutParams lp = mAdapter.getLayoutParams(mCorrect);
                if (lp == null)
                    lp = generateDefaultLayoutParams();
                if (correctView != null)
                    addView(correctView, lp);
                mAdapter.onSelected(this, mCorrect);
            }
        }
    }

    @Override
    public void gotoLeft(int correct, int next, float offset) {
        mCorrect = correct;
        View correctView = null;
        View nextView = null;
        LayoutParams lpC = null;
        LayoutParams lpN = null;

        if (mAdapter != null) {
            correctView = mAdapter.getReplaceView(correct);
            nextView = mAdapter.getReplaceView(next);
            lpC = mAdapter.getLayoutParams(correct);
            lpN = mAdapter.getLayoutParams(next);
        }
        if (lpC == null)
            lpC = generateDefaultLayoutParams();
        if (lpN == null)
            lpN = generateDefaultLayoutParams();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != correctView && child != nextView)
                removeView(child);
        }
        if (correctView != null && correctView.getParent() == null) {
            addView(correctView, lpC);
        }
        if (nextView != null && nextView.getParent() == null) {
            addView(nextView, lpN);
        }
        if (offset == 0) {
            shouldInterceptTouchEvent = false;
            mCorrect = next;
            if (correctView != null && correctView.getParent() != null) {
                removeView(correctView);
            }
            if (mAdapter != null)
                mAdapter.onSelected(this, mCorrect);
        } else {
            shouldInterceptTouchEvent = true;
            if (mAdapter != null)
                mAdapter.onAnimation(this, correct, next, offset);
        }
    }

    @Override
    public void gotoRight(int correct, int next, float offset) {
        mCorrect = correct;
        View correctView = null;
        View nextView = null;
        LayoutParams lpC = null;
        LayoutParams lpN = null;
        if (mAdapter != null) {
            correctView = mAdapter.getReplaceView(correct);
            nextView = mAdapter.getReplaceView(next);
            lpC = mAdapter.getLayoutParams(correct);
            lpN = mAdapter.getLayoutParams(next);
        }
        if (lpC == null)
            lpC = generateDefaultLayoutParams();
        if (lpN == null)
            lpN = generateDefaultLayoutParams();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != correctView && child != nextView)
                removeView(child);
        }
        if (correctView != null && correctView.getParent() == null) {
            addView(correctView, lpC);
        }
        if (nextView != null && nextView.getParent() == null) {
            addView(nextView, lpN);
        }
        if (offset == 1) {
            shouldInterceptTouchEvent = false;
            mCorrect = next;
            if (correctView != null && correctView.getParent() != null) {
                removeView(correctView);
            }
            if (mAdapter != null)
                mAdapter.onSelected(this, mCorrect);
        } else {
            shouldInterceptTouchEvent = true;
            if (mAdapter != null)
                mAdapter.onAnimation(this, correct, next, 1F - offset);
        }
    }

    public void setAdapter(ReplaceAdapter adapter) {
        this.mAdapter = adapter;
        removeAllViews();
        if (mAdapter != null && mCorrect != -1) {
            View correctView = mAdapter.getReplaceView(mCorrect);
            LayoutParams lp = mAdapter.getLayoutParams(mCorrect);
            if (lp == null)
                lp = generateDefaultLayoutParams();
            if (correctView != null)
                addView(correctView, lp);
            mAdapter.onSelected(this, mCorrect);
        }
    }

    public interface ReplaceAdapter {
        LayoutParams getLayoutParams(int position);

        View getReplaceView(int position);

        void onAnimation(ViewGroup replace, int correct, int next, float offset);

        void onSelected(ViewGroup replace, int position);
    }

}
