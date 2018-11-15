package am.widget.floatingactionmode.impl;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import am.widget.floatingactionmode.FloatingActionMode;
import am.widget.floatingactionmode.FloatingMenu;
import am.widget.floatingactionmode.FloatingMenuItem;

/**
 * Created by Xiang Zhicheng on 2018/11/13.
 */
public final class FloatingActionModeImpl implements View.OnLayoutChangeListener,
        View.OnAttachStateChangeListener {

    private final FloatingActionMode mMode;
    private final FloatingActionMode.Callback mCallback;
    private final FloatingMenuImpl mMenu;
    private final Rect mContentBound = new Rect();
    private final Rect tRect = new Rect();
    private final ViewManager mView;
    private View mTarget;
    private boolean mClippingEnabled;
    private boolean mLayoutInScreen;
    private boolean mStarted;
    private boolean mFinished;
    private boolean mHidden;

    public FloatingActionModeImpl(FloatingActionMode mode, View target,
                                  FloatingActionMode.Callback callback, int themeResId) {
        mMode = mode;
        mTarget = target;
        mCallback = callback;
        final Context context = target.getContext();
        mMenu = new FloatingMenuImpl(target.getContext());
        mView = new ViewManager(context, themeResId, mode, callback);
        final View root = mTarget.getRootView();
        root.addOnLayoutChangeListener(this);
        root.addOnAttachStateChangeListener(this);
        mTarget.addOnAttachStateChangeListener(this);
    }

    public void setLocation(int location) {
        if (mView.setLocation(location)) {
            if (!mStarted)
                return;
            if (mFinished)
                return;
            invalidateContentRect();
        }
    }

    public void setForceForward(boolean force) {
        if (mView.setForceForward(force)) {
            if (!mStarted)
                return;
            if (mFinished)
                return;
            invalidateContentRect();
        }
    }

    public boolean isClippingEnabled() {
        return mClippingEnabled;
    }

    public void setClippingEnabled(boolean enabled) {
        if (mClippingEnabled == enabled)
            return;
        mClippingEnabled = enabled;
        if (!mStarted)
            return;
        if (mFinished)
            return;
        invalidateContentRect();
    }

    public boolean isLayoutInScreenEnabled() {
        return mLayoutInScreen;
    }

    public void setLayoutInScreenEnabled(boolean enabled) {
        if (mLayoutInScreen == enabled)
            return;
        mLayoutInScreen = enabled;
        if (!mStarted)
            return;
        if (mFinished)
            return;
        invalidateContentRect();
    }

    public void start() {
        if (mStarted)
            return;
        if (mFinished)
            return;
        mCallback.onPrepareActionMode(mMode, mMenu);
        mCallback.onGetContentRect(mMode, mTarget, mContentBound);
        mView.invalidateViewData(mTarget, mMenu, mContentBound);
        mView.addView();
        mStarted = true;
    }

    public void invalidate() {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        invalidateContentRect(mCallback.onPrepareActionMode(mMode, mMenu));
    }

    public void invalidateContentRect() {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        invalidateContentRect(true);
    }

    public void finish() {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        finish(false);
    }

    public void hide() {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        if (mHidden)
            return;
        mHidden = true;
        mView.hide();
    }

    public void show() {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        if (!mHidden)
            return;
        mHidden = false;
        mView.show();
    }

    public boolean isHidden() {
        return mStarted && mHidden;
    }

    public boolean isFinished() {
        return mStarted && mFinished;
    }

    public void performActionItemClicked(FloatingMenuItem item) {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        mView.performActionItemClicked(item);
    }

    public void backToMain(boolean animate) {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        mView.backToMain(animate);
    }

    public void openOverflow(boolean animate) {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        mView.openOverflow(animate);
    }

    public FloatingMenu getMenu() {
        return mMenu;
    }

    // Listener
    @Override
    public void onViewAttachedToWindow(View v) {
        // ignore
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        finish(true);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (!mStarted)
            return;
        if (mFinished)
            return;
        invalidateContentRect(true);
    }

    private void invalidateContentRect(boolean force) {
        if (force) {
            mCallback.onGetContentRect(mMode, mTarget, mContentBound);
        } else {
            final Rect bound = tRect;
            mCallback.onGetContentRect(mMode, mTarget, bound);
            if (bound.equals(mContentBound))
                return;
            mContentBound.set(bound);
        }
        mView.invalidateViewData(mTarget, mMenu, mContentBound);
        mView.updateViewLayout();
    }

    private void finish(boolean immediate) {
        mView.removeView(immediate);
        mCallback.onDestroyActionMode(mMode);
        mFinished = true;
        mTarget.removeOnAttachStateChangeListener(this);
        final View root = mTarget.getRootView();
        root.removeOnLayoutChangeListener(this);
        root.removeOnAttachStateChangeListener(this);
        mTarget = null;
    }

}
