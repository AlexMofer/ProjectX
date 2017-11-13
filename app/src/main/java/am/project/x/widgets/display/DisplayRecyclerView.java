package am.project.x.widgets.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

import am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;
import am.widget.scalerecyclerview.ScaleRecyclerView;

/**
 * 显示RecyclerView
 * Created by Xiang Zhicheng on 2017/11/3.
 */

public class DisplayRecyclerView extends MultifunctionalRecyclerView implements
        MultifunctionalRecyclerView.OnTabListener {

    private int mPaddingTop;
    private int mPaddingLeft;
    private int mPaddingBottom;
    private int mPaddingRight;
    private DisplayItemDecoration mItemDecoration;
    private DisplayLayoutManager mLayoutManager;
    private OnDisplayListener mListener;
    private int mOffset = 0;
    private int mCurrentPosition = NO_POSITION;

    public DisplayRecyclerView(Context context) {
        super(context);
        initView(context);
    }

    public DisplayRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DisplayRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        setScaleEnable(true);
        mItemDecoration = new DisplayItemDecoration(context);
        mLayoutManager = new DisplayLayoutManager(context);
        mItemDecoration.updateDecorationMaxWidthOfChildWithMaxSize(mLayoutManager);
        addItemDecoration(mItemDecoration);
        setLayoutManager(mLayoutManager);
        setOnTabListener(this);
        setScaleRange(1, 6);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPaddingTop = getPaddingTop();
        mPaddingLeft = getPaddingLeft();
        mPaddingBottom = getPaddingBottom();
        mPaddingRight = getPaddingRight();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        mLayoutManager.setChildMaxSize(getDisplayWidth(), getDisplayHeight());
    }

    @Override
    @SuppressWarnings("all")
    protected boolean fitSystemWindows(Rect insets) {
        if (Build.VERSION.SDK_INT >= 20) {
            return super.fitSystemWindows(insets);
        }
        setPadding(mPaddingLeft + insets.left, mPaddingTop,
                mPaddingRight + insets.right, mPaddingBottom);
        mItemDecoration.fitSystemWindows(insets.left, insets.top, insets.right, insets.bottom);
        return super.fitSystemWindows(insets);
    }

    @Override
    @RequiresApi(20)
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        final int left = insets.getSystemWindowInsetLeft();
        final int top = insets.getSystemWindowInsetTop();
        final int right = insets.getSystemWindowInsetRight();
        final int bottom = insets.getSystemWindowInsetBottom();
        setPadding(mPaddingLeft + left, mPaddingTop,
                mPaddingRight + right, mPaddingBottom);
        mItemDecoration.fitSystemWindows(left, top, right, bottom);
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final View target = findChildViewNear(
                (getWidth() - getPaddingLeft() - getPaddingRight()) * 0.5f,
                (getHeight() - getPaddingTop() - getPaddingBottom()) * 0.5f);
        if (target == null)
            return;
        final int position = getChildAdapterPosition(target);
        if (mCurrentPosition != position) {
            mCurrentPosition = position;
            if (mListener != null)
                mListener.onCurrentItemChanged(this, position);
        }
    }

    @Nullable
    @Override
    public View findChildViewNear(float x, float y) {
        return mLayoutManager.findChildViewNear(x, y);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        if (isHorizontal())
            return;
        final int offset = computeVerticalScrollOffset();
        if (offset <= 0) {
            if (mListener != null)
                mListener.onScrollToTopEdge(this);
        }
        mOffset = offset;
    }

    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent e) {
        if (isHorizontal())
            return super.onTouchEvent(e);
        final float lastOffset = mOffset;
        final boolean result = super.onTouchEvent(e);
        if (lastOffset < mOffset) {
            if (mListener != null)
                mListener.onScrollDown(this);
        }
        return result;
    }

    protected int getDisplayWidth() {
        return mItemDecoration.getDisplayWidth(this);
    }

    protected int getDisplayHeight() {
        return mItemDecoration.getDisplayHeight(this);
    }

    protected boolean isHorizontal() {
        return mLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL;
    }

    // Listener
    @Override
    public boolean onSingleTap(ScaleRecyclerView view) {
        if (mListener != null) {
            mListener.onClick(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(ScaleRecyclerView view) {
        return false;
    }

    /**
     * 设置监听器
     *
     * @param listener 监听器
     */
    public void setOnDisplayListener(OnDisplayListener listener) {
        mListener = listener;
    }

    /**
     * 设置显示方式
     *
     * @param orientation 滚动方向
     * @param paging      是否分页
     * @param scale       缩放比例
     */
    public void setDisplaySetting(int orientation, boolean paging, float scale) {
        mLayoutManager.setOrientation(orientation);
        mLayoutManager.setPagingEnable(paging);
        setScale(scale);
    }

    /**
     * 获取滚动方向
     *
     * @return 滚动方向
     */
    public int getOrientation() {
        return mLayoutManager.getOrientation();
    }

    /**
     * 是否开启分页
     *
     * @return 分页
     */
    public boolean isPagingEnable() {
        return mLayoutManager.isPagingEnable();
    }

    /**
     * 设置阅读位置
     *
     * @param position   第几页
     * @param offset     页偏移
     * @param percentage 另一方向百分比
     */
    public void setDisplayLocation(int position, int offset, float percentage) {
        mLayoutManager.setDisplayLocation(position, offset, percentage);
    }

    /**
     * 设置阅读位置
     *
     * @param position       第几页
     * @param x              高宽无关的左上角在页面的x坐标
     * @param y              高宽无关的左上角在页面的y坐标
     * @param last           是否为最后一个子项
     * @param maxChildWidth  最大子项宽
     * @param maxChildHeight 最大子项高
     * @param width          页面宽
     * @param height         页面高
     */
    public void setDisplayLocation(int position, float x, float y, boolean last,
                                   float maxChildWidth, float maxChildHeight,
                                   float width, float height) {
        mLayoutManager.setDisplayLocation(position, x, y, last, maxChildWidth, maxChildHeight,
                width, height);
    }

    void getHorizontalDecoration(Rect decoration, int type) {
        mItemDecoration.getHorizontalDecoration(decoration, type);
    }

    void getVerticalDecoration(Rect decoration, int type) {
        mItemDecoration.getVerticalDecoration(decoration, type);
    }

    public interface OnDisplayListener {
        void onClick(DisplayRecyclerView display);

        void onScrollToTopEdge(DisplayRecyclerView display);

        void onScrollDown(DisplayRecyclerView display);

        void onCurrentItemChanged(DisplayRecyclerView display, int position);
    }

    /**
     * 内容提供者
     *
     * @param <VH> 视图持有者
     */
    public static abstract class Adapter<VH extends ViewHolder> extends
            MultifunctionalRecyclerView.Adapter<VH> {

        private DisplayRecyclerView mView;

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (recyclerView instanceof DisplayRecyclerView) {
                mView = (DisplayRecyclerView) recyclerView;
            }
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            mView = null;
        }

        protected int getDisplayWidth() {
            return mView == null ? 0 : mView.getDisplayWidth();
        }

        protected int getDisplayHeight() {
            return mView == null ? 0 : mView.getDisplayHeight();
        }

        protected float getChildMaxWidth() {
            return 0;
        }

        protected float getChildMaxHeight() {
            return 0;
        }

        protected boolean isHorizontal() {
            return mView != null && mView.isHorizontal();
        }
    }

    /**
     * 视图持有者
     */
    public static class ViewHolder extends MultifunctionalRecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
