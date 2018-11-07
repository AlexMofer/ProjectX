/*
 * Copyright (C) 2018 AlexMofer
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
package am.project.x.business.others.floatingactionmode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import am.project.x.R;

/**
 * 触摸绘图
 */
public class PressView extends View {

    private OnPressListener mListener;
    private final Rect mPress = new Rect();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF mOval = new RectF();
    private int mSlop;

    public PressView(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public PressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public PressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        final TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.PressView,
                defStyleAttr, 0);
        final int color = custom.getColor(R.styleable.PressView_pvColor, 0x80fe439b);
        final int slop = custom.getDimensionPixelOffset(R.styleable.PressView_pvSlop, (int) (10 *
                context.getResources().getDisplayMetrics().density));
        custom.recycle();
        mPaint.setColor(color);
        mSlop = slop;
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPress.isEmpty())
            return;
        mOval.set(mPress);
        canvas.drawOval(mOval, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        final int x = Math.round(event.getX());
        final int y = Math.round(event.getY());
        mPress.set(x - mSlop, y - mSlop, x + mSlop, y + mSlop);
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_UP && mListener != null) {
            mListener.onPressed(this, mPress.left, mPress.top, mPress.right, mPress.bottom);
        }
        return true;
    }

    public void setOnPressListener(OnPressListener listener) {
        mListener = listener;
    }

    public void clear() {
        mPress.setEmpty();
        invalidate();
    }

    public interface OnPressListener {
        void onPressed(View view, int left, int top, int right, int bottom);
    }
}
