package io.github.alexmofer.android.support.widget.disablenoinput;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * 禁用时无事件反馈
 * Created by Alex on 2026/2/3.
 */
public class HorizontalScrollView extends android.widget.HorizontalScrollView {

    public HorizontalScrollView(Context context) {
        super(context);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr,
                                int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isEnabled() && super.dispatchTouchEvent(ev);
    }

    @Override
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        return isEnabled() && super.dispatchGenericFocusedEvent(event);
    }

    @Override
    protected boolean dispatchGenericPointerEvent(MotionEvent event) {
        return isEnabled() && super.dispatchGenericPointerEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return isEnabled() && super.dispatchHoverEvent(event);
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {
        return isEnabled() && super.dispatchDragEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return isEnabled() && super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return isEnabled() && super.dispatchKeyShortcutEvent(event);
    }
}
