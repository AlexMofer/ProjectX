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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 跨进程首选项ContentProvider
 * Created by Alex on 2020/8/11.
 */
public class MultiProcessSharedPreferencesContentProvider extends ContentProvider {

    static final String PATH_GET_ALL = "getAll";
    static final String PATH_GET_STRING = "getString";
    static final String PATH_GET_STRING_SET = "getStringSet";
    static final String PATH_GET_INT = "getInt";
    static final String PATH_GET_LONG = "getLong";
    static final String PATH_GET_FLOAT = "getFloat";
    static final String PATH_GET_BOOLEAN = "getBoolean";
    static final String PATH_CONTAINS = "contains";
    static final String PATH_APPLY = "apply";
    static final String PATH_COMMIT = "commit";
    private static final int CODE_GET_ALL = 1;
    private static final int CODE_GET_STRING = 2;
    private static final int CODE_GET_STRING_SET = 3;
    private static final int CODE_GET_INT = 4;
    private static final int CODE_GET_LONG = 5;
    private static final int CODE_GET_FLOAT = 6;
    private static final int CODE_GET_BOOLEAN = 7;
    private static final int CODE_CONTAINS = 8;
    private static final int CODE_APPLY = 9;
    private static final int CODE_COMMIT = 10;
    private static final String KEY_NAME = "MULTI_PROCESS_SHARED_PREFERENCES_KEY_NAME_86D0651E94434D6B939139F9CD5613DE";
    private static String AUTHORITY;
    private final UriMatcher mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final ChangeHandler mChangeHandler = new ChangeHandler();

    private static void checkAuthority(Context context) {
        if (AUTHORITY != null)
            return;
        if (context == null)
            return;
        final PackageInfo info;
        try {
            info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PROVIDERS);
        } catch (Exception e) {
            return;
        }
        if (info == null || info.providers == null)
            return;
        final ProviderInfo[] providers = info.providers;
        final String name = MultiProcessSharedPreferencesContentProvider.class.getName();
        for (ProviderInfo provider : providers) {
            if (TextUtils.equals(provider.name, name)) {
                AUTHORITY = provider.authority;
                break;
            }
        }
    }

    static String getAuthority(Context context) {
        checkAuthority(context);
        return AUTHORITY;
    }

    protected String getAuthority() {
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        checkAuthority(getContext());
        final String authority = getAuthority();
        if (TextUtils.isEmpty(authority))
            return false;
        mMatcher.addURI(authority, PATH_GET_ALL, CODE_GET_ALL);
        mMatcher.addURI(authority, PATH_GET_STRING, CODE_GET_STRING);
        mMatcher.addURI(authority, PATH_GET_STRING_SET, CODE_GET_STRING_SET);
        mMatcher.addURI(authority, PATH_GET_INT, CODE_GET_INT);
        mMatcher.addURI(authority, PATH_GET_LONG, CODE_GET_LONG);
        mMatcher.addURI(authority, PATH_GET_FLOAT, CODE_GET_FLOAT);
        mMatcher.addURI(authority, PATH_GET_BOOLEAN, CODE_GET_BOOLEAN);
        mMatcher.addURI(authority, PATH_CONTAINS, CODE_CONTAINS);
        mMatcher.addURI(authority, PATH_APPLY, CODE_APPLY);
        mMatcher.addURI(authority, PATH_COMMIT, CODE_COMMIT);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final Context context = getContext();
        if (context == null)
            return null;
        if (selectionArgs == null || selectionArgs.length != 2)
            return null;
        final String name = selectionArgs[0];
        if (TextUtils.isEmpty(name))
            return null;
        final SharedPreferences preferences =
                context.getSharedPreferences(name, Context.MODE_PRIVATE);
        final int code = mMatcher.match(uri);
        if (code == CODE_GET_ALL) {
            final Map<String, ?> all = preferences.getAll();
            if (all != null)
                all.remove(KEY_NAME);
            return new MultiProcessSharedPreferencesCursor(false)
                    .setAll(all);
        }
        final String key = selectionArgs[1];
        if (key == null)
            return null;
        final boolean contains = preferences.contains(key);
        switch (code) {
            default:
                return null;
            case CODE_GET_STRING:
                return new MultiProcessSharedPreferencesCursor(contains)
                        .setString(preferences.getString(key, null));
            case CODE_GET_STRING_SET:
                return new MultiProcessSharedPreferencesCursor(contains)
                        .setStringSet(preferences.getStringSet(key, null));
            case CODE_GET_INT:
                return new MultiProcessSharedPreferencesCursor(contains)
                        .setInt(preferences.getInt(key, 0));
            case CODE_GET_LONG:
                return new MultiProcessSharedPreferencesCursor(contains)
                        .setLong(preferences.getLong(key, 0));
            case CODE_GET_FLOAT:
                return new MultiProcessSharedPreferencesCursor(contains)
                        .setFloat(preferences.getFloat(key, 0.0f));
            case CODE_GET_BOOLEAN:
                return new MultiProcessSharedPreferencesCursor(contains)
                        .setBoolean(preferences.getBoolean(key, false));
            case CODE_CONTAINS:
                return new MultiProcessSharedPreferencesCursor(contains);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final Context context = getContext();
        if (context == null)
            return 0;
        if (selectionArgs == null || selectionArgs.length != 2)
            return 0;
        final String name = selectionArgs[0];
        if (TextUtils.isEmpty(name))
            return 0;
        final boolean notify = Boolean.parseBoolean(selectionArgs[1]);
        if (values == null)
            return 0;
        final SharedPreferences preferences =
                context.getSharedPreferences(name, Context.MODE_PRIVATE);
        if (!preferences.contains(KEY_NAME))
            preferences.edit().putString(KEY_NAME, name).apply();
        preferences.registerOnSharedPreferenceChangeListener(mChangeHandler);
        mChangeHandler.mNotify = notify;
        final int code = mMatcher.match(uri);
        if (code != CODE_APPLY && code != CODE_COMMIT)
            return 0;
        final List<MultiProcessSharedPreferencesAction> actions =
                MultiProcessSharedPreferencesContentValuesHelper.get(values);
        if (actions == null)
            return 0;
        final SharedPreferences.Editor editor = preferences.edit();
        for (MultiProcessSharedPreferencesAction action : actions) {
            switch (action.getType()) {
                case MultiProcessSharedPreferencesAction.TYPE_STRING:
                    editor.putString(action.getKey(), action.getString());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_STRING_SET:
                    editor.putStringSet(action.getKey(), action.getStringSet());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_INT:
                    editor.putInt(action.getKey(), action.getInt());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_LONG:
                    editor.putLong(action.getKey(), action.getLong());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_FLOAT:
                    editor.putFloat(action.getKey(), action.getFloat());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_BOOLEAN:
                    editor.putBoolean(action.getKey(), action.getBoolean());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_REMOVE:
                    editor.remove(action.getKey());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_CLEAR:
                    editor.clear();
                    break;
            }
        }
        if (code == CODE_APPLY) {
            editor.apply();
            return 0;
        } else {
            return editor.commit() ? 1 : 0;
        }
    }

    private class ChangeHandler implements SharedPreferences.OnSharedPreferenceChangeListener {

        private boolean mNotify = false;

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (!mNotify)
                return;
            MultiProcessSharedPreferencesChangeBroadcastReceiver.sendBroadcast(getContext(),
                    sharedPreferences.getString(KEY_NAME, null), key);
        }
    }
}
