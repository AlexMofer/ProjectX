/*
 * Copyright (C) 2017 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.widget.scalerecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;

import am.widget.scrollbarrecyclerview.ScrollbarRecyclerView;

/**
 * 可缩放的RecyclerView
 * Created by Alex on 2017/11/8.
 */
@SuppressWarnings("all")
public class ScaleRecyclerView extends ScrollbarRecyclerView {

    public static final int TYPE_ADJUST_AUTO = 0;
    public static final int TYPE_ADJUST_HORIZONTAL = 1;
    public static final int TYPE_ADJUST_VERTICAL = 2;
    public static final int TYPE_ADJUST_ALL = 3;
    public static final int SCROLL_STATE_SCALING = 3;
    private static final String KEY_SCALE = "am.widget.scalerecyclerview.ScaleRecyclerView.KEY_SCALE";
    private final ScaleHelper mScaleHelper = new ScaleHelper(this);
    private boolean mScaleEnable = false;
    private GestureDetectorCompat mGestureDetector;
    private boolean mInterceptTouch = false;
    private boolean mShouldReactDoubleTab = true;
    private boolean mShouldReactSingleTab = true;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mScaleBegin = false;
    private OnTabListener mListener;
    private float mScale = 1;
    private float mMaxScale = 6;
    private float mMinScale = 0.000000001f;


    private int mAdjustType = TYPE_ADJUST_AUTO;

    public ScaleRecyclerView(Context context) {
        super(context);
        initView();
    }

    public ScaleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScaleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 设置子项缩放
     *
     * @param child 属于ScaleRecyclerView的子项
     */
    public static void setScale(View child) {
        final ViewParent parent = child.getParent();
        if (parent instanceof ScaleRecyclerView) {
            final ScaleRecyclerView view = (ScaleRecyclerView) parent;
            RecyclerView.ViewHolder holder = view.findContainingViewHolder(child);
            if (holder instanceof ScaleRecyclerView.ViewHolder) {
                ((ScaleRecyclerView.ViewHolder) holder).setScale(view.getScale());
            }
        }
    }

    private void initView() {
        mGestureDetector = new GestureDetectorCompat(getContext(), new DoubleTapListener());
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mScaleEnable)
            return super.dispatchTouchEvent(ev);
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mScaleHelper.stop();
            mShouldReactDoubleTab = true;
            mShouldReactSingleTab = true;
        }
        final boolean superResult = super.dispatchTouchEvent(ev);
        if (!mInterceptTouch) {
            // 点击与双击事件不会被拦截
            // 放置此处是保证点击与双击事件发生在子项触摸事件之后，以便子项控制其是否响应。
            mGestureDetector.onTouchEvent(ev);
        }
        return superResult;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!mScaleEnable)
            return super.onInterceptTouchEvent(e);
        mInterceptTouch = super.onInterceptTouchEvent(e);
        return mInterceptTouch;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            mShouldReactDoubleTab = false;
            mShouldReactSingleTab = false;
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    @SuppressWarnings("all")
    public boolean onTouchEvent(MotionEvent e) {
        if (!mScaleEnable)
            return super.onTouchEvent(e);
        boolean superResult = false;
        // 检查缩放事件
        if (e.getPointerCount() >= 2) {
            // 多指
            if (!mScaleBegin) {
                // 开始缩放，对列表模拟一个ACTION_UP事件
                final int action = e.getAction();
                e.setAction(MotionEvent.ACTION_UP);
                setForceInterceptDispatchOnScrollStateChanged(true);
                superResult = super.onTouchEvent(e);
                setForceInterceptDispatchOnScrollStateChanged(false);
                e.setAction(action);
                mScaleBegin = true;
            }
            superResult = mScaleGestureDetector.onTouchEvent(e) || superResult;
        } else {
            // 单指
            final int action = e.getAction();
            boolean dispatch = false;
            if (action == MotionEvent.ACTION_UP && getScrollState() == SCROLL_STATE_IDLE) {
                dispatch = true;
            }
            if (mScaleBegin) {
                mScaleBegin = false;
                // 结束缩放，对列表模拟一个ACTION_DOWN事件
                e.setAction(MotionEvent.ACTION_DOWN);
                super.onInterceptTouchEvent(e);
                e.setAction(action);
            }
            superResult = super.onTouchEvent(e);
            if (dispatch) {
                dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
            }
        }
        return superResult;
    }

    private void setForceInterceptDispatchOnScrollStateChanged(boolean force) {
        final ScaleLinearLayoutManager manager = getLayoutManager();
        if (manager != null) {
            manager.setForceInterceptDispatchOnScrollStateChanged(force);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putFloat(KEY_SCALE, mScale);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mScale = bundle.getFloat(KEY_SCALE, 1f);
        invalidateLayoutManagerScale();
        invalidate();
    }

    @Override
    public ScaleLinearLayoutManager getLayoutManager() {
        final LayoutManager layoutManager = super.getLayoutManager();
        if (layoutManager == null)
            return null;
        return (ScaleLinearLayoutManager) layoutManager;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof ScaleLinearLayoutManager) && layout != null) {
            throw new RuntimeException("Unsupport other LayoutManager");
        }
        super.setLayoutManager(layout);
    }

    protected void invalidateLayoutManagerScale() {
        final ScaleLinearLayoutManager manager = getLayoutManager();
        if (manager != null) {
            manager.setChildScale(mScale);
        }
    }

    /**
     * 分发单击事件
     */
    protected boolean dispatchSingleTap() {
        return mShouldReactSingleTab && mListener != null && mListener.onSingleTap(this);
    }

    /**
     * 分发双击事件
     *
     * @param e 触摸事件
     */
    protected boolean dispatchDoubleTapEvent(MotionEvent e) {
        // 仅在双击事件第二次点击的ACTION_UP出现时触发双击缩放。
        if (e.getAction() != MotionEvent.ACTION_UP)
            return false;
        // 在此过程中，一些触摸事件可能会导致事件取消，其次该事件可以被子View取消。
        if (!mShouldReactDoubleTab)
            return false;
        // 监听拦截双击事件
        if (mListener != null && mListener.onDoubleTap(this))
            return true;
        // 执行双击缩放事件
        final float targetScale = getDoubleTapScale(mScale);
        if (targetScale == mScale)
            return false;
        mScaleHelper.scale(mScale, targetScale, e.getX(), e.getY());
        return true;
    }

    private float getDoubleTapScale(float scale) {
        final float targetScale = scale * 2;
        if (targetScale < mMaxScale) {
            return targetScale;
        }
        return mMaxScale;
    }

    /**
     * 分发开始缩放
     *
     * @param detector 缩放手势检测器
     * @return 是否继续处理缩放
     */
    protected boolean dispatchScaleBegin(ScaleGestureDetector detector) {
        onScaleBegin(mScale, detector.getFocusX(), detector.getFocusY());
        return true;
    }

    /**
     * 开始缩放
     *
     * @param scale  起始缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    protected void onScaleBegin(float scale, float focusX, float focusY) {
        dispatchOnScrollStateChanged(SCROLL_STATE_SCALING);
    }

    /**
     * 分发缩放
     *
     * @param detector 缩放手势检测器
     * @return 是否处理了事件
     */
    protected boolean dispatchScale(ScaleGestureDetector detector) {
        onScale(mScale * detector.getScaleFactor(),
                detector.getFocusX(), detector.getFocusY());
        return true;
    }

    /**
     * 处理缩放
     *
     * @param scale  目标缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    protected void onScale(float scale, float focusX, float focusY) {
        scaleTo(scale, focusX, focusY);
    }

    /**
     * 缩放
     *
     * @param scale  目标缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    public void scaleTo(float scale, float focusX, float focusY) {
        scale = scale > mMaxScale ? mMaxScale : scale;
        scale = scale < mMinScale ? mMinScale : scale;
        if (scale == mScale)
            return;
        View child = getChildAt(0);
        // 计算focus对应View的(x1, y1)
        final float beforeViewX = focusX - child.getLeft();
        final float beforeViewY = focusY - child.getTop();
        // 计算View的(x1, y1)对应缩放后的(x2, y2)
        final float afterViewX = beforeViewX / mScale * scale;
        final float afterViewY = beforeViewY / mScale * scale;
        mScale = scale;
        invalidateLayoutManagerScale();
        requestLayout();
        // 应用偏移
        final int dx = Math.round(afterViewX - beforeViewX);
        final int dy = Math.round(afterViewY - beforeViewY);
        switch (mAdjustType) {
            default:
            case TYPE_ADJUST_AUTO:
                final LayoutManager layoutManager = getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    if (((LinearLayoutManager) layoutManager).getOrientation() ==
                            LinearLayoutManager.HORIZONTAL) {
                        scrollBy(dx, 0);
                    } else {
                        scrollBy(0, dy);
                    }
                } else {
                    scrollBy(dx, dy);
                }
                break;
            case TYPE_ADJUST_HORIZONTAL:
                scrollBy(dx, 0);
                break;
            case TYPE_ADJUST_VERTICAL:
                scrollBy(0, dy);
                break;
            case TYPE_ADJUST_ALL:
                scrollBy(dx, dy);
                break;
        }
    }

    /**
     * 缩放
     * TODO 改进中
     *
     * @param scale  目标缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    private void scaleToNew(float scale, float focusX, float focusY) {
        scale = scale > mMaxScale ? mMaxScale : scale;
        scale = scale < mMinScale ? mMinScale : scale;
        if (scale == mScale)
            return;
        final ScaleLinearLayoutManager manager = getLayoutManager();
        if (manager == null) {
            mScale = scale;
            invalidateLayoutManagerScale();
            requestLayout();
            return;
        }
        final View target = findChildViewNear(focusX, focusY);
        if (target == null) {
            mScale = scale;
            invalidateLayoutManagerScale();
            requestLayout();
            return;
        }
        mScale = scale;
        invalidateLayoutManagerScale();
        adjustScroll(manager, target, scale, focusX, focusY);
    }

    private void adjustScroll(ScaleLinearLayoutManager manager, View target,
                              float scale, float focusX, float focusY) {

//        LayoutParams layoutParams = (LayoutParams) target.getLayoutParams(); TODO Marrgin
        final int width = target.getWidth() == 0 ? 1 : target.getWidth();
        final int height = target.getHeight() == 0 ? 1 : target.getHeight();
        final int leftDecorationWidth = manager.getLeftDecorationWidth(target); // TODO 增加可缩放的ItemDecorations ？
        final int topDecorationWidth = manager.getTopDecorationHeight(target); // TODO 增加可缩放的ItemDecorations ？
        final float dx = focusX - target.getLeft();
        final float dy = focusY - target.getTop();
        final float px = dx / width;
        final float py = dy / height;
        final float widthScaled = width * scale;
        final float heightScaled = height * scale;
        final float dXScaled = widthScaled * px;
        final float dYScaled = heightScaled * py;
        final float leftScaled = focusX - dXScaled;
        final float topScaled = focusY - dYScaled;
        final int position = manager.getPosition(target);
        final float offsetX = (leftScaled - leftDecorationWidth - getPaddingLeft());
        final float offsetY = (topScaled - topDecorationWidth - getPaddingTop());
//        final float percentage = 0.5f; // TODO 位置校调可优化
//        manager.setAnotherDirectionScrollOffsetPercentage(percentage, false);
        System.out.println("position-----------------------------------------------------:" + position);
        System.out.println("offsetX------------------------------------------------------:" + offsetX);
        System.out.println("offsetY------------------------------------------------------:" + offsetY);
        if (manager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
            manager.scrollToPositionWithOffset(position, Math.round(offsetX));
        } else {
            manager.scrollToPositionWithOffset(position, Math.round(offsetY));
        }
    }

    /**
     * 分发结束缩放
     *
     * @param detector 缩放手势检测器
     */
    protected void dispatchScaleEnd(ScaleGestureDetector detector) {
        onScaleEnd(mScale, detector.getFocusX(), detector.getFocusY());
    }

    /**
     * 分发最后的缩放结果
     *
     * @param scale  缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    protected void onScaleEnd(float scale, float focusX, float focusY) {
        if (mScaleBegin)
            return;
        dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
    }

    protected void onDoubleTapScaleBegin(float scale, float focusX, float focusY) {
        dispatchOnScrollStateChanged(SCROLL_STATE_SCALING);
    }

    protected void onDoubleTapScale(float scale, float focusX, float focusY) {
        scaleTo(scale, focusX, focusY);
    }

    protected void onDoubleTapScaleEnd(float scale, float focusX, float focusY) {
        if (mScaleBegin)
            return;
        dispatchOnScrollStateChanged(SCROLL_STATE_IDLE);
    }

    /**
     * 判断缩放是否开启
     *
     * @return 是否开启
     */
    public boolean isScaleEnable() {
        return mScaleEnable;
    }

    /**
     * 设置缩放是否开启
     *
     * @param enable 是否开启
     */
    public void setScaleEnable(boolean enable) {
        mScaleEnable = enable;
    }

    /**
     * 设置单双击监听
     *
     * @param listener 点击监听
     */
    public void setOnTabListener(OnTabListener listener) {
        mListener = listener;
    }

    /**
     * 设置缩放区间
     *
     * @param min 最小缩放
     * @param max 最大缩放
     */
    public void setScaleRange(float min, float max) {
        mMinScale = min;
        mMaxScale = max;
    }

    /**
     * 获取缩放比
     *
     * @return 缩放比
     */
    public float getScale() {
        return mScale;
    }

    /**
     * 设置缩放比
     *
     * @param scale 缩放比
     */
    public void setScale(float scale) {
        if (scale < mMinScale || scale > mMaxScale || scale == mScale)
            return;
        mScale = scale;
        invalidateLayoutManagerScale();
        requestLayout();
    }

    /**
     * 设置缩放后的位置校调方式
     *
     * @param type 校调方式
     */
    public void setAdjustType(int type) {
        mAdjustType = type;
    }

    public interface OnTabListener {
        /**
         * 单击事件
         *
         * @param view ScaleRecyclerView
         * @return 是否消耗事件
         */
        boolean onSingleTap(ScaleRecyclerView view);

        /**
         * 双击事件
         *
         * @param view ScaleRecyclerView
         * @return 是否消耗事件
         */
        boolean onDoubleTap(ScaleRecyclerView view);
    }

    /**
     * 内容提供者
     *
     * @param <VH> 视图持有者
     */
    public static abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {

        private ScaleRecyclerView mView;

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            if (recyclerView instanceof ScaleRecyclerView) {
                mView = (ScaleRecyclerView) recyclerView;
            }
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mView = null;
            super.onDetachedFromRecyclerView(recyclerView);
        }

        public boolean isAttachedToRecyclerView() {
            return mView != null;
        }

        public float getScale() {
            return mView == null ? 1 : mView.getScale();
        }
    }

    /**
     * 视图持有者
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setScale(float scale) {

        }
    }

    private class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return dispatchSingleTap();
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return dispatchDoubleTapEvent(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {


        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return dispatchScaleBegin(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return dispatchScale(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            dispatchScaleEnd(detector);
        }
    }
}
