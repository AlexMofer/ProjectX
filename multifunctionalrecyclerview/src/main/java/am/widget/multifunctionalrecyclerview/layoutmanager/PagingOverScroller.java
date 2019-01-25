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

package am.widget.multifunctionalrecyclerview.layoutmanager;

import android.content.Context;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.PublicRecyclerView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 分页飞行辅助器
 * Created by Alex on 2017/11/6.
 */
class PagingOverScroller implements Runnable {
    private final OverScroller mScroller;
    private RecyclerView mRecyclerView;
    private int mLastFlingX;
    private int mLastFlingY;
    // When set to true, postOnAnimation callbacks are delayed until the run method completes
    private boolean mEatRunOnAnimationRequest = false;

    // Tracks if postAnimationCallback should be re-attached when it is done
    private boolean mReSchedulePostAnimationCallback = false;

    PagingOverScroller(@NonNull Context context) {
        mScroller = new OverScroller(context, PublicRecyclerView.getScrollerInterpolator());
    }

    @Override
    public void run() {
        if (mRecyclerView == null)
            return;
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            stop();
            return; // no layout, cannot scroll.
        }
        disableRunOnAnimationRequests();
        // keep a local reference so that if it is changed during onAnimation method, it won't
        // cause unexpected behaviors
        final OverScroller scroller = mScroller;

        if (scroller.computeScrollOffset()) {
            final int x = scroller.getCurrX();
            final int y = scroller.getCurrY();
            final int dx = x - mLastFlingX;
            final int dy = y - mLastFlingY;
            mLastFlingX = x;
            mLastFlingY = y;
            if (dx != 0 || dy != 0)
                mRecyclerView.scrollBy(dx, dy);
            if (scroller.isFinished()) {
                if (layoutManager instanceof PagingLayoutManager) {
                    ((PagingLayoutManager) layoutManager).onFlingFinish();
                }
            } else {
                postOnAnimation();
            }
        }
        enableRunOnAnimationRequests();
    }

    private void disableRunOnAnimationRequests() {
        mReSchedulePostAnimationCallback = false;
        mEatRunOnAnimationRequest = true;
    }

    private void enableRunOnAnimationRequests() {
        mEatRunOnAnimationRequest = false;
        if (mReSchedulePostAnimationCallback) {
            postOnAnimation();
        }
    }

    private void postOnAnimation() {
        if (mEatRunOnAnimationRequest) {
            mReSchedulePostAnimationCallback = true;
        } else {
            if (mRecyclerView == null)
                return;
            mRecyclerView.removeCallbacks(this);
            ViewCompat.postOnAnimation(mRecyclerView, this);
        }
    }

    private void stop() {
        if (mRecyclerView == null)
            return;
        mRecyclerView.removeCallbacks(this);
        mScroller.abortAnimation();
    }

    void attach(RecyclerView view) {
        mRecyclerView = view;
    }

    void detach() {
        if (mRecyclerView == null)
            return;
        mRecyclerView.removeCallbacks(this);
        mRecyclerView = null;
    }

    void fling(int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        if (mRecyclerView == null)
            return;
        mLastFlingX = mLastFlingY = 0;
        mScroller.fling(0, 0, velocityX, velocityY, minX, maxX, minY, maxY);
        postOnAnimation();
    }
}