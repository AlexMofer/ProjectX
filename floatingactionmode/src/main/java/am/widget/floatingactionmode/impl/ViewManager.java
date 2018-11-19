package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import am.widget.floatingactionmode.FloatingActionMode;
import am.widget.floatingactionmode.FloatingMenuItem;
import am.widget.floatingactionmode.R;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class ViewManager implements View.OnClickListener, AnimationLayout.OnAnimationListener {
    private static final int TYPE_MAIN = 0;
    private static final int TYPE_OVERFLOW = 1;
    private static final int TYPE_SUB = 2;
    private final FloatingActionMode mMode;
    private final FloatingMenuImpl mMenu;
    private final FloatingActionMode.Callback mCallback;
    private final WindowManager mManager;
    private final int mMargin;
    private final WindowParam mWindowParam = new WindowParam();
    private final Size mContentMaxSize = new Size();
    private final Point mMainButtonLocation = new Point();

    private final AnimationLayout mAnimation;
    private final WindowManager.LayoutParams mAnimationParams = new WindowManager.LayoutParams();

    private final OverflowButton mButton;
    private final WindowManager.LayoutParams mButtonParams = new WindowManager.LayoutParams();
    private final Point mOverflowButtonLocation = new Point();
    private final Point mSubButtonLocation = new Point();
    private final Size mMainSize = new Size();

    private final MainLayout mMain;
    private final WindowManager.LayoutParams mMainParams = new WindowManager.LayoutParams();
    private final Size mOverflowSize = new Size();
    private final Point mMainLocation = new Point();

    private final OverflowListView mOverflow;
    private final WindowManager.LayoutParams mOverflowParams = new WindowManager.LayoutParams();
    private final Point mOverflowLocation = new Point();
    private final Point mSubLocation = new Point();
    private int mLocation = FloatingActionMode.LOCATION_BELOW_PRIORITY;

    private final SubLayout mSub;
    private final WindowManager.LayoutParams mSubParams = new WindowManager.LayoutParams();
    private final Size mSubSize = new Size();
    private boolean mHasOverflow;
    private boolean mHasSub;
    private float mBaseLineX;
    private int mBaseLineY;
    private boolean mBelow;

    private int mType = TYPE_MAIN;
    private FloatingMenuItem mSubMenuItem;

    ViewManager(Context context, int themeResId, FloatingActionMode mode, FloatingMenuImpl menu,
                FloatingActionMode.Callback callback) {
        mMode = mode;
        mMenu = menu;
        mCallback = callback;
        context = new ContextThemeWrapper(context, themeResId);
        mManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        int margin = context.getResources().getDimensionPixelOffset(
                R.dimen.floatingActionModeMargin);
        @SuppressLint("CustomViewStyleable") final TypedArray custom =
                context.obtainStyledAttributes(R.styleable.FloatingActionMode);
        margin = custom.getDimensionPixelOffset(
                R.styleable.FloatingActionMode_floatingActionModeMargin, margin);
        custom.recycle();
        mMargin = margin;

        mAnimation = new AnimationLayout(context);
        mAnimation.setOnAnimationListener(this);
        mAnimationParams.format = PixelFormat.TRANSPARENT;
        mAnimationParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mAnimationParams.gravity = Gravity.START | Gravity.TOP;
        mAnimationParams.flags = computeFlags(mAnimationParams.flags, false,
                true, false, false);

        mButton = new OverflowButton(context);
        mButton.setOnClickListener(this);
        mButtonParams.format = PixelFormat.TRANSPARENT;
        mButtonParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mButtonParams.gravity = Gravity.START | Gravity.TOP;

        mMain = new MainLayout(context);
        mMainParams.format = PixelFormat.TRANSPARENT;
        mMainParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mMainParams.gravity = Gravity.START | Gravity.TOP;

        mOverflow = new OverflowListView(context);
        mOverflow.setPaddingSpace(mButton.getSize());
        mOverflowParams.format = PixelFormat.TRANSPARENT;
        mOverflowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mOverflowParams.gravity = Gravity.START | Gravity.TOP;

        mSub = new SubLayout(context);
        mSubParams.format = PixelFormat.TRANSPARENT;
        mSubParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mSubParams.gravity = Gravity.START | Gravity.TOP;
    }

    boolean setLocation(int location) {
        if (mLocation == location)
            return false;
        mLocation = location;
        return true;
    }

    void invalidateViewData(View target, FloatingMenuImpl menu, Rect bound) {
        final boolean layoutNoLimits = mMode.isLayoutNoLimitsEnabled();
        final boolean layoutInScreen = mMode.isLayoutInScreenEnabled();
        final boolean layoutInsetDecor = mMode.isLayoutInsetDecorEnabled();
        mWindowParam.getParam(target, bound, layoutNoLimits, layoutInScreen, layoutInsetDecor);
        getAnimationLayoutParams();
        final int maxWidth = mWindowParam.width - mMargin - mMargin;
        final int maxMainHeight = Math.max(mWindowParam.above - mMargin - mMargin,
                mWindowParam.below - mMargin - mMargin);
        menu.prepareToShow();
        mMain.setData(menu, maxWidth, mButton.getSize());
        mMain.measure(View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(maxMainHeight, View.MeasureSpec.AT_MOST));
        final int mainHeight = mMain.getMeasuredHeight();
        mMainSize.set(mMain.getMeasuredWidth(), mainHeight);
        getLocationParams(layoutNoLimits);
        mHasOverflow = menu.hasMoreMenu();
        getMainLayoutParams(layoutNoLimits);
        mContentMaxSize.width = maxWidth;
        if (mBelow)
            mContentMaxSize.height = Math.max(mWindowParam.below - mMargin - mMargin,
                    mWindowParam.height - mWindowParam.below + mainHeight);
        else
            mContentMaxSize.height = Math.max(mWindowParam.above - mMargin - mMargin,
                    mWindowParam.height - mWindowParam.above + mainHeight);
        mOverflowSize.set(0, 0);
        if (mHasOverflow) {
            final int overflowMaxWidth = mOverflow.setData(menu);
            final int overflowMaxHeight =
                    Math.min(mOverflow.getMaxHeight(), mContentMaxSize.height);
            mOverflow.measure(
                    View.MeasureSpec.makeMeasureSpec(overflowMaxWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(overflowMaxHeight, View.MeasureSpec.AT_MOST));
            mOverflowSize.set(mOverflow.getMeasuredWidth(), mOverflow.getMeasuredHeight());
            getOverflowLayoutParams(layoutNoLimits);
        }
        mSubSize.set(0, 0);
        mHasSub = mSub.hasSubMenu(menu);
        getButtonLayoutParamsWithoutSub();
        stopAllAnimation();
        FloatingMenuItem item = null;
        if (mType == TYPE_SUB) {
            item = mMenu.getSubItem(mSubMenuItem);
            if (item == null)
                mType = TYPE_MAIN;
        }
        if (mType == TYPE_MAIN) {
            setMain(layoutNoLimits, layoutInScreen, layoutInsetDecor);
        } else if (mType == TYPE_OVERFLOW) {
            setOverflow(layoutNoLimits, layoutInScreen, layoutInsetDecor);
        } else if (mType == TYPE_SUB) {
            setSub(item, layoutNoLimits, layoutInScreen, layoutInsetDecor);
        }
        if (mMode.isHidden())
            hide();
    }

    private void getLocationParams(boolean layoutNoLimits) {
        mBaseLineX = mWindowParam.focus.exactCenterX();
        if (layoutNoLimits) {
            switch (mLocation) {
                default:
                case FloatingActionMode.LOCATION_BELOW_PRIORITY:
                case FloatingActionMode.LOCATION_BELOW_ALWAYS:
                    mBelow = true;
                    mBaseLineY = mWindowParam.focus.bottom;
                    break;
                case FloatingActionMode.LOCATION_ABOVE_PRIORITY:
                case FloatingActionMode.LOCATION_ABOVE_ALWAYS:
                    mBelow = false;
                    mBaseLineY = mWindowParam.focus.top;
                    break;
            }
            return;
        }
        final int above = mWindowParam.above - mMargin - mMargin;
        final int below = mWindowParam.below - mMargin - mMargin;
        final int mainHeight = mMainSize.height;
        switch (mLocation) {
            default:
            case FloatingActionMode.LOCATION_BELOW_PRIORITY:
                if (below >= mainHeight) {
                    mBelow = true;
                    mBaseLineY = mWindowParam.focus.bottom;
                } else if (above >= mainHeight) {
                    mBelow = false;
                    mBaseLineY = mWindowParam.focus.top;
                } else {
                    // 布局溢出窗口
                    mBelow = true;
                    mBaseLineY = mWindowParam.focus.bottom;
                }
                break;
            case FloatingActionMode.LOCATION_BELOW_ALWAYS:
                mBelow = true;
                mBaseLineY = mWindowParam.focus.bottom;
                break;
            case FloatingActionMode.LOCATION_ABOVE_PRIORITY:
                if (above >= mainHeight) {
                    mBelow = false;
                    mBaseLineY = mWindowParam.focus.top;
                } else if (below >= mainHeight) {
                    mBelow = true;
                    mBaseLineY = mWindowParam.focus.bottom;
                } else {
                    // 布局溢出窗口
                    mBelow = false;
                    mBaseLineY = mWindowParam.focus.top;
                }
                break;
            case FloatingActionMode.LOCATION_ABOVE_ALWAYS:
                mBelow = false;
                mBaseLineY = mWindowParam.focus.top;
                break;
        }
    }

    private void getAnimationLayoutParams() {
        mAnimationParams.x = mWindowParam.x;
        mAnimationParams.y = mWindowParam.y;
        mAnimationParams.width = mWindowParam.width;
        mAnimationParams.height = mWindowParam.height;
    }

    private void getMainLayoutParams(boolean layoutNoLimits) {
        mMainParams.width = mMainSize.width;
        mMainParams.height = mMainSize.height;
        final int width = mHasOverflow ? mMainSize.width + mButton.getSize() : mMainSize.width;
        mMainLocation.x = Math.round(mWindowParam.x + mBaseLineX - width * 0.5f);
        if (mBelow) {
            mMainLocation.y = mWindowParam.y + mBaseLineY + mMargin;
        } else {
            mMainLocation.y = mWindowParam.y + mBaseLineY - mMargin - mMainSize.height;
        }
        if (layoutNoLimits) {
            mMainParams.x = mMainLocation.x;
            mMainParams.y = mMainLocation.y;
            return;
        }
        // 限定布局
        final int minX = mWindowParam.x + mMargin;
        final int maxX = mWindowParam.x + mWindowParam.width - mMargin - width;
        final int minY = mWindowParam.y + mMargin;
        final int maxY = mWindowParam.y + mWindowParam.height - mMainSize.height - mMargin;
        mMainLocation.x = Math.max(Math.min(mMainLocation.x, maxX), minX);
        mMainLocation.y = Math.max(Math.min(mMainLocation.y, maxY), minY);
        mMainParams.x = mMainLocation.x;
        mMainParams.y = mMainLocation.y;
    }

    private void getOverflowLayoutParams(boolean layoutNoLimits) {
        mOverflowParams.width = mOverflowSize.width;
        mOverflowParams.height = mOverflowSize.height;
        if (!mHasOverflow) {
            mOverflowLocation.x = 0;
            mOverflowLocation.y = 0;
            mOverflowParams.x = mOverflowLocation.x;
            mOverflowParams.y = mOverflowLocation.y;
            return;
        }
        mOverflowLocation.x = mMainLocation.x + mMainSize.width + mButton.getSize()
                - mOverflowSize.width;
        if (layoutNoLimits) {
            mOverflowLocation.y = mMainLocation.y;
            mOverflowParams.x = mOverflowLocation.x;
            mOverflowParams.y = mOverflowLocation.y;
            mOverflow.setReverse(false);
            return;
        }
        final int top = mWindowParam.y;
        final int bottom = mWindowParam.y + mWindowParam.height;
        if (mBelow) {
            if (bottom - mMargin - mOverflowSize.height >= mMainLocation.y) {
                mOverflowLocation.y = mMainLocation.y;
                mOverflow.setReverse(false);
            } else {
                mOverflowLocation.y = mMainLocation.y + mMainSize.height - mOverflowSize.height;
                mOverflow.setReverse(true);
            }
        } else {
            if (top + mMargin + mOverflowSize.height - mMainSize.height <= mMainLocation.y) {
                mOverflowLocation.y = mMainLocation.y + mMainSize.height - mOverflowSize.height;
                mOverflow.setReverse(true);
            } else {
                mOverflowLocation.y = mMainLocation.y;
                mOverflow.setReverse(false);
            }
        }
        mOverflowParams.x = mOverflowLocation.x;
        mOverflowParams.y = mOverflowLocation.y;
    }

    private void getSubLayoutParams(boolean layoutNoLimits) {
        mSubButtonLocation.x = 0;
        mSubButtonLocation.y = 0;
        if (!mSub.isBind())
            return;
        if (!layoutNoLimits) {
            final int maxWidth = mWindowParam.width - mMargin - mMargin;
            final int maxHeight;
            if (mBelow)
                maxHeight = Math.max(mWindowParam.below - mMargin - mMargin,
                        mWindowParam.height - mWindowParam.below + mMainSize.height);
            else
                maxHeight = Math.max(mWindowParam.above - mMargin - mMargin,
                        mWindowParam.height - mWindowParam.above + mMainSize.height);
            mSubSize.width = Math.min(mSubSize.width, maxWidth);
            mSubSize.height = Math.min(mSubSize.height, maxHeight);
        }
        mSubParams.width = mSubSize.width;
        mSubParams.height = mSubSize.height;
        if (!mHasSub) {
            mSubLocation.x = 0;
            mSubLocation.y = 0;
            mSubParams.x = mSubLocation.x;
            mSubParams.y = mSubLocation.y;
            mSubButtonLocation.x = mSubLocation.x;
            mSubButtonLocation.y = mMainLocation.y;
            return;
        }
        mSubLocation.x = mMainLocation.x + mMainSize.width + mButton.getSize()
                - mSubSize.width;
        if (layoutNoLimits) {
            mSubLocation.y = mMainLocation.y;
            mSubParams.x = mSubLocation.x;
            mSubParams.y = mSubLocation.y;
            mSub.setReverse(false);
            mSubButtonLocation.x = mSubLocation.x;
            mSubButtonLocation.y = mMainLocation.y;
            return;
        }
        final int maxX = mWindowParam.x + mWindowParam.width - mSubParams.width - mMargin;
        final int minX = mWindowParam.x + mMargin;
        mSubLocation.x = Math.max(Math.min(mSubLocation.x, maxX), minX);
        final int maxY = mWindowParam.y + mWindowParam.height - mSubParams.height - mMargin;
        final int minY = mWindowParam.y + mMargin;
        if (mOverflow.isReverse()) {
            mSubLocation.y = mMainLocation.y + mMainSize.height - mSubParams.height;
            if (mSubLocation.y < minY) {
                mSubLocation.y = mMainLocation.y;
                mSub.setReverse(false);
            } else
                mSub.setReverse(true);
        } else {
            mSubLocation.y = mMainLocation.y;
            if (mSubLocation.y > maxY) {
                mSubLocation.y = mMainLocation.y + mMainSize.height - mSubParams.height;
                mSub.setReverse(true);
            } else
                mSub.setReverse(false);
        }
        mSubParams.x = mSubLocation.x;
        mSubParams.y = mSubLocation.y;
        mSubButtonLocation.x = mSubLocation.x;
        mSubButtonLocation.y = mMainLocation.y;
    }

    private void getButtonLayoutParamsWithoutSub() {
        mButtonParams.width = mButton.getSize();
        mButtonParams.height = mMainSize.height;
        if (mHasOverflow) {
            mMainButtonLocation.x = mMainLocation.x + mMainSize.width;
            mMainButtonLocation.y = mMainLocation.y;
            mOverflowButtonLocation.x = mOverflowLocation.x;
            mOverflowButtonLocation.y = mMainLocation.y;
        } else {
            mMainButtonLocation.x = 0;
            mMainButtonLocation.y = 0;
            mOverflowButtonLocation.x = 0;
            mOverflowButtonLocation.y = 0;
        }
    }

    private void stopAllAnimation() {
        // TODO
    }

    private int computeFlags(int curFlags, boolean touchable, boolean layoutNoLimits,
                             boolean layoutInScreen, boolean layoutInsetDecor) {
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
        if (layoutNoLimits)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (layoutInScreen)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (layoutInsetDecor)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        return curFlags;
    }

    private void setMain(boolean layoutNoLimits, boolean layoutInScreen,
                         boolean layoutInsetDecor) {
        final int left = mMainLocation.x - mAnimationParams.x;
        final int top = mMainLocation.y - mAnimationParams.y;
        final int right = mMainLocation.x + mMainSize.width + mButton.getSize()
                - mAnimationParams.x;
        final int bottom = mMainLocation.y + mMainSize.height - mAnimationParams.y;
        mAnimation.setBound(left, top, right, bottom);
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mOverflow.setVisibility(View.INVISIBLE);
        mSubParams.flags = computeFlags(mSubParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mSub.setVisibility(View.INVISIBLE);
        if (mHasOverflow) {
            mButtonParams.x = mMainButtonLocation.x;
            mButtonParams.y = mMainButtonLocation.y;
            mButtonParams.flags = computeFlags(mButtonParams.flags, true,
                    layoutNoLimits, layoutInScreen, layoutInsetDecor);
            mButton.setOverflow(false);
            mButton.setVisibility(View.VISIBLE);
        } else {
            mButtonParams.x = 0;
            mButtonParams.y = 0;
            mButtonParams.flags = computeFlags(mButtonParams.flags, false,
                    layoutNoLimits, layoutInScreen, layoutInsetDecor);
            mButton.setOverflow(false);
            mButton.setVisibility(View.INVISIBLE);
        }
        mMainParams.flags = computeFlags(mMainParams.flags, true,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mMain.setVisibility(View.VISIBLE);
        mType = TYPE_MAIN;
    }

    private void setOverflow(boolean layoutNoLimits, boolean layoutInScreen, boolean
            layoutInsetDecor) {
        final int left = mOverflowLocation.x - mAnimationParams.x;
        final int top = mOverflowLocation.y - mAnimationParams.y;
        final int right = mOverflowLocation.x + mOverflowSize.width - mAnimationParams.x;
        final int bottom = mOverflowLocation.y + mOverflowSize.height - mAnimationParams.y;
        mAnimation.setBound(left, top, right, bottom);
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, true,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mOverflow.setVisibility(View.VISIBLE);
        mOverflow.awakenScrollBar();
        mSubParams.flags = computeFlags(mSubParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mSub.setVisibility(View.INVISIBLE);
        mButtonParams.x = mOverflowButtonLocation.x;
        mButtonParams.y = mOverflowButtonLocation.y;
        mButtonParams.flags = computeFlags(mButtonParams.flags, true,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mButton.setArrow(false);
        mButton.setVisibility(View.VISIBLE);
        mMainParams.flags = computeFlags(mMainParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mMain.setVisibility(View.INVISIBLE);
        mType = TYPE_OVERFLOW;
    }

    private void setSub(FloatingMenuItem item,
                        boolean layoutNoLimits, boolean layoutInScreen, boolean layoutInsetDecor) {
        mSub.setData(mSubMenuItem, mContentMaxSize.width, mContentMaxSize.height,
                mSubSize, mMode);
        getSubLayoutParams(layoutNoLimits);
        final int left = mSubLocation.x - mAnimationParams.x;
        final int top = mSubLocation.y - mAnimationParams.y;
        final int right = mSubLocation.x + mSubSize.width - mAnimationParams.x;
        final int bottom = mSubLocation.y + mSubSize.height - mAnimationParams.y;
        mAnimation.setBound(left, top, right, bottom);
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mOverflow.setVisibility(View.INVISIBLE);
        mSubParams.flags = computeFlags(mSubParams.flags, true,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mSub.setVisibility(View.VISIBLE);
        mSub.awakenScrollBar();
        mButtonParams.x = mSubButtonLocation.x;
        mButtonParams.y = mSubButtonLocation.y;
        mButtonParams.flags = computeFlags(mButtonParams.flags, true,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mButton.setArrow(false);
        mButton.setVisibility(View.VISIBLE);
        mMainParams.flags = computeFlags(mMainParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mMain.setVisibility(View.INVISIBLE);
        mType = TYPE_SUB;
        mSubMenuItem = item;
    }

    void updateViewLayout() {
        mManager.updateViewLayout(mAnimation, mAnimationParams);
        mManager.updateViewLayout(mOverflow, mOverflowParams);
        mManager.updateViewLayout(mSub, mSubParams);
        mManager.updateViewLayout(mButton, mButtonParams);
        mManager.updateViewLayout(mMain, mMainParams);
    }

    void addView() {
        mManager.addView(mAnimation, mAnimationParams);
        mManager.addView(mOverflow, mOverflowParams);
        mManager.addView(mSub, mSubParams);
        mManager.addView(mButton, mButtonParams);
        mManager.addView(mMain, mMainParams);
    }

    void removeView(boolean immediate) {
        if (immediate) {
            mManager.removeViewImmediate(mAnimation);
            mManager.removeViewImmediate(mOverflow);
            mManager.removeViewImmediate(mSub);
            mManager.removeViewImmediate(mButton);
            mManager.removeViewImmediate(mMain);
            mOverflow.clear();
            mSub.clear(mMode);
        } else {
            mManager.removeView(mAnimation);
            mManager.removeView(mOverflow);
            mManager.removeView(mSub);
            mManager.removeView(mButton);
            mManager.removeView(mMain);
            mOverflow.clear();
            mSub.clear(mMode);
        }
    }

    void hide() {
        mAnimation.setVisibility(View.GONE);
        mOverflow.setVisibility(View.GONE);
        mSub.setVisibility(View.GONE);
        mButton.setVisibility(View.GONE);
        mMain.setVisibility(View.GONE);
        final boolean layoutNoLimits = mMode.isLayoutNoLimitsEnabled();
        final boolean layoutInScreen = mMode.isLayoutInScreenEnabled();
        final boolean layoutInsetDecor = mMode.isLayoutInsetDecorEnabled();
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mSubParams.flags = computeFlags(mSubParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mButtonParams.flags = computeFlags(mButtonParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        mMainParams.flags = computeFlags(mMainParams.flags, false,
                layoutNoLimits, layoutInScreen, layoutInsetDecor);
        updateViewLayout();
    }

    void show() {
        final boolean layoutNoLimits = mMode.isLayoutNoLimitsEnabled();
        final boolean layoutInScreen = mMode.isLayoutInScreenEnabled();
        final boolean layoutInsetDecor = mMode.isLayoutInsetDecorEnabled();
        FloatingMenuItem item = null;
        if (mType == TYPE_SUB) {
            item = mMenu.getSubItem(mSubMenuItem);
            if (item == null) {
                setMain(layoutNoLimits, layoutInScreen, layoutInsetDecor);
                updateViewLayout();
                return;
            }
        }
        if (mType == TYPE_MAIN) {
            setMain(layoutNoLimits, layoutInScreen, layoutInsetDecor);
        } else if (mType == TYPE_OVERFLOW) {
            setOverflow(layoutNoLimits, layoutInScreen, layoutInsetDecor);
        } else if (mType == TYPE_SUB) {
            setSub(item, layoutNoLimits, layoutInScreen, layoutInsetDecor);
        }
        updateViewLayout();
    }


    void performActionItemClicked(FloatingMenuItem item) {
        // TODO
    }

    void backToMain(boolean animate) {
        if (mType == TYPE_MAIN)
            return;
        if (!animate) {
            setMain(mMode.isLayoutNoLimitsEnabled(), mMode.isLayoutInScreenEnabled(),
                    mMode.isLayoutInsetDecorEnabled());
            updateViewLayout();
            return;
        }
        if (mHasOverflow) {


            mButton.setOverflow(true);


            return;
        }

        // TODO
    }

    void openOverflow(boolean animate) {
        if (mType == TYPE_OVERFLOW)
            return;
        if (!animate) {
            setOverflow(mMode.isLayoutNoLimitsEnabled(), mMode.isLayoutInScreenEnabled(),
                    mMode.isLayoutInsetDecorEnabled());
            updateViewLayout();
            return;
        }

        final int left = mOverflowLocation.x - mAnimationParams.x;
        final int top = mOverflowLocation.y - mAnimationParams.y;
        final int right = mOverflowLocation.x + mOverflowSize.width - mAnimationParams.x;
        final int bottom = mOverflowLocation.y + mOverflowSize.height - mAnimationParams.y;
        mAnimation.setBound(left, top, right, bottom, 4000);// TODO


        mButton.setArrow(true);



        // TODO
    }

    // Listener
    @Override
    public void onClick(View v) {
        if (mType == TYPE_MAIN) {
            openOverflow(true);
        } else if (mType == TYPE_OVERFLOW) {
            backToMain(true);
        }
    }

    @Override
    public void onAnimationStart() {
        // TODO
    }

    @Override
    public void onAnimationChange(float interpolatedTime) {
        // TODO
    }

    @Override
    public void onAnimationEnd() {
        // TODO
    }
}
