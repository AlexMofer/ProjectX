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

import android.content.Context;
import android.view.View;

/**
 * 次级普通菜单子项
 * Created by Alex on 2018/10/24.
 */
final class FloatingSubMenuItemImpl extends FloatingMenuItemImpl {

    FloatingSubMenuItemImpl(Context context) {
        super(context);
    }

    FloatingSubMenuItemImpl(Context context, int id, int order) {
        super(context, id, order);
    }

    @Override
    public boolean hasSubMenu() {
        return false;
    }

    @Override
    public FloatingSubMenu getSubMenu() {
        return null;
    }

    @Override
    public FloatingSubMenu setSubMenu() {
        throw new UnsupportedOperationException("Sub menu item not support set sub menu.");
    }

    @Override
    public FloatingSubMenu setSubMenu(View custom) {
        throw new UnsupportedOperationException("Sub menu item not support set sub menu.");
    }
}
