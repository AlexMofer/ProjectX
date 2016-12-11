package am.widget.treelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 树布局
 * Created by Alex on 2016/12/8.
 */

public class TreeLayout extends ViewGroup {

    private boolean mExpand = false;
    private boolean right = false;
    private int offset;
    private long itemDuration = 2000;
    private long durationDelay = 200;

    public TreeLayout(Context context) {
        super(context);
    }

    public TreeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public TreeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT} and no spanning.
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        //        private float mCenterX;
//        private float mCenterY;
        private int mLeft, mTop, mRight, mBottom;
        private int mRootLeft, mRootTop;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }

        void setLayoutRect(int left, int top, int right, int bottom) {
            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;
        }

        private void layout(View child) {
            child.layout(mLeft, mTop, mRight, mBottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        offset = (int) (100 * getResources().getDisplayMetrics().density);

        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                layoutParams.setLayoutRect(0, 0, 0, 0);
            }
        }
        int width = 0;
        int height = 0;
        if (!mExpand) {
            // TODO
            View child = getChildAt(5);
            if (child != null) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                width += childWidth;
                height += childHeight;
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                layoutParams.setLayoutRect(paddingStart, paddingTop,
                        paddingStart + childWidth, paddingTop + childHeight);
            }
        } else {
            // TODO
            View childRoot = getChildAt(5);
            final int childRootWidth = childRoot == null ? 0 : childRoot.getMeasuredWidth();

            View childTop = getChildAt(0);
            final int childTopHeight = childTop == null ? 0 : childTop.getMeasuredHeight();

            View childCenter = getChildAt(2);
            final int childCenterWidth = childCenter == null ? 0 : childCenter.getMeasuredWidth();

            View childBottom = getChildAt(4);
            final int childBottomHeight = childBottom == null ? 0 : childBottom.getMeasuredHeight();

            width += (int) (childRootWidth * 0.5f + childCenterWidth * 0.5f + offset);
            height += (int) (childTopHeight * 0.5f + childBottomHeight * 0.5f + offset + offset);

        }
        width += paddingStart + paddingEnd;
        height += paddingTop + paddingBottom;
        setMeasuredDimension(resolveSize(Math.max(width, getSuggestedMinimumWidth()), widthMeasureSpec),
                resolveSize(Math.max(height, getSuggestedMinimumWidth()), heightMeasureSpec));

        if (mExpand) {
            final int measuredWidth = getMeasuredWidth();
            final int measuredHeight = getMeasuredHeight();
            final float centerY = (measuredHeight - paddingTop - paddingBottom) * 0.5f;
            View childRoot = getChildAt(5);
            final int childRootWidth = childRoot == null ? 0 : childRoot.getMeasuredWidth();
            final int childRootHeight = childRoot == null ? 0 : childRoot.getMeasuredHeight();

            View childTop = getChildAt(0);
            final int childTopWidth = childTop == null ? 0 : childTop.getMeasuredWidth();
            final int childTopHeight = childTop == null ? 0 : childTop.getMeasuredHeight();

            View childCenter = getChildAt(2);
            final int childCenterWidth = childCenter == null ? 0 : childCenter.getMeasuredWidth();
            final int childCenterHeight = childCenter == null ? 0 : childCenter.getMeasuredHeight();

            View childBottom = getChildAt(4);
            final int childBottomWidth = childCenter == null ? 0 : childCenter.getMeasuredWidth();
            final int childBottomHeight = childBottom == null ? 0 : childBottom.getMeasuredHeight();

            final double offset45 = offset * Math.sin(Math.toRadians(45));

            if (right) {
                final float rootCenterX = measuredWidth - paddingEnd - childRootWidth * 0.5f;
                if (childRoot != null) {
                    LayoutParams layoutParams = (LayoutParams) childRoot.getLayoutParams();
                    final int left = measuredWidth - paddingEnd - childRootWidth;
                    final int top = (int) (centerY - childRootHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + childRootWidth, top + childRootHeight);
                }
                if (childTop != null) {
                    LayoutParams layoutParams = (LayoutParams) childTop.getLayoutParams();
                    final int left = measuredWidth - paddingEnd - childTopWidth;
                    final int top = paddingTop;
                    layoutParams.setLayoutRect(left, top,
                            left + childTopWidth, top + childTopHeight);
                }
                View leftTop = getChildAt(1);
                if (leftTop != null) {

                    final int leftTopWidth = leftTop.getMeasuredWidth();
                    final int leftTopHeight = leftTop.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) leftTop.getLayoutParams();
                    final int left = (int) (rootCenterX - offset45 - leftTopWidth * 0.5f);
                    final int top = (int) (centerY - offset45 - leftTopHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + leftTopWidth, top + leftTopHeight);
                }
                if (childCenter != null) {
                    LayoutParams layoutParams = (LayoutParams) childCenter.getLayoutParams();
                    final int left = paddingStart;
                    final int top = (int) (centerY - childCenterHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + childCenterWidth, top + childCenterHeight);
                }
                View rightBottom = getChildAt(3);
                if (rightBottom != null) {
                    final int rightBottomWidth = rightBottom.getMeasuredWidth();
                    final int rightBottomHeight = rightBottom.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) rightBottom.getLayoutParams();
                    final int left = (int) (rootCenterX - offset45 - rightBottomWidth * 0.5f);
                    final int top = (int) (centerY + offset45 - rightBottomHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + rightBottomWidth, top + rightBottomHeight);
                }
                if (childBottom != null) {
                    LayoutParams layoutParams = (LayoutParams) childBottom.getLayoutParams();
                    final int left = measuredWidth - paddingEnd - childBottomWidth;
                    final int top = measuredHeight - paddingBottom - childBottomHeight;
                    layoutParams.setLayoutRect(left, top,
                            left + childBottomWidth, top + childBottomHeight);
                }
            } else {
                final float rootCenterX = paddingStart + childRootWidth * 0.5f;
                if (childRoot != null) {
                    LayoutParams layoutParams = (LayoutParams) childRoot.getLayoutParams();
                    final int left = paddingStart;
                    final int top = (int) (centerY - childRootHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + childRootWidth, top + childRootHeight);
                }
                if (childTop != null) {
                    LayoutParams layoutParams = (LayoutParams) childTop.getLayoutParams();
                    final int left = paddingStart;
                    final int top = paddingTop;
                    layoutParams.setLayoutRect(left, top,
                            left + childTopWidth, top + childTopHeight);
                }
                View leftTop = getChildAt(1);
                if (leftTop != null) {
                    final int leftTopWidth = leftTop.getMeasuredWidth();
                    final int leftTopHeight = leftTop.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) leftTop.getLayoutParams();
                    final int left = (int) (rootCenterX + offset45 - leftTopWidth * 0.5f);
                    final int top = (int) (centerY - offset45 - leftTopHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + leftTopWidth, top + leftTopHeight);
                }
                if (childCenter != null) {
                    LayoutParams layoutParams = (LayoutParams) childCenter.getLayoutParams();
                    final int left = measuredWidth - paddingEnd - childRootWidth;
                    final int top = (int) (centerY - childCenterHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + childCenterWidth, top + childCenterHeight);
                }
                View rightBottom = getChildAt(3);
                if (rightBottom != null) {
                    final int rightBottomWidth = rightBottom.getMeasuredWidth();
                    final int rightBottomHeight = rightBottom.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) rightBottom.getLayoutParams();
                    final int left = (int) (rootCenterX + offset45 - rightBottomWidth * 0.5f);
                    final int top = (int) (centerY + offset45 - rightBottomHeight * 0.5f);
                    layoutParams.setLayoutRect(left, top,
                            left + rightBottomWidth, top + rightBottomHeight);
                }
                if (childBottom != null) {
                    LayoutParams layoutParams = (LayoutParams) childBottom.getLayoutParams();
                    final int left = paddingStart;
                    final int top = measuredHeight - paddingBottom - childBottomHeight;
                    layoutParams.setLayoutRect(left, top,
                            left + childBottomWidth, top + childBottomHeight);
                }
            }

        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                layoutParams.layout(child);
            }
        }
    }

    /**
     * 设置是否展开
     *
     * @param expand 展开
     */
    public void setExpand(boolean expand) {
        setExpand(expand, true);
    }

    /**
     * 设置是否展开
     *
     * @param expand 展开
     */
    public void setExpand(boolean expand, boolean animator) {
        if (mExpand == expand)
            return;
        mExpand = expand;
        if (animator & !mExpand) {
            // 关闭动画
        }
        requestLayout();
        if (animator & mExpand) {
            // 打开动画
        }
    }

    /**
     * 判断是否已展开
     *
     * @return 是否已展开
     */
    public boolean isExpand() {
        return mExpand;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}
