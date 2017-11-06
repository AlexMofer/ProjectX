package am.project.x.widgets.display;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import am.widget.multifunctionalrecyclerview.layoutmanager.BothDirectionsScrollLayoutManager;

/**
 * 分页LayoutManager
 * Created by Alex on 2017/11/6.
 */

public class PagingLayoutManager extends BothDirectionsScrollLayoutManager {


    private static final int PAGE_IGNORE_DISTANCE = 3;// 3个像素点内不移动
    private final PagingOnFlingListener mFlingListener = new PagingOnFlingListener();
    private final PagingSmoothScroller mSmoothScroller;
    private final Rect mChildBound = new Rect();
    private boolean mPagingEnable = true;
    private RecyclerView mView;

    public PagingLayoutManager(Context context) {
        super(context);
        mSmoothScroller = new PagingSmoothScroller(context);
    }

    public PagingLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mSmoothScroller = new PagingSmoothScroller(context);
    }

    public PagingLayoutManager(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mSmoothScroller = new PagingSmoothScroller(context);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        mView = view;
        super.onAttachedToWindow(view);
        view.setOnFlingListener(mFlingListener);
    }

    @Override
    public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(view, recycler);
        view.setOnFlingListener(null);
        mView = null;
    }

    @Override
    public void measureChildWithMargins(View child, int widthUsed, int heightUsed) {
        if (!mPagingEnable) {
            super.measureChildWithMargins(child, widthUsed, heightUsed);
            return;
        }
        final RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        if (getOrientation() == HORIZONTAL) {
            lp.leftMargin = 0;
            lp.rightMargin = 0;
        } else {
            lp.topMargin = 0;
            lp.bottomMargin = 0;
        }
        super.measureChildWithMargins(child, widthUsed, heightUsed);
        if (getOrientation() == HORIZONTAL) {
            int maxMargin = getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
                    - child.getMeasuredWidth()
                    - getLeftDecorationWidth(child) - getRightDecorationWidth(child);
            maxMargin = maxMargin < 0 ? 0 : maxMargin;
            final int start = getPagingMarginStart(maxMargin);
            lp.leftMargin = start;
            lp.rightMargin = maxMargin - start;
        } else {
            int maxMargin = getMeasuredHeight() - getPaddingTop() - getPaddingBottom()
                    - child.getMeasuredHeight()
                    - getTopDecorationHeight(child) - getBottomDecorationHeight(child);
            maxMargin = maxMargin < 0 ? 0 : maxMargin;
            final int start = getPagingMarginStart(maxMargin);
            lp.topMargin = start;
            lp.bottomMargin = maxMargin - start;
        }
    }

    protected int getPagingMarginStart(int maxMargin) {
        if (isLayoutInCenter()) {
            return maxMargin / 2;
        }
        return 0;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
//        if (state == RecyclerView.SCROLL_STATE_IDLE) {
//            if (getOrientation() == HORIZONTAL) {
//                adjustPagingHorizontal(true);
//            } else {
//                adjustPagingVertically(true);
//            }
//        }
    }

    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        super.onLayoutCompleted(state);
//        if (getOrientation() == HORIZONTAL) {
//            adjustPagingHorizontal(false);
//        } else {
//            adjustPagingVertically(false);
//        }
    }

    protected boolean adjustPagingHorizontal(boolean smooth) {
        if (mView == null)
            return false;
        final int childCount = getChildCount();
        if (childCount < 1)
            return false;
        final View child = getChildAt(0);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                child.getLayoutParams();
        final int edgeLeft = getPaddingLeft();
        final int edgeRight = getWidth() - getPaddingRight();
        final float centerX = (edgeRight - edgeLeft) * 0.5f;
        final int childLeft = getDecoratedLeft(child) - params.leftMargin;
        final int childRight = getDecoratedRight(child) + params.rightMargin;
        // 子View只可能大于或等于View显示区域
        if (childLeft == edgeLeft)
            // 刚好紧贴左边框
            return false;
        if (childRight == edgeRight)
            // 刚好紧贴右边框
            return false;
        if (childLeft < edgeLeft && childRight > edgeRight)
            // 子View区域大于View显示区域
            return false;
        final int moveX;
        if (childRight <= centerX) {
            final View nextChild = getChildAt(1);
            if (null == nextChild)
                return false;
            final RecyclerView.LayoutParams nextParams = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            moveX = getDecoratedLeft(nextChild) - nextParams.leftMargin - edgeLeft;
        } else if (childRight <= edgeRight) {
            moveX = -(edgeRight - childRight);
        } else {
            moveX = childLeft - edgeLeft;
        }
        if (smooth)
            mView.smoothScrollBy(moveX, 0);
        else
            mView.scrollBy(moveX, 0);
        return smooth;
    }

    protected boolean adjustPagingVertically(boolean smooth) {
        if (mView == null)
            return false;
        final int childCount = getChildCount();
        if (childCount < 1)
            return false;
        final View child = getChildAt(0);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                child.getLayoutParams();
        final float centerY = (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f;
        final int edgeTop = getPaddingTop();
        final int edgeBottom = getHeight() - getPaddingBottom();
        final int childTop = getDecoratedTop(child) - params.topMargin;
        final int childBottom = getDecoratedBottom(child) + params.bottomMargin;
        System.out.println("adjustPagingVertically------------------------------------------------------------");
        System.out.println("adjustPagingVertically---------------------------------------------ptop:" + edgeTop);
        System.out.println("adjustPagingVertically---------------------------------------------ptop:" + edgeBottom);
        System.out.println("adjustPagingVertically---------------------------------------------cbtn:" + childTop);
        System.out.println("adjustPagingVertically---------------------------------------------cbtn:" + childBottom);
        // 子View只可能大于或等于View显示区域
        if (childTop == edgeTop)
            // 刚好紧贴顶边框
            return false;
        if (childBottom == edgeBottom)
            // 刚好紧贴底边框
            return false;
        if (childTop < edgeTop && childBottom > edgeBottom)
            // 子View区域大于View显示区域
            return false;
        final int moveY;
        if (childBottom <= centerY) {
            final View nextChild = getChildAt(1);
            if (null == nextChild)
                return false;
            final RecyclerView.LayoutParams nextParams = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            moveY = getDecoratedTop(nextChild) - nextParams.topMargin - edgeTop;
        } else if (childBottom <= edgeBottom) {
            moveY = -(edgeBottom - childBottom);
        } else {
            moveY = childTop - edgeTop;
        }
        if (smooth)
            mView.smoothScrollBy(0, moveY);
        else
            mView.scrollBy(0, moveY);
        return smooth;
    }


    boolean fling(int velocityX, int velocityY) {
        if (!mPagingEnable)
            return false;
        return false;
    }

    public boolean isPagingEnable() {
        return mPagingEnable;
    }

    public void setPagingEnable(boolean enable) {
        mPagingEnable = enable;
        requestLayout();
    }

    private class PagingOnFlingListener extends RecyclerView.OnFlingListener {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            return fling(velocityX, velocityY);
        }
    }
}
