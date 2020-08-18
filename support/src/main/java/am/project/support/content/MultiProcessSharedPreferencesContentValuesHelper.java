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

import android.content.ContentValues;

import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * ContentValues辅助
 * Created by Alex on 2020/8/12.
 */
class MultiProcessSharedPreferencesContentValuesHelper {
    private static final String KEY_DATA = "data";
    private static final String KEY_TYPE = "type";
    private static final String KEY_KEY = "key";
    private static final String KEY_VALUE = "value";

    private MultiProcessSharedPreferencesContentValuesHelper() {
        //no instance
    }

    private static String toJson(Collection<? extends MultiProcessSharedPreferencesAction> actions)
            throws Exception {
        final JSONArray array = new JSONArray();
        for (MultiProcessSharedPreferencesAction action : actions) {
            final int type = action.getType();
            final JSONObject object = new JSONObject();
            object.put(KEY_TYPE, type);
            switch (type) {
                case MultiProcessSharedPreferencesAction.TYPE_STRING:
                    object.put(KEY_KEY, action.getKey());
                    object.put(KEY_VALUE, action.getString());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_STRING_SET:
                    object.put(KEY_KEY, action.getKey());
                    final Set<String> values = action.getStringSet();
                    if (values == null)
                        object.put(KEY_VALUE, JSONObject.NULL);
                    else {
                        final JSONArray set = new JSONArray();
                        for (String value : values) {
                            set.put(value);
                        }
                        object.put(KEY_VALUE, set);
                    }
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_INT:
                    object.put(KEY_KEY, action.getKey());
                    object.put(KEY_VALUE, action.getInt());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_LONG:
                    object.put(KEY_KEY, action.getKey());
                    object.put(KEY_VALUE, action.getLong());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_FLOAT:
                    object.put(KEY_KEY, action.getKey());
                    object.put(KEY_VALUE, String.valueOf(action.getFloat()));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_BOOLEAN:
                    object.put(KEY_KEY, action.getKey());
                    object.put(KEY_VALUE, action.getBoolean());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_REMOVE:
                    object.put(KEY_KEY, action.getKey());
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_CLEAR:
                    break;
            }
            array.put(object);
        }
        return array.toString();
    }

    static void put(ContentValues values,
                    Collection<? extends MultiProcessSharedPreferencesAction> actions) {
        if (values == null)
            return;
        try {
            values.put(KEY_DATA, toJson(actions));
        } catch (Exception e) {
            // ignore
        }
    }

    private static List<MultiProcessSharedPreferencesAction> formJson(String json)
            throws Exception {
        final ArrayList<MultiProcessSharedPreferencesAction> actions = new ArrayList<>();
        final JSONArray array = new JSONArray(json);
        final int count = array.length();
        for (int i = 0; i < count; i++) {
            final JSONObject object = array.getJSONObject(i);
            final int type = object.getInt(KEY_TYPE);
            switch (type) {
                case MultiProcessSharedPreferencesAction.TYPE_STRING:
                    actions.add(MultiProcessSharedPreferencesAction.putString(
                            object.getString(KEY_KEY),
                            object.has(KEY_VALUE) ? object.getString(KEY_VALUE) : null));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_STRING_SET:
                    final String key = object.getString(KEY_KEY);
                    final Set<String> values;
                    if (object.has(KEY_VALUE)) {
                        values = new ArraySet<>();
                        final JSONArray set = object.getJSONArray(KEY_VALUE);
                        final int length = set.length();
                        for (int j = 0; j < length; j++) {
                            values.add(set.getString(j));
                        }
                    } else {
                        values = null;
                    }
                    actions.add(MultiProcessSharedPreferencesAction.putStringSet(key, values));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_INT:
                    actions.add(MultiProcessSharedPreferencesAction.putInt(
                            object.getString(KEY_KEY), object.getInt(KEY_VALUE)));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_LONG:
                    actions.add(MultiProcessSharedPreferencesAction.putLong(
                            object.getString(KEY_KEY), object.getLong(KEY_VALUE)));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_FLOAT:
                    actions.add(MultiProcessSharedPreferencesAction.putFloat(
                            object.getString(KEY_KEY), object.has(KEY_VALUE) ?
                                    Float.parseFloat(object.getString(KEY_VALUE)) : 0.0f));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_BOOLEAN:
                    actions.add(MultiProcessSharedPreferencesAction.putBoolean(
                            object.getString(KEY_KEY), object.getBoolean(KEY_VALUE)));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_REMOVE:
                    actions.add(MultiProcessSharedPreferencesAction.remove(
                            object.getString(KEY_KEY)));
                    break;
                case MultiProcessSharedPreferencesAction.TYPE_CLEAR:
                    actions.add(MultiProcessSharedPreferencesAction.clear());
                    break;
            }
        }
        return actions;
    }

    @Nullable
    static List<MultiProcessSharedPreferencesAction> get(ContentValues values) {
        try {
            return formJson(values == null ? null : values.getAsString(KEY_DATA));
        } catch (Exception e) {
            return null;
        }
    }
}
