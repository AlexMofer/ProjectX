/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.widget;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 带方向的 OnPageChangeCallback
 * Created by Alex on 2026/3/17.
 */
public class OnPageChangeCallbackDirected extends ViewPager2.OnPageChangeCallback {
    private int mState = ViewPager2.SCROLL_STATE_IDLE;
    private int mSelectedPosition;
    private int mFromPosition;
    private int mToPosition;
    private int mDraggingPosition;
    private int mSettlingPosition;

    @Override
    public final void onPageScrolled(final int position,
                                     final float positionOffset,
                                     final int positionOffsetPixels) {
        if (mState == ViewPager2.SCROLL_STATE_DRAGGING) {
            // 拖动模式
            final int leftPosition = mDraggingPosition - 1;
            if (position == leftPosition) {
                // 往左翻页
                mFromPosition = mDraggingPosition;
                mToPosition = leftPosition;
            } else if (position == mDraggingPosition) {
                // 往右翻页
                mFromPosition = mDraggingPosition;
                mToPosition = mDraggingPosition + 1;
            } else {
                if (position == mDraggingPosition + 1 && positionOffset == 0) {
                    // 即将跳转到右边第二页
                    mFromPosition = mDraggingPosition;
                    mToPosition = mDraggingPosition + 1;
                } else {
                    // 已经跳页
                    if (position > mDraggingPosition) {
                        // 已经跳转到右边第二页及以上
                        mDraggingPosition = position;
                        // 重入方法
                        onPageScrolled(position, positionOffset, positionOffsetPixels);
                        return;
                    }
                    if (position < leftPosition) {
                        // 已经跳转到左边第二页及以上
                        mDraggingPosition = position + 1;
                        // 重入方法
                        onPageScrolled(position, positionOffset, positionOffsetPixels);
                        return;
                    }
                }
            }
            onPageScrolled(mFromPosition, mToPosition, position, positionOffset);
        } else if (mState == ViewPager2.SCROLL_STATE_SETTLING) {
            // 自动模式
            if (positionOffset == 0) {
                // 抵达
                mFromPosition = mSelectedPosition;
                mToPosition = mSelectedPosition;
            } else {
                // 自动滚动中
                if (mSettlingPosition < mSelectedPosition) {
                    // 往右翻页
                    mFromPosition = mSelectedPosition - 1;
                    mToPosition = mSelectedPosition;
                } else if (mSettlingPosition > mSelectedPosition) {
                    // 往左翻页
                    mFromPosition = mSelectedPosition + 1;
                    mToPosition = mSelectedPosition;
                } else {
                    // 归位
                    if (mSettlingPosition == position) {
                        // 从右边归位
                        mFromPosition = mSettlingPosition;
                        mToPosition = position + 1;
                    } else {
                        // 从左边归位
                        mFromPosition = mSettlingPosition;
                        mToPosition = position;
                    }
                }
            }
            onPageScrolled(mFromPosition, mToPosition, position, positionOffset);
        } else {
            // 其他均算静止模式
            mFromPosition = mSelectedPosition;
            mToPosition = mSelectedPosition;
            onPageScrolled(mFromPosition, mToPosition, position, positionOffset);
        }
    }

    @Override
    public final void onPageSelected(final int position) {
        mSelectedPosition = position;
    }

    @Override
    public final void onPageScrollStateChanged(final int state) {
        mState = state;
        if (mState == ViewPager2.SCROLL_STATE_DRAGGING) {
            // 进入拖动模式
            mDraggingPosition = mSelectedPosition;
        } else if (mState == ViewPager2.SCROLL_STATE_SETTLING) {
            // 进入自动模式
            mSettlingPosition = mSelectedPosition;
        }
    }

    public void onPageScrolled(int fromPosition, int toPosition,
                               int scrolledPosition, float scrolledPositionOffset) {
    }
}