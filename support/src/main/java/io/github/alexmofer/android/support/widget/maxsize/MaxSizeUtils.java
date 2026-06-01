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
package io.github.alexmofer.android.support.widget.maxsize;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.github.alexmofer.android.support.widget.builders.LinearLayoutBuilder;
import io.github.alexmofer.android.support.widget.builders.ViewGroupBuilder;

/**
 * 最大尺寸辅助
 * Created by Alex on 2026/1/23.
 */
public final class MaxSizeUtils {

    private MaxSizeUtils() {
        //no instance
    }

    public static int handleMeasureSpec(int measureSpec, int max) {
        if (max <= 0) {
            return measureSpec;
        }
        final int mode = View.MeasureSpec.getMode(measureSpec);
        if (mode == View.MeasureSpec.UNSPECIFIED) {
            // 不受限制
            return View.MeasureSpec.makeMeasureSpec(max, View.MeasureSpec.AT_MOST);
        }
        return View.MeasureSpec.makeMeasureSpec(
                Math.min(max, View.MeasureSpec.getSize(measureSpec)), mode);
    }

    @NonNull
    public static ViewGroupBuilder newMaxSizeFrameLayout(@NonNull Context context, int maxWidth, int maxHeight) {
        final FrameLayout view = new FrameLayout(context);
        view.setMaximumWidth(maxWidth);
        view.setMaximumHeight(maxHeight);
        return new ViewGroupBuilder(view);
    }

    @NonNull
    public static ViewGroupBuilder newMaxSizeFrameLayout(@NonNull Context context, int max) {
        return newMaxSizeFrameLayout(context, max, max);
    }

    @NonNull
    public static ViewGroupBuilder newMaxWidthFrameLayout(@NonNull Context context, int max) {
        return newMaxSizeFrameLayout(context, max, 0);
    }

    @NonNull
    public static ViewGroupBuilder newMaxHeightFrameLayout(@NonNull Context context, int max) {
        return newMaxSizeFrameLayout(context, 0, max);
    }

    public static LinearLayoutBuilder newMaxSize(Context context, int maxWidth, int maxHeight) {
        final LinearLayout view = new LinearLayout(context);
        view.setMaximumWidth(maxWidth);
        view.setMaximumHeight(maxHeight);
        return new LinearLayoutBuilder(view);
    }

    public static LinearLayoutBuilder newMaxSize(Context context, int max) {
        return newMaxSize(context, max, max);
    }

    public static LinearLayoutBuilder newMaxWidth(Context context, int max) {
        return newMaxSize(context, max, 0);
    }

    public static LinearLayoutBuilder newMaxHeight(Context context, int max) {
        return newMaxSize(context, 0, max);
    }
}
