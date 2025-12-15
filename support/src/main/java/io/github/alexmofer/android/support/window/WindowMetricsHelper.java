package io.github.alexmofer.android.support.window;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.UiContext;
import androidx.window.layout.WindowMetrics;
import androidx.window.layout.WindowMetricsCalculator;

/**
 * 窗口尺寸辅助
 * 较小的宽度	小于 600dp	99.96% 的手机处于竖屏模式
 * 中等宽度	600dp+	93.73% 的平板电脑处于竖屏模式，展开的大型内部显示屏处于竖屏模式
 * 展开宽度	840dp+	97.22% 的平板电脑处于横屏模式，展开的大型内部显示屏处于横屏模式
 * 较小的高度	小于 480dp	99.78% 的手机处于横屏模式
 * 中等高度	480dp+	96.56% 的平板电脑处于横屏模式，97.59% 的手机处于竖屏模式
 * 展开高度	900dp+	94.25% 的平板电脑处于竖屏模式
 * Created by Alex on 2025/12/15.
 */
public final class WindowMetricsHelper {
    public static final int LEVEL_MIN = 0;// 低于320dp的等级
    public static final int LEVEL_320 = 1;// 不低于320dp的等级
    public static final int LEVEL_480 = 2;// 不低于480dp的等级
    public static final int LEVEL_600 = 3;// 不低于600dp的等级
    public static final int LEVEL_720 = 4;// 不低于720dp的等级
    public static final int LEVEL_840 = 5;// 不低于840dp的等级
    public static final int LEVEL_900 = 6;// 不低于900dp的等级
    public final WindowMetrics windowMetrics;

    private static WindowMetricsCalculator sCalculator;

    private WindowMetricsHelper(WindowMetrics windowMetrics) {
        this.windowMetrics = windowMetrics;
    }

    private static WindowMetricsCalculator getCalculator() {
        if (sCalculator == null) {
            sCalculator = WindowMetricsCalculator.getOrCreate();
        }
        return sCalculator;
    }

    public static WindowMetricsHelper computeCurrentWindowMetrics(Activity activity) {
        return new WindowMetricsHelper(getCalculator().computeCurrentWindowMetrics(activity));
    }

    public static WindowMetricsHelper computeCurrentWindowMetrics(@UiContext Context context) {
        return new WindowMetricsHelper(getCalculator().computeCurrentWindowMetrics(context));
    }

    /**
     * 获取窗口宽度
     *
     * @return 窗口宽度
     */
    public int getWidth() {
        return windowMetrics.getBounds().width();
    }

    /**
     * 获取窗口高度
     *
     * @return 窗口高度
     */
    public int getHeight() {
        return windowMetrics.getBounds().height();
    }

    /**
     * 判断是否窗口可用宽度在320dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isW320() {
        return windowMetrics.getWidthDp() >= 320;
    }

    /**
     * 判断是否窗口可用宽度在480dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isW480() {
        return windowMetrics.getWidthDp() >= 480;
    }

    /**
     * 判断是否窗口可用宽度在600dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isW600() {
        return windowMetrics.getWidthDp() >= 600;
    }

    /**
     * 判断是否窗口可用宽度在720dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isW720() {
        return windowMetrics.getWidthDp() >= 720;
    }

    /**
     * 判断是否窗口可用宽度在840dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isW840() {
        return windowMetrics.getWidthDp() >= 840;
    }

    /**
     * 判断是否窗口可用宽度在900dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isW900() {
        return windowMetrics.getWidthDp() >= 900;
    }

    /**
     * 获取窗口可用宽度等级
     *
     * @return 可用宽度等级
     */
    public int getWLevel() {
        if (isW900()) {
            return LEVEL_900;
        }
        if (isW840()) {
            return LEVEL_840;
        }
        if (isW720()) {
            return LEVEL_720;
        }
        if (isW600()) {
            return LEVEL_600;
        }
        if (isW480()) {
            return LEVEL_480;
        }
        if (isW320()) {
            return LEVEL_320;
        }
        return LEVEL_MIN;
    }

    /**
     * 判断是否窗口可用高度在320dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isH320() {
        return windowMetrics.getHeightDp() >= 320;
    }

    /**
     * 判断是否窗口可用高度在480dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isH480() {
        return windowMetrics.getHeightDp() >= 480;
    }

    /**
     * 判断是否窗口可用高度在600dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isH600() {
        return windowMetrics.getHeightDp() >= 600;
    }

    /**
     * 判断是否窗口可用高度在720dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isH720() {
        return windowMetrics.getHeightDp() >= 720;
    }

    /**
     * 判断是否窗口可用高度在840dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isH840() {
        return windowMetrics.getHeightDp() >= 840;
    }

    /**
     * 判断是否窗口可用高度在900dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isH900() {
        return windowMetrics.getHeightDp() >= 900;
    }

    /**
     * 获取窗口可用高度等级
     *
     * @return 可用高度等级
     */
    public int getHLevel() {
        if (isH900()) {
            return LEVEL_900;
        }
        if (isH840()) {
            return LEVEL_840;
        }
        if (isH720()) {
            return LEVEL_720;
        }
        if (isH600()) {
            return LEVEL_600;
        }
        if (isH480()) {
            return LEVEL_480;
        }
        if (isH320()) {
            return LEVEL_320;
        }
        return LEVEL_MIN;
    }

    /**
     * 判断是否窗口短边宽度在320dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isSW320() {
        return windowMetrics.getWidthDp() >= 320 && windowMetrics.getHeightDp() >= 320;
    }

    /**
     * 判断是否窗口短边宽度在480dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isSW480() {
        return windowMetrics.getWidthDp() >= 480 && windowMetrics.getHeightDp() >= 480;
    }

    /**
     * 判断是否窗口短边宽度在600dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isSW600() {
        return windowMetrics.getWidthDp() >= 600 && windowMetrics.getHeightDp() >= 600;
    }

    /**
     * 判断是否窗口短边宽度在720dp及以上
     *
     * @return 不低于限定值时返回true
     */
    public boolean isSW720() {
        return windowMetrics.getWidthDp() >= 720 && windowMetrics.getHeightDp() >= 720;
    }

    /**
     * 获取窗口短边等级
     *
     * @return 窗口短边等级
     */
    public int getSWLevel() {
        if (isSW720()) {
            return LEVEL_720;
        }
        if (isSW600()) {
            return LEVEL_600;
        }
        if (isSW480()) {
            return LEVEL_480;
        }
        if (isSW320()) {
            return LEVEL_320;
        }
        return LEVEL_MIN;
    }
}
