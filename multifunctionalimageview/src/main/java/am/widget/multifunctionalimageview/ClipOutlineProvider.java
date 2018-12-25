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

package am.widget.multifunctionalimageview;

import android.graphics.Path;
import android.view.View;

/**
 * 裁剪轮廓提供器
 * Created by Alex on 2018/12/25.
 */
@SuppressWarnings("WeakerAccess")
public abstract class ClipOutlineProvider {

    /**
     * 圆形
     */
    public static final ClipOutlineProvider CIRCLE = new ClipOutlineProvider() {
        @Override
        public void getOutline(View view, Path outline) {
            final int width = view.getWidth();
            final int height = view.getHeight();
            final float radius = Math.min(width, height) * 0.5f;
            outline.addCircle(width * 0.5f, height * 0.5f, radius, Path.Direction.CW);
        }
    };

    /**
     * 椭圆
     */
    public static final ClipOutlineProvider OVAL = new ClipOutlineProvider() {
        @Override
        public void getOutline(View view, Path outline) {
            final int width = view.getWidth();
            final int height = view.getHeight();
            Compat.addOval(outline, 0, 0, width, height, Path.Direction.CW);
        }
    };

    /**
     * 圆角矩形（短边为半圆）
     */
    public static final ClipOutlineProvider FULL_ROUND_RECT = new ClipOutlineProvider() {
        @Override
        public void getOutline(View view, Path outline) {
            final int width = view.getWidth();
            final int height = view.getHeight();
            final float radius = Math.min(width, height) * 0.5f;
            Compat.addRoundRect(outline, 0, 0, width, height, radius, radius,
                    Path.Direction.CW);
        }
    };

    /**
     * 获取裁剪轮廓
     *
     * @param view    视图
     * @param outline 路径
     */
    public abstract void getOutline(View view, Path outline);
}
