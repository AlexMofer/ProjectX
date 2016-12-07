package am.widget.draglayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 拖动视图
 * Created by Alex on 2016/12/7.
 */

public class DragLayoutOld extends ViewGroup {

    private final DragPadding mDragPadding = new DragPadding();
    private boolean draggable = true;
    private GestureDetectorCompat mDragGestureDetector;
    private final SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener();
    private boolean dragging = false;
    private final Rect mCheckRect = new Rect();
    private View mDraggingChild;

    public DragLayoutOld(Context context) {
        super(context);
        initView(null);
    }

    public DragLayoutOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    @TargetApi(11)
    public DragLayoutOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public DragLayoutOld(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mDragGestureDetector = new GestureDetectorCompat(getContext(), simpleOnGestureListener);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link ViewGroup.LayoutParams#WRAP_CONTENT} and no spanning.
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int childLeft = layoutParams.mX;
            final int childTop = layoutParams.mCenterY - layoutParams.height / 2;
            child.layout(childLeft, childTop,
                    childLeft + layoutParams.width, childTop + layoutParams.height);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!draggable)
            return super.dispatchTouchEvent(ev); // 不可拖动
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dragging = checkDragging((int) ev.getX(), (int) ev.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!mDragGestureDetector.onTouchEvent(ev) && dragging) {
                    // 没有在Fling，将子项移动到边缘
                    // TODO
                }
                dragging = false;
            default:
                break;
        }
        if (dragging) {
            return mDragGestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    private boolean checkDragging(int x, int y) {
        boolean dragging = false;
        mDraggingChild = null;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            child.getHitRect(mCheckRect);
            if (mCheckRect.contains(x, y)) {
                mDraggingChild = child;
                dragging = true;
                break;
            }
        }
        return dragging;
    }

    private void drag(MotionEvent e1, MotionEvent e2,
                      float distanceX, float distanceY) {

        // 拖动
        // TODO
    }

    private void fling(final MotionEvent e1,
                       final MotionEvent e2, final float velocityX,
                       final float velocityY) {
        // 飞行
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

        public void setLocation(DragPadding dragPadding, int parentWidth, int parentHeight,
                                int paddingStart, int paddingTop, int paddingEnd,
                                int paddingBottom) {
            if (dragPadding == null)
                return;
            switch (mGravity) {
                default:
                case GRAVITY_START_TOP:
                    mX = paddingStart;
                    mCenterY = paddingTop + dragPadding.mStartTop;
                    break;
                case GRAVITY_END_TOP:
                    mX = parentWidth - paddingEnd - width;
                    mCenterY = paddingTop + dragPadding.mStartTop;
                    break;
                case GRAVITY_START_CENTER:
                    mX = paddingStart;
                    mCenterY = paddingTop + dragPadding.mStartTop +
                            (parentHeight - paddingTop - paddingBottom
                                    - dragPadding.mStartTop - dragPadding.mStartBottom) / 2;
                    break;
                case GRAVITY_END_CENTER:
                    mX = parentWidth - paddingEnd - width;
                    mCenterY = paddingTop + dragPadding.mStartTop +
                            (parentHeight - paddingTop - paddingBottom
                                    - dragPadding.mStartTop - dragPadding.mStartBottom) / 2;
                    break;
                case GRAVITY_START_BOTTOM:
                    mX = paddingStart;
                    mCenterY = parentHeight - paddingBottom - dragPadding.mStartBottom;
                    break;
                case GRAVITY_END_BOTTOM:
                    mX = parentWidth - paddingEnd - width;
                    mCenterY = parentHeight - paddingBottom - dragPadding.mStartBottom;
                    break;
            }
            // TODO
        }
    }

    private class DragPadding {
        int mStartTop;
        int mEndTop;
        int mStartBottom;
        int mEndBottom;

        public DragPadding() {
            this(0, 0, 0, 0);
        }

        public DragPadding(int startTop, int startBottom, int endTop, int endBottom) {
            set(startTop, startBottom, endTop, endBottom);
        }

        public void set(int startTop, int startBottom, int endTop, int endBottom) {
            mStartTop = startTop;
            mEndTop = endTop;
            mStartBottom = startBottom;
            mEndBottom = endBottom;
        }
    }

    private class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            drag(e1, e2, distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onFling(final MotionEvent e1,
                               final MotionEvent e2, final float velocityX,
                               final float velocityY) {
            fling(e1, e2, velocityX, velocityY);
            return true;
        }
    }
}
