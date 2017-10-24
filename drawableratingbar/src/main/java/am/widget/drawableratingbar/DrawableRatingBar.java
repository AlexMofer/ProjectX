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

package am.widget.drawableratingbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * 图片评级
 * Created by Alex on 2015/8/31.
 */
public class DrawableRatingBar extends View {
    private static final int[] ATTRS = new int[]{android.R.attr.progressDrawable,
            android.R.attr.drawablePadding};
    private static final int DEFAULT_MAX = 5;
    private Drawable mProgressDrawable;
    private Drawable mSecondaryProgress;
    private int mDrawablePadding;
    private int mGravity;
    private int mMax;
    private int mMin;
    private int mRating;
    private int drawableWidth;
    private int drawableHeight;
    private float xOffset;
    private float yOffset;
    private boolean tReGetOffset = true;
    private boolean mManually;
    private boolean mOnlyItemTouchable;
    private OnRatingChangeListener listener;

    public DrawableRatingBar(Context context) {
        this(context, null);
    }

    public DrawableRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS, defStyleAttr, 0);
        int n = a.getIndexCount();
        Drawable drawable = null;
        int drawablePadding = 0;

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 0:
                    drawable = a.getDrawable(attr);
                    break;
                case 1:
                    drawablePadding = a.getDimensionPixelSize(attr, drawablePadding);
                    break;
            }
        }
        a.recycle();
        Drawable selected = null;
        Drawable normal = null;
        if (drawable != null && drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            selected = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            normal = layerDrawable.findDrawableByLayerId(android.R.id.secondaryProgress);
        }
        int max = DEFAULT_MAX;
        int min = 0;
        int rating = 0;
        boolean manually;
        boolean onlyItemTouchable;
        int gravity = Compat.START;
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.DrawableRatingBar);
        if (custom.hasValue(R.styleable.DrawableRatingBar_drbProgressDrawable))
            selected = custom.getDrawable(R.styleable.DrawableRatingBar_drbProgressDrawable);
        if (custom.hasValue(R.styleable.DrawableRatingBar_drbSecondaryProgress))
            normal = custom.getDrawable(R.styleable.DrawableRatingBar_drbSecondaryProgress);
        drawablePadding = custom.getDimensionPixelSize(
                R.styleable.DrawableRatingBar_drbDrawablePadding, drawablePadding);
        max = custom.getInteger(R.styleable.DrawableRatingBar_drbMax, max);
        min = custom.getInteger(R.styleable.DrawableRatingBar_drbMin, min);
        rating = custom.getInteger(R.styleable.DrawableRatingBar_drbRating, rating);
        manually = custom.getBoolean(R.styleable.DrawableRatingBar_drbManually, false);
        onlyItemTouchable = custom.getBoolean(
                R.styleable.DrawableRatingBar_drbOnlyItemTouchable, false);
        gravity = a.getInteger(R.styleable.DrawableRatingBar_drbGravity, gravity);
        custom.recycle();
        setRatingDrawable(selected, normal);
        setDrawablePadding(drawablePadding);
        setGravity(gravity);
        setMax(max <= 0 ? DEFAULT_MAX : max);
        setMin(min < 0 ? 0 : min);
        setRating(rating);
        setManually(manually);
        setOnlyItemTouchable(onlyItemTouchable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int suggestedMinimumWidth = getSuggestedMinimumWidth();
        final int suggestedMinimumHeight = getSuggestedMinimumHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int progressDrawableWidth = mProgressDrawable == null ?
                0 : mProgressDrawable.getIntrinsicWidth();
        final int progressDrawableHeight = mProgressDrawable == null ?
                0 : mProgressDrawable.getIntrinsicHeight();
        final int secondaryProgressWidth = mSecondaryProgress == null ?
                0 : mSecondaryProgress.getIntrinsicWidth();
        final int secondaryProgressHeight = mSecondaryProgress == null ?
                0 : mSecondaryProgress.getIntrinsicHeight();
        drawableWidth = Math.max(progressDrawableWidth, secondaryProgressWidth);
        drawableHeight = Math.max(progressDrawableHeight, secondaryProgressHeight);
        final int itemWidth = drawableWidth * mMax + mDrawablePadding * (mMax - 1);
        final int width = Math.max(itemWidth + paddingStart + paddingEnd, suggestedMinimumWidth);
        final int height = Math.max(drawableHeight + paddingTop + paddingBottom,
                suggestedMinimumHeight);
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
        tReGetOffset = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgressDrawable != null && mSecondaryProgress != null) {
            getOffset();
            final int progressDrawableWidth = mProgressDrawable.getIntrinsicWidth();
            final int progressDrawableHeight = mProgressDrawable.getIntrinsicHeight();
            final int secondaryProgressWidth = mSecondaryProgress.getIntrinsicWidth();
            final int secondaryProgressHeight = mSecondaryProgress.getIntrinsicHeight();
            mProgressDrawable.setBounds(0, 0, progressDrawableWidth, progressDrawableHeight);
            mSecondaryProgress.setBounds(0, 0, secondaryProgressWidth, secondaryProgressHeight);
            canvas.save();
            canvas.translate(xOffset, yOffset);
            for (int i = 0; i < mRating; i++) {
                canvas.save();
                canvas.translate(drawableWidth * 0.5f, drawableHeight * 0.5f);
                canvas.translate(-progressDrawableWidth * 0.5f, -progressDrawableHeight * 0.5f);
                mProgressDrawable.draw(canvas);
                canvas.restore();
                canvas.translate(drawableWidth + mDrawablePadding, 0);
            }
            for (int i = mRating; i < mMax; i++) {
                canvas.save();
                canvas.translate(drawableWidth * 0.5f, drawableHeight * 0.5f);
                canvas.translate(-secondaryProgressWidth * 0.5f, -secondaryProgressHeight * 0.5f);
                mSecondaryProgress.draw(canvas);
                canvas.restore();
                canvas.translate(drawableWidth + mDrawablePadding, 0);
            }
            canvas.restore();
        }
    }

    @SuppressWarnings("all")
    private void getOffset() {
        if (!tReGetOffset)
            return;
        tReGetOffset = false;
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();
        final int paddingStart = Compat.getPaddingStart(this);
        final int paddingTop = getPaddingTop();
        final int paddingEnd = Compat.getPaddingEnd(this);
        final int paddingBottom = getPaddingBottom();
        final int itemWidth = drawableWidth * mMax + mDrawablePadding * (mMax - 1);
        switch (mGravity) {
            default:
            case Compat.START:
            case Gravity.LEFT:
            case Gravity.TOP:
            case Compat.START | Gravity.TOP:
            case Gravity.LEFT | Gravity.TOP:
                xOffset = paddingStart;
                yOffset = paddingTop;
                break;
            case Gravity.CENTER_HORIZONTAL:
            case Gravity.CENTER_HORIZONTAL | Gravity.TOP:
                xOffset = measuredWidth * 0.5f - itemWidth * 0.5f;
                yOffset = paddingTop;
                break;
            case Compat.END:
            case Gravity.RIGHT:
            case Compat.END | Gravity.TOP:
            case Gravity.RIGHT | Gravity.TOP:
                xOffset = measuredWidth - paddingEnd - itemWidth;
                yOffset = paddingTop;
                break;
            case Gravity.CENTER_VERTICAL:
            case Gravity.CENTER_VERTICAL | Compat.START:
            case Gravity.CENTER_VERTICAL | Gravity.LEFT:
                xOffset = paddingStart;
                yOffset = measuredHeight * 0.5f - drawableHeight * 0.5f;
                break;
            case Gravity.CENTER:
                xOffset = measuredWidth * 0.5f - itemWidth * 0.5f;
                yOffset = measuredHeight * 0.5f - drawableHeight * 0.5f;
                break;
            case Gravity.CENTER_VERTICAL | Compat.END:
            case Gravity.CENTER_VERTICAL | Gravity.RIGHT:
                xOffset = measuredWidth  - paddingEnd - itemWidth;
                yOffset = measuredHeight * 0.5f - drawableHeight * 0.5f;
                break;
            case Gravity.BOTTOM:
            case Gravity.BOTTOM | Compat.START:
            case Gravity.BOTTOM | Gravity.LEFT:
                xOffset = paddingStart;
                yOffset = measuredHeight - paddingBottom - drawableHeight;
                break;
            case Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL:
                xOffset = measuredWidth * 0.5f - itemWidth * 0.5f;
                yOffset = measuredHeight - paddingBottom - drawableHeight;
                break;
            case Gravity.BOTTOM | Compat.END:
            case Gravity.BOTTOM | Gravity.RIGHT:
                xOffset = measuredWidth  - paddingEnd - itemWidth;
                yOffset = measuredHeight - paddingBottom - drawableHeight;
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean superResult = super.onTouchEvent(event);
        boolean touch = false;
        if (mManually) {
            final int action = event.getAction();
            final float x = event.getX();
            final float y = event.getY();
            if (mOnlyItemTouchable && (y < yOffset || y > yOffset + drawableHeight)) {
                return superResult;
            }
            final int oldRating = mRating;
            final int rating = getRatingByMotionEvent(x);
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                touch = true;
                mRating = rating;
                invalidate();
                if (oldRating != rating && listener != null)
                    listener.onRatingChanged(rating, oldRating);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                touch = true;
                mRating = rating;
                invalidate();
                if (listener != null) {
                    if (oldRating != rating)
                        listener.onRatingChanged(rating, oldRating);
                    listener.onRatingSelected(rating);
                }
            }
        }
        return touch || superResult;
    }

    private int getRatingByMotionEvent(float x) {
        int rating = mMax;
        for (int i = 0; i <= mMax; i++) {
            if (x <= xOffset + drawableWidth * i + mDrawablePadding * (i > 0 ? i - 1 : 0)) {
                rating = i;
                break;
            }
        }
        return rating < mMin ? mMin : rating;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mDrawablePadding = mDrawablePadding;
        ss.mGravity = mGravity;
        ss.mMax = mMax;
        ss.mMin = mMin;
        ss.mRating = mRating;
        ss.mManually = mManually;
        ss.mOnlyItemTouchable = mOnlyItemTouchable;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        mDrawablePadding = ss.mDrawablePadding;
        mGravity = ss.mGravity;
        mMax = ss.mMax;
        mMin = ss.mMin;
        mRating = ss.mRating;
        mManually = ss.mManually;
        mOnlyItemTouchable = ss.mOnlyItemTouchable;
        super.onRestoreInstanceState(ss.getSuperState());
        requestLayout();
        invalidate();
    }

    /**
     * 设置评级图片
     *
     * @param selected 选中状态
     * @param normal   普通状态
     */
    public void setRatingDrawable(Drawable selected, Drawable normal) {
        boolean invalidate = false;
        if (selected != mProgressDrawable) {
            mProgressDrawable = selected;
            invalidate = true;
        }
        if (normal != mSecondaryProgress) {
            mSecondaryProgress = normal;
            invalidate = true;
        }
        if (invalidate) {
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取图片间距
     *
     * @return 图片间距
     */
    @SuppressWarnings("unused")
    public int getDrawablePadding() {
        return mDrawablePadding;
    }

    /**
     * 设置图片间距
     *
     * @param padding 图片间距
     */
    public void setDrawablePadding(int padding) {
        if (padding != mDrawablePadding) {
            mDrawablePadding = padding;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取对齐方式
     *
     * @return 对齐方式
     */
    @SuppressWarnings("unused")
    public int getGravity() {
        return mGravity;
    }

    /**
     * 设置对齐方式
     *
     * @param gravity 对齐方式
     */
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            tReGetOffset = true;
            invalidate();
        }
    }

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    @SuppressWarnings("unused")
    public int getMax() {
        return mMax;
    }

    /**
     * 设置最大值
     *
     * @param max 最大值
     */
    public void setMax(int max) {
        if (max > 0 && mMax != max) {
            mMax = max;
            mRating = mRating > mMax ? mMax : mRating;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 获取最小值
     *
     * @return 最小值
     */
    @SuppressWarnings("unused")
    public int getMin() {
        return mMin;
    }

    /**
     * 设置最小值
     *
     * @param min 最小值
     */
    public void setMin(int min) {
        if (min >= 0 && mMin != min) {
            mMin = min;
            mRating = mRating < mMin ? mMin : mRating;
            invalidate();
        }
    }

    /**
     * 获取评级
     *
     * @return 评级
     */
    @SuppressWarnings("unused")
    public int getRating() {
        return mRating;
    }

    /**
     * 设置评级
     *
     * @param rating 评级
     */
    public void setRating(int rating) {
        if (rating >= 0 && rating <= mMax && rating != mRating) {
            mRating = rating;
            invalidate();
        }
    }

    /**
     * 是否手动
     *
     * @return 手动
     */
    @SuppressWarnings("unused")
    public boolean isManually() {
        return mManually;
    }

    /**
     * 设置是否可手动
     *
     * @param manually 手动
     */
    public void setManually(boolean manually) {
        mManually = manually;
    }

    /**
     * 仅图片区域可触摸
     *
     * @return 图片区域可触摸
     */
    @SuppressWarnings("unused")
    public boolean isOnlyItemTouchable() {
        return mOnlyItemTouchable;
    }

    /**
     * 设置 图片区域可触摸
     *
     * @param touchable 图片区域可触摸
     */
    public void setOnlyItemTouchable(boolean touchable) {
        mOnlyItemTouchable = touchable;
    }

    /**
     * 设置变化监听
     *
     * @param listener 变化监听
     */
    @SuppressWarnings("unused")
    public void setOnRatingChangeListener(OnRatingChangeListener listener) {
        if (listener != null)
            setManually(true);
        this.listener = listener;
    }

    /**
     * 评级变化监听
     */
    public interface OnRatingChangeListener {

        /**
         * 评级变化
         *
         * @param rating    新评级
         * @param oldRating 旧评级
         */
        void onRatingChanged(int rating, int oldRating);

        /**
         * 评级选中
         *
         * @param rating 评级
         */
        void onRatingSelected(int rating);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
        private int mDrawablePadding;
        private int mGravity;
        private int mMax;
        private int mMin;
        private int mRating;
        private boolean mManually;
        private boolean mOnlyItemTouchable;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mDrawablePadding = in.readInt();
            mGravity = in.readInt();
            mMax = in.readInt();
            mMin = in.readInt();
            mRating = in.readInt();
            mManually = in.readInt() == 1;
            mOnlyItemTouchable = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mDrawablePadding);
            out.writeInt(mGravity);
            out.writeInt(mMax);
            out.writeInt(mMin);
            out.writeInt(mRating);
            out.writeInt(mManually ? 1 : 0);
            out.writeInt(mOnlyItemTouchable ? 1 : 0);
        }
    }
}
