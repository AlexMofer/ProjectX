/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.am.widget.swiperefreshlayout;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.Toast;

/**
 * The SwipeRefreshLayout should be used whenever the user can refresh the
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The SwipeRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to show just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and
 * progress animation, call setEnabled(false) on the view.
 * 
 * <p>
 * This layout should be made the parent of the view that will be refreshed as a
 * result of the gesture and can only support one direct child. This view will
 * also be made the target of the gesture and will be forced to match both the
 * width and the height supplied in this layout. The SwipeRefreshLayout does not
 * provide accessibility events; instead, a menu item must be provided to allow
 * refresh of the content wherever this gesture is used.
 * </p>
 */
@SuppressLint({ "NewApi", "ClickableViewAccessibility" })
public class SuperSwipeRefreshLayout extends ViewGroup {
//	private static final String LOG_TAG = SwipeRefreshLayout.class
//			.getSimpleName();

	private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 300;
	private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
	private static final float PROGRESS_BAR_HEIGHT = 4;
	private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
	private static final int REFRESH_TRIGGER_DISTANCE = 120;
	private static final int INVALID_POINTER = -1;

	private SwipeProgressBar mStartProgressBar, mEndProgressBar; // the thing
																	// that
																	// shows
																	// progress
																	// is going
	private View mTarget; // the content that gets pulled down
	private int mOriginalOffsetTop;
	private OnRefreshListener mRefreshListener;
	private OnLoadListener mLoadListener;
	private int mFrom;
	private boolean mRefreshing = false;
	private boolean mLoading = false;
	private boolean refreshable = true;
	private boolean loadable = true;
	private int mTouchSlop;
	private float mDistanceToTriggerSync = -1;
	private int mMediumAnimationDuration;
	private float mFromPercentage = 0;
	private float mCurrPercentage = 0;
	private int mProgressBarHeight;
	private int mCurrentTargetOffsetTop;

	private float mInitialMotionY;
	private float mLastMotionY;
	private boolean mIsBeingDragged;
	private int mActivePointerId = INVALID_POINTER;

	private static final int TOUCH_SLOP = 20;
	private float stateY;
	private ScrollState state = ScrollState.NULL;
	private OnScrollDirectionChangedListener mDirectionListener;
	private OnScrollListener mScrollListener;
	private Method metFastSoller;
	private int mFSWidth, mFSHeight;

	/**
	 * 滚动状态
	 * 
	 */
	public enum ScrollState {
		NULL, TOP, BOTTOM;
	}

	// Target is returning to its start offset because it was cancelled or a
	// refresh was triggered.
	private boolean mReturningToStart;
	private final DecelerateInterpolator mDecelerateInterpolator;
	private final AccelerateInterpolator mAccelerateInterpolator;
	private static final int[] LAYOUT_ATTRS = new int[] {
			android.R.attr.enabled, android.R.attr.fastScrollThumbDrawable };
	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			if (mFrom != mOriginalOffsetTop) {
				targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
			}
			int offset = targetTop - mTarget.getTop();
			// final int currentTop = mTarget.getTop();
			// if (offset + currentTop < 0) {
			// offset = 0 - currentTop;
			// }
			setTargetOffsetTopAndBottom(offset);
		}
	};

	private Animation mStartShrinkTrigger = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			float percent = mFromPercentage
					+ ((0 - mFromPercentage) * interpolatedTime);
			mStartProgressBar.setTriggerPercentage(percent);
		}
	};
	private Animation mEndShrinkTrigger = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			float percent = mFromPercentage
					+ ((0 - mFromPercentage) * interpolatedTime);
			mEndProgressBar.setTriggerPercentage(percent);
		}
	};

	private final AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			// Once the target content has returned to its start position, reset
			// the target offset to 0
			mCurrentTargetOffsetTop = 0;
		}
	};

	private final AnimationListener mShrinkAnimationListener = new BaseAnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
			mCurrPercentage = 0;
		}
	};

	private final Runnable mReturnToStartPosition = new Runnable() {

		@Override
		public void run() {
			mReturningToStart = true;
			animateOffsetToStartPosition(mCurrentTargetOffsetTop
					+ getPaddingTop(), mReturnToStartPositionListener);
		}

	};

	// Cancel the refresh gesture and animate everything back to its original
	// state.
	private final Runnable mStartCancel = new Runnable() {

		@Override
		public void run() {
			mReturningToStart = true;
			// Timeout fired since the user last moved their finger; animate the
			// trigger to 0 and put the target back at its original position
			if (mStartProgressBar != null) {
				mFromPercentage = mCurrPercentage;
				mStartShrinkTrigger.setDuration(mMediumAnimationDuration);
				mStartShrinkTrigger
						.setAnimationListener(mShrinkAnimationListener);
				mStartShrinkTrigger.reset();
				mStartShrinkTrigger.setInterpolator(mDecelerateInterpolator);
				startAnimation(mStartShrinkTrigger);
			}
			animateOffsetToStartPosition(mCurrentTargetOffsetTop
					+ getPaddingTop(), mReturnToStartPositionListener);
		}

	};

	// Cancel the refresh gesture and animate everything back to its original
	// state.
	private final Runnable mEndCancel = new Runnable() {

		@Override
		public void run() {
			mReturningToStart = true;
			// Timeout fired since the user last moved their finger; animate the
			// trigger to 0 and put the target back at its original position
			if (mEndProgressBar != null) {
				mFromPercentage = mCurrPercentage;
				mEndShrinkTrigger.setDuration(mMediumAnimationDuration);
				mEndShrinkTrigger
						.setAnimationListener(mShrinkAnimationListener);
				mEndShrinkTrigger.reset();
				mEndShrinkTrigger.setInterpolator(mDecelerateInterpolator);
				startAnimation(mEndShrinkTrigger);
			}
			animateOffsetToStartPosition(mCurrentTargetOffsetTop
					+ getPaddingTop(), mReturnToStartPositionListener);
		}

	};

	/**
	 * Simple constructor to use when creating a SwipeRefreshLayout from code.
	 * 
	 * @param context
	 */
	public SuperSwipeRefreshLayout(Context context) {
		this(context, null);
	}

	/**
	 * Constructor that is called when inflating SwipeRefreshLayout from XML.
	 * 
	 * @param context
	 * @param attrs
	 */
	public SuperSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		mMediumAnimationDuration = getResources().getInteger(
				android.R.integer.config_mediumAnimTime);

		setWillNotDraw(false);
		mStartProgressBar = new SwipeProgressBar(this);
		mEndProgressBar = new SwipeProgressBar(this);
		final DisplayMetrics metrics = getResources().getDisplayMetrics();
		mProgressBarHeight = (int) (metrics.density * PROGRESS_BAR_HEIGHT);
		mDecelerateInterpolator = new DecelerateInterpolator(
				DECELERATE_INTERPOLATION_FACTOR);
		mAccelerateInterpolator = new AccelerateInterpolator(
				ACCELERATE_INTERPOLATION_FACTOR);

		final TypedArray a = context
				.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
		setEnabled(a.getBoolean(0, true));
		Drawable drawable = a.getDrawable(1);
		if (drawable != null) {
			mFSWidth = drawable.getMinimumWidth();
			mFSHeight = drawable.getMinimumHeight();
		}
		a.recycle();
		try {
			metFastSoller = AbsListView.class
					.getDeclaredMethod("isVerticalScrollBarHidden");
			metFastSoller.setAccessible(true);
			Boolean hidden = (Boolean) metFastSoller
					.invoke((AbsListView) mTarget);
			Toast.makeText(getContext(), hidden.toString(), Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		removeCallbacks(mStartCancel);
		removeCallbacks(mEndCancel);
		removeCallbacks(mReturnToStartPosition);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		removeCallbacks(mReturnToStartPosition);
		removeCallbacks(mStartCancel);
		removeCallbacks(mEndCancel);
	}

	private void animateOffsetToStartPosition(int from,
			AnimationListener listener) {
		mFrom = from;
		mAnimateToStartPosition.reset();
		mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
		mAnimateToStartPosition.setAnimationListener(listener);
		mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
		mTarget.startAnimation(mAnimateToStartPosition);
	}

	/**
	 * Set the listener to be notified when a refresh is triggered via the swipe
	 * gesture.
	 */
	public void setOnRefreshListener(OnRefreshListener listener) {
		mRefreshListener = listener;
	}

	/**
	 * Set the listener to be notified when a refresh is triggered via the swipe
	 * gesture.
	 */
	public void setOnLoadListener(OnLoadListener listener) {
		mLoadListener = listener;
	}

	public void setOnScrollDirectionChangedListener(
			OnScrollDirectionChangedListener listener) {
		mDirectionListener = listener;
	}

	public void setOnScrollListener(OnScrollListener listener) {
		mScrollListener = listener;
	}

	private void setTriggerPercentageStart(float percent) {
		if (percent == 0f) {
			// No-op. A null trigger means it's uninitialized, and setting it to
			// zero-percent
			// means we're trying to reset state, so there's nothing to reset in
			// this case.
			mCurrPercentage = 0;
			return;
		}
		mCurrPercentage = percent;
		mStartProgressBar.setTriggerPercentage(percent);
	}

	private void setTriggerPercentageEnd(float percent) {
		if (percent == 0f) {
			// No-op. A null trigger means it's uninitialized, and setting it to
			// zero-percent
			// means we're trying to reset state, so there's nothing to reset in
			// this case.
			mCurrPercentage = 0;
			return;
		}
		mCurrPercentage = percent;
		mEndProgressBar.setTriggerPercentage(percent);
	}

	/**
	 * Notify the widget that refresh state has changed. Do not call this when
	 * refresh is triggered by a swipe gesture.
	 * 
	 * @param refreshing
	 *            Whether or not the view should show refresh progress.
	 */
	public void setRefreshing(boolean refreshing) {
		if (mRefreshing != refreshing) {
			ensureTarget();
			mCurrPercentage = 0;
			mRefreshing = refreshing;
			if (mRefreshing) {
				mStartProgressBar.start();
			} else {
				mStartProgressBar.stop();
			}
		}
	}

	/**
	 * Notify the widget that refresh state has changed. Do not call this when
	 * refresh is triggered by a swipe gesture.
	 * 
	 * @param refreshing
	 *            Whether or not the view should show refresh progress.
	 */
	public void setLoading(boolean loading) {
		if (mLoading != loading) {
			ensureTarget();
			mCurrPercentage = 0;
			mLoading = loading;
			if (mLoading) {
				mEndProgressBar.start();
			} else {
				mEndProgressBar.stop();
			}
		}
	}

	/**
	 * @deprecated Use {@link #setColorSchemeResources(int, int, int, int)}
	 */
	@Deprecated
	public void setColorScheme(int colorRes1, int colorRes2, int colorRes3,
			int colorRes4) {
		setColorSchemeResources(colorRes1, colorRes2, colorRes3, colorRes4);
	}

	/**
	 * Set the four colors used in the progress animation from color resources.
	 * The first color will also be the color of the bar that grows in response
	 * to a user swipe gesture.
	 */
	public void setColorSchemeResources(int colorRes1, int colorRes2,
			int colorRes3, int colorRes4) {
		final Resources res = getResources();
		setColorSchemeColors(res.getColor(colorRes1), res.getColor(colorRes2),
				res.getColor(colorRes3), res.getColor(colorRes4));
	}

	/**
	 * Set the four colors used in the progress animation. The first color will
	 * also be the color of the bar that grows in response to a user swipe
	 * gesture.
	 */
	public void setColorSchemeColors(int color1, int color2, int color3,
			int color4) {
		ensureTarget();
		mStartProgressBar.setColorScheme(color1, color2, color3, color4);
		mEndProgressBar.setColorScheme(color1, color2, color3, color4);
	}

	/**
	 * @return Whether the SwipeRefreshWidget is actively showing refresh
	 *         progress.
	 */
	public boolean isRefreshing() {
		return mRefreshing;
	}

	/**
	 * @return Whether the SwipeRefreshWidget is actively showing refresh
	 *         progress.
	 */
	public boolean isLoading() {
		return mLoading;
	}

	/**
	 * @return Whether the SwipeRefreshWidget is actively showing refresh
	 *         progress.
	 */
	public void setRefreshable(boolean enable) {
		refreshable = enable;
	}

	/**
	 * @return Whether the SwipeRefreshWidget is actively showing refresh
	 *         progress.
	 */
	public void setLoadable(boolean enable) {
		loadable = enable;
	}

	private void ensureTarget() {
		// Don't bother getting the parent height if the parent hasn't been laid
		// out yet.
		if (mTarget == null) {
			if (getChildCount() > 1 && !isInEditMode()) {
				throw new IllegalStateException(
						"SwipeRefreshLayout can host only one direct child");
			}
			mTarget = getChildAt(0);
			mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
		}
		if (mDistanceToTriggerSync == -1) {
			if (getParent() != null && ((View) getParent()).getHeight() > 0) {
				final DisplayMetrics metrics = getResources()
						.getDisplayMetrics();
				mDistanceToTriggerSync = (int) Math.min(
						((View) getParent()).getHeight()
								* MAX_SWIPE_DISTANCE_FACTOR,
						REFRESH_TRIGGER_DISTANCE * metrics.density);
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		mStartProgressBar.draw(canvas);
		canvas.save();
		canvas.translate(0, getMeasuredHeight() - mProgressBarHeight);
		mEndProgressBar.draw(canvas);
		canvas.restore();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		final int width = getMeasuredWidth();
		final int height = getMeasuredHeight();
		mStartProgressBar.setBounds(0, 0, width, mProgressBarHeight);
		mEndProgressBar.setBounds(0, 0, width, mProgressBarHeight);
		if (getChildCount() == 0) {
			return;
		}
		final View child = getChildAt(0);
		final int childLeft = getPaddingLeft();
		final int childTop = mCurrentTargetOffsetTop + getPaddingTop();
		final int childWidth = width - getPaddingLeft() - getPaddingRight();
		final int childHeight = height - getPaddingTop() - getPaddingBottom();
		child.layout(childLeft, childTop, childLeft + childWidth, childTop
				+ childHeight);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getChildCount() > 1 && !isInEditMode()) {
			throw new IllegalStateException(
					"SwipeRefreshLayout can host only one direct child");
		}
		if (getChildCount() > 0) {
			getChildAt(0).measure(
					MeasureSpec.makeMeasureSpec(getMeasuredWidth()
							- getPaddingLeft() - getPaddingRight(),
							MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(getMeasuredHeight()
							- getPaddingTop() - getPaddingBottom(),
							MeasureSpec.EXACTLY));
		}
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 *         scroll up. Override this if the child view is a custom view.
	 */
	public boolean canChildScrollUp() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0
						&& (absListView.getFirstVisiblePosition() > 0 || absListView
								.getChildAt(0).getTop() < absListView
								.getPaddingTop());
			} else {
				return mTarget.getScrollY() > 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, -1);
		}
	}

	/**
	 * @return Whether it is possible for the child view of this layout to
	 *         scroll down. Override this if the child view is a custom view.
	 */
	public boolean canChildScrollDown() {
		if (android.os.Build.VERSION.SDK_INT < 14) {
			if (mTarget instanceof AbsListView) {
				//TODO 待验证
				final AbsListView absListView = (AbsListView) mTarget;
				return absListView.getChildCount() > 0
						&& (absListView.getLastVisiblePosition() < absListView
								.getChildCount() - 1 || absListView.getChildAt(
								absListView.getChildCount() - 1).getBottom() > absListView
								.getHeight() - absListView.getPaddingBottom());
			} else {
				return mTarget.getScrollY() < 0;
			}
		} else {
			return ViewCompat.canScrollVertically(mTarget, 1);
		}
	}

	private boolean isFastSollerVisible() {
		boolean hidden = false;
		try {
			hidden = (Boolean) metFastSoller.invoke((AbsListView) mTarget);
		} catch (Exception e) {
		}
		return hidden;
	}

	private boolean isPointInside(float x, float y) {
		if (x > getWidth() - mFSWidth) {
			if (y < mFSHeight || y > getHeight() - mFSHeight)
				return true;
		}
		return false;
	}

	private boolean getActionDown = false;
	
	private boolean isTouchFS = false;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mScrollListener != null)
			mScrollListener.onScroll(this);
		
		ensureTarget();
		
		final int action = MotionEventCompat.getActionMasked(ev);
		
		if (action == MotionEvent.ACTION_DOWN) {
			if (isFastSollerVisible() && isPointInside(ev.getX(), ev.getY()) && (!canChildScrollUp() || !canChildScrollDown())) {
				isTouchFS = true;
				return false;
			}
		} else if (action == MotionEvent.ACTION_UP ||action == MotionEvent.ACTION_CANCEL) {
			isTouchFS = false;
		}
		
		if (isTouchFS)
			return false;
		//触摸事件发生在快速滑动滑块上
		checkUpOrDown(ev);
		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}

		if (!isEnabled() || mReturningToStart) {
			// Fail fast if we're not in a state where a swipe is possible
			getActionDown = false;
			return false;
		}
		if ((!canChildScrollUp() && refreshable)
				&& (!canChildScrollDown() && loadable)) {
			return interceptTouchEvent(action, ev, null);
		} else if (!canChildScrollUp() && refreshable) {
			return interceptTouchEvent(action, ev, true);
		} else if (!canChildScrollDown() && loadable) {
			return interceptTouchEvent(action, ev, false);
		} else {
			// Fail fast if we're not in a state where a swipe is possible
			getActionDown = false;
			return false;
		}

	}

	/**
	 * 判断滑动方向
	 * 
	 * @param ev
	 */
	private void checkUpOrDown(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			stateY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (stateY + TOUCH_SLOP / 2 < event.getY()) {
				if (state != ScrollState.TOP && mDirectionListener != null) {
					mDirectionListener.onDirectionChanged(this, state,
							ScrollState.TOP);
					state = ScrollState.TOP;
				}
				stateY = event.getY();

			} else if (stateY - TOUCH_SLOP / 2 > event.getY()) {
				if (state != ScrollState.BOTTOM && mDirectionListener != null) {
					mDirectionListener.onDirectionChanged(this, state,
							ScrollState.BOTTOM);
					state = ScrollState.BOTTOM;

				}
				stateY = event.getY();
			} else {
				// do nothing
			}
			break;
		}
	}

	public void setScrollDirectionChangedTo(ScrollState state, boolean listen) {
		if (state != this.state && mDirectionListener != null && listen) {
			mDirectionListener.onDirectionChanged(this, this.state, state);
			this.state = state;
		} else {
			// do nothing
		}

	}

	private boolean interceptTouchEvent(int action, MotionEvent ev, Boolean up) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			getActionDown = true;
			break;

		case MotionEvent.ACTION_MOVE:
			if (!getActionDown) {
				mLastMotionY = mInitialMotionY = ev.getY();
				mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
				mIsBeingDragged = false;
				mCurrPercentage = 0;
				getActionDown = true;
			}
			if (mActivePointerId == INVALID_POINTER) {
//				Log.e(LOG_TAG,
//						"Got ACTION_MOVE event but don't have an active pointer id.");
				return false;
			}

			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (pointerIndex < 0) {
//				Log.e(LOG_TAG,
//						"Got ACTION_MOVE event but have an invalid active pointer id.");
				return false;
			}

			final float y = MotionEventCompat.getY(ev, pointerIndex);
			float f;
			if (up == null) {
				f = Math.abs(y - mInitialMotionY);
			} else if (up) {
				f = y - mInitialMotionY;
			} else {
				f = mInitialMotionY - y;
			}
			final float yDiff = f;
			if (yDiff > mTouchSlop) {
				mLastMotionY = y;
				mIsBeingDragged = true;
			}
			break;

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			mActivePointerId = INVALID_POINTER;
			getActionDown = false;
			break;
		}
		return mIsBeingDragged;
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean b) {
		// Nope.
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		

		final int action = MotionEventCompat.getActionMasked(ev);

		if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
			mReturningToStart = false;
		}

		if (!isEnabled() || mReturningToStart) {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}
		if ((!canChildScrollUp() && refreshable)
				&& (!canChildScrollDown() && loadable)) {
			return touchEvent(action, ev, null);
		} else if (!canChildScrollUp() && refreshable) {
			return touchEvent(action, ev, true);
		} else if (!canChildScrollDown() && loadable) {
			return touchEvent(action, ev, false);
		} else {
			// Fail fast if we're not in a state where a swipe is possible
			return false;
		}

	}

	private boolean touchEvent(int action, MotionEvent ev, Boolean up) {
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = mInitialMotionY = ev.getY();
			mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			break;

		case MotionEvent.ACTION_MOVE:
			final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
			if (pointerIndex < 0) {
//				Log.e(LOG_TAG,
//						"Got ACTION_MOVE event but have an invalid active pointer id.");
				return false;
			}

			final float y = MotionEventCompat.getY(ev, pointerIndex);
			float f;
			boolean toBottom;
			if (up == null) {
				f = y - mInitialMotionY;
				if (f <= 0) {
					f = mInitialMotionY - y;
					toBottom = true;
				} else {
					toBottom = false;
				}
			} else if (up) {
				f = y - mInitialMotionY;
				toBottom = false;
			} else {
				f = mInitialMotionY - y;
				toBottom = true;
			}
			final float yDiff = f;

			if (!mIsBeingDragged && yDiff > mTouchSlop) {
				mIsBeingDragged = true;
			}

			if (mIsBeingDragged) {
				if (toBottom) {
					// User velocity passed min velocity; trigger a refresh
					if (yDiff > mDistanceToTriggerSync) {

						// User movement passed distance; trigger a refresh
						startLoad();
					} else {
						// Just track the user's movement
						setTriggerPercentageEnd(mAccelerateInterpolator
								.getInterpolation(yDiff
										/ mDistanceToTriggerSync));
						updateContentOffsetBottom((int) (yDiff));
						if (mLastMotionY > y
								&& mTarget.getBottom() == getPaddingBottom()) {
							// If the user puts the view back at the top, we
							// don't need to. This shouldn't be considered
							// cancelling the gesture as the user can
							// restart
							// from
							// the top.
							removeCallbacks(mEndCancel);
						} else {
							updatePositionTimeoutEnd();
						}
					}
				} else {
					// User velocity passed min velocity; trigger a refresh
					if (yDiff > mDistanceToTriggerSync) {
						// User movement passed distance; trigger a refresh
						startRefresh();
					} else {
						// Just track the user's movement
						setTriggerPercentageStart(mAccelerateInterpolator
								.getInterpolation(yDiff
										/ mDistanceToTriggerSync));
						updateContentOffsetTop((int) (yDiff));
						if (mLastMotionY > y
								&& mTarget.getTop() == getPaddingTop()) {
							// If the user puts the view back at the top, we
							// don't need to. This shouldn't be considered
							// cancelling the gesture as the user can
							// restart
							// from
							// the top.
							removeCallbacks(mStartCancel);
						} else {
							updatePositionTimeoutStart();
						}
					}
				}
				mLastMotionY = y;
			}
			break;

		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int index = MotionEventCompat.getActionIndex(ev);
			mLastMotionY = MotionEventCompat.getY(ev, index);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			break;
		}

		case MotionEventCompat.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mIsBeingDragged = false;
			mCurrPercentage = 0;
			mActivePointerId = INVALID_POINTER;
			return false;
		}

		return true;
	}

	private void startRefresh() {
		removeCallbacks(mStartCancel);
		mReturnToStartPosition.run();
		setRefreshing(true);
		if (mRefreshListener != null)
			mRefreshListener.onRefresh();
	}

	private void startLoad() {
		removeCallbacks(mEndCancel);
		mReturnToStartPosition.run();
		setLoading(true);
		if (mLoadListener != null)
			mLoadListener.onLoad();
	}

	private void updateContentOffsetTop(int targetTop) {
		final int currentTop = mTarget.getTop();
		if (targetTop > mDistanceToTriggerSync) {
			targetTop = (int) mDistanceToTriggerSync;
		} else if (targetTop < 0) {
			targetTop = 0;
		}
		setTargetOffsetTopAndBottom(targetTop - currentTop);
	}

	private void updateContentOffsetBottom(int targetBottom) {
		final int currentTop = mTarget.getTop();
		if (targetBottom > mDistanceToTriggerSync) {
			targetBottom = (int) mDistanceToTriggerSync;
		} else if (targetBottom < 0) {
			targetBottom = 0;
		}
		setTargetOffsetTopAndBottom(-targetBottom - currentTop);
	}

	private void setTargetOffsetTopAndBottom(int offset) {
		mTarget.offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = mTarget.getTop();
	}

	private void updatePositionTimeoutStart() {
		removeCallbacks(mStartCancel);
		postDelayed(mStartCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
	}

	private void updatePositionTimeoutEnd() {
		removeCallbacks(mEndCancel);
		postDelayed(mEndCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = MotionEventCompat.getActionIndex(ev);
		final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
			mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
		}
	}

	/**
	 * Classes that wish to be notified when the swipe gesture correctly
	 * triggers a refresh should implement this interface.
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 * Classes that wish to be notified when the swipe gesture correctly
	 * triggers a refresh should implement this interface.
	 */
	public interface OnLoadListener {
		public void onLoad();
	}

	public interface OnScrollDirectionChangedListener {
		public void onDirectionChanged(SuperSwipeRefreshLayout srl,
				ScrollState last, ScrollState currect);
	}

	public interface OnScrollListener {
		public void onScroll(SuperSwipeRefreshLayout srl);
	}

	/**
	 * Simple AnimationListener to avoid having to implement unneeded methods in
	 * AnimationListeners.
	 */
	private class BaseAnimationListener implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * Custom progress bar that shows a cycle of colors as widening circles that
	 * overdraw each other. When finished, the bar is cleared from the inside
	 * out as the main cycle continues. Before running, this can also indicate
	 * how close the user is to triggering something (e.g. how far they need to
	 * pull down to trigger a refresh).
	 */
	final class SwipeProgressBar {

		// Default progress animation colors are grays.
		private final static int COLOR1 = 0xff00ddff;// holo_blue_bright
		private final static int COLOR2 = 0xff99cc00;// holo_green_light
		private final static int COLOR3 = 0xffffbb33;// holo_orange_light
		private final static int COLOR4 = 0xffff4444;// holo_red_light

		// The duration of the animation cycle.
		private static final int ANIMATION_DURATION_MS = 2000;

		// The duration of the animation to clear the bar.
		private static final int FINISH_ANIMATION_DURATION_MS = 1000;

		// Interpolator for varying the speed of the animation.
		private final Interpolator INTERPOLATOR = new BakedBezierInterpolator();

		private final Paint mPaint = new Paint();
		private final RectF mClipRect = new RectF();
		private float mTriggerPercentage;
		private long mStartTime;
		private long mFinishTime;
		private boolean mRunning;

		// Colors used when rendering the animation,
		private int mColor1;
		private int mColor2;
		private int mColor3;
		private int mColor4;
		private View mParent;

		private Rect mBounds = new Rect();

		public SwipeProgressBar(View parent) {
			mParent = parent;
			mColor1 = COLOR1;
			mColor2 = COLOR2;
			mColor3 = COLOR3;
			mColor4 = COLOR4;
		}

		/**
		 * Set the four colors used in the progress animation. The first color
		 * will also be the color of the bar that grows in response to a user
		 * swipe gesture.
		 * 
		 * @param color1
		 *            Integer representation of a color.
		 * @param color2
		 *            Integer representation of a color.
		 * @param color3
		 *            Integer representation of a color.
		 * @param color4
		 *            Integer representation of a color.
		 */
		void setColorScheme(int color1, int color2, int color3, int color4) {
			mColor1 = color1;
			mColor2 = color2;
			mColor3 = color3;
			mColor4 = color4;
		}

		/**
		 * Update the progress the user has made toward triggering the swipe
		 * gesture. and use this value to update the percentage of the trigger
		 * that is shown.
		 */
		void setTriggerPercentage(float triggerPercentage) {
			mTriggerPercentage = triggerPercentage;
			mStartTime = 0;
			ViewCompat.postInvalidateOnAnimation(mParent);
		}

		/**
		 * Start showing the progress animation.
		 */
		void start() {
			if (!mRunning) {
				mTriggerPercentage = 0;
				mStartTime = AnimationUtils.currentAnimationTimeMillis();
				mRunning = true;
				mParent.postInvalidate();
			}
		}

		/**
		 * Stop showing the progress animation.
		 */
		void stop() {
			if (mRunning) {
				mTriggerPercentage = 0;
				mFinishTime = AnimationUtils.currentAnimationTimeMillis();
				mRunning = false;
				mParent.postInvalidate();
			}
		}

		/**
		 * @return Return whether the progress animation is currently running.
		 */
		boolean isRunning() {
			return mRunning || mFinishTime > 0;
		}

		void draw(Canvas canvas) {
			final int width = mBounds.width();
			final int height = mBounds.height();
			final int cx = width / 2;
			final int cy = height / 2;
			boolean drawTriggerWhileFinishing = false;
			int restoreCount = canvas.save();
			canvas.clipRect(mBounds);

			if (mRunning || (mFinishTime > 0)) {
				long now = AnimationUtils.currentAnimationTimeMillis();
				long elapsed = (now - mStartTime) % ANIMATION_DURATION_MS;
				long iterations = (now - mStartTime) / ANIMATION_DURATION_MS;
				float rawProgress = (elapsed / (ANIMATION_DURATION_MS / 100f));

				// If we're not running anymore, that means we're running
				// through
				// the finish animation.
				if (!mRunning) {
					// If the finish animation is done, don't draw anything, and
					// don't repost.
					if ((now - mFinishTime) >= FINISH_ANIMATION_DURATION_MS) {
						mFinishTime = 0;
						return;
					}

					// Otherwise, use a 0 opacity alpha layer to clear the
					// animation
					// from the inside out. This layer will prevent the circles
					// from
					// drawing within its bounds.
					long finishElapsed = (now - mFinishTime)
							% FINISH_ANIMATION_DURATION_MS;
					float finishProgress = (finishElapsed / (FINISH_ANIMATION_DURATION_MS / 100f));
					float pct = (finishProgress / 100f);
					// Radius of the circle is half of the screen.
					float clearRadius = width / 2
							* INTERPOLATOR.getInterpolation(pct);
					mClipRect
							.set(cx - clearRadius, 0, cx + clearRadius, height);
					canvas.saveLayerAlpha(mClipRect, 0, 0);
					// Only draw the trigger if there is a space in the center
					// of
					// this refreshing view that needs to be filled in by the
					// trigger. If the progress view is just still animating,
					// let it
					// continue animating.
					drawTriggerWhileFinishing = true;
				}

				// First fill in with the last color that would have finished
				// drawing.
				if (iterations == 0) {
					canvas.drawColor(mColor1);
				} else {
					if (rawProgress >= 0 && rawProgress < 25) {
						canvas.drawColor(mColor4);
					} else if (rawProgress >= 25 && rawProgress < 50) {
						canvas.drawColor(mColor1);
					} else if (rawProgress >= 50 && rawProgress < 75) {
						canvas.drawColor(mColor2);
					} else {
						canvas.drawColor(mColor3);
					}
				}

				// Then draw up to 4 overlapping concentric circles of varying
				// radii, based on how far
				// along we are in the cycle.
				// progress 0-50 draw mColor2
				// progress 25-75 draw mColor3
				// progress 50-100 draw mColor4
				// progress 75 (wrap to 25) draw mColor1
				if ((rawProgress >= 0 && rawProgress <= 25)) {
					float pct = (((rawProgress + 25) * 2) / 100f);
					drawCircle(canvas, cx, cy, mColor1, pct);
				}
				if (rawProgress >= 0 && rawProgress <= 50) {
					float pct = ((rawProgress * 2) / 100f);
					drawCircle(canvas, cx, cy, mColor2, pct);
				}
				if (rawProgress >= 25 && rawProgress <= 75) {
					float pct = (((rawProgress - 25) * 2) / 100f);
					drawCircle(canvas, cx, cy, mColor3, pct);
				}
				if (rawProgress >= 50 && rawProgress <= 100) {
					float pct = (((rawProgress - 50) * 2) / 100f);
					drawCircle(canvas, cx, cy, mColor4, pct);
				}
				if ((rawProgress >= 75 && rawProgress <= 100)) {
					float pct = (((rawProgress - 75) * 2) / 100f);
					drawCircle(canvas, cx, cy, mColor1, pct);
				}
				if (mTriggerPercentage > 0 && drawTriggerWhileFinishing) {
					// There is some portion of trigger to draw. Restore the
					// canvas,
					// then draw the trigger. Otherwise, the trigger does not
					// appear
					// until after the bar has finished animating and appears to
					// just jump in at a larger width than expected.
					canvas.restoreToCount(restoreCount);
					restoreCount = canvas.save();
					canvas.clipRect(mBounds);
					drawTrigger(canvas, cx, cy);
				}
				// Keep running until we finish out the last cycle.
				ViewCompat.postInvalidateOnAnimation(mParent);
			} else {
				// Otherwise if we're in the middle of a trigger, draw that.
				if (mTriggerPercentage > 0 && mTriggerPercentage <= 1.0) {
					drawTrigger(canvas, cx, cy);
				}
			}
			canvas.restoreToCount(restoreCount);
		}

		private void drawTrigger(Canvas canvas, int cx, int cy) {
			mPaint.setColor(mColor1);
			canvas.drawCircle(cx, cy, cx * mTriggerPercentage, mPaint);
		}

		/**
		 * Draws a circle centered in the view.
		 * 
		 * @param canvas
		 *            the canvas to draw on
		 * @param cx
		 *            the center x coordinate
		 * @param cy
		 *            the center y coordinate
		 * @param color
		 *            the color to draw
		 * @param pct
		 *            the percentage of the view that the circle should cover
		 */
		private void drawCircle(Canvas canvas, float cx, float cy, int color,
				float pct) {
			mPaint.setColor(color);
			canvas.save();
			canvas.translate(cx, cy);
			float radiusScale = INTERPOLATOR.getInterpolation(pct);
			canvas.scale(radiusScale, radiusScale);
			canvas.drawCircle(0, 0, cx, mPaint);
			canvas.restore();
		}

		/**
		 * Set the drawing bounds of this SwipeProgressBar.
		 */
		void setBounds(int left, int top, int right, int bottom) {
			mBounds.left = left;
			mBounds.top = top;
			mBounds.right = right;
			mBounds.bottom = bottom;
		}
	}

	/**
	 * A pre-baked bezier-curved interpolator for indeterminate progress
	 * animations.
	 */
	final class BakedBezierInterpolator implements Interpolator {

		/**
		 * Use getInstance instead of instantiating.
		 */
		private BakedBezierInterpolator() {
			super();
		}

		/**
		 * Lookup table values. Generated using a Bezier curve from (0,0) to
		 * (1,1) with control points: P0 (0,0) P1 (0.4, 0) P2 (0.2, 1.0) P3
		 * (1.0, 1.0)
		 * 
		 * Values sampled with x at regular intervals between 0 and 1.
		 */
		private final float[] VALUES = new float[] { 0.0f, 0.0002f, 0.0009f,
				0.0019f, 0.0036f, 0.0059f, 0.0086f, 0.0119f, 0.0157f, 0.0209f,
				0.0257f, 0.0321f, 0.0392f, 0.0469f, 0.0566f, 0.0656f, 0.0768f,
				0.0887f, 0.1033f, 0.1186f, 0.1349f, 0.1519f, 0.1696f, 0.1928f,
				0.2121f, 0.237f, 0.2627f, 0.2892f, 0.3109f, 0.3386f, 0.3667f,
				0.3952f, 0.4241f, 0.4474f, 0.4766f, 0.5f, 0.5234f, 0.5468f,
				0.5701f, 0.5933f, 0.6134f, 0.6333f, 0.6531f, 0.6698f, 0.6891f,
				0.7054f, 0.7214f, 0.7346f, 0.7502f, 0.763f, 0.7756f, 0.7879f,
				0.8f, 0.8107f, 0.8212f, 0.8326f, 0.8415f, 0.8503f, 0.8588f,
				0.8672f, 0.8754f, 0.8833f, 0.8911f, 0.8977f, 0.9041f, 0.9113f,
				0.9165f, 0.9232f, 0.9281f, 0.9328f, 0.9382f, 0.9434f, 0.9476f,
				0.9518f, 0.9557f, 0.9596f, 0.9632f, 0.9662f, 0.9695f, 0.9722f,
				0.9753f, 0.9777f, 0.9805f, 0.9826f, 0.9847f, 0.9866f, 0.9884f,
				0.9901f, 0.9917f, 0.9931f, 0.9944f, 0.9955f, 0.9964f, 0.9973f,
				0.9981f, 0.9986f, 0.9992f, 0.9995f, 0.9998f, 1.0f, 1.0f };

		private final float STEP_SIZE = 1.0f / (VALUES.length - 1);

		@Override
		public float getInterpolation(float input) {
			if (input >= 1.0f) {
				return 1.0f;
			}

			if (input <= 0f) {
				return 0f;
			}

			int position = Math.min((int) (input * (VALUES.length - 1)),
					VALUES.length - 2);

			float quantized = position * STEP_SIZE;
			float difference = input - quantized;
			float weight = difference / STEP_SIZE;

			return VALUES[position] + weight
					* (VALUES[position + 1] - VALUES[position]);
		}

	}
}
