/*
 * Copyright (C) 2018 AlexMofer
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
package am.widget.floatingactionmode;

import android.graphics.Rect;
import android.view.View;

import am.widget.floatingactionmode.impl.FloatingActionModeHelper;

/**
 * 悬浮菜单
 * Created by Alex on 2018/11/21.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class FloatingActionMode {

    /**
     * 优先考虑显示在矩形区域的下方，区域不够放置主面板时会显示在矩形区域上方
     */
    public static final int LOCATION_BELOW_PRIORITY = 0;
    /**
     * 固定显示在矩形区域的下方，可能空间不足而导致遮盖矩形区域
     */
    public static final int LOCATION_BELOW_ALWAYS = 1;
    /**
     * 优先考虑显示在矩形区域的上方，区域不够放置主面板时会显示在矩形区域下方
     */
    public static final int LOCATION_ABOVE_PRIORITY = 2;
    /**
     * 固定显示在矩形区域的上方，可能空间不足而导致遮盖矩形区域
     */
    public static final int LOCATION_ABOVE_ALWAYS = 3;
    private Object mTag;
    private final FloatingActionModeHelper mHelper;

    /**
     * 构造悬浮菜单
     *
     * @param target   目标视图
     * @param callback 回调
     */
    public FloatingActionMode(View target, Callback callback) {
        this(target, callback, 0);
    }

    /**
     * 构造悬浮菜单
     *
     * @param target   目标视图
     * @param callback 回调
     * @param theme    主题资源
     */
    public FloatingActionMode(View target, Callback callback, int theme) {
        mHelper = new FloatingActionModeHelper(this, target, callback, theme);
    }

    /**
     * 获取附件
     *
     * @return 附件
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * 设置附件
     *
     * @param tag 附件
     * @return 自身
     */
    public FloatingActionMode setTag(Object tag) {
        mTag = tag;
        return this;
    }

    /**
     * 设置位置类型
     *
     * @param location 位置类型
     * @return 自身
     */
    public FloatingActionMode setLocation(int location) {
        if (location != LOCATION_BELOW_PRIORITY && location != LOCATION_BELOW_ALWAYS &&
                location != LOCATION_ABOVE_PRIORITY && location != LOCATION_ABOVE_ALWAYS)
            return this;
        mHelper.setLocation(location);
        return this;
    }

    /**
     * 判断布局是否不受限制
     *
     * @return 布局是否不受限制
     */
    public boolean isLayoutNoLimitsEnabled() {
        return mHelper.isLayoutNoLimitsEnabled();
    }

    /**
     * 设置是否开启布局不受限制
     *
     * @param enabled 是否开启
     * @return 自身
     */
    public FloatingActionMode setLayoutNoLimitsEnabled(boolean enabled) {
        mHelper.setLayoutNoLimitsEnabled(enabled);
        return this;
    }

    /**
     * 判断布局是否可在屏幕中（可以覆盖到状态栏）
     *
     * @return 是否可以
     */
    public boolean isLayoutInScreenEnabled() {
        return mHelper.isLayoutInScreenEnabled();
    }

    /**
     * 设置布局是否在屏幕中（可以覆盖到状态栏）
     *
     * @param enabled 是否开启
     */
    public FloatingActionMode setLayoutInScreenEnabled(boolean enabled) {
        mHelper.setLayoutInScreenEnabled(enabled);
        return this;
    }

    /**
     * 判断布局是否可在窗口裁剪区域上（可以覆盖到虚拟导航栏）
     *
     * @return 是否可以
     */
    public boolean isLayoutInsetDecorEnabled() {
        return mHelper.isLayoutInsetDecorEnabled();
    }

    /**
     * 设置布局是否可在窗口裁剪区域上（可以覆盖到虚拟导航栏）
     *
     * @param enabled 是否开启
     */
    public FloatingActionMode setLayoutInsetDecorEnabled(boolean enabled) {
        mHelper.setLayoutInsetDecorEnabled(enabled);
        return this;
    }

    /**
     * 开启
     */
    public void start() {
        mHelper.start();
    }

    /**
     * 刷新
     */
    public void invalidate() {
        mHelper.invalidate();
    }

    /**
     * 刷新内容区域
     */
    public void invalidateContentRect() {
        mHelper.invalidateContentRect();
    }

    /**
     * 结束
     */
    public void finish() {
        mHelper.finish();
    }

    /**
     * 隐藏
     */
    public void hide() {
        mHelper.hide();
    }

    /**
     * 显示
     */
    public void show() {
        mHelper.show();
    }

    /**
     * 判断是否隐藏
     *
     * @return 是否隐藏
     */
    public boolean isHidden() {
        return mHelper.isHidden();
    }

    /**
     * 判断是否结束
     *
     * @return 是否结束
     */
    public boolean isFinished() {
        return mHelper.isFinished();
    }

    /**
     * 执行子项点击事件
     *
     * @param item 子项
     */
    public void performItemClicked(FloatingMenuItem item) {
        mHelper.performItemClicked(item);
    }

    /**
     * 退回主面板
     *
     * @param animate 是否进行动画
     */
    public void backToMain(boolean animate) {
        mHelper.backToMain(animate);
    }

    /**
     * 打开更多面板
     *
     * @param animate 是否进行动画
     */
    public void openOverflow(boolean animate) {
        mHelper.openOverflow(animate);
    }

    /**
     * 获取菜单
     *
     * @return 菜单
     */
    public FloatingMenu getMenu() {
        return mHelper.getMenu();
    }

    /**
     * 回调
     */
    public interface Callback {

        /**
         * 准备悬浮菜单
         *
         * @param mode 悬浮菜单
         * @param menu 菜单
         * @return 菜单是否发生改变
         */
        boolean onPrepareActionMode(FloatingActionMode mode, FloatingMenu menu);

        /**
         * 获取内容区域
         *
         * @param mode    悬浮菜单
         * @param view    目标视图
         * @param outRect 内容区域
         */
        void onGetContentRect(FloatingActionMode mode, View view, Rect outRect);

        /**
         * 悬浮菜单子项发生点击事件
         *
         * @param mode 悬浮菜单
         * @param item 菜单子项
         * @return 是否已处理完子项点击事件
         */
        boolean onActionItemClicked(FloatingActionMode mode, FloatingMenuItem item);

        /**
         * 销毁悬浮菜单
         *
         * @param mode 悬浮菜单
         */
        void onDestroyActionMode(FloatingActionMode mode);
    }
}
