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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 字体族
 */
public class FontFamily implements Parcelable {
    public static final Creator<FontFamily> CREATOR = new Creator<FontFamily>() {
        @Override
        public FontFamily createFromParcel(Parcel source) {
            return new FontFamily(source);
        }

        @Override
        public FontFamily[] newArray(int size) {
            return new FontFamily[size];
        }
    };
    private final ArrayList<Font> fonts = new ArrayList<>();
    private String name;
    private String lang;
    private String variant;

    FontFamily(String name) {
        this.name = name;
    }

    FontFamily(String name, String lang, String variant) {
        this.name = name;
        this.lang = lang;
        this.variant = variant;
    }

    private FontFamily(Parcel in) {
        this.name = in.readString();
        this.lang = in.readString();
        this.variant = in.readString();
        this.fonts.addAll(in.createTypedArrayList(Font.CREATOR));
    }

    public String getName() {
        return name;
    }

    public String getLang() {
        return lang;
    }

    public String getVariant() {
        return variant;
    }

    public ArrayList<Font> getFonts() {
        return fonts;
    }

    @Override
    public String toString() {
        return "Family{" +
                "name='" + name + '\'' +
                ", lang='" + lang + '\'' +
                ", variant='" + variant + '\'' +
                ", fonts=" + fonts +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.lang);
        dest.writeString(this.variant);
        dest.writeTypedList(this.fonts);
    }
}
