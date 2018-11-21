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
package am.widget.floatingactionmode;

import android.view.View;

/**
 * 次级菜单
 * Created by Alex on 2018/11/21.
 */
public interface FloatingSubMenu extends FloatingMenu {

    /**
     * 设置标题
     *
     * @param title 标题
     * @return 自身
     */
    FloatingSubMenu setTitle(CharSequence title);

    /**
     * 获取标题
     *
     * @return 标题
     */
    CharSequence getTitle();

    /**
     * 设置标题
     *
     * @param title 标题
     * @return 自身
     */
    FloatingSubMenu setTitle(int title);

    /**
     * 判断是否为自定义次级菜单
     *
     * @return 是否为自定义次级菜单
     */
    boolean isCustom();

    /**
     * 获取自定义视图
     *
     * @return 自定义视图
     */
    View getCustomView();

    /**
     * 设置自定义视图
     *
     * @param view 自定义视图
     * @return 自身
     */
    FloatingSubMenu setCustomView(View view);

    /**
     * 绑定状态改变监听
     */
    interface OnAttachStateChangeListener {

        /**
         * 自定义视图与悬浮菜单捆绑
         *
         * @param mode 悬浮菜单
         */
        void onViewAttachedToFloatingActionMode(FloatingActionMode mode);

        /**
         * 自定义视图与悬浮菜单脱离
         *
         * @param mode 悬浮菜单
         */
        void onViewDetachedFromFloatingActionMode(FloatingActionMode mode);
    }
}
