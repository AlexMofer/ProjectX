/*
 * Copyright (C) 2015 AlexMofer
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

package am.drawable;

import android.graphics.drawable.Drawable;

/**
 * Drawable 动画回调
 * Created by Alex on 2016/9/9.
 */
public interface AnimationCallback {

    /**
     * 开始动画
     *
     * @param drawable Drawable
     */
    void onAnimationStart(Drawable drawable);

    /**
     * 结束动画
     *
     * @param drawable Drawable
     */
    void onAnimationEnd(Drawable drawable);
}
