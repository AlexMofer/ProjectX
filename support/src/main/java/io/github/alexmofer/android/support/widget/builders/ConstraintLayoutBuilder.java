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
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;

/**
 * ConstraintLayout
 * Created by Alex on 2026/3/9.
 */
public final class ConstraintLayoutBuilder extends ViewGroupBuilder {
    private final ConstraintLayout mView;

    public ConstraintLayoutBuilder(@NonNull ConstraintLayout view) {
        super(view);
        this.mView = view;
    }

    public ConstraintLayoutBuilder(@NonNull Context context) {
        this(new ConstraintLayout(context));
    }

    @NonNull
    @Override
    public ConstraintLayout build() {
        return mView;
    }

    public ConstraintLayoutBuilder addViewWithVerticalBias(@NonNull View child,
                                                           float verticalBias) {
        ViewGroup.LayoutParams set = child.getLayoutParams();
        if (set == null) {
            set = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        final ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(set);
        lp.startToStart = Constraints.LayoutParams.PARENT_ID;
        lp.endToEnd = Constraints.LayoutParams.PARENT_ID;
        lp.topToTop = Constraints.LayoutParams.PARENT_ID;
        lp.bottomToBottom = Constraints.LayoutParams.PARENT_ID;
        lp.verticalBias = verticalBias;// 上间隙/（上间隙+下间隙）
        return (ConstraintLayoutBuilder) addView(child, lp);
    }
}
