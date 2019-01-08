/*
 * Copyright (C) 2019 AlexMofer
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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import am.widget.R;

/**
 * 帧Drawable
 * Created by Alex on 2019/1/7.
 */
@SuppressWarnings({"NullableProblems", "unused", "WeakerAccess"})
public class FrameDrawable extends Drawable implements Drawable.Callback {

    private final ArrayList<ChildDrawable> mItems = new ArrayList<>();
    private int mWidth;
    private int mHeight;
    private final Rect mPadding = new Rect();

    public FrameDrawable() {
    }

    public FrameDrawable(Drawable... frames) {
        for (Drawable frame : frames)
            mItems.add(new ChildDrawable(frame));
    }

    @Override
    public void inflate(Resources resources, XmlPullParser parser, AttributeSet attrs,
                        Resources.Theme theme)
            throws XmlPullParserException, IOException {
        super.inflate(resources, parser, attrs, theme);
        final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                R.styleable.FrameDrawable);
        final int width = custom.getLayoutDimension(
                R.styleable.FrameDrawable_android_layout_width, "layout_width");
        final int height = custom.getLayoutDimension(
                R.styleable.FrameDrawable_android_layout_height, "layout_height");
        mWidth = width < 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : width;
        mHeight = height < 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : height;
        if (custom.hasValue(R.styleable.FrameDrawable_android_padding)) {
            final int padding = custom.getDimensionPixelSize(
                    R.styleable.FrameDrawable_android_padding, 0);
            mPadding.set(padding, padding, padding, padding);
        }
        if (custom.hasValue(R.styleable.FrameDrawable_android_paddingStart))
            mPadding.left = custom.getDimensionPixelSize(
                    R.styleable.FrameDrawable_android_paddingStart, mPadding.left);
        if (custom.hasValue(R.styleable.FrameDrawable_android_paddingTop))
            mPadding.top = custom.getDimensionPixelSize(
                    R.styleable.FrameDrawable_android_paddingTop, mPadding.top);
        if (custom.hasValue(R.styleable.FrameDrawable_android_paddingEnd))
            mPadding.right = custom.getDimensionPixelSize(
                    R.styleable.FrameDrawable_android_paddingEnd, mPadding.right);
        if (custom.hasValue(R.styleable.FrameDrawable_android_paddingBottom))
            mPadding.bottom = custom.getDimensionPixelSize(
                    R.styleable.FrameDrawable_android_paddingBottom, mPadding.bottom);
        custom.recycle();
        inflateFrames(resources, parser, attrs, theme);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void inflateFrames(Resources resources, XmlPullParser parser, AttributeSet attrs,
                               Resources.Theme theme) throws XmlPullParserException, IOException {
        final int innerDepth = parser.getDepth() + 1;
        int type;
        int depth;
        while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                && ((depth = parser.getDepth()) >= innerDepth || type != XmlPullParser.END_TAG)) {
            if (type != XmlPullParser.START_TAG)
                continue;
            if (depth > innerDepth || !parser.getName().equals("item"))
                continue;
            final TypedArray custom = DrawableHelper.obtainAttributes(resources, theme, attrs,
                    R.styleable.FrameDrawableItem);
            final int width = custom.getLayoutDimension(
                    R.styleable.FrameDrawableItem_android_layout_width, "layout_width");
            final int height = custom.getLayoutDimension(
                    R.styleable.FrameDrawableItem_android_layout_height, "layout_height");
            int marginStart = 0;
            int marginTop = 0;
            int marginEnd = 0;
            int marginBottom = 0;
            if (custom.hasValue(R.styleable.FrameDrawableItem_android_layout_margin)) {
                final int margin = custom.getDimensionPixelSize(
                        R.styleable.FrameDrawableItem_android_layout_margin, 0);
                marginStart = marginTop = marginEnd = marginBottom = margin;
            }
            if (custom.hasValue(R.styleable.FrameDrawableItem_android_layout_marginStart))
                marginStart = custom.getDimensionPixelSize(
                        R.styleable.FrameDrawableItem_android_layout_marginStart, marginStart);
            if (custom.hasValue(R.styleable.FrameDrawableItem_android_layout_marginTop))
                marginTop = custom.getDimensionPixelSize(
                        R.styleable.FrameDrawableItem_android_layout_marginTop, marginTop);
            if (custom.hasValue(R.styleable.FrameDrawableItem_android_layout_marginEnd))
                marginEnd = custom.getDimensionPixelSize(
                        R.styleable.FrameDrawableItem_android_layout_marginEnd, marginEnd);
            if (custom.hasValue(R.styleable.FrameDrawableItem_android_layout_marginBottom))
                marginBottom = custom.getDimensionPixelSize(
                        R.styleable.FrameDrawableItem_android_layout_marginBottom, marginBottom);
            final int gravity = custom.getInteger(R.styleable.FrameDrawableItem_android_gravity,
                    Gravity.NO_GRAVITY);
            final int id = custom.getResourceId(R.styleable.FrameDrawableItem_android_id,
                    View.NO_ID);
            final Drawable dr = custom.getDrawable(R.styleable.FrameDrawableItem_android_drawable);
            if (dr != null) {
                dr.setCallback(this);
                mItems.add(new ChildDrawable(dr, width, height, gravity, id,
                        marginStart, marginTop, marginEnd, marginBottom));
            }
            custom.recycle();
            if (dr == null) {
                //noinspection StatementWithEmptyBody
                while ((type = parser.next()) == XmlPullParser.TEXT) {
                }
                if (type != XmlPullParser.START_TAG)
                    throw new XmlPullParserException(parser.getPositionDescription()
                            + ": <item> tag requires a 'drawable' attribute or "
                            + "child tag defining a drawable");
                final Drawable item = Drawable.createFromXmlInner(resources, parser, attrs, theme);
                item.setCallback(this);
                mItems.add(new ChildDrawable(item, width, height, gravity, id,
                        marginStart, marginTop, marginEnd, marginBottom));
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (mItems.isEmpty())
            return;
        for (ChildDrawable child : mItems) {
            child.getDrawable().draw(canvas);
        }
    }

    @Override
    public boolean getPadding(Rect padding) {
        if (mItems.isEmpty())
            return super.getPadding(padding);
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        for (ChildDrawable child : mItems) {
            padding.setEmpty();
            child.getPadding(padding);
            left = Math.max(left, padding.left);
            top = Math.max(top, padding.top);
            right = Math.max(right, padding.right);
            bottom = Math.max(bottom, padding.bottom);
        }
        padding.set(left, top, right, bottom);
        return left != 0 || top != 0 || right != 0 || bottom != 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void getOutline(Outline outline) {
        if (mItems.isEmpty()) {
            super.getOutline(outline);
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().getOutline(outline);
            if (!outline.isEmpty())
                return;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setHotspot(float x, float y) {
        if (mItems.isEmpty()) {
            super.setHotspot(x, y);
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().setHotspot(x, y);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setHotspotBounds(int left, int top, int right, int bottom) {
        if (mItems.isEmpty()) {
            super.setHotspotBounds(left, top, right, bottom);
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().setHotspotBounds(left, top, right, bottom);
        }
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        if (mItems.isEmpty())
            return super.setVisible(visible, restart);
        boolean result = false;
        for (ChildDrawable child : mItems) {
            if (child.getDrawable().setVisible(visible, restart))
                result = true;
        }
        return result;
    }

    @Override
    public void setAlpha(int alpha) {
        if (mItems.isEmpty())
            return;
        for (ChildDrawable child : mItems) {
            child.getDrawable().setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        if (mItems.isEmpty())
            return;
        for (ChildDrawable child : mItems) {
            child.getDrawable().setColorFilter(colorFilter);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTintList(ColorStateList tint) {
        if (mItems.isEmpty()) {
            super.setTintList(tint);
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().setTintList(tint);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setTintMode(PorterDuff.Mode tintMode) {
        if (mItems.isEmpty()) {
            super.setTintMode(tintMode);
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().setTintMode(tintMode);
        }
    }

    @Override
    public int getOpacity() {
        if (mItems.isEmpty())
            return PixelFormat.TRANSPARENT;
        int op = mItems.get(0).getDrawable().getOpacity();
        final int count = mItems.size();
        for (int i = 1; i < count; i++) {
            op = Drawable.resolveOpacity(op, mItems.get(i).getDrawable().getOpacity());
        }
        return op;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setAutoMirrored(boolean mirrored) {
        if (mItems.isEmpty()) {
            super.setAutoMirrored(mirrored);
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().setAutoMirrored(mirrored);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void jumpToCurrentState() {
        if (mItems.isEmpty()) {
            super.jumpToCurrentState();
            return;
        }
        for (ChildDrawable child : mItems) {
            child.getDrawable().jumpToCurrentState();
        }
    }

    @Override
    public boolean isStateful() {
        if (mItems.isEmpty())
            return super.isStateful();
        boolean result = false;
        for (ChildDrawable child : mItems) {
            if (child.getDrawable().isStateful())
                result = true;
        }
        return result;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        if (mItems.isEmpty())
            return super.onStateChange(state);
        boolean result = false;
        for (ChildDrawable child : mItems) {
            if (child.getDrawable().setState(state))
                result = true;
        }
        return result;
    }

    @Override
    protected boolean onLevelChange(int level) {
        if (mItems.isEmpty())
            return super.onLevelChange(level);
        boolean result = false;
        for (ChildDrawable child : mItems) {
            if (child.getDrawable().setLevel(level))
                result = true;
        }
        return result;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        if (mItems.isEmpty()) {
            super.onBoundsChange(bounds);
            return;
        }
        refreshChildBounds();
    }

    @Override
    public int getIntrinsicWidth() {
        if (mItems.isEmpty())
            return super.getIntrinsicWidth();
        if (mWidth >= 0)
            return mWidth;
        int width = -1;
        for (ChildDrawable child : mItems) {
            width = Math.max(width, child.getIntrinsicWidth());
        }
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        if (mItems.isEmpty())
            return super.getIntrinsicHeight();
        if (mHeight >= 0)
            return mHeight;
        int height = -1;
        for (ChildDrawable child : mItems) {
            height = Math.max(height, child.getIntrinsicHeight());
        }
        return height;
    }

    @Override
    public int getMinimumWidth() {
        if (mItems.isEmpty())
            return super.getMinimumWidth();
        if (mWidth >= 0)
            return mWidth;
        int width = -1;
        for (ChildDrawable child : mItems) {
            width = Math.max(width, child.getMinimumWidth());
        }
        return width;
    }

    @Override
    public int getMinimumHeight() {
        if (mItems.isEmpty())
            return super.getMinimumHeight();
        if (mHeight >= 0)
            return mHeight;
        int height = -1;
        for (ChildDrawable child : mItems) {
            height = Math.max(height, child.getMinimumHeight());
        }
        return height;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onLayoutDirectionChanged(int layoutDirection) {
        if (mItems.isEmpty())
            return super.onLayoutDirectionChanged(layoutDirection);
        boolean changed = false;
        for (ChildDrawable child : mItems) {
            changed |= child.getDrawable().setLayoutDirection(layoutDirection);
        }
        refreshChildBounds();
        return changed;
    }

    private void refreshChildBounds() {
        final Rect bounds = getBounds();
        for (ChildDrawable child : mItems) {
            child.setBounds(bounds, mPadding);
        }
    }

    // Callback
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

    /**
     * 添加帧
     *
     * @param drawable     图片
     * @param width        宽
     * @param height       高
     * @param gravity      引力
     * @param id           ID
     * @param marginStart  起始边距
     * @param marginTop    顶部边距
     * @param marginEnd    结束边距
     * @param marginBottom 底部边距
     */
    public void addFrame(Drawable drawable, int width, int height, int gravity, int id,
                         int marginStart, int marginTop, int marginEnd, int marginBottom) {
        if (drawable == null)
            return;
        drawable.setCallback(this);
        mItems.add(new ChildDrawable(drawable, width, height, gravity, id,
                marginStart, marginTop, marginEnd, marginBottom));
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 添加帧
     *
     * @param drawable 图片
     * @param width    宽
     * @param height   高
     * @param gravity  引力
     * @param id       ID
     */
    public void addFrame(Drawable drawable, int width, int height, int gravity, int id) {
        addFrame(drawable, width, height, gravity, id, 0, 0, 0,
                0);
    }

    /**
     * 添加帧
     *
     * @param drawable 图片
     * @param width    宽
     * @param height   高
     * @param gravity  引力
     */
    public void addFrame(Drawable drawable, int width, int height, int gravity) {
        addFrame(drawable, width, height, gravity, View.NO_ID);
    }

    /**
     * 添加帧
     *
     * @param drawable 图片
     * @param width    宽
     * @param height   高
     */
    public void addFrame(Drawable drawable, int width, int height) {
        addFrame(drawable, width, height, Gravity.NO_GRAVITY);
    }

    /**
     * 添加帧
     *
     * @param drawable 图片
     */
    public void addFrame(Drawable drawable) {
        addFrame(drawable, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }


    /**
     * 通过帧ID移除帧
     *
     * @param id 帧ID
     * @return 是否成功移除
     */
    public boolean removeFrameByFrameId(int id) {
        if (mItems.isEmpty())
            return false;
        for (ChildDrawable child : mItems) {
            if (child.getId() == id) {
                child.getDrawable().setCallback(null);
                mItems.remove(child);
                refreshChildBounds();
                invalidateSelf();
                return true;
            }
        }
        return false;
    }

    /**
     * 移除帧
     *
     * @param index 下标
     * @return 是否成功移除
     */
    public boolean removeFrame(int index) {
        if (mItems.isEmpty())
            return false;
        if (index >= mItems.size())
            return false;
        final ChildDrawable child = mItems.remove(index);
        child.getDrawable().setCallback(null);
        refreshChildBounds();
        invalidateSelf();
        return true;
    }


    /**
     * 通过帧ID查找Drawable
     *
     * @param id 帧ID
     * @return Drawable
     */
    public Drawable findDrawableByFrameId(int id) {
        if (mItems.isEmpty())
            return null;
        for (ChildDrawable child : mItems) {
            if (child.getId() == id)
                return child.getDrawable();
        }
        return null;
    }

    /**
     * 获取帧图片
     *
     * @param index 下标
     * @return 图片
     */
    public Drawable getDrawable(int index) {
        return mItems.get(index).getDrawable();
    }

    /**
     * 设置帧ID
     *
     * @param index 下标
     * @param id    ID
     */
    public void setId(int index, int id) {
        if (index >= mItems.size())
            return;
        mItems.get(index).setId(id);
    }

    /**
     * 获取帧ID
     *
     * @param index 下标
     * @return 帧ID
     */
    public int getId(int index) {
        return mItems.get(index).getId();
    }

    /**
     * 获取帧数目
     *
     * @return 帧数目
     */
    public int getNumberOfFrames() {
        return mItems.size();
    }

    /**
     * 通过帧ID设置图片
     *
     * @param id       ID
     * @param drawable 图片
     * @return 是否设置成功
     */
    public boolean setDrawableByFrameId(int id, Drawable drawable) {
        if (drawable == null)
            return false;
        for (ChildDrawable child : mItems) {
            if (child.getId() == id) {
                drawable.setCallback(this);
                child.setDrawable(drawable);
                refreshChildBounds();
                invalidateSelf();
                return true;
            }
        }
        return false;
    }

    /**
     * 设置帧图片
     *
     * @param index    下标
     * @param drawable 图片
     */
    public void setDrawable(int index, Drawable drawable) {
        drawable.setCallback(this);
        mItems.get(index).setDrawable(drawable);
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 通过帧ID获取下标
     *
     * @param id ID
     * @return 下标
     */
    public int findIndexByFrameId(int id) {
        int index = 0;
        for (ChildDrawable child : mItems) {
            if (child.getId() == id)
                return index;
            index++;
        }
        return -1;
    }

    /**
     * 设置帧尺寸
     *
     * @param index 下标
     * @param w     宽
     * @param h     高
     */
    public void setFrameSize(int index, int w, int h) {
        final ChildDrawable child = mItems.get(index);
        if (child.getWidth() == w && child.getHeight() == h)
            return;
        child.setSize(w, h);
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 设置帧宽度
     *
     * @param index 下标
     * @param w     宽度
     */
    public void setFrameWidth(int index, int w) {
        final ChildDrawable child = mItems.get(index);
        if (child.getWidth() == w)
            return;
        child.setSize(w, child.getHeight());
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 获取帧宽度
     *
     * @param index 下标
     * @return 宽度
     */
    public int getFrameWidth(int index) {
        return mItems.get(index).getWidth();
    }

    /**
     * 设置帧高度
     *
     * @param index 下标
     * @param h     高度
     */
    public void setFrameHeight(int index, int h) {
        final ChildDrawable child = mItems.get(index);
        if (child.getHeight() == h)
            return;
        child.setSize(child.getWidth(), h);
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 获取帧高度
     *
     * @param index 下标
     * @return 高度
     */
    public int getFrameHeight(int index) {
        return mItems.get(index).getHeight();
    }

    /**
     * 设置帧引力
     *
     * @param index   下标
     * @param gravity 引力
     */
    public void setFrameGravity(int index, int gravity) {
        final ChildDrawable child = mItems.get(index);
        if (child.getGravity() == gravity)
            return;
        child.setGravity(gravity);
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 获取帧引力
     *
     * @param index 下标
     * @return 引力
     */
    public int getFrameGravity(int index) {
        return mItems.get(index).getGravity();
    }

    /**
     * 设置在帧边距
     *
     * @param index  下标
     * @param start  起始位
     * @param top    顶部
     * @param end    结束位
     * @param bottom 底部
     */
    public void setFrameMarginRelative(int index, int start, int top, int end, int bottom) {
        final ChildDrawable child = mItems.get(index);
        if (!child.setMargin(start, top, end, bottom))
            return;
        refreshChildBounds();
        invalidateSelf();
    }

    /**
     * 获取帧边距
     *
     * @param index  下标
     * @param margin 边距
     */
    public void getFrameMarginRelative(int index, Rect margin) {
        mItems.get(index).getMargin(margin);
    }

    static class ChildDrawable {

        private Drawable mDrawable;
        private int mWidth;
        private int mHeight;
        private int mGravity;
        private int mId;
        private final Rect mMargin = new Rect();
        private final Rect mBounds = new Rect();
        private final Rect tRect = new Rect();

        ChildDrawable(Drawable drawable) {
            this(drawable, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.NO_GRAVITY, View.NO_ID, 0, 0, 0, 0);
        }

        ChildDrawable(Drawable drawable, int width, int height, int gravity, int id,
                      int start, int top, int end, int bottom) {
            mDrawable = drawable;
            mWidth = width;
            mHeight = height;
            mGravity = gravity;
            mId = id;
            mMargin.set(start, top, end, bottom);
        }

        Drawable getDrawable() {
            return mDrawable;
        }

        void setDrawable(Drawable drawable) {
            if (mDrawable != null)
                mDrawable.setCallback(null);
            mDrawable = drawable;
        }

        int getWidth() {
            return mWidth;
        }

        int getHeight() {
            return mHeight;
        }

        void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        int getGravity() {
            return mGravity;
        }

        void setGravity(int gravity) {
            mGravity = gravity;
        }

        int getId() {
            return mId;
        }

        void setId(int id) {
            mId = id;
        }

        boolean setMargin(int start, int top, int end, int bottom) {
            if (mMargin.left == start && mMargin.top == top && mMargin.right == end
                    && mMargin.bottom == bottom)
                return false;
            mMargin.set(start, top, end, bottom);
            return true;
        }

        void getMargin(Rect margin) {
            margin.set(mMargin);
        }

        void setBounds(Rect bounds, Rect padding) {
            final Rect container = tRect;
            container.set(bounds);
            container.set(container.left + padding.left, container.top + padding.top,
                    container.right - padding.right,
                    container.bottom - padding.bottom);
            if (Compat.isLayoutDirectionLTR(mDrawable))
                container.set(container.left + mMargin.left, container.top + mMargin.top,
                        container.right - mMargin.right,
                        container.bottom - mMargin.bottom);
            else
                container.set(container.left + mMargin.right, container.top + mMargin.top,
                        container.right - mMargin.left,
                        container.bottom - mMargin.bottom);
            final int minWidth = mDrawable.getMinimumWidth();
            final int minHeight = mDrawable.getMinimumHeight();
            final int width;
            final int height;
            if (mWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
                width = minWidth;
            } else if (mWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
                width = container.width();
            } else {
                width = mWidth;
            }
            if (mHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height = minHeight;
            } else if (mHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
                height = container.height();
            } else {
                height = mHeight;
            }
            mBounds.setEmpty();
            Compat.apply(mDrawable, mGravity, width, height, container, mBounds);
            mDrawable.setBounds(mBounds);
        }

        void getPadding(Rect padding) {
            mDrawable.getPadding(padding);
            if (Compat.isLayoutDirectionLTR(mDrawable))
                padding.set(mMargin.left + padding.left, mMargin.top + padding.top,
                        mMargin.right + padding.right,
                        mMargin.bottom + padding.bottom);
            else
                padding.set(mMargin.right + padding.left, mMargin.top + padding.top,
                        mMargin.left + padding.right,
                        mMargin.bottom + padding.bottom);
        }

        int getIntrinsicWidth() {
            final int width = mDrawable.getIntrinsicWidth();
            return Math.max(mMargin.left + mMargin.right + width, width);
        }

        int getIntrinsicHeight() {
            final int height = mDrawable.getIntrinsicHeight();
            return Math.max(mMargin.top + mMargin.bottom + height, height);
        }

        int getMinimumWidth() {
            final int width = mDrawable.getMinimumWidth();
            return Math.max(mMargin.left + mMargin.right + width, width);
        }

        int getMinimumHeight() {
            final int height = mDrawable.getMinimumHeight();
            return Math.max(mMargin.top + mMargin.bottom + height, height);
        }
    }
}
