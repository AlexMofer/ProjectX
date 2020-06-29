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
package am.project.x.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import am.project.x.R;

/**
 * 带进度条的WebView
 * Created by Alex on 2017/9/26.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class PowerfulWebView extends WebView {


    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final ArrayList<String> mTitles = new ArrayList<>();
    private final ArrayList<JavascriptInterfaceHelper> mHelpers = new ArrayList<>();
    private int mProgress = 0;
    private int mProgressColor;
    private int mProgressHeight;
    private StateWebViewClient mState;
    private OnTitleListener mTitleListener;
    private OnProgressListener mProgressListener;
    private OnErrorListener mErrorListener;

    public PowerfulWebView(Context context) {
        super(context);
        initView(context, null);
    }

    public PowerfulWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PowerfulWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setWebChromeClient(new WebChromeClient());
        TypedArray custom = context.obtainStyledAttributes(attrs, R.styleable.PowerfulWebView);
        mProgressColor = custom.getColor(R.styleable.PowerfulWebView_pwvProgressColor, Color.BLACK);
        mProgressHeight = custom.getDimensionPixelSize(
                R.styleable.PowerfulWebView_pwvProgressHeight, 10);
        custom.recycle();
        if (isInEditMode()) {
            mProgress = 50;
        }
        mPaint.setColor(mProgressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        if (mProgress >= 100)
            return;
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int maxWidth = getWidth() - paddingLeft - paddingRight;
        canvas.drawRect(paddingLeft, paddingTop, Math.round(maxWidth * mProgress / 100.0f),
                paddingTop + mProgressHeight, mPaint);
    }

    protected void onProgressChanged(int progress) {
        mProgress = progress;
        invalidate();
        if (mProgressListener != null)
            mProgressListener.onProgressChanged(this, progress);
    }

    protected void onFinish(String url) {
        if (mState != null && mState.isError()) {
            if (mErrorListener != null)
                mErrorListener.onError(this);
        }
    }

    private void addTitle(String title) {
        mTitles.add(title);
        if (mTitleListener != null)
            mTitleListener.onTitleChanged(this, title);
    }

    @Override
    public void goBack() {
        if (!mTitles.isEmpty()) {
            mTitles.remove(mTitles.size() - 1);
            if (mTitleListener != null) {
                if (mTitles.size() > 0) {
                    mTitleListener.onTitleChanged(this, mTitles.get(mTitles.size() - 1));
                } else {
                    mTitleListener.onTitleChanged(this, null);
                }
            }
        }
        super.goBack();
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        if (client instanceof StateWebViewClient) {
            mState = (StateWebViewClient) client;
        }
    }

    public void setProgressColor(int color) {
        mProgressColor = color;
        invalidate();
    }

    public void setProgressHeight(int height) {
        mProgressHeight = height;
        invalidate();
    }

    public int getProgress() {
        return mProgress;
    }

    public void powerfulDestroy() {
        if (getParent() instanceof ViewGroup)
            ((ViewGroup) getParent()).removeView(this);
        loadUrl("about:blank");
        stopLoading();
        onPause();
        removeAllViews();
        destroyDrawingCache();
        for (JavascriptInterfaceHelper helper : mHelpers) {
            helper.onRemove(this);
        }
        setWebChromeClient(null);
        setWebViewClient(null);
        destroy();
    }

    public void addJavascriptInterface(JavascriptInterfaceHelper helper) {
        if (helper == null)
            return;
        helper.onAdd(this);
        if (!mHelpers.contains(helper))
            mHelpers.add(helper);
    }

    public void removeJavascriptInterface(JavascriptInterfaceHelper helper) {
        if (helper == null)
            return;
        helper.onRemove(this);
        mHelpers.remove(helper);
    }

    public void setOnTitleListener(OnTitleListener listener) {
        mTitleListener = listener;
    }

    public void setOnProgressListener(OnProgressListener listener) {
        mProgressListener = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        mErrorListener = listener;
    }

    public interface OnTitleListener {
        void onTitleChanged(PowerfulWebView view, String title);
    }

    public interface OnProgressListener {
        void onProgressChanged(PowerfulWebView view, int progress);
    }

    public interface OnErrorListener {
        void onError(PowerfulWebView view);
    }

    public static class WebChromeClient extends android.webkit.WebChromeClient {


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (view instanceof PowerfulWebView) {
                ((PowerfulWebView) view).onProgressChanged(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (view instanceof PowerfulWebView) {
                ((PowerfulWebView) view).addTitle(title);
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            if (!(view instanceof PowerfulWebView))
                return super.onJsAlert(view, url, message, result);
            final ArrayList<JavascriptInterfaceHelper> helpers = ((PowerfulWebView) view).mHelpers;
            if (helpers.isEmpty())
                return super.onJsAlert(view, url, message, result);
            boolean response = false;
            for (JavascriptInterfaceHelper helper : helpers) {
                if (helper.getSupportType() == JavascriptInterfaceHelper.SUPPORT_TYPE_COMPAT) {
                    response = true;
                    helper.onReceiveValue(message);
                }
            }
            if (response) {
                result.cancel();
                return true;
            }
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            if (window instanceof PowerfulWebView) {
                ((PowerfulWebView) window).mHelpers.clear();
            }
        }
    }

    public static class StateWebViewClient extends WebViewClient {

        private boolean isError = false;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isError = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (view instanceof PowerfulWebView) {
                ((PowerfulWebView) view).onFinish(url);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            isError = true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {
            super.onReceivedError(view, request, error);
            isError = true;
        }

        public boolean isError() {
            return isError;
        }
    }

    /**
     * JS接口辅助器
     */
    public abstract static class JavascriptInterfaceHelper {

        public static final int SUPPORT_TYPE_IGNORE = 0;// 忽略API 17以下用户
        public static final int SUPPORT_TYPE_PERMIT = 1;// 允许API 17以下用户（不安全且不一定有效）
        public static final int SUPPORT_TYPE_COMPAT = 2;// 兼容API 17以下用户
        private final String mName;
        private final Handler mHandler = new Handler(Looper.getMainLooper(),
                msg -> {
                    onReceiveValue((String) msg.obj);
                    return true;
                });

        public JavascriptInterfaceHelper(@NonNull String name) {
            mName = name;
        }

        @SuppressLint({"JavascriptInterface", "AddJavascriptInterface", "ObsoleteSdkInt"})
        private void onAdd(WebView view) {
            final int type = getSupportType();
            switch (type) {
                default:
                case SUPPORT_TYPE_IGNORE:
                case SUPPORT_TYPE_COMPAT:
                    if (Build.VERSION.SDK_INT >= 17) {
                        view.addJavascriptInterface(this, mName);
                    }
                    break;
                case SUPPORT_TYPE_PERMIT:
                    view.addJavascriptInterface(this, mName);
                    break;
            }
        }

        private void onRemove(WebView view) {
            view.removeJavascriptInterface(mName);
        }

        /**
         * 获取支持类型
         *
         * @return 支持类型
         */
        public int getSupportType() {
            return SUPPORT_TYPE_IGNORE;
        }

        /**
         * 收到JS调用
         *
         * @param value 数据（一般都是Json）
         */
        protected abstract void onReceiveValue(String value);

        /**
         * 发送JS调用结果
         *
         * @param value 结果
         */
        protected void sendJavascriptResult(String value) {
            Message message = mHandler.obtainMessage();
            message.obj = value;
            message.sendToTarget();
        }
    }
}
