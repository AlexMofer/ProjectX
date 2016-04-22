/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.am.widget.tabstrips;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.BaseTabStrip;
import android.text.TextPaint;
import android.util.AttributeSet;


/**
 * GradientPagerTabStrip ，其Tabs固定于ViewPager上方或者下方，ViewPager滑动对应变化效果。不建议Tabs大于5个
 * 
 * @author Alex
 * 
 */
public class GradientPagerTabStrip extends BaseTabStrip {

	final float density;
	private float itemWidth;

	private float textTop;// 文字最大高度
	private boolean showTextGradient = true;// 显示文字颜色渐变
	private int textColorNormal;// 文字普通颜色
	private int textColorSelected;// 文字选中时颜色
	private boolean showTextScale = true;// 显示文字缩放
	private float textSize;// 文字大小
	private float magnification = 0.2f;
	private float textSizeOffset = 1;

	private int intervalWidth;

	private boolean showIndicator = true;
	private int indicatorPadding;
	private int indicatorHeight;
	private int indicatorColor = Color.BLACK;
	private float indicatorOffset = 0;

	private boolean showUnderLine = true;
	private int underLineHeight;
	private int underLineColor = Color.BLACK;

	private boolean showTabColor = false;
	private int tabColorSelected = Color.DKGRAY;
	private int tabColorNormal = Color.argb(Color.alpha(0x80000000),
			Color.red(tabColorSelected), Color.green(tabColorSelected),
			Color.blue(tabColorSelected));
	private TabTagAdapter mTagAdapter;
	private final Rect mRefreshRect = new Rect();
	private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

	private int currectPager = 0;
	private int nextPager = 0;

	private static final int[] ATTRS = new int[] { android.R.attr.textSize,
			android.R.attr.textColor };

	public GradientPagerTabStrip(Context context) {
		this(context, null);
	}

	public GradientPagerTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	@SuppressWarnings("ResourceType")
	public GradientPagerTabStrip(Context context, AttributeSet attrs,
								 int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		density = getResources().getDisplayMetrics().density;
		final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		textSize = a.getDimensionPixelSize(0, (int) (16 * density));
		textColorSelected = a.getColor(1, Color.BLACK);
		textColorNormal = Color.argb(Color.alpha(0x80000000),
				Color.red(textColorSelected), Color.green(textColorSelected),
				Color.blue(textColorSelected));
		a.recycle();
		mTextPaint.setAntiAlias(true);

		intervalWidth = (int) (0 * density);
		indicatorPadding = (int) (10 * density);
		indicatorHeight = (int) (3 * density);
		underLineHeight = (int) (2 * density);
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
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthMode != MeasureSpec.EXACTLY) {
			// TODO 使用不确定宽度处理
			throw new IllegalStateException("Must measure with an exact width");
		}
		itemWidth = (float) (widthSize - getPaddingLeft() - getPaddingRight() - intervalWidth
				* (getViewTabs().size() <= 0 ? 1 : getViewTabs().size() - 1))
				/ (getViewTabs().size() <= 0 ? 1 : getViewTabs().size());
		mTextPaint.setTextSize(magnification <= 0 ? textSize : textSize
				+ textSize * magnification);
		// TODO 当Tab文字长度超过itemWidth时需处理
		FontMetrics fontMetrics = mTextPaint.getFontMetrics();
		float textHeight = fontMetrics.descent - fontMetrics.ascent;
		textTop = textHeight
				- (-fontMetrics.ascent - fontMetrics.descent + (fontMetrics.bottom - fontMetrics.descent)
						* density);
		textHeight += textTop;
        int itemHeight = (int) Math.ceil(textHeight + indicatorHeight
				+ underLineHeight);
		int minHeight = getMinHeight();
		if (minHeight > itemHeight + getPaddingTop() + getPaddingBottom()) {
			itemHeight = minHeight - getPaddingTop() - getPaddingBottom();
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			setMeasuredDimension(widthSize, heightSize);
		} else {
			setMeasuredDimension(widthSize, itemHeight + getPaddingTop()
					+ getPaddingBottom());
		}

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
		drawTabBG(canvas);
		drawText(canvas);
		drawTag(canvas);
		drawIndicator(canvas);
		drawUnderLine(canvas);
		super.onDraw(canvas);
	}

	@Override
	protected void jumpTo(int currect) {
		indicatorOffset = 1;
		currectPager = currect - 1;
		nextPager = currect;
		textSizeOffset = 1;
		invalidate();
	}

	@Override
	protected void gotoLeft(int currect, int next, float mPositionOffset) {
		indicatorOffset = mPositionOffset - 1;
		currectPager = currect;
		nextPager = next;
		textSizeOffset = 1 - mPositionOffset;
		invalidate();
	}

	@Override
	protected void gotoRight(int currect, int next, float mPositionOffset) {
		indicatorOffset = mPositionOffset;
		currectPager = currect;
		nextPager = next;
		textSizeOffset = mPositionOffset;
		invalidate();
	}

	/**
	 * 绘制Tab背景
	 * 
	 * @param canvas Canvas
	 */
	private void drawTabBG(Canvas canvas) {
		if (showTabColor) {
			mTextPaint.setColor(tabColorNormal);
			canvas.save();
			canvas.translate(getPaddingLeft(), 0);
			if (getViewTabs().size() > 0)
				for (int i = 0; i < getViewTabs().size(); i++) {
					if (i == nextPager) {
						mTextPaint.setColor(getColor(tabColorNormal,
								tabColorSelected, textSizeOffset));
					} else if (i == currectPager) {
						mTextPaint.setColor(getColor(tabColorNormal,
								tabColorSelected, 1 - textSizeOffset));
					} else {
						mTextPaint.setColor(tabColorNormal);
					}
					canvas.drawRect(0, getPaddingTop(), itemWidth, getHeight()
							- getPaddingBottom(), mTextPaint);
					canvas.translate(itemWidth + intervalWidth, 0);
				}
			else {
				mTextPaint.setColor(tabColorSelected);
				canvas.drawRect(0, getPaddingTop(), itemWidth + intervalWidth
						+ getPaddingLeft(), getHeight() - getPaddingBottom(),
						mTextPaint);
			}
			canvas.restore();
		}
	}

	/**
	 * 绘制文字
	 * 
	 * @param canvas Canvas
	 */
	private void drawText(Canvas canvas) {
		float x = getPaddingLeft() + itemWidth / 2;
		int y = getHeight() - getPaddingBottom() - indicatorHeight
				- underLineHeight;
		int position = 0;
		for (String text : getViewTabs()) {
			canvas.save();
			mTextPaint.setColor(textColorNormal);
			mTextPaint.setTextAlign(Align.LEFT);
			mTextPaint.setTextSize(textSize);
			if (showTextGradient) {
				if (position == nextPager) {
					mTextPaint.setColor(getColor(textColorNormal,
							textColorSelected, textSizeOffset));
				} else if (position == currectPager) {
					mTextPaint.setColor(getColor(textColorNormal,
							textColorSelected, 1 - textSizeOffset));
				} else {
					mTextPaint.setColor(textColorNormal);
				}
			}
			if (showTextScale) {
				if (position == nextPager) {
					canvas.translate(x - mTextPaint.measureText(text)
							* (1 + textSizeOffset * magnification) / 2, y
							- textTop);
					canvas.scale(1 + textSizeOffset * magnification, 1
							+ textSizeOffset * magnification);
				} else if (position == currectPager) {
					canvas.translate(x - mTextPaint.measureText(text)
							* (1 + (1 - textSizeOffset) * magnification) / 2, y
							- textTop);
					canvas.scale(1 + (1 - textSizeOffset) * magnification, 1
							+ (1 - textSizeOffset) * magnification);
				} else {
					canvas.translate(x - mTextPaint.measureText(text) / 2, y
							- textTop);
					canvas.scale(1, 1);
				}
			} else {
				canvas.translate(x - mTextPaint.measureText(text) / 2, y
						- textTop);
			}
			canvas.drawText(text, 0, 0, mTextPaint);
			x += itemWidth + intervalWidth;
			position++;
			canvas.restore();
		}
	}

	/**
	 * 绘制标记
	 * 
	 * @param canvas Canvas
	 */
	private void drawTag(Canvas canvas) {
		// TODO
		if (mTagAdapter != null) {
			canvas.save();
			float canvasOffset = 0;
			int x = getPaddingLeft();
			int y = getPaddingTop();
			float textTop;
			mTextPaint.setColor(Color.WHITE);
			mTextPaint.setTextAlign(Align.LEFT);
			for (int position = 0; position < getViewTabs().size(); position++) {
				canvas.translate(canvasOffset, 0);
				canvasOffset = itemWidth + intervalWidth;
				canvas.save();
				if (mTagAdapter.isEnable(position)) {
					int textWidth;
					int textHeight;
					mTextPaint.setTextSize(mTagAdapter.getTextSize(position));
					String tag = mTagAdapter.getTag(position) == null ? ""
							: mTagAdapter.getTag(position);
					textWidth = (int) Math.ceil(mTextPaint.measureText(tag));
					FontMetrics fontMetrics = mTextPaint.getFontMetrics();
					textHeight = (int) Math.ceil(fontMetrics.descent
							- fontMetrics.ascent);
					textTop = textHeight
							- (-fontMetrics.ascent - fontMetrics.descent + (fontMetrics.bottom - fontMetrics.descent)
									* density);
					textHeight += textTop;
					if ("".equals(tag)) {
						textHeight = 0;
					}
					final Drawable drawable = mTagAdapter
							.getBackground(position);
					int drawableWidth = drawable == null ? 0 : drawable
							.getMinimumWidth();
					int drawableHeight = drawable == null ? 0 : drawable
							.getMinimumHeight();
					float offsetX = Math.max(
							textWidth + mTagAdapter.getPaddingLeft(position)
									+ mTagAdapter.getPaddingRight(position),
							drawableWidth);
					float offsetTextX = (offsetX - (textWidth
							+ mTagAdapter.getPaddingLeft(position) + mTagAdapter
							.getPaddingRight(position))) * 0.5f;
					float offsetY = Math.max(
							textHeight + mTagAdapter.getPaddingBottom(position)
									+ mTagAdapter.getPaddingTop(position),
							drawableHeight);
					float offsetTextY = (offsetY - (textHeight
							+ mTagAdapter.getPaddingBottom(position) + mTagAdapter
							.getPaddingTop(position))) * 0.5f;

					mTextPaint.setTextSize(showTextScale ? textSize
							* (1 + magnification) : textSize);
					float myTextWidth = mTextPaint.measureText(getViewTabs()
							.get(position));
					TabTagAdapter.TagAlign ta = mTagAdapter.getTagAlign(position);
					switch (ta) {
					default:
					case LEFTTOP:
						canvas.translate(mTagAdapter.getMarginLeft(position),
								mTagAdapter.getMarginTop(position));
						break;
					case LEFTCENTER:
						canvas.translate(mTagAdapter.getMarginLeft(position),
								(getHeight() - underLineHeight - offsetY) / 2);
						break;
					case LEFTBOTTOM:
						canvas.translate(mTagAdapter.getMarginLeft(position),
								getHeight() - offsetY - underLineHeight
										- mTagAdapter.getMarginBottom(position));
						break;
					case RIGHTTOP:
						canvas.translate(
								itemWidth - offsetX
										- mTagAdapter.getMarginRight(position),
								mTagAdapter.getMarginTop(position));
						break;
					case RIGHTCENTER:
						canvas.translate(
								itemWidth - offsetX
										- mTagAdapter.getMarginRight(position),
								(getHeight() - underLineHeight - offsetY) / 2);
						break;
					case RIGHTBOTTOM:
						canvas.translate(
								itemWidth - offsetX
										- mTagAdapter.getMarginRight(position),
								getHeight() - offsetY - underLineHeight
										- mTagAdapter.getMarginBottom(position));
						break;
					case LEFTTOPTEXT:
						canvas.translate(
								(itemWidth - myTextWidth) / 2 - offsetX
										- mTagAdapter.getMarginRight(position),
								mTagAdapter.getMarginTop(position));
						break;
					case LEFTCENTERTEXT:
						canvas.translate(
								(itemWidth - myTextWidth) / 2 - offsetX
										- mTagAdapter.getMarginRight(position),
								(getHeight() - underLineHeight - offsetY) / 2);
						break;
					case LEFTBOTTOMTEXT:
						canvas.translate(
								(itemWidth - myTextWidth) / 2 - offsetX
										- mTagAdapter.getMarginRight(position),
								getHeight() - offsetY - underLineHeight
										- mTagAdapter.getMarginBottom(position));
						break;
					case RIGHTTOPTEXT:
						canvas.translate((itemWidth + myTextWidth) / 2
								+ mTagAdapter.getMarginLeft(position),
								mTagAdapter.getMarginTop(position));
						break;
					case RIGHTCENTERTEXT:
						canvas.translate((itemWidth + myTextWidth) / 2
								+ mTagAdapter.getMarginLeft(position),
								(getHeight() - underLineHeight - offsetY) / 2);
						break;

					case RIGHTBOTTOMTEXT:
						canvas.translate((itemWidth + myTextWidth) / 2
								+ mTagAdapter.getMarginLeft(position),
								getHeight() - offsetY - underLineHeight
										- mTagAdapter.getMarginBottom(position));
						break;

					}
					mTextPaint.setTextSize(mTagAdapter.getTextSize(position));
					if (drawable != null) {
						drawable.setBounds(x, y, (int) (x + offsetX),
								(int) (y + offsetY));
						drawable.draw(canvas);
					}
					canvas.drawText(
							tag,
							x + offsetTextX
									+ mTagAdapter.getPaddingLeft(position), y
									+ offsetY - textTop - offsetTextY
									- mTagAdapter.getPaddingTop(position),
							mTextPaint);
				}
				canvas.restore();
			}
			canvas.restore();
		}
	}

	/**
	 * 绘制指示标
	 * 
	 * @param canvas Canvas
	 */
	private void drawIndicator(Canvas canvas) {

		canvas.save();
		canvas.translate(getPaddingLeft() + currectPager
				* (itemWidth + intervalWidth) + indicatorPadding
				+ indicatorOffset * (itemWidth + intervalWidth), getHeight()
				- underLineHeight - getPaddingBottom() - indicatorHeight);
		if (showIndicator) {
			mTextPaint.setColor(indicatorColor);
			if (getViewTabs().size() > 1)
				canvas.drawRect(0, 0, itemWidth - 2 * indicatorPadding,
						indicatorHeight, mTextPaint);
			else if (getViewTabs().size() > 0)
				canvas.drawRect(itemWidth / 4, 0, itemWidth - itemWidth / 4 - 2
						* indicatorPadding, indicatorHeight, mTextPaint);
			else
				canvas.drawRect(itemWidth / 4, 0, itemWidth - itemWidth / 4 -
						indicatorPadding, indicatorHeight, mTextPaint);
		}
		canvas.restore();
	}

	/**
	 * 绘制下划线
	 * 
	 * @param canvas Canvas
	 */
	private void drawUnderLine(Canvas canvas) {
		if (showUnderLine) {
			mTextPaint.setColor(underLineColor);
			canvas.drawRect(getPaddingLeft(), getHeight() - underLineHeight
					- getPaddingBottom(), getWidth() - getPaddingRight(),
					getHeight() - getPaddingBottom(), mTextPaint);
		}
	}

	/**
	 * 设置Tabs之间的间隙
	 * 
	 * @param intervalWidth 间隔
	 */
	public void setTabsInterval(int intervalWidth) {
		this.intervalWidth = intervalWidth;
		invalidate();
	}

	/**
	 * @return Tabs之间的间隙
	 */
	public int getTabsInterval() {
		return intervalWidth;
	}

	/**
	 * 显示标签下标
	 * 
	 * @param show 显示下标
	 */
	public void showTabIndicator(boolean show) {
		showIndicator = show;
		invalidate();
	}

	/**
	 * 设置标签下标
	 * 
	 * @param color
	 *            下标颜色
	 * @param height
	 *            下标高度
	 * @param padding
	 *            下标两端padding
	 */
	public void setTabIndicator(int color, int height, int padding) {
		indicatorColor = color;
		indicatorHeight = height;
		indicatorPadding = padding;
		invalidate();
	}

	/**
	 * 显示下划线
	 * 
	 * @param show 显示下划线
	 */
	public void showUnderline(boolean show) {
		showUnderLine = show;
		invalidate();
	}

	/**
	 * 设置下划线
	 * 
	 * @param color
	 *            下划线颜色
	 * @param height
	 *            下划线高度
	 */
	public void setUnderline(int color, int height) {
		underLineColor = color;
		underLineHeight = height;
		invalidate();
	}

	/**
	 * 显示文字颜色渐变
	 * 
	 * @param show 显示文字渐变
	 */
	public void showTextGradient(boolean show) {
		showTextGradient = show;
		invalidate();
	}

	/**
	 * 设置文字颜色
	 * 
	 * @param colorNormal
	 *            普通颜色
	 * @param colorSelected
	 *            选中颜色
	 */
	public void setTextGradient(int colorNormal, int colorSelected) {
		textColorNormal = colorNormal;
		textColorSelected = colorSelected;
		invalidate();
	}

	public void showTextScale(boolean show) {
		showTextScale = show;
		invalidate();
	}

	/**
	 * 设置字体大小
	 * 
	 * @param size 设置文字大小
	 */
	public void setTextSize(float size) {
		textSize = size;
		invalidate();
	}

	/**
	 * 设置文字放大发倍数 默认值1.2
	 * 
	 * @param magnification 文字缩放比
	 */
	public void setMagnification(float magnification) {
		if (magnification == 1)
			showTextScale(false);
		else {
			this.magnification = magnification - 1;
			invalidate();
		}
	}

	/**
	 * 是否显示Tab背景颜色
	 * 
	 * @param show 显示背景
	 */
	public void showTabGradient(boolean show) {
		showTabColor = show;
		invalidate();
	}

	/**
	 * 设置Tab背景颜色
	 * 
	 * @param colorNormal
	 *            普通颜色
	 * @param colorSelected
	 *            选中颜色
	 */
	public void setTabGradient(int colorNormal, int colorSelected) {
		tabColorNormal = colorNormal;
		tabColorSelected = colorSelected;
		invalidate();
	}

	/**
	 * 设置角标Adapter
	 * 
	 * @param adapter Adapter
	 */
	public void setTagAdapter(TabTagAdapter adapter) {
		mTagAdapter = adapter;
		invalidate();
	}

	@Override
	protected int pointToPosition(float x, float y) {
		int position = 0;
		for (int i = 0; i < getViewTabs().size(); i++) {
			float l = getPaddingLeft() + intervalWidth * i + itemWidth * i;
			float r = l + itemWidth;
			if (x >= l && x <= r) {
				position = i;
				break;
			}
		}
		return position;
	}

}