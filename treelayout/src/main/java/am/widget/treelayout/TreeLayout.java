package am.widget.treelayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
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
    private final Rect mMarkRect = new Rect();

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
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
        private boolean isMarkView = false;
        private float mCenterX;
        private float mCenterY;

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

        public boolean isMarkView() {
            return isMarkView;
        }

        private void layout(View child) {
            final int left = (int) (mCenterX - child.getMeasuredWidth() * 0.5f);
            final int top = (int) (mCenterY - child.getMeasuredHeight() * 0.5f);
            child.layout(left, top, left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

        }
        int width = 0;
        int height = 0;
        if (!mExpand) {
            // TODO
        } else {
            // TODO
        }
        width += paddingStart + paddingEnd;
        height += paddingTop + paddingBottom;
        setMeasuredDimension(resolveSize(Math.max(width, getSuggestedMinimumWidth()), widthMeasureSpec),
                resolveSize(Math.max(height, getSuggestedMinimumWidth()), heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.isMarkView()) {
                child.layout(mMarkRect.left, mMarkRect.top, mMarkRect.right, mMarkRect.bottom);
            } else {
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
        mExpand = expand;
        requestLayout();
    }

    /**
     * 判断是否已展开
     *
     * @return 是否已展开
     */
    public boolean isExpand() {
        return mExpand;
    }
}
