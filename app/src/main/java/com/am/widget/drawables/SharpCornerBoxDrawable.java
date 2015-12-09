package com.am.widget.drawables;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * 尖角框
 * 
 * @author Mofer
 * 
 */
public class SharpCornerBoxDrawable extends Drawable {

	/**
	 * 方向
	 * 
	 * @author Mofer
	 * 
	 */
	public enum Direction {
		LEFT, TOP, RIGHT, BOTTOM;
	}

	/**
	 * 位置
	 * 
	 * @author Mofer
	 * 
	 */
	public enum Location {
		START, CENTER, END;
	}

	private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Rect mPaddingRect = new Rect();
	private final Path mPath = new Path();
	private final RectF mRoundRect = new RectF();
	private Point mPoint;
	private int mCornerWidth;
	private int mCornerHeight;
	private Direction mDirection;
	private Location mLocation;
	private int mPadding;
	private float mRoundRectCorner;

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight) {
		this(color, cornerWidth, cornerHeight, Direction.TOP);
	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			float roundRectCorner) {
		this(color, cornerWidth, cornerHeight, Direction.TOP, roundRectCorner);
	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			Direction direction) {
		this(color, cornerWidth, cornerHeight, direction, Location.CENTER, 0, 0);
	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			Direction direction, int padding) {
		this(color, cornerWidth, cornerHeight, direction, Location.CENTER,
				padding, 0);
	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			Direction direction, float roundRectCorner) {
		this(color, cornerWidth, cornerHeight, direction, Location.CENTER, 0,
				roundRectCorner);
	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			Direction direction, Location location, int padding) {
		this(color, cornerWidth, cornerHeight, direction, location, padding, 0);

	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			Direction direction, Location location, float roundRectCorner) {
		this(color, cornerWidth, cornerHeight, direction, location, 0,
				roundRectCorner);

	}

	public SharpCornerBoxDrawable(int color, int cornerWidth, int cornerHeight,
			Direction direction, Location location, int padding,
			float roundRectCorner) {
		mPaint.setColor(color);
		mCornerWidth = cornerWidth;
		mCornerHeight = cornerHeight;
		mDirection = direction == null ? Direction.TOP : direction;
		mLocation = location == null ? Location.CENTER : location;
		mPadding = padding;
		mRoundRectCorner = roundRectCorner;
		if (mRoundRectCorner > mPadding * 0.5f) {
			mPadding = (int) Math.ceil(mRoundRectCorner);
		}
		updatePaddingRect();
	}

	private void updatePaddingRect() {
		switch (mDirection) {
		case BOTTOM:
			mPaddingRect.set(0, 0, 0, mCornerHeight);
			break;
		case LEFT:
			mPaddingRect.set(mCornerHeight, 0, 0, 0);
			break;
		case RIGHT:
			mPaddingRect.set(0, 0, mCornerHeight, 0);
			break;
		default:
		case TOP:
			mPaddingRect.set(0, mCornerHeight, 0, 0);
			break;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		final Rect bounds = getBounds();
		if (bounds == null) {
			return;
		}
		mPath.rewind();
		switch (mDirection) {
		case BOTTOM:
			pathCornerBottom(bounds);
			break;
		case LEFT:
			pathCornerLeft(bounds);
			break;
		case RIGHT:
			pathCornerRight(bounds);
			break;
		default:
		case TOP:
			pathCornerTop(bounds);
			break;
		}
		canvas.drawPath(mPath, mPaint);
		if (mRoundRectCorner > 0) {
			mRoundRect.set(bounds.left + mPaddingRect.left, bounds.top
					+ mPaddingRect.top, bounds.right - mPaddingRect.right,
					bounds.bottom - mPaddingRect.bottom);
			canvas.drawRoundRect(mRoundRect, mRoundRectCorner,
					mRoundRectCorner, mPaint);
		} else {
			canvas.drawRect(bounds.left + mPaddingRect.left, bounds.top
					+ mPaddingRect.top, bounds.right - mPaddingRect.right,
					bounds.bottom - mPaddingRect.bottom, mPaint);
		}

	}

	private void pathCornerLeft(Rect bounds) {
		final float halfWidth = mCornerWidth * 0.5f;
		final float mCornerPointX;
		final float mCornerPointY;
		mCornerPointX = bounds.left;
		if (mPoint == null) {
			switch (mLocation) {
			default:
			case CENTER:
				mCornerPointY = bounds.centerY();
				break;
			case END:
				mCornerPointY = bounds.bottom - mPadding - halfWidth;
				break;
			case START:
				mCornerPointY = bounds.top + mPadding + halfWidth;
				break;
			}
		} else {
			mCornerPointY = mPoint.y;
		}

		mPath.moveTo(mCornerPointX, mCornerPointY);
		mPath.lineTo(mCornerPointX + mCornerHeight, mCornerPointY + halfWidth);
		mPath.lineTo(mCornerPointX + mCornerHeight, mCornerPointY - halfWidth);
		mPath.close();
	}

	private void pathCornerTop(Rect bounds) {
		final float halfWidth = mCornerWidth * 0.5f;
		final float mCornerPointX;
		final float mCornerPointY;
		if (mPoint == null) {
			switch (mLocation) {
			default:
			case CENTER:
				mCornerPointX = bounds.centerX();
				break;
			case END:
				mCornerPointX = bounds.right - mPadding - halfWidth;
				break;
			case START:
				mCornerPointX = bounds.left + mPadding + halfWidth;
				break;
			}
		} else {
			mCornerPointX = mPoint.x;
		}
		mCornerPointY = bounds.top;
		mPath.moveTo(mCornerPointX, mCornerPointY);
		mPath.lineTo(mCornerPointX + halfWidth, mCornerPointY + mCornerHeight);
		mPath.lineTo(mCornerPointX - halfWidth, mCornerPointY + mCornerHeight);
		mPath.close();
	}

	private void pathCornerRight(Rect bounds) {
		final float halfWidth = mCornerWidth * 0.5f;
		final float mCornerPointX;
		final float mCornerPointY;
		mCornerPointX = bounds.right;
		if (mPoint == null) {
			switch (mLocation) {
			default:
			case CENTER:
				mCornerPointY = bounds.centerY();
				break;
			case END:
				mCornerPointY = bounds.bottom - mPadding - halfWidth;
				break;
			case START:
				mCornerPointY = bounds.top + mPadding + halfWidth;
				break;
			}
		} else {
			mCornerPointY = mPoint.y;
		}

		mPath.moveTo(mCornerPointX, mCornerPointY);
		mPath.lineTo(mCornerPointX - mCornerHeight, mCornerPointY + halfWidth);
		mPath.lineTo(mCornerPointX - mCornerHeight, mCornerPointY - halfWidth);
		mPath.close();
	}

	private void pathCornerBottom(Rect bounds) {
		final float halfWidth = mCornerWidth * 0.5f;
		final float mCornerPointX;
		final float mCornerPointY;
		if (mPoint == null) {
			switch (mLocation) {
			default:
			case CENTER:
				mCornerPointX = bounds.centerX();
				break;
			case END:
				mCornerPointX = bounds.right - mPadding - halfWidth;
				break;
			case START:
				mCornerPointX = bounds.left + mPadding + halfWidth;
				break;
			}
		} else {
			mCornerPointX = mPoint.x;
		}
		mCornerPointY = bounds.bottom;
		mPath.moveTo(mCornerPointX, mCornerPointY);
		mPath.lineTo(mCornerPointX + halfWidth, mCornerPointY - mCornerHeight);
		mPath.lineTo(mCornerPointX - halfWidth, mCornerPointY - mCornerHeight);
		mPath.close();
	}

	@Override
	public int getIntrinsicHeight() {
		switch (mDirection) {
		case BOTTOM:
			return mCornerHeight + (int) Math.ceil(mRoundRectCorner * 2d);
		case LEFT:
			return mCornerWidth + (int) Math.ceil(mRoundRectCorner * 2d);
		case RIGHT:
			return mCornerWidth + (int) Math.ceil(mRoundRectCorner * 2d);
		default:
		case TOP:
			return mCornerHeight + (int) Math.ceil(mRoundRectCorner * 2d);
		}
	}

	@Override
	public int getIntrinsicWidth() {
		switch (mDirection) {
		case BOTTOM:
			return mCornerWidth + (int) Math.ceil(mRoundRectCorner * 2d);
		case LEFT:
			return mCornerHeight + (int) Math.ceil(mRoundRectCorner * 2d);
		case RIGHT:
			return mCornerHeight + (int) Math.ceil(mRoundRectCorner * 2d);
		default:
		case TOP:
			return mCornerWidth + (int) Math.ceil(mRoundRectCorner * 2d);
		}
	}

	@Override
	public boolean getPadding(Rect padding) {
		if (mPaddingRect != null) {
			padding.set(mPaddingRect);
			return true;
		} else {
			return super.getPadding(padding);
		}
	}

	@Override
	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mPaint.setColorFilter(cf);
		invalidateSelf();
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	public int getColor() {
		return mPaint.getColor();
	}

	public void setColor(int color) {
		if (getColor() != color) {
			mPaint.setColor(color);
			invalidateSelf();
		}
	}

	public int getCornerWidth() {
		return mCornerWidth;
	}

	public void setCornerWidth(int width) {
		if (getCornerWidth() != width) {
			this.mCornerWidth = width;
			invalidateSelf();
		}
	}

	public int getCornerHeight() {
		return mCornerHeight;
	}

	public void setCornerHeight(int height) {
		if (getCornerHeight() != height) {
			this.mCornerHeight = height;
			updatePaddingRect();
			invalidateSelf();
		}
	}

	public Direction getDirection() {
		return mDirection;
	}

	public void setDirection(Direction direction) {
		if (getDirection() != direction && direction != null) {
			this.mDirection = direction;
			updatePaddingRect();
			invalidateSelf();
		}
	}

	public Location getLocation() {
		return mLocation;
	}

	public void setLocation(Location location) {
		if (getLocation() != location && location != null) {
			this.mLocation = location;
			invalidateSelf();
		}
	}

	public int getPadding() {
		return mPadding;
	}

	public void setPadding(int padding) {
		if (getPadding() != padding) {
			this.mPadding = padding;
			invalidateSelf();
		}
	}

	public float getRoundRectCorner() {
		return mRoundRectCorner;
	}

	public void setRoundRectCorner(float corner) {
		if (getRoundRectCorner() != corner) {
			this.mRoundRectCorner = corner;
			invalidateSelf();
		}
	}

	public void setCornerPoint(Point point) {
		mPoint = point;
		invalidateSelf();
	}

}
