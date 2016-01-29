/*
 * Copyright (C) 2014 The Android Open Source Project
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

package android.support.v4.view;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * BasePagerTabStrip ViewPager滑动对应变化效果<br>
 * 若要作为 ViewPager子项使用 <b>implements ViewPager.Decor</b>
 *
 * @author Alex
 */
public abstract class BaseTabStrip extends View implements ViewPager.Decor {

    private ViewPager mPager;
    private List<String> mTabs = new ArrayList<>();
    private final PageListener mPageListener = new PageListener();
    private WeakReference<PagerAdapter> mWatchingAdapter;
    private int mLastKnownPosition = 0;
    private float mLastKnownPositionOffset = -1;
    private int mCurrentPager = 0;
    private int mNextPager = 0;
    private int mTouchSlop;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private float mDownMotionX = -1;
    private float mDownMotionY = -1;
    private OnTabClickListener mListener;
    private Drawable mTabDrawable;
    private ArrayList<Drawable> mTabDrawables = new ArrayList<>();
    private boolean willNeedTitle = true;
    private boolean willClick = true;
    private OnTabChangeListener mTCListener;

    public BaseTabStrip(Context context) {
        this(context, null);
    }

    public BaseTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BaseTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (mPager == null && parent instanceof ViewPager) {
            setViewPager((ViewPager) parent);
        }
    }

    /**
     * 设置ViewPager
     *
     * @param pager 关联的ViewPager
     */
    public void setViewPager(ViewPager pager) {
        mPager = pager;
        if (mPager != null) {
            final PagerAdapter adapter = mPager.getAdapter();
            mPager.setInternalPageChangeListener(mPageListener);
            mPager.setOnAdapterChangeListener(mPageListener);
            updateAdapter(mWatchingAdapter != null ? mWatchingAdapter.get()
                    : null, adapter);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPager != null) {
            updateAdapter(mPager.getAdapter(), null);
            mPager.setInternalPageChangeListener(null);
            mPager.setOnAdapterChangeListener(null);
            mPager = null;
        }
        if (mTabDrawables != null) {
            for (Drawable drawable : mTabDrawables) {
                drawable.setCallback(null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!willClick) {
            return super.onTouchEvent(ev);
        }
        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionX = x;
                mInitialMotionY = y;
                mDownMotionX = x;
                mDownMotionY = y;
                break;

            case MotionEvent.ACTION_UP:
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                if (dx * dx + dy * dy < mTouchSlop * mTouchSlop) {
                    mInitialMotionX = x;
                    mInitialMotionY = y;
                    final int position = pointToPosition(mInitialMotionX,
                            mInitialMotionY);
                    performClick(position);
                    mInitialMotionX = -1;
                    mInitialMotionY = -1;

                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 点击
     *
     * @param position 位置
     */
    public void performClick(int position) {
        performClick(position, false, true);
    }

    /**
     * 点击
     *
     * @param position     位置
     * @param smoothScroll 是否平滑滚动
     */
    public void performClick(int position, boolean smoothScroll, boolean notifyListener) {
        if (getViewPager() != null && getViewPager().getAdapter() != null
                && getViewPager().getAdapter().getCount() > 0) {
            if (mListener != null && notifyListener) {
                mListener.onTabClick(position);
            }
            int current = position % getViewPager().getAdapter().getCount();
            getViewPager().setCurrentItem(current, smoothScroll);
            if (!smoothScroll) {
                mCurrentPager = current;
                jumpTo(mCurrentPager);
                if (mTCListener != null)
                    mTCListener.jumpTo(mCurrentPager);

            }

        }
    }

    protected void updateAdapter(PagerAdapter oldAdapter,
                                 PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener);
            mWatchingAdapter = null;
        }
        mTabs.clear();
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(mPageListener);
            mWatchingAdapter = new WeakReference<>(newAdapter);
            mLastKnownPosition = mPager.getCurrentItem();
            mCurrentPager = mLastKnownPosition;
            mNextPager = mLastKnownPosition;
            if (willNeedTitle) {
                for (int i = 0; i < newAdapter.getCount(); i++) {
                    mTabs.add(newAdapter.getPageTitle(i).toString());
                }
            }
        }
        getDrawables(newAdapter);
        if (mPager != null) {
            jumpTo(mCurrentPager);
            if (mTCListener != null)
                mTCListener.jumpTo(mCurrentPager);
        }
        requestLayout();
        invalidate();
    }

    private boolean getDrawables(PagerAdapter adapter) {
        mTabDrawables.clear();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (mTabDrawable == null) {
                    break;
                }
                Drawable tag = mTabDrawable.getConstantState().newDrawable();
                if (tag != null) {
                    tag.setCallback(this);
                    mTabDrawables.add(tag);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 更新View
     *
     * @param position       位置
     * @param positionOffset 位置偏移
     * @param force          是否强制更新
     */
    private void updateView(int position, float positionOffset, boolean force) {
        if (mLastKnownPositionOffset == -1) {
            mLastKnownPositionOffset = positionOffset;
        }
        if (!force && positionOffset == mLastKnownPositionOffset) {
            return;
        }
        float mPositionOffset = positionOffset;
        if (mLastKnownPositionOffset == 0 || mLastKnownPositionOffset == 1)
            if (mPositionOffset > 0.5f)
                mLastKnownPositionOffset = 1;
            else
                mLastKnownPositionOffset = 0;
        if (position > mLastKnownPosition) {
            mLastKnownPosition = position - 1;
            if (mLastKnownPositionOffset > mPositionOffset) {

                if (mPositionOffset == 0) {
                    mPositionOffset = 1;
                } else {
                    mLastKnownPosition = position;
                }
                mCurrentPager = mLastKnownPosition;
                mNextPager = mLastKnownPosition + 1;
                gotoRight(mCurrentPager, mNextPager, mPositionOffset);
                if (mTCListener != null)
                    mTCListener.gotoRight(mCurrentPager, mNextPager, mPositionOffset);
            } else {
                mCurrentPager = mLastKnownPosition + 1;
                mNextPager = mLastKnownPosition;
                gotoLeft(mCurrentPager, mNextPager, mPositionOffset);
                if (mTCListener != null)
                    mTCListener.gotoLeft(mCurrentPager, mNextPager, mPositionOffset);
            }
        } else {
            mLastKnownPosition = position;
            if (mLastKnownPositionOffset > mPositionOffset) {
                mCurrentPager = mLastKnownPosition + 1;
                mNextPager = mLastKnownPosition;
                gotoLeft(mCurrentPager, mNextPager, mPositionOffset);
                if (mTCListener != null)
                    mTCListener.gotoLeft(mCurrentPager, mNextPager, mPositionOffset);
            } else {
                mPositionOffset = mPositionOffset == 0 ? 1 : mPositionOffset;
                mCurrentPager = mLastKnownPosition;
                mNextPager = mLastKnownPosition + 1;
                gotoRight(mCurrentPager, mNextPager, mPositionOffset);
                if (mTCListener != null)
                    mTCListener.gotoRight(mCurrentPager, mNextPager, mPositionOffset);
            }
        }
        mLastKnownPosition = position;
        mLastKnownPositionOffset = positionOffset;
    }

    /**
     * 直接跳转到
     */
    protected abstract void jumpTo(int currect);

    /**
     * 滑向左边
     *
     * @param currect 当前页
     * @param next    目标页
     * @param offset  偏移
     */
    protected abstract void gotoLeft(int currect, int next, float offset);

    /**
     * 滑向右边
     *
     * @param currect 当前页
     * @param next    目标页
     * @param offset  偏移
     */
    protected abstract void gotoRight(int currect, int next, float offset);

    /**
     * 由触摸点转为Tag
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 坐标对应位置
     */
    protected int pointToPosition(float x, float y) {
        return 0;
    }

    /**
     * DOWN 事件对应Position
     *
     * @return 位置坐标
     */
    public int downPointToPosition() {
        return pointToPosition(mDownMotionX, mDownMotionY);
    }

    public float downPointX() {
        return mDownMotionX;
    }

    public float downPointY() {
        return mDownMotionY;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BaseTabStripSavedState ss = new BaseTabStripSavedState(superState);
        ss.currectPager = mPager != null ? mPager.getCurrentItem() : 0;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        BaseTabStripSavedState ss = (BaseTabStripSavedState) state;
        performClick(ss.currectPager, false, false);
        super.onRestoreInstanceState(ss.getSuperState());
    }

    static class BaseTabStripSavedState extends BaseSavedState {
        int currectPager;

        BaseTabStripSavedState(Parcelable superState) {
            super(superState);
        }

        private BaseTabStripSavedState(Parcel in) {
            super(in);
            currectPager = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currectPager);
        }

        public static final Parcelable.Creator<BaseTabStripSavedState> CREATOR = new Parcelable.Creator<BaseTabStripSavedState>() {
            public BaseTabStripSavedState createFromParcel(Parcel in) {
                return new BaseTabStripSavedState(in);
            }

            public BaseTabStripSavedState[] newArray(int size) {
                return new BaseTabStripSavedState[size];
            }
        };
    }

    private class PageListener extends DataSetObserver implements
            ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            updateView(position, positionOffset, false);
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                // Only update the text here if we're not dragging or settling.

                float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset
                        : 0;

                updateView(mPager.getCurrentItem(), offset, false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onAdapterChanged(PagerAdapter oldAdapter,
                                     PagerAdapter newAdapter) {
            updateAdapter(oldAdapter, newAdapter);
        }

        @Override
        public void onChanged() {

            final float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset
                    : 0;
            updateView(mPager.getCurrentItem(), offset, true);
        }
    }

    public final ViewPager getViewPager() {
        return mPager;
    }

    public final List<String> getViewTabs() {
        return mTabs;
    }

    /**
     * 设置Tab点击监听
     * @param listener 监听器
     */
    public void setOnTabClickListener(OnTabClickListener listener) {
        this.mListener = listener;
    }

    public ArrayList<Drawable> getTabDrawables() {
        return mTabDrawables;
    }

    public Drawable getTabDrawable() {
        return mTabDrawable;
    }

    public void setTabDrawable(int resId) {
        setTabDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    public void setTabDrawable(Drawable drawable) {
        if (mTabDrawable != drawable) {
            mTabDrawable = drawable;
            if (mPager != null && getDrawables(mPager.getAdapter())) {
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean willNeedTitle() {
        return willNeedTitle;
    }

    public void setWillNeedTitle(boolean willNeedTitle) {
        this.willNeedTitle = willNeedTitle;
    }

    public boolean willClick() {
        return willClick;
    }

    public void setWillClick(boolean willClick) {
        this.willClick = willClick;
    }

    /**
     * 设置Tab更改监听
     * @param listener 监听器
     */
    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.mTCListener = listener;
        if (this.mTCListener != null)
            this.mTCListener.jumpTo(mCurrentPager);
    }

    /**
     * Tab更改监听
     */
    public interface OnTabChangeListener {
        void jumpTo(int correct);

        void gotoLeft(int correct, int next, float offset);

        void gotoRight(int correct, int next, float offset);
    }

    /**
     * Tab点击监听
     */
    public interface OnTabClickListener {
        void onTabClick(int position);
    }
}