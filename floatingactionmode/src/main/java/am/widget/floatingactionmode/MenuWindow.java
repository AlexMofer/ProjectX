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

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * 菜单窗口
 * Created by Alex on 2018/10/23.
 */
final class MenuWindow implements View.OnLayoutChangeListener, View.OnAttachStateChangeListener,
        View.OnClickListener, SwitchAnimation.AnimationListener, MainLayout.OnMainListener,
        OverflowLayout.OnOverflowListener, SubLayout.OnSubListener {

    private static final int DIP_MARGIN = 20;
    private static final int DIP_ITEM_HEIGHT = 48;
    private static final int DIP_ELEVATION = 2;
    private static final int MAX_OVERFLOW_SHOW_COUNT = 4;
    private static final int TYPE_MAIN = 0;
    private static final int TYPE_OVERFLOW = 1;
    private static final int TYPE_SUB = 2;
    private static final long DURATION = 250;
    private final FloatingActionMode.Callback mCallback;
    private final FloatingActionMode mMode;
    private final WindowManager mManager;
    private final FloatingMenuImpl mMenu;
    private final int mMargin;
    private final int mMaxOverflowHeight;
    private final Rect mContentBound = new Rect();
    private final MainLayout mMainLayout;
    private final WindowManager.LayoutParams mMainParams = new WindowManager.LayoutParams();
    private final Point mMainSize = new Point();
    private final Point mMainCoordinates = new Point();
    private final SwitchLayout mSwitchLayout;
    private final WindowManager.LayoutParams mSwitchParams = new WindowManager.LayoutParams();
    private final Point mSwitchSize = new Point();
    private final Point mSwitchCoordinates = new Point();
    private final Point mSwitchAnimationCoordinates = new Point();
    private final OverflowLayout mOverflowLayout;
    private final WindowManager.LayoutParams mOverflowParams = new WindowManager.LayoutParams();
    private final Point mOverflowSize = new Point();
    private final Point mOverflowNeedSize = new Point();
    private final Point mOverflowCoordinates = new Point();
    private final SubLayout mSubLayout;
    private final WindowManager.LayoutParams mSubParams = new WindowManager.LayoutParams();
    private final Point mSubSize = new Point();
    private final Point mSubMaxSize = new Point();
    private final Point mSubNeedSize = new Point();
    private final Point mSubCoordinates = new Point();
    private final AnimationLayout mAnimationLayout;
    private final WindowManager.LayoutParams mAnimationParams = new WindowManager.LayoutParams();
    private final Point mAnimationSize = new Point();
    private final Point mAnimationCoordinates = new Point();
    private final int[] mTmpCoords = new int[2];
    private final Rect mViewPortOnScreen = new Rect();
    private View mTarget;
    private boolean mStarted;
    private boolean mFinished;
    private boolean mHidden;
    private boolean mLight = true;
    private boolean mUseTheme = true;
    private int mLocation = FloatingActionMode.LOCATION_BELOW_PRIORITY;
    private boolean mBelow;// 是否处于聚焦区域的下面
    private boolean mHasOverflow;
    private boolean mReverse;// 是否逆向向上展开（默认向下为正向展开）
    private int mType;// 面板类型
    private int mPreviousType = TYPE_MAIN;// 前一个面板类型

    MenuWindow(View target, FloatingActionMode.Callback callback, FloatingActionMode mode) {
        mTarget = target;
        mCallback = callback;
        mMode = mode;

        final Context context = target.getContext();
        mManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (mManager == null)
            throw new IllegalStateException();
        mMenu = new FloatingMenuImpl(context);
        final DisplayMetrics display = context.getResources().getDisplayMetrics();
        final int margin = TypedValueUtils.complexToDimensionPixelOffset(DIP_MARGIN, display);
        final int height = TypedValueUtils.complexToDimensionPixelOffset(DIP_ITEM_HEIGHT, display);
        final float elevation = TypedValueUtils.complexToDimension(DIP_ELEVATION, display);
        mMargin = margin;

        mSwitchLayout = new SwitchLayout(context, height, mLight, mUseTheme);
        mSwitchLayout.setOnClickListener(this);
        mSwitchParams.format = PixelFormat.TRANSPARENT;
        mSwitchParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mSwitchParams.gravity = Gravity.START | Gravity.TOP;
        mSwitchLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mSwitchSize.set(mSwitchLayout.getMeasuredWidth(), mSwitchLayout.getMeasuredHeight());

        mMaxOverflowHeight = mSwitchSize.y + MAX_OVERFLOW_SHOW_COUNT * height +
                (int) (height * 0.5f) + mMargin * 2;

        mMainLayout = new MainLayout(context, height, margin, mSwitchSize.x, mLight, mUseTheme,
                this);
        mMainParams.format = PixelFormat.TRANSPARENT;
        mMainParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mMainParams.gravity = Gravity.START | Gravity.TOP;

        mOverflowLayout = new OverflowLayout(context, height, margin, mSwitchSize.y,
                mLight, mUseTheme, this);
        mOverflowParams.format = PixelFormat.TRANSPARENT;
        mOverflowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mOverflowParams.gravity = Gravity.START | Gravity.TOP;

        mSubLayout = new SubLayout(context, height, margin, mSwitchSize.y, mLight, mUseTheme,
                this);
        mSubParams.format = PixelFormat.TRANSPARENT;
        mSubParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mSubParams.gravity = Gravity.START | Gravity.TOP;

        mAnimationLayout = new AnimationLayout(context, margin, elevation, mLight, this);
        mAnimationParams.format = PixelFormat.TRANSPARENT;
        mAnimationParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mAnimationParams.gravity = Gravity.START | Gravity.TOP;
    }

    boolean start(FloatingActionMode mode) {
        if (mFinished)
            return false;
        if (!mCallback.onCreateActionMode(mode, mMenu))
            return false;
        if (mFinished)
            return false;
        mCallback.onPrepareActionMode(mode, mMenu);
        if (mFinished) {
            mCallback.onDestroyActionMode(mode);
            return false;
        }
        mCallback.onGetContentRect(mode, mTarget, mContentBound);
        if (mFinished) {
            mCallback.onDestroyActionMode(mode);
            return false;
        }
        mMenu.prepareToShow();
        mTarget.getWindowVisibleDisplayFrame(mViewPortOnScreen);
        final int maxWidth = mViewPortOnScreen.right - mViewPortOnScreen.left;
        mMainLayout.setData(mMenu, maxWidth);
        mHasOverflow = mOverflowLayout.setData(mMenu, mOverflowNeedSize);
        mSubLayout.setData(mMenu, mSubMaxSize, mode);
        updateLayout();
        applyLayout();
        mType = TYPE_MAIN;
        applyType();
        mManager.addView(mAnimationLayout, mAnimationParams);
        mManager.addView(mSubLayout, mSubParams);
        mManager.addView(mOverflowLayout, mOverflowParams);
        mManager.addView(mMainLayout, mMainParams);
        mManager.addView(mSwitchLayout, mSwitchParams);
        mStarted = true;
        final View root = mTarget.getRootView();
        root.addOnLayoutChangeListener(this);
        root.addOnAttachStateChangeListener(this);
        mTarget.addOnAttachStateChangeListener(this);
        return true;
    }

    void setLocation(int location) {
        mLocation = location;
        if (mFinished || !mStarted)
            return;
        invalidate();
    }

    void setLightTheme(boolean light, boolean useTheme) {
        if (mFinished)
            return;
        if (mLight == light && mUseTheme == useTheme)
            return;
        mLight = light;
        mUseTheme = useTheme;
        mSwitchLayout.setLightTheme(light, useTheme);
        mMainLayout.setLightTheme(light, useTheme);
        mOverflowLayout.setLightTheme(light, useTheme);
        mSubLayout.setLightTheme(light, useTheme);
        mAnimationLayout.setLightTheme(light);
    }

    boolean isLightTheme() {
        return mLight;
    }

    void invalidate(FloatingActionMode mode) {
        if (mFinished || !mStarted)
            return;
        final boolean changed = mCallback.onPrepareActionMode(mode, mMenu);
        if (mFinished) {
            mCallback.onDestroyActionMode(mode);
            return;
        }
        mCallback.onGetContentRect(mode, mTarget, mContentBound);
        if (mFinished) {
            mCallback.onDestroyActionMode(mode);
            return;
        }
        if (changed) {
            mMenu.prepareToShow();
            mTarget.getWindowVisibleDisplayFrame(mViewPortOnScreen);
            final int maxWidth = mViewPortOnScreen.right - mViewPortOnScreen.left;
            mMainLayout.setData(mMenu, maxWidth);
            mHasOverflow = mOverflowLayout.setData(mMenu, mOverflowNeedSize);
            mSubLayout.setData(mMenu, mSubMaxSize, mode);
        }
        invalidate();
    }

    void invalidateContentRect(FloatingActionMode mode) {
        if (mFinished || !mStarted)
            return;
        mCallback.onGetContentRect(mode, mTarget, mContentBound);
        if (mFinished) {
            mCallback.onDestroyActionMode(mode);
            return;
        }
        invalidate();
    }

    private void invalidate() {
        updateLayout();
        applyLayout();
        mType = TYPE_MAIN;
        applyType();
        applyHide();
        mManager.updateViewLayout(mAnimationLayout, mAnimationParams);
        mManager.updateViewLayout(mSubLayout, mSubParams);
        mManager.updateViewLayout(mOverflowLayout, mOverflowParams);
        mManager.updateViewLayout(mMainLayout, mMainParams);
        mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
    }

    void finish(FloatingActionMode mode, boolean immediate) {
        if (mFinished)
            return;
        mHidden = false;
        mFinished = true;
        if (mStarted) {
            if (immediate) {
                mManager.removeViewImmediate(mAnimationLayout);
                mManager.removeViewImmediate(mSubLayout);
                mManager.removeViewImmediate(mOverflowLayout);
                mManager.removeViewImmediate(mMainLayout);
                mManager.removeViewImmediate(mSwitchLayout);
            } else {
                mManager.removeView(mAnimationLayout);
                mManager.removeView(mSubLayout);
                mManager.removeView(mOverflowLayout);
                mManager.removeView(mMainLayout);
                mManager.removeView(mSwitchLayout);
            }
            mCallback.onDestroyActionMode(mode);
            mTarget.removeOnAttachStateChangeListener(this);
            final View root = mTarget.getRootView();
            root.removeOnLayoutChangeListener(this);
            root.removeOnAttachStateChangeListener(this);
            mTarget = null;
        }
    }

    void hide() {
        if (mFinished || !mStarted || mHidden)
            return;
        mAnimationLayout.cancel();
        mAnimationLayout.setVisibility(View.INVISIBLE);
        mMainParams.flags = computeFlags(mMainParams.flags, false);
        mMainLayout.setVisibility(View.INVISIBLE);
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
        mOverflowLayout.setVisibility(View.INVISIBLE);
        mSubParams.flags = computeFlags(mSubParams.flags, false);
        mSubLayout.setVisibility(View.INVISIBLE);
        mSwitchParams.flags = computeFlags(mSwitchParams.flags, false);
        mSwitchLayout.setVisibility(View.INVISIBLE);
        mManager.updateViewLayout(mAnimationLayout, mAnimationParams);
        mManager.updateViewLayout(mMainLayout, mMainParams);
        mManager.updateViewLayout(mOverflowLayout, mOverflowParams);
        mManager.updateViewLayout(mSubLayout, mSubParams);
        mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        mHidden = true;
    }

    void show() {
        if (mFinished || !mStarted || !mHidden)
            return;
        applyType();
        mManager.updateViewLayout(mAnimationLayout, mAnimationParams);
        mManager.updateViewLayout(mMainLayout, mMainParams);
        mManager.updateViewLayout(mOverflowLayout, mOverflowParams);
        mManager.updateViewLayout(mSubLayout, mSubParams);
        mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        mHidden = false;
    }

    boolean isHidden() {
        return mHidden;
    }

    boolean isFinished() {
        return mFinished;
    }

    void performActionItemClicked(FloatingActionMode mode, FloatingMenuItem item) {
        mCallback.onActionItemClicked(mode, item);
    }

    FloatingMenu getMenu() {
        return mMenu;
    }

    private int computeFlags(int curFlags, boolean touchable) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (!touchable)
            curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR;
        curFlags |= WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        return curFlags;
    }

    private void updateLayout() {
        mTarget.getWindowVisibleDisplayFrame(mViewPortOnScreen);
        final int maxWidth = mViewPortOnScreen.right - mViewPortOnScreen.left;
        mMainLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mMainSize.set(Math.min(maxWidth, mMainLayout.getMeasuredWidth()),
                Math.min(mMaxOverflowHeight, mMainLayout.getMeasuredHeight()));
        mOverflowSize.set(Math.min(maxWidth, mOverflowNeedSize.x),
                Math.min(mMaxOverflowHeight, mOverflowNeedSize.y));
        mSubSize.set(Math.min(maxWidth, mSubMaxSize.x),
                Math.min(mMaxOverflowHeight, mSubMaxSize.y));
        mAnimationSize.set(Math.max(mMainSize.x, Math.max(mOverflowSize.x, mSubSize.x)),
                Math.max(mMainSize.y, Math.max(mOverflowSize.y, mSubSize.y)));

        mTarget.getRootView().getLocationOnScreen(mTmpCoords);
        final int appScreenLocationLeft = mTmpCoords[0];
        final int appScreenLocationTop = mTmpCoords[1];
        mTarget.getLocationOnScreen(mTmpCoords);
        final int viewScreenLocationLeft = mTmpCoords[0];
        final int viewScreenLocationTop = mTmpCoords[1];
        final int offsetX = viewScreenLocationLeft - appScreenLocationLeft;
        final int offsetY = viewScreenLocationTop - appScreenLocationTop;

        final int baselineAbove = mContentBound.top + offsetY;
        final int baselineBelow = mContentBound.bottom + offsetY;

        final int aboveHeight = baselineAbove - mViewPortOnScreen.top;
        final int belowHeight = mViewPortOnScreen.bottom - baselineBelow;

        final int x = (mContentBound.left + offsetX + mContentBound.right + offsetX) >> 1;
        switch (mLocation) {
            case FloatingActionMode.LOCATION_BELOW_PRIORITY:
                if (belowHeight >= mAnimationSize.y) {
                    mBelow = true;
                    mReverse = false;
                } else if (aboveHeight >= mAnimationSize.y) {
                    mBelow = false;
                    mReverse = false;
                } else {
                    mBelow = true;
                    mReverse = true;
                }
                break;
            case FloatingActionMode.LOCATION_BELOW_ALWAYS:
                mBelow = true;
                mReverse = belowHeight < mAnimationSize.y;
                break;
            case FloatingActionMode.LOCATION_ABOVE_PRIORITY:
                if (aboveHeight >= mAnimationSize.y) {
                    mBelow = false;
                    mReverse = false;
                } else if (belowHeight >= mAnimationSize.y) {
                    mBelow = true;
                    mReverse = false;
                } else {
                    mBelow = false;
                    mReverse = true;
                }
                break;
            case FloatingActionMode.LOCATION_ABOVE_ALWAYS:
                mBelow = false;
                mReverse = aboveHeight < mAnimationSize.y;
                break;
        }
        final int y = mBelow ? baselineBelow : baselineAbove;
        mMainCoordinates.set(Math.min(Math.max(offsetX, Math.round(x - mMainSize.x * 0.5f)),
                maxWidth + offsetX - mMainSize.x),
                Math.min(Math.max(offsetY, (mBelow ? y : y - mMainSize.y)),
                        mViewPortOnScreen.bottom - mViewPortOnScreen.top + offsetY - mMainSize.y));
        mSwitchCoordinates.set(mMainCoordinates.x + mMainSize.x - mSwitchSize.x - mMargin,
                mMainCoordinates.y + mMargin);

        if (mReverse) {
            // 向上展开
            mOverflowCoordinates.set(mMainCoordinates.x + mMainSize.x - mOverflowSize.x,
                    mMainCoordinates.y + mMainSize.y - mOverflowSize.y);
            mOverflowLayout.setReverse(true);
            mSubCoordinates.set(mMainCoordinates.x + mMainSize.x - mSubSize.x,
                    mMainCoordinates.y + mMainSize.y - mSubSize.y);
            mSubLayout.setReverse(true);
            mAnimationCoordinates.set(mMainCoordinates.x + mMainSize.x - mAnimationSize.x,
                    mMainCoordinates.y + mMainSize.y - mAnimationSize.y);
            mAnimationLayout.setReverse(true);

        } else {
            // 向下展开
            mOverflowCoordinates.set(mMainCoordinates.x + mMainSize.x - mOverflowSize.x,
                    mMainCoordinates.y);
            mOverflowLayout.setReverse(false);
            mSubCoordinates.set(mMainCoordinates.x + mMainSize.x - mSubSize.x,
                    mMainCoordinates.y);
            mSubLayout.setReverse(false);
            mAnimationCoordinates.set(mMainCoordinates.x + mMainSize.x - mAnimationSize.x,
                    mMainCoordinates.y);
            mAnimationLayout.setReverse(false);
        }
    }

    private void applyLayout() {
        mMainParams.width = mMainSize.x;
        mMainParams.height = mMainSize.y;
        mMainParams.x = mMainCoordinates.x;
        mMainParams.y = mMainCoordinates.y;

        mSwitchParams.width = mSwitchSize.x;
        mSwitchParams.height = mSwitchSize.y;
        mSwitchParams.x = mSwitchCoordinates.x;
        mSwitchParams.y = mSwitchCoordinates.y;

        mOverflowParams.width = mOverflowSize.x;
        mOverflowParams.height = mOverflowSize.y;
        mOverflowParams.x = mOverflowCoordinates.x;
        mOverflowParams.y = mOverflowCoordinates.y;

        mSubParams.width = mSubSize.x;
        mSubParams.height = mSubSize.y;
        mSubParams.x = mSubCoordinates.x;
        mSubParams.y = mSubCoordinates.y;

        mAnimationParams.width = mAnimationSize.x;
        mAnimationParams.height = mAnimationSize.y;
        mAnimationParams.x = mAnimationCoordinates.x;
        mAnimationParams.y = mAnimationCoordinates.y;
    }

    private void applyType() {
        mAnimationLayout.cancel();
        mAnimationLayout.setVisibility(View.VISIBLE);
        mAnimationParams.flags = computeFlags(mAnimationParams.flags, false);
        if (mType == TYPE_MAIN) {
            mAnimationLayout.setSize(mMainSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, true);
            mMainLayout.setVisibility(View.VISIBLE);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mOverflowLayout.setVisibility(View.INVISIBLE);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSubLayout.setVisibility(View.INVISIBLE);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, mHasOverflow);
            mSwitchLayout.setVisibility(mHasOverflow ? View.VISIBLE : View.INVISIBLE);
            mSwitchLayout.setOverflow(false);
        } else if (mType == TYPE_OVERFLOW) {
            mAnimationLayout.setSize(mOverflowSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mMainLayout.setVisibility(View.INVISIBLE);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, true);
            mOverflowLayout.setVisibility(View.VISIBLE);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSubLayout.setVisibility(View.INVISIBLE);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchLayout.setVisibility(View.VISIBLE);
            mSwitchLayout.setArrow(false);
        } else if (mType == TYPE_SUB) {
            mAnimationLayout.setSize(mSubSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mMainLayout.setVisibility(View.INVISIBLE);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mOverflowLayout.setVisibility(View.INVISIBLE);
            mSubParams.flags = computeFlags(mSubParams.flags, true);
            mSubLayout.setVisibility(View.VISIBLE);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchLayout.setVisibility(View.VISIBLE);
            mSwitchLayout.setArrow(false);
        }
    }

    private void applyHide() {
        if (mHidden) {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mMainLayout.setVisibility(View.INVISIBLE);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mOverflowLayout.setVisibility(View.INVISIBLE);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSubLayout.setVisibility(View.INVISIBLE);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, false);
            mSwitchLayout.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private long getAdjustedDuration(Point form, Point to, long originalDuration) {
        double transitionDurationScale = 0;
        if (form != null && to != null) {
            final int w = form.x - to.x;
            final int h = form.y - to.y;
            transitionDurationScale = Math.sqrt(w * w + h * h) /
                    mMainLayout.getResources().getDisplayMetrics().density;
        }
        if (transitionDurationScale < 150) {
            return Math.max(originalDuration - 50, 0);
        } else if (transitionDurationScale > 300) {
            return originalDuration + 50;
        }
        return originalDuration;
    }

    // Listener
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        invalidateContentRect(mMode);
    }

    @Override
    public void onViewAttachedToWindow(View v) {
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        finish(mMode, true);
    }

    @Override
    public void onClick(View v) {
        switch (mType) {
            case TYPE_MAIN:
                mType = TYPE_OVERFLOW;
                mPreviousType = TYPE_MAIN;
                mSwitchLayout.setArrow(true);
                mAnimationLayout.setSize(mOverflowSize, true,
                        getAdjustedDuration(mMainSize, mOverflowSize, DURATION));
                break;
            case TYPE_OVERFLOW:
                mType = TYPE_MAIN;
                mPreviousType = TYPE_OVERFLOW;
                mSwitchLayout.setOverflow(true);
                mAnimationLayout.setSize(mMainSize, true,
                        getAdjustedDuration(mOverflowSize, mMainSize, DURATION));
                break;
            case TYPE_SUB:
                mType = mPreviousType;
                mPreviousType = TYPE_SUB;
                if (mType == TYPE_MAIN) {
                    mSwitchLayout.setOverflow(true);
                    mAnimationLayout.setSize(mMainSize, true,
                            getAdjustedDuration(mSubSize, mMainSize, DURATION));
                } else {
                    mAnimationLayout.setSize(mOverflowSize, true,
                            getAdjustedDuration(mSubSize, mOverflowSize, DURATION));
                }
                break;
        }
    }

    @Override
    public void onAnimationStart(SwitchAnimation animation) {
        mMainParams.flags = computeFlags(mMainParams.flags, false);
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
        mSubParams.flags = computeFlags(mSubParams.flags, false);
        mSwitchParams.flags = computeFlags(mSwitchParams.flags, false);
        mManager.updateViewLayout(mMainLayout, mMainParams);
        mManager.updateViewLayout(mOverflowLayout, mOverflowParams);
        mManager.updateViewLayout(mSubLayout, mSubParams);
        mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        if (mPreviousType == TYPE_MAIN && mType == TYPE_OVERFLOW) {
            mMainLayout.setAlpha(1);
            mMainLayout.setCrop(mOverflowSize);
            mMainLayout.setVisibility(View.VISIBLE);
            mOverflowLayout.setAlpha(0);
            mOverflowLayout.setCrop(mMainSize);
            mOverflowLayout.setVisibility(View.VISIBLE);
            if (mReverse)
                mSwitchAnimationCoordinates.set(mOverflowCoordinates.x + mMargin,
                        mOverflowCoordinates.y + mOverflowSize.y - mSwitchSize.y - mMargin);
            else
                mSwitchAnimationCoordinates.set(mOverflowCoordinates.x + mMargin,
                        mOverflowCoordinates.y + mMargin);
        } else if (mPreviousType == TYPE_OVERFLOW && mType == TYPE_MAIN) {
            mMainLayout.setAlpha(0);
            mMainLayout.setCrop(mOverflowSize);
            mMainLayout.setVisibility(View.VISIBLE);
            mOverflowLayout.setAlpha(1);
            mOverflowLayout.setCrop(mMainSize);
            mOverflowLayout.setVisibility(View.VISIBLE);
            mSwitchAnimationCoordinates.set(mMainCoordinates.x + mMainSize.x - mSwitchSize.x
                    - mMargin, mMainCoordinates.y + mMargin);
        } else if (mPreviousType == TYPE_MAIN && mType == TYPE_SUB) {
            mMainLayout.setAlpha(1);
            mMainLayout.setCrop(mSubSize);
            mMainLayout.setVisibility(View.VISIBLE);
            mSubLayout.setAlpha(0);
            mSubLayout.setCrop(mMainSize);
            mSubLayout.setVisibility(View.VISIBLE);
            if (mHasOverflow) {
                mSwitchLayout.setArrow(true);
            } else {
                mSwitchLayout.setAlpha(0);
                mSwitchLayout.setVisibility(View.VISIBLE);
                mSwitchLayout.setArrow(true);
            }
            if (mReverse)
                mSwitchAnimationCoordinates.set(mSubCoordinates.x + mMargin,
                        mSubCoordinates.y + mSubSize.y - mSwitchSize.y - mMargin);
            else
                mSwitchAnimationCoordinates.set(mSubCoordinates.x + mMargin,
                        mSubCoordinates.y + mMargin);
        } else if (mPreviousType == TYPE_SUB && mType == TYPE_MAIN) {
            mMainLayout.setAlpha(0);
            mMainLayout.setCrop(mSubSize);
            mMainLayout.setVisibility(View.VISIBLE);
            mSubLayout.setAlpha(1);
            mSubLayout.setCrop(mMainSize);
            mSubLayout.setVisibility(View.VISIBLE);
            if (!mHasOverflow)
                mSwitchLayout.setAlpha(1);
            mSwitchAnimationCoordinates.set(mMainCoordinates.x + mMainSize.x - mSwitchSize.x
                    - mMargin, mMainCoordinates.y + mMargin);
        } else if (mPreviousType == TYPE_OVERFLOW && mType == TYPE_SUB) {
            mOverflowLayout.setAlpha(1);
            mOverflowLayout.setCrop(mSubSize);
            mOverflowLayout.setVisibility(View.VISIBLE);
            mSubLayout.setAlpha(0);
            mSubLayout.setCrop(mOverflowSize);
            mSubLayout.setVisibility(View.VISIBLE);
            if (mReverse)
                mSwitchAnimationCoordinates.set(mSubCoordinates.x + mMargin,
                        mSubCoordinates.y + mSubSize.y - mSwitchSize.y - mMargin);
            else
                mSwitchAnimationCoordinates.set(mSubCoordinates.x + mMargin,
                        mSubCoordinates.y + mMargin);
        } else if (mPreviousType == TYPE_SUB && mType == TYPE_OVERFLOW) {
            mOverflowLayout.setAlpha(0);
            mOverflowLayout.setCrop(mSubSize);
            mOverflowLayout.setVisibility(View.VISIBLE);
            mSubLayout.setAlpha(1);
            mSubLayout.setCrop(mOverflowSize);
            mSubLayout.setVisibility(View.VISIBLE);
            if (mReverse)
                mSwitchAnimationCoordinates.set(mOverflowCoordinates.x + mMargin,
                        mOverflowCoordinates.y + mOverflowSize.y - mSwitchSize.y - mMargin);
            else
                mSwitchAnimationCoordinates.set(mOverflowCoordinates.x + mMargin,
                        mOverflowCoordinates.y + mMargin);
        }
    }

    @Override
    public void onAnimationChange(SwitchAnimation animation, float interpolatedTime) {
        if (mPreviousType == TYPE_MAIN && mType == TYPE_OVERFLOW) {
            mMainLayout.setAnimationValue(interpolatedTime);
            mMainLayout.setAlpha(1 - interpolatedTime);
            mOverflowLayout.setAnimationValue(1 - interpolatedTime);
            mOverflowLayout.setAlpha(interpolatedTime);
            mAnimationLayout.setAnimationValue(interpolatedTime);
            mSwitchParams.x = mSwitchCoordinates.x + (int) ((mSwitchAnimationCoordinates.x -
                    mSwitchCoordinates.x) * interpolatedTime);
            mSwitchParams.y = mSwitchCoordinates.y + (int) ((mSwitchAnimationCoordinates.y -
                    mSwitchCoordinates.y) * interpolatedTime);
            mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        } else if (mPreviousType == TYPE_OVERFLOW && mType == TYPE_MAIN) {
            mMainLayout.setAnimationValue(1 - interpolatedTime);
            mMainLayout.setAlpha(interpolatedTime);
            mOverflowLayout.setAnimationValue(interpolatedTime);
            mOverflowLayout.setAlpha(1 - interpolatedTime);
            mAnimationLayout.setAnimationValue(interpolatedTime);
            mSwitchParams.x = mSwitchCoordinates.x + (int) ((mSwitchAnimationCoordinates.x -
                    mSwitchCoordinates.x) * interpolatedTime);
            mSwitchParams.y = mSwitchCoordinates.y + (int) ((mSwitchAnimationCoordinates.y -
                    mSwitchCoordinates.y) * interpolatedTime);
            mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        } else if (mPreviousType == TYPE_MAIN && mType == TYPE_SUB) {
            mMainLayout.setAnimationValue(interpolatedTime);
            mMainLayout.setAlpha(1 - interpolatedTime);
            mSubLayout.setAnimationValue(1 - interpolatedTime);
            mSubLayout.setAlpha(interpolatedTime);
            mAnimationLayout.setAnimationValue(interpolatedTime);
            if (!mHasOverflow)
                mSwitchLayout.setAlpha(interpolatedTime);
            mSwitchParams.x = mSwitchCoordinates.x + (int) ((mSwitchAnimationCoordinates.x -
                    mSwitchCoordinates.x) * interpolatedTime);
            mSwitchParams.y = mSwitchCoordinates.y + (int) ((mSwitchAnimationCoordinates.y -
                    mSwitchCoordinates.y) * interpolatedTime);
            mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        } else if (mPreviousType == TYPE_SUB && mType == TYPE_MAIN) {
            mMainLayout.setAnimationValue(1 - interpolatedTime);
            mMainLayout.setAlpha(interpolatedTime);
            mSubLayout.setAnimationValue(interpolatedTime);
            mSubLayout.setAlpha(1 - interpolatedTime);
            mAnimationLayout.setAnimationValue(interpolatedTime);
            if (!mHasOverflow)
                mSwitchLayout.setAlpha(1 - interpolatedTime);
            mSwitchParams.x = mSwitchCoordinates.x + (int) ((mSwitchAnimationCoordinates.x -
                    mSwitchCoordinates.x) * interpolatedTime);
            mSwitchParams.y = mSwitchCoordinates.y + (int) ((mSwitchAnimationCoordinates.y -
                    mSwitchCoordinates.y) * interpolatedTime);
            mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        } else if (mPreviousType == TYPE_OVERFLOW && mType == TYPE_SUB) {
            mOverflowLayout.setAnimationValue(interpolatedTime);
            mOverflowLayout.setAlpha(1 - interpolatedTime);
            mSubLayout.setAnimationValue(1 - interpolatedTime);
            mSubLayout.setAlpha(interpolatedTime);
            mAnimationLayout.setAnimationValue(interpolatedTime);
            mSwitchParams.x = mSwitchCoordinates.x + (int) ((mSwitchAnimationCoordinates.x -
                    mSwitchCoordinates.x) * interpolatedTime);
            mSwitchParams.y = mSwitchCoordinates.y + (int) ((mSwitchAnimationCoordinates.y -
                    mSwitchCoordinates.y) * interpolatedTime);
            mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        } else if (mPreviousType == TYPE_SUB && mType == TYPE_OVERFLOW) {
            mOverflowLayout.setAnimationValue(1 - interpolatedTime);
            mOverflowLayout.setAlpha(interpolatedTime);
            mSubLayout.setAnimationValue(interpolatedTime);
            mSubLayout.setAlpha(1 - interpolatedTime);
            mAnimationLayout.setAnimationValue(interpolatedTime);
            mSwitchParams.x = mSwitchCoordinates.x + (int) ((mSwitchAnimationCoordinates.x -
                    mSwitchCoordinates.x) * interpolatedTime);
            mSwitchParams.y = mSwitchCoordinates.y + (int) ((mSwitchAnimationCoordinates.y -
                    mSwitchCoordinates.y) * interpolatedTime);
            mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
        }
    }

    @Override
    public void onAnimationEnd(SwitchAnimation animation) {
        if (mPreviousType == TYPE_MAIN && mType == TYPE_OVERFLOW) {
            mMainLayout.setVisibility(View.INVISIBLE);
            mMainLayout.setCrop(null);
            mMainLayout.setAlpha(1);
            mOverflowLayout.setCrop(null);
            mOverflowLayout.setAlpha(1);
            mOverflowLayout.awakenScrollBar();
            mAnimationLayout.setSize(mOverflowSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, true);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchCoordinates.set(mSwitchAnimationCoordinates.x, mSwitchAnimationCoordinates.y);
            mSwitchParams.x = mSwitchCoordinates.x;
            mSwitchParams.y = mSwitchCoordinates.y;
        } else if (mPreviousType == TYPE_OVERFLOW && mType == TYPE_MAIN) {
            mMainLayout.setCrop(null);
            mMainLayout.setAlpha(1);
            mOverflowLayout.setVisibility(View.INVISIBLE);
            mOverflowLayout.setCrop(null);
            mOverflowLayout.setAlpha(1);
            mAnimationLayout.setSize(mMainSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, true);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchCoordinates.set(mSwitchAnimationCoordinates.x, mSwitchAnimationCoordinates.y);
            mSwitchParams.x = mSwitchCoordinates.x;
            mSwitchParams.y = mSwitchCoordinates.y;
        } else if (mPreviousType == TYPE_MAIN && mType == TYPE_SUB) {
            mMainLayout.setVisibility(View.INVISIBLE);
            mMainLayout.setCrop(null);
            mMainLayout.setAlpha(1);
            mSubLayout.setCrop(null);
            mSubLayout.setAlpha(1);
            mSubLayout.awakenScrollBar();
            mSwitchLayout.setAlpha(1);
            mAnimationLayout.setSize(mSubSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mSubParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchCoordinates.set(mSwitchAnimationCoordinates.x, mSwitchAnimationCoordinates.y);
            mSwitchParams.x = mSwitchCoordinates.x;
            mSwitchParams.y = mSwitchCoordinates.y;
        } else if (mPreviousType == TYPE_SUB && mType == TYPE_MAIN) {
            mMainLayout.setCrop(null);
            mMainLayout.setAlpha(1);
            mSubLayout.setVisibility(View.INVISIBLE);
            mSubLayout.clear();
            mSubLayout.setCrop(null);
            mSubLayout.setAlpha(1);
            if (mHasOverflow) {
                mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            } else {
                mSwitchLayout.setVisibility(View.INVISIBLE);
                mSwitchLayout.setAlpha(1);
                mSwitchParams.flags = computeFlags(mSwitchParams.flags, false);
            }
            mAnimationLayout.setSize(mMainSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, true);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSwitchCoordinates.set(mSwitchAnimationCoordinates.x, mSwitchAnimationCoordinates.y);
            mSwitchParams.x = mSwitchCoordinates.x;
            mSwitchParams.y = mSwitchCoordinates.y;
        } else if (mPreviousType == TYPE_OVERFLOW && mType == TYPE_SUB) {
            mOverflowLayout.setVisibility(View.INVISIBLE);
            mOverflowLayout.setCrop(null);
            mOverflowLayout.setAlpha(1);
            mSubLayout.setCrop(null);
            mSubLayout.setAlpha(1);
            mSubLayout.awakenScrollBar();
            mAnimationLayout.setSize(mSubSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, false);
            mSubParams.flags = computeFlags(mSubParams.flags, true);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchCoordinates.set(mSwitchAnimationCoordinates.x, mSwitchAnimationCoordinates.y);
            mSwitchParams.x = mSwitchCoordinates.x;
            mSwitchParams.y = mSwitchCoordinates.y;
        } else if (mPreviousType == TYPE_SUB && mType == TYPE_OVERFLOW) {
            mOverflowLayout.setCrop(null);
            mOverflowLayout.setAlpha(1);
            mOverflowLayout.awakenScrollBar();
            mSubLayout.setVisibility(View.INVISIBLE);
            mSubLayout.clear();
            mSubLayout.setCrop(null);
            mSubLayout.setAlpha(1);
            mAnimationLayout.setSize(mOverflowSize, false, 0);
            mMainParams.flags = computeFlags(mMainParams.flags, false);
            mOverflowParams.flags = computeFlags(mOverflowParams.flags, true);
            mSubParams.flags = computeFlags(mSubParams.flags, false);
            mSwitchParams.flags = computeFlags(mSwitchParams.flags, true);
            mSwitchCoordinates.set(mSwitchAnimationCoordinates.x, mSwitchAnimationCoordinates.y);
            mSwitchParams.x = mSwitchCoordinates.x;
            mSwitchParams.y = mSwitchCoordinates.y;
        }
        mManager.updateViewLayout(mMainLayout, mMainParams);
        mManager.updateViewLayout(mOverflowLayout, mOverflowParams);
        mManager.updateViewLayout(mSubLayout, mSubParams);
        mManager.updateViewLayout(mSwitchLayout, mSwitchParams);
    }

    @Override
    public void onMainItemClick(FloatingMenuItem item) {
        performItemClick(item);
    }

    @Override
    public void onOverflowItemClick(FloatingMenuItem item) {
        performItemClick(item);
    }

    @Override
    public void onSubItemClick(FloatingMenuItem item) {
        performItemClick(item);
    }

    private void performItemClick(FloatingMenuItem item) {
        if (mCallback.onActionItemClicked(mMode, item))
            return;
        // 检查是否存在次级菜单
        showSubMenu(item);
    }

    private void showSubMenu(FloatingMenuItem item) {
        if (item == null || !item.hasSubMenu())
            return;
        mSubLayout.setData(item, mSubNeedSize);
        mTarget.getWindowVisibleDisplayFrame(mViewPortOnScreen);
        final int maxWidth = mViewPortOnScreen.right - mViewPortOnScreen.left;
        final int subWidth = Math.min(maxWidth, mSubNeedSize.x);
        final int subHeight = Math.min(mMaxOverflowHeight, mSubNeedSize.y);
        mSubSize.set(subWidth, subHeight);
        if (mReverse) {
            mSubCoordinates.set(mMainCoordinates.x + mMainSize.x - subWidth,
                    mMainCoordinates.y + mMainSize.y - subHeight);
            mSubLayout.setReverse(true);
        } else {
            mSubCoordinates.set(mMainCoordinates.x + mMainSize.x - subWidth,
                    mMainCoordinates.y);
            mSubLayout.setReverse(false);
        }
        mSubParams.flags = computeFlags(mSubParams.flags, false);
        mSubParams.width = mSubSize.x;
        mSubParams.height = mSubSize.y;
        mSubParams.x = mSubCoordinates.x;
        mSubParams.y = mSubCoordinates.y;
        mSubLayout.setVisibility(View.INVISIBLE);
        mManager.updateViewLayout(mSubLayout, mSubParams);
        if (mType == TYPE_MAIN) {
            mPreviousType = TYPE_MAIN;
            mType = TYPE_SUB;
            mAnimationLayout.setSize(mSubSize, true,
                    getAdjustedDuration(mMainSize, mSubSize, DURATION));
        } else {
            mPreviousType = TYPE_OVERFLOW;
            mType = TYPE_SUB;
            mAnimationLayout.setSize(mSubSize, true,
                    getAdjustedDuration(mOverflowSize, mSubSize, DURATION));
        }
    }
}
