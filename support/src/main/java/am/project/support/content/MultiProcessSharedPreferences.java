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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 多进程SharedPreferences
 * Created by Alex on 2020/8/12.
 */
public class MultiProcessSharedPreferences implements SharedPreferences {

    private final Context mContext;
    private final String mName;
    private final Uri mBaseUri;
    private MultiProcessSharedPreferencesChangeBroadcastReceiver mReceiver;
    private boolean mNotifyChanged;

    private MultiProcessSharedPreferences(@NonNull Context context, @NonNull String name,
                                          @Nullable String authority) {
        mContext = context.getApplicationContext();
        mName = name;
        if (authority == null) {
            authority = MultiProcessSharedPreferencesContentProvider.getAuthority(mContext);
        }
        if (authority == null)
            mBaseUri = null;
        else
            mBaseUri = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + authority);
    }

    public static SharedPreferences get(@NonNull Context context, @NonNull String name,
                                        @NonNull String authority) {
        return new MultiProcessSharedPreferences(context, name, authority);
    }

    public static SharedPreferences get(@NonNull Context context, @NonNull String name) {
        return new MultiProcessSharedPreferences(context, name, null);
    }

    @Override
    public Map<String, ?> getAll() {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_ALL, null);
        if (cursor == null)
            return Collections.emptyMap();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return Collections.emptyMap();
        }
        final Map<String, ?> result = MultiProcessSharedPreferencesCursor.getAll(cursor);
        cursor.close();
        return result == null ? Collections.emptyMap() : result;
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_STRING, key);
        if (cursor == null)
            return defValue;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return defValue;
        }
        final String result = MultiProcessSharedPreferencesCursor.getString(cursor, defValue);
        cursor.close();
        return result;
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_STRING_SET, key);
        if (cursor == null)
            return defValues;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return defValues;
        }
        final Set<String> result =
                MultiProcessSharedPreferencesCursor.getStringSet(cursor, defValues);
        cursor.close();
        return result;
    }

    @Override
    public int getInt(String key, int defValue) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_INT, key);
        if (cursor == null)
            return defValue;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return defValue;
        }
        final int result = MultiProcessSharedPreferencesCursor.getInt(cursor, defValue);
        cursor.close();
        return result;
    }

    @Override
    public long getLong(String key, long defValue) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_LONG, key);
        if (cursor == null)
            return defValue;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return defValue;
        }
        final long result = MultiProcessSharedPreferencesCursor.getLong(cursor, defValue);
        cursor.close();
        return result;
    }

    @Override
    public float getFloat(String key, float defValue) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_FLOAT, key);
        if (cursor == null)
            return defValue;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return defValue;
        }
        final float result = MultiProcessSharedPreferencesCursor.getFloat(cursor, defValue);
        cursor.close();
        return result;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_GET_BOOLEAN, key);
        if (cursor == null)
            return defValue;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return defValue;
        }
        final boolean result = MultiProcessSharedPreferencesCursor.getBoolean(cursor, defValue);
        cursor.close();
        return result;
    }

    @Override
    public boolean contains(String key) {
        final Cursor cursor =
                query(MultiProcessSharedPreferencesContentProvider.PATH_CONTAINS, key);
        if (cursor == null)
            return false;
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        final boolean result = MultiProcessSharedPreferencesCursor.contains(cursor);
        cursor.close();
        return result;
    }

    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        if (listener == null)
            return;
        if (mReceiver == null) {
            mReceiver = new MultiProcessSharedPreferencesChangeBroadcastReceiver(this);
            MultiProcessSharedPreferencesChangeBroadcastReceiver
                    .registerReceiver(mContext, mReceiver);
        }
        mReceiver.add(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener) {
        if (mReceiver == null)
            return;
        mReceiver.remove(listener);
        if (mReceiver.isEmpty()) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * 判断是否通知改变
     *
     * @return 通知改变时返回true
     */
    public boolean isNotifyChanged() {
        return mNotifyChanged;
    }

    /**
     * 设置是否通知改变
     *
     * @param notify 是否通知
     */
    public void setNotifyChanged(boolean notify) {
        mNotifyChanged = notify;
    }

    private Cursor query(String path, String key) {
        if (mBaseUri == null)
            return null;
        final Uri uri = Uri.withAppendedPath(mBaseUri, path);
        final String[] args = new String[]{mName, key};
        try {
            return mContext.getContentResolver()
                    .query(uri, null, null, args, null);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean update(String path, ArrayList<MultiProcessSharedPreferencesAction> actions) {
        if (mBaseUri == null)
            return false;
        final Uri uri = Uri.withAppendedPath(mBaseUri, path);
        final String[] args = new String[]{mName, String.valueOf(mNotifyChanged)};
        final ContentValues values = new ContentValues();
        MultiProcessSharedPreferencesContentValuesHelper.put(values, actions);
        try {
            return mContext.getContentResolver().update(uri, values, null, args) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    private class EditorImpl implements Editor {

        private final ArrayList<MultiProcessSharedPreferencesAction> mActions = new ArrayList<>();

        @Override
        public Editor putString(String key, @Nullable String value) {
            mActions.add(MultiProcessSharedPreferencesAction.putString(key, value));
            return this;
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            mActions.add(MultiProcessSharedPreferencesAction.putStringSet(key, values));
            return this;
        }

        @Override
        public Editor putInt(String key, int value) {
            mActions.add(MultiProcessSharedPreferencesAction.putInt(key, value));
            return this;
        }

        @Override
        public Editor putLong(String key, long value) {
            mActions.add(MultiProcessSharedPreferencesAction.putLong(key, value));
            return this;
        }

        @Override
        public Editor putFloat(String key, float value) {
            mActions.add(MultiProcessSharedPreferencesAction.putFloat(key, value));
            return this;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            mActions.add(MultiProcessSharedPreferencesAction.putBoolean(key, value));
            return this;
        }

        @Override
        public Editor remove(String key) {
            mActions.add(MultiProcessSharedPreferencesAction.remove(key));
            return this;
        }

        @Override
        public Editor clear() {
            mActions.add(MultiProcessSharedPreferencesAction.clear());
            return this;
        }

        @Override
        public boolean commit() {
            if (mActions.isEmpty())
                return true;
            return update(MultiProcessSharedPreferencesContentProvider.PATH_COMMIT, mActions);
        }

        @Override
        public void apply() {
            if (mActions.isEmpty())
                return;
            update(MultiProcessSharedPreferencesContentProvider.PATH_APPLY, mActions);
        }
    }
}
