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

package am.project.support.compat;

import android.annotation.TargetApi;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

/**
 * WebView版本兼容器
 * Created by Alex on 2016/11/22.
 */
@SuppressWarnings("unused")
public final class AMWebViewCompat {

    private static final AMWebViewCompatImpl IMPL;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 19) {
            IMPL = new AMWebViewCompatKitKat();
        } else {
            IMPL = new AMWebViewCompatBase();
        }
    }

    private AMWebViewCompat() {
        //no instance
    }

    /**
     * Asynchronously evaluates JavaScript in the context of the currently displayed page.
     * If non-null, |resultCallback| will be invoked with any result returned from that
     * execution. This method must be called on the UI thread and the callback will
     * be made on the UI thread.
     * <p>
     * Compatibility note. Applications targeting {@link android.os.Build.VERSION_CODES#N} or
     * later, JavaScript state from an empty WebView is no longer persisted across navigations like
     * {@link WebView#loadUrl(String)}. For example, global variables and functions defined before calling
     * {@link WebView#loadUrl(String)} will not exist in the loaded page. Applications should use
     * {@link WebView#addJavascriptInterface} instead to persist JavaScript objects across navigations.
     *
     * @param script         the JavaScript to execute.
     * @param resultCallback A callback to be invoked when the script execution
     *                       completes with the result of the execution (if any).
     *                       May be null if no notification of the result is required.
     */
    public static void evaluateJavascript(WebView webView, String script, ValueCallback resultCallback) {
        IMPL.evaluateJavascript(webView, script, resultCallback);
    }

    /**
     * 判断是否支持 evaluateJavascript(String script, ValueCallback<String> resultCallback) 方法
     *
     * @return 是否支持
     */
    public static boolean isSupportEvaluateJavascript() {
        return android.os.Build.VERSION.SDK_INT >= 19;
    }

    private interface AMWebViewCompatImpl {
        void evaluateJavascript(WebView webView, String script, ValueCallback callback);
    }

    private static class AMWebViewCompatBase implements AMWebViewCompatImpl {
        @Override
        public void evaluateJavascript(WebView webView, String script, ValueCallback callback) {
            // do nothing
        }
    }

    @TargetApi(19)
    private static class AMWebViewCompatKitKat extends AMWebViewCompatBase {

        @Override
        public void evaluateJavascript(WebView webView, String script,
                                       ValueCallback resultCallback) {
            webView.evaluateJavascript(script, new ValueCallbackKitKat(resultCallback));
        }
    }

    @TargetApi(19)
    private static class ValueCallbackKitKat implements android.webkit.ValueCallback<String> {
        private final WeakReference<ValueCallback> resultCallbackWeakReference;

        ValueCallbackKitKat(ValueCallback resultCallback) {
            resultCallbackWeakReference = new WeakReference<>(resultCallback);
        }

        @Override
        public void onReceiveValue(String value) {
            ValueCallback resultCallback = resultCallbackWeakReference.get();
            if (resultCallback != null)
                resultCallback.onReceiveValue(value);
        }
    }
}
