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
 * 字体
 */
@SuppressWarnings("unused")
public class Font implements Parcelable {
    public static final Creator<Font> CREATOR = new Creator<Font>() {
        @Override
        public Font createFromParcel(Parcel source) {
            return new Font(source);
        }

        @Override
        public Font[] newArray(int size) {
            return new Font[size];
        }
    };
    private String weight;
    private String style;
    private String ttf;

    Font(String ttf) {
        this.ttf = ttf;
    }

    Font(String weight, String style, String ttf) {
        this.weight = weight;
        this.style = style;
        this.ttf = ttf;
    }

    private Font(Parcel in) {
        this.weight = in.readString();
        this.style = in.readString();
        this.ttf = in.readString();
    }

    public String getWeight() {
        return weight;
    }

    public String getStyle() {
        return style;
    }

    public String getTtf() {
        return ttf;
    }

    @Override
    public String toString() {
        return "Font{" +
                "weight='" + weight + '\'' +
                ", style='" + style + '\'' +
                ", ttf='" + ttf + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.weight);
        dest.writeString(this.style);
        dest.writeString(this.ttf);
    }
}
