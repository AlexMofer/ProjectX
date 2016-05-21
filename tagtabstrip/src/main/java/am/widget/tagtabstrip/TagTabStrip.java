package am.widget.tagtabstrip;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.BaseTabStrip;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * ViewPager 小点
 * @author Alex
 *
 */
public class TagTabStrip extends BaseTabStrip {

	private final static int DEFAULT_SIZE = 6;
	private Drawable mSelectedDrawable;
	private Drawable mNormalDrawable;
	private ColorStateList mColors;
	private int defaultDrawableSize = 0;
	private int drawablePadding = 0;
	private int mCurrentPager = 0;
	private int mNextPager = 0;
	private float mOffset = 1;
	private int mGravity = Gravity.CENTER;
	private float mOffsetX;
	private float mOffsetY;
	private static final int[] ATTRS = new int[]{android.R.attr.textColor,
			android.R.attr.drawablePadding};

	public TagTabStrip(Context context) {
		this(context, null);
	}

	public TagTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressWarnings("ResourceType")
	public TagTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setClickable(false);
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		mColors = a.getColorStateList(0);
		if (mColors == null) {
			mColors = ColorStateList.valueOf(a.getColor(0, Color.BLACK));
		}
		setDrawablePadding(a.getDimensionPixelSize(1, 0));
		a.recycle();
		if (defaultDrawableSize == 0) {
			setDefaultDrawableSize((int) (getResources().getDisplayMetrics().density * DEFAULT_SIZE));
		}
		if (mSelectedDrawable == null) {
			mSelectedDrawable = getDefaultDrawable(true);
		}
		if (mNormalDrawable == null) {
			mNormalDrawable = getDefaultDrawable(false);
		}
	}

	@SuppressWarnings("ResourceAsColor")
	private Drawable getDefaultDrawable(boolean selected) {
		if (selected) {
			final GradientDrawable mBackground = new GradientDrawable();
			mBackground.setShape(GradientDrawable.OVAL);
			mBackground.setColor(mColors.isStateful() ? mColors
					.getColorForState(SELECTED_STATE_SET,
							mColors.getDefaultColor()) : mColors
					.getDefaultColor());
			mBackground.setSize(defaultDrawableSize, defaultDrawableSize);
			return mBackground;
		} else {
			final GradientDrawable mBackground = new GradientDrawable();
			mBackground.setShape(GradientDrawable.OVAL);
			mBackground.setColor(mColors.getDefaultColor());
			mBackground.setSize(defaultDrawableSize, defaultDrawableSize);
			return mBackground;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
		int width;
		if (widthMode == View.MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = getTotalWidth();
			width = Math.max(width, getMinWidth());
		}
		int height;
		if (heightMode == View.MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getTotalHeight();
			height = Math.max(height, getMinHeight());

		}
		setMeasuredDimension(width, height);
		switch (GravityCompat.getAbsoluteGravity(mGravity, ViewCompat.getLayoutDirection(this))) {
			default:
			case Gravity.CENTER:
				mOffsetX = (width - getTotalWidth()) * 0.5f;
				mOffsetY = (height - getTotalHeight()) * 0.5f;
				break;
			case Gravity.CENTER_HORIZONTAL:
			case Gravity.CENTER_HORIZONTAL | Gravity.TOP:
				mOffsetX = (width - getTotalWidth()) * 0.5f;
				mOffsetY = 0;
				break;
			case Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM:
				mOffsetX = (width - getTotalWidth()) * 0.5f;
				mOffsetY = height - getTotalHeight();
				break;
			case Gravity.CENTER_VERTICAL:
			case Gravity.CENTER_VERTICAL | GravityCompat.START:
				mOffsetX = 0;
				mOffsetY = (height - getTotalHeight()) * 0.5f;
				break;
			case Gravity.CENTER_VERTICAL | GravityCompat.END:
				mOffsetX = width - getTotalWidth();
				mOffsetY = (height - getTotalHeight()) * 0.5f;
				break;
			case GravityCompat.START:
			case Gravity.TOP:
			case GravityCompat.START | Gravity.TOP:
				mOffsetX = 0;
				mOffsetY = 0;
				break;
			case GravityCompat.END:
			case GravityCompat.END | Gravity.TOP:
				mOffsetX = width - getTotalWidth();
				mOffsetY = 0;
				break;
			case GravityCompat.END | Gravity.BOTTOM:
				mOffsetX = width - getTotalWidth();
				mOffsetY = height - getTotalHeight();
				break;
			case Gravity.BOTTOM:
			case Gravity.BOTTOM | GravityCompat.START:
				mOffsetX = 0;
				mOffsetY = height - getTotalHeight();
				break;
		}
	}

	private int getTotalWidth() {
		int gap = getItemCount() > 0 ? drawablePadding * (getItemCount() - 1) : 0;
		return getMaxWidth() * getItemCount() + gap + getPaddingLeft()
				+ getPaddingRight();
	}

	private int getTotalHeight() {
		return getMaxHeight() + getPaddingTop() + getPaddingBottom();
	}

	private int getMaxWidth() {
		return mSelectedDrawable.getIntrinsicWidth() > mNormalDrawable
				.getIntrinsicWidth() ? mSelectedDrawable.getIntrinsicWidth()
				: mNormalDrawable.getIntrinsicWidth();
	}

	private int getMaxHeight() {
		return mSelectedDrawable.getIntrinsicHeight() > mNormalDrawable
				.getIntrinsicHeight() ? mSelectedDrawable.getIntrinsicHeight()
				: mNormalDrawable.getIntrinsicHeight();
	}

	private int getMinWidth() {
		int minWidth = 0;
		final Drawable bg = getBackground();
		if (bg != null) {
			minWidth = bg.getIntrinsicWidth();
		}
		return minWidth;
	}

	private int getMinHeight() {
		int minHeight = 0;
		final Drawable bg = getBackground();
		if (bg != null) {
			minHeight = bg.getIntrinsicHeight();
		}
		return minHeight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int startX = getPaddingLeft();
		int startY = getPaddingTop();
		canvas.save();
		canvas.translate(mOffsetX, mOffsetY);
		canvas.translate(startX, startY);
		float alphaNormal;
		float alphaSelected;
		for (int i = 0; i < getItemCount(); i++) {
			if (i == mNextPager) {
				alphaNormal = 1 - mOffset;
				alphaSelected = mOffset;
			} else if (i == mCurrentPager) {
				alphaNormal = mOffset;
				alphaSelected = 1 - mOffset;
			} else {
				alphaNormal = 1;
				alphaSelected = 0;
			}
			mNormalDrawable.setAlpha((int) Math.ceil(0xFF * alphaNormal));
			mSelectedDrawable.setAlpha((int) Math.ceil(0xFF * alphaSelected));
			mNormalDrawable.setBounds(0, 0,
					mNormalDrawable.getIntrinsicWidth(),
					mNormalDrawable.getIntrinsicHeight());
			mSelectedDrawable.setBounds(0, 0,
					mSelectedDrawable.getIntrinsicWidth(),
					mSelectedDrawable.getIntrinsicHeight());
			mNormalDrawable.draw(canvas);
			mSelectedDrawable.draw(canvas);
			canvas.translate(getMaxWidth() + drawablePadding, 0);
		}
		canvas.restore();
	}

	@Override
	protected void jumpTo(int current) {
		mCurrentPager = current - 1;
		mNextPager = current;
		mOffset = 1;
		invalidate();
	}

	@Override
	protected void gotoLeft(int current, int next, float offset) {
		mCurrentPager = current;
		mNextPager = next;
		mOffset = 1 - offset;
		invalidate();
	}

	@Override
	protected void gotoRight(int current, int next, float offset) {
		mCurrentPager = current;
		mNextPager = next;
		mOffset = offset;
		invalidate();
	}

	@SuppressWarnings("unused")
	public final int getDrawablePadding() {
		return drawablePadding;
	}

	public final void setDrawablePadding(int padding) {
		drawablePadding = padding;
		invalidate();
	}

	/**
	 * 获取默认图片尺寸
	 *
	 * @return 默认图片尺寸
	 */
	@SuppressWarnings("unused")
	public final int getDefaultDrawableSize() {
		return defaultDrawableSize;
	}

	/**
	 * 设置默认图片尺寸
	 *
	 * @param size 默认图片尺寸
	 */
	public final void setDefaultDrawableSize(int size) {
		defaultDrawableSize = size;
		invalidate();
	}

	/**
	 * 设置图片
	 *
	 * @param normal   普通图
	 * @param selected 选中图
	 */
	@SuppressWarnings("unused")
	public final void setDrawables(int normal, int selected) {
		setDrawables(ContextCompat.getDrawable(getContext(), normal),
				ContextCompat.getDrawable(getContext(), selected));
	}

	/**
	 * 设置图片
	 *
	 * @param normal   普通图
	 * @param selected 选中图
	 */
	public final void setDrawables(Drawable normal, Drawable selected) {
		mNormalDrawable = normal;
		mSelectedDrawable = selected;
		invalidate();
	}

	/**
	 * 获取排版方式
	 * @return 排版方式
	 */
	@SuppressWarnings("unused")
	public int getGravity() {
		return mGravity;
	}

	/**
	 * 设置排版方式
	 * @param gravity 排版方式
	 */
	@SuppressWarnings("unused")
	public void setGravity(int gravity) {
		mGravity = gravity;
	}

}
