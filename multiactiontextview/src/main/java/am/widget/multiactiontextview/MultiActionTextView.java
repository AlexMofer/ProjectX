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

package am.widget.multiactiontextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 文字可点击TextView
 */
public class MultiActionTextView extends TextView {

    private static final int[] ATTRS = new int[]{android.R.attr.textColorHighlight};
    private int mHighlightColor = Color.TRANSPARENT;
    private OnClickListener mSetListener;
    private boolean interceptClick = false;

    public MultiActionTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public MultiActionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MultiActionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public MultiActionTextView(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setMovementMethod(LinkMovementMethod.getInstance());
        if (!isFocusable()) {
            setFocusable(true);
        }
        if (!isFocusableInTouchMode()) {
            setFocusableInTouchMode(true);
        }
        requestFocus();
        requestFocusFromTouch();
        super.setOnClickListener(new MultiActionListener());
        if (attrs == null)
            return;
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mHighlightColor = a.getColor(0, Color.TRANSPARENT);
        a.recycle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setInterceptClick(false);
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

    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused)
            super.onFocusChanged(true, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused)
            super.onWindowFocusChanged(true);
    }

    @Override
    public boolean isFocused() {
        return true;//一直返回true，假装这个控件一直获取着焦点
    }

    @Override
    public void setHighlightColor(int color) {
        mHighlightColor = color;
        super.setHighlightColor(color);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mSetListener = l;
    }

    void setInterceptClick(boolean intercept) {
        interceptClick = intercept;
    }

    /**
     * 设置文本
     *
     * @param resId   文本资源ID
     * @param actions 文本可点击项
     */
    public final void setText(int resId, MultiActionClickableSpan... actions) {
        if (actions == null || actions.length <= 0) {
            setText(resId);
            return;
        }
        setText(getResources().getString(resId), actions);
    }

    /**
     * 设置文本
     *
     * @param text    文本
     * @param actions 文本可点击项
     */
    public final void setText(CharSequence text, MultiActionClickableSpan... actions) {
        if (actions == null || actions.length <= 0) {
            setText(text);
            return;
        }
        SpannableString spannable = new SpannableString(text);
        for (MultiActionClickableSpan action : actions) {
            spannable.setSpan(action, action.getStart(), action.getEnd(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(spannable);
    }

    private class MultiActionListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (mSetListener == null)
                return;
            if (interceptClick)
                return;
            mSetListener.onClick(view);
        }
    }
}
