/*
 * Copyright (C) 2026 AlexMofer
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
package io.github.alexmofer.android.support.language;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;
import java.util.Objects;

/**
 * 语言
 * Created by Alex on 2026/5/28.
 */
public final class Language implements Parcelable {
    public static final Creator<Language> CREATOR = new Creator<Language>() {
        @Override
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        @Override
        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
    @NonNull
    public final String lang;
    private final Locale mLocale;

    public Language(@NonNull String lang) {
        this.lang = lang;
        mLocale = Locale.forLanguageTag(lang);
    }

    private Language(@NonNull Parcel in) {
        this(Objects.requireNonNull(in.readString()));
    }

    @NonNull
    private static String toUpperCaseFirstLetter(@NonNull String str) {
        if (str.isEmpty()) {
            return str;
        }
        final int firstCodePoint = str.codePointAt(0);
        final String upperFirst = new String(Character.toChars(
                Character.toUpperCase(firstCodePoint)));
        return upperFirst + str.substring(Character.charCount(firstCodePoint));
    }

    @NonNull
    public String getLanguage(boolean upperCaseFirstLetter) {
        final String language = mLocale.getDisplayLanguage(mLocale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(language) : language;
    }

    @NonNull
    public String getLanguage() {
        return getLanguage(true);
    }

    @NonNull
    public String getDisplayLanguage(boolean upperCaseFirstLetter) {
        Locale locale = AppCompatDelegate.getApplicationLocales().get(0);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final String language = mLocale.getDisplayLanguage(locale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(language) : language;
    }

    @NonNull
    public String getDisplayLanguage() {
        return getDisplayLanguage(true);
    }

    @NonNull
    public String getScript(boolean upperCaseFirstLetter) {
        final String script = mLocale.getDisplayScript(mLocale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(script) : script;
    }

    @NonNull
    public String getScript() {
        return getScript(true);
    }

    @NonNull
    public String getDisplayScript(boolean upperCaseFirstLetter) {
        Locale locale = AppCompatDelegate.getApplicationLocales().get(0);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final String script = mLocale.getDisplayScript(locale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(script) : script;
    }

    @NonNull
    public String getDisplayScript() {
        return getDisplayScript(true);
    }

    @NonNull
    public String getCountry(boolean upperCaseFirstLetter) {
        final String country = mLocale.getDisplayCountry(mLocale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(country) : country;
    }

    @NonNull
    public String getCountry() {
        return getCountry(true);
    }

    @NonNull
    public String getVariant(boolean upperCaseFirstLetter) {
        final String variant = mLocale.getDisplayVariant(mLocale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(variant) : variant;
    }

    @NonNull
    public String getVariant() {
        return getVariant(true);
    }

    @NonNull
    public String getName(boolean upperCaseFirstLetter) {
        final String name = mLocale.getDisplayName(mLocale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(name) : name;
    }

    @NonNull
    public String getName() {
        return getName(true);
    }

    @NonNull
    public String getDisplayName(boolean upperCaseFirstLetter) {
        Locale locale = AppCompatDelegate.getApplicationLocales().get(0);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final String name = mLocale.getDisplayName(locale);
        return upperCaseFirstLetter ? toUpperCaseFirstLetter(name) : name;
    }

    @NonNull
    public String getDisplayName() {
        return getDisplayName(true);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(lang, language.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lang);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(lang);
    }
}
