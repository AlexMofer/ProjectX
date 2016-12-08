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
 * TODO 增加不控制贴边处理
 * Created by Alex on 2016/12/7.
 */

public class DragLayout extends ViewGroup {


    private float mCenterLine;
    private ViewDragHelper mDragHelper;
    private final Callback callback = new Callback();

    public DragLayout(Context context) {
        super(context);
        initView();
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @TargetApi(11)
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
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
        final int paddingStart = ViewCompat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = ViewCompat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            if (layoutParams.shouldSetLocation())
                layoutParams.setLocation(widthSize, heightSize,
                        paddingStart, paddingTop, paddingEnd, paddingBottom);
            else
                layoutParams.checkLocation(widthSize, heightSize,
                        paddingStart, paddingTop, paddingEnd, paddingBottom);
        }
        setMeasuredDimension(resolveSize(widthSize, widthMeasureSpec),
                resolveSize(heightSize, heightMeasureSpec));
        mCenterLine = (getMeasuredWidth() - paddingStart - paddingBottom) * 0.5f;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int dragStart = ViewCompat.getPaddingStart(this) + layoutParams.mDragPaddingStart;
            final int childLeft;
            if (layoutParams.mEdge == dragStart)
                childLeft = layoutParams.mEdge;
             else
                childLeft = layoutParams.mEdge - child.getMeasuredWidth();
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
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @SuppressWarnings("all")
    public static class LayoutParams extends ViewGroup.LayoutParams {

        private static final int GRAVITY_START_TOP = 0;
        private static final int GRAVITY_END_TOP = 1;
        private static final int GRAVITY_START_CENTER = 2;
        private static final int GRAVITY_END_CENTER = 3;
        private static final int GRAVITY_START_BOTTOM = 4;
        private static final int GRAVITY_END_BOTTOM = 5;

        private boolean draggable = true;
        private int mDragPaddingStart = 0;
        private int mDragPaddingTop = 0;
        private int mDragPaddingEnd = 0;
        private int mDragPaddingBottom = 0;

        private int mGravity = GRAVITY_START_TOP;
        private int mEdge = -1;
        private int mCenterY = -1;


        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int start = 0;
            int top = 0;
            int end = 0;
            int bottom = 0;
            int gravity = GRAVITY_START_TOP;
            TypedArray custom = c.obtainStyledAttributes(attrs, R.styleable.DragLayout_Layout);
            if (custom.hasValue(R.styleable.DragLayout_Layout_dlDragPadding)) {
                final int padding = custom.getDimensionPixelOffset(
                        R.styleable.DragLayout_Layout_dlDragPadding, 0);
                start = padding;
                top = padding;
                end = padding;
                bottom = padding;
            }
            start = custom.getDimensionPixelOffset(R.styleable.DragLayout_Layout_dlDragPaddingStart,
                    start);
            top = custom.getDimensionPixelOffset(R.styleable.DragLayout_Layout_dlDragPaddingTop,
                    top);
            end = custom.getDimensionPixelOffset(R.styleable.DragLayout_Layout_dlDragPaddingEnd,
                    end);
            bottom = custom.getDimensionPixelOffset(R.styleable.DragLayout_Layout_dlDragPaddingBottom,
                    bottom);
            gravity = custom.getInt(R.styleable.DragLayout_Layout_layout_gravity, gravity);
            custom.recycle();
            mDragPaddingStart = start;
            mDragPaddingTop = top;
            mDragPaddingEnd = end;
            mDragPaddingBottom = bottom;
            mGravity = gravity;
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
            mGravity = source.mGravity;
            mEdge = source.mEdge;
            mCenterY = source.mCenterY;
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

        /**
         * 设置拖动填充
         *
         * @param start  左
         * @param top    上
         * @param end    右
         * @param bottom 下
         */
        public void setDragPadding(int start, int top, int end, int bottom) {
            if (mDragPaddingStart != start || mDragPaddingTop != top ||
                    mDragPaddingEnd != end || mDragPaddingBottom != bottom) {
                mDragPaddingStart = start;
                mDragPaddingTop = top;
                mDragPaddingEnd = end;
                mDragPaddingBottom = bottom;
            }
        }

        public void setGravity(int gravity) {
            mGravity = gravity;
        }



        public boolean shouldSetLocation() {
            return mEdge == -1 || mCenterY == -1;
        }

        public void setLocation(int parentWidth, int parentHeight,
                                int paddingStart, int paddingTop,
                                int paddingEnd, int paddingBottom) {
            switch (mGravity) {
                default:
                case GRAVITY_START_TOP:
                    mEdge = paddingStart + mDragPaddingStart;
                    mCenterY = paddingTop + mDragPaddingTop;
                    break;
                case GRAVITY_END_TOP:
                    mEdge = parentWidth - paddingEnd - mDragPaddingEnd;
                    mCenterY = paddingTop + mDragPaddingTop;
                    break;
                case GRAVITY_START_CENTER:
                    mEdge = paddingStart + mDragPaddingStart;
                    mCenterY = paddingTop + mDragPaddingTop +
                            (parentHeight - paddingTop - paddingBottom
                                    - mDragPaddingTop - mDragPaddingBottom) / 2;
                    break;
                case GRAVITY_END_CENTER:
                    mEdge = parentWidth - paddingEnd - mDragPaddingEnd;
                    mCenterY = paddingTop + mDragPaddingTop +
                            (parentHeight - paddingTop - paddingBottom
                                    - mDragPaddingTop - mDragPaddingBottom) / 2;
                    break;
                case GRAVITY_START_BOTTOM:
                    mEdge = paddingStart + mDragPaddingStart;
                    mCenterY = parentHeight - paddingBottom - mDragPaddingBottom;
                    break;
                case GRAVITY_END_BOTTOM:
                    mEdge = parentWidth - paddingEnd - mDragPaddingEnd;
                    mCenterY = parentHeight - paddingBottom - mDragPaddingBottom;
                    break;
            }
        }

        public void checkLocation(int parentWidth, int parentHeight,
                                  int paddingStart, int paddingTop,
                                  int paddingEnd, int paddingBottom) {
            final int start = paddingStart + mDragPaddingStart;
            final int top = paddingTop + mDragPaddingTop;
            final int end = parentWidth - paddingEnd - mDragPaddingEnd;
            final int bottom = parentHeight - paddingBottom - mDragPaddingBottom;
            mEdge = mEdge < start ? start : mEdge;
            mEdge = mEdge > end ? end : mEdge;
            mCenterY = mCenterY < top ? top : mCenterY;
            mCenterY = mCenterY > bottom ? bottom : mCenterY;

            // TODO 处理贴边
            if (mEdge > start && mEdge < end) {
                if (mEdge - start < end - mEdge) {
                    mEdge = start;
                } else {
                    mEdge = end;
                }
            }
        }

        public void updateLocation(int edge, int centerY) {
            mEdge = edge;
            mCenterY = centerY;
        }
    }

    private class Callback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            return layoutParams.draggable;
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
            LayoutParams layoutParams = (LayoutParams) releasedChild.getLayoutParams();

            final int paddingStart = ViewCompat.getPaddingStart(DragLayout.this);
            final int paddingTop = getPaddingTop();
            final int paddingEnd = ViewCompat.getPaddingEnd(DragLayout.this);
            final int paddingBottom = getPaddingBottom();
            final int dragStart = paddingStart + layoutParams.mDragPaddingStart;
            final int dragTop = paddingTop + layoutParams.mDragPaddingTop;
            final int dragEnd = getMeasuredWidth() - paddingEnd - layoutParams.mDragPaddingEnd;
            final int dragBottom = getMeasuredHeight() - paddingBottom - layoutParams.mDragPaddingBottom;


            final float centerX = releasedChild.getLeft() + releasedChild.getMeasuredWidth() * 0.5f;
            final float centerY = releasedChild.getTop() + releasedChild.getMeasuredHeight() * 0.5f;
            int velTop;
            int mEdge;
            int mCenterY;
            int finalLeft;
            int finalTop;
            if (xvel == 0 && yvel == 0) {
                // 无飞行操作
                velTop = releasedChild.getTop();
                if (centerX < mCenterLine) {
                    // 左边
                    mEdge = dragStart;
                    finalLeft = mEdge;
                    finalTop = velTop;
                    finalTop = (float) finalTop < (dragTop - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                    finalTop = (float) finalTop > (dragBottom - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;

                } else {
                    // 右边
                    mEdge = dragEnd;
                    finalLeft = mEdge - releasedChild.getMeasuredWidth();
                    finalTop = velTop;
                    finalTop = (float) finalTop < (dragTop - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                    finalTop = (float) finalTop > (dragBottom - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                }
            } else if (xvel == 0) {
                // 垂直飞行
                if (yvel > 0) {
                    // 向下垂直飞行
                    if (centerX < mCenterLine) {
                        // 左边
                        mEdge = dragStart;
                        finalLeft = mEdge;
                        finalTop = dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f);

                    } else {
                        // 右边
                        mEdge = dragEnd;
                        finalLeft = mEdge - releasedChild.getMeasuredWidth();
                        finalTop = dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f);
                    }
                } else {
                    // 向上垂直飞行
                    if (centerX < mCenterLine) {
                        // 左边
                        mEdge = dragStart;
                        finalLeft = mEdge;
                        finalTop = dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f);

                    } else {
                        // 右边
                        mEdge = dragEnd;
                        finalLeft = mEdge - releasedChild.getMeasuredWidth();
                        finalTop = dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f);
                    }
                }
            } else if (yvel == 0) {
                // 水平飞行
                velTop = releasedChild.getTop();
                if (xvel > 0) {
                    // 向右飞行
                    mEdge = dragEnd;
                    finalLeft = mEdge - releasedChild.getMeasuredWidth();
                    finalTop = velTop;
                    finalTop = (float) finalTop < (dragTop - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                    finalTop = (float) finalTop > (dragBottom - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                } else {
                    // 向左飞行
                    mEdge = dragStart;
                    finalLeft = mEdge;
                    finalTop = velTop;
                    finalTop = (float) finalTop < (dragTop - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                    finalTop = (float) finalTop > (dragBottom - releasedChild.getMeasuredHeight() * 0.5f) ?
                            dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                }
            } else if (xvel > 0) {
                // 第一象限、第四象限
                mEdge = dragEnd;
                finalLeft = mEdge - releasedChild.getMeasuredWidth();
                if (centerX > getMeasuredWidth() - paddingEnd) {
                    // 右边线外部
                    velTop = releasedChild.getTop();
                } else {
                    // 右边线内部
                    velTop = releasedChild.getTop() - (int) (-yvel / xvel * (getMeasuredWidth() - paddingEnd - centerX));
                }
                finalTop = velTop;
                finalTop = (float) finalTop < (dragTop - releasedChild.getMeasuredHeight() * 0.5f) ?
                        dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                finalTop = (float) finalTop > (dragBottom - releasedChild.getMeasuredHeight() * 0.5f) ?
                        dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                if (centerX < mCenterLine) {
                    // 在二、三象限
                    if (yvel < 0) {
                        // 飞往第一象限
                        boolean stay2 = -yvel / xvel > (centerY - dragTop) / (mCenterLine - centerX);
                        if (centerY < dragTop || stay2) {
                            // 控制范围外飞行
                            // 不能飞入第一象限
                            // 留在第二象限顶部
                            mEdge = dragStart;
                            finalLeft = mEdge;
                            finalTop = dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f);
                        }
                    } else {
                        // 飞往第四象限
                        boolean stay3 = yvel / xvel > (dragBottom - centerY) / (mCenterLine - centerX);
                        if (centerY > dragBottom || stay3) {
                            // 控制范围外飞行
                            // 不能飞入第四象限
                            // 留在第三象限底部
                            mEdge = dragStart;
                            finalLeft = mEdge;
                            finalTop = dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f);
                        }
                    }
                }
            } else {
                // 第二象限、第三象限
                mEdge = dragStart;
                finalLeft = mEdge;
                if (centerX < paddingStart) {
                    // 左边线外部
                    velTop = releasedChild.getTop();
                } else {
                    // 左边线内部
                    velTop = releasedChild.getTop() - (int) (yvel / xvel * (centerX - paddingStart));
                }
                finalTop = velTop;
                finalTop = (float) finalTop < (dragTop - releasedChild.getMeasuredHeight() * 0.5f) ?
                        dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                finalTop = (float) finalTop > (dragBottom - releasedChild.getMeasuredHeight() * 0.5f) ?
                        dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f) : finalTop;
                if (centerX > mCenterLine) {
                    // 在一、四象限
                    if (yvel < 0) {
                        // 飞往第二象限
                        boolean stay1 = yvel / xvel > (centerY - dragTop) / (centerX - mCenterLine);
                        if (centerY < dragTop || stay1) {
                            // 控制范围外飞行
                            // 不能飞入第二象限
                            // 留在第一象限顶部
                            mEdge = dragEnd;
                            finalLeft = mEdge - releasedChild.getMeasuredWidth();
                            finalTop = dragTop - (int) (releasedChild.getMeasuredHeight() * 0.5f);
                        }
                    } else {
                        // 飞往第三象限
                        boolean stay4 = yvel / -xvel > (dragBottom - centerY) / (centerX - mCenterLine);
                        if (centerY > dragBottom || stay4) {
                            // 控制范围外飞行
                            // 不能飞入第三象限
                            // 留在第四象限底部
                            mEdge = dragEnd;
                            finalLeft = mEdge - releasedChild.getMeasuredWidth();
                            finalTop = dragBottom - (int) (releasedChild.getMeasuredHeight() * 0.5f);
                        }
                    }
                }

            }
            mCenterY = finalTop + (int) (releasedChild.getMeasuredHeight() * 0.5f);
            layoutParams.updateLocation(mEdge, mCenterY);
//            mDragHelper.flingCapturedView();
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
                requestLayout();
            }
        }
    }


}
