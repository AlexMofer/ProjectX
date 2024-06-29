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
package io.github.alexmofer.android.support.widget;

import android.graphics.Outline;
import android.graphics.Path;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

import io.github.alexmofer.android.support.utils.ViewOutlineProviderUtils;

/**
 * 圆角矩形裁剪
 * Created by Alex on 2024/3/15.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RoundRectOutlineProvider extends ViewOutlineProvider {

    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomRightRadius;
    private float mBottomLeftRadius;
    private Path mPath;
    private float mAlpha = 1.0f;

    public RoundRectOutlineProvider(float radius) {
        this(radius, radius, radius, radius);
    }

    public RoundRectOutlineProvider(float topLeft, float topRight,
                                    float bottomLeft, float bottomRight) {
        mTopLeftRadius = topLeft;
        mTopRightRadius = topRight;
        mBottomRightRadius = bottomLeft;
        mBottomLeftRadius = bottomRight;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setAlpha(mAlpha);
        if (mTopLeftRadius == mTopRightRadius && mTopLeftRadius == mBottomLeftRadius
                && mTopLeftRadius == mBottomRightRadius) {
            if (mTopLeftRadius == 0) {
                outline.setRect(0, 0, view.getWidth(), view.getHeight());
            } else {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(),
                        mTopLeftRadius);
            }
            return;
        }
        if (mPath == null) {
            mPath = new Path();
        }
        mPath.reset();
        mPath.addRoundRect(0, 0, view.getWidth(), view.getHeight(),
                new float[]{mTopLeftRadius, mTopLeftRadius,
                        mTopRightRadius, mTopRightRadius, mBottomRightRadius, mBottomRightRadius,
                        mBottomLeftRadius, mBottomLeftRadius}, Path.Direction.CW);
        ViewOutlineProviderUtils.setPath(outline, mPath);
    }

    /**
     * 设置圆角半径
     *
     * @param radius 半径
     */
    public void setRadius(float radius) {
        mTopLeftRadius = radius;
        mTopRightRadius = radius;
        mBottomRightRadius = radius;
        mBottomLeftRadius = radius;
    }

    /**
     * 设置圆角半径
     *
     * @param topLeft     左上角
     * @param topRight    右上角
     * @param bottomLeft  左下角
     * @param bottomRight 右下角
     */
    public void setRadius(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        mTopLeftRadius = topLeft;
        mTopRightRadius = topRight;
        mBottomRightRadius = bottomLeft;
        mBottomLeftRadius = bottomRight;
    }

    /**
     * 获取左上角圆角半径
     *
     * @return 左上角圆角半径
     */
    public float getTopLeftRadius() {
        return mTopLeftRadius;
    }

    /**
     * 获取右上角圆角半径
     *
     * @return 右上角圆角半径
     */
    public float getTopRightRadius() {
        return mTopRightRadius;
    }

    /**
     * 获取左下角圆角半径
     *
     * @return 左下角圆角半径
     */
    public float getBottomLeftRadius() {
        return mBottomLeftRadius;
    }

    /**
     * 获取右下角圆角半径
     *
     * @return 右下角圆角半径
     */
    public float getBottomRightRadius() {
        return mBottomRightRadius;
    }

    /**
     * 获取透明度
     *
     * @return 透明度
     */
    public float getAlpha() {
        return mAlpha;
    }

    /**
     * 设置透明度
     *
     * @param alpha 透明度
     */
    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }
}
