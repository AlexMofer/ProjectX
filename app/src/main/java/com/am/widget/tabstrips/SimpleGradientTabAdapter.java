package com.am.widget.tabstrips;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.am.widget.tabstrips.GradientTabStripOld.GradientTabAdapter;

/**
 * 简单的数据容器Adapter
 * 
 * @author xiangzhicheng
 * 
 */
public abstract class SimpleGradientTabAdapter implements GradientTabAdapter{

	/**
	 * 获取普通状态下的 Drawable
	 * 
	 * @param position
	 * @param context
	 * @return 必须非空
	 */
	public abstract Drawable getNormalDrawable(int position, Context context);

	/**
	 * 获取选中状态下的 Drawable
	 * 
	 * @param position
	 * @param context
	 * @return 必须非空
	 */
	public abstract Drawable getSelectedDrawable(int position, Context context);

	public boolean isTagEnable(int position) {
		return false;
	}

	public String getTag(int position) {
		return null;
	}
}
