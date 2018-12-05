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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Fancy progress indicator for Material theme.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class MaterialProgressDrawable extends Drawable implements Animatable {
    // Maps to ProgressBar.Large style
    @SuppressWarnings("WeakerAccess")
    public static final int LARGE = 0;
    // Maps to ProgressBar default style
    public static final int DEFAULT = 1;
    private static final long FRAME_DURATION = 1000 / 60;
    private static final LinearInterpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final FastOutSlowInInterpolator MATERIAL_INTERPOLATOR =
            new FastOutSlowInInterpolator();
    private static final float FULL_ROTATION = 1080.0f;
    // Maps to ProgressBar default style
    private static final int CIRCLE_DIAMETER = 40;
    private static final float CENTER_RADIUS = 8.75f; //should add up to 10 when + stroke_width
    private static final float STROKE_WIDTH = 2.5f;

    // Maps to ProgressBar.Large style
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final float CENTER_RADIUS_LARGE = 12.5f;
    private static final float STROKE_WIDTH_LARGE = 3f;

    private static final int[] COLORS = new int[]{
            Color.BLACK
    };

    /**
     * The value in the linear interpolator for animating the drawable at which
     * the color transition should start
     */
    private static final float COLOR_START_DELAY_OFFSET = 0.75f;
    private static final float END_TRIM_START_DELAY_OFFSET = 0.5f;
    private static final float START_TRIM_DURATION_OFFSET = 0.5f;

    /**
     * The duration of a single progress spin in milliseconds.
     */
    private static final int ANIMATION_DURATION = 1332;

    /**
     * The number of points in the progress "star".
     */
    private static final float NUM_POINTS = 5f;
    /**
     * Layout info for the arrowhead in dp
     */
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_HEIGHT = 5;
    private static final float ARROW_OFFSET_ANGLE = 5;
    /**
     * Layout info for the arrowhead for the large spinner in dp
     */
    private static final int ARROW_WIDTH_LARGE = 12;
    private static final int ARROW_HEIGHT_LARGE = 6;
    private static final float MAX_PROGRESS_ARC = .8f;
    /**
     * The indicator ring, used to manage animation state.
     */
    private final Ring mRing = new Ring();
    /**
     * Canvas rotation in degrees.
     */
    private float mRotation;
    private float mRotationCount;
    private int mWidth;
    private int mHeight;
    private boolean mFinishing;

    private long mDuration;
    private long mStartTime;
    private boolean running = false;
    private final Runnable mUpdater = new Runnable() {
        @Override
        public void run() {
            if (!running)
                return;
            final Ring ring = mRing;
            if (mStartTime == -1) {
                mStartTime = SystemClock.uptimeMillis();
                ring.storeOriginals();
                ring.goToNextColor();
                ring.setStartTrim(ring.getEndTrim());
                if (mFinishing) {
                    // finished closing the last ring from the swipe gesture; go
                    // into progress mode
                    mFinishing = false;
                    mDuration = ANIMATION_DURATION;
                    ring.setShowArrow(false);
                } else {
                    mRotationCount = (mRotationCount + 1) % (NUM_POINTS);
                }
            }
            long timeOffset = SystemClock.uptimeMillis() - mStartTime;
            float interpolatedTime;
            if (timeOffset > mDuration) {
                interpolatedTime = 1f;
                mStartTime = -1;
            } else {
                interpolatedTime = LINEAR_INTERPOLATOR.getInterpolation((float) timeOffset / mDuration);
            }
            if (mFinishing) {
                applyFinishTranslation(interpolatedTime, ring);
            } else {
                // The minProgressArc is calculated from 0 to create an
                // angle that matches the stroke width.
                final float minProgressArc = getMinProgressArc(ring);
                final float startingEndTrim = ring.getStartingEndTrim();
                final float startingTrim = ring.getStartingStartTrim();
                final float startingRotation = ring.getStartingRotation();

                updateRingColor(interpolatedTime, ring);

                // Moving the start trim only occurs in the first 50% of a
                // single ring animation
                if (interpolatedTime <= START_TRIM_DURATION_OFFSET) {
                    // scale the interpolatedTime so that the full
                    // transformation from 0 - 1 takes place in the
                    // remaining time
                    final float scaledTime = (interpolatedTime)
                            / (1.0f - START_TRIM_DURATION_OFFSET);
                    final float startTrim = startingTrim
                            + ((MAX_PROGRESS_ARC - minProgressArc) * MATERIAL_INTERPOLATOR
                            .getInterpolation(scaledTime));
                    ring.setStartTrim(startTrim);
                }

                // Moving the end trim starts after 50% of a single ring
                // animation completes
                if (interpolatedTime > END_TRIM_START_DELAY_OFFSET) {
                    // scale the interpolatedTime so that the full
                    // transformation from 0 - 1 takes place in the
                    // remaining time
                    final float minArc = MAX_PROGRESS_ARC - minProgressArc;
                    float scaledTime = (interpolatedTime - START_TRIM_DURATION_OFFSET)
                            / (1.0f - START_TRIM_DURATION_OFFSET);
                    final float endTrim = startingEndTrim
                            + (minArc * MATERIAL_INTERPOLATOR.getInterpolation(scaledTime));
                    ring.setEndTrim(endTrim);
                }

                final float rotation = startingRotation + (0.25f * interpolatedTime);
                ring.setRotation(rotation);

                float groupRotation = ((FULL_ROTATION / NUM_POINTS) * interpolatedTime)
                        + (FULL_ROTATION * (mRotationCount / NUM_POINTS));
                setRotation(groupRotation);
            }
            scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION);
        }
    };

    public MaterialProgressDrawable(float screenDensity) {
        this(screenDensity, DEFAULT);
    }

    public MaterialProgressDrawable(final float screenDensity, int size) {
        this(screenDensity, size, Color.TRANSPARENT, 255, COLORS);
    }

    public MaterialProgressDrawable(final float screenDensity, int size,
                                    int backgroundColor, int alpha, int... colors) {
        int width;
        int height;
        double centerRadius;
        float strokeWidth;
        float arrowWidth;
        float arrowHeight;
        if (size == LARGE) {
            width = height = (int) (CIRCLE_DIAMETER_LARGE * screenDensity);
            centerRadius = CENTER_RADIUS_LARGE * screenDensity;
            strokeWidth = STROKE_WIDTH_LARGE * screenDensity;
            arrowWidth = ARROW_WIDTH_LARGE * screenDensity;
            arrowHeight = ARROW_HEIGHT_LARGE * screenDensity;
        } else {
            width = height = (int) (CIRCLE_DIAMETER * screenDensity);
            centerRadius = CENTER_RADIUS * screenDensity;
            strokeWidth = STROKE_WIDTH * screenDensity;
            arrowWidth = ARROW_WIDTH * screenDensity;
            arrowHeight = ARROW_HEIGHT * screenDensity;
        }
        setSizeParameters(width, height, centerRadius, strokeWidth, arrowWidth, arrowHeight,
                backgroundColor, alpha, colors);
    }

    public MaterialProgressDrawable(int width, int height, double centerRadius, float strokeWidth,
                                    float arrowWidth, float arrowHeight,
                                    int backgroundColor, int alpha, int... colors) {
        setSizeParameters(width, height, centerRadius, strokeWidth, arrowWidth, arrowHeight,
                backgroundColor, alpha, colors);
    }

    private void setSizeParameters(int width, int height, double centerRadius, float strokeWidth,
                                   float arrowWidth, float arrowHeight,
                                   int backgroundColor, int alpha, int... colors) {
        final Ring ring = mRing;
        mWidth = width;
        mHeight = height;
        ring.setCenterRadius(centerRadius);
        ring.setStrokeWidth(strokeWidth);
        ring.setArrowDimensions(arrowWidth, arrowHeight);
        ring.setBackgroundColor(backgroundColor);
        ring.setAlpha(alpha);
        ring.setColors(colors);
        ring.setInsets(mWidth, mHeight);
    }

    /**
     * @param show Set to true to display the arrowhead on the progress spinner.
     */
    public void showArrow(boolean show) {
        mRing.setShowArrow(show);
    }

    /**
     * @param scale Set the scale of the arrowhead for the spinner.
     */
    public void setArrowScale(float scale) {
        mRing.setArrowScale(scale);
    }

    /**
     * Set the start and end trim for the progress spinner arc.
     *
     * @param startAngle start angle
     * @param endAngle   end angle
     */
    public void setStartEndTrim(float startAngle, float endAngle) {
        mRing.setStartTrim(startAngle);
        mRing.setEndTrim(endAngle);
    }

    /**
     * Set the amount of rotation to apply to the progress spinner.
     *
     * @param rotation Rotation is from [0..1]
     */
    public void setProgressRotation(float rotation) {
        mRing.setRotation(rotation);
    }

    /**
     * Update the background color of the circle image view.
     */
    public void setBackgroundColor(int color) {
        mRing.setBackgroundColor(color);
    }

    /**
     * Set the colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colors colors
     */
    public void setColorSchemeColors(int... colors) {
        mRing.setColors(colors);
        mRing.setColorIndex(0);
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void draw(Canvas c) {
        final Rect bounds = getBounds();
        final int saveCount = c.save();
        c.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        mRing.draw(c, bounds);
        c.restoreToCount(saveCount);
    }

    public int getAlpha() {
        return mRing.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        mRing.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mRing.setColorFilter(colorFilter);
    }

    private float getRotation() {
        return mRotation;
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void start() {
        running = true;
        mRotationCount = 0;
        mStartTime = -1;
        mRing.storeOriginals();
        // Already showing some part of the ring
        if (mRing.getEndTrim() != mRing.getStartTrim()) {
            mFinishing = true;
            mDuration = ANIMATION_DURATION / 2;
        } else {
            mRing.setColorIndex(0);
            mRing.resetOriginals();
            mDuration = ANIMATION_DURATION;
        }
        mUpdater.run();
    }

    @Override
    public void stop() {
        unscheduleSelf(mUpdater);
        running = false;
        setRotation(0);
        mRing.setShowArrow(false);
        mRing.setColorIndex(0);
        mRing.resetOriginals();
    }

    public float getMinProgressArc(Ring ring) {
        return (float) Math.toRadians(
                ring.getStrokeWidth() / (2 * Math.PI * ring.getCenterRadius()));
    }

    // Adapted from ArgbEvaluator.java
    private int evaluateColorChange(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }

    /**
     * Update the ring color if this is within the last 25% of the animation.
     * The new ring color will be a translation from the starting ring color to
     * the next color.
     */
    private void updateRingColor(float interpolatedTime, Ring ring) {
        if (interpolatedTime > COLOR_START_DELAY_OFFSET) {
            // scale the interpolatedTime so that the full
            // transformation from 0 - 1 takes place in the
            // remaining time
            ring.setColor(evaluateColorChange((interpolatedTime - COLOR_START_DELAY_OFFSET)
                            / (1.0f - COLOR_START_DELAY_OFFSET), ring.getStartingColor(),
                    ring.getNextColor()));
        }
    }

    private void applyFinishTranslation(float interpolatedTime, Ring ring) {
        // shrink back down and complete a full rotation before
        // starting other circles
        // Rotation goes between [0..1].
        updateRingColor(interpolatedTime, ring);
        float targetRotation = (float) (Math.floor(ring.getStartingRotation() / MAX_PROGRESS_ARC)
                + 1f);
        final float minProgressArc = getMinProgressArc(ring);
        final float startTrim = ring.getStartingStartTrim()
                + (ring.getStartingEndTrim() - minProgressArc - ring.getStartingStartTrim())
                * interpolatedTime;
        ring.setStartTrim(startTrim);
        ring.setEndTrim(ring.getStartingEndTrim());
        final float rotation = ring.getStartingRotation()
                + ((targetRotation - ring.getStartingRotation()) * interpolatedTime);
        ring.setRotation(rotation);
    }

    private static class FastOutSlowInInterpolator implements Interpolator {

        /**
         * Lookup table values sampled with x at regular intervals between 0 and 1 for a total of
         * 201 points.
         */
        private static final float[] VALUES = new float[]{
                0.0000f, 0.0001f, 0.0002f, 0.0005f, 0.0009f, 0.0014f, 0.0020f,
                0.0027f, 0.0036f, 0.0046f, 0.0058f, 0.0071f, 0.0085f, 0.0101f,
                0.0118f, 0.0137f, 0.0158f, 0.0180f, 0.0205f, 0.0231f, 0.0259f,
                0.0289f, 0.0321f, 0.0355f, 0.0391f, 0.0430f, 0.0471f, 0.0514f,
                0.0560f, 0.0608f, 0.0660f, 0.0714f, 0.0771f, 0.0830f, 0.0893f,
                0.0959f, 0.1029f, 0.1101f, 0.1177f, 0.1257f, 0.1339f, 0.1426f,
                0.1516f, 0.1610f, 0.1707f, 0.1808f, 0.1913f, 0.2021f, 0.2133f,
                0.2248f, 0.2366f, 0.2487f, 0.2611f, 0.2738f, 0.2867f, 0.2998f,
                0.3131f, 0.3265f, 0.3400f, 0.3536f, 0.3673f, 0.3810f, 0.3946f,
                0.4082f, 0.4217f, 0.4352f, 0.4485f, 0.4616f, 0.4746f, 0.4874f,
                0.5000f, 0.5124f, 0.5246f, 0.5365f, 0.5482f, 0.5597f, 0.5710f,
                0.5820f, 0.5928f, 0.6033f, 0.6136f, 0.6237f, 0.6335f, 0.6431f,
                0.6525f, 0.6616f, 0.6706f, 0.6793f, 0.6878f, 0.6961f, 0.7043f,
                0.7122f, 0.7199f, 0.7275f, 0.7349f, 0.7421f, 0.7491f, 0.7559f,
                0.7626f, 0.7692f, 0.7756f, 0.7818f, 0.7879f, 0.7938f, 0.7996f,
                0.8053f, 0.8108f, 0.8162f, 0.8215f, 0.8266f, 0.8317f, 0.8366f,
                0.8414f, 0.8461f, 0.8507f, 0.8551f, 0.8595f, 0.8638f, 0.8679f,
                0.8720f, 0.8760f, 0.8798f, 0.8836f, 0.8873f, 0.8909f, 0.8945f,
                0.8979f, 0.9013f, 0.9046f, 0.9078f, 0.9109f, 0.9139f, 0.9169f,
                0.9198f, 0.9227f, 0.9254f, 0.9281f, 0.9307f, 0.9333f, 0.9358f,
                0.9382f, 0.9406f, 0.9429f, 0.9452f, 0.9474f, 0.9495f, 0.9516f,
                0.9536f, 0.9556f, 0.9575f, 0.9594f, 0.9612f, 0.9629f, 0.9646f,
                0.9663f, 0.9679f, 0.9695f, 0.9710f, 0.9725f, 0.9739f, 0.9753f,
                0.9766f, 0.9779f, 0.9791f, 0.9803f, 0.9815f, 0.9826f, 0.9837f,
                0.9848f, 0.9858f, 0.9867f, 0.9877f, 0.9885f, 0.9894f, 0.9902f,
                0.9910f, 0.9917f, 0.9924f, 0.9931f, 0.9937f, 0.9944f, 0.9949f,
                0.9955f, 0.9960f, 0.9964f, 0.9969f, 0.9973f, 0.9977f, 0.9980f,
                0.9984f, 0.9986f, 0.9989f, 0.9991f, 0.9993f, 0.9995f, 0.9997f,
                0.9998f, 0.9999f, 0.9999f, 1.0000f, 1.0000f
        };
        private final float[] mValues;
        private final float mStepSize;

        private FastOutSlowInInterpolator() {
            mValues = VALUES;
            mStepSize = 1f / (mValues.length - 1);
        }

        @Override
        public float getInterpolation(float input) {
            if (input >= 1.0f) {
                return 1.0f;
            }
            if (input <= 0f) {
                return 0f;
            }

            // Calculate index - We use min with length - 2 to avoid IndexOutOfBoundsException when
            // we lerp (linearly interpolate) in the return statement
            int position = Math.min((int) (input * (mValues.length - 1)), mValues.length - 2);

            // Calculate values to account for small offsets as the lookup table has discrete values
            float quantized = position * mStepSize;
            float diff = input - quantized;
            float weight = diff / mStepSize;

            // Linearly interpolate between the table values
            return mValues[position] + weight * (mValues[position + 1] - mValues[position]);
        }
    }

    private class Ring {
        private final RectF mTempBounds = new RectF();
        private final Paint mPaint = new Paint();
        private final Paint mArrowPaint = new Paint();
        private final Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float mStartTrim = 0.0f;
        private float mEndTrim = 0.0f;
        private float mRotation = 0.0f;
        private float mStrokeWidth = 5.0f;
        private float mStrokeInset = 2.5f;
        private int[] mColors;
        // mColorIndex represents the offset into the available mColors that the
        // progress circle should currently display. As the progress circle is
        // animating, the mColorIndex moves by one to the next available color.
        private int mColorIndex;
        private float mStartingStartTrim;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private boolean mShowArrow;
        private Path mArrow;
        private float mArrowScale;
        private double mRingCenterRadius;
        private int mArrowWidth;
        private int mArrowHeight;
        private int mAlpha;
        private int mBackgroundColor;
        private int mCurrentColor;

        private Ring() {
            mPaint.setStrokeCap(Paint.Cap.SQUARE);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mArrowPaint.setStyle(Paint.Style.FILL);
            mArrowPaint.setAntiAlias(true);
        }

        private void setBackgroundColor(int color) {
            mBackgroundColor = color;
        }

        /**
         * Set the dimensions of the arrowhead.
         *
         * @param width  Width of the hypotenuse of the arrow head
         * @param height Height of the arrow point
         */
        private void setArrowDimensions(float width, float height) {
            mArrowWidth = (int) width;
            mArrowHeight = (int) height;
        }

        /**
         * Draw the progress spinner
         */
        private void draw(Canvas c, Rect bounds) {
            final RectF arcBounds = mTempBounds;
            arcBounds.set(bounds);
            arcBounds.inset(mStrokeInset, mStrokeInset);

            final float startAngle = (mStartTrim + mRotation) * 360;
            final float endAngle = (mEndTrim + mRotation) * 360;
            float sweepAngle = endAngle - startAngle;
            mPaint.setColor(mCurrentColor);
            c.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint);

            drawTriangle(c, startAngle, sweepAngle, bounds);

            if (mAlpha < 255) {
                mCirclePaint.setColor(mBackgroundColor);
                mCirclePaint.setAlpha(255 - mAlpha);
                c.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2,
                        mCirclePaint);
            }
        }

        private void drawTriangle(Canvas c, float startAngle, float sweepAngle, Rect bounds) {
            if (mShowArrow) {
                if (mArrow == null) {
                    mArrow = new android.graphics.Path();
                    mArrow.setFillType(android.graphics.Path.FillType.EVEN_ODD);
                } else {
                    mArrow.reset();
                }

                // Adjust the position of the triangle so that it is inset as
                // much as the arc, but also centered on the arc.
                float inset = (int) mStrokeInset / 2 * mArrowScale;
                float x = (float) (mRingCenterRadius * Math.cos(0) + bounds.exactCenterX());
                float y = (float) (mRingCenterRadius * Math.sin(0) + bounds.exactCenterY());

                // Update the path each time. This works around an issue in SKIA
                // where concatenating a rotation matrix to a scale matrix
                // ignored a starting negative rotation. This appears to have
                // been fixed as of API 21.
                mArrow.moveTo(0, 0);
                mArrow.lineTo(mArrowWidth * mArrowScale, 0);
                mArrow.lineTo((mArrowWidth * mArrowScale / 2), (mArrowHeight
                        * mArrowScale));
                mArrow.offset(x - inset, y);
                mArrow.close();
                // draw a triangle
                mArrowPaint.setColor(mCurrentColor);
                c.rotate(startAngle + sweepAngle - ARROW_OFFSET_ANGLE, bounds.exactCenterX(),
                        bounds.exactCenterY());
                c.drawPath(mArrow, mArrowPaint);
            }
        }

        /**
         * Set the colors the progress spinner alternates between.
         *
         * @param colors Array of integers describing the colors. Must be non-<code>null</code>.
         */
        private void setColors(int... colors) {
            mColors = colors;
            // if colors are reset, make sure to reset the color index as well
            setColorIndex(0);
        }

        /**
         * Set the absolute color of the progress spinner. This is should only
         * be used when animating between current and next color when the
         * spinner is rotating.
         *
         * @param color int describing the color.
         */
        private void setColor(int color) {
            mCurrentColor = color;
        }

        /**
         * @param index Index into the color array of the color to display in
         *              the progress spinner.
         */
        private void setColorIndex(int index) {
            mColorIndex = index;
            mCurrentColor = mColors[mColorIndex];
        }

        /**
         * @return int describing the next color the progress spinner should use when drawing.
         */
        private int getNextColor() {
            return mColors[getNextColorIndex()];
        }

        private int getNextColorIndex() {
            return (mColorIndex + 1) % (mColors.length);
        }

        /**
         * Proceed to the next available ring color. This will automatically
         * wrap back to the beginning of colors.
         */
        private void goToNextColor() {
            setColorIndex(getNextColorIndex());
        }

        private void setColorFilter(ColorFilter filter) {
            mPaint.setColorFilter(filter);
            invalidateSelf();
        }

        /**
         * @return Current alpha of the progress spinner and arrowhead.
         */
        private int getAlpha() {
            return mAlpha;
        }

        /**
         * @param alpha Set the alpha of the progress spinner and associated arrowhead.
         */
        private void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        private float getStrokeWidth() {
            return mStrokeWidth;
        }

        /**
         * @param strokeWidth Set the stroke width of the progress spinner in pixels.
         */
        private void setStrokeWidth(float strokeWidth) {
            mStrokeWidth = strokeWidth;
            mPaint.setStrokeWidth(strokeWidth);
            invalidateSelf();
        }

        private float getStartTrim() {
            return mStartTrim;
        }

        private void setStartTrim(float startTrim) {
            mStartTrim = startTrim;
            invalidateSelf();
        }

        private float getStartingStartTrim() {
            return mStartingStartTrim;
        }

        private float getStartingEndTrim() {
            return mStartingEndTrim;
        }

        private int getStartingColor() {
            return mColors[mColorIndex];
        }

        private float getEndTrim() {
            return mEndTrim;
        }

        private void setEndTrim(float endTrim) {
            mEndTrim = endTrim;
            invalidateSelf();
        }

        public float getRotation() {
            return mRotation;
        }

        private void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        private void setInsets(int width, int height) {
            final float minEdge = (float) Math.min(width, height);
            float insets;
            if (mRingCenterRadius <= 0 || minEdge < 0) {
                insets = (float) Math.ceil(mStrokeWidth / 2.0f);
            } else {
                insets = (float) (minEdge / 2.0f - mRingCenterRadius);
            }
            mStrokeInset = insets;
        }

        private float getInsets() {
            return mStrokeInset;
        }

        private double getCenterRadius() {
            return mRingCenterRadius;
        }

        /**
         * @param centerRadius Inner radius in px of the circle the progress
         *                     spinner arc traces.
         */
        private void setCenterRadius(double centerRadius) {
            mRingCenterRadius = centerRadius;
        }

        /**
         * @param show Set to true to show the arrow head on the progress spinner.
         */
        private void setShowArrow(boolean show) {
            if (mShowArrow != show) {
                mShowArrow = show;
                invalidateSelf();
            }
        }

        /**
         * @param scale Set the scale of the arrowhead for the spinner.
         */
        private void setArrowScale(float scale) {
            if (scale != mArrowScale) {
                mArrowScale = scale;
                invalidateSelf();
            }
        }

        /**
         * @return The amount the progress spinner is currently rotated, between [0..1].
         */
        private float getStartingRotation() {
            return mStartingRotation;
        }

        /**
         * If the start / end trim are offset to begin with, store them so that
         * animation starts from that offset.
         */
        private void storeOriginals() {
            mStartingStartTrim = mStartTrim;
            mStartingEndTrim = mEndTrim;
            mStartingRotation = mRotation;
        }

        /**
         * Reset the progress spinner to default rotation, start and end angles.
         */
        private void resetOriginals() {
            mStartingStartTrim = 0;
            mStartingEndTrim = 0;
            mStartingRotation = 0;
            setStartTrim(0);
            setEndTrim(0);
            setRotation(0);
        }

        private void invalidateSelf() {
            MaterialProgressDrawable.this.invalidateSelf();
        }
    }
}