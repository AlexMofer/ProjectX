/*
 * Copyright (C) 2008 ZXing authors
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

package com.google.zxing.client.android.result;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.wifi.WifiConfigManager;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.WifiParsedResult;

import am.project.x.R;


/**
 * Handles wifi access information.
 *
 * @author Vikram Aggarwal
 * @author Sean Owen
 */
public final class WifiResultHandler extends ResultHandler {

  private static final String TAG = WifiResultHandler.class.getSimpleName();

  private final CaptureActivity parent;

  public WifiResultHandler(CaptureActivity activity, ParsedResult result) {
    super(activity, result);
    parent = activity;
  }

  // Display the name of the network and the network type to the user.
  @Override
  public CharSequence getDisplayContents() {
    WifiParsedResult wifiResult = (WifiParsedResult) getResult();
    return wifiResult.getSsid() + " (" + wifiResult.getNetworkEncryption() + ')';
  }

  @Override
  public int getDisplayTitle() {
    return R.string.result_wifi;
  }
}