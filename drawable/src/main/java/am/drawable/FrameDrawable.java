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
@SuppressWarnings("NullableProblems")
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
        for (ChildDrawable child : mItems) {
            child.setBounds(bounds, mPadding);
        }
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
        final Rect bounds = getBounds();
        for (ChildDrawable child : mItems) {
            child.getDrawable().setBounds(bounds);
        }
        return changed;
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

//    /**
//     * Adds a new layer containing the specified {@code drawable} to the end of
//     * the layer list and returns its index.
//     *
//     * @param dr The drawable to add as a new layer.
//     * @return The index of the new layer.
//     */
//    public int addLayer(Drawable dr) {
//        final ChildDrawable layer = createLayer(dr);
//        final int index = addLayer(layer);
//        ensurePadding();
//        refreshChildPadding(index, layer);
//        return index;
//    }
//
//    /**
//     * Looks for a layer with the given ID and returns its {@link Drawable}.
//     * <p>
//     * If multiple layers are found for the given ID, returns the
//     * {@link Drawable} for the matching layer at the highest index.
//     *
//     * @param id The layer ID to search for.
//     * @return The {@link Drawable} for the highest-indexed layer that has the
//     *         given ID, or null if not found.
//     */
//    public Drawable findDrawableByLayerId(int id) {
//        final ChildDrawable[] layers = mLayerState.mChildren;
//        for (int i = mLayerState.mNumChildren - 1; i >= 0; i--) {
//            if (layers[i].mId == id) {
//                return layers[i].mDrawable;
//            }
//        }
//
//        return null;
//    }
//
//    /**
//     * Sets the ID of a layer.
//     *
//     * @param index The index of the layer to modify, must be in the range
//     *              {@code 0...getNumberOfLayers()-1}.
//     * @param id The id to assign to the layer.
//     *
//     * @see #getId(int)
//     * @attr ref android.R.styleable#LayerDrawableItem_id
//     */
//    public void setId(int index, int id) {
//        mLayerState.mChildren[index].mId = id;
//    }
//
//    /**
//     * Returns the ID of the specified layer.
//     *
//     * @param index The index of the layer, must be in the range
//     *              {@code 0...getNumberOfLayers()-1}.
//     * @return The id of the layer or {@link android.view.View#NO_ID} if the
//     *         layer has no id.
//     *
//     * @see #setId(int, int)
//     * @attr ref android.R.styleable#LayerDrawableItem_id
//     */
//    public int getId(int index) {
//        if (index >= mLayerState.mNumChildren) {
//            throw new IndexOutOfBoundsException();
//        }
//        return mLayerState.mChildren[index].mId;
//    }
//
//    /**
//     * Returns the number of layers contained within this layer drawable.
//     *
//     * @return The number of layers.
//     */
//    public int getNumberOfLayers() {
//        return mLayerState.mNumChildren;
//    }
//
//    /**
//     * Replaces the {@link Drawable} for the layer with the given id.
//     *
//     * @param id The layer ID to search for.
//     * @param drawable The replacement {@link Drawable}.
//     * @return Whether the {@link Drawable} was replaced (could return false if
//     *         the id was not found).
//     */
//    public boolean setDrawableByLayerId(int id, Drawable drawable) {
//        final int index = findIndexByLayerId(id);
//        if (index < 0) {
//            return false;
//        }
//
//        setDrawable(index, drawable);
//        return true;
//    }
//
//    /**
//     * Returns the layer with the specified {@code id}.
//     * <p>
//     * If multiple layers have the same ID, returns the layer with the lowest
//     * index.
//     *
//     * @param id The ID of the layer to return.
//     * @return The index of the layer with the specified ID.
//     */
//    public int findIndexByLayerId(int id) {
//        final ChildDrawable[] layers = mLayerState.mChildren;
//        final int N = mLayerState.mNumChildren;
//        for (int i = 0; i < N; i++) {
//            final ChildDrawable childDrawable = layers[i];
//            if (childDrawable.mId == id) {
//                return i;
//            }
//        }
//
//        return -1;
//    }
//
//    /**
//     * Sets the drawable for the layer at the specified index.
//     *
//     * @param index The index of the layer to modify, must be in the range
//     *              {@code 0...getNumberOfLayers()-1}.
//     * @param drawable The drawable to set for the layer.
//     *
//     * @see #getDrawable(int)
//     * @attr ref android.R.styleable#LayerDrawableItem_drawable
//     */
//    public void setDrawable(int index, Drawable drawable) {
//        if (index >= mLayerState.mNumChildren) {
//            throw new IndexOutOfBoundsException();
//        }
//
//        final ChildDrawable[] layers = mLayerState.mChildren;
//        final ChildDrawable childDrawable = layers[index];
//        if (childDrawable.mDrawable != null) {
//            if (drawable != null) {
//                final Rect bounds = childDrawable.mDrawable.getBounds();
//                drawable.setBounds(bounds);
//            }
//
//            childDrawable.mDrawable.setCallback(null);
//        }
//
//        if (drawable != null) {
//            drawable.setCallback(this);
//        }
//
//        childDrawable.mDrawable = drawable;
//        mLayerState.invalidateCache();
//
//        refreshChildPadding(index, childDrawable);
//    }
//
//    /**
//     * Returns the drawable for the layer at the specified index.
//     *
//     * @param index The index of the layer, must be in the range
//     *              {@code 0...getNumberOfLayers()-1}.
//     * @return The {@link Drawable} at the specified layer index.
//     *
//     * @see #setDrawable(int, Drawable)
//     * @attr ref android.R.styleable#LayerDrawableItem_drawable
//     */
//    public Drawable getDrawable(int index) {
//        if (index >= mLayerState.mNumChildren) {
//            throw new IndexOutOfBoundsException();
//        }
//        return mLayerState.mChildren[index].mDrawable;
//    }
//
//    /**
//     * Sets an explicit size for the specified layer.
//     * <p>
//     * <strong>Note:</strong> Setting an explicit layer size changes the
//     * default layer gravity behavior. See {@link #setLayerGravity(int, int)}
//     * for more information.
//     *
//     * @param index the index of the layer to adjust
//     * @param w width in pixels, or -1 to use the intrinsic width
//     * @param h height in pixels, or -1 to use the intrinsic height
//     * @see #getLayerWidth(int)
//     * @see #getLayerHeight(int)
//     * @attr ref android.R.styleable#LayerDrawableItem_width
//     * @attr ref android.R.styleable#LayerDrawableItem_height
//     */
//    public void setLayerSize(int index, int w, int h) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mWidth = w;
//        childDrawable.mHeight = h;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param w width in pixels, or -1 to use the intrinsic width
//     * @attr ref android.R.styleable#LayerDrawableItem_width
//     */
//    public void setLayerWidth(int index, int w) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mWidth = w;
//    }
//
//    /**
//     * @param index the index of the drawable to adjust
//     * @return the explicit width of the layer, or -1 if not specified
//     * @see #setLayerSize(int, int, int)
//     * @attr ref android.R.styleable#LayerDrawableItem_width
//     */
//    public int getLayerWidth(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mWidth;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param h height in pixels, or -1 to use the intrinsic height
//     * @attr ref android.R.styleable#LayerDrawableItem_height
//     */
//    public void setLayerHeight(int index, int h) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mHeight = h;
//    }
//
//    /**
//     * @param index the index of the drawable to adjust
//     * @return the explicit height of the layer, or -1 if not specified
//     * @see #setLayerSize(int, int, int)
//     * @attr ref android.R.styleable#LayerDrawableItem_height
//     */
//    public int getLayerHeight(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mHeight;
//    }
//
//    /**
//     * Sets the gravity used to position or stretch the specified layer within
//     * its container. Gravity is applied after any layer insets (see
//     * {@link #setLayerInset(int, int, int, int, int)}) or padding (see
//     * {@link #setPaddingMode(int)}).
//     * <p>
//     * If gravity is specified as {@link Gravity#NO_GRAVITY}, the default
//     * behavior depends on whether an explicit width or height has been set
//     * (see {@link #setLayerSize(int, int, int)}), If a dimension is not set,
//     * gravity in that direction defaults to {@link Gravity#FILL_HORIZONTAL} or
//     * {@link Gravity#FILL_VERTICAL}; otherwise, gravity in that direction
//     * defaults to {@link Gravity#LEFT} or {@link Gravity#TOP}.
//     *
//     * @param index the index of the drawable to adjust
//     * @param gravity the gravity to set for the layer
//     *
//     * @see #getLayerGravity(int)
//     * @attr ref android.R.styleable#LayerDrawableItem_gravity
//     */
//    public void setLayerGravity(int index, int gravity) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mGravity = gravity;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return the gravity used to position or stretch the specified layer
//     *         within its container
//     *
//     * @see #setLayerGravity(int, int)
//     * @attr ref android.R.styleable#LayerDrawableItem_gravity
//     */
//    public int getLayerGravity(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mGravity;
//    }
//
//    /**
//     * Specifies the insets in pixels for the drawable at the specified index.
//     *
//     * @param index the index of the drawable to adjust
//     * @param l number of pixels to add to the left bound
//     * @param t number of pixels to add to the top bound
//     * @param r number of pixels to subtract from the right bound
//     * @param b number of pixels to subtract from the bottom bound
//     *
//     * @attr ref android.R.styleable#LayerDrawableItem_left
//     * @attr ref android.R.styleable#LayerDrawableItem_top
//     * @attr ref android.R.styleable#LayerDrawableItem_right
//     * @attr ref android.R.styleable#LayerDrawableItem_bottom
//     */
//    public void setLayerInset(int index, int l, int t, int r, int b) {
//        setLayerInsetInternal(index, l, t, r, b, INSET_UNDEFINED, INSET_UNDEFINED);
//    }
//
//    /**
//     * Specifies the relative insets in pixels for the drawable at the
//     * specified index.
//     *
//     * @param index the index of the layer to adjust
//     * @param s number of pixels to inset from the start bound
//     * @param t number of pixels to inset from the top bound
//     * @param e number of pixels to inset from the end bound
//     * @param b number of pixels to inset from the bottom bound
//     *
//     * @attr ref android.R.styleable#LayerDrawableItem_start
//     * @attr ref android.R.styleable#LayerDrawableItem_top
//     * @attr ref android.R.styleable#LayerDrawableItem_end
//     * @attr ref android.R.styleable#LayerDrawableItem_bottom
//     */
//    public void setLayerInsetRelative(int index, int s, int t, int e, int b) {
//        setLayerInsetInternal(index, 0, t, 0, b, s, e);
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param l number of pixels to inset from the left bound
//     * @attr ref android.R.styleable#LayerDrawableItem_left
//     */
//    public void setLayerInsetLeft(int index, int l) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mInsetL = l;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return number of pixels to inset from the left bound
//     * @attr ref android.R.styleable#LayerDrawableItem_left
//     */
//    public int getLayerInsetLeft(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mInsetL;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param r number of pixels to inset from the right bound
//     * @attr ref android.R.styleable#LayerDrawableItem_right
//     */
//    public void setLayerInsetRight(int index, int r) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mInsetR = r;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return number of pixels to inset from the right bound
//     * @attr ref android.R.styleable#LayerDrawableItem_right
//     */
//    public int getLayerInsetRight(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mInsetR;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param t number of pixels to inset from the top bound
//     * @attr ref android.R.styleable#LayerDrawableItem_top
//     */
//    public void setLayerInsetTop(int index, int t) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mInsetT = t;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return number of pixels to inset from the top bound
//     * @attr ref android.R.styleable#LayerDrawableItem_top
//     */
//    public int getLayerInsetTop(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mInsetT;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param b number of pixels to inset from the bottom bound
//     * @attr ref android.R.styleable#LayerDrawableItem_bottom
//     */
//    public void setLayerInsetBottom(int index, int b) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mInsetB = b;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return number of pixels to inset from the bottom bound
//     * @attr ref android.R.styleable#LayerDrawableItem_bottom
//     */
//    public int getLayerInsetBottom(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mInsetB;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param s number of pixels to inset from the start bound
//     * @attr ref android.R.styleable#LayerDrawableItem_start
//     */
//    public void setLayerInsetStart(int index, int s) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mInsetS = s;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return the number of pixels to inset from the start bound, or
//     *         {@link #INSET_UNDEFINED} if not specified
//     * @attr ref android.R.styleable#LayerDrawableItem_start
//     */
//    public int getLayerInsetStart(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mInsetS;
//    }
//
//    /**
//     * @param index the index of the layer to adjust
//     * @param e number of pixels to inset from the end bound, or
//     *         {@link #INSET_UNDEFINED} if not specified
//     * @attr ref android.R.styleable#LayerDrawableItem_end
//     */
//    public void setLayerInsetEnd(int index, int e) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        childDrawable.mInsetE = e;
//    }
//
//    /**
//     * @param index the index of the layer
//     * @return number of pixels to inset from the end bound
//     * @attr ref android.R.styleable#LayerDrawableItem_end
//     */
//    public int getLayerInsetEnd(int index) {
//        final ChildDrawable childDrawable = mLayerState.mChildren[index];
//        return childDrawable.mInsetE;
//    }

    static class ChildDrawable {

        private final Drawable mDrawable;
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

        public Drawable getDrawable() {
            return mDrawable;
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }

        public void setSize(int width, int height) {
            mWidth = width;
            mHeight = height;
        }

        public int getGravity() {
            return mGravity;
        }

        public void setGravity(int gravity) {
            mGravity = gravity;
        }

        public int getId() {
            return mId;
        }

        public Rect getMargin() {
            return mMargin;
        }

        public void setMargin(int start, int top, int end, int bottom) {
            mMargin.set(start, top, end, bottom);
        }

        void setBounds(Rect bounds, Rect padding) {
            final Rect container = tRect;
            container.set(bounds);
            container.set(container.left + padding.left, container.top + padding.top,
                    container.right - padding.right,
                    container.bottom - padding.bottom);
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
            padding.set(mMargin.left + padding.left, mMargin.top + padding.top,
                    mMargin.right + padding.right, mMargin.bottom + padding.bottom);
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
