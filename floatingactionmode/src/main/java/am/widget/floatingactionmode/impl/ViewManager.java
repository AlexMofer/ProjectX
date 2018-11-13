package am.widget.floatingactionmode.impl;

import android.content.Context;
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
 * Created by Xiang Zhicheng on 2018/11/13.
 */
final class ViewManager {
    private final FloatingActionMode mMode;
    private final FloatingActionMode.Callback mCallback;
    private final WindowManager mManager;
    private final Rect mViewPortOnScreen = new Rect();
    private final int[] mTmpCoords = new int[2];

    private final AnimationLayout mAnimation;
    private final WindowManager.LayoutParams mAnimationParams = new WindowManager.LayoutParams();

    ViewManager(Context context, int themeResId,
                FloatingActionMode mode, FloatingActionMode.Callback callback) {
        mMode = mode;
        mCallback = callback;
        if (themeResId == 0) {
            themeResId = R.style.FloatingActionMode;
        }
        context = new ContextThemeWrapper(context, themeResId);
        mManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mAnimation = new AnimationLayout(context);
        mAnimationParams.format = PixelFormat.TRANSPARENT;
        mAnimationParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
        mAnimationParams.gravity = Gravity.START | Gravity.TOP;
        mAnimationParams.flags = computeFlags(mAnimationParams.flags, false,
                false, false);

    }

    void invalidateViewData(View target, FloatingMenuImpl menu, Rect bound) {
        target.getWindowVisibleDisplayFrame(mViewPortOnScreen);
        final int maxWidth = mViewPortOnScreen.right - mViewPortOnScreen.left;
        final int maxHeight = mViewPortOnScreen.bottom - mViewPortOnScreen.top;
        mAnimationParams.x = mViewPortOnScreen.left;
        mAnimationParams.y = mViewPortOnScreen.top;
        mAnimationParams.width = maxWidth;
        mAnimationParams.height = maxHeight;


        // 刷新视图数据 TODO
    }

    void updateViewLayout() {
        // 更新窗口 TODO
        mManager.updateViewLayout(mAnimation, mAnimationParams);
    }

    void addView() {
        // 添加视图 TODO
        mManager.addView(mAnimation, mAnimationParams);
    }

    void removeView(boolean immediate) {
        // 移除布局 TODO
        if (immediate) {
            mManager.removeViewImmediate(mAnimation);
        } else {
            mManager.removeView(mAnimation);
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
