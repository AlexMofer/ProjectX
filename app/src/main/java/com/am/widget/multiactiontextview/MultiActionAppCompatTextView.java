package com.am.widget.multiactiontextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 文字可点击TextView
 * 
 * @author Mofer
 * 
 */
public class MultiActionAppCompatTextView extends AppCompatTextView {

	private static final int[] ATTRS = new int[] { android.R.attr.textColorHighlight};
	private int mHighlightColor = Color.TRANSPARENT;
	private OnTextClickedListener mListener;
	protected boolean onTextClicked = false;// 用于屏蔽onClick事件

	public MultiActionAppCompatTextView(Context context) {
		super(context);
		setMovementMethod(LinkMovementMethod.getInstance());
		if (!isFocusable()) {
			setFocusable(true);
		}
		if (!isFocusableInTouchMode()) {
			setFocusableInTouchMode(true);
		}
	}

	public MultiActionAppCompatTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setMovementMethod(LinkMovementMethod.getInstance());
		if (!isFocusable()) {
			setFocusable(true);
		}
		if (!isFocusableInTouchMode()) {
			setFocusableInTouchMode(true);
		}
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		mHighlightColor = a.getColor(0, Color.TRANSPARENT);
		a.recycle();
	}

	public MultiActionAppCompatTextView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setMovementMethod(LinkMovementMethod.getInstance());
		if (!isFocusable()) {
			setFocusable(true);
		}
		if (!isFocusableInTouchMode()) {
			setFocusableInTouchMode(true);
		}
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		mHighlightColor = a.getColor(0, Color.TRANSPARENT);
		a.recycle();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!isFocusable()) {
			setFocusable(true);
		}
		if (!isFocusableInTouchMode()) {
			setFocusableInTouchMode(true);
		}
		if (!isFocused()) {
			requestFocus();
			requestFocusFromTouch();
		}
		final int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			super.setHighlightColor(mHighlightColor);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			super.setHighlightColor(Color.TRANSPARENT);
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void setHighlightColor(int color) {
		mHighlightColor = color;
		super.setHighlightColor(color);
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnTextClickedListener(OnTextClickedListener listener) {
		mListener = listener;
	}

	/**
	 * 设置可点击文字
	 * 
	 * @param text
	 */

	public void setClickableText(String text) {
		setText(createSpannableString(text, null));
	}

	/**
	 * 设置可点击文字
	 * 
	 * @param text
	 * @param userId
	 */

	public void setClickableText(String text, Object data) {
		setText(createSpannableString(text, data));
	}

	/**
	 * 创建可点击SpannableString
	 * @param text
	 * @param data
	 * @return
	 */
	public SpannableString createSpannableString(String text, Object data) {
		return createSpannableString(text, data, getTextColors().getDefaultColor());
	}
	
	/**
	 * 创建可点击SpannableString
	 * @param text
	 * @param data
	 * @param color
	 * @return
	 */
	public SpannableString createSpannableString(String text, Object data, int color) {
		if (text != null && !"".endsWith(text)) {
			SpannableString spannable = new SpannableString(text);
			MultiActionClickableSpan click = new MultiActionClickableSpan(data, color);
			spannable.setSpan(click, 0, text.length(),
					SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
			return spannable;
		}
		return null;
	}

	class MultiActionClickableSpan extends ClickableSpan {

		private Object mData;
		private int mColor;
		
		public MultiActionClickableSpan(Object data, int color) {
			mData = data;
			mColor = color;
		}

		@Override
		public void onClick(final View widget) {
			onTextClicked = true;
			if (mListener != null) {
				mListener.onTextClicked(widget, mData);
			}
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			ds.setUnderlineText(false);
			ds.clearShadowLayer();
			ds.setColor(mColor);
		}

	}

}
