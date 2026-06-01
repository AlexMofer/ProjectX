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
package io.github.alexmofer.android.support.widget.builders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

/**
 * 线性布局构建器
 * Created by Alex on 2025/7/17.
 */
public final class LinearLayoutBuilder extends ViewGroupBuilder {
    private final LinearLayout mView;

    public LinearLayoutBuilder(@NonNull LinearLayout view) {
        super(view);
        this.mView = view;
    }

    public LinearLayoutBuilder(@NonNull Context context) {
        this(new LinearLayout(context));
    }

    @NonNull
    @Override
    public LinearLayout build() {
        return mView;
    }

    public LinearLayoutBuilder setOrientation(int orientation) {
        mView.setOrientation(orientation);
        return this;
    }

    public LinearLayoutBuilder setVertical() {
        return setOrientation(LinearLayout.VERTICAL);
    }

    public LinearLayoutBuilder setGravity(int gravity) {
        mView.setGravity(gravity);
        return this;
    }

    /**
     * 设置分割线
     * 注意：水平分割线低版本不支持 RTL，会出现排版错乱问题
     *
     * @param divider 分割线
     */
    public LinearLayoutBuilder setDividerDrawable(Drawable divider) {
        mView.setDividerDrawable(divider);
        return this;
    }

    public LinearLayoutBuilder setShowDividers(int showDividers) {
        mView.setShowDividers(showDividers);
        return this;
    }

    public LinearLayoutBuilder setShowDividersAll() {
        return setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING |
                LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
    }

    public LinearLayoutBuilder setShowDividersMiddle() {
        return setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    }
}
