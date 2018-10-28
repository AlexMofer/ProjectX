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

/**
 * 字体对称轴
 * Created by Alex on 2018/8/30.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class TypefaceAxis implements Parcelable {
    public static final String TAG_ITAL = "ital";// Italic
    public static final String TAG_OPSZ = "opsz";// Optical size
    public static final String TAG_SLNT = "slnt";// Slant
    public static final String TAG_WDTH = "wdth";// Width
    public static final String TAG_WGHT = "wght";// Weight
    private final String mTag;// 标签
    private final float mStyleValue;// 值

    public TypefaceAxis(String tag, float value) {
        mTag = tag;
        mStyleValue = value;
    }

    /**
     * 获取标签
     *
     * @return 标签
     */
    public String getTag() {
        return mTag;
    }

    /**
     * 获取风格值
     *
     * @return 风格值
     */
    public float getStyleValue() {
        return mStyleValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypefaceAxis that = (TypefaceAxis) o;
        return Float.compare(that.mStyleValue, mStyleValue) == 0 &&
                Objects.equals(mTag, that.mTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mTag, mStyleValue);
    }

    @Override
    public String toString() {
        return "TypefaceAxis{" +
                "tag='" + mTag + '\'' +
                ", styleValue=" + mStyleValue +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTag);
        dest.writeFloat(mStyleValue);
    }

    private TypefaceAxis(Parcel in) {
        mTag = in.readString();
        mStyleValue = in.readFloat();
    }

    public static final Creator<TypefaceAxis> CREATOR = new Creator<TypefaceAxis>() {
        @Override
        public TypefaceAxis createFromParcel(Parcel source) {
            return new TypefaceAxis(source);
        }

        @Override
        public TypefaceAxis[] newArray(int size) {
            return new TypefaceAxis[size];
        }
    };
}
