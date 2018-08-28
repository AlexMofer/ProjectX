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

/**
 * 字体别名
 */
public class FontAlias implements Parcelable {
    public static final Creator<FontAlias> CREATOR = new Creator<FontAlias>() {
        @Override
        public FontAlias createFromParcel(Parcel source) {
            return new FontAlias(source);
        }

        @Override
        public FontAlias[] newArray(int size) {
            return new FontAlias[size];
        }
    };
    private String name;
    private String to;
    private String weight;

    FontAlias(String name, String to, String weight) {
        this.name = name;
        this.to = to;
        this.weight = weight;
    }

    private FontAlias(Parcel in) {
        this.name = in.readString();
        this.to = in.readString();
        this.weight = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getTo() {
        return to;
    }

    public String getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Alias{" +
                "name='" + name + '\'' +
                ", to='" + to + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.to);
        dest.writeString(this.weight);
    }
}
