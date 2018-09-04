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
package am.project.x.business.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebSettings;

import am.project.x.R;
import am.project.x.base.BaseActivity;
import am.project.x.widget.PowerfulWebView;

public class BrowserActivity extends BaseActivity implements PowerfulWebView.OnTitleListener {

    private static final String EXTRA_URL = "url";
    private PowerfulWebView mVContent;

    public static void start(Context context, String url) {
        final Intent starter = new Intent(context, BrowserActivity.class);
        starter.putExtra(EXTRA_URL, url);
        context.startActivity(starter);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.activity_browser;
    }

    @Override
    protected void initializeActivity(@Nullable Bundle savedInstanceState) {
        setSupportActionBar(R.id.browser_toolbar);
        final String url = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        setTitle("");
        mVContent = findViewById(R.id.browser_wb_content);
        WebSettings webSettings = mVContent.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setDefaultTextEncodingName("utf-8");
        mVContent.setWebViewClient(new PowerfulWebView.StateWebViewClient());
        mVContent.setOnTitleListener(this);
        mVContent.loadUrl(url);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mVContent.onResume();
        mVContent.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVContent.onPause();
        mVContent.pauseTimers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVContent.powerfulDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mVContent.canGoBack()) {
            mVContent.goBack();
            return;
        }
        super.onBackPressed();
    }

    // Listener
    @Override
    public void onTitleChanged(PowerfulWebView view, String title) {
        setTitle(title);
    }

}
