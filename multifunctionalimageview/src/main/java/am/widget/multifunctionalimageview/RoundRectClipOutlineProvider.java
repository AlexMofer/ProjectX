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
 * 圆角矩形
 * Created by Alex on 2018/12/25.
 */
@SuppressWarnings("WeakerAccess")
public final class RoundRectClipOutlineProvider extends ClipOutlineProvider {

    private float mRadius;

    public RoundRectClipOutlineProvider(float radius) {
        mRadius = radius;
    }

    @Override
    public void getOutline(View view, Path outline) {
        final int width = view.getWidth();
        final int height = view.getHeight();
        Compat.addRoundRect(outline, 0, 0, width, height, mRadius, mRadius,
                Path.Direction.CW);
    }

    /**
     * 设置圆角半径
     *
     * @param radius 圆角半径
     */
    public void setRadius(float radius) {
        mRadius = radius;
    }
}
