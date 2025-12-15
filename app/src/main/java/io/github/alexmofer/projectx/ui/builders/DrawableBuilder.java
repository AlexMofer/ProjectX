package io.github.alexmofer.projectx.ui.builders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import io.github.alexmofer.android.support.utils.ColorUtils;

/**
 * Drawable 构建器
 * Created by Alex on 2025/6/24.
 */
public final class DrawableBuilder {

    private DrawableBuilder() {
        //no instance
    }

    private static ColorStateList getWhiteRippleColor() {
        return ColorStateList.valueOf(ColorUtils.getColor(Color.WHITE, 0.25f));
    }

    /**
     * 新建白色可点击子项背景
     *
     * @return 可点击子项背景
     */
    public static RippleDrawable newClickableItemWhite(Drawable content) {
        if (content instanceof GradientDrawable) {
            // 只做一层兼容，Android 12 及以前版本不支持路径裁剪
            try {
                final float[] radii = ((GradientDrawable) content).getCornerRadii();// 低版本该接口会空指针异常
                if (radii != null) {
                    final GradientDrawable mask = new GradientDrawable();
                    mask.setColor(Color.WHITE);
                    mask.setCornerRadii(radii);
                    return new RippleDrawable(getWhiteRippleColor(), content, mask);
                }
            } catch (Throwable t) {
                // ignore
            }
        }
        return new RippleDrawable(getWhiteRippleColor(), content, new ColorDrawable(Color.WHITE));
    }

    /**
     * 新建白色可点击子项背景
     *
     * @return 可点击子项背景
     */
    public static RippleDrawable newClickableItemWhite(@ColorInt int color) {
        return newClickableItemWhite(new ColorDrawable(color));
    }

    /**
     * 新建分割器
     *
     * @param color  颜色
     * @param width  宽度，-1表示占满
     * @param height 高度，-1表示占满
     * @return 分割器
     */
    public static GradientDrawable newDivider(@ColorInt int color, int width, int height) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setSize(width, height);
        return drawable;
    }

    /**
     * 新建垂直分割器
     *
     * @param color  颜色
     * @param height 高度
     * @return 垂直分割器
     */
    public static GradientDrawable newDividerVertical(@ColorInt int color, int height) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setSize(-1, height);
        return drawable;
    }

    /**
     * 新建透明垂直分割器
     *
     * @param height 高度
     * @return 垂直分割器
     */
    public static GradientDrawable newDividerVertical(int height) {
        return newDividerVertical(Color.TRANSPARENT, height);
    }

    /**
     * 新建水平分割器
     *
     * @param color 颜色
     * @param width 宽度
     * @return 水平分割器
     */
    public static GradientDrawable newDividerHorizontal(@ColorInt int color, int width) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setSize(width, -1);
        return drawable;
    }

    /**
     * 新建透明水平分割器
     *
     * @param width 宽度
     * @return 水平分割器
     */
    public static GradientDrawable newDividerHorizontal(int width) {
        return newDividerHorizontal(Color.TRANSPARENT, width);
    }

    /**
     * 新建水平渐变
     *
     * @param colorStart 起始颜色
     * @param colorEnd   结束颜色
     * @return 水平渐变
     */
    public static GradientDrawable newGradientHorizontal(@ColorInt int colorStart,
                                                         @ColorInt int colorEnd) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{colorStart, colorEnd});
        drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        return drawable;
    }

    /**
     * 新建垂直渐变
     *
     * @param colorStart 起始颜色
     * @param colorEnd   结束颜色
     * @return 垂直渐变
     */
    public static GradientDrawable newGradientVertical(@ColorInt int colorStart,
                                                       @ColorInt int colorEnd) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{colorStart, colorEnd});
        return drawable;
    }

    public static GradientDrawable newColorCorner(@ColorInt int color, float radius) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    public static GradientDrawable newColorTopCorner(@ColorInt int color, float radius) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0, 0, 0});
        return drawable;
    }

    public static GradientDrawable newColorBottomCorner(@ColorInt int color, float radius) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadii(new float[]{0, 0, 0, 0, radius, radius, radius, radius});
        return drawable;
    }

    public static StateListDrawable newSelectable(Drawable unselect, Drawable selected) {
        final StateListDrawable list = new StateListDrawable();
        list.addState(new int[]{android.R.attr.state_selected}, selected);
        list.addState(new int[0], unselect);
        return list;
    }

    public static StateListDrawable newSelectable(Context context,
                                                  @DrawableRes int unselect,
                                                  @DrawableRes int selected) {
        return newSelectable(ContextCompat.getDrawable(context, unselect),
                ContextCompat.getDrawable(context, selected));
    }

    public static StateListDrawable newActivatable(Drawable normal, Drawable activated) {
        final StateListDrawable list = new StateListDrawable();
        list.addState(new int[]{android.R.attr.state_activated}, activated);
        list.addState(new int[0], normal);
        return list;
    }

    public static StateListDrawable newActivatable(Context context,
                                                   @DrawableRes int normal,
                                                   @DrawableRes int activated) {
        return newActivatable(ContextCompat.getDrawable(context, normal),
                ContextCompat.getDrawable(context, activated));
    }

    public static GradientDrawable newEmpty(int width, int height) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(width, height);
        return drawable;
    }
}
