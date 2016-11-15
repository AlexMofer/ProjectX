package am.widget.multiactiontextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * 文字可点击TextView
 */
public class MultiActionTextView extends TextView {

    private static final int[] ATTRS = new int[]{android.R.attr.textColorHighlight};
    private int mHighlightColor = Color.TRANSPARENT;
    private OnClickListener mListener;
    private OnClickListener mSetListener;
    private boolean interceptClick = false;

    public MultiActionTextView(Context context) {
        super(context);
        initView(context, null);
    }

    public MultiActionTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MultiActionTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public MultiActionTextView(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setMovementMethod(LinkMovementMethod.getInstance());
        if (!isFocusable()) {
            setFocusable(true);
        }
        if (!isFocusableInTouchMode()) {
            setFocusableInTouchMode(true);
        }
        mListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (interceptClick || mSetListener == null)
                    return;
                mSetListener.onClick(view);
            }
        };
        if (attrs == null)
            return;
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        mHighlightColor = a.getColor(0, Color.TRANSPARENT);
        a.recycle();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isFocused()) {
                    requestFocus();
                }
                setInterceptClick(false);
                super.setHighlightColor(mHighlightColor);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                super.setHighlightColor(Color.TRANSPARENT);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setHighlightColor(int color) {
        mHighlightColor = color;
        super.setHighlightColor(color);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mSetListener = l;
        if (mSetListener == null) {
            super.setOnClickListener(null);
        } else {
            super.setOnClickListener(mListener);
        }
    }

    void setInterceptClick(boolean intercept) {
        interceptClick = intercept;
    }

    /**
     * 设置文本
     *
     * @param resId   文本资源ID
     * @param actions 文本可点击项
     */
    public final void setText(int resId, MultiActionClickableSpan... actions) {
        if (actions == null || actions.length <= 0) {
            setText(resId);
            return;
        }
        setText(getResources().getString(resId), actions);
    }

    /**
     * 设置文本
     *
     * @param text    文本
     * @param actions 文本可点击项
     */
    public final void setText(CharSequence text, MultiActionClickableSpan... actions) {
        if (actions == null || actions.length <= 0) {
            setText(text);
            return;
        }
        boolean needRequestFocus = false;
        SpannableString spannable = new SpannableString(text);
        for (MultiActionClickableSpan action : actions) {
            spannable.setSpan(action, action.getStart(), action.getEnd(),
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (action.needRequestFocus())
                needRequestFocus = true;
        }
        setText(spannable);
        if (needRequestFocus) {
            super.setOnClickListener(mListener);
        }
    }
}
