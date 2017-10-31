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

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * 点击事件
 * Created by Alex on 2016/7/27.
 */
@SuppressWarnings("all")
public class MultiActionClickableSpan extends ClickableSpan {
    private int mStart;
    private int mEnd;
    private boolean changeColor;
    private int mColor;
    private boolean mUnderline;
    private boolean mInterceptClick;
    private OnTextClickedListener mListener;
    private Object mTag;

    public MultiActionClickableSpan(int start, int end) {
        mStart = start;
        mEnd = end;
        changeColor = false;
        mUnderline = true;
        setInterceptClick(false);
    }

    public MultiActionClickableSpan(int start, int end, int color, boolean underline,
                                    boolean interceptClick, OnTextClickedListener listener) {
        mStart = start;
        mEnd = end;
        changeColor = true;
        mColor = color;
        mUnderline = underline;
        setInterceptClick(interceptClick);
        setOnTextClickedListener(listener);
    }

    @Override
    public void onClick(View widget) {
        if (widget instanceof MultiActionTextView) {
            MultiActionTextView actionTextView = (MultiActionTextView) widget;
            actionTextView.setInterceptClick(mInterceptClick);
        }
        if (mListener != null) {
            mListener.onTextClicked(widget, this);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(mUnderline);
        ds.clearShadowLayer();
        if (changeColor)
            ds.setColor(mColor);
    }

    public int getStart() {
        return mStart;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setInterceptClick(boolean interceptClick) {
        mInterceptClick = interceptClick;
    }

    public void setOnTextClickedListener(OnTextClickedListener listener) {
        mListener = listener;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * 文字点击监听
     */
    public interface OnTextClickedListener {
        void onTextClicked(View view, MultiActionClickableSpan span);
    }
}