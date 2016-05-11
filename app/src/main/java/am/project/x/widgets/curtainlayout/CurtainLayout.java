package am.project.x.widgets.curtainlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 窗帘式提拉Layout
 * 
 * 最后一个Child为窗帘
 * 
 * @author Mofer
 * 
 */
public class CurtainLayout extends ViewGroup {

	private static final String TAG = "CurtainLayout";
	private static final int MIN_FLING_VELOCITY = 400; // dips per second
	private final ViewDragHelper mDragHelper;
	private View mCurtain;
	private View mHandler;
	private Rect mHandlerRect = new Rect();
	private boolean showIntercept;
	private boolean showCheckTap;
	private boolean clickHandlerToCurtainOut = true;
	private float mSlideOffset;
	private float mInitialMotionX;
	private float mInitialMotionY;
	private boolean mCanSlide = true;
	private boolean mSweeped = false;
	private OnCurtainDragListener mListener;

	public CurtainLayout(Context context) {
		this(context, null);
	}

	public CurtainLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CurtainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		final float density = context.getResources().getDisplayMetrics().density;
		mDragHelper = ViewDragHelper.create(this, 0.5f,
				new DragHelperCallback());
		mDragHelper.setMinVelocity(MIN_FLING_VELOCITY * density);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (heightMode == MeasureSpec.UNSPECIFIED) {
			throw new IllegalStateException("Height must not be UNSPECIFIED");
		}
		if (getChildCount() > 0) {
			mCurtain = getChildAt(getChildCount() - 1);
		}

		int layoutHeight = 0;
		int maxLayoutHeight = 0;
		switch (heightMode) {
		case MeasureSpec.EXACTLY:
			layoutHeight = maxLayoutHeight = heightSize - getPaddingTop()
					- getPaddingBottom();
			break;
		case MeasureSpec.AT_MOST:
			maxLayoutHeight = heightSize - getPaddingTop() - getPaddingBottom();
			break;
		}
		int layoutWidth = 0;
		int maxLayoutWidth = 0;
		switch (widthMode) {
		case MeasureSpec.EXACTLY:
			layoutWidth = maxLayoutWidth = widthSize - getPaddingLeft()
					- getPaddingRight();
			break;
		case MeasureSpec.AT_MOST:
			maxLayoutWidth = heightSize - getPaddingLeft() - getPaddingRight();
			break;
		}

		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			int childWidthSpec;
			if (lp.width == LayoutParams.WRAP_CONTENT) {
				childWidthSpec = MeasureSpec.makeMeasureSpec(maxLayoutWidth,
						MeasureSpec.AT_MOST);
			} else if (lp.width == LayoutParams.MATCH_PARENT) {
				childWidthSpec = MeasureSpec.makeMeasureSpec(maxLayoutWidth,
						MeasureSpec.EXACTLY);
			} else {
				childWidthSpec = MeasureSpec.makeMeasureSpec(lp.width,
						MeasureSpec.EXACTLY);
			}

			int childHeightSpec;
			if (lp.height == LayoutParams.WRAP_CONTENT) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
						MeasureSpec.AT_MOST);
			} else if (lp.height == LayoutParams.MATCH_PARENT) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(maxLayoutHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lp.height,
						MeasureSpec.EXACTLY);
			}
			child.measure(childWidthSpec, childHeightSpec);
			final int childWidth = child.getMeasuredWidth();
			final int childHeight = child.getMeasuredHeight();
			if (widthMode == MeasureSpec.AT_MOST && childWidth > layoutWidth) {
				layoutWidth = Math.min(childWidth, maxLayoutWidth);
			}
			if (heightMode == MeasureSpec.AT_MOST && childHeight > layoutHeight) {
				layoutHeight = Math.min(childHeight, maxLayoutHeight);
			}
		}

		final int measuredWidth = layoutWidth + getPaddingLeft()
				+ getPaddingRight();
		final int measuredHeight = layoutHeight + getPaddingTop()
				+ getPaddingBottom();

		setMeasuredDimension(measuredWidth, measuredHeight);

		if (mHandler == null) {
			mHandler = getChildAt(0);
			if (!mHandler.isClickable()) {
				mHandler.setClickable(true);
			}
		}

		if (mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE
				&& !mCanSlide) {
			// Cancel scrolling in progress, it's no longer relevant.
			mDragHelper.abort();
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int paddingLeft = getPaddingLeft();
		final int paddingTop = getPaddingTop();
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = getChildAt(i);
			if (child == mCurtain && mSweeped) {
				child.layout(paddingLeft,
						paddingTop - child.getMeasuredHeight(), paddingLeft
								+ child.getMeasuredWidth(), paddingTop);
			} else {
				child.layout(paddingLeft, paddingTop,
						paddingLeft + child.getMeasuredWidth(), paddingTop
								+ child.getMeasuredHeight());
			}
		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mCanSlide) {
			mDragHelper.cancel();
			return false;
		}
		final int action = MotionEventCompat.getActionMasked(ev);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (isInHander((int) ev.getX(), (int) ev.getY())) {
				showIntercept = true;
			} else {
				showIntercept = false;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			showIntercept = false;
			break;
		}
		if (showIntercept) {
			mDragHelper.shouldInterceptTouchEvent(ev);
			return true;
		} else {
			mDragHelper.cancel();
			return false;
		}
	}

	private boolean isInHander(int x, int y) {
		final int[] location = new int[2];
		getLocationOnScreen(location);
		final int[] locationHandler = new int[2];
		mHandler.getLocationOnScreen(locationHandler);
		final int left = locationHandler[0] - location[0];
		final int top = locationHandler[1] - location[1];
		mHandlerRect.set(left, top, left + mHandler.getMeasuredWidth(), top
				+ mHandler.getMeasuredHeight());
		if (mHandlerRect.contains(x, y)) {
			return true;
		}
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!mCanSlide) {
			return super.onTouchEvent(ev);
		}
		mDragHelper.processTouchEvent(ev);

		final float x = ev.getX();
		final float y = ev.getY();
		final int action = ev.getAction();
		boolean wantTouchEvents = true;
		final float dx = x - mInitialMotionX;
		final float dy = y - mInitialMotionY;
		final int slop = mDragHelper.getTouchSlop();
		switch (action & MotionEventCompat.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mInitialMotionX = x;
			mInitialMotionY = y;
			if (clickHandlerToCurtainOut) {
				showCheckTap = true;
			} else {
				showCheckTap = false;
			}
			
			break;
		case MotionEvent.ACTION_MOVE:
			if (showCheckTap && dx * dx + dy * dy >= slop * slop) {
				showCheckTap = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (showCheckTap && dx * dx + dy * dy < slop * slop) {
				// Taps close a dimmed open pane.
				int move = -getPaddingTop() - getHeight();
				if (mDragHelper.smoothSlideViewTo(mCurtain, mCurtain.getLeft(),
						move)) {
					ViewCompat.postInvalidateOnAnimation(this);
				}
			}
			break;
		}
		return wantTouchEvents;
	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			if (!mCanSlide) {
				mDragHelper.abort();
				return;
			}
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		SavedState ss = new SavedState(superState);
		ss.slideOffset = mSlideOffset;
		ss.sweeped = mSweeped ? 1 : 0;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		mSlideOffset = ss.slideOffset;
		mSweeped = ss.sweeped == 0 ? false : true;
		super.onRestoreInstanceState(ss.getSuperState());
	}

	static class SavedState extends BaseSavedState {
		float slideOffset;
		int sweeped;

		SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			slideOffset = in.readFloat();
			sweeped = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeFloat(slideOffset);
			out.writeInt(sweeped);
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

	private class DragHelperCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			if (child == mCurtain) {
				return true;
			}
			return false;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			mSlideOffset = -(top - getPaddingTop()) / (float) getHeight();
			notifyDragging(mSlideOffset);
			if (mSlideOffset == 1) {
				mSweeped = true;
			} else {
				mSweeped = false;
			}
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			int top = getPaddingTop();
			if (top >= releasedChild.getTop()) {

				if (yvel < 0 || (yvel == 0 && mSlideOffset > 0.5f)) {
					top -= (getHeight() + getPaddingTop());
				}
			}
			mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
			invalidate();
		}

		@Override
		public int getViewVerticalDragRange(View child) {
			return getHeight();
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {

			return child.getLeft();
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			final int newTop;
			int endBound = getPaddingTop();
			int startBound = endBound - getHeight();
			newTop = Math.min(Math.max(top, startBound), endBound);
			return newTop;
		}

		@Override
		public void onEdgeDragStarted(int edgeFlags, int pointerId) {
			mDragHelper.captureChildView(getChildAt(0), pointerId);
		}
	}
	
	private void notifyDragging(float offset) {
		if (mListener != null) {
			mListener.onDrag(offset);
		}
	}
	
	public void setCurtainHandle(int id) {
		setCurtainHandle(findViewById(id));
	}

	public void setCurtainHandle(View handle) {
		if (handle != null) {
			mHandler = handle;
		} else {
			Log.w(TAG, "Handle view is NULL!");
		}
	}

	public void setSlideable(boolean canSlide) {
		mCanSlide = canSlide;
	}

	public boolean isClickHandlerToCurtainOut() {
		return clickHandlerToCurtainOut;
	}

	public void setClickHandlerToCurtainOut(boolean clickHandlerToCurtainOut) {
		this.clickHandlerToCurtainOut = clickHandlerToCurtainOut;
	}

	public void setOnCurtainDragListener(OnCurtainDragListener listener) {
		mListener = listener;
	}

	public interface OnCurtainDragListener {
		public void onDrag(float offset);
	}
}
