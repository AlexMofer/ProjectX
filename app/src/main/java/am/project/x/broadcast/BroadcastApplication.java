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
package am.project.x.broadcast;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDexApplication;

/**
 * 广播Application
 * Created by Alex on 2018/7/23.
 */
@SuppressWarnings("unused")
@SuppressLint("Registered")
public class BroadcastApplication extends MultiDexApplication {

    private final BroadcastReceiver mBroadcastReceiver = new InnerBroadcastReceiver();
    private final BroadcastReceiver mLocalBroadcastReceiver = new InnerLocalBroadcastReceiver();
    private LocalBroadcastManager mLocalBroadcastManager;// 应用内部广播

    @Override
    public void onCreate() {
        super.onCreate();
        final IntentFilter intentFilter = new IntentFilter();
        onAddAction(intentFilter);
        registerReceiver(mBroadcastReceiver, intentFilter);
        final IntentFilter localIntentFilter = new IntentFilter();
        onAddLocalAction(localIntentFilter);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mLocalBroadcastReceiver, localIntentFilter);
    }

    @Override
    public void onTerminate() {
        unregisterReceiver(mBroadcastReceiver);
        mLocalBroadcastManager.unregisterReceiver(mLocalBroadcastReceiver);
        super.onTerminate();
    }

    /**
     * 添加广播意图筛选器动作
     *
     * @param filter 筛选器
     */
    protected void onAddAction(IntentFilter filter) {
    }

    /**
     * 添加本地广播意图筛选器动作
     *
     * @param filter 筛选器
     */
    protected void onAddLocalAction(IntentFilter filter) {
    }

    /**
     * 接收到广播
     *
     * @param context Context
     * @param intent  意图
     */
    protected void onReceiveBroadcast(Context context, Intent intent) {
    }

    /**
     * 接收到本地广播
     *
     * @param context Context
     * @param intent  意图
     */
    protected void onReceiveLocalBroadcast(Context context, Intent intent) {
    }

    /**
     * 获取本地广播管理器
     *
     * @return 本地广播管理器
     */
    public LocalBroadcastManager getLocalBroadcastManager() {
        return mLocalBroadcastManager;
    }

    private class InnerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveBroadcast(context, intent);
        }
    }

    private class InnerLocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            onReceiveLocalBroadcast(context, intent);
        }
    }
}
