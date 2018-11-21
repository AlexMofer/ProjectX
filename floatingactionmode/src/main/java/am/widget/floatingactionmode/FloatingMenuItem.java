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

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 菜单子项
 * Created by Alex on 2018/11/21.
 */
@SuppressWarnings("all")
public interface FloatingMenuItem {

    /**
     * 自动判断显示方案，有文字时显示文字，有图标时显示图标，都有时则都显示，都不存在时显示文字
     */
    int SHOW_TYPE_AUTO = -1;
    /**
     * 仅显示文字
     */
    int SHOW_TYPE_TEXT = 0;
    /**
     * 仅显示图标
     */
    int SHOW_TYPE_ICON = 1;
    /**
     * 全部显示
     */
    int SHOW_TYPE_ALL = 2;

    /**
     * 获取ID
     *
     * @return ID
     */
    int getId();

    /**
     * 获取命令
     *
     * @return 命令
     */
    int getOrder();

    /**
     * 设置标题
     *
     * @param title 标题
     * @return 自身
     */
    FloatingMenuItem setTitle(CharSequence title);

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
    FloatingMenuItem setTitle(int title);

    /**
     * 设置图标
     *
     * @param icon 图标
     * @return 自身
     */
    FloatingMenuItem setIcon(Drawable icon);

    /**
     * 获取图标
     *
     * @return 图标
     */
    Drawable getIcon();

    /**
     * 设置图标资源
     *
     * @param icon 图标资源
     * @return 自身
     */
    FloatingMenuItem setIcon(int icon);

    /**
     * 获取显示类型
     *
     * @return 显示类型
     */
    int getShowType();

    /**
     * 设置显示类型
     *
     * @param type 显示类型
     * @return 自身
     */
    FloatingMenuItem setShowType(int type);

    /**
     * 设置内容描述
     *
     * @param contentDescription 内容描述
     * @return 自身
     */
    FloatingMenuItem setContentDescription(CharSequence contentDescription);

    /**
     * 获取内容描述
     *
     * @return 内容描述
     */
    CharSequence getContentDescription();

    /**
     * 设置内容描述资源
     *
     * @param contentDescription 内容描述资源
     * @return 自身
     */
    FloatingMenuItem setContentDescription(int contentDescription);

    /**
     * 获取附件
     *
     * @return 附件
     */
    Object getTag();

    /**
     * 设置附件
     *
     * @param tag 附件
     */
    void setTag(Object tag);

    /**
     * 判断是否包含次级菜单
     *
     * @return 是否包含次级菜单
     */
    boolean hasSubMenu();

    /**
     * 设置常规次级菜单
     *
     * @return 常规次级菜单
     */
    FloatingSubMenu setSubMenu();

    /**
     * 设置自定义次级菜单
     *
     * @param custom 视图
     * @return 自定义次级菜单
     */
    FloatingSubMenu setSubMenu(View custom);

    /**
     * 获取次级菜单
     *
     * @return 次级菜单
     */
    FloatingSubMenu getSubMenu();
}
