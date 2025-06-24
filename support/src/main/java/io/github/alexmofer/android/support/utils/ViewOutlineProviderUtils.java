/*
 * Copyright (C) 2024 AlexMofer
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
package io.github.alexmofer.android.support.utils;

import android.graphics.Outline;
import android.graphics.Path;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;

/**
 * ViewOutlineProvider 工具
 * Created by Alex on 2024/3/18.
 */
public class ViewOutlineProviderUtils {

    public static final ViewOutlineProvider OVAL = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };
    public static final ViewOutlineProvider PADDED_OVAL = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getWidth() - view.getPaddingRight(),
                    view.getHeight() - view.getPaddingBottom());
        }
    };
    public static final ViewOutlineProvider CAPSULE = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            final int width = view.getWidth();
            final int height = view.getHeight();
            outline.setRoundRect(0, 0, width, height, height * 0.5f);
        }
    };

    private ViewOutlineProviderUtils() {
        //no instance
    }

    /**
     * 设置路径，Android R 以前必须为凸路径
     *
     * @param outline Outline
     * @param path    路径
     */
    public static void setPath(Outline outline, @NonNull Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path);
        } else {
            outline.setConvexPath(path);
        }
    }
}
