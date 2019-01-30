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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * 触摸绘图
 */
public class PressView extends View {

    private OnPressListener mListener;
    private final RectF mPress = new RectF();
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ArrayList<PointF> mPoints = new ArrayList<>();
    private final Path mPath = new Path();
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
        final int color = 0x80fe439b;
        final int slop = (int) (10 *
                context.getResources().getDisplayMetrics().density);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(slop);
        mSlop = slop;
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final ArrayList<PointF> points = mPoints;
        final int count = points.size();
        if (count <= 0)
            return;
        if (count == 1) {
            final PointF p = points.get(0);
            canvas.drawPoint(p.x, p.y, mPaint);
            return;
        }
        canvas.drawPath(mPath, mPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            mPress.setEmpty();
            mPoints.clear();
            mPress.set(x, y, x, y);
            mPoints.add(new PointF(x, y));
            mPress.union(x, y);
            updatePath();
            invalidate();
        } else if (action == MotionEvent.ACTION_UP) {
            if (mListener != null) {
                final int left = Math.round(mPress.left - mSlop);
                final int top = Math.round(mPress.top - mSlop);
                final int right = Math.round(mPress.right + mSlop);
                final int bottom = Math.round(mPress.bottom + mSlop);
                mListener.onPressed(this, left, top, right, bottom);
            }
        } else {
            mPoints.add(new PointF(x, y));
            mPress.union(x, y);
            updatePath();
            invalidate();
        }
        return true;
    }

    private void updatePath() {
        final Path path = mPath;
        path.rewind();
        final ArrayList<PointF> points = mPoints;
        final int count = points.size();
        if (count <= 1)
            return;
        float preX = 0;
        float preY = 0;
        for (int i = 0; i < count; i++) {
            final float x = points.get(i).x;
            final float y = points.get(i).y;
            if (i == 0) {
                path.moveTo(x, y);
                preX = x;
                preY = y;
            } else {
                path.quadTo(preX, preY, (x + preX) / 2, (y + preY) / 2);
                preX = x;
                preY = y;
            }
        }
    }

    public void setOnPressListener(OnPressListener listener) {
        mListener = listener;
    }

    public interface OnPressListener {
        void onPressed(View view, int left, int top, int right, int bottom);
    }
}