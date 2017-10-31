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

package am.widget.smoothinputlayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

/**
 * 顺滑的输入面板
 * Created by Alex on 2016/12/4.
 */
@SuppressWarnings("all")
public class SmoothInputLayout extends LinearLayout {
    public static final int DEFAULT_KEYBOARD_HEIGHT = 387;
    public static final int MIN_KEYBOARD_HEIGHT = 20;
    private static final String SP_KEYBOARD = "keyboard";
    private static final String KEY_HEIGHT = "height";
    private int mMaxKeyboardHeight = Integer.MIN_VALUE;
    private int mDefaultKeyboardHeight;
    private int mMinKeyboardHeight;
    private int mKeyboardHeight;
    private int mInputViewId;
    private View mInputView;
    private boolean mKeyboardOpen = false;
    private int mInputPaneId;
    private View mInputPane;
    private OnVisibilityChangeListener mListener;
    private OnKeyboardChangeListener keyboardChangeListener;
    private boolean mAutoSaveKeyboardHeight;
    private KeyboardProcessor mKeyboardProcessor;
    private boolean tShowInputPane = false;

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
        int defaultInputHeight = (int) (DEFAULT_KEYBOARD_HEIGHT *
                getResources().getDisplayMetrics().density);
        int minInputHeight = (int) (MIN_KEYBOARD_HEIGHT *
                getResources().getDisplayMetrics().density);
        mInputViewId = NO_ID;
        mInputPaneId = NO_ID;
        boolean autoSave;
        TypedArray custom = getContext().obtainStyledAttributes(attrs,
                R.styleable.SmoothInputLayout);
        defaultInputHeight = custom.getDimensionPixelOffset(
                R.styleable.SmoothInputLayout_silDefaultKeyboardHeight, defaultInputHeight);
        minInputHeight = custom.getDimensionPixelOffset(
                R.styleable.SmoothInputLayout_silMinKeyboardHeight, minInputHeight);
        mInputViewId = custom.getResourceId(R.styleable.SmoothInputLayout_silInputView,
                mInputViewId);
        mInputPaneId = custom.getResourceId(R.styleable.SmoothInputLayout_silInputPane,
                mInputPaneId);
        autoSave = custom.getBoolean(R.styleable.SmoothInputLayout_silAutoSaveKeyboardHeight,
                true);
        custom.recycle();
        setDefaultKeyboardHeight(defaultInputHeight);
        setMinKeyboardHeight(minInputHeight);
        setAutoSaveKeyboardHeight(autoSave);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mInputViewId != NO_ID) {
            setInputView(findViewById(mInputViewId));
        }
        if (mInputPaneId != NO_ID) {
            setInputPane(findViewById(mInputPaneId));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSize > mMaxKeyboardHeight) {
            mMaxKeyboardHeight = heightSize;
        }
        final int heightChange = mMaxKeyboardHeight - heightSize;
        if (heightChange > mMinKeyboardHeight) {
            if (mKeyboardHeight != heightChange) {
                mKeyboardHeight = heightChange;
                saveKeyboardHeight();
            }
            mKeyboardOpen = true;
            // 输入法弹出，隐藏功能面板
            if (mInputPane != null && mInputPane.getVisibility() == VISIBLE) {
                mInputPane.setVisibility(GONE);
                if (mListener != null)
                    mListener.onVisibilityChange(GONE);
            }
        } else {
            mKeyboardOpen = false;
            if (tShowInputPane) {
                tShowInputPane = false;
                if (mInputPane != null && mInputPane.getVisibility() == GONE) {
                    updateLayout();
                    mInputPane.setVisibility(VISIBLE);
                    if (mListener != null)
                        mListener.onVisibilityChange(VISIBLE);
                    forceLayout();
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (keyboardChangeListener != null)
            keyboardChangeListener.onKeyboardChanged(mKeyboardOpen);
    }

    /**
     * 获取键盘SP
     *
     * @return 键盘SP
     */
    private SharedPreferences getKeyboardSharedPreferences() {
        return getContext().getSharedPreferences(SP_KEYBOARD, Context.MODE_PRIVATE);
    }

    /**
     * 存储键盘高度
     */
    private void saveKeyboardHeight() {
        if (mAutoSaveKeyboardHeight)
            getKeyboardSharedPreferences().edit().putInt(KEY_HEIGHT, mKeyboardHeight).commit();
        else {
            if (mKeyboardProcessor != null)
                mKeyboardProcessor.onSaveKeyboardHeight(mKeyboardHeight);
        }
    }

    /**
     * 更新子项高度
     */
    private void updateLayout() {
        if (mInputPane == null)
            return;
        if (mKeyboardHeight == 0)
            mKeyboardHeight = getKeyboardHeight(mDefaultKeyboardHeight);
        ViewGroup.LayoutParams layoutParams = mInputPane.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = mKeyboardHeight;
            mInputPane.setLayoutParams(layoutParams);
        }
    }

    private int getKeyboardHeight(int defaultHeight) {
        if (mAutoSaveKeyboardHeight)
            return getKeyboardSharedPreferences().getInt(KEY_HEIGHT, defaultHeight);
        else
            return mKeyboardProcessor != null ?
                    mKeyboardProcessor.getSavedKeyboardHeight(defaultHeight) : defaultHeight;
    }

    /**
     * 设置默认系统输入面板高度
     *
     * @param height 输入面板高度
     */
    public void setDefaultKeyboardHeight(int height) {
        if (mDefaultKeyboardHeight != height)
            mDefaultKeyboardHeight = height;
    }

    /**
     * 设置最小系统输入面板高度
     *
     * @param height 输入面板高度
     */
    public void setMinKeyboardHeight(int height) {
        if (mMinKeyboardHeight != height)
            mMinKeyboardHeight = height;
    }

    /**
     * 设置输入框
     *
     * @param edit 输入框
     */
    public void setInputView(View edit) {
        if (mInputView != edit)
            mInputView = edit;
    }

    /**
     * 设置特殊输入面板
     *
     * @param pane 面板
     */
    public void setInputPane(View pane) {
        if (mInputPane != pane)
            mInputPane = pane;
    }

    /**
     * 设置面板可见改变监听
     *
     * @param listener 面板可见改变监听
     */
    @SuppressWarnings("unused")
    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        mListener = listener;
    }

    /**
     * 设置键盘改变监听
     *
     * @param listener 键盘改变监听
     */
    @SuppressWarnings("unused")
    public void setOnKeyboardChangeListener(OnKeyboardChangeListener listener) {
        keyboardChangeListener = listener;
    }

    /**
     * 设置自动保存键盘高度
     *
     * @param auto 是否自动
     */
    public void setAutoSaveKeyboardHeight(boolean auto) {
        mAutoSaveKeyboardHeight = auto;
    }

    /**
     * 设置键盘处理器
     * 仅在关闭自动保存键盘高度时设置的处理器才有效{@link #setAutoSaveKeyboardHeight(boolean)}
     *
     * @param processor 处理器
     */
    @SuppressWarnings("unused")
    public void setKeyboardProcessor(KeyboardProcessor processor) {
        mKeyboardProcessor = processor;
    }

    /**
     * 是否输入法已打开
     *
     * @return 是否输入法已打开
     */
    @SuppressWarnings("unused")
    public boolean isKeyBoardOpen() {
        return mKeyboardOpen;
    }

    /**
     * 是否特殊输入面板已打开
     *
     * @return 特殊输入面板已打开
     */
    public boolean isInputPaneOpen() {
        return mInputPane != null && mInputPane.getVisibility() == VISIBLE;
    }

    /**
     * 关闭特殊输入面板
     */
    @SuppressWarnings("unused")
    public void closeInputPane() {
        if (isInputPaneOpen()) {
            mInputPane.setVisibility(GONE);
            if (mListener != null)
                mListener.onVisibilityChange(GONE);
        }
    }

    /**
     * 显示特殊输入面板
     *
     * @param focus 是否让输入框拥有焦点
     */
    @SuppressWarnings("unused")
    public void showInputPane(boolean focus) {
        if (isKeyBoardOpen()) {
            tShowInputPane = true;
            InputMethodManager imm = ((InputMethodManager) (getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE)));
            imm.hideSoftInputFromWindow(getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        } else {
            if (mInputPane != null && mInputPane.getVisibility() == GONE) {
                updateLayout();
                mInputPane.setVisibility(VISIBLE);
                if (mListener != null)
                    mListener.onVisibilityChange(VISIBLE);
            }
        }
        if (focus) {
            if (mInputView != null) {
                mInputView.requestFocus();
                mInputView.requestFocusFromTouch();
            }
        } else {
            if (mInputView != null) {
                setFocusable(true);
                setFocusableInTouchMode(true);
                mInputView.clearFocus();
            }
        }
    }

    /**
     * 关闭键盘
     *
     * @param clearFocus 是否清除输入框焦点
     */
    @SuppressWarnings("unused")
    public void closeKeyboard(boolean clearFocus) {
        InputMethodManager imm = ((InputMethodManager) (getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        imm.hideSoftInputFromWindow(getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (clearFocus && mInputView != null) {
            setFocusable(true);
            setFocusableInTouchMode(true);
            mInputView.clearFocus();
        }
    }

    /**
     * 打开键盘
     */
    @SuppressWarnings("unused")
    public void showKeyboard() {
        if (mInputView == null) {
            return;
        }
        mInputView.requestFocus();
        mInputView.requestFocusFromTouch();
        InputMethodManager imm = ((InputMethodManager) (getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE)));
        imm.showSoftInput(mInputView, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 面板可见改变监听
     */
    public interface OnVisibilityChangeListener {
        void onVisibilityChange(int visibility);
    }

    /**
     * 键盘改变监听
     */
    public interface OnKeyboardChangeListener {
        void onKeyboardChanged(boolean open);
    }

    /**
     * 键盘处理器
     */
    public interface KeyboardProcessor {
        /**
         * 存储键盘高度
         *
         * @param height 高度
         */
        void onSaveKeyboardHeight(int height);

        /**
         * 获取存储的键盘高度
         *
         * @param defaultHeight 默认高度
         * @return 键盘高度
         */
        int getSavedKeyboardHeight(int defaultHeight);
    }
}
