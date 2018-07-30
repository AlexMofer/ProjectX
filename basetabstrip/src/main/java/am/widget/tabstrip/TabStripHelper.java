/*
 * Copyright (C) 2018 AlexMofer
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

package am.widget.tabstrip;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * TabStrip辅助器
 */
class TabStripHelper extends DataSetObserver implements
        ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {

    private final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    private final View mView;
    private int mPagerId;
    private boolean mAutoFind;
    private ViewPager mPager;
    private int mScrollState = ViewPager.SCROLL_STATE_IDLE;
    private int mPosition = 0;
    private float mOffset = 0;
    private int mLastPosition = mPosition;
    private float mLastOffset = mOffset;
    private boolean mClickable = false;
    private float mDownX;
    private float mDownY;
    private int mClickPosition = PagerAdapter.POSITION_NONE;
    private boolean mClickSmoothScroll = false;
    private boolean mDoubleClickable = false;
    private long mLastUpTime;
    private float mFirstDownX;
    private float mFirstDownY;
    private float mFirstUpX;
    private float mFirstUpY;
    private float mSecondDownX;
    private float mSecondDownY;
    private boolean mDoubleClicked = false;
    private int mFirstPosition = PagerAdapter.POSITION_NONE;
    private int mSecondPosition = PagerAdapter.POSITION_NONE;
    private TabStripObservable mObservable;
    private boolean mAttached = false;

    TabStripHelper(View view) {
        mView = view;
    }

    @Nullable
    private static ViewPager findViewPager(View view, int id, boolean traversal) {
        ViewPager pager = null;
        if (id != View.NO_ID) {
            View target = null;
            final ViewParent parent = view.getParent();
            if (parent != null && parent instanceof View)
                target = ((View) parent).findViewById(id);
            if (target == null)
                target = view.getRootView().findViewById(id);
            if (target != null && target instanceof ViewPager) {
                pager = (ViewPager) target;
            }
        }
        if (pager == null && traversal) {
            final ViewParent parent = view.getParent();
            if (parent != null && parent instanceof View)
                pager = traversal((View) parent);
            if (pager == null)
                pager = traversal(view.getRootView());
        }
        return pager;
    }

    private static ViewPager traversal(View root) {
        if (root == null)
            return null;
        if (root instanceof ViewPager) {
            return (ViewPager) root;
        }
        if (root instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) root;
            final int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                final ViewPager target = traversal(group.getChildAt(i));
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    void set(int id, boolean autoFind, boolean smoothScroll) {
        mPagerId = id;
        mAutoFind = autoFind;
        mClickSmoothScroll = smoothScroll;
    }

    void onAttachedToWindow() {
        final ViewPager pager = TabStripHelper.findViewPager(mView, mPagerId, mAutoFind);
        if (pager != null)
            bindViewPager(pager);
        mAttached = true;
        if (mObservable != null)
            mObservable.registerObserver(mView);
    }

    void onDetachedFromWindow() {
        if (mPager != null) {
            onDetachedFromViewPager(mPager);
            mPager = null;
        }
        mAttached = false;
        if (mObservable != null)
            mObservable.unregisterObserver(mView);
    }

    void bindViewPager(ViewPager pager) {
        if (mPager == pager)
            return;
        boolean update = false;
        if (mPager != null) {
            onDetachedFromViewPager(mPager);
            update = true;
        }
        mPager = pager;
        if (mPager != null) {
            onAttachedToViewPager(mPager);
            update = false;
        }
        if (update && mScrollState == ViewPager.SCROLL_STATE_IDLE)
            updateView(true);
    }

    private void onAttachedToViewPager(@NonNull ViewPager pager) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onAttachedToViewPager(pager);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onAttachedToViewPager(pager);
        pager.addOnPageChangeListener(this);
        pager.addOnAdapterChangeListener(this);
        onViewPagerAdapterChanged(null, pager.getAdapter());
    }

    private void onDetachedFromViewPager(@NonNull ViewPager pager) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onDetachedFromViewPager(pager);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onDetachedFromViewPager(pager);
        pager.removeOnPageChangeListener(this);
        pager.removeOnAdapterChangeListener(this);
        final PagerAdapter adapter = pager.getAdapter();
        if (adapter != null) {
            adapter.unregisterDataSetObserver(this);
        }
    }

    private void onViewPagerAdapterChanged(@Nullable PagerAdapter oldAdapter,
                                           @Nullable PagerAdapter newAdapter) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onViewPagerAdapterChanged(oldAdapter, newAdapter);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onViewPagerAdapterChanged(oldAdapter, newAdapter);
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mPosition = 0;
            mOffset = 0;
            if (oldAdapter != null)
                oldAdapter.unregisterDataSetObserver(this);
            if (newAdapter != null)
                newAdapter.registerDataSetObserver(this);
            if (mPager != null)
                mPosition = mPager.getCurrentItem();
            updateView(true);
        }
    }

    @Override
    public void onChanged() {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onViewPagerAdapterDataChanged();
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onViewPagerAdapterDataChanged();
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mPosition = 0;
            mOffset = 0;
            if (mPager != null)
                mPosition = mPager.getCurrentItem();
            updateView(true);
        }
    }

    // OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onViewPagerScrolled(position, positionOffset);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onViewPagerScrolled(position, positionOffset);
        mPosition = position;
        mOffset = positionOffset;
        updateView(false);
    }

    @Override
    public void onPageSelected(int position) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onViewPagerItemSelected(position);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onViewPagerItemSelected(position);
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mPosition = position;
            updateView(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onViewPagerScrollStateChanged(state);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onViewPagerScrollStateChanged(state);
        mScrollState = state;
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            if (mPosition != mPager.getCurrentItem() || mOffset != 0) {
                mPosition = mPager.getCurrentItem();
                mOffset = 0;
                updateView(false);
            }
        }
    }

    // OnAdapterChangeListener
    @Override
    public void onAdapterChanged(@NonNull ViewPager viewPager,
                                 @Nullable PagerAdapter oldAdapter,
                                 @Nullable PagerAdapter newAdapter) {
        onViewPagerAdapterChanged(oldAdapter, newAdapter);
    }

    void updateView(boolean force) {
        if (force) {
            mLastPosition = mPosition;
            mLastOffset = mOffset;
            onViewPagerChanged(mPosition, mOffset);
        } else {
            if (mLastPosition != mPosition || mLastOffset != mOffset) {
                if (mScrollState == ViewPager.SCROLL_STATE_SETTLING && mOffset != 0) {
                    if (mPosition >= mLastPosition) {
                        mLastPosition = mPosition;
                        if (mOffset > 0) {
                            mLastOffset = mOffset;
                            onViewPagerChanged(mPosition, mOffset);
                        } else {
                            // 忽略为负的情况，其为不正常回调
                            mOffset = mLastOffset;
                        }
                    } else {
                        if (mPosition >= mPager.getCurrentItem()) {
                            mLastPosition = mPosition;
                            mLastOffset = mOffset;
                            onViewPagerChanged(mPosition, mOffset);
                        } else {
                            // 忽略超出当前子项位置的情况，其为不正常回调
                            mPosition = mLastPosition;
                            mOffset = mLastOffset;
                        }
                    }
                } else {
                    mLastPosition = mPosition;
                    mLastOffset = mOffset;
                    onViewPagerChanged(mPosition, mOffset);
                }
            }
        }
    }

    private void onViewPagerChanged(int position, float offset) {
        if (mView instanceof TabStripView)
            ((TabStripView) mView).onViewPagerChanged(position, offset);
        else if (mView instanceof TabStripViewGroup)
            ((TabStripViewGroup) mView).onViewPagerChanged(position, offset);
    }

    void setRespondClick(boolean respond) {
        mClickable = respond;
    }

    void setRespondDoubleClick(boolean respond) {
        mDoubleClickable = respond;
    }

    boolean onTouchEvent(MotionEvent event) {
        if (mClickable) {
            final float x = event.getX();
            final float y = event.getY();
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mClickPosition = PagerAdapter.POSITION_NONE;
                    mDownX = x;
                    mDownY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    mClickPosition = PagerAdapter.POSITION_NONE;
                    if (mPager != null && getPageCount() > 0) {
                        if (mView instanceof TabStripView)
                            mClickPosition = ((TabStripView) mView)
                                    .getClickedPosition(mDownX, mDownY, x, y);
                        else if (mView instanceof TabStripViewGroup)
                            mClickPosition = ((TabStripViewGroup) mView)
                                    .getClickedPosition(mDownX, mDownY, x, y);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mClickPosition = PagerAdapter.POSITION_NONE;
                    break;
            }
        }
        if (mDoubleClickable) {
            final float x = event.getX();
            final float y = event.getY();
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mFirstDownX = mSecondDownX;
                    mFirstDownY = mSecondDownY;
                    mSecondDownX = x;
                    mSecondDownY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    final long downTime = event.getDownTime();
                    if (mLastUpTime != 0 && downTime - mLastUpTime < DOUBLE_TAP_TIMEOUT) {
                        mDoubleClicked = true;
                        if (mView instanceof TabStripView)
                            mFirstPosition = ((TabStripView) mView).getClickedPosition(
                                    mFirstDownX, mFirstDownY, mFirstUpX, mFirstUpY);
                        else if (mView instanceof TabStripViewGroup)
                            mFirstPosition = ((TabStripViewGroup) mView).getClickedPosition(
                                    mFirstDownX, mFirstDownY, mFirstUpX, mFirstUpY);
                        if (mView instanceof TabStripView)
                            mSecondPosition = ((TabStripView) mView)
                                    .getClickedPosition(mSecondDownX, mSecondDownY, x, y);
                        else if (mView instanceof TabStripViewGroup)
                            mSecondPosition = ((TabStripViewGroup) mView)
                                    .getClickedPosition(mSecondDownX, mSecondDownY, x, y);
                        mLastUpTime = 0;
                    } else {
                        mDoubleClicked = false;
                        mFirstPosition = PagerAdapter.POSITION_NONE;
                        mSecondPosition = PagerAdapter.POSITION_NONE;
                        mFirstUpX = x;
                        mFirstUpY = y;
                        mLastUpTime = event.getEventTime();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:

                    break;
            }
        }

        return false;
    }

    boolean performClick() {
        boolean result = false;
        if (mClickable) {
            if (mClickPosition != PagerAdapter.POSITION_NONE) {
                final int position = mClickPosition;
                mClickPosition = PagerAdapter.POSITION_NONE;
                if (mView instanceof TabStripView)
                    result = ((TabStripView) mView).performClick(position, mClickSmoothScroll);
                else if (mView instanceof TabStripViewGroup)
                    result = ((TabStripViewGroup) mView).performClick(position, mClickSmoothScroll);
            }
        }
        if (mDoubleClickable) {
            if (mDoubleClicked) {
                final int first = mFirstPosition;
                final int second = mSecondPosition;
                mDoubleClicked = false;
                mFirstPosition = PagerAdapter.POSITION_NONE;
                mSecondPosition = PagerAdapter.POSITION_NONE;
                if (mView instanceof TabStripView)
                    ((TabStripView) mView).performDoubleClick(first, second, mClickSmoothScroll);
                else if (mView instanceof TabStripViewGroup)
                    ((TabStripViewGroup) mView).performDoubleClick(first, second, mClickSmoothScroll);
            }
        }
        return result;
    }

    boolean isBoundViewPager() {
        return mPager != null;
    }

    boolean isDoubleClick() {
        return mDoubleClicked;
    }

    void setClickSmoothScroll(boolean smoothScroll) {
        mClickSmoothScroll = smoothScroll;
    }

    void setObservable(TabStripObservable observable) {
        if (mAttached) {
            if (mObservable != null)
                mObservable.unregisterObserver(mView);
            mObservable = observable;
            if (mObservable != null)
                mObservable.registerObserver(mView);
        } else {
            mObservable = observable;
        }
    }

    int getPageCount() {
        if (mPager == null)
            return 0;
        final PagerAdapter adapter = mPager.getAdapter();
        if (adapter == null)
            return 0;
        return adapter.getCount();
    }

    void setCurrentItem(int position, boolean smoothScroll) {
        if (mPager != null)
            mPager.setCurrentItem(position, smoothScroll);
    }

    @Nullable
    CharSequence getPageTitle(int position) {
        if (mPager == null)
            return null;
        final PagerAdapter adapter = mPager.getAdapter();
        if (adapter == null)
            return null;
        return adapter.getPageTitle(position);
    }
}
