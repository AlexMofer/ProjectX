/*
 * Copyright (C) 2025 AlexMofer
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
package io.github.alexmofer.android.support.other;

import android.text.SpannableString;

import androidx.annotation.NonNull;

/**
 * SpannableString 构建器
 * Created by Alex on 2025/7/20.
 */
public final class SpannableStringBuilder {

    private final SpannableString mText;

    public SpannableStringBuilder(CharSequence source) {
        mText = new SpannableString(source);
    }

    /**
     * 构建
     *
     * @return SpannableString
     */
    @NonNull
    public SpannableString build() {
        return mText;
    }

    public SpannableStringBuilder setSpan(Object what, int start, int end, int flags) {
        mText.setSpan(what, start, end, flags);
        return this;
    }

    public SpannableStringBuilder removeSpan(Object what) {
        mText.removeSpan(what);
        return this;
    }
}
