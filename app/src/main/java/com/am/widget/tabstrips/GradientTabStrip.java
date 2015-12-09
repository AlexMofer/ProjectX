package com.am.widget.tabstrips;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.BaseTabStrip;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
/**
 * 微信式滑动渐变TabStrip，并实现点击效果，非ViewGroup实现
 * @author xiangzhicheng
 *
 */
public class GradientTabStrip extends BaseTabStrip {

	public static final int DEFAULT_TEXTSIZE = 16;// dp
	public static final int DEFAULT_TAGTEXTSIZE = 11;// dp
	public static final int DEFAULT_TAGTEXTCOLOR = 0xffffffff;
	public static final int DEFAULT_TAGPADDING = 6;
	private final TextPaint mTextPaint;
	private static final int[] ATTRS = new int[] { android.R.attr.textSize,
			android.R.attr.textColor, android.R.attr.drawablePadding };
	private int mGravity = Gravity.CENTER;
	private ColorStateList mTextColor;
	private int mTextColorNormal;
	private int mTextColorSelected;
	private float mTextColorOffset = 1;
	private float mTextSize;
	private float mItemWidth;
	private int mDrawableHeight;
	private int mDrawablePadding;
	private boolean includepad = true;
	private int mTextHeight;
	private int mDesc;
	private float mTopOffset = 0;
	private float mTagTextSize;
	private int mTagTextColor = DEFAULT_TAGTEXTCOLOR;
	private int mTagTextHeight;
	private int mTagDesc;
	private int mTagPadding;
	private Drawable mTagBackground;
	private final Rect mRefreshRect = new Rect();
	private GradientTabAdapter mAdapter;
	private int mCurrectPager = 0;
	private int mNextPager = 0;

	public GradientTabStrip(Context context) {
		this(context, null);
	}

	public GradientTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public GradientTabStrip(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		final float density = getResources().getDisplayMetrics().density;
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.CENTER);
		if (Build.VERSION.SDK_INT > 4) {
			mTextPaint.density = density;
		}
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		int textSize = a.getDimensionPixelSize(0,
				(int) (DEFAULT_TEXTSIZE * density));
		ColorStateList colors = a.getColorStateList(1);
		if (colors == null) {
			colors = ColorStateList.valueOf(a.getColor(1, Color.BLACK));
		}
		mDrawablePadding = a.getDimensionPixelSize(2, 0);
		a.recycle();
		setTextSize(textSize);
		setTextColor(colors);
		if (mTagTextSize == 0) {
			mTagTextSize = DEFAULT_TAGTEXTSIZE
					* context.getResources().getDisplayMetrics().density;
		}
		if (mTagBackground == null) {
			mTagBackground = getDefaultTagBackground();
		}
		mTagPadding = (int) (DEFAULT_TAGPADDING * density);
		setClickable(true);
	}

	/**
	 * 获取默认Tag背景
	 * 
	 * @return
	 */
	private Drawable getDefaultTagBackground() {
		final float density = getResources().getDisplayMetrics().density;
		final GradientDrawable mBackground = new GradientDrawable();
		mBackground.setShape(GradientDrawable.RECTANGLE);
		mBackground.setColor(0xffff4444);
		mBackground.setCornerRadius(10 * density);
		mBackground.setSize((int) (10 * density), (int) (10 * density));
		return mBackground;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException("Must measure with an exact width");
		}
		mItemWidth = (float) (widthSize - getPaddingLeft() - getPaddingRight())
				/ (getViewTabs().size() <= 0 ? 1 : getViewTabs().size());
		mTextPaint.setTextSize(mTextSize);
		FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
		if (includepad) {
			mTextHeight = metrics.bottom - metrics.top;
			mDesc = mTextHeight + metrics.top;
		} else {
			mTextHeight = metrics.descent - metrics.ascent;
			mDesc = mTextHeight + metrics.ascent;
		}
		if (mAdapter != null) {
			mDrawableHeight = mAdapter.getSelectedDrawable(mCurrectPager,
					getContext()).getIntrinsicHeight();
		} else {
			mDrawableHeight = -mDrawablePadding;
		}
		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
			setMeasuredDimension(widthSize, heightSize);
		} else {
			height = mDrawableHeight + mDrawablePadding + mTextHeight
					+ getPaddingTop() + getPaddingBottom();
			height = Math.max(height, getMinHeight());
			setMeasuredDimension(widthSize, height);
		}
		switch (mGravity) {
		default:
		case Gravity.CENTER:
			mTopOffset = (height - mDrawableHeight - mDrawablePadding
					- mTextHeight - getPaddingTop() - getPaddingBottom()) * 0.5f;
			break;
		case Gravity.TOP:
			mTopOffset = 0;
			break;
		case Gravity.BOTTOM:
			mTopOffset = height - mDrawableHeight - mDrawablePadding
					- mTextHeight - getPaddingTop() - getPaddingBottom();
			break;
		}
		mTextPaint.setTextSize(mTagTextSize);
		FontMetricsInt tagMetrics = mTextPaint.getFontMetricsInt();

		mTagTextHeight = tagMetrics.descent - tagMetrics.ascent;
		mTagDesc = (int) (mTagTextHeight - (-tagMetrics.ascent
				- tagMetrics.descent + (tagMetrics.bottom - tagMetrics.descent)
				* getResources().getDisplayMetrics().density));
		mTagTextHeight += mTagDesc;
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
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.save();
		for (Drawable tag : getTabDrawables()) {
			if (tag != null) {
				tag.setBounds(0, 0, (int) mItemWidth, getHeight());
				tag.draw(canvas);
			}
			canvas.translate(mItemWidth, 0);
		}
		canvas.restore();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		final float x = getPaddingLeft() + mItemWidth / 2;
		final float y = getPaddingTop() + mDrawableHeight + mDrawablePadding
				+ mTopOffset;
		final int compoundTopOffset = -mDrawableHeight - mDrawablePadding;
		float alphaNormal;
		float alphaSelected;
		canvas.translate(x, y);
		int position = 0;
		for (String text : getViewTabs()) {
			mTextPaint.setColor(mTextColorNormal);
			mTextPaint.setTextSize(mTextSize);
			if (position == mNextPager) {
				mTextPaint.setColor(getColor(mTextColorNormal,
						mTextColorSelected, mTextColorOffset));
			} else if (position == mCurrectPager) {
				mTextPaint.setColor(getColor(mTextColorNormal,
						mTextColorSelected, 1 - mTextColorOffset));
			} else {
				mTextPaint.setColor(mTextColorNormal);
			}
			canvas.drawText(text, 0, mTextHeight - mDesc, mTextPaint);
			if (mAdapter != null) {
				final Drawable normalDrawable = mAdapter.getNormalDrawable(
						position, getContext());
				final Drawable seletedDrawable = mAdapter.getSelectedDrawable(
						position, getContext());
				if (position == mNextPager) {
					alphaNormal = 1 - mTextColorOffset;
					alphaSelected = mTextColorOffset;
				} else if (position == mCurrectPager) {
					alphaNormal = mTextColorOffset;
					alphaSelected = 1 - mTextColorOffset;
				} else {
					alphaNormal = 1;
					alphaSelected = 0;
				}
				normalDrawable.setAlpha((int) Math.ceil((0xFF - 0x00)
						* alphaNormal));
				seletedDrawable.setAlpha((int) Math.ceil((0xFF - 0x00)
						* alphaSelected));
				canvas.translate(normalDrawable.getIntrinsicWidth() * 0.5f, 0);
				normalDrawable
						.setBounds(-normalDrawable.getIntrinsicWidth(),
								compoundTopOffset, 0,
								normalDrawable.getIntrinsicHeight()
										+ compoundTopOffset);
				seletedDrawable
						.setBounds(-normalDrawable.getIntrinsicWidth(),
								compoundTopOffset, 0,
								normalDrawable.getIntrinsicHeight()
										+ compoundTopOffset);
				normalDrawable.draw(canvas);
				seletedDrawable.draw(canvas);
				if (mAdapter.isTagEnable(position)) {
					mTextPaint.setColor(mTagTextColor);
					mTextPaint.setTextSize(mTagTextSize);
					int textWidth;
					int textHeight = mTagTextHeight;
					String tag = mAdapter.getTag(position) == null ? ""
							: mAdapter.getTag(position);
					if ("".equals(tag)) {
						textHeight = 0;
					}
					textWidth = (int) (Math.ceil(mTextPaint.measureText(tag)));
					int drawableWidth = mTagBackground == null ? 0
							: mTagBackground.getMinimumWidth();
					int drawableHeight = mTagBackground == null ? 0
							: mTagBackground.getMinimumHeight();
					drawableWidth = Math.max(textWidth, drawableWidth);
					drawableHeight = Math.max(textHeight, drawableHeight);
					if (drawableWidth > drawableHeight) {
						drawableWidth = drawableWidth + mTagPadding;
					} else {
						drawableWidth = drawableHeight;
					}
					final float move = drawableWidth * 0.5f;
					canvas.translate(-move, 0);
					if (mTagBackground != null) {
						mTagBackground.setBounds(0, compoundTopOffset,
								drawableWidth, drawableHeight
										+ compoundTopOffset);
						mTagBackground.draw(canvas);
					}
					canvas.translate(move, 0);
					canvas.drawText(tag, 0, mTagTextHeight - mTagDesc
							+ compoundTopOffset, mTextPaint);
				}
				canvas.translate(-normalDrawable.getIntrinsicWidth() * 0.5f, 0);
			}

			position++;
			canvas.translate(mItemWidth, 0);
		}
		canvas.restore();
	}

	private int getColor(int normalColor, int selectedColor, float offset) {
		int normalAlpha = Color.alpha(normalColor);
		int normalRed = Color.red(normalColor);
		int normalGreen = Color.green(normalColor);
		int normalBlue = Color.blue(normalColor);
		int selectedAlpha = Color.alpha(selectedColor);
		int selectedRed = Color.red(selectedColor);
		int selectedGreen = Color.green(selectedColor);
		int selectedBlue = Color.blue(selectedColor);
		int a = (int) Math.ceil((selectedAlpha - normalAlpha) * offset);
		int r = (int) Math.ceil((selectedRed - normalRed) * offset);
		int g = (int) Math.ceil((selectedGreen - normalGreen) * offset);
		int b = (int) Math.ceil((selectedBlue - normalBlue) * offset);
		return Color.argb(normalAlpha + a, normalRed + r, normalGreen + g,
				normalBlue + b);
	}

	@Override
	protected void drawableStateChanged() {
		int position = downPointToPosition();
		if (position >= 0 && position < getTabDrawables().size()) {
			Drawable tag = getTabDrawables().get(position);
			DrawableCompat.setHotspot(tag, downPointX() - mItemWidth * position, downPointY());
			if (tag != null && tag.isStateful()) {
				tag.setState(getDrawableState());
			}
		}

		super.drawableStateChanged();

	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		boolean isTag = false;
		for (Drawable tag : getTabDrawables()) {
			if (who == tag) {
				isTag = true;
				break;
			}
		}
		return isTag || super.verifyDrawable(who);
	}

	@Override
	protected void jumpTo(int currect) {
		mRefreshRect.set((int) Math.floor(mItemWidth * mNextPager),
				getPaddingTop(), (int) Math.ceil(mItemWidth * (currect + 1)),
				getHeight() - getPaddingBottom());
		mCurrectPager = currect - 1;
		mNextPager = currect;
		mTextColorOffset = 1;
		invalidate(mRefreshRect);
	}

	@Override
	protected void gotoLeft(int currect, int next, float offset) {
		mCurrectPager = currect;
		mNextPager = next;
		mTextColorOffset = 1 - offset;
		mRefreshRect.set((int) Math.floor(mItemWidth * mNextPager),
				getPaddingTop(),
				(int) Math.ceil(mItemWidth * (mCurrectPager + 1)), getHeight()
						- getPaddingBottom());
		invalidate(mRefreshRect);
	}

	@Override
	protected void gotoRight(int currect, int next, float offset) {
		mCurrectPager = currect;
		mNextPager = next;
		mTextColorOffset = offset;
		mRefreshRect.set((int) Math.floor(mItemWidth * mCurrectPager),
				getPaddingTop(),
				(int) Math.ceil(mItemWidth * (mNextPager + 1)), getHeight()
						- getPaddingBottom());
		invalidate(mRefreshRect);
	}

	public final GradientTabAdapter getAdapter() {
		return mAdapter;
	}

	public final void setAdapter(GradientTabAdapter adapter) {
		if (mAdapter != adapter) {
			mAdapter = adapter;
			requestLayout();
			invalidate();
		}
	}

	@Override
	protected int pointToPosition(float x, float y) {
		int position = 0;
		for (int i = 0; i < getViewTabs().size(); i++) {
			float l = getPaddingLeft() + mItemWidth * i;
			float r = l + mItemWidth;
			if (x >= l && x <= r) {
				position = i;
				break;
			}
		}
		return position;
	}

	public final int getGravity() {
		return mGravity;
	}

	public final void setGravity(int gravity) {
		if (mGravity != gravity) {
			mGravity = gravity;
			invalidate();
		}
	}

	public float getTagTextSize() {
		return mTagTextSize;
	}

	public void setTagTextSize(float tagTextSize) {
		if (mTagTextSize != tagTextSize) {
			mTagTextSize = tagTextSize;
			invalidate();
		}
	}

	public ColorStateList getTextColor() {
		return mTextColor;
	}

	public void setTextColor(ColorStateList color) {
		if (color != null && mTextColor != color) {
			mTextColor = color;
			mTextColorNormal = mTextColor.getDefaultColor();
			mTextColorSelected = mTextColor.getColorForState(
					SELECTED_STATE_SET, mTextColorNormal);
			invalidate();
		}
	}

	public final float getTextSize() {
		return mTextSize;
	}

	public final void setTextSize(int textSize) {
		if (mTextSize != textSize) {
			mTextSize = textSize;
			requestLayout();
			invalidate();
		}
	}

	public int getTagTextColor() {
		return mTagTextColor;
	}

	public void setTagTextColor(int color) {
		if (mTagTextColor != color) {
			mTagTextColor = color;
			invalidate();
		}
	}

	public Drawable getTagBackground() {
		return mTagBackground;
	}

	public void setTagBackground(Drawable background) {
		if (mTagBackground != background) {
			mTagBackground = background;
			invalidate();
		}
	}
	
	/**
	 * 数据容器Adapter
	 * 
	 * @author xiangzhicheng
	 * 
	 */
	public interface GradientTabAdapter {

		/**
		 * 获取普通状态下的 Drawable
		 * 
		 * @param position
		 * @param context
		 * @return 必须非空
		 */
		public Drawable getNormalDrawable(int position, Context context);

		/**
		 * 获取选中状态下的 Drawable
		 * 
		 * @param position
		 * @param context
		 * @return 必须非空
		 */
		public Drawable getSelectedDrawable(int position, Context context);

		public boolean isTagEnable(int position);

		public String getTag(int position);
	}

}
