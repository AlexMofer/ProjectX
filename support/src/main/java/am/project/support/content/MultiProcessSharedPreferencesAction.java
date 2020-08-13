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

import java.util.Set;

/**
 * 操作
 * Created by Alex on 2020/8/12.
 */
class MultiProcessSharedPreferencesAction {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_STRING_SET = 1;
    public static final int TYPE_INT = 2;
    public static final int TYPE_LONG = 3;
    public static final int TYPE_FLOAT = 4;
    public static final int TYPE_BOOLEAN = 5;
    public static final int TYPE_REMOVE = 6;
    public static final int TYPE_CLEAR = 7;
    private final int mType;
    private final String mKey;
    private String mString;
    private Set<String> mSet;
    private int mInt;
    private long mLong;
    private float mFloat;
    private boolean mBoolean;

    private MultiProcessSharedPreferencesAction(int type, String key) {
        mType = type;
        mKey = key;
    }

    public static MultiProcessSharedPreferencesAction putString(String key, String value) {
        final MultiProcessSharedPreferencesAction action =
                new MultiProcessSharedPreferencesAction(TYPE_STRING, key);
        action.mString = value;
        return action;
    }

    public static MultiProcessSharedPreferencesAction putStringSet(String key, Set<String> values) {
        final MultiProcessSharedPreferencesAction action =
                new MultiProcessSharedPreferencesAction(TYPE_STRING_SET, key);
        action.mSet = values;
        return action;
    }

    public static MultiProcessSharedPreferencesAction putInt(String key, int value) {
        final MultiProcessSharedPreferencesAction action =
                new MultiProcessSharedPreferencesAction(TYPE_INT, key);
        action.mInt = value;
        return action;
    }

    public static MultiProcessSharedPreferencesAction putLong(String key, long value) {
        final MultiProcessSharedPreferencesAction action =
                new MultiProcessSharedPreferencesAction(TYPE_LONG, key);
        action.mLong = value;
        return action;
    }

    public static MultiProcessSharedPreferencesAction putFloat(String key, float value) {
        final MultiProcessSharedPreferencesAction action =
                new MultiProcessSharedPreferencesAction(TYPE_FLOAT, key);
        action.mFloat = value;
        return action;
    }

    public static MultiProcessSharedPreferencesAction putBoolean(String key, boolean value) {
        final MultiProcessSharedPreferencesAction action =
                new MultiProcessSharedPreferencesAction(TYPE_BOOLEAN, key);
        action.mBoolean = value;
        return action;
    }

    public static MultiProcessSharedPreferencesAction remove(String key) {
        return new MultiProcessSharedPreferencesAction(TYPE_REMOVE, key);
    }

    public static MultiProcessSharedPreferencesAction clear() {
        return new MultiProcessSharedPreferencesAction(TYPE_CLEAR, null);
    }

    public int getType() {
        return mType;
    }

    public String getKey() {
        return mKey;
    }

    public String getString() {
        return mString;
    }

    public Set<String> getStringSet() {
        return mSet;
    }

    public int getInt() {
        return mInt;
    }

    public long getLong() {
        return mLong;
    }

    public float getFloat() {
        return mFloat;
    }

    public boolean getBoolean() {
        return mBoolean;
    }
}
