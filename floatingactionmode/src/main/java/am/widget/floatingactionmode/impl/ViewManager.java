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
final class ViewManager {
    private final FloatingActionMode mMode;
    private final FloatingActionMode.Callback mCallback;
    private final WindowManager mManager;
    private final int mMargin;
    private final WindowParam mWindowParam = new WindowParam();
    private final Size mMainSize = new Size();

    private final AnimationLayout mAnimation;
    private final WindowManager.LayoutParams mAnimationParams = new WindowManager.LayoutParams();


    private final OverflowButton mButton;
    private final WindowManager.LayoutParams mButtonParams = new WindowManager.LayoutParams();

    private final MainLayout mMain;
    private final WindowManager.LayoutParams mMainParams = new WindowManager.LayoutParams();
    private final Point mMainLocation = new Point();
    private final Size mOverflowSize = new Size();

    private final OverflowListView mOverflow;
    private final WindowManager.LayoutParams mOverflowParams = new WindowManager.LayoutParams();
    private final SubLayout mSub;
    private final WindowManager.LayoutParams mSubParams = new WindowManager.LayoutParams();
    private final Size mSubSize = new Size();
    private final Size mSubMaxSize = new Size();
    private int mLocation = FloatingActionMode.LOCATION_BELOW_PRIORITY;
    private boolean mHasOverflow;
    private boolean mHasSub;
    private int mBaseLine;
    private boolean mReverse;
    private boolean mForceForward;

    ViewManager(Context context, int themeResId,
                FloatingActionMode mode, FloatingActionMode.Callback callback) {
        mMode = mode;
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
        mAnimationParams.format = PixelFormat.TRANSPARENT;
        mAnimationParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mAnimationParams.gravity = Gravity.START | Gravity.TOP;
        mAnimationParams.flags = computeFlags(mAnimationParams.flags, false,
                false, false);

        mButton = new OverflowButton(context);
        mButtonParams.format = PixelFormat.TRANSPARENT;
        mButtonParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mButtonParams.gravity = Gravity.START | Gravity.TOP;

        mMain = new MainLayout(context);
        mMainParams.format = PixelFormat.TRANSPARENT;
        mMainParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mMainParams.gravity = Gravity.START | Gravity.TOP;

        mOverflow = new OverflowListView(context);
        mOverflow.setTopSpace(mButton.getSize());
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

    boolean setForceForward(boolean force) {
        if (mForceForward == force)
            return false;
        mForceForward = force;
        return true;
    }

    void invalidateViewData(View target, FloatingMenuImpl menu, Rect bound) {
        // 获取窗口数据
        mWindowParam.getParam(target, bound);
        // 设置菜单数据
        menu.prepareToShow();
        final int maxMainWidth = mWindowParam.width - mMargin - mMargin;
        final int overflowButtonWidth = mButton.getSize();
        mMain.setData(menu, maxMainWidth, overflowButtonWidth);
        mMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mMainSize.set(mMain.getMeasuredWidth(), mMain.getMeasuredHeight());
        mHasOverflow = menu.hasMoreMenu();
        mOverflowSize.set(0, 0);
        if (mHasOverflow) {
            final int overflowMaxWidth = mOverflow.setData(menu);
            final int overflowMaxHeight = mOverflow.getMaxHeight();
            mOverflow.measure(
                    View.MeasureSpec.makeMeasureSpec(overflowMaxWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(overflowMaxHeight, View.MeasureSpec.AT_MOST));
            mOverflowSize.set(mOverflow.getMeasuredWidth(), mOverflow.getMeasuredHeight());
        }
        final int above = mWindowParam.above - mMargin - mMargin;
        final int below = mWindowParam.below - mMargin - mMargin;
        mSubMaxSize.set(0, 0);
        mHasSub = mSub.calculateMaxSize(menu, mSubMaxSize, maxMainWidth, Math.max(above, below));

        // 获取布局数据
        mAnimationParams.x = mWindowParam.x;
        mAnimationParams.y = mWindowParam.y;
        mAnimationParams.width = mWindowParam.width;
        mAnimationParams.height = mWindowParam.height;
        final int maxContentHeight = Math.max(mButton.getSize(),
                Math.max(mMainSize.height, Math.max(mOverflowSize.height, mSubMaxSize.height)));
        switch (mLocation) {
            default:
            case FloatingActionMode.LOCATION_BELOW_PRIORITY:
                if (below >= maxContentHeight) {
                    mBaseLine = mWindowParam.focus.bottom;
                    mReverse = false;
                    System.out.println("lalalalal---------------------------------bottom");
                } else if (above >= maxContentHeight) {
                    mBaseLine = mWindowParam.focus.top;
                    mReverse = false;
                    System.out.println("lalalalal---------------------------------top");
                }
                break;
            case FloatingActionMode.LOCATION_BELOW_ALWAYS:
                mBaseLine = mWindowParam.focus.bottom;
                break;
            case FloatingActionMode.LOCATION_ABOVE_PRIORITY:
                break;
            case FloatingActionMode.LOCATION_ABOVE_ALWAYS:
                mBaseLine = mWindowParam.focus.top;
                break;
        }
        getMainLayoutParams();

        // 刷新视图数据 TODO
        mAnimation.setBound(mWindowParam.focus);

        mButtonParams.x = mWindowParam.x + 40;
        mButtonParams.y = mWindowParam.y + 100;
        mButtonParams.width = 300;
        mButtonParams.height = 300;
        mButtonParams.flags = computeFlags(mButtonParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());
        mButton.setArrow(false);


        mOverflowParams.x = mWindowParam.x + 40;
        mOverflowParams.y = mWindowParam.y + 600;
        mOverflowParams.width = mOverflowSize.width;
        mOverflowParams.height = mOverflowSize.height;
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());

        FloatingMenuItem sub = null;
        final int count = menu.size();
        for (int i = 0; i < count; i++) {
            final FloatingMenuItem item = menu.getItem(i);
            if (!item.hasSubMenu())
                continue;
            sub = item;
            break;
        }
        if (sub != null) {
            mSub.setData(sub, maxMainWidth, Math.max(above, below), mSubSize, mMode, true);
        }
        mSubParams.x = mWindowParam.x + 300;
        mSubParams.y = mWindowParam.y + 600;
        mSubParams.width = mSubSize.width;
        mSubParams.height = mSubSize.height;
        mSubParams.flags = computeFlags(mSubParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());
    }

    private void getMainLayoutParams() {


        mMainParams.x = mWindowParam.x + 40;
        mMainParams.y = mWindowParam.y + 400;
        mMainParams.width = mMainSize.width;
        mMainParams.height = mMainSize.height;
        mMainParams.flags = computeFlags(mMainParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());

    }

    void updateViewLayout() {
        // 更新窗口 TODO
        mManager.updateViewLayout(mAnimation, mAnimationParams);
        mManager.updateViewLayout(mOverflow, mOverflowParams);
        mManager.updateViewLayout(mSub, mSubParams);
        mManager.updateViewLayout(mButton, mButtonParams);
        mManager.updateViewLayout(mMain, mMainParams);
    }

    void addView() {
        // 添加视图 TODO
        mManager.addView(mAnimation, mAnimationParams);
        mManager.addView(mOverflow, mOverflowParams);
        mManager.addView(mSub, mSubParams);
        mManager.addView(mButton, mButtonParams);
        mManager.addView(mMain, mMainParams);
    }

    void removeView(boolean immediate) {
        // 移除布局 TODO
        if (immediate) {
            mManager.removeViewImmediate(mAnimation);
            mManager.removeViewImmediate(mOverflow);
            mManager.removeViewImmediate(mSub);
            mManager.removeViewImmediate(mButton);
            mManager.removeViewImmediate(mMain);
            mSub.clear(mMode);
        } else {
            mManager.removeView(mAnimation);
            mManager.removeView(mOverflow);
            mManager.removeView(mSub);
            mManager.removeView(mButton);
            mManager.removeView(mMain);
            mSub.clear(mMode);
        }
    }

    void hide() {
        // TODO
    }

    void show() {
        // TODO
    }


    void performActionItemClicked(FloatingMenuItem item) {
        // TODO
    }

    void backToMain(boolean animate) {
        // TODO
    }

    void openOverflow(boolean animate) {
        // TODO
    }

    private int computeFlags(int curFlags, boolean touchable,
                             boolean clipping, boolean layoutInScreen) {
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
        if (!clipping)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (layoutInScreen)
            curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        return curFlags;
    }
}
