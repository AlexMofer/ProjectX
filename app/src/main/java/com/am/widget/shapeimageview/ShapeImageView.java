package com.am.widget.shapeimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 图形裁剪ImageView
 * <p>
 * API 21 及以上 使用 setOutlineProvider 方式实现<br>
 * 以下使用 BitmapShader 方式实现<br>
 * API 21 及以上 支持动态图
 * 
 * @author Mofer
 * 
 */
public class ShapeImageView extends ImageView {

	private ShapeCompat mShapeCompat = new ShapeCompat();
	private ImageShape mShape;

	public ShapeImageView(Context context) {
		super(context);
	}

	public ShapeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (mShapeCompat != null) {
			mShapeCompat.sizeChanged(w, h, mShape);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mShapeCompat != null) {
			if (mShapeCompat.needOnDraw(this, canvas, mShape)) {
				super.onDraw(canvas);
			}
			mShapeCompat.drawBorder(this, canvas, mShape);
		} else {
			super.onDraw(canvas);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (mShapeCompat != null) {
			mShapeCompat.detachedFromWindow(mShape);
		}
		super.onDetachedFromWindow();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		if (mShapeCompat != null) {
			mShapeCompat.setColorFilter(cf);
		}
		super.setColorFilter(cf);
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (mShapeCompat != null) {
			mShapeCompat.changeBitmap(getDrawable(), mShape);
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		if (mShapeCompat != null) {
			mShapeCompat.changeBitmap(getDrawable(), mShape);
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if (mShapeCompat != null) {
			mShapeCompat.changeBitmap(drawable, mShape);
		}
		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		if (mShapeCompat != null) {
			mShapeCompat.changeBitmap(getDrawable(), mShape);
		}
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (mShapeCompat != null) {
			mShapeCompat.setScaleType(scaleType, mShape);
		}
		super.setScaleType(scaleType);
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		if (mShapeCompat != null) {
			mShapeCompat.setPadding(left, top, right, bottom, mShape);
		}
		super.setPadding(left, top, right, bottom);
	}

	@Override
	public void setPaddingRelative(int start, int top, int end, int bottom) {
		if (mShapeCompat != null) {
			mShapeCompat.setPadding(start, top, end, bottom, mShape);
		}
		ViewCompat.setPaddingRelative(this, start, top, end, bottom);
	}

    /**
	 * 设置图像形状
     * @param shape 图像形状
     */
	public void setImageShape(ImageShape shape) {
		if (mShape != shape) {
			mShape = shape;
            mShapeCompat.init(this, mShape);
            mShapeCompat.changeBitmap(getDrawable(), mShape);
            mShapeCompat.setScaleType(getScaleType(), mShape);
            mShapeCompat.setPadding(ViewCompat.getPaddingStart(this),
                    getPaddingTop(), ViewCompat.getPaddingEnd(this),
                    getPaddingBottom(), mShape);
			invalidate();
		}
	}
}