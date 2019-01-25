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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;

import am.widget.multifunctionalrecyclerview.R;
import am.widget.scrollbarrecyclerview.ScrollbarRecyclerView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.customview.view.AbsSavedState;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 可缩放的RecyclerView
 * Created by Alex on 2017/11/8.
 */
@SuppressWarnings("unused")
public class ScaleRecyclerView extends ScrollbarRecyclerView {

    public static final int SCROLL_STATE_SCALING = 3;
    @SuppressWarnings("unused")
    private static final String KEY_SCALE = "am.widget.scalerecyclerview.ScaleRecyclerView.KEY_SCALE";
    private final ScaleHelper mScaleHelper = new ScaleHelper(this);
    private final Rect tRect = new Rect();
    private boolean mScaleEnable = false;
    private GestureDetectorCompat mGestureDetector;
    private boolean mInterceptTouch = false;
    private boolean mShouldReactDoubleTab = true;
    private boolean mShouldReactSingleTab = true;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mScaleBegin = false;
    private OnTabListener mListener;
    private float mScale;
    private float mMinScale;
    private float mMaxScale;

    public ScaleRecyclerView(Context context) {
        super(context);
        initView(context, null);
    }

    public ScaleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ScaleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
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
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).setScale(view.getScale());
            }
        }
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.ScaleRecyclerView);
        mScaleEnable = custom.getBoolean(R.styleable.ScaleRecyclerView_srvScaleEnable,
                false);
        mScale = custom.getFloat(R.styleable.ScaleRecyclerView_srvScale, 1);
        mMinScale = custom.getFloat(R.styleable.ScaleRecyclerView_srvMinScale,
                0.000000001f);
        mMaxScale = custom.getFloat(R.styleable.ScaleRecyclerView_srvMaxScale,
                6);
        custom.recycle();
        mGestureDetector = new GestureDetectorCompat(context, new DoubleTapListener());
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
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
    public Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), mScale);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState))
            super.onRestoreInstanceState(state);
        else {
            final SavedState saved = (SavedState) state;
            super.onRestoreInstanceState(saved.getSuperState());
            mScale = saved.getScale();
            invalidateLayoutManagerScale();
            invalidate();
        }
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
        if (layout != null && !(layout instanceof ScaleLinearLayoutManager))
            throw new IllegalArgumentException("Only support ScaleLinearLayoutManager.");
        super.setLayoutManager(layout);
    }

    /**
     * 刷新布局管理器缩放比
     */
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
        if (mShouldReactSingleTab) {
            onSingleTap();
            return mListener != null && mListener.onSingleTap(this);
        }
        return false;
    }

    /**
     * 单击事件
     */
    protected void onSingleTap() {
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
        onDoubleTapEvent(e);
        if (mListener != null && mListener.onDoubleTap(this))
            return true;
        // 执行双击缩放事件
        final float targetScale = getDoubleTapScale(mScale);
        if (targetScale == mScale)
            return false;
        mScaleHelper.scale(mScale, targetScale, e.getX(), e.getY());
        return true;
    }

    /**
     * 双击事件
     *
     * @param e 触摸事件
     */
    protected void onDoubleTapEvent(MotionEvent e) {
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
        final int position = getChildAdapterPosition(target);
        float maxWidth = manager.getChildMaxWidth(manager.getChildMaxWidth());
        float maxHeight = manager.getChildMaxHeight(manager.getChildMaxHeight());
        final int offsetA = manager.computeAnotherDirectionScrollOffset();
        final float normalWidth = target.getWidth() / mScale;
        final float normalHeight = target.getHeight() / mScale;
        final float inset;
        final float focusA;
        final float focusS;
        getDecoratedBoundsWithMargins(target, tRect);
        if (manager.getOrientation() == HORIZONTAL) {
            focusA = (offsetA + (focusY - getPaddingTop())) / maxHeight;
            inset = target.getLeft() - tRect.left;
            focusS = (focusX - target.getLeft()) / target.getWidth();
        } else {
            focusA = (offsetA + (focusX - getPaddingLeft())) / maxWidth;
            focusS = (focusY - target.getTop()) / target.getHeight();
            inset = target.getTop() - tRect.top;
        }
        mScale = scale;
        invalidateLayoutManagerScale();
        final float maxOffset = manager.computeAnotherDirectionMaxScrollOffset();
        if (maxOffset <= 0) {
            manager.setAnotherDirectionScrollOffsetPercentage(0);
        } else {
            maxWidth = manager.getChildMaxWidth(manager.getChildMaxWidth());
            maxHeight = manager.getChildMaxHeight(manager.getChildMaxHeight());
            final float scaleOffset;
            if (manager.getOrientation() == HORIZONTAL) {
                scaleOffset = focusA * maxHeight - (focusY - getPaddingTop());
            } else {
                scaleOffset = focusA * maxWidth - (focusX - getPaddingLeft());
            }
            manager.setAnotherDirectionScrollOffsetPercentage(scaleOffset / maxOffset);
        }
        final float offsetS;
        if (manager.getOrientation() == HORIZONTAL) {
            offsetS = -(focusS * normalWidth * mScale - (focusX - getPaddingLeft())) - inset;
        } else {
            offsetS = -(focusS * normalHeight * mScale - (focusY - getPaddingTop())) - inset;
        }
        manager.scrollToPositionWithOffset(position, Math.round(offsetS));
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
     * 处理最后的缩放结果
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

    /**
     * 双击缩放开始
     *
     * @param scale  缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    protected void onDoubleTapScaleBegin(float scale, float focusX, float focusY) {
        dispatchOnScrollStateChanged(SCROLL_STATE_SCALING);
    }

    /**
     * 双击缩放
     *
     * @param scale  缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
    protected void onDoubleTapScale(float scale, float focusX, float focusY) {
        scaleTo(scale, focusX, focusY);
    }

    /**
     * 双击缩放结束
     *
     * @param scale  缩放比
     * @param focusX 焦点X
     * @param focusY 焦点Y
     */
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
     * 单双击监听
     */
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
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            if (recyclerView instanceof ScaleRecyclerView) {
                mView = (ScaleRecyclerView) recyclerView;
            }
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            mView = null;
            super.onDetachedFromRecyclerView(recyclerView);
        }

        /**
         * 获取缩放比
         *
         * @return 缩放比
         */
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

        /**
         * 设置缩放比
         *
         * @param scale 缩放比
         */
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

    protected static class SavedState extends AbsSavedState {

        public static final Creator<SavedState> CREATOR =
                new ClassLoaderCreator<SavedState>() {

                    @Override
                    public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                        return new SavedState(source, loader);
                    }

                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in, null);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

        private final float mScale;

        private SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            mScale = in.readFloat();
        }

        private SavedState(Parcelable superState, float scale) {
            super(superState);
            mScale = scale;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(mScale);
        }

        float getScale() {
            return mScale;
        }
    }
}
