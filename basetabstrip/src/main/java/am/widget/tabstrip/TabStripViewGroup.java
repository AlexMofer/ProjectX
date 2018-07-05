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

package am.widget.tabstrip;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewGroup;

import am.widget.basetabstrip.R;

@SuppressWarnings("unused")
@ViewPager.DecorView
public abstract class TabStripViewGroup extends ViewGroup {

    private final TabStripHelper mHelper = new TabStripHelper(this);

    public TabStripViewGroup(Context context) {
        super(context);
        initView(context, null);
    }

    public TabStripViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TabStripViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        final TypedArray custom = context.obtainStyledAttributes(attrs,
                R.styleable.TabStripViewGroup);
        mHelper.set(custom.getResourceId(R.styleable.TabStripViewGroup_tsgViewPager, NO_ID),
                custom.getBoolean(R.styleable.TabStripViewGroup_tsgAutoFindViewPager, true),
                custom.getBoolean(R.styleable.TabStripViewGroup_tsgClickSmoothScroll, false));
        custom.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHelper.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHelper.onDetachedFromWindow();
    }

    @SuppressWarnings("all")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final boolean touch = mHelper.onTouchEvent(event);
        final boolean result = super.onTouchEvent(event);
        return touch || result;
    }

    @Override
    public boolean performClick() {
        final boolean click = mHelper.performClick();
        final boolean result = super.performClick();
        if (result)
            return true;
        if (click) {
            playSoundEffect(SoundEffectConstants.CLICK);
            return true;
        }
        return false;
    }

    /**
     * 进行点击事件
     *
     * @param position     位置
     * @param smoothScroll 是否平滑滚动
     * @return 是否响应
     */
    public boolean performClick(int position, boolean smoothScroll) {
        if (mHelper.isBoundViewPager()) {
            setCurrentItem(position, smoothScroll);
            return true;
        }
        return false;
    }

    /**
     * 进行双击事件
     *
     * @param first        第一个点击位置
     * @param second       第二个点击位置
     * @param smoothScroll 是否平滑滚动
     */
    public void performDoubleClick(int first, int second, boolean smoothScroll) {
    }

    /**
     * 附着
     *
     * @param pager ViewPager
     */
    protected void onAttachedToViewPager(@NonNull ViewPager pager) {
    }

    /**
     * 脱离
     *
     * @param pager ViewPager
     */
    protected void onDetachedFromViewPager(@NonNull ViewPager pager) {
    }

    /**
     * 所绑定的ViewPager设置了新的PagerAdapter
     *
     * @param oldAdapter 旧的PagerAdapter
     * @param newAdapter 新的PagerAdapter
     */
    protected void onViewPagerAdapterChanged(@Nullable PagerAdapter oldAdapter,
                                             @Nullable PagerAdapter newAdapter) {
    }

    /**
     * 所绑定的ViewPager的PagerAdapter的数据有变化
     */
    protected void onViewPagerAdapterDataChanged() {
    }

    /**
     * 所绑定的ViewPager发生了滚动
     *
     * @param position 坐标
     * @param offset   偏移
     */
    protected void onViewPagerScrolled(int position, float offset) {
    }

    /**
     * 所绑定的ViewPager子项被选中了
     *
     * @param position 坐标
     */
    protected void onViewPagerItemSelected(int position) {
    }

    /**
     * 所绑定的ViewPager的滚动状态有变化
     *
     * @param state 滚动状态
     */
    protected void onViewPagerScrollStateChanged(int state) {
    }

    /**
     * 所绑定的ViewPager发生了变化
     *
     * @param position 位置
     * @param offset   偏移
     */
    protected abstract void onViewPagerChanged(int position, float offset);

    /**
     * 获取点击的页位置
     *
     * @param downX ACTION_DOWN X轴坐标
     * @param downY ACTION_DOWN Y轴坐标
     * @param upX   ACTION_UP X轴坐标
     * @param upY   ACTION_UP Y轴坐标
     * @return 位置
     */
    protected int getClickedPosition(float downX, float downY, float upX, float upY) {
        return PagerAdapter.POSITION_NONE;
    }

    /**
     * 设置是否响应点击事件
     *
     * @param respond 响应点击事件
     */
    protected void setRespondClick(boolean respond) {
        mHelper.setRespondClick(respond);
    }

    /**
     * 设置是否响应双击事件
     *
     * @param respond 响应双击事件
     */
    protected void setRespondDoubleClick(boolean respond) {
        mHelper.setRespondDoubleClick(respond);
    }

    /**
     * 获取页标题
     *
     * @param position 页坐标
     * @return 页标题
     */
    @Nullable
    protected CharSequence getPageTitle(int position) {
        return mHelper.getPageTitle(position);
    }

    /**
     * 获取页总数
     *
     * @return 页总数
     */
    protected int getPageCount() {
        return mHelper.getPageCount();
    }

    /**
     * 设置选中页
     *
     * @param position     页坐标
     * @param smoothScroll 是否平滑滚动
     */
    protected void setCurrentItem(int position, boolean smoothScroll) {
        mHelper.setCurrentItem(position, smoothScroll);
    }

    /**
     * 判断是否为双击
     * 一般用于{@link #performClick(int, boolean)}方法中用于判断当前点击是否为双击，其他地方调用则无意义
     *
     * @return 是否为双击
     */
    protected boolean isDoubleClick() {
        return mHelper.isDoubleClick();
    }

    /**
     * 捆绑ViewPager
     *
     * @param pager 关联的ViewPager
     */
    public void bindViewPager(ViewPager pager) {
        mHelper.bindViewPager(pager);
    }

    /**
     * 更新视图
     *
     * @param force 是否强制
     */
    public void updateView(boolean force) {
        mHelper.updateView(force);
    }

    /**
     * 设置点击是否平滑滚动
     *
     * @param smoothScroll 是否平滑滚动
     */
    public void setClickSmoothScroll(boolean smoothScroll) {
        mHelper.setClickSmoothScroll(smoothScroll);
    }
}
