/*
 * Copyright (C) 2012 The Android Open Source Project
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

package am.project.x.widgets.bilateralpanelayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * BilateralPaneLayout
 * 
 * @author xiangzhicheng
 * 
 */
public class BilateralPaneLayout extends ViewGroup {
	private static final String TAG = "BilateralPaneLayout";
	private static final int MIN_FLING_VELOCITY = 400; // dips per second
	private View mLeftPaneView;
	private View mCenterPaneView;
	private View mRightPaneView;
	private boolean slideable = true;
	private boolean leftSlideable = true;
	private boolean rightSlideable = true;
	private boolean isUnableToSlide;
	private final ViewDragHelper mDragHelper;
	private float mSlideOffset = 0;
	private PanelSlideListener mPanelSlideListener;
	private float mInitialMotionX;
	private float mInitialMotionY;
	private final ArrayList<WeakReference<View>> mRightCheckViews = new ArrayList<WeakReference<View>>();
	private final ArrayList<WeakReference<View>> mLeftCheckViews = new ArrayList<WeakReference<View>>();
	private final ArrayList<WeakReference<ScrollHorizontally>> mLeftCheckViewsICS = new ArrayList<WeakReference<ScrollHorizontally>>();

	public BilateralPaneLayout(Context context) {
		this(context, null);
	}

	public BilateralPaneLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BilateralPaneLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		final float density = context.getResources().getDisplayMetrics().density;
		mDragHelper = ViewDragHelper.create(this, 0.5f,
				new DragHelperCallback());
		mDragHelper.setMinVelocity(MIN_FLING_VELOCITY * density);
		mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT
				| ViewDragHelper.EDGE_LEFT);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			Log.e(TAG, "Width must have an exact value or MATCH_PARENT");
			throw new IllegalStateException(
					"Width must have an exact value or MATCH_PARENT");
		} else if (heightMode == MeasureSpec.UNSPECIFIED) {
			Log.e(TAG, "Height must not be UNSPECIFIED");
			throw new IllegalStateException("Height must not be UNSPECIFIED");
		} else if (getChildCount() != 3) {
			Log.e(TAG, "Must have three child views.");
			throw new IllegalStateException("Must have three child views.");
		}
		mLeftPaneView = getChildAt(0);
		mCenterPaneView = getChildAt(1);
		mRightPaneView = getChildAt(2);
		final int widthAvailable = widthSize - getPaddingLeft()
				- getPaddingRight();
		final int heightAvailable = heightSize - getPaddingTop()
				- getPaddingBottom();
		int maxLayoutHeight = -1;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);

			final LayoutParams lp = child.getLayoutParams();
			int childWidthMeasureSpec;
			int childHeightMeasureSpec;

			if (lp.width == LayoutParams.MATCH_PARENT) {
				childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
						widthAvailable, MeasureSpec.EXACTLY);
			} else {
				childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
						getPaddingLeft() + getPaddingRight(), lp.width);
			}

			if (lp.height == LayoutParams.MATCH_PARENT) {
				childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
						heightAvailable, MeasureSpec.EXACTLY);
			} else {
				childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
						getPaddingTop() + getPaddingBottom(), lp.height);
			}
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			maxLayoutHeight = child.getMeasuredHeight() > maxLayoutHeight ? child
					.getMeasuredHeight() : maxLayoutHeight;
		}
		maxLayoutHeight = maxLayoutHeight + getPaddingTop()
				+ getPaddingBottom();
		setMeasuredDimension(widthSize, maxLayoutHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final int paddingLeft = getPaddingLeft();
		final int paddingRight = getPaddingRight();
		final int paddingTop = getPaddingTop();
		if (mSlideOffset < 0) {
			mLeftPaneView
					.layout((int) (paddingLeft
							- mLeftPaneView.getMeasuredWidth() - mLeftPaneView
							.getMeasuredWidth() * mSlideOffset),
							paddingTop,
							(int) (paddingLeft - mLeftPaneView
									.getMeasuredWidth() * mSlideOffset),
							paddingTop + mLeftPaneView.getMeasuredHeight());
			mCenterPaneView.layout(
					(int) (paddingLeft - mLeftPaneView.getMeasuredWidth()
							* mSlideOffset), paddingTop,
					(int) (paddingLeft - mLeftPaneView.getMeasuredWidth()
							* mSlideOffset)
							+ mCenterPaneView.getMeasuredWidth(), paddingTop
							+ mCenterPaneView.getMeasuredHeight());
			mRightPaneView.layout(
					getWidth() - paddingRight,
					paddingTop,
					getWidth() - paddingRight
							+ mRightPaneView.getMeasuredWidth(), paddingTop
							+ mRightPaneView.getMeasuredHeight());
		} else if (mSlideOffset > 0) {
			mLeftPaneView
					.layout(paddingLeft - mLeftPaneView.getMeasuredWidth(),
							paddingTop, paddingLeft,
							paddingTop + mLeftPaneView.getMeasuredHeight());
			mCenterPaneView
					.layout((int) (getWidth() - paddingRight
							- mCenterPaneView.getMeasuredWidth() - mRightPaneView
							.getMeasuredWidth() * mSlideOffset),
							paddingTop,
							(int) (getWidth() - paddingRight - mRightPaneView
									.getMeasuredWidth() * mSlideOffset),
							paddingTop + mCenterPaneView.getMeasuredHeight());
			mRightPaneView.layout(
					(int) (getWidth() - paddingRight - mRightPaneView
							.getMeasuredWidth() * mSlideOffset),
					paddingTop,
					(int) (getWidth() - paddingRight - mRightPaneView
							.getMeasuredWidth() * mSlideOffset)
							+ mRightPaneView.getMeasuredWidth(), paddingTop
							+ mRightPaneView.getMeasuredHeight());
		} else {
			mLeftPaneView
					.layout(paddingLeft - mLeftPaneView.getMeasuredWidth(),
							paddingTop, paddingLeft,
							paddingTop + mLeftPaneView.getMeasuredHeight());
			mCenterPaneView.layout(paddingLeft, paddingTop, paddingLeft
					+ mCenterPaneView.getMeasuredWidth(), paddingTop
					+ mCenterPaneView.getMeasuredHeight());
			mRightPaneView.layout(
					getWidth() - paddingRight,
					paddingTop,
					getWidth() - paddingRight
							+ mRightPaneView.getMeasuredWidth(), paddingTop
							+ mRightPaneView.getMeasuredHeight());
		}

	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			postInvalidateOnAnimationCompat();
		}
	}

	/**
	 * 刷新
	 */
	private void postInvalidateOnAnimationCompat() {
		ViewCompat.postInvalidateOnAnimation(this);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!slideable) {
			mDragHelper.cancel();
			return false;
		}
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		boolean interceptTap = false;
		if (action == MotionEvent.ACTION_DOWN) {
			mDragHelper.shouldInterceptTouchEvent(ev);
			mInitialMotionX = x;
			mInitialMotionY = y;
			isUnableToSlide = false;
			if (isTouchUnder(mLeftPaneView, x, y)) {
				if (!mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_RIGHT) && (canChildScrollHorizontally(mLeftPaneView, 1)
						|| canLeftViewScroll(x, y)
						|| canLeftViewScrollISC(x, y))) {
					isUnableToSlide = true;
				}
			} else if (isTouchUnder(mRightPaneView, x, y)) {
				if (!mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT) && (canChildScrollHorizontally(mRightPaneView, -1)
						|| canRightViewScroll(x, y))) {
					isUnableToSlide = true;
				}
			} else {
				if (mSlideOffset != 0) {
					interceptTap = true;
				}
			}
			mDragHelper.cancel();
		}
		if (isUnableToSlide) {
			return false;
		}
		if (action == MotionEvent.ACTION_MOVE) {
			final float adx = Math.abs(x - mInitialMotionX);
			final float ady = Math.abs(y - mInitialMotionY);
			final int slop = mDragHelper.getTouchSlop();
			if (adx > slop && ady > adx) {
				mDragHelper.cancel();
				return false;
			}
		}
		final boolean interceptForDrag = mDragHelper
				.shouldInterceptTouchEvent(ev);
		return interceptForDrag || interceptTap;
	}

	/**
	 * 检查右边是否可滚动
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canRightViewScroll(float x, float y) {
		boolean canScroll = false;
		for (WeakReference<View> wRight : mRightCheckViews) {
			View right = wRight.get();
			if (isTouchUnder(right, x, y)) {
				if (canChildScrollHorizontally(right, -1)) {
					canScroll = true;
					break;
				}
			}
		}
		return canScroll;
	}

	private boolean isTouchUnder(View view, float x, float y) {
		if (view == null) {
			return false;
		}
		return x >= view.getLeft() && x < view.getRight() && y >= view.getTop()
				&& y < view.getBottom();
	}

	/**
	 * 检查左边是否可滚动
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canLeftViewScroll(float x, float y) {
		boolean canScroll = false;
		for (WeakReference<View> wLeft : mLeftCheckViews) {
			View left = wLeft.get();
			if (left != null) {
				if (isTouchUnder(left, x, y)) {
					if (canChildScrollHorizontally(left, 1)) {
						canScroll = true;
						break;
					}
				}
			}
		}
		return canScroll;
	}

	/**
	 * 检查左边是否可滚动
	 * <p>
	 * for API < 14
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canLeftViewScrollISC(float x, float y) {
		boolean canScroll = false;
		for (WeakReference<ScrollHorizontally> wLeft : mLeftCheckViewsICS) {
			ScrollHorizontally left = wLeft.get();
			if (left != null && left instanceof View) {
				if (isTouchUnder((View) left, x, y)) {
					if (left.canScrollHorizontally(1)) {
						canScroll = true;
						break;
					}
				}
			}
		}
		return canScroll;
	}
	
	public final void setPanelSlideListener(PanelSlideListener listener) {
		mPanelSlideListener = listener;
	}

	/**
	 * 不用担心，ViewPager 已实现canScrollHorizontally()方法，不存在版本问题，14 以下普通View 左滑判断
	 * 存在不兼容，需谨慎使用左滑面板
	 * 
	 * @return Whether it is possible for the child view of this layout to
	 *         scroll Horizontally.
	 */
	@SuppressLint("NewApi")
	public boolean canChildScrollHorizontally(View child, int direction) {
		if (direction == 0) {
			return true;
		}
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (child instanceof ViewPager) {
				final ViewPager viewPager = (ViewPager) child;
				return viewPager.canScrollHorizontally(direction);
			} else {
				if (direction < 0) {
					return child.getScrollX() > 0;
				} else {
					if (child instanceof HorizontalScrollView) {
						final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) child;
						final int maxScroll = horizontalScrollView
								.getMaxScrollAmount();
						final int left = child.getScrollX();
						final int width = child.getWidth();
						if (maxScroll <= width - left - child.getPaddingRight()) {
							return true;
						}
						return false;
					} else {
						// 不支持
						return false;
					}
				}
			}
		} else {
			return ViewCompat.canScrollHorizontally(child, direction);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!slideable) {
			mDragHelper.cancel();
			return super.onTouchEvent(event);
		}
		mDragHelper.processTouchEvent(event);
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mInitialMotionX = x;
			mInitialMotionY = y;
			break;

		case MotionEvent.ACTION_UP:
			final float dx = x - mInitialMotionX;
			final float dy = y - mInitialMotionY;
			final int slop = mDragHelper.getTouchSlop();
			if (dx * dx + dy * dy < slop * slop
					&& isTouchUnder(mCenterPaneView, x, y)) {
				performClick();
				break;
			}
			break;
		}
		return true;
	}

	@Override
	public boolean performClick() {
		return super.performClick() || closePane();
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		SavedState ss = new SavedState(superState);
		ss.slideOffset = mSlideOffset;
		return ss;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		mSlideOffset = ss.slideOffset;
		//requestLayout();
	}

	public boolean closePane() {
		return closePane(true);
	}

	public boolean closePane(boolean animate) {
		if (animate) {
			return smoothSlideTo(0) ? true : false;
		} else {
			mSlideOffset = 0;
			requestLayout();
			dispatchOnPanelClosed();
			return true;
		}

	}

	public boolean openLeft() {
		return openLeft(true);
	}

	public boolean openLeft(boolean animate) {
		if (animate) {
			return smoothSlideTo(-1) ? true : false;
		} else {
			mSlideOffset = -1;
			requestLayout();
			dispatchOnLeftOpened();
			return true;
		}

	}

	public boolean openRight() {
		return openRight(true);
	}

	public boolean openRight(boolean animate) {
		if (animate) {
			return smoothSlideTo(1) ? true : false;
		} else {
			mSlideOffset = 1;
			requestLayout();
			dispatchOnRightOpened();
			return true;
		}

	}

	private boolean smoothSlideTo(int type) {
		if (!slideable) {
			return false;
		}
		int left;
		switch (type) {
		default:
		case 0:
			left = getPaddingLeft();
			break;
		case -1:
			left = getPaddingLeft() + mLeftPaneView.getMeasuredWidth();
			break;
		case 1:
			left = getWidth() - getPaddingRight()
					- mRightPaneView.getMeasuredWidth()
					- mCenterPaneView.getMeasuredWidth();
			break;
		}
		if (mDragHelper.smoothSlideViewTo(mCenterPaneView, left,
				mCenterPaneView.getTop())) {
			ViewCompat.postInvalidateOnAnimation(this);
			return true;
		}
		return false;
	}

	private void dispatchOnPanelSlide() {
		if (mPanelSlideListener != null) {
			mPanelSlideListener.onPanelSlide(mLeftPaneView, mCenterPaneView,
					mRightPaneView, mSlideOffset);
		}
	}

	private void dispatchOnLeftOpened() {
		if (mPanelSlideListener != null) {
			mPanelSlideListener.onLeftOpened(mLeftPaneView, mCenterPaneView,
					mRightPaneView);
		}
	}

	private void dispatchOnRightOpened() {
		if (mPanelSlideListener != null) {
			mPanelSlideListener.onRightOpened(mLeftPaneView, mCenterPaneView,
					mRightPaneView);
		}
	}

	private void dispatchOnPanelClosed() {
		if (mPanelSlideListener != null) {
			mPanelSlideListener.onPanelClosed(mLeftPaneView, mCenterPaneView,
					mRightPaneView);
		}
	}

	private class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			if (!slideable) {
				return false;
			}
			if (child == mLeftPaneView) {
				return leftSlideable;
			} else if (child == mRightPaneView) {
				return rightSlideable;
			} else {
				return true;
			}
		}

		@Override
		public void onViewDragStateChanged(int state) {
			if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
				if (mSlideOffset == -1) {
					dispatchOnLeftOpened();
					mDragHelper
							.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
				} else if (mSlideOffset == 1) {
					dispatchOnRightOpened();
					mDragHelper
							.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
				} else {
					dispatchOnPanelClosed();
					mDragHelper
							.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT
									| ViewDragHelper.EDGE_LEFT);
				}
			}
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			onPanelDragged(changedView, left);
			postInvalidateOnAnimationCompat();
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int left;
			if (releasedChild == mLeftPaneView) {
				left = getPaddingLeft() - mLeftPaneView.getMeasuredWidth();
				if (xvel > 0 || (xvel == 0 && mSlideOffset < -0.5f)) {
					left = getPaddingLeft();
				}
			} else if (releasedChild == mCenterPaneView) {
				left = getPaddingLeft();
				if (xvel > 0) {
					if (mSlideOffset < 0) {
						left = mLeftPaneView.getMeasuredWidth()
								+ getPaddingLeft();
					}
				} else if (xvel < 0) {
					if (mSlideOffset > 0) {
						left = getWidth() - getPaddingRight()
								- mRightPaneView.getMeasuredWidth()
								- mCenterPaneView.getMeasuredWidth();
					}
				} else {
					if (mSlideOffset < -0.5f) {
						left = mLeftPaneView.getMeasuredWidth()
								+ getPaddingLeft();
					} else if (mSlideOffset > 0.5f) {
						left = getWidth() - getPaddingRight()
								- mRightPaneView.getMeasuredWidth()
								- mCenterPaneView.getMeasuredWidth();
					}
				}
			} else {
				left = getWidth() - getPaddingRight();
				if (xvel < 0 || (xvel == 0 && mSlideOffset > 0.5f)) {
					left = getWidth() - getPaddingRight()
							- mRightPaneView.getMeasuredWidth();
				}
			}
			mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
			postInvalidateOnAnimationCompat();
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			return mLeftPaneView.getMeasuredWidth() - getPaddingLeft() > mRightPaneView
					.getMeasuredWidth() - getPaddingRight() ? mLeftPaneView
					.getMeasuredWidth() - getPaddingLeft() : mRightPaneView
					.getMeasuredWidth() - getPaddingRight();
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			final int newLeft;
			final int startBound;
			final int endBound;
			if (child == mLeftPaneView) {
				startBound = getPaddingLeft()
						- mLeftPaneView.getMeasuredWidth();
				endBound = getPaddingLeft();
			} else if (child == mCenterPaneView) {
				startBound = getWidth() - getPaddingRight()
						- mRightPaneView.getMeasuredWidth()
						- mCenterPaneView.getMeasuredWidth();
				endBound = getPaddingLeft() + mLeftPaneView.getMeasuredWidth();
			} else {
				startBound = getWidth() - getPaddingRight()
						- mRightPaneView.getMeasuredWidth();
				endBound = getWidth() - getPaddingRight();
			}
			newLeft = Math.min(Math.max(left, startBound), endBound);
			return newLeft;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			return child.getTop();
		}

		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {

			mDragHelper.captureChildView(mCenterPaneView, pointerId);
		}
	}

	public void onPanelDragged(View changedView, int left) {
		final int otherLeft;
		if (changedView == mLeftPaneView) {
			mSlideOffset = (float) (left + mLeftPaneView.getMeasuredWidth())
					/ (float) -mLeftPaneView.getMeasuredWidth();
			otherLeft = left + mLeftPaneView.getMeasuredWidth();
			mCenterPaneView.layout(otherLeft, getPaddingTop(), otherLeft
					+ mCenterPaneView.getMeasuredWidth(), getPaddingTop()
					+ mCenterPaneView.getMeasuredHeight());
		} else if (changedView == mCenterPaneView) {
			if (left > getPaddingLeft()) {
				mSlideOffset = (float) left
						/ (float) -(mLeftPaneView.getMeasuredWidth() + getPaddingLeft());
				otherLeft = left - mLeftPaneView.getMeasuredWidth();
				mLeftPaneView.layout(otherLeft, getPaddingTop(), otherLeft
						+ mLeftPaneView.getMeasuredWidth(), getPaddingTop()
						+ mLeftPaneView.getMeasuredHeight());
				if (mRightPaneView.getLeft() < getWidth() - getPaddingRight()) {
					mRightPaneView.layout(
							getWidth() - getPaddingRight(),
							getPaddingTop(),
							getWidth() - getPaddingRight()
									+ mRightPaneView.getMeasuredWidth(),
							getPaddingTop()
									+ mRightPaneView.getMeasuredHeight());
				}
			} else {
				mSlideOffset = -(float) (left - getPaddingLeft())
						/ (float) mRightPaneView.getMeasuredWidth();
				otherLeft = left + mCenterPaneView.getMeasuredWidth();
				if (mLeftPaneView.getRight() > getPaddingLeft()) {
					mLeftPaneView
							.layout(getPaddingLeft()
									- mLeftPaneView.getMeasuredWidth(),
									getPaddingTop(),
									getPaddingLeft(),
									getPaddingTop()
											+ mLeftPaneView.getMeasuredHeight());
				}
				mRightPaneView.layout(otherLeft, getPaddingTop(), otherLeft
						+ mRightPaneView.getMeasuredWidth(), getPaddingTop()
						+ mRightPaneView.getMeasuredHeight());
			}

		} else {
			mSlideOffset = (float) (getWidth() - getPaddingRight() - left)
					/ (float) mRightPaneView.getMeasuredWidth();
			otherLeft = left - mCenterPaneView.getMeasuredWidth();
			mCenterPaneView.layout(otherLeft, getPaddingTop(), left,
					getPaddingTop() + mCenterPaneView.getMeasuredHeight());
		}
		dispatchOnPanelSlide();
	}

	static class SavedState extends BaseSavedState {
		float slideOffset;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			slideOffset = in.readFloat();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeFloat(slideOffset);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public final boolean isSlideable() {
		return slideable;
	}

	public final void setSlideable(boolean slideable) {
		this.slideable = slideable;
	}

	public boolean isLeftSlideable() {
		return leftSlideable;
	}

	public void setLeftSlideable(boolean leftSlideable) {
		this.leftSlideable = leftSlideable;
	}

	public boolean isRightSlideable() {
		return rightSlideable;
	}

	public void setRightSlideable(boolean rightSlideable) {
		this.rightSlideable = rightSlideable;
	}

	/**
	 * 添加右检查View
	 * 
	 * @param right
	 */
	public void addRightCheckView(View... right) {
		if (right != null) {
			for (int i = 0; i < right.length; i++) {
				if (right[i] != null) {
					mRightCheckViews.add(new WeakReference<View>(right[i]));
				}
			}
		}
	}

	/**
	 * 移除右检查View
	 * 
	 * @param right
	 */
	public void removeRightCheckView(View... right) {
		if (right != null) {
			for (int j = 0; j < right.length; j++) {
				for (int i = 0; i < mRightCheckViews.size(); i++) {
					if (right[j] == mRightCheckViews.get(i).get()) {
						mRightCheckViews.remove(i);
						break;
					}
				}
			}

		}
	}

	/**
	 * 添加左检查View
	 * <p>
	 * 添加的View为非子View，一般是子View的子View
	 * 
	 * @param left
	 */
	public void addLeftCheckView(View... left) {
		if (left != null) {
			for (int i = 0; i < left.length; i++) {
				if (left[i] != null) {
					mLeftCheckViews.add(new WeakReference<View>(left[i]));
				}
			}
		}
	}

	/**
	 * 移除左检查View
	 * 
	 * @param left
	 */
	public void removeLeftCheckView(View... left) {
		if (left != null) {
			for (int j = 0; j < left.length; j++) {
				for (int i = 0; i < mLeftCheckViews.size(); i++) {
					if (left[j] == mLeftCheckViews.get(i).get()) {
						mLeftCheckViews.remove(i);
						break;
					}
				}
			}

		}
	}

	/**
	 * 添加左检查View
	 * <p>
	 * 添加的View为非子View，一般是子View的子View<br>
	 * for API < 14
	 * 
	 * @param left
	 */
	public void addLeftCheckViewICS(ScrollHorizontally... left) {
		if (left != null) {
			for (int i = 0; i < left.length; i++) {
				if (left[i] != null) {
					mLeftCheckViewsICS
							.add(new WeakReference<ScrollHorizontally>(left[i]));
				}
			}
		}
	}

	/**
	 * 移除左检查View for API < 14
	 * 
	 * @param left
	 */
	public void removeLeftCheckViewICS(ScrollHorizontally... left) {
		if (left != null) {
			for (int j = 0; j < left.length; j++) {
				for (int i = 0; i < mLeftCheckViewsICS.size(); i++) {
					if (left[j] == mLeftCheckViewsICS.get(i).get()) {
						mLeftCheckViewsICS.remove(i);
						break;
					}
				}
			}

		}
	}

	public interface PanelSlideListener {
		public void onPanelSlide(View left, View center, View right,
								 float slideOffset);

		public void onLeftOpened(View left, View center, View right);

		public void onRightOpened(View left, View center, View right);

		public void onPanelClosed(View left, View center, View right);
	}

	/**
	 * 水平滑动接口
	 * 
	 * 为 API 14 以下的用户提供
	 * 
	 * @author xiangzhicheng
	 * 
	 */
	public interface ScrollHorizontally {
		public boolean canScrollHorizontally(int direction);
	}

}
