/*
 * Copyright (C) 2015 AlexMofer
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

package am.widget.basetabstrip;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * BasePagerTabStrip ViewPager滑动对应变化效果
 *
 * @author Alex
 */
@ViewPager.DecorView
public abstract class BaseTabStrip extends View {

    private final PageListener mPageListener = new PageListener();
    private final Rect mRefreshRect = new Rect();
    private final Rect mRefreshTempRect = new Rect();
    private ViewPager mPager;
    private WeakReference<PagerAdapter> mWatchingAdapter;
    private int mLastKnownPosition = 0;
    private float mLastKnownPositionOffset = -1;
    private int mCurrentPager = 0;
    private int mPosition = 0;
    private boolean clickSelectedItem = false;
    private Drawable mTabItemBackground;
    private ArrayList<Drawable> mTabItemBackgrounds = new ArrayList<>();
    private boolean tabClickable;
    private boolean clickSmoothScroll;
    private TabStripGestureDetector tabStripGestureDetector;
    private OnItemClickListener clickListener;
    private ArrayList<OnChangeListener> changeListeners;

    public BaseTabStrip(Context context) {
        super(context);
        initView();
    }

    public BaseTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BaseTabStrip(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setItemClickable(false);
        setClickSmoothScroll(false);
        tabStripGestureDetector = new TabStripGestureDetector();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (mPager == null && parent != null && parent instanceof ViewPager) {
            bindViewPager((ViewPager) parent);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bindViewPager(null);
        clearItemBackground();
    }

    /**
     * 捆绑ViewPager
     *
     * @param pager 关联的ViewPager
     */
    public void bindViewPager(ViewPager pager) {
        PagerAdapter oldAdapter = null;
        PagerAdapter newAdapter = null;
        if (mPager != null) {
            mPager.removeOnPageChangeListener(mPageListener);
            mPager.removeOnAdapterChangeListener(mPageListener);
            oldAdapter = mPager.getAdapter();
        }
        mPager = pager;
        if (mPager != null) {
            mPager.addOnPageChangeListener(mPageListener);
            mPager.addOnAdapterChangeListener(mPageListener);
            newAdapter = mPager.getAdapter();
        }
        bindPagerAdapter(oldAdapter, newAdapter);
    }

    private void clearItemBackground() {
        for (Drawable drawable : mTabItemBackgrounds) {
            drawable.setCallback(null);
        }
        mTabItemBackgrounds.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!tabClickable) {
            return super.onTouchEvent(ev);
        }
        final boolean tab = tabStripGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev) || tab;
    }

    @Override
    @Deprecated
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * 点击
     *
     * @param position 位置
     * @return 点击成功
     */
    @SuppressWarnings("unused")
    public boolean performClick(int position) {
        return performClick(position, false, true);
    }

    /**
     * 点击
     *
     * @param position       位置
     * @param smoothScroll   是否平滑滚动
     * @param notifyListener 是否通知监听器
     * @return 点击成功
     */
    public boolean performClick(int position, boolean smoothScroll, boolean notifyListener) {
        if (getViewPager() != null && position >= 0 && position < getItemCount()) {
            clickSelectedItem = position == mPosition;
            mPosition = position;
            if (!clickSelectedItem) {
                if (!smoothScroll) {
                    mCurrentPager = position;
                    mLastKnownPosition = mCurrentPager;
                    mLastKnownPositionOffset = 0;
                    jumpTo(mCurrentPager);
                    notifyJumpTo(mCurrentPager);
                }
                getViewPager().setCurrentItem(position, smoothScroll);
            }
            if (clickListener != null && notifyListener) {
                clickListener.onItemClick(mPosition);
            }
            playSoundEffect(SoundEffectConstants.CLICK);
            return true;
        }
        return false;
    }

    /**
     * 捆绑PagerAdapter
     *
     * @param oldAdapter 旧Adapter
     * @param newAdapter 新Adapter
     */
    protected void bindPagerAdapter(PagerAdapter oldAdapter, PagerAdapter newAdapter) {
        if (oldAdapter != null) {
            oldAdapter.unregisterDataSetObserver(mPageListener);
            mWatchingAdapter = null;
        }
        if (newAdapter != null) {
            newAdapter.registerDataSetObserver(mPageListener);
            mWatchingAdapter = new WeakReference<>(newAdapter);
        }
        createItemBackgrounds();
        onBindPagerAdapter();
        checkCurrentItem();
        requestLayout();
        invalidate();
    }

    /**
     * 判断是否有子项背景
     *
     * @return 是否子项背景
     */
    protected boolean hasItemBackgrounds() {
        return mTabItemBackground != null;
    }

    /**
     * 创建子项背景
     */
    protected void createItemBackgrounds() {
        if (!hasItemBackgrounds())
            return;
        int count = getItemCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (i < mTabItemBackgrounds.size()) {
                    mTabItemBackgrounds.get(i).setState(onCreateDrawableState(0));
                } else {
                    Drawable tag;
                    if (mTabItemBackground.getConstantState() != null) {
                        tag = mTabItemBackground.getConstantState().newDrawable().mutate();
                    } else {
                        tag = mTabItemBackground.mutate();
                    }
                    tag.setCallback(this);
                    mTabItemBackgrounds.add(tag);
                }
            }
        } else {
            for (Drawable drawable : mTabItemBackgrounds) {
                drawable.setState(onCreateDrawableState(0));
            }
        }
    }

    /**
     * 重新创建子项背景
     */
    protected void recreateItemBackgrounds() {
        clearItemBackground();
        if (!hasItemBackgrounds())
            return;
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            Drawable tag;
            if (mTabItemBackground.getConstantState() != null) {
                tag = mTabItemBackground.getConstantState().newDrawable().mutate();
            } else {
                tag = mTabItemBackground.mutate();
            }
            tag.setCallback(this);
            mTabItemBackgrounds.add(tag);
        }
    }

    /**
     * 获取当前选中的子项
     *
     * @return 当前选中的子项
     */
    public int getCurrentItem() {
        return mPager == null ? -1 : mPager.getCurrentItem();
    }

    /**
     * 检查选中子项
     */
    public void checkCurrentItem() {
        final int position = getCurrentItem();
        mPosition = position;
        if (position >= 0 && position != mCurrentPager) {
            mCurrentPager = position;
            mLastKnownPosition = mCurrentPager;
            mLastKnownPositionOffset = 0;
            jumpTo(mCurrentPager);
            notifyJumpTo(mCurrentPager);
        }
    }

    /**
     * 捆绑PagerAdapter
     */
    protected void onBindPagerAdapter() {

    }

    /**
     * 由触摸点转为Position
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 坐标对应位置
     */
    @SuppressWarnings("unused")
    protected int pointToPosition(float x, float y) {
        return 0;
    }

    @Override
    protected void drawableStateChanged() {
        final float downMotionX = tabStripGestureDetector.getDownMotionX();
        final float downMotionY = tabStripGestureDetector.getDownMotionY();
        int position = pointToPosition(downMotionX, downMotionY);
        if (position >= 0 && position < mTabItemBackgrounds.size()) {
            for (int i = 0; i < mTabItemBackgrounds.size(); i++) {
                Drawable tag = mTabItemBackgrounds.get(i);
                if (i == position) {
                    DrawableCompat.setHotspot(tag, getHotspotX(tag, position, downMotionX, downMotionY),
                            getHotspotY(tag, position, downMotionX, downMotionY));
                    if (tag.isStateful()) {
                        tag.setState(getDrawableState());
                    }
                } else {
                    if (tag.isStateful()) {
                        tag.setState(new int[] { 0 });
                        DrawableCompat.jumpToCurrentState(tag);
                    }
                }

            }
        }
        super.drawableStateChanged();
    }

    /**
     * set hotspot's x location
     *
     * @param background 背景图
     * @param position   图片Position
     * @param motionX    点击位置X
     * @param motionY    点击位置Y
     * @return x location
     */
    @SuppressWarnings("unused")
    protected float getHotspotX(Drawable background, int position, float motionX, float motionY) {
        return background.getIntrinsicWidth() * 0.5f;
    }

    /**
     * set hotspot's y location
     *
     * @param background 背景图
     * @param position   图片Position
     * @param motionX    点击位置X
     * @param motionY    点击位置Y
     * @return y location
     */
    @SuppressWarnings("unused")
    protected float getHotspotY(Drawable background, int position, float motionX, float motionY) {
        return background.getIntrinsicHeight() * 0.5f;
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        boolean isTag = false;
        for (Drawable tag : mTabItemBackgrounds) {
            if (who == tag) {
                isTag = true;
                break;
            }
        }
        return isTag || super.verifyDrawable(who);
    }

    /**
     * 获取Tab子项背景
     *
     * @param position 位置
     * @return 背景
     */
    @SuppressWarnings("unused")
    protected Drawable getItemBackground(int position) {
        return position < mTabItemBackgrounds.size() ? mTabItemBackgrounds.get(position) : null;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        BaseTabStripSavedState ss = new BaseTabStripSavedState(superState);
        ss.currentPager = mCurrentPager;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof BaseTabStripSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        BaseTabStripSavedState ss = (BaseTabStripSavedState) state;
        performClick(ss.currentPager, false, false);
        super.onRestoreInstanceState(ss.getSuperState());
    }

    /**
     * 更新View
     *
     * @param position       位置
     * @param positionOffset 位置偏移
     * @param force          是否强制更新
     */
    private void updateView(int position, float positionOffset, boolean force) {
        if (mLastKnownPositionOffset == -1) {
            mLastKnownPositionOffset = positionOffset;
        }
        if (!force && positionOffset == mLastKnownPositionOffset) {
            return;
        }
        float mPositionOffset = positionOffset;
        if (mLastKnownPositionOffset == 0 || mLastKnownPositionOffset == 1)
            if (mPositionOffset > 0.5f)
                mLastKnownPositionOffset = 1;
            else
                mLastKnownPositionOffset = 0;
        int nextPager;
        if (position > mLastKnownPosition) {
            mLastKnownPosition = position - 1;
            if (mLastKnownPositionOffset > mPositionOffset) {
                if (mPositionOffset == 0) {
                    mPositionOffset = 1;
                } else {
                    mLastKnownPosition = position;
                }
                mCurrentPager = mLastKnownPosition;
                nextPager = mLastKnownPosition + 1;
                gotoRight(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoRight(mCurrentPager, nextPager, mPositionOffset);
            } else {
                mCurrentPager = mLastKnownPosition + 1;
                nextPager = mLastKnownPosition;
                gotoLeft(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoLeft(mCurrentPager, nextPager, mPositionOffset);
            }
        } else {
            mLastKnownPosition = position;
            if (mLastKnownPositionOffset > mPositionOffset) {
                mCurrentPager = mLastKnownPosition + 1;
                nextPager = mLastKnownPosition;
                gotoLeft(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoLeft(mCurrentPager, nextPager, mPositionOffset);
            } else {
                mPositionOffset = mPositionOffset == 0 ? 1 : mPositionOffset;
                mCurrentPager = mLastKnownPosition;
                nextPager = mLastKnownPosition + 1;
                gotoRight(mCurrentPager, nextPager, mPositionOffset);
                notifyGotoRight(mCurrentPager, nextPager, mPositionOffset);
            }
        }
        mLastKnownPosition = position;
        mLastKnownPositionOffset = positionOffset;
    }

    /**
     * 通知跳转到
     *
     * @param current 位置
     */
    private void notifyJumpTo(int current) {
        if (changeListeners == null)
            return;
        for (OnChangeListener listener : changeListeners) {
            listener.jumpTo(current);
        }
    }

    /**
     * 通知滑向左边
     *
     * @param current 当前页
     * @param next    目标页
     * @param offset  偏移
     */
    private void notifyGotoLeft(int current, int next, float offset) {
        if (changeListeners == null)
            return;
        for (OnChangeListener listener : changeListeners) {
            listener.gotoLeft(current, next, offset);
        }
    }

    /**
     * 通知滑向右边
     *
     * @param current 当前页
     * @param next    目标页
     * @param offset  偏移
     */
    private void notifyGotoRight(int current, int next, float offset) {
        if (changeListeners == null)
            return;
        for (OnChangeListener listener : changeListeners) {
            listener.gotoRight(current, next, offset);
        }
    }

    /**
     * 直接跳转到
     *
     * @param current 位置
     */
    protected abstract void jumpTo(int current);

    /**
     * 滑向左边
     *
     * @param current 当前页
     * @param next    目标页
     * @param offset  偏移
     */
    protected abstract void gotoLeft(int current, int next, float offset);

    /**
     * 滑向右边
     *
     * @param current 当前页
     * @param next    目标页
     * @param offset  偏移
     */
    protected abstract void gotoRight(int current, int next, float offset);

    /**
     * 获取ViewPager
     *
     * @return ViewPager
     */
    protected ViewPager getViewPager() {
        return mPager;
    }

    /**
     * 获取子项总数
     *
     * @return 子项总数
     */
    protected int getItemCount() {
        try {
            return mWatchingAdapter.get().getCount();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取子项文字
     *
     * @param position 子项坐标
     * @return 子项文字
     */
    @SuppressWarnings("unused")
    protected CharSequence getItemText(int position) {
        try {
            return mWatchingAdapter.get().getPageTitle(position);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 设置Tab子项背景
     *
     * @param background 背景
     */
    public void setItemBackground(Drawable background) {
        if (mTabItemBackground != background) {
            mTabItemBackground = background;
            recreateItemBackgrounds();
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置Tab子项背景
     *
     * @param background 背景
     */
    @SuppressWarnings("unused")
    public void setItemBackground(int background) {
        setItemBackground(ContextCompat.getDrawable(getContext(), background));
    }

    /**
     * 设置Tab是否可以点击
     *
     * @param clickable 是否可以点击
     */
    public void setItemClickable(boolean clickable) {
        tabClickable = clickable;
        if (tabClickable) {
            setClickable(true);
        }
    }

    /**
     * Tab是否可以点击
     *
     * @return Tab是否可以点击
     */
    @SuppressWarnings("unused")
    public boolean isTabClickable() {
        return tabClickable;
    }

    /**
     * 是否点击时平滑滚动
     *
     * @return 点击时是否平滑滚动
     */
    @SuppressWarnings("unused")
    public boolean isClickSmoothScroll() {
        return clickSmoothScroll;
    }

    /**
     * 设置点击时是否平滑滚动
     *
     * @param smooth 点击时是否平滑滚动
     */
    public void setClickSmoothScroll(boolean smooth) {
        clickSmoothScroll = smooth;
    }

    /**
     * 设置点击监听
     *
     * @param listener 监听器
     */
    @SuppressWarnings("unused")
    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
    }

    /**
     * 添加变化监听器
     *
     * @param listener 变化监听器
     */
    @SuppressWarnings("unused")
    public void addOnChangeListener(OnChangeListener listener) {
        if (listener == null)
            return;
        if (changeListeners == null)
            changeListeners = new ArrayList<>();
        changeListeners.add(listener);
        listener.jumpTo(mCurrentPager);
    }

    /**
     * 移除变化监听器
     *
     * @param listener 变化监听器
     */
    @SuppressWarnings("unused")
    public void removeOnChangeListener(OnChangeListener listener) {
        if (changeListeners == null)
            return;
        changeListeners.remove(listener);
    }

    /**
     * 获取默认Tag背景
     *
     * @return 默认Tag背景
     */
    @SuppressWarnings("unused")
    protected Drawable getDefaultTagBackground() {
        final float density = getResources().getDisplayMetrics().density;
        final GradientDrawable mBackground = new GradientDrawable();
        mBackground.setShape(GradientDrawable.RECTANGLE);
        mBackground.setColor(0xffff4444);
        mBackground.setCornerRadius(100000 * density);
        mBackground.setSize((int) (10 * density), (int) (10 * density));
        return mBackground;
    }

    /**
     * 获取子项背景最小宽度
     *
     * @return 子项背景最小宽度
     */
    @SuppressWarnings("unused")
    protected int getMinItemBackgroundWidth() {
        if (!hasItemBackgrounds())
            return 0;
        return mTabItemBackground.getMinimumWidth();
    }

    /**
     * 获取子项背景最小高度
     *
     * @return 子项背景最小高度
     */
    @SuppressWarnings("unused")
    protected int getMinItemBackgroundHeight() {
        if (!hasItemBackgrounds())
            return 0;
        return mTabItemBackground.getMinimumHeight();
    }

    /**
     * 双色合成
     *
     * @param normalColor   普通颜色
     * @param selectedColor 选中颜色
     * @param offset        偏移值
     * @return 合成色
     */
    @SuppressWarnings("unused")
    protected int getColor(int normalColor, int selectedColor, float offset) {
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

    /**
     * 刷新指定区域
     *
     * @param dirty 区域集
     */
    @SuppressWarnings("unused")
    public void invalidate(int... dirty) {
        if (dirty == null || dirty.length <= 0)
            return;
        mRefreshRect.set(0, 0, 0, 0);
        for (int position : dirty) {
            mRefreshTempRect.set(0, 0, 0, 0);
            getItemRect(position, mRefreshTempRect);
            mRefreshRect.union(mRefreshTempRect);
        }
        if (!mRefreshTempRect.isEmpty())
            invalidate(mRefreshTempRect);
    }

    /**
     * 刷新位置
     * ViewPager做了跳转，可执行该方法重新定位
     */
    @SuppressWarnings("unused")
    public void invalidatePosition() {
        jumpTo(getCurrentItem());
    }

    /**
     * 获取子项边界
     *
     * @param position 子项
     * @param bound    边界
     */
    @SuppressWarnings("unused")
    protected void getItemRect(int position, Rect bound) {

    }

    /**
     * 点击监听
     */
    public interface OnItemClickListener {
        /**
         * 点击子项
         *
         * @param position 位置
         */
        void onItemClick(int position);

        /**
         * 点击已选中子项
         *
         * @param position 位置
         */
        void onSelectedClick(int position);

        /**
         * 双击子项
         *
         * @param position 位置
         */
        void onDoubleClick(int position);
    }

    /**
     * 变化监听
     */
    @SuppressWarnings("all")
    public interface OnChangeListener {
        /**
         * 跳转到当前位置
         *
         * @param correct 当前位置
         */
        void jumpTo(int correct);

        /**
         * 往左滚动
         *
         * @param correct 当前位置
         * @param next    将要抵达位置
         * @param offset  移动便宜
         */
        void gotoLeft(int correct, int next, float offset);

        /**
         * 往右滚动
         *
         * @param correct 当前位置
         * @param next    将要抵达位置
         * @param offset  移动便宜
         */
        void gotoRight(int correct, int next, float offset);
    }

    /**
     * 角标数据容器Adapter
     */
    @SuppressWarnings("unused")
    public interface ItemTabAdapter {

        /**
         * 是否启用角标
         *
         * @param position Item位置
         * @return 是否启用
         */
        boolean isTagEnable(int position);

        /**
         * 获取角标值
         *
         * @param position Item位置
         * @return 角标值
         */
        String getTag(int position);
    }

    private static class BaseTabStripSavedState extends BaseSavedState {
        public static final Creator<BaseTabStripSavedState> CREATOR =
                new Creator<BaseTabStripSavedState>() {
                    public BaseTabStripSavedState createFromParcel(Parcel in) {
                        return new BaseTabStripSavedState(in);
                    }

                    public BaseTabStripSavedState[] newArray(int size) {
                        return new BaseTabStripSavedState[size];
                    }
                };
        int currentPager;

        BaseTabStripSavedState(Parcelable superState) {
            super(superState);
        }

        private BaseTabStripSavedState(Parcel in) {
            super(in);
            currentPager = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentPager);
        }
    }

    /**
     * Tag位置
     */
    @SuppressWarnings("all")
    protected static class TagLocation {
        public static final int LOCATION_CONTENT = 0;//贴近内容
        public static final int LOCATION_EDGE = 1;// 贴近边缘
        private int location = LOCATION_CONTENT;// 位置

        private int paddingLeft;
        private int paddingRight;
        private int paddingTop;
        private int paddingBottom;

        private int marginLeft;
        private int marginRight;
        private int marginTop;
        private int marginBottom;

        public TagLocation() {
            this(LOCATION_CONTENT);
        }

        public TagLocation(int location) {
            setLocation(location);
            setPadding(0, 0, 0, 0);
            setMargin(0, 0, 0, 0);
        }

        public boolean setLocation(int location) {
            if (location != LOCATION_CONTENT && location != LOCATION_EDGE)
                return false;
            if (this.location != location) {
                this.location = location;
                return true;
            }
            return false;
        }

        public boolean setPadding(int left, int top, int right, int bottom) {
            if (left < 0 || top < 0 || right < 0 || bottom < 0 ||
                    (paddingLeft == left && paddingTop == top
                            && paddingRight == right && paddingBottom == bottom))
                return false;
            paddingLeft = left;
            paddingTop = top;
            paddingRight = right;
            paddingBottom = bottom;
            return true;
        }

        public boolean setMargin(int left, int top, int right, int bottom) {
            if (left < 0 || top < 0 || right < 0 || bottom < 0 ||
                    (marginLeft == left && marginTop == top
                            && marginRight == right && marginBottom == bottom))
                return false;
            marginLeft = left;
            marginTop = top;
            marginRight = right;
            marginBottom = bottom;
            return true;
        }

        public int getLocation() {
            return location;
        }

        public int getPaddingLeft() {
            return paddingLeft;
        }

        public int getPaddingRight() {
            return paddingRight;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }

        public int getMarginLeft() {
            return marginLeft;
        }

        public int getMarginRight() {
            return marginRight;
        }

        public int getMarginTop() {
            return marginTop;
        }

        public int getMarginBottom() {
            return marginBottom;
        }
    }

    private class PageListener extends DataSetObserver implements
            ViewPager.OnPageChangeListener, ViewPager.OnAdapterChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            updateView(position, positionOffset, false);
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset : 0;
                updateView(position, offset, false);
            }
            mPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter,
                                     @Nullable PagerAdapter newAdapter) {
            bindPagerAdapter(oldAdapter, newAdapter);
        }

        @Override
        public void onChanged() {
            final float offset = mLastKnownPositionOffset >= 0 ? mLastKnownPositionOffset : 0;
            final int position = getCurrentItem();
            mPosition = position;
            updateView(position, offset, true);
        }
    }

    private class TabStripGestureDetector {

        private final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        private float mDownMotionX;
        private float mDownMotionY;
        private int mDownPosition;
        private int mLastPosition;
        private long mLastUpTime;

        boolean onTouchEvent(MotionEvent ev) {
            boolean isClick = false;
            final int action = ev.getAction();
            final float x = ev.getX();
            final float y = ev.getY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mDownMotionX = x;
                    mDownMotionY = y;
                    mDownPosition = pointToPosition(x, y);
                    mLastPosition = getCurrentItem();
                    break;
                case MotionEvent.ACTION_UP:
                    final int clickPosition = pointToPosition(x, y);
                    if (mDownPosition == clickPosition) {
                        isClick = onItemClick(clickPosition);
                        if (mLastPosition == clickPosition) {
                            final long downTime = ev.getDownTime();
                            if (mLastUpTime != 0 && downTime - mLastUpTime < DOUBLE_TAP_TIMEOUT) {
                                onDoubleClick(clickPosition);
                            } else {
                                onSelectedClick(clickPosition);
                            }
                        }
                        mLastUpTime = ev.getEventTime();
                    } else {
                        mLastUpTime = 0;
                    }
                    break;
            }
            return isClick;
        }

        private boolean onItemClick(int position) {
            performClick(position, clickSmoothScroll, true);
            return true;
        }

        private void onSelectedClick(int position) {
            if (clickSelectedItem && clickListener != null) {
                clickListener.onSelectedClick(position);
            }
        }

        /**
         * 双击子项
         *
         * @param position 位置
         */
        private void onDoubleClick(int position) {
            if (clickListener != null) {
                clickListener.onDoubleClick(position);
            }
        }

        float getDownMotionX() {
            return mDownMotionX;
        }

        float getDownMotionY() {
            return mDownMotionY;
        }
    }
}