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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 安全的Bitmap Drawable
 * 由BitmapDrawable去除部分代码的来
 * Created by Alex on 2019/10/22.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class SafeBitmapDrawable extends Drawable {

    private static final int DEFAULT_PAINT_FLAGS =
            Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;
    private final Rect mDstRect = new Rect();
    private BitmapState mBitmapState;
    private int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
    private boolean mDstRectAndInsetsDirty = true;
    private boolean mMutated;
    private int mBitmapWidth;
    private int mBitmapHeight;

    /**
     * Optical insets due to gravity.
     */
    private Insets mOpticalInsets = Insets.NONE;

    // Mirroring matrix for using with Shaders
    private Matrix mMirrorMatrix;


    private SafeBitmapDrawable(BitmapState state, Resources res) {
        mBitmapState = state;
        updateLocalState(res);

        if (mBitmapState != null && res != null) {
            mBitmapState.mTargetDensity = mTargetDensity;
        }
    }

    public SafeBitmapDrawable(Resources res, Bitmap bitmap) {
        this(new BitmapState(bitmap), res);
    }

    public SafeBitmapDrawable(Resources res) {
        this(res, null);
    }

    /**
     * Returns the paint used to render this drawable.
     */
    public final Paint getPaint() {
        return mBitmapState.mPaint;
    }

    /**
     * Returns the bitmap used by this drawable to render. May be null.
     */
    public final Bitmap getBitmap() {
        return mBitmapState.mBitmap;
    }

    private void computeBitmapSize() {
        final Bitmap bitmap = mBitmapState.mBitmap;
        if (bitmap != null && !bitmap.isRecycled()) {
            mBitmapWidth = bitmap.getScaledWidth(mTargetDensity);
            mBitmapHeight = bitmap.getScaledHeight(mTargetDensity);
        } else {
            mBitmapWidth = mBitmapHeight = -1;
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (mBitmapState.mBitmap != bitmap) {
            mBitmapState.mBitmap = bitmap;
            computeBitmapSize();
            invalidateSelf();
        }
    }

    /**
     * Set the density scale at which this drawable will be rendered. This
     * method assumes the drawable will be rendered at the same density as the
     * specified canvas.
     *
     * @param canvas The Canvas from which the density scale must be obtained.
     * @see android.graphics.Bitmap#setDensity(int)
     * @see android.graphics.Bitmap#getDensity()
     */
    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    /**
     * Set the density scale at which this drawable will be rendered.
     *
     * @param metrics The DisplayMetrics indicating the density scale for this drawable.
     * @see android.graphics.Bitmap#setDensity(int)
     * @see android.graphics.Bitmap#getDensity()
     */
    public void setTargetDensity(DisplayMetrics metrics) {
        setTargetDensity(metrics.densityDpi);
    }

    /**
     * Set the density at which this drawable will be rendered.
     *
     * @param density The density scale for this drawable.
     * @see android.graphics.Bitmap#setDensity(int)
     * @see android.graphics.Bitmap#getDensity()
     */
    public void setTargetDensity(int density) {
        if (mTargetDensity != density) {
            mTargetDensity = density == 0 ? DisplayMetrics.DENSITY_DEFAULT : density;
            if (mBitmapState.mBitmap != null && !mBitmapState.mBitmap.isRecycled()) {
                computeBitmapSize();
            }
            invalidateSelf();
        }
    }

    /**
     * Get the gravity used to position/stretch the bitmap within its bounds.
     * See android.view.Gravity
     *
     * @return the gravity applied to the bitmap
     */
    public int getGravity() {
        return mBitmapState.mGravity;
    }

    /**
     * Set the gravity used to position/stretch the bitmap within its bounds.
     * See android.view.Gravity
     *
     * @param gravity the gravity
     */
    public void setGravity(int gravity) {
        if (mBitmapState.mGravity != gravity) {
            mBitmapState.mGravity = gravity;
            mDstRectAndInsetsDirty = true;
            invalidateSelf();
        }
    }

    /**
     * Enables or disables the mipmap hint for this drawable's bitmap.
     * See {@link Bitmap#setHasMipMap(boolean)} for more information.
     * <p>
     * If the bitmap is null calling this method has no effect.
     *
     * @param mipMap True if the bitmap should use mipmaps, false otherwise.
     * @see #hasMipMap()
     */
    public void setMipMap(boolean mipMap) {
        if (mBitmapState.mBitmap != null && !mBitmapState.mBitmap.isRecycled()) {
            mBitmapState.mBitmap.setHasMipMap(mipMap);
            invalidateSelf();
        }
    }

    /**
     * Indicates whether the mipmap hint is enabled on this drawable's bitmap.
     *
     * @return True if the mipmap hint is set, false otherwise. If the bitmap
     * is null, this method always returns false.
     * @see #setMipMap(boolean)
     */
    public boolean hasMipMap() {
        return mBitmapState.mBitmap != null && !mBitmapState.mBitmap.isRecycled()
                && mBitmapState.mBitmap.hasMipMap();
    }

    /**
     * Enables or disables anti-aliasing for this drawable. Anti-aliasing affects
     * the edges of the bitmap only so it applies only when the drawable is rotated.
     *
     * @param aa True if the bitmap should be anti-aliased, false otherwise.
     * @see #hasAntiAlias()
     */
    public void setAntiAlias(boolean aa) {
        mBitmapState.mPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    /**
     * Indicates whether anti-aliasing is enabled for this drawable.
     *
     * @return True if anti-aliasing is enabled, false otherwise.
     * @see #setAntiAlias(boolean)
     */
    public boolean hasAntiAlias() {
        return mBitmapState.mPaint.isAntiAlias();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mBitmapState.mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public boolean isFilterBitmap() {
        return mBitmapState.mPaint.isFilterBitmap();
    }

    @Override
    public void setDither(boolean dither) {
        mBitmapState.mPaint.setDither(dither);
        invalidateSelf();
    }

    /**
     * Indicates the repeat behavior of this drawable on the X axis.
     *
     * @return {@link android.graphics.Shader.TileMode#CLAMP} if the bitmap does not repeat,
     * {@link android.graphics.Shader.TileMode#REPEAT} or
     * {@link android.graphics.Shader.TileMode#MIRROR} otherwise.
     */
    public Shader.TileMode getTileModeX() {
        return mBitmapState.mTileModeX;
    }

    /**
     * Indicates the repeat behavior of this drawable on the Y axis.
     *
     * @return {@link android.graphics.Shader.TileMode#CLAMP} if the bitmap does not repeat,
     * {@link android.graphics.Shader.TileMode#REPEAT} or
     * {@link android.graphics.Shader.TileMode#MIRROR} otherwise.
     */
    public Shader.TileMode getTileModeY() {
        return mBitmapState.mTileModeY;
    }

    /**
     * Sets the repeat behavior of this drawable on the X axis. By default, the drawable
     * does not repeat its bitmap. Using {@link android.graphics.Shader.TileMode#REPEAT} or
     * {@link android.graphics.Shader.TileMode#MIRROR} the bitmap can be repeated (or tiled)
     * if the bitmap is smaller than this drawable.
     *
     * @param mode The repeat mode for this drawable.
     * @see #setTileModeY(android.graphics.Shader.TileMode)
     * @see #setTileModeXY(android.graphics.Shader.TileMode, android.graphics.Shader.TileMode)
     */
    public void setTileModeX(Shader.TileMode mode) {
        setTileModeXY(mode, mBitmapState.mTileModeY);
    }

    /**
     * Sets the repeat behavior of this drawable on the Y axis. By default, the drawable
     * does not repeat its bitmap. Using {@link android.graphics.Shader.TileMode#REPEAT} or
     * {@link android.graphics.Shader.TileMode#MIRROR} the bitmap can be repeated (or tiled)
     * if the bitmap is smaller than this drawable.
     *
     * @param mode The repeat mode for this drawable.
     * @see #setTileModeX(android.graphics.Shader.TileMode)
     * @see #setTileModeXY(android.graphics.Shader.TileMode, android.graphics.Shader.TileMode)
     */
    public final void setTileModeY(Shader.TileMode mode) {
        setTileModeXY(mBitmapState.mTileModeX, mode);
    }

    /**
     * Sets the repeat behavior of this drawable on both axis. By default, the drawable
     * does not repeat its bitmap. Using {@link android.graphics.Shader.TileMode#REPEAT} or
     * {@link android.graphics.Shader.TileMode#MIRROR} the bitmap can be repeated (or tiled)
     * if the bitmap is smaller than this drawable.
     *
     * @param xmode The X repeat mode for this drawable.
     * @param ymode The Y repeat mode for this drawable.
     * @see #setTileModeX(android.graphics.Shader.TileMode)
     * @see #setTileModeY(android.graphics.Shader.TileMode)
     */
    public void setTileModeXY(Shader.TileMode xmode, Shader.TileMode ymode) {
        final BitmapState state = mBitmapState;
        if (state.mTileModeX != xmode || state.mTileModeY != ymode) {
            state.mTileModeX = xmode;
            state.mTileModeY = ymode;
            state.mRebuildShader = true;
            mDstRectAndInsetsDirty = true;
            invalidateSelf();
        }
    }

    @Override
    public void setAutoMirrored(boolean mirrored) {
        if (mBitmapState.mAutoMirrored != mirrored) {
            mBitmapState.mAutoMirrored = mirrored;
            invalidateSelf();
        }
    }

    @Override
    public final boolean isAutoMirrored() {
        return mBitmapState.mAutoMirrored;
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mBitmapState.getChangingConfigurations();
    }

    private boolean needMirroring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isAutoMirrored() && getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        }
        return isAutoMirrored();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mDstRectAndInsetsDirty = true;

        final Bitmap bitmap = mBitmapState.mBitmap;
        final Shader shader = mBitmapState.mPaint.getShader();
        if (bitmap != null && !bitmap.isRecycled() && shader != null) {
            updateShaderMatrix(bitmap, mBitmapState.mPaint, shader, needMirroring());
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Bitmap bitmap = mBitmapState.mBitmap;
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }
        final BitmapState state = mBitmapState;
        final Paint paint = state.mPaint;
        if (state.mRebuildShader) {
            final Shader.TileMode tmx = state.mTileModeX;
            final Shader.TileMode tmy = state.mTileModeY;
            if (tmx == null && tmy == null) {
                paint.setShader(null);
            } else {
                paint.setShader(new BitmapShader(bitmap,
                        tmx == null ? Shader.TileMode.CLAMP : tmx,
                        tmy == null ? Shader.TileMode.CLAMP : tmy));
            }

            state.mRebuildShader = false;
        }
        updateDstRectAndInsetsIfDirty();
        final Shader shader = paint.getShader();
        final boolean needMirroring = needMirroring();
        if (shader == null) {
            if (needMirroring) {
                canvas.save();
                // Mirror the bitmap
                canvas.translate(mDstRect.right - mDstRect.left, 0);
                canvas.scale(-1.0f, 1.0f);
            }

            canvas.drawBitmap(bitmap, null, mDstRect, paint);

            if (needMirroring) {
                canvas.restore();
            }
        } else {
            updateShaderMatrix(bitmap, paint, shader, needMirroring);
            canvas.drawRect(mDstRect, paint);
        }
    }

    /**
     * Updates the {@code paint}'s shader matrix to be consistent with the
     * destination size and layout direction.
     *
     * @param bitmap        the bitmap to be drawn
     * @param paint         the paint used to draw the bitmap
     * @param shader        the shader to set on the paint
     * @param needMirroring whether the bitmap should be mirrored
     */
    private void updateShaderMatrix(@NonNull Bitmap bitmap, @NonNull Paint paint,
                                    @NonNull Shader shader, boolean needMirroring) {
        final int sourceDensity = bitmap.getDensity();
        final int targetDensity = mTargetDensity;
        final boolean needScaling = sourceDensity != 0 && sourceDensity != targetDensity;
        if (needScaling || needMirroring) {
            final Matrix matrix = getOrCreateMirrorMatrix();
            matrix.reset();

            if (needMirroring) {
                final int dx = mDstRect.right - mDstRect.left;
                matrix.setTranslate(dx, 0);
                matrix.setScale(-1, 1);
            }

            if (needScaling) {
                final float densityScale = targetDensity / (float) sourceDensity;
                matrix.postScale(densityScale, densityScale);
            }

            shader.setLocalMatrix(matrix);
        } else {
            mMirrorMatrix = null;
            shader.setLocalMatrix(null);
        }

        paint.setShader(shader);
    }

    private Matrix getOrCreateMirrorMatrix() {
        if (mMirrorMatrix == null) {
            mMirrorMatrix = new Matrix();
        }
        return mMirrorMatrix;
    }

    private void updateDstRectAndInsetsIfDirty() {
        if (mDstRectAndInsetsDirty) {
            if (mBitmapState.mTileModeX == null && mBitmapState.mTileModeY == null) {
                final Rect bounds = getBounds();
                final int layoutDirection;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    layoutDirection = getLayoutDirection();
                } else {
                    layoutDirection = View.LAYOUT_DIRECTION_LTR;
                }
                Gravity.apply(mBitmapState.mGravity, mBitmapWidth, mBitmapHeight,
                        bounds, mDstRect, layoutDirection);

                final int left = mDstRect.left - bounds.left;
                final int top = mDstRect.top - bounds.top;
                final int right = bounds.right - mDstRect.right;
                final int bottom = bounds.bottom - mDstRect.bottom;
                mOpticalInsets = Insets.of(left, top, right, bottom);
            } else {
                copyBounds(mDstRect);
                mOpticalInsets = Insets.NONE;
            }
        }
        mDstRectAndInsetsDirty = false;
    }

    @NonNull
    @Override
    public Insets getOpticalInsets() {
        updateDstRectAndInsetsIfDirty();
        return mOpticalInsets;
    }

    @Override
    public void getOutline(@NonNull Outline outline) {
        updateDstRectAndInsetsIfDirty();
        outline.setRect(mDstRect);

        // Only opaque Bitmaps can report a non-0 alpha,
        // since only they are guaranteed to fill their bounds
        boolean opaqueOverShape = mBitmapState.mBitmap != null
                && !mBitmapState.mBitmap.isRecycled()
                && !mBitmapState.mBitmap.hasAlpha();
        outline.setAlpha(opaqueOverShape ? getAlpha() / 255.0f : 0.0f);
    }

    @Override
    public void setAlpha(int alpha) {
        final int oldAlpha = mBitmapState.mPaint.getAlpha();
        if (alpha != oldAlpha) {
            mBitmapState.mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        return mBitmapState.mPaint.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mBitmapState.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mBitmapState.mPaint.getColorFilter();
    }

    /**
     * A mutable BitmapDrawable still shares its Bitmap with any other Drawable
     * that comes from the same resource.
     *
     * @return This drawable.
     */
    @NonNull
    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mBitmapState = new BitmapState(mBitmapState);
            mMutated = true;
        }
        return this;
    }

    @Override
    public void applyTheme(@NonNull Resources.Theme t) {
        super.applyTheme(t);
        final BitmapState state = mBitmapState;
        if (state == null) {
            return;
        }
        updateLocalState(t.getResources());
    }


    @Override
    public boolean canApplyTheme() {
        return mBitmapState != null && mBitmapState.canApplyTheme();
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    @Override
    public int getOpacity() {
        if (mBitmapState.mGravity != Gravity.FILL) {
            return PixelFormat.TRANSLUCENT;
        }

        final Bitmap bitmap = mBitmapState.mBitmap;
        return (bitmap == null || bitmap.isRecycled() || bitmap.hasAlpha() ||
                mBitmapState.mPaint.getAlpha() < 255) ?
                PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    public final ConstantState getConstantState() {
        mBitmapState.mChangingConfigurations |= getChangingConfigurations();
        return mBitmapState;
    }

    final static class BitmapState extends ConstantState {
        final Paint mPaint;
        Bitmap mBitmap;
        int mGravity = Gravity.FILL;
        Shader.TileMode mTileModeX = null;
        Shader.TileMode mTileModeY = null;
        boolean mRebuildShader;
        int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        boolean mAutoMirrored = false;
        int mChangingConfigurations;

        BitmapState(Bitmap bitmap) {
            mBitmap = bitmap;
            mPaint = new Paint(DEFAULT_PAINT_FLAGS);
        }

        BitmapState(BitmapState bitmapState) {
            mBitmap = bitmapState.mBitmap;
            mChangingConfigurations = bitmapState.mChangingConfigurations;
            mGravity = bitmapState.mGravity;
            mTileModeX = bitmapState.mTileModeX;
            mTileModeY = bitmapState.mTileModeY;
            mTargetDensity = bitmapState.mTargetDensity;
            mPaint = new Paint(bitmapState.mPaint);
            mRebuildShader = bitmapState.mRebuildShader;
            mAutoMirrored = bitmapState.mAutoMirrored;
        }

        @Override
        public Drawable newDrawable() {
            return new SafeBitmapDrawable(this, null);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new SafeBitmapDrawable(this, res);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    /**
     * Initializes local dynamic properties from state. This should be called
     * after significant state changes, e.g. from the One True Constructor and
     * after inflating or applying a theme.
     */
    private void updateLocalState(Resources res) {
        mTargetDensity = resolveDensity(res, mBitmapState.mTargetDensity);
        computeBitmapSize();
    }

    private static int resolveDensity(@Nullable Resources r, int parentDensity) {
        final int densityDpi = r == null ? parentDensity : r.getDisplayMetrics().densityDpi;
        return densityDpi == 0 ? DisplayMetrics.DENSITY_DEFAULT : densityDpi;
    }
}
