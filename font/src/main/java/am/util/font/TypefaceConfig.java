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
package am.util.font;

import java.util.List;

/**
 * 字体配置
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings("unused")
public class TypefaceConfig {

    private static TypefaceConfig mInstance;

    public static TypefaceConfig getInstance() {
        if (mInstance == null)
            mInstance = new TypefaceConfig();
        return mInstance;
    }

    private final FamilySet mFamilySet;

    private TypefaceConfig() {
        mFamilySet = FontsReaderCompat.getFontsReader().readConfig();
    }

    /**
     * 判断是否可用
     *
     * @return 是否可用
     */
    public boolean isAvailable() {
        return mFamilySet != null;
    }

    /**
     * 获取默认字体名（包含字形）
     *
     * @return 字体名，不可用情况下为空，一般来说都是 sans-serif
     */
    public String getDefaultName() {
        return mFamilySet == null ? null : mFamilySet.getNames().get(0);
    }

    /**
     * 获取名字集合
     *
     * @return 名字集合，不可用情况下为空
     */
    public List<String> getNames() {
        return mFamilySet == null ? null : mFamilySet.getNames();
    }

    /**
     * 获取别名集合
     *
     * @return 别名集合，不可用情况下为空
     */
    public List<String> getAlias() {
        return mFamilySet == null ? null : mFamilySet.getAlias();
    }

    /**
     * 获取字体集
     *
     * @param nameOrAlias 名称或别名
     * @return 字体集，不可用情况下为空，不包含给定的名称或别名的字体集时返回空
     */
    public TypefaceCollection getTypefaceCollection(String nameOrAlias) {
        return mFamilySet == null ? null : mFamilySet.getTypefaceCollection(nameOrAlias);
    }

    /**
     * 获取字体目录
     *
     * @return 目录
     */
    public static String getFontsDir() {
        return FontsReaderCompat.getFontsReader().getFontsDir();
    }
}