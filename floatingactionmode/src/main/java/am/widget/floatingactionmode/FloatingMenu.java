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

/**
 * 菜单
 * Created by Alex on 2018/11/21.
 */
@SuppressWarnings("unused")
public interface FloatingMenu {

    /**
     * 作为默认ID与命令
     */
    int NONE = 0;

    /**
     * 添加子项
     *
     * @param title 标题
     * @return 子项
     */
    FloatingMenuItem add(CharSequence title);

    /**
     * 添加子项
     *
     * @param title 标题资源
     * @return 子项
     */
    FloatingMenuItem add(int title);

    /**
     * 添加子项
     *
     * @param id    ID
     * @param order 命令
     * @param title 标题
     * @return 子项
     */
    FloatingMenuItem add(int id, int order, CharSequence title);

    /**
     * 添加子项
     *
     * @param id    ID
     * @param order 命令
     * @param title 标题资源
     * @return 子项
     */
    FloatingMenuItem add(int id, int order, int title);

    /**
     * 移除子项
     *
     * @param id ID
     */
    void removeItem(int id);

    /**
     * 清空所有子项
     */
    void clear();

    /**
     * 通过ID查找子项
     *
     * @param id ID
     * @return 子项，默认查询第一个匹配的子项，查询不到时返回null
     */
    FloatingMenuItem findItem(int id);

    /**
     * 获取子项总数
     *
     * @return 子项总数
     */
    int size();

    /**
     * 通过下标获取子项
     *
     * @param index 下标
     * @return 子项
     */
    FloatingMenuItem getItem(int index);
}
