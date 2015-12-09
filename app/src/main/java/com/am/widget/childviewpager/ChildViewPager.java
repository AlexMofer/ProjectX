package com.am.widget.childviewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

/**
 * ViewPager嵌套，子ViewPager
 * 
 * @author Mofer
 * 
 */
 public class ChildViewPager extends ViewPager {

	private ViewParent mParent;
	private float downX;
	private float downY;
	private static final int TOUCH_SLOP = 20;

	public ChildViewPager(Context context) {
		super(context);
	}

	public ChildViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	@SuppressLint("NewApi")
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN
				&& (canScrollHorizontally(1) || canScrollHorizontally(-1))) {
			if (mParent != null) {
				mParent.requestDisallowInterceptTouchEvent(true);
			} else {
				getParent().requestDisallowInterceptTouchEvent(true);
			}
			super.onInterceptTouchEvent(ev);
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			if (event.getX() < downX + TOUCH_SLOP
					&& event.getX() > downX - TOUCH_SLOP
					&& event.getY() < downY + TOUCH_SLOP
					&& event.getY() > downY - TOUCH_SLOP) {
				return performClick();
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	public void setParent(ViewParent parent) {
		mParent = parent;
	}

}
