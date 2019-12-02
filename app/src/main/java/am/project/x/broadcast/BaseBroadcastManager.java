/*
 * Copyright (C) 2019 AlexMofer
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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * 基础广播管理器
 * Created by Alex on 2019/11/8.
 */
public abstract class BaseBroadcastManager {

    private final InnerBroadcastReceiver mReceiver = new InnerBroadcastReceiver();
    private final InnerLocalBroadcastReceiver mLocalReceiver = new InnerLocalBroadcastReceiver();
    private LocalBroadcastManager mManager;// 应用内部广播

    public BaseBroadcastManager(Context context) {
        mManager = LocalBroadcastManager.getInstance(context);
        final IntentFilter filter = new IntentFilter();
        onCreateIntentFilter(filter);
        final IntentFilter localFilter = new IntentFilter();
        onCreateLocalIntentFilter(localFilter);
        mManager.registerReceiver(mLocalReceiver, localFilter);
        context.registerReceiver(mReceiver, filter);
    }

    protected abstract void onCreateIntentFilter(IntentFilter filter);

    protected abstract void onCreateLocalIntentFilter(IntentFilter filter);

    protected abstract void onReceiveBroadcast(Context context, Intent intent);

    protected abstract void onReceiveLocalBroadcast(Context context, Intent intent);

    protected LocalBroadcastManager getLocalBroadcastManager() {
        return mManager;
    }

    public void destroy(Context context) {
        context.unregisterReceiver(mReceiver);
        mManager.unregisterReceiver(mLocalReceiver);
        mManager = null;
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
