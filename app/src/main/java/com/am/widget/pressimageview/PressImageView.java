package com.am.widget.pressimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 按压效果ImageView
 * 
 * @author Mofer
 * 
 */
public class PressImageView extends ImageView {

	private Drawable mPressDrawable;
	private boolean control = true;

	public PressImageView(Context context) {
		super(context);
	}

	public PressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PressImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mPressDrawable != null) {
			mPressDrawable.setBounds(0, 0, getWidth(), getHeight());
			mPressDrawable.draw(canvas);
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mPressDrawable != null
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			DrawableCompat.setHotspot(mPressDrawable, event.getX(),
					event.getY());
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void drawableStateChanged() {
		if (mPressDrawable != null && mPressDrawable.isStateful()) {
			if (control) {
				mPressDrawable.setState(getDrawableState());
			} else {
				mPressDrawable.setState(EMPTY_STATE_SET);
			}
		}
		super.drawableStateChanged();

	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		boolean isPress = false;
		if (mPressDrawable != null && who == mPressDrawable) {
			isPress = true;
		}
		return isPress || super.verifyDrawable(who);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mPressDrawable != null) {
			mPressDrawable.setCallback(this);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mPressDrawable != null) {
			mPressDrawable.setCallback(null);
		}
		super.onDetachedFromWindow();
	}

	/**
	 * 设置按压 Drawable
	 * 
	 * @param id
	 */
	public void setPressDrawable(int id) {
		setPressDrawable(ContextCompat.getDrawable(getContext(), id));
	}

	/**
	 * 设置按压 Drawable
	 * 
	 * @param drawable
	 */
	public void setPressDrawable(Drawable drawable) {
		if (mPressDrawable != drawable) {
			if (mPressDrawable != null) {
				mPressDrawable.setCallback(null);
			}
			mPressDrawable = drawable;
			mPressDrawable.setCallback(this);
			invalidate();
		}
	}

	public boolean isControl() {
		return control;
	}

	public void setControl(boolean control) {
		this.control = control;
	}
}
