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
package am.widget.tabstrip;

import android.graphics.Color;

/**
 * 颜色工具
 */
public class ColorUtils {

    private ColorUtils() {
        //no instance
    }

    /**
     * 双色合成
     *
     * @param normal   普通颜色
     * @param selected 选中颜色
     * @param offset   偏移值
     * @return 合成色
     */
    public static int makeColor(int normal, int selected, float offset) {
        if (offset <= 0)
            return normal;
        if (offset >= 1)
            return selected;
        int normalAlpha = Color.alpha(normal);
        int normalRed = Color.red(normal);
        int normalGreen = Color.green(normal);
        int normalBlue = Color.blue(normal);
        int selectedAlpha = Color.alpha(selected);
        int selectedRed = Color.red(selected);
        int selectedGreen = Color.green(selected);
        int selectedBlue = Color.blue(selected);
        int a = (int) Math.ceil((selectedAlpha - normalAlpha) * offset);
        int r = (int) Math.ceil((selectedRed - normalRed) * offset);
        int g = (int) Math.ceil((selectedGreen - normalGreen) * offset);
        int b = (int) Math.ceil((selectedBlue - normalBlue) * offset);
        return Color.argb(normalAlpha + a, normalRed + r, normalGreen + g,
                normalBlue + b);
    }
}
