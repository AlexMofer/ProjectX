package am.widget.smoothinputlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * 顺滑的输入面板
 * Created by Alex on 2016/12/4.
 */

public class SmoothInputLayout extends LinearLayout {

    private int mMaxHeight = Integer.MIN_VALUE;
    private int mDefaultInputHeight;
    private int mInputHeight = 387 * 2;
    private int mInputEditTextId = 0;
    private EditText mInputEditText;
    private boolean mInputOpen = false;

    public SmoothInputLayout(Context context) {
        super(context);
        initView(null);
    }

    public SmoothInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    @TargetApi(11)
    public SmoothInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @TargetApi(21)
    @SuppressWarnings("unused")
    public SmoothInputLayout(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        int defaultInputHeight = (int) (387 * getResources().getDisplayMetrics().density);
        mInputEditTextId = NO_ID;
        TypedArray custom = getContext().obtainStyledAttributes(attrs,
                R.styleable.SmoothInputLayout);
        defaultInputHeight = custom.getDimensionPixelOffset(
                R.styleable.SmoothInputLayout_silDefaultInputHeight, defaultInputHeight);
        mInputEditTextId = custom.getResourceId(R.styleable.SmoothInputLayout_silInputEditText,
                mInputEditTextId);
        custom.recycle();
        setDefaultInputHeight(defaultInputHeight);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mInputEditTextId != NO_ID) {
            setInputEditText((EditText) findViewById(mInputEditTextId));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight > mMaxHeight) {
            mMaxHeight = measuredHeight;
        }
    }

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight < mMaxHeight) {
            // TODO 与最大值的差值很小可忽略
            mInputHeight = mMaxHeight - measuredHeight;
            mInputOpen = true;
            // 输入法弹出，隐藏功能面板
            System.out.println("onSizeChanged:输入法出现，高度：" + mInputHeight);
        } else {
            mInputOpen = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight < mMaxHeight) {
            mInputHeight = mMaxHeight - measuredHeight;
            System.out.println("onLayout:输入法出现，高度：" + mInputHeight);
        }
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 设置默认输入面板高度
     *
     * @param height 输入面板高度
     */
    public void setDefaultInputHeight(int height) {
        if (mDefaultInputHeight != height)
            mDefaultInputHeight = height;
    }

    /**
     * 设置输入框
     *
     * @param edit 输入框
     */
    public void setInputEditText(EditText edit) {
        if (mInputEditText != edit)
            mInputEditText = edit;
    }

    /**
     * 是否输入法已打开
     *
     * @return 是否输入法已打开
     */
    @SuppressWarnings("unused")
    public boolean isInputOpen() {
        return mInputOpen;
    }

    /**
     * 关闭输入面板
     *
     * @param clearFocus 是否清除输入框焦点
     */
    @SuppressWarnings("unused")
    public void closeInput(boolean clearFocus) {
        InputMethodManager imm = ((InputMethodManager) (getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        imm.hideSoftInputFromWindow(getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (clearFocus && mInputEditText != null) {
            setFocusable(true);
            setFocusableInTouchMode(true);
            mInputEditText.clearFocus();
        }
        // TODO 关闭其他面板
    }

    /**
     * 打开输入面板
     */
    @SuppressWarnings("unused")
    public void showInput() {
        if (mInputEditText == null) {
            return;
        }
        mInputEditText.requestFocus();
        mInputEditText.requestFocusFromTouch();
        InputMethodManager imm = ((InputMethodManager) (getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        imm.showSoftInput(mInputEditText, InputMethodManager.SHOW_IMPLICIT);
        // TODO 关闭其他面板
    }

}
