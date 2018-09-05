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
package am.project.support.font;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 字体家族集
 * Created by Alex on 2018/8/30.
 */
class FamilySet {
    private final String mVersion;// 版本，可能为空，API 21 时出现版本字段，一般为整数

    private final ArrayList<String> mNames = new ArrayList<>();
    private final HashMap<String, Family> mSystemFamily = new HashMap<>();// 基础系统字体族（按名称检索）
    private final ArrayList<String> mAlias = new ArrayList<>();
    private final HashMap<String, Alias> mSystemFamilyAlias = new HashMap<>();// 基础系统字体族别名（按名称检索）
    private final ArrayList<Fallback> mFallbacks = new ArrayList<>();// 备选字体族（可能存在多个无语言参数的默认备选字体）

    FamilySet(String version) {
        mVersion = version;
    }

    boolean putFamily(Family family) {
        if (family == null || !family.isAvailable())
            return false;
        final String name = family.getName();
        mNames.add(name);
        mSystemFamily.put(name, family);
        return true;
    }

    void putAlias(Alias alias) {
        if (alias == null || !alias.isAvailable())
            return;

        final String to = alias.getTo();
        if (mSystemFamily.containsKey(to)) {
            final String name = alias.getName();
            mAlias.add(name);
            mSystemFamilyAlias.put(name, alias);
        }
    }

    void putFallback(Fallback fallback) {
        if (fallback == null || !fallback.isAvailable())
            return;
        mFallbacks.add(fallback);
    }

    boolean isAvailable() {
        return mSystemFamily.size() > 0;
    }

    String getVersion() {
        return mVersion;
    }

    ArrayList<String> getNames() {
        return mNames;
    }

    ArrayList<String> getAlias() {
        return mAlias;
    }

    TypefaceCollection getTypefaceCollection(String nameOrAlias) {
        Family family = mSystemFamily.get(nameOrAlias);
        int weight = -1;
        if (family == null) {
            final Alias alias = mSystemFamilyAlias.get(nameOrAlias);
            if (alias != null) {
                family = mSystemFamily.get(alias.getTo());
                weight = alias.getWeight();
            }
        }
        if (family == null)
            return null;
        final String name = family.getName();
        final ArrayList<TypefaceItem> items = family.convert(weight);
        final ArrayList<TypefaceFallback> fallbacks = getFallbacks(name);
        if (items == null || fallbacks == null)
            return null;
        return new TypefaceCollection(name, items, fallbacks);
    }

    private ArrayList<TypefaceFallback> getFallbacks(String name) {
        final ArrayList<TypefaceFallback> fallbacks = new ArrayList<>();
        for (Fallback fallback : mFallbacks) {
            final TypefaceFallback f = fallback.convert(name);
            if (f != null)
                fallbacks.add(f);
        }
        return fallbacks.isEmpty() ? null : fallbacks;
    }
}
