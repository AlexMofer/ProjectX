package am.widget.floatingactionmode.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
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
 *
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class ViewManager {
    private final FloatingActionMode mMode;
    private final FloatingActionMode.Callback mCallback;
    private final WindowManager mManager;
    private final int mMargin;
    private final Rect mViewPortOnScreen = new Rect();
    private final int[] mTmpCoords = new int[2];

    private final AnimationLayout mAnimation;
    private final WindowManager.LayoutParams mAnimationParams = new WindowManager.LayoutParams();

    private final OverflowButton mButton;
    private final WindowManager.LayoutParams mButtonParams = new WindowManager.LayoutParams();

    private final MainLayout mMain;
    private final WindowManager.LayoutParams mMainParams = new WindowManager.LayoutParams();

    private final OverflowListView mOverflow;
    private final WindowManager.LayoutParams mOverflowParams = new WindowManager.LayoutParams();


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
        mOverflowParams.format = PixelFormat.TRANSPARENT;
        mOverflowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mOverflowParams.gravity = Gravity.START | Gravity.TOP;
    }

    void invalidateViewData(View target, FloatingMenuImpl menu, Rect bound) {
        target.getWindowVisibleDisplayFrame(mViewPortOnScreen);
        final int maxWidth = mViewPortOnScreen.right - mViewPortOnScreen.left;
        final int maxHeight = mViewPortOnScreen.bottom - mViewPortOnScreen.top;
        mAnimationParams.x = mViewPortOnScreen.left;
        mAnimationParams.y = mViewPortOnScreen.top;
        mAnimationParams.width = maxWidth;
        mAnimationParams.height = maxHeight;
        // 设置菜单数据
        menu.prepareToShow();
        final int maxMainWidth = maxWidth - mMargin - mMargin;
        final int overflowButtonWidth = mButton.getSize();
        mMain.setData(menu, maxMainWidth, overflowButtonWidth);
        mMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        Size mOverflowSize = new Size();
        if (menu.hasMoreMenu()) {
            mOverflow.setData(menu, mOverflowSize);// TODO
        }


        // 刷新视图数据 TODO
        mAnimation.setBound(new Rect(40, 100, 1000, 400));

        mButtonParams.x = mViewPortOnScreen.left + 40;
        mButtonParams.y = mViewPortOnScreen.top + 100;
        mButtonParams.width = 300;
        mButtonParams.height = 300;
        mButtonParams.flags = computeFlags(mButtonParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());
        mButton.setArrow(false);

        mMainParams.x = mViewPortOnScreen.left + 40;
        mMainParams.y = mViewPortOnScreen.top + 400;
        mMainParams.width = mMain.getMeasuredWidth();
        mMainParams.height = mMain.getMeasuredHeight();
        mMainParams.flags = computeFlags(mMainParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());

        mOverflowParams.x = mViewPortOnScreen.left + 40;
        mOverflowParams.y = mViewPortOnScreen.top + 600;
        mOverflowParams.width = mOverflowSize.width;
        mOverflowParams.height = mOverflowSize.height - 300;
        mOverflowParams.flags = computeFlags(mOverflowParams.flags, true,
                mMode.isClippingEnabled(), mMode.isLayoutInScreenEnabled());
    }

    void updateViewLayout() {
        // 更新窗口 TODO
        mManager.updateViewLayout(mAnimation, mAnimationParams);

        mManager.updateViewLayout(mButton, mButtonParams);
        mManager.updateViewLayout(mMain, mMainParams);
        mManager.updateViewLayout(mOverflow, mOverflowParams);
    }

    void addView() {
        // 添加视图 TODO
        mManager.addView(mAnimation, mAnimationParams);
        mManager.addView(mButton, mButtonParams);
        mManager.addView(mMain, mMainParams);
        mManager.addView(mOverflow, mOverflowParams);
    }

    void removeView(boolean immediate) {
        // 移除布局 TODO
        if (immediate) {
            mManager.removeViewImmediate(mAnimation);
            mManager.removeViewImmediate(mButton);
            mManager.removeViewImmediate(mMain);
            mManager.removeViewImmediate(mOverflow);
        } else {
            mManager.removeView(mAnimation);
            mManager.removeView(mButton);
            mManager.removeView(mMain);
            mManager.removeView(mOverflow);
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
