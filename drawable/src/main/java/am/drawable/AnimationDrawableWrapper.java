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

package am.drawable;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import am.widget.R;

/**
 * 动画图片包装器
 * Created by Alex on 2019/1/11.
 */
@SuppressWarnings({"NullableProblems", "unused", "WeakerAccess"})
abstract class AnimationDrawableWrapper extends AnimationDrawable implements Drawable.Callback {

    private Drawable mDrawable;

    public AnimationDrawableWrapper(Drawable drawable) {
        mDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.AnimationDrawableWrapper);
        final Drawable drawable = custom.getDrawable(
                R.styleable.AnimationDrawableWrapper_android_drawable);
        custom.recycle();
        if (drawable != null) {
            mDrawable = drawable;
            drawable.setCallback(this);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected boolean setWrappedDrawableFormText(Resources resources, XmlPullParser parser,
                                                 AttributeSet attrs, Resources.Theme theme)
            throws XmlPullParserException, IOException {
        int type;
        //noinspection StatementWithEmptyBody
        while ((type = parser.next()) == XmlPullParser.TEXT) {
        }
        if (type != XmlPullParser.START_TAG)
            return false;
        mDrawable = Drawable.createFromXmlInner(resources, parser, attrs, theme);
        mDrawable.setCallback(this);
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mDrawable == null)
            return;
        mDrawable.draw(canvas);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (mDrawable == null)
            return;
        mDrawable.setBounds(bounds);
    }

    @Override
    public void setChangingConfigurations(int configs) {
        if (mDrawable == null)
            return;
        mDrawable.setChangingConfigurations(configs);
    }

    @Override
    public int getChangingConfigurations() {
        if (mDrawable == null)
            return super.getChangingConfigurations();
        return mDrawable.getChangingConfigurations();
    }

    @Override
    public void setDither(boolean dither) {
        if (mDrawable == null)
            return;
        mDrawable.setDither(dither);
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        if (mDrawable == null)
            return;
        mDrawable.setFilterBitmap(filter);
    }

    @Override
    public void setAlpha(int alpha) {
        if (mDrawable == null)
            return;
        mDrawable.setAlpha(alpha);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public int getAlpha() {
        if (mDrawable == null)
            return super.getAlpha();
        return mDrawable.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mDrawable == null)
            return;
        mDrawable.setColorFilter(cf);
    }

    @Override
    public boolean isStateful() {
        if (mDrawable == null)
            return false;
        return mDrawable.isStateful();
    }

    @Override
    public boolean setState(final int[] stateSet) {
        if (mDrawable == null)
            return false;
        return mDrawable.setState(stateSet);
    }

    @Override
    public int[] getState() {
        if (mDrawable == null)
            return super.getState();
        return mDrawable.getState();
    }

    @Override
    public void jumpToCurrentState() {
        if (mDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mDrawable.jumpToCurrentState();
    }

    @Override
    public Drawable getCurrent() {
        if (mDrawable == null)
            return super.getCurrent();
        return mDrawable.getCurrent();
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if (mDrawable == null)
            return super.setVisible(visible, restart);
        return super.setVisible(visible, restart) || mDrawable.setVisible(visible, restart);
    }

    @Override
    public int getOpacity() {
        if (mDrawable == null)
            return PixelFormat.UNKNOWN;
        return mDrawable.getOpacity();
    }

    @Override
    public Region getTransparentRegion() {
        if (mDrawable == null)
            return null;
        return mDrawable.getTransparentRegion();
    }

    @Override
    public int getIntrinsicWidth() {
        if (mDrawable == null)
            return -1;
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        if (mDrawable == null)
            return -1;
        return mDrawable.getIntrinsicHeight();
    }

    @Override
    public int getMinimumWidth() {
        if (mDrawable == null)
            return super.getMinimumWidth();
        return mDrawable.getMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        if (mDrawable == null)
            return super.getMinimumHeight();
        return mDrawable.getMinimumHeight();
    }

    @Override
    public boolean getPadding(Rect padding) {
        if (mDrawable == null)
            return super.getPadding(padding);
        return mDrawable.getPadding(padding);
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }

    @Override
    protected boolean onLevelChange(int level) {
        if (mDrawable == null)
            return super.onLevelChange(level);
        return mDrawable.setLevel(level);
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        if (mDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            mDrawable.setAutoMirrored(mirrored);
    }

    @Override
    public boolean isAutoMirrored() {
        if (mDrawable == null)
            return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            return mDrawable.isAutoMirrored();
        return false;
    }

    @Override
    public void setTint(int tint) {
        if (mDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mDrawable.setTint(tint);
    }

    @Override
    public void setTintList(ColorStateList tint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mDrawable.setTintList(tint);
    }

    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        if (mDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mDrawable.setTintMode(tintMode);
    }

    @Override
    public void setHotspot(float x, float y) {
        if (mDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mDrawable.setHotspot(x, y);
    }

    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        if (mDrawable == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mDrawable.setHotspotBounds(left, top, right, bottom);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        if (mDrawable == null)
            return;
        mDrawable.getOutline(outline);
    }

    /**
     * 获取包装的Drawable
     *
     * @return 包装的Drawable
     */
    public Drawable getWrappedDrawable() {
        return mDrawable;
    }

    /**
     * 设置Drawable
     *
     * @param drawable 待包装的Drawable
     */
    public void setWrappedDrawable(Drawable drawable) {
        if (mDrawable != null) {
            mDrawable.setCallback(null);
        }
        mDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
    }
}
