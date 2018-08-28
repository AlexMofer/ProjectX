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
 * 字体族集
 */
public class FontFamilySet implements Parcelable {
    public static final Creator<FontFamilySet> CREATOR = new Creator<FontFamilySet>() {
        @Override
        public FontFamilySet createFromParcel(Parcel source) {
            return new FontFamilySet(source);
        }

        @Override
        public FontFamilySet[] newArray(int size) {
            return new FontFamilySet[size];
        }
    };
    private final ArrayList<FontFamily> families = new ArrayList<>();
    private final ArrayList<FontAlias> aliases = new ArrayList<>();
    private String version;

    FontFamilySet(String version) {
        this.version = version;
    }

    private FontFamilySet(Parcel in) {
        this.version = in.readString();
        this.families.addAll(in.createTypedArrayList(FontFamily.CREATOR));
        this.aliases.addAll(in.createTypedArrayList(FontAlias.CREATOR));
    }

    public String getVersion() {
        return version;
    }

    public ArrayList<FontFamily> getFamilies() {
        return families;
    }

    public ArrayList<FontAlias> getAliases() {
        return aliases;
    }

    @Override
    public String toString() {
        return "FamilySet{" +
                "version='" + version + '\'' +
                ", families=" + families +
                ", aliases=" + aliases +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
        dest.writeTypedList(this.families);
        dest.writeTypedList(this.aliases);
    }
}
