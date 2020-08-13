/*
 * Copyright (C) 2020 AlexMofer
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

package am.project.support.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.collection.ArraySet;

/**
 * 变化广播接收器
 * Created by Alex on 2020/8/13.
 */
public class MultiProcessSharedPreferencesChangeBroadcastReceiver extends BroadcastReceiver {

    private static final String ACTION = "am.support.content.ACTION_MULTI_PROCESS_SHARED_PREFERENCES_CHANGED";
    private static final String EXTRA = "am.support.content.extra.MULTI_PROCESS_SHARED_PREFERENCES_CHANGED_KEY";

    private final MultiProcessSharedPreferences mPreferences;
    private final ArraySet<SharedPreferences.OnSharedPreferenceChangeListener> mListeners =
            new ArraySet<>();

    public MultiProcessSharedPreferencesChangeBroadcastReceiver(
            MultiProcessSharedPreferences preferences) {
        mPreferences = preferences;
    }

    static void sendBroadcast(Context context, String key) {
        if (context == null)
            return;
        context.sendBroadcast(new Intent(ACTION).putExtra(EXTRA, key));
    }

    static void registerReceiver(Context context,
                                 MultiProcessSharedPreferencesChangeBroadcastReceiver receiver) {
        if (context == null)
            return;
        context.registerReceiver(receiver, new IntentFilter(ACTION));
    }

    public void add(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    public void remove(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    public boolean isEmpty() {
        synchronized (mListeners) {
            return mListeners.isEmpty();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            final String key = intent.getStringExtra(EXTRA);
            synchronized (mListeners) {
                for (SharedPreferences.OnSharedPreferenceChangeListener listener : mListeners) {
                    listener.onSharedPreferenceChanged(mPreferences, key);
                }
            }
        }
    }
}
