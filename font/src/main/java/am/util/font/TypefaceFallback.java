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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 备选字体
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class TypefaceFallback implements Parcelable {
    private final String mLang;// 语言，可能为空（API 26 后仅一个，API 25 存在两个，表示匹配不到的语言的备选项），带空格表示多种语言
    private final String mVariant;// 变种，可能为空
    private final List<TypefaceItem> mItems;// 子项

    public TypefaceFallback(String lang, String variant, List<TypefaceItem> items) {
        mLang = lang;
        mVariant = variant;
        mItems = items;
    }

    /**
     * 获取语言
     *
     * @return 语言，可能为空（API 26 后仅一个，API 25 存在两个，表示匹配不到的语言的备选项），带空格表示多种语言
     */
    public String getLang() {
        return mLang;
    }

    /**
     * 获取变体信息
     *
     * @return 变体信息，可能为空
     */
    public String getVariant() {
        return mVariant;
    }

    /**
     * 获取子项集合
     *
     * @return 子项集合
     */
    public List<TypefaceItem> getItems() {
        return mItems;
    }

    @Override
    public String toString() {
        return "TypefaceFallback{" +
                "lang='" + mLang + '\'' +
                ", variant='" + mVariant + '\'' +
                ", items=" + mItems.toString() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLang);
        dest.writeString(mVariant);
        dest.writeTypedList(mItems);
    }

    private TypefaceFallback(Parcel in) {
        mLang = in.readString();
        mVariant = in.readString();
        mItems = in.createTypedArrayList(TypefaceItem.CREATOR);
    }

    public static final Creator<TypefaceFallback> CREATOR =
            new Creator<TypefaceFallback>() {
                @Override
                public TypefaceFallback createFromParcel(Parcel source) {
                    return new TypefaceFallback(source);
                }

                @Override
                public TypefaceFallback[] newArray(int size) {
                    return new TypefaceFallback[size];
                }
            };
}
