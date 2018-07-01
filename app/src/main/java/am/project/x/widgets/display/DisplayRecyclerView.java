package am.project.x.widgets.display;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import am.widget.multifunctionalrecyclerview.MultifunctionalRecyclerView;

/**
 * 显示RecyclerView
 * Created by Xiang Zhicheng on 2017/11/3.
 */

public class DisplayRecyclerView extends MultifunctionalRecyclerView {

    private DisplayItemDecoration mItemDecoration;
    private DisplayLayoutManager mLayoutManager;

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
        setScaleRange(1, 6);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        mLayoutManager.setChildMaxSize(getDisplayWidth(), getDisplayHeight());
    }

//    @Override
//    public void onScrolled(int dx, int dy) {
//        super.onScrolled(dx, dy);
//        if (isHorizontal())
//            return;
//        final int offset = computeVerticalScrollOffset();
//        if (offset <= 0) {
//            if (mListener != null)
//                mListener.onScrollToTopEdge(this);
//        }
//        mOffset = offset;
//    }

//    @Override
//    @SuppressWarnings("all")
//    public boolean onTouchEvent(MotionEvent e) {
//        if (isHorizontal())
//            return super.onTouchEvent(e);
//        final float lastOffset = mOffset;
//        final boolean result = super.onTouchEvent(e);
//        if (lastOffset < mOffset) {
//            if (mListener != null)
//                mListener.onScrollDown(this);
//        }
//        setOnScrollChangeListener();
//        return result;
//    }

    protected int getDisplayWidth() {
        return mItemDecoration.getDisplayWidth(this);
    }

    protected int getDisplayHeight() {
        return mItemDecoration.getDisplayHeight(this);
    }

    protected boolean isHorizontal() {
        return mLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL;
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

    public void setPagingEnable(boolean enable) {
        mLayoutManager.setPagingEnable(enable);
    }

    void getHorizontalDecoration(Rect decoration, int type) {
        mItemDecoration.getHorizontalDecoration(decoration, type);
    }

    void getVerticalDecoration(Rect decoration, int type) {
        mItemDecoration.getVerticalDecoration(decoration, type);
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
