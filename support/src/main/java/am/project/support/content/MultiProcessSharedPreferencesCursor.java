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

import android.database.AbstractCursor;
import android.database.Cursor;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * 游标实现
 * Created by Alex on 2020/8/12.
 */
class MultiProcessSharedPreferencesCursor extends AbstractCursor {

    private static final String NAME_CONTAINS = "contains";
    private static final String NAME_DATA = "data";
    private static final String KEY_KEY = "key";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";
    private static final String TYPE_STRING = "String";
    private static final String TYPE_SET = "Set";
    private static final String TYPE_INT = "Int";
    private static final String TYPE_LONG = "Long";
    private static final String TYPE_FLOAT = "Float";
    private static final String TYPE_BOOLEAN = "Boolean";
    private final byte[] mContains;
    private int mType;
    private String mString;
    private long mInteger;
    private byte[] mBlob;

    public MultiProcessSharedPreferencesCursor(boolean contains) {
        final byte b = contains ? (byte) 1 : (byte) 0;
        mContains = new byte[]{b};
    }

    @Nullable
    public static Map<String, ?> getAll(Cursor cursor) {
        final String json = cursor.getString(1);
        if (TextUtils.isEmpty(json))
            return null;
        try {
            final ArrayMap<String, Object> result = new ArrayMap<>();
            final JSONArray array = new JSONArray(json);
            final int count = array.length();
            for (int i = 0; i < count; i++) {
                final JSONObject item = array.getJSONObject(i);
                final String key = item.getString(KEY_KEY);
                final String type = item.getString(KEY_TYPE);
                switch (type) {
                    case TYPE_STRING:
                        result.put(key, item.getString(KEY_DATA));
                        break;
                    case TYPE_SET:
                        final ArraySet<String> data = new ArraySet<>();
                        final JSONArray a = item.getJSONArray(KEY_DATA);
                        final int c = a.length();
                        for (int j = 0; j < c; j++) {
                            data.add(a.getString(j));
                        }
                        result.put(key, data);
                        break;
                    case TYPE_INT:
                        result.put(key, item.getInt(KEY_DATA));
                        break;
                    case TYPE_LONG:
                        result.put(key, item.getLong(KEY_DATA));
                        break;
                    case TYPE_FLOAT:
                        result.put(key, Float.parseFloat(item.getString(KEY_DATA)));
                        break;
                    case TYPE_BOOLEAN:
                        result.put(key, item.getBoolean(KEY_DATA));
                        break;
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static String getString(Cursor cursor, @Nullable String defValue) {
        if (!contains(cursor))
            return defValue;
        return cursor.getString(1);
    }

    @Nullable
    public static Set<String> getStringSet(Cursor cursor, @Nullable Set<String> defValues) {
        if (!contains(cursor))
            return defValues;
        final String json = cursor.getString(1);
        if (TextUtils.isEmpty(json))
            return null;
        try {
            final ArraySet<String> result = new ArraySet<>();
            final JSONArray array = new JSONArray(json);
            final int count = array.length();
            for (int i = 0; i < count; i++) {
                result.add(array.getString(i));
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getInt(Cursor cursor, int defValue) {
        if (!contains(cursor))
            return defValue;
        return cursor.getInt(1);
    }

    public static long getLong(Cursor cursor, long defValue) {
        if (!contains(cursor))
            return defValue;
        return cursor.getLong(1);
    }

    public static float getFloat(Cursor cursor, float defValue) {
        if (!contains(cursor))
            return defValue;
        return Float.parseFloat(cursor.getString(1));
    }

    public static boolean getBoolean(Cursor cursor, boolean defValue) {
        if (!contains(cursor))
            return defValue;
        final byte[] blob = cursor.getBlob(1);
        return blob != null && blob.length == 1 && blob[0] != 0;
    }

    public static boolean contains(Cursor cursor) {
        final byte[] blob = cursor.getBlob(0);
        return blob != null && blob.length == 1 && blob[0] != 0;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{NAME_CONTAINS, NAME_DATA};
    }

    @Override
    public byte[] getBlob(int column) {
        if (column == 0)
            return mContains;
        if (column == 1 && mType == FIELD_TYPE_BLOB)
            return mBlob;
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public String getString(int column) {
        if (column == 1 && mType == FIELD_TYPE_STRING)
            return mString;
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public short getShort(int column) {
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public int getInt(int column) {
        if (column == 1 && mType == FIELD_TYPE_INTEGER)
            return (int) mInteger;
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public long getLong(int column) {
        if (column == 1 && mType == FIELD_TYPE_INTEGER)
            return mInteger;
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public float getFloat(int column) {
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public double getDouble(int column) {
        throw new IllegalArgumentException("Error column!");
    }

    @Override
    public int getType(int column) {
        return column == 0 ? FIELD_TYPE_BLOB : mType;
    }

    @Override
    public boolean isNull(int column) {
        if (column == 0)
            return false;
        if (mType == FIELD_TYPE_STRING)
            return mString == null;
        if (mType == FIELD_TYPE_BLOB)
            return mBlob == null;
        return false;
    }

    public MultiProcessSharedPreferencesCursor setAll(@Nullable Map<String, ?> all) {
        mType = FIELD_TYPE_STRING;
        mString = null;
        if (all != null) {
            final JSONArray array = new JSONArray();
            final Set<String> keys = all.keySet();
            for (String key : keys) {
                final Object data = all.get(key);
                try {
                    if (data instanceof String) {
                        final JSONObject item = new JSONObject();
                        item.put(KEY_KEY, key);
                        item.put(KEY_TYPE, TYPE_STRING);
                        item.put(KEY_DATA, data);
                        array.put(item);
                    } else if (data instanceof Set) {
                        final JSONObject item = new JSONObject();
                        item.put(KEY_KEY, key);
                        item.put(KEY_TYPE, TYPE_SET);
                        //noinspection unchecked
                        final Set<String> values = (Set<String>) data;
                        final JSONArray da = new JSONArray();
                        for (String value : values) {
                            da.put(value);
                        }
                        item.put(KEY_DATA, da);
                        array.put(item);
                    } else if (data instanceof Integer) {
                        final JSONObject item = new JSONObject();
                        item.put(KEY_KEY, key);
                        item.put(KEY_TYPE, TYPE_INT);
                        item.put(KEY_DATA, data);
                        array.put(item);
                    } else if (data instanceof Long) {
                        final JSONObject item = new JSONObject();
                        item.put(KEY_KEY, key);
                        item.put(KEY_TYPE, TYPE_LONG);
                        item.put(KEY_DATA, data);
                        array.put(item);
                    } else if (data instanceof Float) {
                        final JSONObject item = new JSONObject();
                        item.put(KEY_KEY, key);
                        item.put(KEY_TYPE, TYPE_FLOAT);
                        item.put(KEY_DATA, String.valueOf(data));
                        array.put(item);
                    } else if (data instanceof Boolean) {
                        final JSONObject item = new JSONObject();
                        item.put(KEY_KEY, key);
                        item.put(KEY_TYPE, TYPE_BOOLEAN);
                        item.put(KEY_DATA, data);
                        array.put(item);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
            mString = array.toString();
        }
        return this;
    }

    public MultiProcessSharedPreferencesCursor setString(@Nullable String value) {
        mType = FIELD_TYPE_STRING;
        mString = value;
        return this;
    }

    public MultiProcessSharedPreferencesCursor setStringSet(@Nullable Set<String> values) {
        mType = FIELD_TYPE_STRING;
        mString = null;
        if (values != null) {
            final JSONArray array = new JSONArray();
            for (String value : values) {
                array.put(value);
            }
            mString = array.toString();
        }
        return this;
    }

    public MultiProcessSharedPreferencesCursor setInt(int value) {
        mType = FIELD_TYPE_INTEGER;
        mInteger = value;
        return this;
    }

    public MultiProcessSharedPreferencesCursor setLong(long value) {
        mType = FIELD_TYPE_INTEGER;
        mInteger = value;
        return this;
    }

    public MultiProcessSharedPreferencesCursor setFloat(float value) {
        mType = FIELD_TYPE_STRING;
        mString = String.valueOf(value);
        return this;
    }

    public MultiProcessSharedPreferencesCursor setBoolean(boolean value) {
        mType = FIELD_TYPE_BLOB;
        final byte b = value ? (byte) 1 : (byte) 0;
        mBlob = new byte[]{b};
        return this;
    }
}
