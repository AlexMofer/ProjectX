package am.widget.draglayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 拖动视图
 * Created by Alex on 2016/12/7.
 */

public class DragLayout extends ViewGroup {

    private final DragPadding mDragPadding = new DragPadding();
    private boolean draggable = true;
    private ViewDragHelper mDragHelper;
    private final Callback callback = new Callback();

    public DragLayout(Context context) {
        super(context);
        initView(null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    @TargetApi(11)
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        int startTop = 0;
        int startBottom = 0;
        int endTop = 0;
        int endBottom = 0;
        TypedArray custom = getContext().obtainStyledAttributes(attrs,
                R.styleable.DragLayout);
        if (custom.hasValue(R.styleable.DragLayout_dlDragPadding)) {
            final int padding = custom.getDimensionPixelOffset(R.styleable.DragLayout_dlDragPadding,
                    0);
            startTop = padding;
            startBottom = padding;
            endTop = padding;
            endBottom = padding;
        }
        startTop = custom.getDimensionPixelOffset(R.styleable.DragLayout_dlDragPaddingStartTop,
                startTop);
        startBottom = custom.getDimensionPixelOffset(R.styleable.DragLayout_dlDragPaddingStartBottom,
                startBottom);
        endTop = custom.getDimensionPixelOffset(R.styleable.DragLayout_dlDragPaddingEndTop,
                endTop);
        endBottom = custom.getDimensionPixelOffset(R.styleable.DragLayout_dlDragPaddingEndBottom,
                endBottom);

        custom.recycle();
        setDragPadding(startTop, startBottom, endTop, endBottom);
        mDragHelper = ViewDragHelper.create(this, 0.5f, callback);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            layoutParams.setLocation(mDragPadding, widthSize, heightSize,
                    paddingStart, paddingTop, paddingEnd, paddingBottom);
        }
        setMeasuredDimension(resolveSize(widthSize, widthMeasureSpec),
                resolveSize(heightSize, heightMeasureSpec));
    }

    @SuppressWarnings("all")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int childLeft = layoutParams.isOnStartSide() ? layoutParams.mX :
                    (layoutParams.mX - child.getMeasuredWidth());
            final int childTop = layoutParams.mCenterY - child.getMeasuredHeight() / 2;
            child.layout(childLeft, childTop,
                    childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            if (!draggable) {
                mDragHelper.abort();
                return;
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {

        private static final int GRAVITY_START_TOP = 0;
        private static final int GRAVITY_END_TOP = 1;
        private static final int GRAVITY_START_CENTER = 2;
        private static final int GRAVITY_END_CENTER = 3;
        private static final int GRAVITY_START_BOTTOM = 4;
        private static final int GRAVITY_END_BOTTOM = 5;

        private int mX;
        private int mCenterY;
        private int mGravity = GRAVITY_START_TOP;
        private int mPaddingStart;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int gravity = GRAVITY_START_TOP;
            TypedArray custom = c.obtainStyledAttributes(attrs, R.styleable.DragLayout_Layout);
            gravity = custom.getInt(R.styleable.DragLayout_Layout_layout_gravity, gravity);
            custom.recycle();
            setGravity(gravity);
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
            // TODO
        }

        public void setGravity(int gravity) {
            mGravity = gravity;
        }

        public boolean isOnStartSide() {
            return mX == mPaddingStart;
        }

        public void setLocation(DragPadding dragPadding, int parentWidth, int parentHeight,
                                int paddingStart, int paddingTop, int paddingEnd,
                                int paddingBottom) {
            mPaddingStart = paddingStart;
            if (dragPadding == null)
                return;
            switch (mGravity) {
                default:
                case GRAVITY_START_TOP:
                    mX = paddingStart;
                    mCenterY = paddingTop + dragPadding.mStartTop;
                    break;
                case GRAVITY_END_TOP:
                    mX = parentWidth - paddingEnd;
                    mCenterY = paddingTop + dragPadding.mStartTop;
                    break;
                case GRAVITY_START_CENTER:
                    mX = paddingStart;
                    mCenterY = paddingTop + dragPadding.mStartTop +
                            (parentHeight - paddingTop - paddingBottom
                                    - dragPadding.mStartTop - dragPadding.mStartBottom) / 2;
                    break;
                case GRAVITY_END_CENTER:
                    mX = parentWidth - paddingEnd;
                    mCenterY = paddingTop + dragPadding.mStartTop +
                            (parentHeight - paddingTop - paddingBottom
                                    - dragPadding.mStartTop - dragPadding.mStartBottom) / 2;
                    break;
                case GRAVITY_START_BOTTOM:
                    mX = paddingStart;
                    mCenterY = parentHeight - paddingBottom - dragPadding.mStartBottom;
                    break;
                case GRAVITY_END_BOTTOM:
                    mX = parentWidth - paddingEnd;
                    mCenterY = parentHeight - paddingBottom - dragPadding.mStartBottom;
                    break;
            }
        }
    }

    private class DragPadding {
        int mStartTop;
        int mEndTop;
        int mStartBottom;
        int mEndBottom;

        public void set(int startTop, int startBottom, int endTop, int endBottom) {
            mStartTop = startTop;
            mEndTop = endTop;
            mStartBottom = startBottom;
            mEndBottom = endBottom;
        }
    }

    private class Callback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return draggable;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int finalLeft = 0;
            int finalTop = 0;
            if (xvel == 0 && yvel == 0) {
                // 无飞行操作
            } else if (xvel == 0) {
                // 垂直飞行
            } else if (yvel == 0) {
                // 水平飞行
            } else if (xvel > 0 && yvel < 0) {
                // 第一象限
            } else if (xvel < 0 && yvel < 0) {
                // 第二象限
            } else if (xvel < 0 && yvel > 0) {
                // 第三象限
            } else {
                // 第四象限
            }

            System.out.println("xvel=" + xvel + ";yvel=" + yvel);
            // TODO
            mDragHelper.settleCapturedViewAt(finalLeft, finalTop);
            invalidate();
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                // 更新位置
                // TODO
            }
        }
    }

    /**
     * 设置拖动填充
     *
     * @param startTop    左上
     * @param startBottom 左下
     * @param endTop      右上
     * @param endBottom   右下
     */
    public void setDragPadding(int startTop, int startBottom, int endTop, int endBottom) {
        mDragPadding.set(startTop, startBottom, endTop, endBottom);
        requestLayout();
    }

    /**
     * 判断是否可拖动
     *
     * @return 是否可拖动
     */
    @SuppressWarnings("unused")
    public boolean isDraggable() {
        return draggable;
    }

    /**
     * 设置是否可拖动
     *
     * @param draggable 是否可拖动
     */
    @SuppressWarnings("unused")
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
}
