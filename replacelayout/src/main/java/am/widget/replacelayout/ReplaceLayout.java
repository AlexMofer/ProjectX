/*
 * Copyright (C) 2015 AlexMofer
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

package am.widget.replacelayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 交替布局
 * Created by Alex on 2015/8/28.
 */
public class ReplaceLayout extends FrameLayout {

    private ReplaceAdapter mAdapter;
    private int mCorrect = -1;
    private boolean shouldInterceptTouchEvent = false;

    public ReplaceLayout(Context context) {
        super(context);
    }

    public ReplaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReplaceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return shouldInterceptTouchEvent || super.onInterceptTouchEvent(ev);
    }

    /**
     * 移动到某一位置
     *
     * @param position 某一位置
     */
    @SuppressWarnings("unused")
    public void moveTo(int position) {
        shouldInterceptTouchEvent = false;
        if (mCorrect != position) {
            mCorrect = position;
            removeAllViews();
            if (mAdapter != null) {
                View correctView = mAdapter.getReplaceView(this, mCorrect);
                if (correctView != null)
                    addView(correctView);
                mAdapter.onSelected(this, mCorrect);
            }
        }
    }

    /**
     * 从某一位置移动一定距离
     *
     * @param position 某一位置
     * @param positionOffset  相对于完全到达另一位置的偏移（-1～1）
     */
    @SuppressWarnings("unused")
    public void move(int position, float positionOffset) {
        if (positionOffset < 0) {
            moveLeft(position, -positionOffset);
        } else{
            moveRight(position, positionOffset);
        }
    }

    /**
     * 从某一位置往左边移动一定距离
     *
     * @param correct 某一位置
     * @param offset  相对于完全到达左边位置的偏移（0～1）
     */
    public void moveLeft(int correct, float offset) {
        mCorrect = correct;
        int next = correct - 1;
        View correctView = null;
        View nextView = null;
        if (mAdapter != null) {
            correctView = mAdapter.getReplaceView(this, correct);
            nextView = mAdapter.getReplaceView(this, next);
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != correctView && child != nextView)
                removeView(child);
        }
        if (correctView != null && correctView.getParent() == null) {
            addView(correctView);
        }
        if (nextView != null && nextView.getParent() == null) {
            addView(nextView);
        }
        if (offset == 0) {
            shouldInterceptTouchEvent = false;
            mCorrect = next;
            if (correctView != null && correctView.getParent() != null) {
                removeView(correctView);
            }
            if (mAdapter != null)
                mAdapter.onSelected(this, mCorrect);
        } else {
            shouldInterceptTouchEvent = true;
            if (mAdapter != null)
                mAdapter.onAnimation(this, correct, next, offset);
        }
    }

    /**
     * 从某一位置往右边移动一定距离
     *
     * @param correct 某一位置
     * @param offset  相对于完全到达右边位置的偏移（0～1）
     */
    public void moveRight(int correct, float offset) {
        mCorrect = correct;
        int next = correct + 1;
        View correctView = null;
        View nextView = null;
        if (mAdapter != null) {
            correctView = mAdapter.getReplaceView(this, correct);
            nextView = mAdapter.getReplaceView(this, next);
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != correctView && child != nextView)
                removeView(child);
        }
        if (correctView != null && correctView.getParent() == null) {
            addView(correctView);
        }
        if (nextView != null && nextView.getParent() == null) {
            addView(nextView);
        }
        if (offset == 1) {
            shouldInterceptTouchEvent = false;
            mCorrect = next;
            if (correctView != null && correctView.getParent() != null) {
                removeView(correctView);
            }
            if (mAdapter != null)
                mAdapter.onSelected(this, mCorrect);
        } else {
            shouldInterceptTouchEvent = true;
            if (mAdapter != null)
                mAdapter.onAnimation(this, correct, next, 1F - offset);
        }
    }

    /**
     * 设置容器
     *
     * @param adapter 容器
     */
    @SuppressWarnings("unused")
    public void setAdapter(ReplaceAdapter adapter) {
        this.mAdapter = adapter;
        invalidateLayout();
    }

    public void invalidateLayout() {
        removeAllViews();
        if (mAdapter != null && mCorrect != -1) {
            View correctView = mAdapter.getReplaceView(this, mCorrect);
            if (correctView != null)
                addView(correctView);
            mAdapter.onSelected(this, mCorrect);
        }
    }

    public interface ReplaceAdapter {

        View getReplaceView(ReplaceLayout replaceLayout, int position);

        void onAnimation(ViewGroup replace, int correct, int next, float offset);

        void onSelected(ViewGroup replace, int position);
    }

}
