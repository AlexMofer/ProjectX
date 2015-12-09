package com.am.widget.tabstrips;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * Tag角标
 * 
 * @author Mofer
 *
 */
public abstract class TabTagAdapter {

	/**
	 * 角标对其方式
	 * 
	 * @author AlexMofer
	 * 
	 */
	public enum TagAlign {
		LEFTTOP(0), LEFTCENTER(1), LEFTBOTTOM(2), RIGHTTOP(3), RIGHTCENTER(4), RIGHTBOTTOM(
				5), LEFTTOPTEXT(6), LEFTCENTERTEXT(7), LEFTBOTTOMTEXT(8), RIGHTTOPTEXT(
				9), RIGHTCENTERTEXT(10), RIGHTBOTTOMTEXT(11);
		private TagAlign(int nativeInt) {
			this.nativeInt = nativeInt;
		}

		final int nativeInt;
	}
	
	private final float density;
	private final float mTextSize;
	private final GradientDrawable mBackground;
	public TabTagAdapter(Context context) {
		density = context.getResources().getDisplayMetrics().density;
		mTextSize = 10 * density;
		mBackground = new GradientDrawable();
		mBackground.setShape(GradientDrawable.RECTANGLE);
		mBackground.setColor(0xffff4444);
		mBackground.setCornerRadius(8 * density);
		mBackground.setSize((int)(10 * density), (int)(10 * density));
	}
	
	/**
	 * 获取角标文字
	 * @param position
	 * @return
	 */
	public abstract String getTag(int position);
	
	/**
	 * 是否绘制
	 * @param position
	 * @return
	 */
	public boolean isEnable(int position) {
		return true;
	}
	
	/**
	 * 获取角标位置
	 * @param position
	 * @return
	 */
	public TagAlign getTagAlign(int position) {
		return TagAlign.RIGHTTOP;
	}
	
	/**
	 * 获取文字大小
	 * @param position
	 * @return
	 */
	public float getTextSize(int position) {
		return mTextSize;
	}
	
	/**
	 * 获取背景图片
	 */
	public Drawable getBackground(int position) {
		return mBackground;
	}
	
	public float getMarginLeft(int position) {
		return 0;
	}
	
	public float getMarginTop(int position) {
		return 0;
	}
	
	public float getMarginRight(int position) {
		return 0;
	}
	
	public float getMarginBottom(int position) {
		return 0;
	}
	
	public float getPaddingLeft(int position) {
		return 0;
	}
	
	public float getPaddingTop(int position) {
		return 0;
	}
	
	public float getPaddingRight(int position) {
		return 0;
	}
	
	public float getPaddingBottom(int position) {
		return 0;
	}
}
