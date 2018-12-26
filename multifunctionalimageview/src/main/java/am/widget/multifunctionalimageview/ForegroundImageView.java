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

package am.widget.multifunctionalimageview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;

/**
 * 前景图带状态的ImageView
 * Created by Alex on 2018/12/26.
 */
public class ForegroundImageView extends ClipImageView {

    private ForegroundInfo mForegroundInfo;

    public ForegroundImageView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public ForegroundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public ForegroundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ForegroundImageView(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.ForegroundImageView, defStyleAttr, defStyleRes);
        final Drawable foreground = custom.getDrawable(
                R.styleable.ForegroundImageView_fivForeground);
        custom.recycle();
        if (isForegroundEnable())
            setForeground(foreground);
        else {
            if (mForegroundInfo == null)
                mForegroundInfo = new ForegroundInfo();
            mForegroundInfo.mDrawable = foreground;
            if (foreground != null)
                foreground.setCallback(this);
        }
    }

    private boolean isForegroundEnable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isForegroundEnable()) {
            super.onDraw(canvas);
            return;
        }
        super.onDraw(canvas);
        onDrawForeground(canvas);
    }

    public void onDrawForeground(Canvas canvas) {
        if (isForegroundEnable()) {
            super.onDrawForeground(canvas);
            return;
        }
        final Drawable foreground = getForeground();
        if (foreground != null) {
            final Rect selfBounds = mForegroundInfo.mSelfBounds;
            final Rect overlayBounds = mForegroundInfo.mOverlayBounds;
            if (mForegroundInfo.mInsidePadding) {
                selfBounds.set(0, 0, getWidth(), getHeight());
            } else {
                selfBounds.set(getPaddingLeft(), getPaddingTop(),
                        getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            }
            Compat.apply(mForegroundInfo.mGravity, foreground.getIntrinsicWidth(),
                    foreground.getIntrinsicHeight(), selfBounds, overlayBounds,
                    Compat.getLayoutDirection(this));
            foreground.setBounds(overlayBounds);

            foreground.draw(canvas);
        }
    }

    @Override
    protected void drawableStateChanged() {
        if (isForegroundEnable()) {
            super.drawableStateChanged();
            return;
        }
        final Drawable drawable = getForeground();
        if (drawable != null && drawable.isStateful()
                && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    @Override
    protected boolean verifyDrawable(@SuppressWarnings("NullableProblems") Drawable who) {
        if (isForegroundEnable())
            return super.verifyDrawable(who);
        return getForeground() == who || super.verifyDrawable(who);
    }

    /**
     * Returns the drawable used as the foreground of this View. The
     * foreground drawable, if non-null, is always drawn on top of the view's content.
     *
     * @return a Drawable or null if no foreground was set
     */
    public Drawable getForeground() {
        if (isForegroundEnable())
            return super.getForeground();
        else
            return mForegroundInfo == null ? null : mForegroundInfo.mDrawable;
    }

    /**
     * Supply a Drawable that is to be rendered on top of all of the content in the view.
     *
     * @param foreground the Drawable to be drawn on top of the children
     */
    public void setForeground(Drawable foreground) {
        if (isForegroundEnable()) {
            super.setForeground(foreground);
            return;
        }
        if (mForegroundInfo == null) {
            if (foreground == null) {
                // Nothing to do.
                return;
            }
            mForegroundInfo = new ForegroundInfo();
        }
        if (foreground == mForegroundInfo.mDrawable) {
            // Nothing to do
            return;
        }
        if (mForegroundInfo.mDrawable != null) {
            mForegroundInfo.mDrawable.setCallback(null);
            unscheduleDrawable(mForegroundInfo.mDrawable);
        }
        mForegroundInfo.mDrawable = foreground;
        if (foreground != null) {
            if (foreground.isStateful()) {
                foreground.setState(getDrawableState());
            }
            // Set callback last, since the view may still be initializing.
            foreground.setCallback(this);
        }
        requestLayout();
        invalidate();
    }

    /**
     * Describes how the foreground is positioned.
     *
     * @return foreground gravity.
     * @see #setForegroundGravity(int)
     */
    public int getForegroundGravity() {
        if (isForegroundEnable())
            return super.getForegroundGravity();
        return mForegroundInfo != null ? mForegroundInfo.mGravity
                : Gravity.START | Gravity.TOP;
    }

    /**
     * Describes how the foreground is positioned. Defaults to START and TOP.
     *
     * @param gravity see {@link android.view.Gravity}
     * @see #getForegroundGravity()
     */
    public void setForegroundGravity(int gravity) {
        if (isForegroundEnable()) {
            super.setForegroundGravity(gravity);
            return;
        }
        if (mForegroundInfo == null)
            mForegroundInfo = new ForegroundInfo();
        if (mForegroundInfo.mGravity != gravity) {
            if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0)
                gravity |= Gravity.START;
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0)
                gravity |= Gravity.TOP;
            mForegroundInfo.mGravity = gravity;
            requestLayout();
        }
    }

    private static class ForegroundInfo {
        private Drawable mDrawable;
        private int mGravity = Gravity.FILL;
        private boolean mInsidePadding = true;
        private final Rect mSelfBounds = new Rect();
        private final Rect mOverlayBounds = new Rect();
    }
}
