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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 字体子项
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class TypefaceItem implements Parcelable {
    public static final int STYLE_NORMAL = 0;// 常规
    public static final int STYLE_ITALIC = 1;// 斜体

    private final String mName;// 文件名，ttf、ttc、otf等类型的字体文件
    private final int mWeight;// 默认值为400，常规字体
    private final int mStyle;// 样式，常规或斜体
    private final int mIndex;// 字体集中的角标，该参数仅对ttc文件有效
    private final Map<String, TypefaceAxis> mAxises;// 对称信息，可能为空

    public TypefaceItem(String name, int weight, int style, int index,
                        Map<String, TypefaceAxis> axises) {
        mName = name;
        mWeight = weight;
        mStyle = style;
        mIndex = index;
        mAxises = axises;
    }

    /**
     * 获取文件名
     *
     * @return 文件名
     */
    public String getName() {
        return mName;
    }

    /**
     * 获取权重
     *
     * @return 权重
     */
    public int getWeight() {
        return mWeight;
    }

    /**
     * 获取字体样式
     *
     * @return 字体样式
     */
    public int getStyle() {
        return mStyle;
    }

    /**
     * 获取所属字体集的下标
     *
     * @return 字体集的下标（仅对ttc文件有效，其他文件均为-1）
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * 获取对称信息总数
     *
     * @return 对称信息总数
     */
    public int getAxisCount() {
        return mAxises == null ? 0 : mAxises.size();
    }

    /**
     * 获取对称信息
     *
     * @param tag 标签
     * @return 对称信息，可能为空
     */
    public TypefaceAxis getAxis(String tag) {
        if (tag == null || mAxises == null)
            return null;
        if (TypefaceAxis.TAG_ITAL.equals(tag) || TypefaceAxis.TAG_OPSZ.equals(tag) ||
                TypefaceAxis.TAG_SLNT.equals(tag) || TypefaceAxis.TAG_WDTH.equals(tag) ||
                TypefaceAxis.TAG_WGHT.equals(tag))
            return mAxises.get(tag);
        return null;
    }

    /**
     * 获取对称信息Map
     *
     * @return 对称信息Map，可能为空
     */
    public Map<String, TypefaceAxis> getAxises() {
        return mAxises;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypefaceItem that = (TypefaceItem) o;
        return mWeight == that.mWeight &&
                mStyle == that.mStyle &&
                mIndex == that.mIndex &&
                Objects.equals(mName, that.mName) &&
                Objects.equals(mAxises, that.mAxises);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mWeight, mStyle, mIndex, mAxises);
    }

    @Override
    public String toString() {
        return "TypefaceItem{" +
                "name='" + mName + '\'' +
                ", weight=" + mWeight +
                ", style=" + mStyle +
                ", index=" + mIndex +
                ", axises=" + String.valueOf(mAxises) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mWeight);
        dest.writeInt(mStyle);
        dest.writeInt(mIndex);
        final int size = mAxises == null ? 0 : mAxises.size();
        dest.writeInt(size);
        if (size > 0) {
            final Set<Map.Entry<String, TypefaceAxis>> axisesSet = mAxises.entrySet();
            for (Map.Entry<String, TypefaceAxis> entry : axisesSet) {
                dest.writeString(entry.getKey());
                dest.writeParcelable(entry.getValue(), flags);
            }
        }
    }

    private TypefaceItem(Parcel in) {
        mName = in.readString();
        mWeight = in.readInt();
        mStyle = in.readInt();
        mIndex = in.readInt();
        final int size = in.readInt();
        if (size > 0) {
            mAxises = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                final String key = in.readString();
                final TypefaceAxis value = in.readParcelable(TypefaceAxis.class.getClassLoader());
                mAxises.put(key, value);
            }
        } else {
            mAxises = null;
        }
    }

    public static final Creator<TypefaceItem> CREATOR =
            new Creator<TypefaceItem>() {
                @Override
                public TypefaceItem createFromParcel(Parcel source) {
                    return new TypefaceItem(source);
                }

                @Override
                public TypefaceItem[] newArray(int size) {
                    return new TypefaceItem[size];
                }
            };
}