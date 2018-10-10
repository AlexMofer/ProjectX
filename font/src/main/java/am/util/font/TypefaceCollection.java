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
 * 字体集（常规字体集合及备用字体集合的封装）
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings("unused")
public class TypefaceCollection implements Parcelable {
    private final String mName;// 名称
    private final List<TypefaceItem> mItems;// 常规字体
    private final List<TypefaceFallback> mFallbacks;// 备用字体

    @SuppressWarnings("all")
    public TypefaceCollection(String name, List<TypefaceItem> items,
                              List<TypefaceFallback> fallbacks) {
        mName = name;
        mItems = items;
        mFallbacks = fallbacks;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return mName;
    }

    /**
     * 获取子项合集
     *
     * @return 子项合集
     */
    public List<TypefaceItem> getItems() {
        return mItems;
    }

    /**
     * 获取备用字体集
     *
     * @return 备用字体集
     */
    public List<TypefaceFallback> getFallbacks() {
        return mFallbacks;
    }

    @Override
    public String toString() {
        return "TypefaceCollection{" +
                "name='" + mName + '\'' +
                ", items=" + mItems.toString() +
                ", fallbacks=" + mFallbacks.toString() +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeTypedList(mItems);
        dest.writeTypedList(mFallbacks);
    }

    private TypefaceCollection(Parcel in) {
        mName = in.readString();
        mItems = in.createTypedArrayList(TypefaceItem.CREATOR);
        mFallbacks = in.createTypedArrayList(TypefaceFallback.CREATOR);
    }

    public static final Creator<TypefaceCollection> CREATOR =
            new Creator<TypefaceCollection>() {
                @Override
                public TypefaceCollection createFromParcel(Parcel source) {
                    return new TypefaceCollection(source);
                }

                @Override
                public TypefaceCollection[] newArray(int size) {
                    return new TypefaceCollection[size];
                }
            };
}
