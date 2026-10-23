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
package io.github.alexmofer.android.support.utils;

import android.graphics.Paint;
import android.text.TextPaint;

import androidx.annotation.NonNull;

/**
 * TextPaint 工具
 * Created by Alex on 2026/10/14.
 */
public final class TextPaintUtils {

    private TextPaintUtils() {
        //no instance
    }

    /**
     * 获取文本的基线
     *
     * @param paint   TextPaint
     * @param centerY 中心Y
     * @return 基线
     */
    public static float getBaseline(@NonNull TextPaint paint, float centerY) {
        final float ascent = paint.ascent();
        return centerY - (paint.descent() - ascent) * 0.5f - ascent;
    }

    /**
     * 获取字体高度
     *
     * @param paint 字体画笔
     * @return 字体高度
     */
    public static float getFontHeight(@NonNull TextPaint paint) {
        final Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.descent - metrics.ascent;
    }

    /**
     * 获取字体高度（包含过多的冗余留白）
     *
     * @param paint 字体画笔
     * @return 字体高度（包含过多的冗余留白）
     */
    public static float getFontHeightWithPadding(@NonNull TextPaint paint) {
        final Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.bottom - metrics.top;
    }

    /**
     * 获取行高
     *
     * @param paint 字体画笔
     * @return 行高
     */
    public static float getLineHeight(@NonNull TextPaint paint) {
        final Paint.FontMetrics metrics = paint.getFontMetrics();
        return metrics.descent - metrics.ascent + metrics.leading;
    }
}
